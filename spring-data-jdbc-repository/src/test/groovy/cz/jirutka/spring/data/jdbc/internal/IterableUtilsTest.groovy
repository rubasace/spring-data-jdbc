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
package cz.jirutka.spring.data.jdbc.internal

import spock.lang.Specification

import static cz.jirutka.spring.data.jdbc.internal.IterableUtils.toList

class IterableUtilsTest extends Specification {


    def 'toList(): converts given iterable into a list'() {
        given:
        def input = type.newInstance([1, 2, 3]) as Iterable
        expect:
        toList(input) == [1, 2, 3]
        where:
        type << [ArrayList, PriorityQueue, HashSet]
    }
}
