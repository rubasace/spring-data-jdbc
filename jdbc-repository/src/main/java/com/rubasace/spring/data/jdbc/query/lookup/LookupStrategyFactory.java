package com.rubasace.spring.data.jdbc.query.lookup;

import com.rubasace.spring.data.jdbc.query.JdbcQueryMethod;
import org.springframework.data.repository.query.parser.PartTree;

class LookupStrategyFactory {

    private LookupStrategyFactory() {

    }

    //TODO get rid of this so now we create directly the execution implementation
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
        if (tree.isExistsProjection()) {
            return LookupStrategy.EXISTS_QUERY;
        }
        throw new IllegalArgumentException("Don't know what lookupStrategy to follow!!");
    }
}
