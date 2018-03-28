package cn.immer.session.core.session.http.sessionid;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2018/3/28.
 */
public interface SessionIdFinder {

    String find(HttpServletRequest request,String cookieName);
}
