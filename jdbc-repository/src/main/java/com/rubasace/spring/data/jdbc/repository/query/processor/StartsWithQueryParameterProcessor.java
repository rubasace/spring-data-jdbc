package com.rubasace.spring.data.jdbc.repository.query.processor;

public class StartsWithQueryParameterProcessor implements QueryParameterProcessor {

    @Override
    public Object processParameter(Object parameter) {
        return (String) parameter + "%";
    }

}
