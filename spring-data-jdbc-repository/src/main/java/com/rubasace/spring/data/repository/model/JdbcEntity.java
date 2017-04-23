package com.rubasace.spring.data.repository.model;

public interface JdbcEntity {

    void _setNew(boolean persisted);

    boolean _isNew();
}
