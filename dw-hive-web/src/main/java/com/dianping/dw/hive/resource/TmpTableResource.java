package com.dianping.dw.hive.resource;

import com.dianping.dw.hive.exception.LionException;
import com.dianping.dw.hive.model.CreateTableInfoDO;
import com.dianping.dw.hive.model.HqlInfoDO;
import com.dianping.dw.hive.model.LoadTableInfoDO;
import com.dianping.dw.hive.model.ResultDO;
import com.dianping.dw.hive.security.HWSecurityContext;
import com.dianping.dw.hive.security.User;
import com.dianping.dw.hive.service.TmpTableService;
import com.dianping.dw.hive.util.CommonUtil;
import com.dianping.dw.hive.util.HDFSUtil;
import com.dianping.dw.hive.util.HiveUtil;
import com.dianping.dw.hive.util.LionUtil;
import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 临时表创建与上传服务
 *
 * Author: tao.meng
 */
@Controller
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Path("/tmpTable")
public class TmpTableResource {
    public static final Logger LOG = LoggerFactory.getLogger(TmpTableResource.class);

    @Autowired
    TmpTableService tmpTableService;

    @Context
    private ContainerRequestContext requestContext;

    private String uploadFileLocation;
    private String hdfsFileLocation;
    private String hdfsTmpFilePermission;

