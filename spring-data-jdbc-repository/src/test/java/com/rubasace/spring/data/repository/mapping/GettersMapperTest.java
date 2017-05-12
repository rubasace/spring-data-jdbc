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

import com.rubasace.spring.data.repository.model.AnnotatedTestEntity;
import com.rubasace.spring.data.repository.model.TestEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class GettersMapperTest {

    @Test
    public void shouldMapSettersCorrectly() throws NoSuchMethodException {

        Map<String, Method> methods = GettersMapper.createGettersMap(TestEntity.class);
        Method getId = TestEntity.class.getDeclaredMethod("getId");
        Method isImportant = TestEntity.class.getDeclaredMethod("isImportant");
        Method getValid = TestEntity.class.getDeclaredMethod("getValid");
        Method getName = TestEntity.class.getDeclaredMethod("getName");

        assertThat(methods.keySet().size(), is(4));
        assertThat(methods.get("id"), is(getId));
        assertThat(methods.get("important"), is(isImportant));
        assertThat(methods.get("valid"), is(getValid));
        assertThat(methods.get("name"), is(getName));
    }

    @Test
    public void shouldIgnoreTransientAnnotatedFields() {

        Map<String, Method> methods = GettersMapper.createGettersMap(AnnotatedTestEntity.class);
        assertFalse(methods.keySet().contains("name"));
    }

    @Test
    public void shouldProcessFieldAnnotation() {

        Map<String, Method> methods = GettersMapper.createGettersMap(AnnotatedTestEntity.class);
        assertTrue(methods.keySet().contains("validated"));
        assertFalse(methods.keySet().contains("valid"));
    }
}
