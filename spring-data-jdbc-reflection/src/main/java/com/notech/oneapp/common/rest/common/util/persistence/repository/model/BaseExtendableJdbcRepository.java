package com.notech.oneapp.common.rest.common.util.persistence.repository.model;

import java.io.Serializable;

public abstract class BaseExtendableJdbcRepository<T, ID extends Serializable> extends BaseReflectionJdbcRepository<T, ID> {

    public BaseExtendableJdbcRepository(Class<T> entityClass) {
        super(entityClass);
    }


    //	public long count(String whereClause, Object... args) {
    //		return getJdbcOperations().queryForObject(sqlGenerator.countByWherePart(getTableDesc(), whereClause),
    //				Long.class, args);
    //	}
    //
    //	protected boolean exists(String whereClause, Object... args) {
    //		return count(whereClause, args) > 0;
    //	}
    //
    //	protected void deleteByClause(String whereClause, Object... args) {
    //		getJdbcOperations().update(sqlGenerator.deleteByWherePart(getTableDesc(), whereClause), args);
    //	}
    //
    //	// TODO revisar excepciones
    //	protected T findOneByClause(String wherePart, Object... args) throws SQLException {
    //		List<T> list = findAllByWherePart(wherePart, args);
    //		if (list.size() != 1) {
    //			throw new SQLException("Found more than one result");
    //		}
    //		return list.get(0);
    //	}
    //
    //	protected List<T> findAllByWherePart(String wherePart, Object... args) {
    //		return getJdbcOperations().query(sqlGenerator.selectByWherePart(getTableDesc(), wherePart), rowMapper, args);
    //	}
    //
    //	protected List<T> findAllByClause(Sort sort, String whereClause, Object... args) {
    //		return getJdbcOperations().query(sqlGenerator.selectAll(getTableDesc(), whereClause, sort), rowMapper, args);
    //	}
    //
    //	protected Page<T> findAllByClause(Pageable page, String whereClause, Object... args) {
    //		String query = sqlGenerator.selectAll(getTableDesc(), whereClause, page);
    //		return new PageImpl<T>(getJdbcOperations().query(query, rowMapper, args), page, count(whereClause, args));
    //	}
}
