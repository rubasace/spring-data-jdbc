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

import com.rubasace.spring.data.repository.model.CompoundKey;
import com.rubasace.spring.data.repository.model.CompoundKeyTestEntity;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@PerfTest(threads = 4,
          warmUp = 2,
          invocations = 1000)
@RunWith(MockitoJUnitRunner.class)
public class IdMapperTest {

    public static final long ID_1 = 214l;
    public static final long ID_2 = 413l;
    @Rule
    public ContiPerfRule rule = new ContiPerfRule();

    @Test
    public void shouldMapIdClassToPojo() {
        IdMapper<CompoundKeyTestEntity, CompoundKey> idMapper = new IdMapper(CompoundKeyTestEntity.class, CompoundKey.class);
        CompoundKeyTestEntity entity = new CompoundKeyTestEntity();
        entity.setId1(ID_1);
        entity.setId2(ID_2);
        CompoundKey key = idMapper.mapObject(entity);
        assertThat(key.getId1(), is(ID_1));
        assertThat(key.getId2(), is(ID_2));
    }
}