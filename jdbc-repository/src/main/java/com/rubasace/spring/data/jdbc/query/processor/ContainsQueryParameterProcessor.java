package com.rubasace.spring.data.jdbc.query.processor;

public class ContainsQueryParameterProcessor implements QueryParameterProcessor {

    @Override
    public Object processParameter(Object parameter) {
        return "%" + (String) parameter + "%";
    }

}
