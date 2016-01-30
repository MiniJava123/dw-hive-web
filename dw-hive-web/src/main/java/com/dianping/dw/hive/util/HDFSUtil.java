package com.dianping.dw.hive.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import jersey.repackaged.com.google.common.collect.Lists;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.dw.hive.exception.HdfsException;

/**
 * HDFS工具类
 * 
 * @author yujie.yao
 * @author tao.meng
 */
public class HDFSUtil {

    private static final Logger LOG = LoggerFactory.getLogger(HDFSUtil.class);

    private static Configuration conf = null;
    private static FileSystem fs = null;

    static {
        loginKeytab();
    }

    /**
     * 使用keytab登录
     */
    private static void loginKeytab() {
        conf = new Configuration();
        conf.addResource("hive/hive-site.xml");
        conf.addResource("hive/core-site.xml");
        conf.addResource("hive/hdfs-site.xml");

        LOG.info("fs.default.name:" + conf.get("fs.default.name"));
        LOG.info("hadoop.security.authentication:" + conf.get("hadoop.security.authentication"));
        LOG.info("test.hadoop.principal:" + conf.get("test.hadoop.principal"));
        LOG.info("test.hadoop.keytab.file:" + conf.get("test.hadoop.keytab.file"));

        UserGroupInformation.setConfiguration(conf);
        try {
            SecurityUtil.login(conf, "test.hadoop.keytab.file", "test.hadoop.principal");
            LOG.info("使用keytab登录成功");
            fs = FileSystem.get(conf);
        } catch (IOException ex) {
            LOG.error("使用keytab登录失败", ex);
        }
    }

    /**
     * 打开hdfs文件
     * 
     * @param hdfsPath
     * @return InputStream 文件流
     * @throws IOException
     */
    public static InputStream open(String hdfsPath) throws IOException {
        Path path = new Path(hdfsPath);
        try {
            if (!fs.exists(path)) {
                throw new RuntimeException(path.toString() + "不存在！");
            }
            if (!fs.isFile(path)) {
                throw new RuntimeException(path.toString() + "不是文件！");
            }
            return fs.open(path);
        } catch (IOException ex) {
            LOG.error("打开文件流异常！", ex);
            throw new IOException("打开文件流异常！", ex);
        }
    }

    /**
     * 读取文件内容queryResultMaxLines行
     * 
     * @param path hdfs路径
     * @param queryResultMaxLines 文件内容限制行数
     * @throws HdfsException
     */
    public static String[] readFileLimited(String path, int queryResultMaxLines) throws HdfsException {
        try {
            Path hdfsPath = new Path(path);
            if (!fs.exists(hdfsPath)) {
                return new String[0];
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(hdfsPath)));
            List<String> result = Lists.newArrayList();
            String line;
            for (int i = 0; i < queryResultMaxLines; ++i) {
                line = br.readLine();
                if (null == line) {
                    break;
                }
                result.add(line);
            }
            return result.toArray(new String[0]);
        } catch (Exception ex) {
            String exMsg = "读取文件失败, path:【" + path + "】";
            LOG.error(exMsg, ex);
            throw new HdfsException(exMsg, ex);
        }
    }

    /**
     * 读取文件内容
     *
     * @param path hdfs路径
     * @return 文件内容
     * @throws com.dianping.dw.polestar.remote.exception.HdfsException
     */
    public static String readFile(String path) throws com.dianping.dw.polestar.remote.exception.HdfsException {
        try {
            Path hdfsPath = new Path(path);
            if (!fs.exists(hdfsPath)) {
                return "";
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(hdfsPath)));
            StringBuffer ret = new StringBuffer();
            String line = br.readLine();
            boolean isFirst = true;
            while (null != line) {
                if (!isFirst) {
                    ret.append('\n');
                } else {
                    isFirst = false;
                }
                ret.append(line);
                line = br.readLine();
            }
            br.close();
            return ret.toString();
        } catch (Exception ex) {
            String exMsg = "读取文件失败, path:【" + path + "】";
            LOG.error(exMsg, ex);
            throw new com.dianping.dw.polestar.remote.exception.HdfsException(exMsg, ex);
        }
    }

    /**
     * 将服务器端的文件上传到 HDFS
     *
     * @param srcPath
     * @param destPath
     */
    public static void copyFileToHdfs(String srcPath, String destPath) throws IOException {
        Path src = new Path(srcPath);
        Path dst = new Path(destPath);

        try {
            fs.copyFromLocalFile(true, true, src, dst);
        } catch (IOException e) {
            LOG.error("copy local file: " + srcPath + " to hdfs 发生错误：" + e.getMessage());
            throw new IOException(e);
        }
    }

    /**
     * 检查HDFS上是否存有对应文件
     *
     * @param filePath
     * @return boolean
     */
    public static boolean hdfsFileExists(String filePath) throws IOException {
        Path path = new Path(filePath);
        return fs.exists(path);
    }

    /**
     * 删除临时文件
     *
     * @param filePath
     * @return boolean
     */
    public static boolean hdfsFileDelete(String filePath) throws IOException {
        Path path = new Path(filePath);
        return fs.delete(path, true);
    }

    /**
     * 修改临时文件的权限
     *
     * @param filePath
     * @param permission
     */
    public static void setFilePermission(String filePath, String permission) throws IOException {
        Path path = new Path(filePath);
        FsPermission permission1 = FsPermission.createImmutable(Short.valueOf(permission, 8));
        fs.setPermission(path, permission1);
    }

    public static boolean createHdfsFolder(String path) throws com.dianping.dw.polestar.remote.exception.HdfsException {
        LOG.info("createHdfsFolder:【" + path + "】");
        Path hdfsPath = new Path(path);
        try {
            if (fs.isFile(hdfsPath)) {
                throw new com.dianping.dw.polestar.remote.exception.HdfsException();
            }
            if (!fs.exists(hdfsPath)) {
                fs.mkdirs(hdfsPath);
                return true;
            } else {
                return false;
            }
        } catch (IOException ex) {
            String exMsg = "创建HDFS目录异常";
            LOG.error(exMsg, ex);
            throw new com.dianping.dw.polestar.remote.exception.HdfsException(exMsg, ex);
        }
    }

    public static void main(String[] args) {
        try {
            HDFSUtil.createHdfsFolder("/data/dw-polestar-web/123");
        } catch (com.dianping.dw.polestar.remote.exception.HdfsException e) {
            e.printStackTrace();
        }
    }

}
