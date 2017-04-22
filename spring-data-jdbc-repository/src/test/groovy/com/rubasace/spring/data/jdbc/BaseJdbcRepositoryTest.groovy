/*
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
package com.rubasace.spring.data.jdbc

import com.rubasace.spring.data.jdbc.fixtures.Comment
import com.rubasace.spring.data.jdbc.fixtures.CommentRepository
import com.rubasace.spring.data.jdbc.fixtures.User
import com.rubasace.spring.data.jdbc.fixtures.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class BaseJdbcRepositoryTest extends Specification {

    def 'constructor: creates correct default EntityInformation for #desc'() {
        setup:
        def entityInfo = fixtureRepo.newInstance().entityInfo
        expect:
        entityInfo.javaType == entityType
        entityInfo.idType == idType
        where:
        fixtureRepo       || entityType | idType  || desc
        UserRepository    || User       | String  || 'Persistable entity type'
        CommentRepository || Comment    | Integer || 'non-Persistable entity type'
    }
}