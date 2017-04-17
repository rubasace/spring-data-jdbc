package com.notech.oneapp.common.rest.common.util.persistence.repository.query.processor;

import com.notech.oneapp.common.rest.common.util.persistence.repository.query.lookup.ParameterProperties;

public class QueryParameterProcessorFactory {

    private static DefaultQueryParameterProcessor defaultProcessor;
    private static ContainsQueryParameterProcessor containsProcessor;
    private static StartsWithQueryParameterProcessor startsWithProcessor;
    private static EndsWithQueryParameterProcessor endsWithProcessor;

    // have in count ignoreCase
    public static QueryParameterProcessor getQueryParameterProcessor(ParameterProperties properties) {
        if (properties.getSpecialLike() != null) {
            switch (properties.getSpecialLike()) {
                case CONTAINS:
                    return getContainsParameterProcessor();
                case ENDS_WITH:
                    return getEndsWithParameterProcessor();
                case STARTS_WITH:
                    return getStartsWithParameterProcessor();
            }
        }
        return getDefaultParameterProcessor();
    }

    private static ContainsQueryParameterProcessor getContainsParameterProcessor() {
        if (containsProcessor == null) {
            containsProcessor = new ContainsQueryParameterProcessor();
        }
        return containsProcessor;
    }

    private static EndsWithQueryParameterProcessor getEndsWithParameterProcessor() {
        if (endsWithProcessor == null) {
            endsWithProcessor = new EndsWithQueryParameterProcessor();
        }
        return endsWithProcessor;
    }

    private static StartsWithQueryParameterProcessor getStartsWithParameterProcessor() {
        if (startsWithProcessor == null) {
            startsWithProcessor = new StartsWithQueryParameterProcessor();
        }
        return startsWithProcessor;
    }

    private static DefaultQueryParameterProcessor getDefaultParameterProcessor() {
        if (defaultProcessor == null) {
            defaultProcessor = new DefaultQueryParameterProcessor();
        }
        return defaultProcessor;
    }

}
