package com.dianping.dw.hive.service.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import jersey.repackaged.com.google.common.collect.Lists;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dianping.dw.hive.constant.DownloadTypeEnum;
import com.dianping.dw.hive.exception.EnumTypeException;
import com.dianping.dw.hive.exception.HdfsException;
import com.dianping.dw.hive.exception.LionException;
import com.dianping.dw.hive.mapper.QueryDOMapper;
import com.dianping.dw.hive.mapper.QueryLogDOMapper;
import com.dianping.dw.hive.model.QueryDO;
import com.dianping.dw.hive.model.QueryDOExample;
import com.dianping.dw.hive.model.QueryDOExample.Criteria;
import com.dianping.dw.hive.model.QueryLogDO;
import com.dianping.dw.hive.service.QueryService;
import com.dianping.dw.hive.util.CommonUtil;
import com.dianping.dw.hive.util.HDFSUtil;
import com.dianping.dw.hive.util.LionUtil;
import com.dianping.dw.polestar.remote.PolestarService;
import com.dianping.dw.polestar.remote.constant.QueryStatusEnum;
import com.dianping.dw.polestar.remote.dto.Query;
import com.dianping.dw.polestar.remote.dto.QueryResult;
import com.dianping.dw.polestar.remote.exception.ParamException;
import com.dianping.dw.polestar.remote.exception.SubmissionException;
import com.dianping.pigeon.remoting.invoker.config.annotation.Reference;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
/**
 * 查询服务
 *
 * @author yujie.yao
 * @author tao.meng
 */
