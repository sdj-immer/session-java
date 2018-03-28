package cn.immer.session.core.session.http.wrapper;

import cn.immer.session.core.session.Session;
import cn.immer.session.core.session.SessionFilter;
import cn.immer.session.core.session.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by sdj on 2018/3/28.
 */
public class SessionRepositoryRequestWrapper<S extends Session> extends HttpServletRequestWrapper {

    static  final Logger SESSION_LOGGER = LoggerFactory.getLogger(SessionRepositoryRequestWrapper.class);


    /**
     * The session repository request attribute name.
     */
    public static final String SESSION_REPOSITORY_ATTR = SessionRepository.class.getName();

    private static final String SESSION_LOGGER_NAME = SessionFilter.class.getName().concat(".SESSION_LOGGER");
    /**
     * Invalid session id (not backed by the session repository) request attribute name.
     */
    public static final  String INVALID_SESSION_ID_ATTR = SESSION_REPOSITORY_ATTR + ".invalidSessionId";

    private static final String CURRENT_SESSION_ATTR = SESSION_REPOSITORY_ATTR + ".CURRENT_SESSION";


    private  SessionRepository sessionRepository;

    private Boolean requestedSessionIdValid;
    private boolean requestedSessionInvalidated;
    private final HttpServletResponse response;
    private final ServletContext servletContext;
    private final String COOKIE_NAME ;

    public SessionRepositoryRequestWrapper(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext,String cookieName,SessionRepository sessionRepository) {
        super(request);
        this.response = response;
        this.servletContext = servletContext;
        this.COOKIE_NAME = cookieName;
        this.sessionRepository = sessionRepository ;
    }


    public void commitSession(){
        HttpSessionWrapper wrappedSession = getCurrentSession();
        if (wrappedSession == null) {
            if (isInvalidateClientSession()) {
                SessionRepositoryFilter.this.httpSessionIdResolver.expireSession(this, this.response);
            }
        }
        else {
            S session = wrappedSession.getSession();
            this.sessionRepository.save(session);
            String sessionId = session.getId();
            if (!isRequestedSessionIdValid()
                    || !sessionId.equals(getRequestedSessionId())) {
                this.httpSessionIdResolver.setSessionId(this,this.response, sessionId);
            }
        }
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        if (this.requestedSessionIdValid == null) {
            String sessionId = getRequestedSessionId();
            S session = sessionId == null ? null : getSession(sessionId);
            return isRequestedSessionIdValid(session);
        }

        return this.requestedSessionIdValid;
    }

    private boolean isRequestedSessionIdValid(S session) {
        if (this.requestedSessionIdValid == null) {
            this.requestedSessionIdValid = session != null;
        }
        return this.requestedSessionIdValid;
    }
    private boolean isInvalidateClientSession() {
        return getCurrentSession() == null && this.requestedSessionInvalidated;
    }


    @Override
    public HttpSessionWrapper getSession() {
        return getSession(true);
    }
    @Override
    public HttpSessionWrapper getSession(boolean create) {
        //快速获取session，可以理解为一级缓存、二级缓存这种关系
        HttpSessionWrapper currentSession = getCurrentSession();
        if (currentSession != null) {
            return currentSession;
        }
        //从httpSessionStratge里面根据cookie或者header获取sessionID
        String requestedSessionId = getRequestedSessionId();
        if (requestedSessionId != null && getAttribute(INVALID_SESSION_ID_ATTR) == null) {
            //从存储容器获取session以及设置当次初始化属性
            S session = getSession(requestedSessionId);
            if (session != null) {
                this.requestedSessionIdValid = true;
                currentSession = new HttpSessionWrapper(session, getServletContext());
                currentSession.setNew(false);
                setCurrentSession(currentSession);
                return currentSession;
            }
            else {
                if (SESSION_LOGGER.isDebugEnabled()) {
                    SESSION_LOGGER.debug("No session found by id: Caching result for getSession(false) for this HttpServletRequest.");
                }
                setAttribute(INVALID_SESSION_ID_ATTR, "true");
            }
        }
        if (!create) {
            return null;
        }
        if (SESSION_LOGGER.isDebugEnabled()) {
            SESSION_LOGGER.debug(
                    "A new session was created. To help you troubleshoot where the session was created we provided a StackTrace (this is not an error). You can prevent this from appearing by disabling DEBUG logging for "
                            + SESSION_LOGGER_NAME,
                    new RuntimeException(
                            "For debugging purposes only (not an error)"));
        }
        // 如果该浏览器或者其他http访问者是初次访问服务器，则为他创建个新的session
        S session = sessionRepository.createSession();
        session.setLastAccessedTime(System.currentTimeMillis());
        currentSession = new HttpSessionWrapper(session, getServletContext());
        setCurrentSession(currentSession);
        return currentSession;
    }

    @SuppressWarnings("unchecked")
    private HttpSessionWrapper getCurrentSession() {
        return (HttpSessionWrapper) getAttribute(CURRENT_SESSION_ATTR);
    }

    private S getSession(String sessionId) {
        // 从session存储容器中根据sessionID获取session
        S session = sessionRepository.findById(sessionId);
        if (session == null) {
            return null;
        }
        // 设置sesison的最后访问时间，以防过期
        session.setLastAccessedTime(System.currentTimeMillis());
        return session;
    }

    private void setCurrentSession(HttpSessionWrapper currentSession) {
        if (currentSession == null) {
            removeAttribute(CURRENT_SESSION_ATTR);
        }
        else {
            setAttribute(CURRENT_SESSION_ATTR, currentSession);
        }
    }

    @Override
    public String getRequestedSessionId() {
        return SessionFilter.sessionIdFinder.find(this,COOKIE_NAME);
    }

    @Override
    public ServletContext getServletContext() {
        if (this.servletContext != null) {
            return this.servletContext;
        }
        // Servlet 3.0+
        return super.getServletContext();
    }

}
