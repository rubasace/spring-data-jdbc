package com.rubasace.spring.data.repository.strategy.id;

import com.rubasace.spring.data.repository.information.AbstractJdbcEntityInformation;
import com.rubasace.spring.data.repository.util.ReflectionMethodsUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class CompoundIdToArrayStrategy implements IdToArrayStrategy {

    private final AbstractJdbcEntityInformation entityInformation;
    private List<Method> methods;

    CompoundIdToArrayStrategy(final AbstractJdbcEntityInformation entityInformation) {
        this.entityInformation = entityInformation;
        prepareMethods();
    }

    private void prepareMethods() {
        try {
            List<Field> idFields = entityInformation.getIdFields();
            methods = new ArrayList<Method>(idFields.size());
            for (Field field : idFields) {
                methods.add(ReflectionMethodsUtils.findGetterMethod(field, entityInformation.getIdType()));
            }
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object[] getIdArray(Object id) {
        try {
            Object[] objects = new Object[methods.size()];
            for (int i = 0; i < methods.size(); i++) {
                objects[i] = methods.get(i).invoke(id);
            }
            return objects;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
