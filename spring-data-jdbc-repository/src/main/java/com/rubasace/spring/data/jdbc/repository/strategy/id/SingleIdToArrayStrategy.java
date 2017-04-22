package com.rubasace.spring.data.jdbc.repository.strategy.id;

import com.rubasace.spring.data.jdbc.internal.ObjectUtils;

class SingleIdToArrayStrategy implements IdToArrayStrategy {

    @Override
    public Object[] getIdArray(Object id) {
        return ObjectUtils.wrapToArray(id);
    }

}
