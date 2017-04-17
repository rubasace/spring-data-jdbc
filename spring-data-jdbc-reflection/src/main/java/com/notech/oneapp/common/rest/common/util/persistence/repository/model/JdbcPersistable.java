package com.notech.oneapp.common.rest.common.util.persistence.repository.model;

import java.io.Serializable;

public interface JdbcPersistable extends Serializable {

    boolean isNew();

    void setNew(boolean isNew);

}
