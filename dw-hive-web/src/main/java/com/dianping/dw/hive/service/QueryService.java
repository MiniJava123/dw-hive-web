package com.dianping.dw.hive.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.StreamingOutput;

import com.dianping.dw.hive.constant.DownloadTypeEnum;
import com.dianping.dw.hive.exception.LionException;
import com.dianping.dw.hive.model.QueryDO;
//import com.dianping.dw.polestar.remote.dto.Query;
//import com.dianping.dw.polestar.remote.dto.QueryResult;
//import com.dianping.dw.polestar.remote.exception.ParamException;
//import com.dianping.dw.polestar.remote.exception.SubmissionException;

/**
 * 查询服务
 * 
 * @author yujie.yao
 * @author tao.meng
 */
public interface QueryService {

    /**
     * 提交查询
     * 
     * @param query
     * @param currentTime
     * @return queryId
     * @throws ParamException
     * @throws SubmissionException
     */
    String submit(Query query, Date currentTime) throws ParamException, SubmissionException;

    /**
     * 获取查询结果
     * 
     * @param queryId
     * @param currentTime
     * @return QueryResultResponseFromPolestar 查询结果
     */
    QueryResult getLogAndResult(String queryId, Date currentTime);

    /**
     * 停止查询
     *
     * @param queryId
     * @param adminId
     * @param currentTime
     * @throws LionException 
     * @throws ParamException 
     */
    void stopQuery(String queryId, Integer adminId, Date currentTime) throws LionException, ParamException;

    /**
     * 下载结果文件
     *
     * @param queryId
     * @param downloadType
     * @return StreamingOutput 下载流
     * @throws IOException
     * @throws LionException 
     * */
    StreamingOutput downloadResultFile(String queryId, DownloadTypeEnum downloadType) throws IOException, LionException;

    /**
     * 获取用户的查询历史
     *
     * @param adminId
     * @param keyword
     * @return List<QueryDO> 查询历史信息
     * */
    List<QueryDO> getQueryHistory(Integer adminId, String keyword);

    /**
     * 更新查询结果
     * 
     * @param queryResult 查询结果
     * @param currentTime 当前时间
     * @return
     */
    boolean updateQueryResult(QueryResult queryResult, Date currentTime);

    /**
     * 发送用户反馈信息
     * 
     * @param content 
     * @param email
     * @return
     */
    boolean sendReply(String content, String email);

}
