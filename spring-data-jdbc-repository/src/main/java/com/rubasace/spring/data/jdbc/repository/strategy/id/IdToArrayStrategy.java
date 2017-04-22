package com.rubasace.spring.data.jdbc.repository.strategy.id;

public interface IdToArrayStrategy {

    Object[] getIdArray(Object id);
}
