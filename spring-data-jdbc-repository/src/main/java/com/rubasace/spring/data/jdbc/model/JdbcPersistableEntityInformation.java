package com.rubasace.spring.data.jdbc.model;

import java.io.Serializable;

public class JdbcPersistableEntityInformation<T extends JdbcPersistable, ID extends Serializable>
        extends JdbcEntityInformation<T, ID> {

    public JdbcPersistableEntityInformation(Class<T> domainClass) {
        super(domainClass);
    }

    @Override
    public boolean isNew(T entity) {
        return entity.isNew();
    }


}
