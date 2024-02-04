package org.cloud.examples.commons;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SqlParser {

    @Test
    public void test() throws JSQLParserException {
        String sqlStr = "select 1 from dual where a=b";

        PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse(sqlStr);

        SelectItem<?> selectItem = select.getSelectItems().get(0);
        Assertions.assertEquals(new LongValue(1), selectItem.getExpression());

        Table table = (Table) select.getFromItem();
        Assertions.assertEquals("dual", table.getName());

        EqualsTo equalsTo = (EqualsTo) select.getWhere();
        Column a = (Column) equalsTo.getLeftExpression();
        Column b = (Column) equalsTo.getRightExpression();
        Assertions.assertEquals("a", a.getColumnName());
        Assertions.assertEquals("b", b.getColumnName());
    }
}
