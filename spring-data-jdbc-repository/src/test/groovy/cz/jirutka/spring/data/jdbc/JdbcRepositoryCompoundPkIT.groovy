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
package cz.jirutka.spring.data.jdbc

import cz.jirutka.spring.data.jdbc.fixtures.BoardingPass
import cz.jirutka.spring.data.jdbc.fixtures.BoardingPassRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Order
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import spock.lang.Unroll

import javax.annotation.Resource

import static org.springframework.data.domain.Sort.Direction.ASC
import static org.springframework.data.domain.Sort.Direction.DESC

@Unroll
@Transactional
abstract class JdbcRepositoryCompoundPkIT extends Specification {

    @Resource
    BoardingPassRepository repository

    final entities = [
            new BoardingPass('FOO-100', 1, 'Smith', 'B01'),
            new BoardingPass('FOO-100', 2, 'Johnson', 'C02'),
            new BoardingPass('BAR-100', 1, 'Gordon', 'D03'),
            new BoardingPass('BAR-100', 2, 'Who', 'E04')
    ]


    def '#method(T): inserts entity with compound PK'() {
        setup:
        def entity = entities[0]
        when:
        repository./$method/(entity)
        then:
        repository.findOne(entity.id) == entity
        where:
        method << ['save', 'insert']
    }

    def '#method(T): updates entity with compound PK'() {
        setup:
        repository.save(entities[0])
        def entity = repository.save(entities[1])
        and:
        entity.passenger = 'Jameson'
        entity.seat = 'C03'
        when:
        repository./$method/(entity)
        then:
        repository.count() == 2
        repository.findOne(entity.id) == new BoardingPass('FOO-100', 2, 'Jameson', 'C03')
        where:
        method << ['save', 'update']
    }


    def 'delete(ID): deletes entity by given compound PK'() {
        setup:
        def entity = entities[0]
        repository.save(entities)
        when:
        repository.delete(entity.id)
        then:
        !repository.exists(entity.id)
        repository.count() == 3
    }

    def 'delete(T): deletes given entity with compound PK'() {
        setup:
        def entity = entities[0]
        repository.save(entities)
        when:
        repository.delete(entity)
        then:
        !repository.exists(entity.id)
        repository.count() == 3
    }


    def 'findAll(Sortable): returns sorted entities'() {
        setup:
        repository.save(entities)
        when:
        def results = repository.findAll(
                new Sort(new Order(ASC, 'flight_no'), new Order(DESC, 'seq_no')))
        then:
        results == entities.reverse()
    }

    def 'findAll(Pageable): returns paged and sorted entities'() {
        setup:
        repository.save(entities)
        when:
        def page = repository.findAll(
                new PageRequest(pageNum, 3,
                        new Sort(new Order(ASC, 'flight_no'), new Order(DESC, 'seq_no'))
                ))
        then:
        page.totalElements == 4
        page.totalPages == 2
        page.content == entities[entitiesIdx]
        where:
        pageNum || entitiesIdx
        0       || 3..1
        1       || 0..0
    }


    def 'findAll(Iterable<ID>): returns nothing when given empty list'() {
        setup:
        repository.save(entities)
        when:
        def results = repository.findAll([])
        then:
        results.asList().isEmpty()
    }

    def 'findAll(Iterable<ID>): returns one entity when given one id'() {
        setup:
        def ids = entities.collect { repository.save(it).id }
        when:
        def results = repository.findAll([ids[1]])
        then:
        results.size() == 1
        results == [entities[1]]
    }

    def 'findAll(Iterable<ID>): returns two entities when given two ids'() {
        setup:
        def ids = entities.collect { repository.save(it).id }
        when:
        def results = repository.findAll(ids[1..2])
        then:
        results.size() == 2
        results as Set == entities[1..2] as Set
    }
}
