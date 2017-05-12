package com.rubasace.spring.data.repository.model.util;

import java.lang.reflect.InvocationTargetException;

public interface ObjectInstantiator {

    <T> T newInstance(Class<T> c) throws InstantiationException, InvocationTargetException, IllegalAccessException;

}
