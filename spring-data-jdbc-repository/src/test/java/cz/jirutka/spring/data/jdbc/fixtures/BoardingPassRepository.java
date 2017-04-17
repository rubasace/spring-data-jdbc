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
package cz.jirutka.spring.data.jdbc.fixtures;

import cz.jirutka.spring.data.jdbc.BaseJdbcRepository;
import cz.jirutka.spring.data.jdbc.RowUnmapper;
import cz.jirutka.spring.data.jdbc.TableDescription;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class BoardingPassRepository extends BaseJdbcRepository<BoardingPass, Object[]> {

    public static final RowMapper<BoardingPass> ROW_MAPPER = new RowMapper<BoardingPass>() {

        public BoardingPass mapRow(ResultSet rs, int rowNum) throws SQLException {
            BoardingPass boardingPass = new BoardingPass(
                    rs.getString("flight_no"),
                    rs.getInt("seq_no"),
                    rs.getString("passenger"),
                    rs.getString("seat")
            );
            return boardingPass.withPersisted(true);
        }
    };

    public static final RowUnmapper<BoardingPass> ROW_UNMAPPER = new RowUnmapper<BoardingPass>() {

        public Map<String, Object> mapColumns(BoardingPass o) {
            HashMap<String, Object> row = new HashMap<>();
            row.put("flight_no", o.getFlightNo());
            row.put("seq_no", o.getSeqNo());
            row.put("passenger", o.getPassenger());
            row.put("seat", o.getSeat());
            return row;
        }
    };


    public BoardingPassRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER,
              new TableDescription("BOARDING_PASS", null, "flight_no", "seq_no"));
    }


    @Override
    protected <S extends BoardingPass> S postInsert(S entity, Number generatedId) {
        entity.withPersisted(true);
        return entity;
    }

}
