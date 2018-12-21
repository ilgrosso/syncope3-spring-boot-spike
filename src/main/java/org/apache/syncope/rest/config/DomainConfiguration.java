package org.apache.syncope.rest.config;

import java.io.Serializable;

public class DomainConfiguration implements Serializable {

    private static final long serialVersionUID = 3842608635517859919L;

    private String domainName;

    private String driverClassName;

    private String url;

    private String schema;

    private String username;

    private String password;

    private String transactionIsolation;

    private String maximumPoolSize;

    private String minimumIdle;

    private String auditSql;

    private String orm;

    private String databasePlatform;

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTransactionIsolation() {
        return transactionIsolation;
    }

    public void setTransactionIsolation(String transactionIsolation) {
        this.transactionIsolation = transactionIsolation;
    }

    public String getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(String maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public String getMinimumIdle() {
        return minimumIdle;
    }

    public void setMinimumIdle(String minimumIdle) {
        this.minimumIdle = minimumIdle;
    }

    public String getAuditSql() {
        return auditSql;
    }

    public void setAuditSql(String auditSql) {
        this.auditSql = auditSql;
    }

    public String getOrm() {
        return orm;
    }

    public void setOrm(String orm) {
        this.orm = orm;
    }

    public String getDatabasePlatform() {
        return databasePlatform;
    }

    public void setDatabasePlatform(String databasePlatform) {
        this.databasePlatform = databasePlatform;
    }

    public DomainConfiguration domainName(final String value) {
        this.domainName = value;
        return this;
    }

    public DomainConfiguration driverClassName(final String value) {
        this.driverClassName = value;
        return this;
    }

    public DomainConfiguration url(final String value) {
        this.url = value;
        return this;
    }

    public DomainConfiguration schema(final String value) {
        this.schema = value;
        return this;
    }

    public DomainConfiguration username(final String value) {
        this.username = value;
        return this;
    }

    public DomainConfiguration password(final String value) {
        this.password = value;
        return this;
    }

    public DomainConfiguration transactionIsolation(final String value) {
        this.transactionIsolation = value;
        return this;
    }

    public DomainConfiguration maximumPoolSize(final String value) {
        this.maximumPoolSize = value;
        return this;
    }

    public DomainConfiguration minimumIdle(final String value) {
        this.minimumIdle = value;
        return this;
    }

    public DomainConfiguration auditSql(final String value) {
        this.auditSql = value;
        return this;
    }

    public DomainConfiguration orm(final String value) {
        this.orm = value;
        return this;
    }

    public DomainConfiguration databasePlatform(final String value) {
        this.databasePlatform = value;
        return this;
    }

}
