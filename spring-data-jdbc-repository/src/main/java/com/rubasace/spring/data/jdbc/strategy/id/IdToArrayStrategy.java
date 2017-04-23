package com.rubasace.spring.data.jdbc.strategy.id;

public interface IdToArrayStrategy {

    Object[] getIdArray(Object id);
}
