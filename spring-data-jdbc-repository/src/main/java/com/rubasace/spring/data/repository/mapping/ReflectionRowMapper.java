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

package com.rubasace.spring.data.repository.mapping;

import com.rubasace.spring.data.repository.model.JdbcPersistable;
import com.rubasace.spring.data.repository.model.util.ObjectInstantiator;
import com.rubasace.spring.data.repository.model.util.ObjectInstantiatorDefault;
import com.rubasace.spring.data.repository.util.MethodsUtils;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

//TODO implement Strategy and clean up classes
public class ReflectionRowMapper<T> implements RowMapper<T> {

    private final Map<String, Method> methodsMap;
    private final ObjectInstantiator objectInstantiator = new ObjectInstantiatorDefault();
    private Class<? extends T> entityClass;

    //TODO revisar exception
    public ReflectionRowMapper(Class<? extends T> objectClass) {
        super();
        this.entityClass = objectClass;
        methodsMap = SettersMapper.createSettersMapForDatabase(objectClass);
    }

    //TODO revisar excepciones, add logs, etc
    @Override
    @SuppressWarnings("rawtypes")
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            //TODO change for proper library (ask nestor)
            T entity = objectInstantiator.newInstance(entityClass);
            Object row;
            for (String columnName : methodsMap.keySet()) {
                // TODO ejecutar el get correspondiente a la clase para evitar
                // problemas
                Class<?> type = methodsMap.get(columnName).getParameterTypes()[0];
                row = MethodsUtils.getResultSetGetMethod(type).invoke(rs, columnName);
                if (!rs.wasNull()) {
                    methodsMap.get(columnName).invoke(entity, row);
                }
            }
            if (entity instanceof JdbcPersistable) {
                ((JdbcPersistable) entity).setNew(false);
            }
            return entity;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | InstantiationException e) {
            throw new SQLException(e);
        }
    }
}
