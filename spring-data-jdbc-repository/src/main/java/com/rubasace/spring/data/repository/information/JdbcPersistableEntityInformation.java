/*
 *
 *  * Copyright (C) 2017 Ruben Pahino Verdugo <ruben.pahino.verdugo@gmail.com>
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.rubasace.spring.data.repository.information;

import com.rubasace.spring.data.repository.EntityType;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

public class JdbcPersistableEntityInformation<T extends Persistable, ID extends Serializable>
        extends AbstractJdbcEntityInformation<T, ID> {

    public JdbcPersistableEntityInformation(Class<T> domainClass) {
        super(domainClass);
    }

    @Override
    protected EntityType calculateEntityType(final Class<T> domainClass, final List<Field> idFields) {
        return null;
    }

    @Override
    public boolean isNew(T entity) {
        return entity.isNew();
    }


}
