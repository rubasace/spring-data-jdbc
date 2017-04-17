package com.notech.oneapp.common.rest.common.util.persistence.util;

import com.notech.oneapp.common.rest.common.util.persistence.EntityType;
import com.notech.oneapp.common.rest.common.util.persistence.repository.model.JdbcEntityInformation;
import com.notech.oneapp.common.rest.common.util.persistence.repository.model.JdbcPersistable;
import com.notech.oneapp.common.rest.common.util.persistence.repository.model.JdbcPersistableEntityInformation;
import com.notech.oneapp.common.rest.common.util.persistence.repository.model.JdbcReflectionEntityInformation;
import cz.jirutka.spring.data.jdbc.TableDescription;
import org.springframework.data.repository.core.EntityInformation;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class EntityUtils {

    private static final String DEFAULT_SELECT_CLAUSE = "*";

    private static Map<Class<?>, JdbcEntityInformation> entityInformationCache;

    static {
        // TODO use guava for synchronized?
        entityInformationCache = new WeakHashMap<>();
    }

    public static TableDescription getTableDescription(Class<?> entityClass) {
        return getTableDescription(entityClass, null);
    }

    public static TableDescription getTableDescription(Class<?> entityClass, String selectClause) {
        selectClause = selectClause != null ? selectClause : DEFAULT_SELECT_CLAUSE;
        String tableName = getTableName(entityClass);
        return new TableDescription(tableName, selectClause, tableName, getIdColumnNames(entityClass));
    }

    // TODO use annotations
    public static String getTableName(Class<?> entityClass) {
        return SQLJavaNamingUtils.getTableNameFromJavaClass(entityClass.getSimpleName());
    }

    // TODO use annotations and have in count multiple Ids
    public static List<String> getIdColumnNames(Class<?> entityClass) {
        try {
            List<Field> pkFields = getPkFields(entityClass);
            List<String> pkColumns = new ArrayList<>(pkFields.size());
            for (Field field : pkFields) {
                pkColumns.add(getAttributeName(field));
            }
            return pkColumns;
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<Field> getPkFields(Class<T> entityClass)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        return getEntityInformation(entityClass).getIdFields();
    }

    public static String getAttributeName(Field field) {
        return SQLJavaNamingUtils.geColumnNameFromAttributeName(field.getName());
    }

    @SuppressWarnings("unchecked")
    public static <T, ID extends Serializable> JdbcEntityInformation<T, ID> getEntityInformation(Class<T> entityClass) {
        if (entityClass == null) {
            return null;
        }
        if (entityInformationCache.containsKey(entityClass)) {
            return entityInformationCache.get(entityClass);
        }
        @SuppressWarnings("rawtypes")
        JdbcEntityInformation entityInfo;
        if (JdbcPersistable.class.isAssignableFrom(entityClass)) {
            entityInfo = new JdbcPersistableEntityInformation<>((Class<JdbcPersistable>) entityClass);
        } else {
            entityInfo = new JdbcReflectionEntityInformation<>(entityClass);
        }
        entityInformationCache.put(entityClass, entityInfo);
        return entityInfo;
    }

    public static <T, ID extends Serializable> EntityType getEntityType(EntityInformation<T, ID> information) {
        if (information instanceof JdbcEntityInformation) {
            return ((JdbcEntityInformation<T, ID>) information).getEntityType();
        }
        throw new RuntimeException("Don't know how to retrieve entity type");
    }
}
