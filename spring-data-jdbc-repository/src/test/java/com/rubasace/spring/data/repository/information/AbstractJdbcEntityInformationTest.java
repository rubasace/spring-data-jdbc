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

import com.rubasace.spring.data.jdbc.fixtures.Comment;
import com.rubasace.spring.data.repository.EntityType;
import com.rubasace.spring.data.repository.annotation.IdClass;
import org.junit.Test;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AbstractJdbcEntityInformationTest {

    @Test
    public void shouldDelegateCalculateEntityType() {
        TestInfo testInfo = new TestInfo(Comment.class);
        assertThat(testInfo.getEntityType(), is(EntityType.READ_ONLY));
    }

    @Test
    public void shouldRetrieveSingleID() {
        TestInfo<Comment, Integer> testInfo = new TestInfo<>(Comment.class);
        int id = 1;
        Comment comment = new Comment(id, "", "", null, 4);
        assertThat(testInfo.getId(comment), is(id));
    }

    @Test
    public void shouldRetrieveCompoundID() {
        TestInfo<MultipleKeyEntity, MultipleKeyEntityCompoundKey> testInfo = new TestInfo<>(MultipleKeyEntity.class);
        int id1 = 1;
        int id2 = 2;
        MultipleKeyEntity comment = new MultipleKeyEntity(id1, id2);
        MultipleKeyEntityCompoundKey id = testInfo.getId(comment);
        assertThat(id.id1, is(id1));
        assertThat(id.id2, is(id2));
    }

    @Test
    public void shouldRetrieveSingleIdField() throws NoSuchFieldException {
        TestInfo testInfo = new TestInfo(Comment.class);
        Field id = Comment.class.getDeclaredField("id");
        List<Field> expectedFields = Arrays.asList(id);
        List<Field> idFields = testInfo.getIdFields();
        assertThat(idFields.size(), is(expectedFields.size()));
        assertThat(idFields.get(0), is(expectedFields.get(0)));
    }

    @Test
    public void shouldRetrieveMultipleIdFields() throws NoSuchFieldException {
        TestInfo testInfo = new TestInfo(MultipleKeyEntity.class);
        Field id1 = MultipleKeyEntity.class.getDeclaredField("id1");
        Field id2 = MultipleKeyEntity.class.getDeclaredField("id2");
        List<Field> expectedFields = Arrays.asList(id1, id2);
        List<Field> idFields = testInfo.getIdFields();
        assertThat(idFields.size(), is(expectedFields.size()));
        for (int i = 0; i < idFields.size(); i++) {
            assertThat(idFields.get(i), is(expectedFields.get(i)));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFail_WhenMultipleIdFieldsWithoutIdClassAnnotation() throws NoSuchFieldException {
        new TestInfo(MultipleKeyEntityNoAnnotation.class);
    }

    private static class TestInfo<T, ID extends Serializable> extends AbstractJdbcEntityInformation<T, ID> {

        public TestInfo(final Class<T> domainClass) {
            super(domainClass);
        }

        @Override
        protected EntityType calculateEntityType(final Class domainClass, final List idFields) {
            return EntityType.READ_ONLY;
        }
    }

    @IdClass(MultipleKeyEntityCompoundKey.class)
    private static class MultipleKeyEntity {

        @Id
        private Integer id1;
        @Id
        private Integer id2;

        public MultipleKeyEntity(final Integer id1, final Integer id2) {
            this.id1 = id1;
            this.id2 = id2;
        }
    }

    private static class MultipleKeyEntityNoAnnotation {
        @Id
        private Integer id1;
        @Id
        private Integer id2;
    }

    private static class MultipleKeyEntityCompoundKey implements Serializable {
        private Integer id1;
        private Integer id2;
    }
}