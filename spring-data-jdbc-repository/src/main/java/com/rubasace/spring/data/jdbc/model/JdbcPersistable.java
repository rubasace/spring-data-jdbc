package com.rubasace.spring.data.jdbc.model;

import java.io.Serializable;

public interface JdbcPersistable extends Serializable {

    boolean isNew();

    void setNew(boolean isNew);

}
