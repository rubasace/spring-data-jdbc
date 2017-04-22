package com.rubasace.spring.data.jdbc.model.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ObjectInstantiatorDefault implements ObjectInstantiator {
    final Object unsafe;

    final Method allocateInstance;

    {
        try {
            Class<?> unsafeClass;
            unsafeClass = Class.forName("sun.misc.Unsafe");
            Field f = unsafeClass.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = f.get(null);
            allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException
                | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T newInstance(Class<T> c) throws Exception {
        return (T) allocateInstance.invoke(unsafe, c);
    }

}
