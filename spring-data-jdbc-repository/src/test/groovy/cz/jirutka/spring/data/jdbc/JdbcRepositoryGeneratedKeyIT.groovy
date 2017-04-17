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
package cz.jirutka.spring.data.jdbc

import cz.jirutka.spring.data.jdbc.fixtures.Comment
import cz.jirutka.spring.data.jdbc.fixtures.CommentRepository
import cz.jirutka.spring.data.jdbc.fixtures.User
import cz.jirutka.spring.data.jdbc.fixtures.UserRepository
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import spock.lang.Unroll

import javax.annotation.Resource

@Unroll
@Transactional
abstract class JdbcRepositoryGeneratedKeyIT extends Specification {

    @Resource
    CommentRepository repository
    @Resource
    UserRepository userRepository

    final someUser = 'some_user'


    def setup() {
        userRepository.save(new User(someUser, new Date(), -1, false))
    }


    def '#method(T): generates key'() {
        given:
        def comment = createComment()
        when:
        repository./$method/(comment)
        then:
        comment.id != null
        where:
        method << ['save', 'insert']
    }

    def '#method(T): generates subsequent ids'() {
        given:
        def first = createComment()
        def second = createComment()
        when:
        repository./$method/(first)
        repository./$method/(second)
        then:
        first.id < second.id
        where:
        method << ['save', 'insert']
    }

    def '#method(T): updates the record when already exists'() {
        given:
        def oldDate = new Date(100000000)
        def newDate = new Date(200000000)
        and:
        def comment = repository.save(new Comment(null, someUser, 'Some content', oldDate, 0))
        def modifiedComment = new Comment(comment.id, someUser, 'New content', newDate, 1)
        when:
        def updatedComment = repository./$method/(modifiedComment)
        then:
        repository.count() == 1
        updatedComment == modifiedComment
        where:
        method << ['save', 'update']
    }


    private createComment() {
        new Comment(null, someUser, 'Some content', new Date(), 0)
    }
}
