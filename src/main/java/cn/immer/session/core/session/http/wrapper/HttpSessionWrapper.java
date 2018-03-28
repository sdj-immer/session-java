package cn.immer.session.core.session.http.wrapper;

import cn.immer.session.core.session.Session;
import cn.immer.session.core.session.http.HttpSessionAdapter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

/**
 * Created by Administrator on 2018/3/28.
 */
public class HttpSessionWrapper extends HttpSessionAdapter {
    public HttpSessionWrapper(Session session, ServletContext servletContext) {
        super(session, servletContext);
    }

    @Override
    public void invalidate() {

    }
}
