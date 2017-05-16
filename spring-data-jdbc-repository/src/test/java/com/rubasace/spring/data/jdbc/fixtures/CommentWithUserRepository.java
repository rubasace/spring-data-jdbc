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
import com.rubasace.spring.data.repository.TableDescription;
import com.rubasace.spring.data.repository.sql.SqlGeneratorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

@Repository
public class CommentWithUserRepository extends BaseJdbcRepository<CommentWithUser, Integer> {

    public static final RowMapper<CommentWithUser> ROW_MAPPER = new RowMapper<CommentWithUser>() {

        public CommentWithUser mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = UserRepository.ROW_MAPPER.mapRow(rs, rowNum);
            return new CommentWithUser(
                    rs.getInt("id"),
                    user,
                    rs.getString("contents"),
                    rs.getTimestamp("created_time"),
                    rs.getInt("favourite_count")
            );
        }
    };

    public static final RowUnmapper<CommentWithUser> ROW_UNMAPPER = new RowUnmapper<CommentWithUser>() {

        public Map<String, Object> mapColumns(CommentWithUser o) {
            Map<String, Object> cols = new LinkedHashMap<>();
            cols.put("id", o.getId());
            cols.put("user_name", o.getUser().getUserName());
            cols.put("contents", o.getContents());
            cols.put("created_time", new Timestamp(o.getCreatedTime().getTime()));
            cols.put("favourite_count", o.getFavouriteCount());
            return cols;
        }
    };

    public CommentWithUserRepository(final SqlGeneratorFactory sqlGeneratorFactory, final DataSource dataSource) {
        this(new TableDescription(
                "COMMENTS", "COMMENTS JOIN USERS ON COMMENTS.user_name = USERS.user_name", "id"), sqlGeneratorFactory, dataSource);
    }

    public CommentWithUserRepository(TableDescription table, final SqlGeneratorFactory sqlGeneratorFactory, final DataSource dataSource) {
        super(ROW_MAPPER, ROW_UNMAPPER, table, sqlGeneratorFactory, dataSource);
    }


    @Override
    protected <S extends CommentWithUser> S postInsert(S entity, Number generatedId) {
        entity.setId(generatedId.intValue());
        return entity;
    }
}
