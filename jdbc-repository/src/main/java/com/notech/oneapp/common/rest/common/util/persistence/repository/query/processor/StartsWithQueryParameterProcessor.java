package com.notech.oneapp.common.rest.common.util.persistence.repository.query.processor;

public class StartsWithQueryParameterProcessor implements QueryParameterProcessor {

    @Override
    public Object processParameter(Object parameter) {
        return (String) parameter + "%";
    }

}
