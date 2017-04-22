package com.rubasace.spring.data.jdbc.repository.model;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.io.Serializable;
import java.lang.reflect.Method;


public class JdbcEntityProxyHandler implements MethodInterceptor, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2669533440160139021L;

    private static final String IS_NEW = "_isNew";
    private static final String SET_NEW = "_setNew";

    private boolean isNew;

    {
        isNew = true;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        switch (method.getName()) {
            case IS_NEW:
                return isNew;
            case SET_NEW:
                isNew = (boolean) args[0];
                return null;
            default:
                return proxy.invokeSuper(obj, args);
        }
    }

}
