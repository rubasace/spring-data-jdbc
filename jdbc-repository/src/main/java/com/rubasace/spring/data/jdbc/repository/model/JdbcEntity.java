package com.rubasace.spring.data.jdbc.repository.model;

public interface JdbcEntity {

    void _setNew(boolean persisted);

    boolean _isNew();
}
