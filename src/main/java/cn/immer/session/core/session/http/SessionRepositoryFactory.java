package cn.immer.session.core.session.http;

import cn.immer.session.core.session.SessionRepository;

/**
 * session repository factory.
 *
 * @author sdj
 */
public interface SessionRepositoryFactory {
    SessionRepository getSessionRepository();
}
