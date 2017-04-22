package com.rubasace.spring.data.jdbc.repository.model;

import com.rubasace.spring.data.jdbc.AutoGenerated;
import com.rubasace.spring.data.jdbc.EntityType;
import com.rubasace.spring.data.jdbc.IdClass;
import com.rubasace.spring.data.jdbc.ReadOnlyEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.core.support.AbstractEntityInformation;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class JdbcEntityInformation<T, ID extends Serializable> extends AbstractEntityInformation<T, ID> {

    private static final Class<Id> ID_ANNOTATION = Id.class;

    private static final Class<IdClass> ID_CLASS_ANNOTATION = IdClass.class;

    private static final Class<? extends Annotation> READ_ONLY_ANNOTATION = ReadOnlyEntity.class;

    private static final Class<? extends Annotation> AUTO_GENERATED_ANNOTATION = AutoGenerated.class;
    protected final EntityType entityType;
    protected final Class<ID> idType;
    private final GetIdStrategy getIdStrategy;
    protected List<Field> idFields;

    {
        idFields = new ArrayList<Field>();
    }

    @SuppressWarnings("unchecked")
    public JdbcEntityInformation(Class<T> domainClass) {

        super(domainClass);

        ReflectionUtils.doWithFields(domainClass, new FieldCallback() {
            public void doWith(Field field) {
                if (field.isAnnotationPresent(ID_ANNOTATION)) {
                    JdbcEntityInformation.this.idFields.add(field);
                    return;
                }
            }
        });

        if (domainClass.isAnnotationPresent(ID_CLASS_ANNOTATION)) {
            idType = domainClass.getAnnotation(ID_CLASS_ANNOTATION).value();
            Assert.notNull(idType, "@IdClass must have a valid class");
            getIdStrategy = new GetIdCompound();
        } else {
            assertUniqueId(
                    "There must be one and only one field annotated with @Id unless yo annotate the class with @IdClass");
            idType = (Class<ID>) idFields.get(0).getType();
            idFields.get(0).setAccessible(true);
            getIdStrategy = new GetIdSingleKey();
        }
        entityType = calculateEntityType(domainClass, idFields);

    }

    protected void assertUniqueId(String message) {
        if (idFields.size() != 1) {
            throw new RuntimeException(message);
        }
    }

    private EntityType calculateEntityType(Class<T> entityClass, List<Field> idFields) {
        if (entityClass.isAnnotationPresent(READ_ONLY_ANNOTATION)) {
            return EntityType.READ_ONLY;
        }
        if (idFields.get(0).isAnnotationPresent(AUTO_GENERATED_ANNOTATION)) {
            assertUniqueId("Only single key classes can use autogenerated primary keys");
            return EntityType.AUTO_INCREMENTAL;
        }
        return EntityType.MANUALLY_ASSIGNED;
    }

    @Override
    public boolean isNew(T entity) {
        return ((JdbcEntity) entity)._isNew();
    }

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
                return JdbcEntityInformation.this.idFields.get(0).get(entity);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private class GetIdCompound implements GetIdStrategy {

        @Override
        public Object getId(Object entity) {
            return IdMapper.mapObject(entity, idType);
        }

    }

}
