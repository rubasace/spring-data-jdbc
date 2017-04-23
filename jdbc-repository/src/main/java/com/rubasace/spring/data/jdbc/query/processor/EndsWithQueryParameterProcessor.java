package com.rubasace.spring.data.jdbc.query.processor;

public class EndsWithQueryParameterProcessor implements QueryParameterProcessor {

    @Override
    public Object processParameter(Object parameter) {
        return "%" + (String) parameter;
    }

}
