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
import org.springframework.data.annotation.Id

@ToString
@EqualsAndHashCode
class Comment {

    @Id
    Integer id

    String userName

    String contents

    Date createdTime

    int favouriteCount


    Comment(Integer id, String userName, String contents, Date createdTime, int favouriteCount) {
        this.id = id
        this.userName = userName
        this.contents = contents
        this.createdTime = createdTime
        this.favouriteCount = favouriteCount
    }
}
