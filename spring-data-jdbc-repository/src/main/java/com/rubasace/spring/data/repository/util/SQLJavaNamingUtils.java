package com.rubasace.spring.data.repository.util;

import com.google.common.base.CaseFormat;

//TODO permitir inyectar como bean y cambiar los convention mediante properties
public class SQLJavaNamingUtils {

    private static final CaseFormat DATABASE_TABLE_NAMING_CONVENTION = CaseFormat.LOWER_UNDERSCORE;

    private static final CaseFormat DATABASE_ATTRIBUTE_NAMING_CONVENTION = CaseFormat.LOWER_UNDERSCORE;

    private static final CaseFormat JAVA_CLASS_NAMING_CONVENTION = CaseFormat.UPPER_CAMEL;

    private static final CaseFormat JAVA_ATTRIBUTE_NAMING_CONVENTION = CaseFormat.LOWER_CAMEL;


    public static String getTableNameFromJavaClass(String className) {
        return JAVA_CLASS_NAMING_CONVENTION.to(DATABASE_TABLE_NAMING_CONVENTION, className);
    }

    public static String getJavaClassFromTableName(String table) {
        return DATABASE_TABLE_NAMING_CONVENTION.to(JAVA_CLASS_NAMING_CONVENTION, table);
    }

    public static String geColumnNameFromAttributeName(String attributeName) {
        return JAVA_ATTRIBUTE_NAMING_CONVENTION.to(DATABASE_ATTRIBUTE_NAMING_CONVENTION, attributeName);
    }

    public static String getAttributeNameFromColumn(String columnName) {
        return DATABASE_ATTRIBUTE_NAMING_CONVENTION.to(JAVA_ATTRIBUTE_NAMING_CONVENTION, columnName);
    }


}
