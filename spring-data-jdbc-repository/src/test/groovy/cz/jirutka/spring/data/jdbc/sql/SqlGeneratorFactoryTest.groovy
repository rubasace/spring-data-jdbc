///*
// * Copyright 2016 Jakub Jirutka <jakub@jirutka.cz>.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package cz.jirutka.spring.data.jdbc.sql
//
//import org.springframework.dao.DataAccessResourceFailureException
//import spock.lang.Specification
//
//import javax.sql.DataSource
//import java.sql.Connection
//import java.sql.DatabaseMetaData
//import java.sql.SQLException
//
//class SqlGeneratorFactoryTest extends Specification {
//
//    def factory = new SqlGeneratorFactory(true)
//
//    def sqlGenerator = Mock(SqlGenerator)
//    def dbMetaData = Stub(DatabaseMetaData)
//
//    def dataSource = Mock(DataSource) {
//        getConnection() >> Mock(Connection) {
//            getMetaData() >> dbMetaData
//        }
//    }
//
//
//    def 'getInstance(): returns singleton instance with registered generators'() {
//        expect:
//            SqlGeneratorFactory.getInstance() != null
//            SqlGeneratorFactory.getInstance().is(SqlGeneratorFactory.getInstance())
//            ! SqlGeneratorFactory.getInstance().@generators.isEmpty()
//    }
//
//
//    def 'getGenerator(): returns first generator that responds with true for isCompatible()'() {
//        setup:
//            def sqlGenerator2 = Mock(SqlGenerator)
//            def sqlGenerator3 = Mock(SqlGenerator)
//        and:
//            [sqlGenerator3, sqlGenerator2, sqlGenerator].each {
//                factory.registerGenerator(it)
//            }
//        when:
//            def actual = factory.getGenerator(dataSource)
//        then:
//            1 * sqlGenerator.isCompatible(dbMetaData) >> false
//            1 * sqlGenerator2.isCompatible(dbMetaData) >> { throw new SQLException('Not me!') }
//            1 * sqlGenerator3.isCompatible(dbMetaData) >> true
//           actual == sqlGenerator3
//    }
//
//    def 'getGenerator(): caches result'() {
//        setup:
//            def dbMetaData2 = Mock(DatabaseMetaData)
//            def dataSource2 = Mock(DataSource) {
//                getConnection() >> Mock(Connection) {
//                    getMetaData() >> dbMetaData2
//                }
//            }
//            def sqlGenerator2 = Mock(SqlGenerator)
//        and:
//            factory.registerGenerator(sqlGenerator2)
//            factory.registerGenerator(sqlGenerator)
//        when:
//            2.times { assert factory.getGenerator(dataSource).is(sqlGenerator) }
//        then:
//            1 * sqlGenerator.isCompatible(dbMetaData) >> true
//        when:
//            factory.getGenerator(dataSource2).is(sqlGenerator2)
//        then:
//            1 * sqlGenerator.isCompatible(dbMetaData2) >> false
//            1 * sqlGenerator2.isCompatible(dbMetaData2) >> true
//        when:
//            factory.getGenerator(dataSource).is(sqlGenerator)
//        then:
//            0 * sqlGenerator.isCompatible(_)
//    }
//
//    def 'getGenerator(): throws IllegalStateException when no compatible generator is found'() {
//        setup:
//            factory.clear()
//            factory.registerGenerator(sqlGenerator)
//            sqlGenerator.isCompatible(dbMetaData) >> false
//        when:
//            factory.getGenerator(dataSource)
//        then:
//            thrown IllegalStateException
//    }
//
//    def 'getGenerator(): throws DataAccessResourceFailureException when failed to get MetaData'() {
//        when:
//            factory.getGenerator(dataSource)
//        then:
//            1 * dataSource.getConnection() >> { throw new SQLException('Oh crap!') }
//        and:
//            thrown DataAccessResourceFailureException
//    }
//
//
//    def 'registerGenerator(): adds given generator to the top of the generators stack'() {
//        when:
//            factory.registerGenerator(sqlGenerator)
//        then:
//            factory.@generators.first.is(sqlGenerator)
//    }
//
//
//    def 'clear(): removes all registered generators'() {
//        setup:
//            assert !factory.@generators.isEmpty()
//        when:
//            factory.clear()
//        then:
//            factory.@generators.isEmpty()
//    }
//}
