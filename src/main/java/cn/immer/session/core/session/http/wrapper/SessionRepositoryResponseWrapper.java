package cn.immer.session.core.session.http.wrapper;

import cn.immer.session.core.session.Session;
import cn.immer.session.core.session.SessionRepository;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Created by sdj on 2018/3/28.
 */
public class SessionRepositoryResponseWrapper extends OnCommittedResponseWrapper {

    private final HttpServletResponse response;

    public SessionRepositoryResponseWrapper(HttpServletRequest request, HttpServletResponse response) {
        super(response);
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        this.response = response;
    }

    @Override
    protected void onResponseCommitted() {
        this.response.isCommitted();
    }
}
