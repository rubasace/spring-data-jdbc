package com.rubasace.spring.data.jdbc.query.processor;

public class DefaultQueryParameterProcessor implements QueryParameterProcessor {

    @Override
    public Object processParameter(Object parameter) {
        return parameter;
    }

}
