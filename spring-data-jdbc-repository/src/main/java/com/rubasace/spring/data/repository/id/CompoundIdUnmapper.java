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

package com.rubasace.spring.data.repository.id;

import com.rubasace.spring.data.repository.util.ReflectionMethodsUtils;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CompoundIdUnmapper<ID extends Serializable> implements IdUnmapper<ID> {

    private final List<Method> gettersList;

    public CompoundIdUnmapper(final Class<ID> idClass, final List<Field> idFields) {
        this.gettersList = generateGettersList(idClass, idFields);
    }

    private List<Method> generateGettersList(final Class<ID> idClass, final List<Field> idFields) {
        List<Method> list = new ArrayList<>();
        for (Field idField : idFields) {
            String idFieldName = idField.getName();
            list.add(ReflectionMethodsUtils.findGetterMethod(idFieldName, idClass));
        }
        return list;
    }

    @Override
    public Object[] getIdValues(final ID id) {
        int size = gettersList.size();
        Object[] ids = new Object[size];
        for (int i = 0; i < size; i++) {
            ids[i] = ReflectionUtils.invokeMethod(gettersList.get(i), id);
        }
        return ids;
    }
}
