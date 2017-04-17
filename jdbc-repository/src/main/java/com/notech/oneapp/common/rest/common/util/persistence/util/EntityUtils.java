package com.notech.oneapp.common.rest.common.util.persistence.util;

import com.notech.oneapp.common.rest.common.util.persistence.EntityType;
import com.notech.oneapp.common.rest.common.util.persistence.repository.model.JdbcEntityInformation;
import com.notech.oneapp.common.rest.common.util.persistence.repository.query.JdbcQueryMethod;
import cz.jirutka.spring.data.jdbc.TableDescription;
import org.springframework.data.repository.core.EntityInformation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class EntityUtils {

    private static final String DEFAULT_SELECT_CLAUSE = "*";

    private static Map<Class<?>, EntityInformation> entityInformationCache;

    static {
        //TODO use guava for synchronized?
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

    public static List<Field> getPkFields(Class<?> entityClass)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        return ((JdbcEntityInformation) getEntityInformation(entityClass)).getIdFields();
    }

    public static String getAttributeName(Field field) {
        return SQLJavaNamingUtils.geColumnNameFromAttributeName(field.getName());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static EntityInformation getEntityInformation(Class<?> entityClass) {
        if (entityInformationCache.containsKey(entityClass)) {
            return entityInformationCache.get(entityClass);
        }
        EntityInformation entityInfo = new JdbcEntityInformation<>(entityClass);
        entityInformationCache.put(entityClass, entityInfo);
        return entityInfo;
    }

    public static TableDescription getTableDescription(JdbcQueryMethod method) {
        Class<?> entityClass = method.getEntityInformation().getJavaType();
        return getTableDescription(entityClass, getSelectClause(method));
    }

    // TODO getColumns from the method
    public static String getSelectClause(JdbcQueryMethod method) {
        return DEFAULT_SELECT_CLAUSE;
    }

    public static EntityType getEntityType(EntityInformation information) {
        if (information instanceof JdbcEntityInformation) {
            return ((JdbcEntityInformation) information).getEntityType();
        }
        throw new RuntimeException("Don't know how to retrieve entity type");
    }
}
