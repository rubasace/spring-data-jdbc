package com.notech.oneapp.common.rest.common.util.persistence.repository.model;

import java.io.Serializable;


//TODO sobra?
public class JdbcReflectionEntityInformation<T, ID extends Serializable> extends JdbcEntityInformation<T, ID> {

    public JdbcReflectionEntityInformation(Class<T> domainClass) {
        super(domainClass);
    }

}
