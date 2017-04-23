package com.rubasace.spring.data.jdbc.query.lookup;

import com.rubasace.spring.data.jdbc.query.JdbcQueryMethod;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.data.repository.query.parser.PartTree;

class LookupStrategyFactory {

    private LookupStrategyFactory() {

    }

    static LookupStrategy chooseStrategy(PartTree tree, JdbcQueryMethod method) {
        if (tree.isCountProjection()) {
            return LookupStrategy.COUNT;
        }
        if (method.isCollectionQuery()) {
            return LookupStrategy.COLLECTION_QUERY;
        }
        if (method.isQueryForEntity()) {
            return LookupStrategy.SINGLE_QUERY;
        }
        if (method.isPageQuery()) {
            return LookupStrategy.PAGE_QUERY;
        }
        if (tree.isDelete()) {
            return LookupStrategy.UPDATE_QUERY;
        }
        Class<?> returnedClass = method.getReturnedObjectType();
        if (returnedClass.isPrimitive()) {
            returnedClass = ClassUtils.primitiveToWrapper(returnedClass);
        }
        //TODO check newer versions of Spring as they may have implemented it on PartTree level
        if (returnedClass.equals(Boolean.class)) {
            return LookupStrategy.EXISTS_QUERY;
        }
        throw new IllegalArgumentException("Don't know what lookupStrategy to follow!!");
    }
}
