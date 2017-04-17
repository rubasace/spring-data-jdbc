package com.notech.oneapp.common.rest.common.util.persistence.repository.model;

import javassist.util.proxy.MethodHandler;

import java.lang.reflect.Method;

public class JdbcEntityProxyHandler implements MethodHandler {

    private static final String IS_NEW = "_isNew";
    private static final String SET_NEW = "_setNew";

    private boolean isNew;

    {
        isNew = true;
    }

    public JdbcEntityProxyHandler(Object original) {
        super();
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        switch (thisMethod.getName()) {
            case IS_NEW:
                return isNew;
            case SET_NEW:
                isNew = (boolean) args[0];
                return null;
            default:
                throw new NoSuchMethodException("Don't know how to handle given method");
        }
    }

}
