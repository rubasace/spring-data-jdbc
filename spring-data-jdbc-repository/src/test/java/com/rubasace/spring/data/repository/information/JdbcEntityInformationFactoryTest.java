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

import com.rubasace.spring.data.jdbc.fixtures.CommentWithUser;
import com.rubasace.spring.data.repository.model.JdbcPersistable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class JdbcEntityInformationFactoryTest {

    private final JdbcEntityInformationFactory factory = new JdbcEntityInformationFactory();

    @Test
    public void shouldReturnPersistableEntityInformation_WhenImplementsJdbcPersistable() {
        AbstractJdbcEntityInformation entityInformation = factory.getEntityInformation(PersistableEntity.class);
        assertTrue(entityInformation instanceof JdbcPersistableEntityInformation);
    }

    @Test
    public void shouldReturnPersistableEntityInformation_WhenDoesNotImplementJdbcPersistable() {
        AbstractJdbcEntityInformation entityInformation = factory.getEntityInformation(CommentWithUser.class);
        assertTrue(entityInformation instanceof JdbcReflectionEntityInformation);
    }

    private static class PersistableEntity implements JdbcPersistable {

        @Override
        public boolean isNew() {
            return false;
        }

        @Override
        public void setNew(final boolean isNew) {

        }
    }
}