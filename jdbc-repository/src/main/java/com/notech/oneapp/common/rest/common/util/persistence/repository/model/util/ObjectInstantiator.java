package com.notech.oneapp.common.rest.common.util.persistence.repository.model.util;

public interface ObjectInstantiator {

    <T> T newInstance(Class<T> c) throws Exception;

}
