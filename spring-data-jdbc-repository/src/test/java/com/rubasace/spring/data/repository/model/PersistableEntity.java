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

package com.rubasace.spring.data.repository.model;

import org.springframework.data.domain.Persistable;

public class PersistableEntity implements Persistable<Long> {

    private Long longId;

    private boolean important;

    private Boolean valid;

    private String name;

    public boolean isImportant() {
        return important;
    }

    public void setImportant(final boolean important) {
        this.important = important;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(final Boolean valid) {
        this.valid = valid;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public Long getId() {
        return getLongId();
    }

    public Long getLongId() {
        return longId;
    }

    public void setLongId(final Long longId) {
        this.longId = longId;
    }

    @Override
    public boolean isNew() {
        return longId == null;
    }
}
