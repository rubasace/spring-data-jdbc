/*
 * Copyright 2012-2014 Tomasz Nurkiewicz <nurkiewicz@gmail.com>.
 * Copyright 2016 Jakub Jirutka <jakub@jirutka.cz>.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License')
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rubasace.spring.data.jdbc

import com.rubasace.spring.data.jdbc.fixtures.CommentWithUser
import com.rubasace.spring.data.jdbc.fixtures.CommentWithUserRepository
import com.rubasace.spring.data.jdbc.fixtures.User
import com.rubasace.spring.data.jdbc.fixtures.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import spock.lang.Unroll

import javax.annotation.Resource
import java.sql.Date
import java.sql.Timestamp

import static java.util.Calendar.JANUARY
import static org.springframework.data.domain.Sort.Direction.ASC
import static org.springframework.data.domain.Sort.Direction.DESC

@Unroll
@Transactional
abstract class JdbcRepositoryManyToOneIT extends Specification {

    @Resource
    CommentWithUserRepository repository
    @Resource
    UserRepository userRepository

    final someDate = new Date(new GregorianCalendar(2013, JANUARY, 19).timeInMillis)
    final someTimestamp = new Timestamp(new GregorianCalendar(2013, JANUARY, 20).timeInMillis)
    final someUser = new User('Jimmy', someDate, -1, false)

    final entities = [
            new CommentWithUser(someUser, 'First comment', someTimestamp, 3),
            new CommentWithUser(someUser, 'Second comment', someTimestamp, 2),
            new CommentWithUser(someUser, 'Third comment', someTimestamp, 1)
    ]


    def setup() {
        userRepository.save(someUser)
    }


    def '#method(T): generates primary key'() {
        when:
        repository.save(entities[0])
        then:
        entities[0].id != null
        where:
        method << ['save', 'insert']
    }

    def '#method(T)/findOne(): inserts and returns entity with association attached'() {
        setup:
        def expected = entities[0]
        when:
        repository./$method/(expected)
        def actual = repository.findOne(expected.id)
        then:
        actual == expected
        actual.user == expected.user
        where:
        method << ['save', 'insert']
    }

    def "#method(T): updates entity's association"() {
        setup:
        def firstUser = userRepository.save(new User('First user', someDate, 10, false))
        def comment = repository.save(entities[0])
        when:
        comment.user = firstUser
        repository./$method/(comment)
        then:
        repository.count() == 1
        def result = repository.findOne(comment.id)
        result.user == firstUser
        where:
        method << ['save', 'update']
    }


    def 'findAll(Sort): returns sorted entities with the same association'() {
        setup:
        repository.save(entities)
        when:
        def actual = repository.findAll(new Sort('favourite_count'))
        then:
        actual == entities.sort { it.favouriteCount }
        actual*.user == [someUser] * 3
    }

    def 'findAll(Sort): returns sorted entities with different associations'() {
        given:
        def firstUser = userRepository.save(new User('First user', someDate, 10, false))
        def secondUser = userRepository.save(new User('Second user', someDate, 20, false))
        def thirdUser = userRepository.save(new User('Third user', someDate, 30, false))

        def first = repository.save(new CommentWithUser(firstUser, 'First comment', someTimestamp, 3))
        def second = repository.save(new CommentWithUser(secondUser, 'Second comment', someTimestamp, 2))
        def third = repository.save(new CommentWithUser(thirdUser, 'Third comment', someTimestamp, 1))
        when:
        def results = repository.findAll(new Sort(DESC, 'favourite_count'))
        then:
        results == [first, second, third]
    }

    def 'findAll(Pageable): returns paged and sorted entities with associations attached'() {
        setup:
        repository.save(entities)
        when:
        def page = repository.findAll(new PageRequest(pageNum, 2, ASC, 'favourite_count'))
        then:
        page.totalElements == 3
        page.totalPages == 2
        page.content == entities[entitiesIdx]
        page.content*.user.unique() == [someUser]
        where:
        pageNum || entitiesIdx
        0       || 2..1
        1       || 0..0
    }


    def 'delete(T): deletes an entity without deleting associated entity'() {
        setup:
        def comment = repository.save(entities[0])
        when:
        repository.delete(comment)
        then:
        repository.count() == 0
        userRepository.exists(someUser.userName)
    }


    def 'deletesAll(): deletes all entities without deleting associated entities'() {
        setup:
        repository.save(entities)
        when:
        repository.deleteAll()
        then:
        repository.count() == 0
        userRepository.exists(someUser.userName)
    }
}
