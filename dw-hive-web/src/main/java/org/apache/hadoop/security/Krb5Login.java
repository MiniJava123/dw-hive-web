package org.apache.hadoop.security;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 通过username, password 进行krb5认证 返回ugi
 *
 * Author: tao.meng
 */
public class Krb5Login {
    private static final Logger LOGGER = LoggerFactory.getLogger(Krb5Login.class);
    private static final Configuration conf = new Configuration();
    private static Object lockObject = new Object();
    private static Cache<String, UserGroupInformation> ugiCache = CacheBuilder
            .newBuilder().concurrencyLevel(4).maximumSize(100000)
            .expireAfterAccess(24, TimeUnit.HOURS)
            .removalListener(new RemovalListener<String, UserGroupInformation>() {
                @Override
                public void onRemoval(RemovalNotification<String, UserGroupInformation> rn) {
                    LOGGER.info("username: " + rn.getKey() + " ugi was removed from ugiCache");
                }
            }).build();

    static {
        conf.set("hadoop.security.authentication", "kerberos");
    }

    public static UserGroupInformation getVerifiedUgi(String username, final String password) throws LoginException {
        UserGroupInformation ugi;
        synchronized (lockObject) {
            ugi = ugiCache.getIfPresent(username);
            if (null == ugi) {
                ugi = _getVerifiedUgi(username, password);
                ugiCache.put(username, ugi);
            }
        }
        return ugi;
    }

    private static UserGroupInformation _getVerifiedUgi(String username, final String password) throws LoginException {
        
        Subject subject = new Subject();
        LoginContext loginContext = null;
        CallbackHandler handler = new CallbackHandler() {
            @Override
            public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                for (Callback callback : callbacks) {
                    if (callback instanceof PasswordCallback) {
                        PasswordCallback pc = (PasswordCallback) callback;
                        pc.setPassword(password.toCharArray());
                    }
                }
            }
        };
        try {
            HadoopConf hadoopConf = new HadoopConf();
            hadoopConf.putUserKerberosOptions("principal", username + "@DIANPING.COM");
            hadoopConf.putUserKerberosOptions("useTicketCache", "false");
            hadoopConf.putUserKerberosOptions("storeKey", "false");

            loginContext = new LoginContext(HadoopConf.USER_KERBEROS_CONFIG_NAME,
                    subject, handler, hadoopConf);
            loginContext.login();
        } catch (LoginException e) {
            LOGGER.error("kerberos login from " + username + " failure! Reason: " + e.getMessage());
            throw new LoginException("kerberos login from " + username + " failure! Reason: " + e.getMessage());
        }
        UserGroupInformation loginUser = new UserGroupInformation(subject);
        subject.getPrincipals(User.class).iterator().next().setLogin(loginContext);
        loginUser.setAuthenticationMethod(UserGroupInformation.AuthenticationMethod.KERBEROS);
        loginUser = new UserGroupInformation(loginContext.getSubject());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loginUser.getShortUserName():" + loginUser.getShortUserName());
            LOGGER.debug("loginUser.getUserName() " + loginUser.getUserName());
        }
        return loginUser;
    }
}
