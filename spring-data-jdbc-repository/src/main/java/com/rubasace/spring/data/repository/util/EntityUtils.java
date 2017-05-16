package com.rubasace.spring.data.repository.util;

import com.rubasace.spring.data.repository.EntityType;
import com.rubasace.spring.data.repository.TableDescription;
import com.rubasace.spring.data.repository.information.AbstractJdbcEntityInformation;
import com.rubasace.spring.data.repository.information.JdbcPersistableEntityInformation;
import com.rubasace.spring.data.repository.information.JdbcReflectionEntityInformation;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.core.EntityInformation;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class EntityUtils {

    private static final String DEFAULT_SELECT_CLAUSE = "*";

    private static Map<Class<?>, AbstractJdbcEntityInformation> entityInformationCache;

    static {
        // TODO use guava for synchronized?
        entityInformationCache = new WeakHashMap<>();
    }

    private EntityUtils() {
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

    // TODO use annotations and take in count multiple Ids
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

    //TODO add @Cacheable probably
    @SuppressWarnings("unchecked")
    public static <T, ID extends Serializable> AbstractJdbcEntityInformation getEntityInformation(Class<T> entityClass) {
        if (entityClass == null) {
            return null;
        }
        if (entityInformationCache.containsKey(entityClass)) {
            return entityInformationCache.get(entityClass);
        }
        @SuppressWarnings("rawtypes")
        AbstractJdbcEntityInformation entityInfo;
        if (Persistable.class.isAssignableFrom(entityClass)) {
            entityInfo = new JdbcPersistableEntityInformation<>((Class<Persistable>) entityClass);
        } else {
            entityInfo = new JdbcReflectionEntityInformation<>(entityClass);
        }
        entityInformationCache.put(entityClass, entityInfo);
        return entityInfo;
    }

    public static <T, ID extends Serializable> EntityType getEntityType(EntityInformation<T, ID> information) {
        if (!(information instanceof AbstractJdbcEntityInformation)) {
            throw new RuntimeException("Don't know how to retrieve entity type");
        }
        return ((AbstractJdbcEntityInformation<T, ID>) information).getEntityType();
    }
}
