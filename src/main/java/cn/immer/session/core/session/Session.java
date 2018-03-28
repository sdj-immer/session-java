package cn.immer.session.core.session;

import java.util.Date;
import java.util.Set;

/**
 * Created by Administrator on 2018/3/27.
 */
public interface Session {
    String getId();
    String changeSessionId();
    <T> T getAttribute(String attributeName);


    default <T> T getRequiredAttribute(String name) {
        T result = getAttribute(name);
        if (result == null) {
            throw new IllegalArgumentException(
                    "Required attribute '" + name + "' is missing.");
        }
        return result;
    }


    default <T> T getAttributeOrDefault(String name, T defaultValue) {
        T result = getAttribute(name);
        return result == null ? defaultValue : result;
    }

    Set<String> getAttributeNames();
    void setAttribute(String attributeName, Object attributeValue);
    void removeAttribute(String attributeName);

    Long getCreationTime();
    void setLastAccessedTime(Long lastAccessedTime);
    Long getLastAccessedTime();

    void setMaxInactiveInterval(Integer interval);
    Integer getMaxInactiveInterval();
    boolean isExpired();
}
