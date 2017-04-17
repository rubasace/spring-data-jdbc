package com.notech.oneapp.common.rest.common.util.persistence.repository.query.processor;

public class EndsWithQueryParameterProcessor implements QueryParameterProcessor {

    @Override
    public Object processParameter(Object parameter) {
        return "%" + (String) parameter;
    }

}
