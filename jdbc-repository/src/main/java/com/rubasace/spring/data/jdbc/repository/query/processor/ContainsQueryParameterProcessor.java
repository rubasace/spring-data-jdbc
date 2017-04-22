package com.rubasace.spring.data.jdbc.repository.query.processor;

public class ContainsQueryParameterProcessor implements QueryParameterProcessor {

    @Override
    public Object processParameter(Object parameter) {
        return "%" + (String) parameter + "%";
    }

}
