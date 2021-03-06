package com.rubasace.spring.data.jdbc.support;

import com.rubasace.spring.data.repository.sql.SqlGenerator;
import org.springframework.jdbc.core.JdbcOperations;

class RepositoryData {

    private JdbcOperations jdbcOps;
    private SqlGenerator sqlGenerator;

    public RepositoryData(JdbcOperations jdbcOps, SqlGenerator sqlGenerator) {
        super();
        this.jdbcOps = jdbcOps;
        this.sqlGenerator = sqlGenerator;
    }

    public JdbcOperations getJdbcOps() {
        return jdbcOps;
    }

    public void setJdbcOps(JdbcOperations jdbcOps) {
        this.jdbcOps = jdbcOps;
    }

    public SqlGenerator getSqlGenerator() {
        return sqlGenerator;
    }

    public void setSqlGenerator(SqlGenerator sqlGenerator) {
        this.sqlGenerator = sqlGenerator;
    }


}
