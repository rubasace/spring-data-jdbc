package com.rubasace.spring.data.repository.strategy.id;

import com.rubasace.spring.data.repository.internal.ObjectUtils;

class SingleIdToArrayStrategy implements IdToArrayStrategy {

    @Override
    public Object[] getIdArray(Object id) {
        return ObjectUtils.wrapToArray(id);
    }

}
