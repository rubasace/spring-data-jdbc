package com.notech.oneapp.common.rest.common.util.persistence.mapping;

import java.util.Map;

public class MissingRowUnmapper<T> extends ReflectionRowUnmapper<T> {

    public MissingRowUnmapper(Class<T> objectClass) {
        super(objectClass);
    }

    @Override
    public Map<String, Object> mapColumns(T entity) {
        throw new UnsupportedOperationException("This repository is read-only, it can't store or update entities");
    }


}
