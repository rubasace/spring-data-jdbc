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
package com.rubasace.spring.data.jdbc.fixtures;

import com.rubasace.spring.data.repository.BaseJdbcRepository;
import com.rubasace.spring.data.repository.RowUnmapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@Repository
public class UserRepository extends BaseJdbcRepository<User, String> {

    public static final RowMapper<User> ROW_MAPPER = new RowMapper<User>() {

        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(
                    rs.getString("user_name"),
                    rs.getDate("date_of_birth"),
                    rs.getInt("reputation"),
                    rs.getBoolean("enabled")
            ).withPersisted(true);
        }
    };

    public static final RowUnmapper<User> ROW_UNMAPPER = new RowUnmapper<User>() {

        public Map<String, Object> mapColumns(User o) {
            LinkedHashMap<String, Object> row = new LinkedHashMap<>();
            row.put("user_name", o.getUserName());
            row.put("date_of_birth", new Date(o.getDateOfBirth().getTime()));
            row.put("reputation", o.getReputation());
            row.put("enabled", o.isEnabled());
            return row;
        }
    };


    public UserRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, "USERS", "user_name");
    }


    @Override
    protected <S extends User> S postUpdate(S entity) {
        entity.withPersisted(true);
        return entity;
    }

    @Override
    protected <S extends User> S postInsert(S entity, Number generatedId) {
        entity.withPersisted(true);
        return entity;
    }
}
