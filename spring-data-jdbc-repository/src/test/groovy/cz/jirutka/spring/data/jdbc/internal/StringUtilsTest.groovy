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

import static cz.jirutka.spring.data.jdbc.internal.StringUtils.repeat

@Unroll
class StringUtilsTest extends Specification {

    def 'repeat("#str", "#sep", #count) == "#expected"'() {
        expect:
        repeat(str, sep, count) == expected
        where:
        str   | sep    | count || expected
        'x'   | ' or ' | 3     || 'x or x or x'
        'x'   | ' or ' | 1     || 'x'
        'x'   | ' or ' | 0     || ''
        'x'   | ' or ' | -2    || ''
        'pew' | ''     | 2     || 'pewpew'
        ''    | '-'    | 2     || '-'
    }
}
