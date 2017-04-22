package com.rubasace.spring.data.jdbc.model;

public interface JdbcEntity {

    void _setNew(boolean persisted);

    boolean _isNew();
}