    @PostConstruct
    public void init() throws LionException {
        uploadFileLocation = LionUtil.getProperty("dw-hive-web.uploadFileLocation");
        hdfsFileLocation = LionUtil.getProperty("dw-hive-web.hdfsTmpTableFileLocation");
        hdfsTmpFilePermission = LionUtil.getProperty("dw-hive-web.hdfsTmpFilePermission");
        
        // 创建/app/appdatas/hive-web工作目录
        File file = new File(uploadFileLocation);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    @POST
    @Path("/createSql")
    @Consumes(MediaType.APPLICATION_JSON)
    public ResultDO<String> getCreateTableSql(CreateTableInfoDO tableInfo) {
        ResultDO<String> result = new ResultDO<String>();
        String hql = HiveUtil.buildCreateTableSQL(tableInfo);
        if (StringUtils.isNotBlank(hql)) {
            result.setSuccess(true);
            result.setResult(hql);
        } else {
            result.setSuccess(false);
            result.setMessages("backend 创建 建表语句失败...");
        }
        return result;
    }

    @POST
    @Path("/createTable")
    @Consumes(MediaType.APPLICATION_JSON)
    public ResultDO<String> createTable(HqlInfoDO hql) {
        ResultDO<String> result = new ResultDO<String>();
        result.setSuccess(true);
        Statement statement = null;

        // 1. 获取当前用户信息
        HWSecurityContext context = (HWSecurityContext) requestContext.getSecurityContext();

        // 2. 获取hive jdbc 链接
        try {
            statement = HiveUtil.getConnectStatement(context, hql.getRoleName());
            if (null == statement) {
                result.setSuccess(false);
                result.setMessages("获取hive server2 jdbc 链接失败.");
            }
        } catch (ClassNotFoundException e) {
            result.setSuccess(false);
            result.setMessages("获取hive server2 连接失败:" + e.getMessage());
        } catch (SQLException e) {
            result.setSuccess(false);
            result.setMessages("获取hive server2 连接失败: " + e.getMessage());
        } catch (LoginException e) {
            result.setSuccess(false);
            result.setMessages("kerberos 认证失败: " + e.getMessage());
        }
        if (!result.isSuccess()) {
            return result;
        }

        // 3. 执行 临时表 创建
        try {
            tmpTableService.createTmpTable(statement, "use "+ hql.getDatabase());
            tmpTableService.createTmpTable(statement, hql.getHql());
            result.setSuccess(true);
            result.setMessages("临时表创建成功...");
        } catch (SQLException e) {
            result.setSuccess(false);
            result.setMessages("创建临时表 发生异常:" + e.getMessage());
        }
        
        // 4. 关闭statement
        try {
            statement.close();
        } catch (SQLException e) {
            LOG.error("操作完毕，close 链接失败： " + e.getMessage());
        }
        return result;
    }

    @POST
    @Path("/loadData")
    @Consumes(MediaType.APPLICATION_JSON)
    public ResultDO<String> loadDataIntoTable(LoadTableInfoDO loadTableInfo) {
        ResultDO<String> result = new ResultDO<String>();
        result.setSuccess(true);
        Statement statement = null;

        // 1. 获取当前用户信息
        HWSecurityContext context = (HWSecurityContext) requestContext.getSecurityContext();
        User user = (User) context.getUserPrincipal();
        Integer adminId = user.getAdminId();
        String username = user.getEmployeeEnName();

        // 2. 获取hive jdbc 链接
        try {
            statement = HiveUtil.getConnectStatement(context, loadTableInfo.getRoleName());
        } catch (ClassNotFoundException e) {
            result.setSuccess(false);
            result.setMessages("获取hive server2 连接失败:" + e.getMessage());
        } catch (SQLException e) {
            result.setSuccess(false);
            result.setMessages("获取hive server2 连接失败: " + e.getMessage());
        } catch (LoginException e) {
            result.setSuccess(false);
            result.setMessages("kerberos 认证失败: " + e.getMessage());
        }
        if (!result.isSuccess()) {
            return result;
        }

        // 3. 执行上传文件到临时表
        for (String fileName : loadTableInfo.getFileList()) {
            String hdfsStorePath = hdfsFileLocation + File.separator
                    + username + adminId.toString() + "_" + fileName;

            // 进一步 检测是否文件存在
            try {
                if (!HDFSUtil.hdfsFileExists(hdfsStorePath)) {
                    result.setSuccess(false);
                    result.setMessages("HDFS端没有临时表数据文件，请上传后在提交");
                    break;
                }
            } catch (IOException e) {
                result.setSuccess(false);
                result.setMessages("HDFS端检查文件存在时发生异常: " + e.getMessage());
                break;
            }

            String loadDataHql = HiveUtil.buildLoadDataIntoTableSQL(loadTableInfo, hdfsStorePath);
            try {
                // 执行 load data into 语句
                tmpTableService.loadDataIntoTmpTable(statement, loadDataHql);
                result.setSuccess(true);
                result.setMessages("临时表 上传数据成功...");
            } catch (SQLException e) {
                result.setSuccess(false);
                result.setMessages("临时表 上传数据: " + fileName + " 发生异常: " + e.getMessage());
            } finally {
                // 4. 文件加载到临时表成功/失败后， HDFS端 需要删除临时文件
                try {
                    HDFSUtil.hdfsFileDelete(hdfsStorePath);
                    LOG.info("HDFS 删除临时文件 ： " + hdfsStorePath);
                } catch (IOException e) {
                    LOG.error("HDFS删除临时文件 发生异常: " + e.getMessage());
//                    result.setSuccess(false);
//                    result.setMessages("HDFS删除临时文件 发生异常: " + e.getMessage());
                }
                
                // 如果是上传多个文件，第二次以及后面上传的 overwrite 属性需要设置为false
                if (loadTableInfo.isOverWrite()) {
                    loadTableInfo.setOverWrite(false);
                }
                
                // 5. 如果临时表 上传数据失败， 则跳出循环， 后续的文件不再继续上传， 待问题处理后 重新上传
                if (!result.isSuccess()) {
                    break;
                }
            }
        }

        // 6. 关闭statement
        try {
            statement.close();
        } catch (SQLException e) {
            LOG.error("操作完毕，close 链接失败： " + e.getMessage());
        }
        return result;
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ResultDO<String> uploadDataFile(FormDataMultiPart form) {
        ResultDO<String> result = new ResultDO<String>();
        result.setSuccess(true);

        HWSecurityContext context = (HWSecurityContext) requestContext.getSecurityContext();
        User user = (User) context.getUserPrincipal();
        Integer adminId = user.getAdminId();
        String username = user.getEmployeeEnName();

        // step 1:  获取上传文件信息
        FormDataBodyPart filePart = form.getField("file");
        ContentDisposition headerOfFilePart = filePart.getContentDisposition();
        String fileName = headerOfFilePart.getFileName();

        // 避免多用户 在存储路径下 上传了 同名文件问题 —— 新文件名： adminId + username + filename
        String storePath = uploadFileLocation + File.separator
                + username + adminId.toString() + "_" + fileName;
        InputStream fileInputStream = filePart.getValueAs(InputStream.class);
        OutputStream outputStream = null;

        // 进一步 检测是否有同名文件存在
        File newFile = new File(storePath);
        if (newFile.exists()) {
            result.setSuccess(false);
            result.setMessages("服务器端有同名文件，请修改名字，在上传!");
            return result;
        }

        // step 2:  开始读文件流 写入到server端
        try {
            outputStream = new FileOutputStream(newFile);
            int read = 0;
            byte[] readBytes = new byte[1024];
            while ((read = fileInputStream.read(readBytes)) != -1) {
                outputStream.write(readBytes, 0, read);
            }
        } catch (IOException e) {
            LOG.error("上传文件到服务器失败...", e);
            result.setSuccess(false);
            result.setResult("上传文件失败: " + e.getMessage());
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                LOG.error("关闭文件流失败...", e);
                result.setSuccess(false);
                result.setResult("上传文件到服务器失败: " + e.getMessage());
            }
        }
        
        // step 3:  如果 server接受文件出现异常，直接返回失败信息
        if (!result.isSuccess()) {
            return result;
        }
         
        // step 4:  将server端上传上来的文件 copy 到HDFS中， 避免在hive-web server端部署hive
        String hdfsFileName = hdfsFileLocation + File.separator
                + username + adminId.toString() + "_" + fileName;
        try {
            HDFSUtil.copyFileToHdfs(storePath, hdfsFileName);
            // 上传操作是由 hadoop 用户执行，需要修改文件权限为 777
            HDFSUtil.setFilePermission(hdfsFileName, hdfsTmpFilePermission);
        } catch (IOException e) {
            LOG.error("上传文件到HDFS失败: ", e);
            result.setSuccess(false);
            result.setResult("上传文件到HDFS失败: " + e.getMessage());
            
            // 如果hdfs上传失败，删除server端文件
            CommonUtil.deleteTmpFile(storePath);
        }
        return result;
    }
}
