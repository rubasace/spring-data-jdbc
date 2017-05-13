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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@Repository
public class CommentRepository extends BaseJdbcRepository<Comment, Integer> {

    public static final RowMapper<Comment> ROW_MAPPER = new RowMapper<Comment>() {

        public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Comment(
                    rs.getInt("id"),
                    rs.getString("user_name"),
                    rs.getString("contents"),
                    rs.getTimestamp("created_time"),
                    rs.getInt("favourite_count")
            );
        }
    };

    public static final RowUnmapper<Comment> ROW_UNMAPPER = new RowUnmapper<Comment>() {

        public Map<String, Object> mapColumns(Comment o) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", o.getId());
            row.put("user_name", o.getUserName());
            row.put("contents", o.getContents());
            row.put("created_time", new java.sql.Timestamp(o.getCreatedTime().getTime()));
            row.put("favourite_count", o.getFavouriteCount());
            return row;
        }
    };

    public CommentRepository(final SqlGeneratorFactory sqlGeneratorFactory) {
        super(ROW_MAPPER, ROW_UNMAPPER, "COMMENTS", sqlGeneratorFactory);
    }

    public CommentRepository(TableDescription table, final SqlGeneratorFactory sqlGeneratorFactory) {
        super(ROW_MAPPER, ROW_UNMAPPER, table, sqlGeneratorFactory);
    }


    @Override
    protected <S extends Comment> S postInsert(S entity, Number generatedId) {
        entity.setId(generatedId.intValue());
        return entity;
    }
}
