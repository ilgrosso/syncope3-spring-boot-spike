package org.apache.syncope.rest.config;

import java.io.Serializable;
import org.apache.openjpa.jdbc.sql.DBDictionary;
import org.apache.syncope.common.lib.types.CipherAlgorithm;

public class DomainCR implements Serializable {

    private static final long serialVersionUID = 3842608635517859919L;

    public static class Builder {

        private final DomainCR conf;

        public Builder(final String domainName) {
            this.conf = new DomainCR();
            this.conf.domainName = domainName;
        }

        public Builder jdbcDriver(final String jdbcDriver) {
            this.conf.jdbcDriver = jdbcDriver;
            return this;
        }

        public Builder jdbcURL(final String jdbcURL) {
            this.conf.jdbcURL = jdbcURL;
            return this;
        }

        public Builder dbSchema(final String dbSchema) {
            this.conf.dbSchema = dbSchema;
            return this;
        }

        public Builder dbUsername(final String dbUsername) {
            this.conf.dbUsername = dbUsername;
            return this;
        }

        public Builder dbPassword(final String dbPassword) {
            this.conf.dbPassword = dbPassword;
            return this;
        }

        public Builder transactionIsolation(final String transactionIsolation) {
            this.conf.transactionIsolation = transactionIsolation;
            return this;
        }

        public Builder maxPoolSize(final int maxPoolSize) {
            this.conf.maxPoolSize = maxPoolSize;
            return this;
        }

        public Builder minIdle(final int minIdle) {
            this.conf.minIdle = minIdle;
            return this;
        }

        public Builder auditSql(final String auditSql) {
            this.conf.auditSql = auditSql;
            return this;
        }

        public Builder orm(final String orm) {
            this.conf.orm = orm;
            return this;
        }

        public Builder databasePlatform(final Class<? extends DBDictionary> databasePlatform) {
            this.conf.databasePlatform = databasePlatform.getName();
            return this;
        }

        public Builder adminPassword(final String adminPassword) {
            this.conf.adminPassword = adminPassword;
            return this;
        }

        public Builder adminCipherAlgorithm(final CipherAlgorithm adminCipherAlgorithm) {
            this.conf.adminCipherAlgorithm = adminCipherAlgorithm;
            return this;
        }

        public DomainCR build() {
            return conf;
        }
    }

    private String domainName;

    private String jdbcDriver;

    private String jdbcURL;

    private String dbSchema;

    private String dbUsername;

    private String dbPassword;

    private String transactionIsolation = "TRANSACTION_READ_COMMITTED";

    private int maxPoolSize = 10;

    private int minIdle = 2;

    private String auditSql = "audit.sql";

    private String orm = "META-INF/spring-orm.xml";

    private String databasePlatform;

    private String adminPassword;

    private CipherAlgorithm adminCipherAlgorithm = CipherAlgorithm.SHA512;

    public String getDomainName() {
        return domainName;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public String getJdbcURL() {
        return jdbcURL;
    }

    public String getDbSchema() {
        return dbSchema;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getTransactionIsolation() {
        return transactionIsolation;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public String getAuditSql() {
        return auditSql;
    }

    public String getOrm() {
        return orm;
    }

    public String getDatabasePlatform() {
        return databasePlatform;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public CipherAlgorithm getAdminCipherAlgorithm() {
        return adminCipherAlgorithm;
    }
}
