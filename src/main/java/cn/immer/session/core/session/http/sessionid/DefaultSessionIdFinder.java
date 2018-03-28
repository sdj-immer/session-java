package cn.immer.session.core.session.http.sessionid;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2018/3/28.
 */
public class DefaultSessionIdFinder implements  SessionIdFinder {

    @Override
    public String find(HttpServletRequest request,String cookieName) {
        String sessionId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (cookieName.equals(c.getName())) {
                    sessionId = c.getValue();
                    break;
                }
            }
        }
        return sessionId;
    }
}
