package com.rubasace.spring.data.repository.model;

import com.rubasace.spring.data.repository.model.util.ObjectInstantiator;
import com.rubasace.spring.data.repository.model.util.ObjectInstantiatorDefault;
import org.springframework.beans.BeanUtils;

class IdMapper {

    private static final ObjectInstantiator instantiator;

    static {
        instantiator = new ObjectInstantiatorDefault();
    }

    static <T> T mapObject(Object fromObject, Class<T> toClass) {
        try {
            T object = instantiator.newInstance(toClass);
            BeanUtils.copyProperties(fromObject, object);
            return object;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
