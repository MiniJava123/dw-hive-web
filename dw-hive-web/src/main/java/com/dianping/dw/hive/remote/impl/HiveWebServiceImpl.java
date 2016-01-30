package com.dianping.dw.hive.remote.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dianping.dw.hive.service.QueryService;
import com.dianping.dw.hiveweb.remote.HiveWebService;
import com.dianping.dw.hiveweb.remote.exception.ParamException;
import com.dianping.dw.polestar.remote.dto.QueryResult;
import com.dianping.pigeon.remoting.provider.config.annotation.Service;

/**
 * Polestar查询服务
 * 
 * @author yujie.yao
 */
@Service(port = 9600, autoSelectPort = false)
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class HiveWebServiceImpl implements HiveWebService {

    private static final Logger LOG = LoggerFactory.getLogger(HiveWebServiceImpl.class);

    @Autowired
    private QueryService queryService;

    @Override
    public boolean updateQueryResult(QueryResult queryResult) throws ParamException {
        // 0. 参数检查
        checkNotNull(queryResult, "queryResult不能为null");

//        LOG.info("收到Pigeon请求, updateQueryResult(), queryResult:【" + queryResult + "】");

        // 1. 更新
        Date currentTime = new Date();
        return queryService.updateQueryResult(queryResult, currentTime);
    }

}
