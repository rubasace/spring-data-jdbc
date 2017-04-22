package com.rubasace.spring.data.jdbc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

public class ExtendableJdbcRepository<T, ID extends Serializable> extends ReflectionJdbcRepository<T, ID> {

    public ExtendableJdbcRepository(Class<T> entityClass) {
        super(entityClass);
    }

    protected boolean exists(String whereClause, Object... args) {
        return count(whereClause, args) > 0;
    }

    public long count(String whereClause, Object... args) {
        return getJdbcOperations().queryForObject(sqlGenerator.count(getTableDesc(), whereClause),
                                                  Long.class, args);
    }

    protected void deleteByClause(String whereClause, Object... args) {
        getJdbcOperations().update(sqlGenerator.deleteByWhereClause(getTableDesc(), whereClause), args);
    }

    // TODO revisar excepciones
    protected T findOneByClause(String whereClause, Object... args) throws SQLException {
        List<T> list = findAllByClause(whereClause, args);
        if (list.size() != 1) {
            throw new SQLException("Found more than one result");
        }
        return list.get(0);
    }

    protected List<T> findAllByClause(String whereClause, Object... args) {
        return getJdbcOperations().query(sqlGenerator.selectAll(getTableDesc(), whereClause), rowMapper,
                                         args);
    }

    protected List<T> findAllByClause(Sort sort, String whereClause, Object... args) {
        return getJdbcOperations().query(sqlGenerator.selectAll(getTableDesc(), whereClause, sort),
                                         rowMapper, args);
    }

    protected Page<T> findAllByClause(Pageable page, String whereClause, Object... args) {
        String query = sqlGenerator.selectAll(getTableDesc(), whereClause, page);
        return new PageImpl<T>(getJdbcOperations().query(query, rowMapper, args), page,
                               count(whereClause, args));
    }
}
