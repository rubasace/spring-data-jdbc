package com.notech.oneapp.common.rest.common.util.persistence.repository.model;

public interface JdbcEntity {

    void _setNew(boolean persisted);

    boolean _isNew();
}
