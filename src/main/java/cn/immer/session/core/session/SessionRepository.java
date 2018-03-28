package cn.immer.session.core.session;

/**
 * Created by Administrator on 2018/3/27.
 */
public interface SessionRepository <S extends Session> {
    S createSession();
    void save(S session);
    S findById(String id);
    void deleteById(String id);
}
