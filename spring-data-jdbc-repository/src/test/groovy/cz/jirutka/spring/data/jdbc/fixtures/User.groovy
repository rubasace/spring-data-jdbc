/*
 * Copyright 2012-2014 Tomasz Nurkiewicz <nurkiewicz@gmail.com>.
 * Copyright 2016 Jakub Jirutka <jakub@jirutka.cz>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.jirutka.spring.data.jdbc.fixtures

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.data.domain.Persistable

@ToString
@EqualsAndHashCode
class User implements Persistable<String> {

    private transient boolean persisted

    String userName

    Date dateOfBirth

    int reputation

    boolean enabled


    User(String userName, Date dateOfBirth, int reputation, boolean enabled) {
        this.userName = userName
        this.dateOfBirth = dateOfBirth
        this.reputation = reputation
        this.enabled = enabled
    }


    String getId() {
        userName
    }

    boolean isNew() {
        !persisted
    }

    User withPersisted(boolean persisted) {
        this.persisted = persisted
        this
    }
}