@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class QueryServiceImpl implements QueryService {

    private static final Logger LOG = LoggerFactory.getLogger(QueryServiceImpl.class);

    /**
     * LOG文件后缀
     */
    private static final String LOG_SUFFIX = ".log";

    /**
     * 根目录
     */
    private static String WORK_DIRECTORY_BASE;

    /**
     * 最大查询行数
     */
    private static int QUERY_RESULT_MAX_LINES;


    /**
     * mail 相关参数
     */
    private static final String[] ADMIN_MAILS ;
    private static final String MAIL_SUBJECT ;
    private static final String MAIL_SMTP_HOST_LABEL ;
    private static final String MAIL_SMTP_HOST ;
    private static final String MAIL_SMTP_AUTH_LABEL ;
    private static final String MAIL_SMTP_AUTH;
    private static final String MAIL_51PING;

    /**
     * 返回查询历史记录的条数
     */
    private static final int HISTORY_SQL_NUM;

    static {
        QUERY_RESULT_MAX_LINES = LionUtil.getIntProperty("dw-hive-web.query.result.max.lines");
        WORK_DIRECTORY_BASE = LionUtil.getProperty("dw-polestar-web.work.directory.base");
        ADMIN_MAILS = LionUtil.getProperty("dw-hive-web.hive-web-admin-mails").split(",");
        MAIL_SUBJECT = LionUtil.getProperty("dw-hive-web.mail.subject");
        MAIL_SMTP_HOST_LABEL = LionUtil.getProperty("dw-hive-web.mail.smtp.host.label");
        MAIL_SMTP_HOST = LionUtil.getProperty("dw-hive-web.mail.smtp.host");
        MAIL_SMTP_AUTH_LABEL = LionUtil.getProperty("dw-hive-web.mail.smtp.auth.label");
        MAIL_SMTP_AUTH = LionUtil.getProperty("dw-hive-web.mail.smtp.auth");
        MAIL_51PING = LionUtil.getProperty("dw-hive-web.mail.51ping");
        HISTORY_SQL_NUM = LionUtil.getIntProperty("dw-hive-web.history.query.sql.num");
    }

    @Autowired
    private QueryDOMapper queryDOMapper;

    @Autowired
    private QueryLogDOMapper queryLogDOMapper;

    @Reference(timeout = 1000, url = "com.dianping.dw.polestar.remote.PolestarService")
    private PolestarService polestarService;

    private static final int EOF = -1;
    private static final int DEFAULT_EXEC_TIME = -1;
    private static final char CSV_FIELD_SEPARATOR = '\t';

    @Override
    @Transactional
    public String submit(Query query, Date currentTime) throws ParamException, SubmissionException {
        // 0. 参数检查
        checkNotNull(query);
        checkNotNull(currentTime);

        String queryId = "";
        String detail = "";
        QueryStatusEnum queryStatus = null;
        try {
            // 1. 调用polestar
            LOG.info("提交Polestar查询，query:【" + query.toString() + "】");
            queryId = polestarService.submitQuery(query);
        } catch (ParamException ex) {
            LOG.error("Polestar提交查询失败，参数错误，query:【" + query.toString() + "】", ex);
            detail = CommonUtil.getStackTraceMsg(ex);
            throw ex;
        } catch (SubmissionException ex) {
            LOG.error("Polestar提交查询失败，Polestar端处理异常，query:【" + query.toString() + "】", ex);
            detail = CommonUtil.getStackTraceMsg(ex);
            throw ex;
        } finally {
            Integer adminId = query.getAdminId();
            LOG.info("【" + adminId + "】提交了SQL:【" + query.getHql() + "】");

            // 2. 获取queryId
            if (!Strings.isNullOrEmpty(queryId)) {
                queryStatus = QueryStatusEnum.READY;
            } else {
                queryId = "";
                queryStatus = QueryStatusEnum.FAILED;
            }

            // 3. 插QueryDO
            QueryDO queryDO = new QueryDO();
            queryDO.setAddTime(currentTime);
            queryDO.setUpdateTime(currentTime);
            queryDO.setUuid(queryId);
            queryDO.setAdminId(adminId);
            queryDO.setDatabaseName(query.getDatabaseName());
            queryDO.setQueryEngine(query.getExecMode().value());
            queryDO.setRole(query.getRoleName());
            queryDO.setProperties(query.getProperties().toString());
            queryDO.setQuerySql(query.getHql());
            queryDO.setDetail(detail);
            queryDO.setQueryStatus(queryStatus.value());
            queryDO.setLogPath(WORK_DIRECTORY_BASE + File.separator + queryId + File.separator + queryId + LOG_SUFFIX);
            queryDO.setCsvPath("");
            queryDO.setXlsxPath("");
            queryDO.setResultFilePath("");
            queryDO.setExecTime(DEFAULT_EXEC_TIME);
            queryDOMapper.insert(queryDO);

            // 插入Log信息不再需要, 后续不会使用, 并且插入的log detail信息字段内容太大
            // 4. 插QueryLogDO
            /**
            QueryLogDO queryLogDO = new QueryLogDO();
            queryLogDO.setAddTime(currentTime);
            queryLogDO.setUpdateTime(currentTime);
            queryLogDO.setUuid(queryId);
            queryLogDO.setAdminId(adminId);
            queryLogDO.setDatabaseName(query.getDatabaseName());
            queryLogDO.setQueryEngine(query.getExecMode().value());
            queryLogDO.setRole(query.getRoleName());
            queryLogDO.setProperties(query.getProperties().toString());
            queryLogDO.setQuerySql(query.getHql());
            queryLogDO.setDetail(detail);
            queryLogDO.setQueryStatus(queryStatus.value());
            queryLogDO.setLogPath(WORK_DIRECTORY_BASE + File.separator + queryId + File.separator + queryId + LOG_SUFFIX);
            queryLogDO.setCsvPath("");
            queryLogDO.setXlsxPath("");
            queryLogDO.setResultFilePath("");
            queryLogDO.setExecTime(DEFAULT_EXEC_TIME);
            queryLogDOMapper.insert(queryLogDO);
             */
        }

        return queryId;
    }

    @Override
    @Transactional
    public QueryResult getLogAndResult(String queryId, Date currentTime) {
        // 0. 参数检查
        checkNotNull(queryId);
        checkArgument(!queryId.isEmpty());
        checkNotNull(currentTime);

        // 1. 搜QueryDO
        QueryDOExample queryDOExample = new QueryDOExample();
        queryDOExample.or().andUuidEqualTo(queryId);

        List<QueryDO> queryList = queryDOMapper.selectByExample(queryDOExample);

        QueryResult queryResult = new QueryResult();
        String exMsg = "";
        if (null == queryList || queryList.size() != 1) {
            queryResult.setQueryId(queryId);
            queryResult.setQueryStatus(QueryStatusEnum.FAILED);
            exMsg = "查询结果一致性异常，queryList's size:【" + (null == queryList ? 0 : queryList.size()) + "】";
            LOG.error(exMsg);
            queryResult.setQueryLog(exMsg);

            queryResult.setAdminId(0);
            queryResult.setColumnNames(null);
            queryResult.setCsvPath("");
            queryResult.setXlsxPath("");
            queryResult.setData(null);
            queryResult.setExecTime(-1);
        } else {
            QueryDO queryDO = queryList.get(0);

            String queryStatus = queryDO.getQueryStatus();
            String csvPath = queryDO.getCsvPath();
            String xlsxPath = queryDO.getXlsxPath();

            queryResult.setQueryId(queryId);
            QueryStatusEnum queryStatusEnum = null;
            for (QueryStatusEnum obj : QueryStatusEnum.values()) {
                if (obj.toString().equals(queryStatus)) {
                    queryStatusEnum = obj;
                    break;
                }
            }
            queryResult.setQueryStatus(queryStatusEnum);
            
            //在polestar log中总会出现一次读取log时, log文件不在的报错，可能是在这里query提交后
            //执行状态还是ready, 就来了一次查询log的请求, 此时该query还没有生成log文件, 就报错了.
            
            String queryLog = "";


            String logPath = queryDO.getLogPath();
            LOG.info("logPath:【" + logPath + "】");
            if ( !queryStatus.equals(QueryStatusEnum.READY.toString()) ) {
                try {
                    // Todo 修改成从 hdfs 读取 log 信息
                    // queryLog = FileUtils.readFileToString(new File(logPath));
                    queryLog = HDFSUtil.readFile(logPath);
                } catch (com.dianping.dw.polestar.remote.exception.HdfsException ex) {
                    LOG.error("HdfsException", ex);
                }
            }

            queryResult.setLogPath(logPath);
            queryResult.setQueryLog(queryLog);
            queryResult.setAdminId(queryDO.getAdminId());
            queryResult.setCsvPath(csvPath);
            queryResult.setXlsxPath(xlsxPath);
            queryResult.setExecTime(queryDO.getExecTime());

            // HDFS取数据
            if (queryResult.getQueryStatus().equals(QueryStatusEnum.FINISHED)) {
                String[] columnNames = null;
                String[][] data = null;
                try {
                    String[] dataWithColumnNames = HDFSUtil.readFileLimited(csvPath, QUERY_RESULT_MAX_LINES + 1);
//                    LOG.info("HadoopUtil.readFileLimited:【" + StringUtils.join(dataWithColumnNames, "\n") + "】");
                    if (null != dataWithColumnNames && dataWithColumnNames.length > 0) {
                        String columnNamesLine = dataWithColumnNames[0];
                        columnNames = Lists.newArrayList(Splitter.on(CSV_FIELD_SEPARATOR).split(columnNamesLine)).toArray(new String[0]);
                        data = new String[dataWithColumnNames.length - 1][];
                        for (int i = 1; i < dataWithColumnNames.length; ++i) {
                            String[] arr = Lists.newArrayList(Splitter.on(CSV_FIELD_SEPARATOR).split(dataWithColumnNames[i])).toArray(new String[0]);
                            data[i - 1] = arr;
                        }
                    }
                } catch (HdfsException ex) {
                    LOG.error("HDFS读取CSV文件异常", ex);
                }
                queryResult.setColumnNames(columnNames);
                queryResult.setData(data);
            } else {
                queryResult.setColumnNames(null);
                queryResult.setData(null);
            }
        }
        return queryResult;
    }

    @Override
    @Transactional
    public void stopQuery(String queryId, Integer adminId, Date currentTime) throws LionException, ParamException {
        // 0. check params
        checkNotNull(queryId);
        checkArgument(!queryId.isEmpty());
        checkNotNull(adminId);
        checkNotNull(currentTime);

        // 1. call polestar
        polestarService.stopQuery(queryId);

        // 2. find query
        QueryDOExample queryDOExample = new QueryDOExample();
        queryDOExample.or().andUuidEqualTo(queryId);
        List<QueryDO> queryDOList = queryDOMapper.selectByExample(queryDOExample);

        checkNotNull(queryDOList);
        checkArgument(queryDOList.size() == 1);

        QueryDO queryDO = queryDOList.get(0);

        if (queryDO.getQueryStatus().equals(QueryStatusEnum.READY.value()) ||
                queryDO.getQueryStatus().equals(QueryStatusEnum.RUNNING.value())) {
            // 3. update query
            queryDO.setUpdateTime(currentTime);
            queryDO.setExecTime(DEFAULT_EXEC_TIME);
            queryDO.setQueryStatus(QueryStatusEnum.STOPPED.value());
            queryDOMapper.updateByExampleSelective(queryDO, queryDOExample);

            // 4. add querylog
            /**
            QueryLogDO queryLogDO = new QueryLogDO();
            queryLogDO.setAddTime(currentTime);
            queryLogDO.setUpdateTime(currentTime);
            queryLogDO.setUuid(queryId);
            queryLogDO.setAdminId(adminId);
            queryLogDO.setDatabaseName(queryDO.getDatabaseName());
            queryLogDO.setQueryEngine(queryDO.getQueryEngine());
            queryLogDO.setRole(queryDO.getRole());
            queryLogDO.setProperties(queryDO.getProperties());
            queryLogDO.setQuerySql(queryDO.getQuerySql());
            queryLogDO.setExecTime(DEFAULT_EXEC_TIME);
            queryLogDO.setDetail("");
            queryLogDO.setQueryStatus(queryDO.getQueryStatus());
            queryLogDO.setResultFilePath(queryDO.getResultFilePath());
            queryLogDO.setCsvPath(queryDO.getCsvPath());
            queryLogDO.setXlsxPath(queryDO.getXlsxPath());
            queryLogDO.setLogPath(queryDO.getLogPath());
            queryLogDOMapper.insert(queryLogDO);
             */
        }
    }

    @Override
    public StreamingOutput downloadResultFile(String queryId, DownloadTypeEnum downloadType) throws IOException, LionException {
        QueryDOExample queryDOExample = new QueryDOExample();
        queryDOExample.or().andUuidEqualTo(queryId);
        List<QueryDO> queryDOList = queryDOMapper.selectByExample(queryDOExample);

        checkNotNull(queryDOList);
        checkArgument(queryDOList.size() == 1);

        final String filePath;
        if (DownloadTypeEnum.CSV.equals(downloadType)) {
            filePath = queryDOList.get(0).getCsvPath();
        } else if (DownloadTypeEnum.XLSX.equals(downloadType)) {
            filePath = queryDOList.get(0).getXlsxPath();
        } else {
            String exMsg = "下载类型错误，downloadType:【" + downloadType + "】";
            LOG.error(exMsg);
            throw new EnumTypeException(exMsg);
        }
        LOG.info("下载文件，filePath:【" + filePath + "】");

        final InputStream is = HDFSUtil.open(filePath);
        StreamingOutput streamingOutput = new StreamingOutput() {
            @Override
            public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                try {
                    long count = 0;
                    int read = 0;
                    byte[] bytes = new byte[1024 * 4];
                    while (EOF != (read = is.read(bytes))) {
                        outputStream.write(bytes, 0, read);
                        count += read;
                    }
                    LOG.info("【" + filePath + "】读取了" + count + " bytes");
                } catch (IOException ex) {
                    LOG.error("读取文件异常，filePath:【" + filePath + "】", ex);
                    throw new IOException("读取文件异常，filePath:【" + filePath + "】", ex);
                }
            }
        };
        return streamingOutput;
    }

    @Override
    public List<QueryDO> getQueryHistory(Integer adminId, String keyword) {
        // 0. 参数检查
        checkNotNull(adminId);
        checkNotNull(keyword);

        // 1. 搜索
        QueryDOExample queryDOExample = new QueryDOExample();
        Criteria criteria = queryDOExample.or();
        criteria.andAdminIdEqualTo(adminId);
        LOG.info("adminId:" + adminId + ",keyword:" + keyword);
        if (!Strings.isNullOrEmpty(keyword)) {
            criteria.andQuerySqlLike('%' + keyword + '%');
        }

        queryDOExample.setOrderByClause("ID desc");

        List<QueryDO> res = queryDOMapper.selectByExample(queryDOExample);
        LOG.info("res's size:" + res.size());
        // limit 500
        /**
         * 这里的500修改成在lion里 可配值的
         */
        List<QueryDO> ret = Lists.newArrayList();
        for (int i = 0; i < res.size() && i < HISTORY_SQL_NUM; ++i) {
            ret.add(res.get(i));
        }
        return ret;
    }

    @Override
    public boolean updateQueryResult(QueryResult queryResult, Date currentTime) {
        // 0. 参数检查
        checkNotNull(queryResult);

        String queryId = queryResult.getQueryId();
        String csvPath = queryResult.getCsvPath();
        String xlsxPath = queryResult.getXlsxPath();
        String queryStatus = queryResult.getQueryStatus().toString();
        Integer adminId = queryResult.getAdminId();
        Integer execTime = queryResult.getExecTime();
        String logPath = queryResult.getLogPath();
        String queryLog = queryResult.getQueryLog();

        checkNotNull(queryId);
        checkArgument(!queryId.isEmpty());
        checkNotNull(adminId);
        checkNotNull(currentTime);
        checkNotNull(csvPath);
        checkNotNull(xlsxPath);
        checkNotNull(execTime);
        checkNotNull(queryStatus);
        checkArgument(!queryStatus.isEmpty());

        // 1. 更新QueryDO
        QueryDOExample queryExample = new QueryDOExample();
        queryExample.or().andUuidEqualTo(queryId);

        QueryDO queryDO = new QueryDO();
        queryDO.setUpdateTime(currentTime);
        queryDO.setCsvPath(csvPath);
        queryDO.setXlsxPath(xlsxPath);
        queryDO.setExecTime(execTime);
        queryDO.setQueryStatus(queryStatus);
        queryDO.setResultFilePath("");
        queryDO.setLogPath(logPath);
        queryDO.setDetail("");

        queryDOMapper.updateByExampleSelective(queryDO, queryExample);

        // 不再需要更新或者插入QueryLog表
        /**
        List<QueryDO> queryDOList = queryDOMapper.selectByExample(queryExample);
        if (queryDOList == null || queryDOList.size() != 1) {
            return true;
        }
        queryDO = queryDOList.get(0);

        // 2. 插QueryLogDO
        QueryLogDO queryLogDO = new QueryLogDO();
        queryLogDO.setAddTime(currentTime);
        queryLogDO.setUpdateTime(currentTime);
        queryLogDO.setUuid(queryId);
        queryLogDO.setAdminId(adminId);
        queryLogDO.setDatabaseName(queryDO.getDatabaseName());
        queryLogDO.setQueryEngine(queryDO.getQueryEngine());
        queryLogDO.setRole(queryDO.getRole());
        queryLogDO.setProperties(queryDO.getProperties());
        queryLogDO.setQuerySql(queryDO.getQuerySql());
        queryLogDO.setDetail(queryLog);
        queryLogDO.setQueryStatus(queryStatus);
        // legacy
        queryLogDO.setResultFilePath("");
        queryLogDO.setLogPath(logPath);
        queryLogDO.setCsvPath(csvPath);
        queryLogDO.setXlsxPath(xlsxPath);
        queryLogDO.setExecTime(execTime);

        queryLogDOMapper.insert(queryLogDO);
        */
        
        return true;
    }

    @Override
    public boolean sendReply(String content, String userEmail) {
        List<String> mailAddr = new ArrayList<String>();
        String[] adminMails = ADMIN_MAILS;
        for (String mail: adminMails) {
            mailAddr.add(mail);
        }
        mailAddr.add(userEmail);

        Properties props = new Properties();
        props.put(MAIL_SMTP_HOST_LABEL, MAIL_SMTP_HOST);
        props.put(MAIL_SMTP_AUTH_LABEL, MAIL_SMTP_AUTH);
        Session session = Session.getInstance(props);
        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(MAIL_51PING));
            for (String mail: mailAddr) {
                msg.addRecipients(Message.RecipientType.TO, mail);
            }
            msg.setSubject(MAIL_SUBJECT);
            msg.setSentDate(new Date());
            msg.setContent(content, "text/plain;charset=utf8");
            Transport.send(msg);
        } catch (Exception e) {
            LOG.error("发送用户反馈信息失败：", e.getMessage());
            return false;
        }
        return true;
    }

}
