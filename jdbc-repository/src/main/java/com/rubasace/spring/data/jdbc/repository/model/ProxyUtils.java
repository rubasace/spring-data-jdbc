package com.rubasace.spring.data.jdbc.repository.model;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.ProxyFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ProxyUtils {

    @SuppressWarnings("unchecked")
    public static <T> T getProxiedEntity(T original) {
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(original.getClass());
        factory.setInterfaces(new Class[]{JdbcEntity.class});
        factory.setFilter(new MethodFilter() {

            @Override
            public boolean isHandled(Method method) {
                return Modifier.isAbstract(method.getModifiers());
            }
        });

        try {
            T proxy = (T) factory.create(null, null, new JdbcEntityProxyHandler(original));
            //TODO maybe be careful with attributes called handler?
            BeanUtils.copyProperties(original, proxy);
            return proxy;
        } catch (NoSuchMethodException | IllegalArgumentException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }
}
