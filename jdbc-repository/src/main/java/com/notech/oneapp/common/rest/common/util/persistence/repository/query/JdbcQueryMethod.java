package com.notech.oneapp.common.rest.common.util.persistence.repository.query;

import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;

import java.lang.reflect.Method;

/**
 * Convenience class for future needs
 *
 * @author Ruben
 */
public class JdbcQueryMethod extends QueryMethod {

    public JdbcQueryMethod(Method method, RepositoryMetadata metadata) {
        super(method, metadata);
    }

}
