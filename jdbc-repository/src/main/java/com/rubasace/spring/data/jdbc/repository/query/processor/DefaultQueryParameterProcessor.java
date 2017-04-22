package com.rubasace.spring.data.jdbc.repository.query.processor;

public class DefaultQueryParameterProcessor implements QueryParameterProcessor {

    @Override
    public Object processParameter(Object parameter) {
        return parameter;
    }

}
