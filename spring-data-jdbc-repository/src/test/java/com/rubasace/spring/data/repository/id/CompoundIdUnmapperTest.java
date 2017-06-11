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

import com.rubasace.spring.data.repository.model.CompoundKey;
import com.rubasace.spring.data.repository.model.CompoundKeyTestEntity;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CompoundIdUnmapperTest {

    public static final long ID1_VALUE = 23l;
    public static final long ID2_VALUE = 43l;
    private Field id1Field;
    private Field id2Field;

    @Before
    public void setup() throws NoSuchFieldException {
        id1Field = CompoundKeyTestEntity.class.getDeclaredField("id1");
        id2Field = CompoundKeyTestEntity.class.getDeclaredField("id2");
    }

    @Test
    public void shouldUnmapFieldsInOrder_1() {
        List<Field> idFields = Arrays.asList(id1Field, id2Field);
        CompoundIdUnmapper compoundIdUnmapper = new CompoundIdUnmapper(CompoundKey.class, idFields);
        CompoundKey key = new CompoundKey(ID1_VALUE, ID2_VALUE);
        Object[] idValues = compoundIdUnmapper.getIdValues(key);
        assertThat(idValues.length, is(idFields.size()));
        assertThat(idValues[0], is(ID1_VALUE));
        assertThat(idValues[1], is(ID2_VALUE));
    }

    @Test
    public void shouldUnmapFieldsInOrder_2() {
        List<Field> idFields = Arrays.asList(id2Field, id1Field);
        CompoundIdUnmapper compoundIdUnmapper = new CompoundIdUnmapper(CompoundKey.class, idFields);
        CompoundKey key = new CompoundKey(ID1_VALUE, ID2_VALUE);
        Object[] idValues = compoundIdUnmapper.getIdValues(key);
        assertThat(idValues.length, is(idFields.size()));
        assertThat(idValues[0], is(ID2_VALUE));
        assertThat(idValues[1], is(ID1_VALUE));
    }
}
