package cn.immer.session.core.session;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/3/27.
 */
public final class MapSession implements Session,Serializable {


    public static final int DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS = 1800;
    private String id;
    private String originalId;
    private Map<String, Object> sessionAttrs = new HashMap<String, Object>();
    private Long creationTime = System.currentTimeMillis();
    private Long lastAccessedTime = this.creationTime;

    private Integer  maxInactiveInterval = Integer.valueOf(DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS);

    public MapSession(String id) {
        this.id = id;
        this.originalId = id;
    }

    public MapSession(Session session) {
        if (session == null) {
            throw new IllegalArgumentException("session cannot be null");
        }
        this.id = session.getId();
        this.originalId = this.id;
        this.sessionAttrs = new HashMap<String, Object>(
                session.getAttributeNames().size());
        for (String attrName : session.getAttributeNames()) {
            Object attrValue = session.getAttribute(attrName);
            if (attrValue != null) {
                this.sessionAttrs.put(attrName, attrValue);
            }
        }
        this.lastAccessedTime = session.getLastAccessedTime();
        this.creationTime = session.getCreationTime();
        this.maxInactiveInterval = session.getMaxInactiveInterval();
    }
    @Override
    public String getId() {
        return this.id;
    }
    @Override
    public String changeSessionId() {
        String changedId = generateId();
        setId(changedId);
        return changedId;
    }
    @Override
    public <T> T getAttribute(String attributeName) {
        return (T) this.sessionAttrs.get(attributeName);
    }
    @Override
    public Set<String> getAttributeNames() {
        return this.sessionAttrs.keySet();
    }
    @Override
    public void setAttribute(String attributeName, Object attributeValue) {
        if (attributeValue == null ){
            sessionAttrs.remove(attributeName);
        }else {
            this.sessionAttrs.put(attributeName,attributeValue);
        }
    }
    @Override
    public void removeAttribute(String attributeName) {this.sessionAttrs.remove(attributeName);
    }
    @Override
    public Long getCreationTime() {
        return this.creationTime;
    }
    @Override
    public void setLastAccessedTime(Long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }
    @Override
    public Long getLastAccessedTime() {
        return this.lastAccessedTime;
    }
    @Override
    public void setMaxInactiveInterval(Integer interval) {
        this.maxInactiveInterval = interval ;
    }
    @Override
    public Integer getMaxInactiveInterval() {
        return this.maxInactiveInterval;
    }
    @Override
    public boolean isExpired() {
        return false;
    }
    @Override
    public <T> T getRequiredAttribute(String name) {
        return (T) this.sessionAttrs.get(attributeName);
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    private static String generateId() {
        return UUID.randomUUID().toString();
    }
}
