package com.rubasace.spring.data.jdbc.query;

import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;

import java.lang.reflect.Method;

/**
 * Convenience class for future needs
 *
 * @author Ruben Pahino Verdugo
 */
public class JdbcQueryMethod extends QueryMethod {

    public JdbcQueryMethod(final Method method, final RepositoryMetadata metadata, final ProjectionFactory factory) {
        super(method, metadata, factory);
    }
}
