package com.dianping.dw.hive.resource;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dianping.dw.hive.model.ReplyDO;
import com.dianping.dw.hive.model.ResultDO;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dianping.dw.hive.constant.DownloadTypeEnum;
import com.dianping.dw.hive.exception.AuthorizationException;
import com.dianping.dw.hive.exception.LionException;
import com.dianping.dw.hive.model.QueryDO;
import com.dianping.dw.hive.security.HWSecurityContext;
import com.dianping.dw.hive.security.Role;
import com.dianping.dw.hive.security.User;
import com.dianping.dw.hive.service.QueryService;
import com.dianping.dw.polestar.remote.constant.ExecModeEnum;
import com.dianping.dw.polestar.remote.dto.Query;
import com.dianping.dw.polestar.remote.dto.QueryResult;
import com.dianping.dw.polestar.remote.exception.ParamException;
import com.google.common.base.Strings;

/**
 * 查询资源类
 * 
 * @author yujie.yao
 * @author tao.meng
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Path("/query")
@Produces(MediaType.APPLICATION_JSON)
public class QueryResource {

    private static final Logger LOG = LoggerFactory.getLogger(QueryResource.class);

    @Context
    private ContainerRequestContext requestContext;

    @Autowired
    private QueryService queryService;

    @POST
    @Path("/submit")
    public String submitQuery(Query query) throws Exception {
        Date currentTime = new Date();

        String application = query.getApplication();
        String databaseName = query.getDatabaseName();
        ExecModeEnum execMode = query.getExecMode();
        String roleName = query.getRoleName();
        String hql = query.getHql();
        Integer rowLimit = query.getRowLimit();
        String properties = query.getProperties();

        // 0. 参数检查
        checkArgument(!Strings.isNullOrEmpty(application));
        checkArgument(!Strings.isNullOrEmpty(databaseName));
        checkNotNull(execMode);
        checkArgument(!Strings.isNullOrEmpty(roleName));
        checkArgument(!Strings.isNullOrEmpty(hql));
        checkNotNull(rowLimit);
        checkArgument(rowLimit > 0);
        checkNotNull(properties);

        // 1. 获取用户信息
        HWSecurityContext context = (HWSecurityContext) requestContext.getSecurityContext();
        User user = (User) context.getUserPrincipal();

        query.setAdminId(user.getAdminId());

        // 2. 检查权限，填入密码
        fillInPassword(query, roleName, user);

        // 3. 提交查询
        return queryService.submit(query, currentTime);
    }

    @GET
    @Path("/result/{queryId}")
    public QueryResult getLogAndResult(@NotEmpty @PathParam("queryId") String queryId) {
        Date currentTime = new Date();

        return queryService.getLogAndResult(queryId, currentTime);
    }

    @GET
    @Path("/stop/{queryId}")
    public void stopQuery(@PathParam("queryId") String queryId) throws LionException, ParamException {
        Date currentTime = new Date();

        HWSecurityContext context = (HWSecurityContext) requestContext.getSecurityContext();
        User user = (User) context.getUserPrincipal();

        queryService.stopQuery(queryId, user.getAdminId(), currentTime);
    }

    @GET
    @Path("/download/csv/{queryId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadCsv(@PathParam("queryId") String queryId) throws IOException, LionException {
        return Response.ok(queryService.downloadResultFile(queryId, DownloadTypeEnum.CSV))
                .header("Content-Disposition", "attachment;filename=" + queryId + ".csv").build();
    }

    @GET
    @Path("/download/xlsx/{queryId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadXlsx(@PathParam("queryId") String queryId) throws IOException, LionException {
        return Response.ok(queryService.downloadResultFile(queryId, DownloadTypeEnum.XLSX))
                .header("Content-Disposition", "attachment;filename=" + queryId + ".xlsx").build();
    }

    @POST
    @Path("/history")
    @Produces(MediaType.APPLICATION_JSON)
    public List<QueryDO> getQueryHistory(Map<String, String> paramMap) {
        HWSecurityContext context = (HWSecurityContext) requestContext.getSecurityContext();
        User user = (User) context.getUserPrincipal();

        String keyword = paramMap.get("keyword");
        return queryService.getQueryHistory(new Integer(user.getName()), keyword);
    }
    
    @POST
    @Path("/reply")
    @Produces(MediaType.APPLICATION_JSON)
    public ResultDO<String> sendReply(ReplyDO reply) {
        ResultDO<String> result = new ResultDO<String>();
        HWSecurityContext context = (HWSecurityContext) requestContext.getSecurityContext();
        User user = (User) context.getUserPrincipal();
        
        boolean isSuccess = queryService.sendReply(reply.getReply(), user.getEmployeeEmail());
        LOG.info("User: " + user.getEmployeeCnName() + " send feedback!");
        if (isSuccess) {
            result.setSuccess(true);
        } else {
            result.setMessages("后台发送反馈信息失败，请联系管理员！");
            result.setSuccess(false);
        }
        return result;
    }

    private void fillInPassword(Query query, String roleName, User user) throws AuthorizationException {
        List<Role> roleList = user.getRoleList();
        boolean found = false;
        for (Role role : roleList) {
            if (roleName.equals(role.getUserName())) {
                query.setRolePassword(role.getPassword());
                found = true;
                break;
            }
        }
        if (!found) {
            LOG.error("对不起，您没有" + roleName + "的权限");
            throw new AuthorizationException("对不起，您没有" + roleName + "的权限");
        }
    }

}
