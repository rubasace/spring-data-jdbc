package com.rubasace.spring.data.jdbc.repository.query.processor;

public class EndsWithQueryParameterProcessor implements QueryParameterProcessor {

    @Override
    public Object processParameter(Object parameter) {
        return "%" + (String) parameter;
    }

}
