/*
 *
 *  * Copyright (C) 2017 Ruben Pahino Verdugo <ruben.pahino.verdugo@gmail.com>
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.rubasace.spring.data.repository.information;

import com.rubasace.spring.data.repository.mapping.SettersMapper;
import com.rubasace.spring.data.repository.model.util.ObjectInstantiator;
import com.rubasace.spring.data.repository.model.util.ObjectInstantiatorDefault;
import com.rubasace.spring.data.repository.util.ReflectionMethodsUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

class IdMapper<T, ID extends Serializable> {

    private static final ObjectInstantiator instantiator;

    static {
        instantiator = new ObjectInstantiatorDefault();
    }

    private final Class<ID> idClass;
    private final Map<String, Method> gettersMap;
    private final Map<String, Method> settersMap;

    public IdMapper(final Class<T> entityClass, final Class<ID> idClass) {
        this.idClass = idClass;
        settersMap = SettersMapper.createStandardSettersMap(idClass);
        gettersMap = generateGettersMap(entityClass);
    }

    private Map<String, Method> generateGettersMap(final Class<T> entityClass) {
        Map<String, Method> gettersMap = new LinkedHashMap<>();
        for (String fieldName : settersMap.keySet()) {
            gettersMap.put(fieldName, ReflectionMethodsUtils.findGetterMethod(fieldName, entityClass));
        }
        return gettersMap;
    }

    ID mapObject(final T entity) {
        try {
            ID key = instantiator.newInstance(idClass);
            for (String fieldName : settersMap.keySet()) {
                Object value = gettersMap.get(fieldName).invoke(entity);
                settersMap.get(fieldName).invoke(key, value);
            }
            return key;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
