package cn.immer.session.core.session;

import cn.immer.session.core.session.http.sessionid.DefaultSessionIdFinder;
import cn.immer.session.core.session.http.sessionid.SessionIdFinder;
import cn.immer.session.core.session.http.wrapper.SessionRepositoryRequestWrapper;
import cn.immer.session.core.session.http.wrapper.SessionRepositoryResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 2018/3/28.
 */
public class SessionFilter  extends OncePerRequestFilter{
    private static final Logger LOG = LoggerFactory.getLogger(SessionFilter.class);
    public static String SESSION_REPOSITORY_FACTORY = "SESSION_REPOSITORY_FACTORY";


    public static String SESSION_KEY = "session";
    private String  cookieName = "sessionId";
    private Boolean httpOnly = true;

    public  static SessionRepositoryFactory repositoryFactory;
    private ServletContext servletContext;

    public static final SessionIdFinder sessionIdFinder = new DefaultSessionIdFinder();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String factory = filterConfig.getInitParameter("factory");
        if (factory != null && !"".equals(factory)) {
            try {
                repositoryFactory = (SessionRepositoryFactory) Class.forName(factory).newInstance();
            } catch (Exception e) {
                LOG.error("instantiation session repository factory exception", e);
            }
        }
        if (repositoryFactory == null) {
            repositoryFactory = new DefaultSessionRepositoryFactory();
        }

        this.servletContext = filterConfig.getServletContext();
        filterConfig.getServletContext().setAttribute(SESSION_REPOSITORY_FACTORY, repositoryFactory);

        String sessionKey = filterConfig.getInitParameter("sessionKey");
        if (sessionKey != null && !"".equals(sessionKey)) {
            SESSION_KEY = sessionKey;
        }
        String cookieName = filterConfig.getInitParameter("cookieName");
        if (cookieName != null && !"".equals(cookieName)) {
            this.cookieName = cookieName;
        }
        String httpOnly = filterConfig.getInitParameter("httpOnly");
        if (httpOnly != null && !"".equals(httpOnly)) {
            this.httpOnly = Boolean.valueOf(httpOnly);
        }
    }


    @Override
    protected  void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        SessionRepositoryRequestWrapper  wrappedRequest  = new SessionRepositoryRequestWrapper(request, response, this.servletContext,this.cookieName,repositoryFactory.getSessionRepository());
        SessionRepositoryResponseWrapper wrappedResponse = new SessionRepositoryResponseWrapper(request, response);
        try {
            filterChain.doFilter(wrappedRequest,wrappedResponse);
        }
        finally{
            wrappedRequest.commitSession();
        }
    }

    @Override
    public void destroy() {

    }
}
