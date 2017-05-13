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

import com.rubasace.spring.data.repository.annotation.Column;
import com.rubasace.spring.data.repository.util.ReflectionMethodsUtils;
import com.rubasace.spring.data.repository.util.SQLJavaNamingUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class SettersMapper {

    private SettersMapper() {

    }

    public static Map<String, Method> createSettersMap(Class<?> beanClass) {
        Map<String, Method> methodsMap = new LinkedHashMap<>();
        for (Field field : beanClass.getDeclaredFields()) {
            if (field.getAnnotation(Transient.class) != null) {
                continue;
            }
            Column column = field.getAnnotation(Column.class);
            String columnName = column != null ? column.value() : SQLJavaNamingUtils.geColumnNameFromAttributeName(field.getName());
            Method setter = ReflectionMethodsUtils.findSetterMethod(field, beanClass);
            methodsMap.put(columnName, setter);
        }
        return methodsMap;
    }
}
