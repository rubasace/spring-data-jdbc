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

import com.rubasace.spring.data.repository.EntityType;
import com.rubasace.spring.data.repository.annotation.IdClass;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.core.support.AbstractEntityInformation;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

//TODO no necesario @Id si es persistable?? tiene sentido findOne?
public abstract class AbstractJdbcEntityInformation<T, ID extends Serializable> extends AbstractEntityInformation<T, ID> {

    private static final Class<Id> ID_ANNOTATION = Id.class;

    private static final Class<IdClass> ID_CLASS_ANNOTATION = IdClass.class;

    protected final EntityType entityType;
    protected final Class<T> entityClass;
    protected final Class<ID> idType;
    private final GetIdStrategy getIdStrategy;
    private final String className;
    protected List<Field> idFields = new ArrayList<Field>();

    @SuppressWarnings("unchecked")
    public AbstractJdbcEntityInformation(Class<T> domainClass) {
        super(domainClass);
        this.entityClass = domainClass;
        className = domainClass.getName();

        ReflectionUtils.doWithFields(domainClass, new FieldCallback() {
            public void doWith(Field field) {
                if (field.isAnnotationPresent(ID_ANNOTATION)) {
                    AbstractJdbcEntityInformation.this.idFields.add(field);
                    return;
                }
            }
        });

        if (domainClass.isAnnotationPresent(ID_CLASS_ANNOTATION)) {
            idType = (Class<ID>) domainClass.getAnnotation(ID_CLASS_ANNOTATION).value();
            Assert.notNull(idType, "@IdClass must have a valid class");
            getIdStrategy = new GetIdCompound();
        } else {
            assertUniqueId(
                    "There must be one and only one field annotated with @Id unless you annotate the class with @IdClass");
            idType = (Class<ID>) idFields.get(0).getType();
            idFields.get(0).setAccessible(true);
            getIdStrategy = new GetIdSingleKey();
        }
        entityType = calculateEntityType(domainClass, idFields);
    }

    protected void assertUniqueId(String message) {
        if (idFields.size() == 0) {
            throw new IllegalArgumentException("Couldn't find any field annotated with @Id in class " + className);
        }
        if (idFields.size() != 1) {
            throw new IllegalArgumentException(message);
        }
    }

    protected abstract EntityType calculateEntityType(final Class<T> domainClass, final List<Field> idFields);

    public List<Field> getIdFields() {
        return idFields;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ID getId(T entity) {
        return (ID) getIdStrategy.getId(entity);
    }

    @Override
    public Class<ID> getIdType() {
        return idType;
    }

    public boolean isCompoundKey() {
        return idFields.size() > 1;
    }

    private interface GetIdStrategy {
        Object getId(Object entity);
    }

    private class GetIdSingleKey implements GetIdStrategy {

        @Override
        public Object getId(Object entity) {
            try {
                return AbstractJdbcEntityInformation.this.idFields.get(0).get(entity);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class GetIdCompound implements GetIdStrategy {

        private final IdMapper<T, ID> idMapper;

        private GetIdCompound() {
            this.idMapper = new IdMapper<>(entityClass, idType);
        }

        @Override
        public Object getId(Object entity) {
            return idMapper.mapObject((T) entity);
        }
    }
}
