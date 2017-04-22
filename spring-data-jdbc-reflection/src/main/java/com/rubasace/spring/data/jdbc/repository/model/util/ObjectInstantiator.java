package com.rubasace.spring.data.jdbc.repository.model.util;

public interface ObjectInstantiator {

    <T> T newInstance(Class<T> c) throws Exception;

}
