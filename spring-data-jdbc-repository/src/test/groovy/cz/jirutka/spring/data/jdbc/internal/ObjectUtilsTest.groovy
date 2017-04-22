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
import spock.lang.Unroll

import static com.rubasace.spring.data.jdbc.internal.ObjectUtils.wrapToArray

@Unroll
class ObjectUtilsTest extends Specification {


    def 'wrapToArray(): returns #expected for #desc'() {
        expect:
        wrapToArray(input) == expected
        where:
        input                  | expected   || desc
        null                   | []         || 'null'
        'foo'                  | ['foo']    || 'a single object value'
        42                     | [42]       || 'a single primitive value'
        ['a', 'b'] as Object[] | ['a', 'b'] || 'an array of objects'
        [1, 2] as int[]        | [1, 2]     || 'an array of primitives'
    }
}
