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

package com.rubasace.spring.data.repository.util;

import com.rubasace.spring.data.repository.model.TestEntity;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ReflectionMethodsUtilsTest {

    @Test
    public void shouldFindNormalGetter() throws NoSuchFieldException, NoSuchMethodException {
        Field nameField = TestEntity.class.getDeclaredField("name");
        Method calculatedGetter = ReflectionMethodsUtils.findGetterMethod(nameField, TestEntity.class);
        Method expectedGetter = TestEntity.class.getMethod("getName");
        assertThat(calculatedGetter, is(expectedGetter));
    }

    @Test
    public void shouldFindIsGetterForPrimitiveBoolean() throws NoSuchFieldException, NoSuchMethodException {
        Field nameField = BooleansHolder.class.getDeclaredField("primitiveIs");
        Method calculatedGetter = ReflectionMethodsUtils.findGetterMethod(nameField, BooleansHolder.class);
        Method expectedGetter = BooleansHolder.class.getMethod("isPrimitiveIs");
        assertThat(calculatedGetter, is(expectedGetter));
    }

    @Test
    public void shouldFindGetGetterForPrimitiveBoolean() throws NoSuchFieldException, NoSuchMethodException {
        Field nameField = BooleansHolder.class.getDeclaredField("primitiveGet");
        Method calculatedGetter = ReflectionMethodsUtils.findGetterMethod(nameField, BooleansHolder.class);
        Method expectedGetter = BooleansHolder.class.getMethod("getPrimitiveGet");
        assertThat(calculatedGetter, is(expectedGetter));
    }

    @Test
    public void shouldFindIsGetterForObjectBoolean() throws NoSuchFieldException, NoSuchMethodException {
        Field nameField = BooleansHolder.class.getDeclaredField("objectIs");
        Method calculatedGetter = ReflectionMethodsUtils.findGetterMethod(nameField, BooleansHolder.class);
        Method expectedGetter = BooleansHolder.class.getMethod("isObjectIs");
        assertThat(calculatedGetter, is(expectedGetter));
    }

    @Test
    public void shouldFindGetGetterForObjectBoolean() throws NoSuchFieldException, NoSuchMethodException {
        Field nameField = BooleansHolder.class.getDeclaredField("objectGet");
        Method calculatedGetter = ReflectionMethodsUtils.findGetterMethod(nameField, BooleansHolder.class);
        Method expectedGetter = BooleansHolder.class.getMethod("getObjectGet");
        assertThat(calculatedGetter, is(expectedGetter));
    }

    @Test
    public void shouldFindSetterPrimitive() throws NoSuchFieldException, NoSuchMethodException {
        Field nameField = BooleansHolder.class.getDeclaredField("primitiveGet");
        Method calculatedGetter = ReflectionMethodsUtils.findSetterMethod(nameField, BooleansHolder.class);
        Method expectedGetter = BooleansHolder.class.getMethod("setPrimitiveGet", boolean.class);
        assertThat(calculatedGetter, is(expectedGetter));
    }

    @Test
    public void shouldFindSetterObject() throws NoSuchFieldException, NoSuchMethodException {
        Field nameField = BooleansHolder.class.getDeclaredField("objectGet");
        Method calculatedGetter = ReflectionMethodsUtils.findSetterMethod(nameField, BooleansHolder.class);
        Method expectedGetter = BooleansHolder.class.getMethod("setObjectGet", Boolean.class);
        assertThat(calculatedGetter, is(expectedGetter));
    }

    private static class BooleansHolder {
        private boolean primitiveIs;
        private boolean primitiveGet;
        private Boolean objectIs;
        private Boolean objectGet;

        public boolean isPrimitiveIs() {
            return primitiveIs;
        }

        public boolean getPrimitiveGet() {
            return primitiveGet;
        }

        public void setPrimitiveGet(final boolean primitiveGet) {
            this.primitiveGet = primitiveGet;
        }

        public Boolean isObjectIs() {
            return objectIs;
        }

        public Boolean getObjectGet() {
            return objectGet;
        }

        public void setObjectGet(final Boolean objectGet) {
            this.objectGet = objectGet;
        }
    }
}
