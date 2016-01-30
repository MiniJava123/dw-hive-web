package com.dianping.dw.hive.exception;

import java.util.Locale;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异常处理类
 * 
 * @author yujie.yao
 */
@Provider
public class CustomExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = LoggerFactory.getLogger(CustomExceptionMapper.class);

    @Override
    public Response toResponse(Exception ex) {
        LOG.error("ExceptionMapper:\n", ex);
        if (ex != null) {
            shortenStackTrace(ex);
        }
        return buildResponse(ex);
    }

    /**
     * 根据异常生成响应正文
     * 
     * @param ex 异常
     * @return 响应正文
     */
    private Response buildResponse(Exception ex) {
        return Response
                .status(Status.INTERNAL_SERVER_ERROR)
                .type("text/html;charset=UTF-8")
                .header("Exception-Type",
                        ex == null ? "null"
                                : (ex.getClass() == null ? "" : ex.getClass().getName()))
                .entity(ex == null ? "null" : ex).language(Locale.SIMPLIFIED_CHINESE).build();
    }

    /**
     * 全量StackTrace太长，前端截取第一个
     * 
     * @param ex 异常
     */
    private void shortenStackTrace(Exception ex) {
        StackTraceElement[] trace = new StackTraceElement[1];
        trace[0] = ex.getStackTrace()[0];
        ex.setStackTrace(trace);
    }

}
