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
package com.rubasace.spring.data.jdbc.sql;

///**
// * SQL Generator compatible with SQL:99.
// */
//public class DefaultSqlGenerator2 implements SqlGenerator {
//
//	static final String AND = " AND ", OR = " OR ", COMMA = ", ", PARAM = " = ?", NOT_EQUAL = " <> ", EQUAL = " = ",
//			PARAM_PLACEHOLDER = "?";
//
//	public boolean isCompatible(DatabaseMetaData metadata) throws SQLException {
//		return true;
//	}
//
//	public String count(TableDescription table) {
//		return format("SELECT count(*) FROM %s", table.getFromClause());
//	}
//
//	public String deleteAll(TableDescription table) {
//		return format("DELETE FROM %s", table.getTableName());
//	}
//
//	public String deleteById(TableDescription table) {
//		return deleteByIds(table, 1);
//	}
//
//	public String deleteByIds(TableDescription table, int idsCount) {
//		return deleteAll(table) + " WHERE " + idsPredicate(table, idsCount);
//	}
//
//	public String existsById(TableDescription table) {
//		return format("SELECT 1 FROM %s WHERE %s", table.getTableName(), idPredicate(table));
//	}
//
//	public String insert(TableDescription table, Map<String, Object> columns) {
//
//		return format("INSERT INTO %s (%s) VALUES (%s)", table.getTableName(),
//				collectionToDelimitedString(columns.keySet(), COMMA), repeat("?", COMMA, columns.size()));
//	}
//
//	public String selectAll(TableDescription table) {
//		return format("SELECT %s FROM %s", table.getSelectClause(), table.getFromClause());
//	}
//
//	public String selectAll(TableDescription table, Pageable page) {
//		Sort sort = page.getSort() != null ? page.getSort() : sortById(table);
//
//		return format(
//				"SELECT t2__.* FROM ( " + "SELECT row_number() OVER (ORDER BY %s) AS rn__, t1__.* FROM ( %s ) t1__ "
//						+ ") t2__ WHERE t2__.rn__ BETWEEN %s AND %s",
//				orderByExpression(sort), selectAll(table), page.getOffset() + 1, page.getOffset() + page.getPageSize());
//	}
//
//	public String selectAll(TableDescription table, Sort sort) {
//		return selectAll(table) + (sort != null ? orderByClause(sort) : "");
//	}
//
//	public String selectById(TableDescription table) {
//		return selectByIds(table, 1);
//	}
//
//	public String selectByIds(TableDescription table, int idsCount) {
//		return idsCount > 0 ? selectAll(table) + " WHERE " + idsPredicate(table, idsCount) : selectAll(table);
//	}
//
//	public String update(TableDescription table, Map<String, Object> columns) {
//
//		return format("UPDATE %s SET %s WHERE %s", table.getTableName(), formatParameters(columns.keySet(), COMMA),
//				idPredicate(table));
//	}
//
//	protected String orderByClause(Sort sort) {
//		return " ORDER BY " + orderByExpression(sort);
//	}
//
//	protected String orderByExpression(Sort sort) {
//		StringBuilder sb = new StringBuilder();
//
//		for (Iterator<Order> it = sort.iterator(); it.hasNext();) {
//			Order order = it.next();
//			sb.append(order.getProperty()).append(' ').append(order.getDirection());
//
//			if (it.hasNext())
//				sb.append(COMMA);
//		}
//		return sb.toString();
//	}
//
//	protected Sort sortById(TableDescription table) {
//		return new Sort(Direction.ASC, table.getPkColumns());
//	}
//
//	private String idPredicate(TableDescription table) {
//		return formatParameters(table.getPkColumns(), AND);
//	}
//
//	private String idsPredicate(TableDescription table, int idsCount) {
//		Assert.isTrue(idsCount > 0, "idsCount must be greater than zero");
//
//		List<String> idColumnNames = table.getPkColumns();
//
//		if (idsCount == 1) {
//			return idPredicate(table);
//
//		} else if (idColumnNames.size() > 1) {
//			return repeat("(" + formatParameters(idColumnNames, AND) + ")", OR, idsCount);
//
//		} else {
//			return idColumnNames.get(0) + " IN (" + repeat("?", COMMA, idsCount) + ")";
//		}
//	}
//
//	private String formatParameters(Collection<String> columns, String delimiter) {
//		return collectionToDelimitedString(columns, delimiter, "", PARAM);
//	}
//
//	// NEW
//
//	public String or(String left, String right) {
//		return left + OR + right;
//	}
//
//	public String and(String left, String right) {
//		return left + AND + right;
//	}
//
//	public String sort(String select, Sort sort) {
//		return select + (sort != null ? orderByClause(sort) : "");
//	}
//
//	public String compareAttribute(String attribute) {
//		return compareAttribute(attribute, false);
//	}
//
//	public String compareAttribute(String attribute, boolean negating) {
//		return attribute + (negating ? NOT_EQUAL : EQUAL) + PARAM_PLACEHOLDER;
//	}
//
//	public String select(String table) {
//		return select(table, false);
//	}
//
//	public String select(String table, boolean distinct) {
//		return format("SELECT %s%s FROM %s", distinct ? "DISTINCT " : "", "*", table);
//	}
//
//	public String where() {
//		return " WHERE ";
//	}
//
//	public String count(String table) {
//		return format("SELECT count(*) FROM %s", table);
//	}
//
//	public String delete(String table) {
//		return format("DELETE FROM %s", table);
//	}
//
//	public String limit(String query, Pageable page) {
//		String sortClause = getSortClause(page);
//		return format(
//				"SELECT t2__.* FROM ( " + "SELECT row_number() OVER %s AS rn__, t1__.* FROM ( %s ) t1__ "
//						+ ") t2__ WHERE t2__.rn__ BETWEEN %s AND %s",
//				sortClause, query, page.getOffset() + 1, page.getOffset() + page.getPageSize());
//	}
//	
//	protected String getSortClause(Pageable page){
//		Sort sort = page.getSort() != null ? page.getSort() : null;
//		return sort != null ? String.format(" ORDER BY %s", orderByExpression(sort)) : "";
//	}
//}