package com.skylucene.sqlparser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;

import com.skylucene.core.LuceneSqlHandler;
import com.skylucene.core.exception.NotFoundException;
import com.skylucene.core.model.FieldInfo;
import com.skylucene.core.model.FieldType;

public class LuceneSqlHandlerImpl extends LuceneSqlHandler{
    
    
    
    
    private BooleanQuery rootQuery; 
    private int paramIndexNow=0;
    private Object[] param;
    
    private Class<?> tableClazz;
    private Map<String, FieldInfo> fieldInfos;
    
    public LuceneSqlHandlerImpl initTable(Class<?> tableClazz,Map<String, FieldInfo> fieldInfos){
	this.tableClazz =tableClazz;
	this.fieldInfos =fieldInfos;
	return this;
    }
    

    @SuppressWarnings("unchecked")
    public void handle(BooleanQuery rootQuery,Sort sort , String sql,Object[] param) throws Exception { 
	this.rootQuery = rootQuery; 
	this.param = param;
	sql = sql.trim();
	while (-1 != sql.indexOf("  ")) {
	    sql = sql.replaceAll("  ", " ");
	}

	String t1 = "FROM " + tableClazz.getSimpleName();
	try {
	    String t2 = sql.substring(0, t1.length());
	    if (!t1.equalsIgnoreCase(t2)) {
		throw new Exception("error :table name");
	    }
	} catch (Exception e) {
	    throw new Exception("error :table name");
	}

	sql = "select * " + sql;
	

	CCJSqlParserManager pm = new CCJSqlParserManager();
	net.sf.jsqlparser.statement.Statement statement = pm.parse(new StringReader(sql));
	if (statement instanceof Select) {
	    Select selectStatement = (Select) statement;
	    PlainSelect sb = (PlainSelect) selectStatement.getSelectBody();
	    Expression where = sb.getWhere();
	    handleWhere(where);
	    
	    List<OrderByElement> list = sb.getOrderByElements();
	    if(null!=list && list.size()>0){
		 sort.setSort(handleOrder(list)) ;
	    }
	   
	} 
    }
    /**
     * net.sf.jsqlparser.expression.operators.relational.MinorThan
net.sf.jsqlparser.expression.operators.relational.MinorThanEquals   
net.sf.jsqlparser.expression.operators.relational.GreaterThan
net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals
     * @param where
     * @throws Exception
     */

    private void handleWhere(Expression where ) throws Exception{
	System.out.println(where.getClass().getName());
	if (where instanceof Parenthesis) {
	    //多个.
	    Parenthesis parenthesis = (Parenthesis) where;
	    //System.out.println("Parenthesis: " + parenthesis.toString());
	    handleWhere(parenthesis.getExpression());
	    
	} else if (where instanceof AndExpression) {
	    AndExpression andWhere = (AndExpression) where; 
	    handleWhere(andWhere.getLeftExpression());
	    handleWhere(andWhere.getRightExpression());
	} else if (where instanceof OrExpression) {
	    OrExpression orWhere = (OrExpression) where; 
	    handleWhere(orWhere.getLeftExpression());
	    handleWhere(orWhere.getRightExpression());
	}else{
	    simpleWhere(where);
	}
    }
    
    
    
    private void simpleWhere(Expression where) throws Exception{ 
	System.out.println("eq: " + where.toString());
	if (where instanceof EqualsTo) {
	    EqualsTo equalsTo = (EqualsTo) where; 
	    Column column = (Column) equalsTo.getLeftExpression(); 
	    //Query termQuery = new TermQuery(new Term(column.getColumnName(), param[paramIndexNow++].toString())); 
	   // rootQuery.add(termQuery,org.apache.lucene.search.BooleanClause.Occur.MUST);
	    Expression right =  equalsTo.getRightExpression();
	    FieldInfo fieldInfo = getFieldInfo(column.getColumnName()); 
	    FieldType fieldType = fieldInfo.getFieldType(); 
	    Query termQuery =null;
	    switch(fieldType){
		case STRING: 
		    termQuery = new TermQuery(new Term(column.getColumnName(), getValue(right))); 
		    break;
		case INT:
		    Integer intValue = Integer.valueOf(getValue(right));
		    termQuery = NumericRangeQuery.newIntRange(column.getColumnName(), intValue,intValue , true, true);
		    break;
		case LONG:
		    Long longValue = Long.valueOf(getValue(right));
		    termQuery = NumericRangeQuery.newLongRange(column.getColumnName(), longValue ,longValue , true, true);
		    break;
		case FLOAT:
		    Float floatValue = Float.valueOf(getValue(right));
		    termQuery = NumericRangeQuery.newFloatRange(column.getColumnName(),floatValue ,floatValue, true, true);
		    break;
		case DOUBLE:
		    Double doubleValue = Double.valueOf(getValue(right));
		    termQuery = NumericRangeQuery.newDoubleRange(column.getColumnName(), doubleValue,doubleValue , true, true);
		    break;
		default:
		    throw new Exception();

	    } 
	    rootQuery.add(termQuery,org.apache.lucene.search.BooleanClause.Occur.MUST);
	    //JdbcParameter jdbcParam = (JdbcParameter) equalsTo.getRightExpression();
	    //System.out.println(column.getColumnName() + "		" + jdbcParam.toString());
	    // NotEqualsTo equalsTo2 =(NotEqualsTo) where;
	}else if(where instanceof MinorThan){  //  < 小于号
	    MinorThan minorThan = (MinorThan) where;
	    Column column = (Column) minorThan.getLeftExpression();
	    
	    FieldInfo fieldInfo = getFieldInfo(column.getColumnName()); 
	    Expression right =  minorThan.getRightExpression();
	    FieldType fieldType = fieldInfo.getFieldType(); 
	    Query termQuery =null;
	    switch(fieldType){
		case STRING: 
		    //termQuery = NumericRangeQuery.newDoubleRange(column.getColumnName(), requestVo.getLowPrice() ,requestVo.getHighPrice() , true, true);
		    break;
		case INT:
		    Integer intValue = Integer.valueOf(getValue(right));
		    termQuery = NumericRangeQuery.newIntRange(column.getColumnName(), Integer.MIN_VALUE ,intValue , true, false);
		    break;
		case LONG:
		    Long longValue = Long.valueOf(getValue(right));
		    termQuery = NumericRangeQuery.newLongRange(column.getColumnName(), Long.MIN_VALUE ,longValue , true, false);
		    break;
		case FLOAT:
		    Float floatValue = Float.valueOf(getValue(right));
		    termQuery = NumericRangeQuery.newFloatRange(column.getColumnName(), Float.MIN_VALUE ,floatValue, true, false);
		    break;
		case DOUBLE:
		    Double doubleValue = Double.valueOf(getValue(right));
		    termQuery = NumericRangeQuery.newDoubleRange(column.getColumnName(), Double.MIN_VALUE,doubleValue , true, false);
		    break;
		default:
		    throw new Exception();

	    } 
	    rootQuery.add(termQuery,org.apache.lucene.search.BooleanClause.Occur.MUST);
	}else if(where instanceof MinorThanEquals){
	    MinorThanEquals minorThanEquals = (MinorThanEquals) where;
	    Column column = (Column) minorThanEquals.getLeftExpression();
	    
	    FieldInfo fieldInfo = getFieldInfo(column.getColumnName()); 
	    FieldType fieldType = fieldInfo.getFieldType(); 
	    Expression right =  minorThanEquals.getRightExpression();
	    Query termQuery =null;
	    switch(fieldType){
		case STRING: 
		    //termQuery = NumericRangeQuery.newDoubleRange(column.getColumnName(), requestVo.getLowPrice() ,requestVo.getHighPrice() , true, true);
		    break;
		case INT:
		    Integer intValue = Integer.valueOf(getValue(right));
		    termQuery = NumericRangeQuery.newIntRange(column.getColumnName(), Integer.MIN_VALUE ,intValue , true, true);
		    break;
		case LONG:
		    Long longValue = Long.valueOf(getValue(right));
		    termQuery = NumericRangeQuery.newLongRange(column.getColumnName(), Long.MIN_VALUE ,longValue , true, true);
		    break;
		case FLOAT:
		    Float floatValue = Float.valueOf(getValue(right));
		    termQuery = NumericRangeQuery.newFloatRange(column.getColumnName(), Float.MIN_VALUE ,floatValue, true, true);
		    break;
		case DOUBLE:
		    Double doubleValue = Double.valueOf(getValue(right));
		    termQuery = NumericRangeQuery.newDoubleRange(column.getColumnName(), Double.MIN_VALUE,doubleValue , true, true);
		    break;
		default:
		    throw new Exception();

	    } 
	    rootQuery.add(termQuery,org.apache.lucene.search.BooleanClause.Occur.MUST);
	    
	}else if(where instanceof GreaterThan){
	    GreaterThan greaterThan = (GreaterThan) where;
	    Column column = (Column) greaterThan.getLeftExpression();
	    
	    FieldInfo fieldInfo = getFieldInfo(column.getColumnName()); 
	    FieldType fieldType = fieldInfo.getFieldType(); 
	    Expression right =  greaterThan.getRightExpression();
	    Query termQuery =null;
	    switch(fieldType){
		case STRING: 
		    //termQuery = NumericRangeQuery.newDoubleRange(column.getColumnName(), requestVo.getLowPrice() ,requestVo.getHighPrice() , true, true);
		    break;
		case INT:
		    Integer intValue = Integer.valueOf(getValue(right));
		    termQuery = NumericRangeQuery.newIntRange(column.getColumnName(), intValue ,Integer.MAX_VALUE , false, true);
		    break;
		case LONG:
		    Long longValue = Long.valueOf(getValue(right));
		    termQuery = NumericRangeQuery.newLongRange(column.getColumnName(),longValue , Long.MAX_VALUE , false, true);
		    break;
		case FLOAT:
		    Float floatValue = Float.valueOf(getValue(right));
		    termQuery = NumericRangeQuery.newFloatRange(column.getColumnName(), floatValue,Float.MAX_VALUE , false, true);
		    break;
		case DOUBLE:
		    Double doubleValue = Double.valueOf(getValue(right));
		    termQuery = NumericRangeQuery.newDoubleRange(column.getColumnName(), doubleValue ,Double.MAX_VALUE, false, true);
		    break;
		default:
		    throw new Exception();

	    } 
	    rootQuery.add(termQuery,org.apache.lucene.search.BooleanClause.Occur.MUST);
	    
	}else if(where instanceof GreaterThanEquals){
	    GreaterThanEquals greaterThanEquals = (GreaterThanEquals) where;
	    Column column = (Column) greaterThanEquals.getLeftExpression();
	    
	    FieldInfo fieldInfo = getFieldInfo(column.getColumnName()); 
	    FieldType fieldType = fieldInfo.getFieldType(); 
	    Expression right =  greaterThanEquals.getRightExpression();
	    Query termQuery =null;
	    switch(fieldType){// >=
		case STRING:
		    //termQuery = NumericRangeQuery.newDoubleRange(column.getColumnName(), requestVo.getLowPrice() ,requestVo.getHighPrice() , true, true);
		    break;
		case INT:
		    Integer intValue = Integer.valueOf(getValue(right));
		    termQuery = NumericRangeQuery.newIntRange(column.getColumnName(), intValue ,Integer.MAX_VALUE , true, true);
		    break;
		case LONG:
		    Long longValue = Long.valueOf(getValue(right));
		    termQuery = NumericRangeQuery.newLongRange(column.getColumnName(),longValue , Long.MAX_VALUE , true, true);
		    break;
		case FLOAT:
		    Float floatValue = Float.valueOf(getValue(right));
		    termQuery = NumericRangeQuery.newFloatRange(column.getColumnName(), floatValue,Float.MAX_VALUE , true, true);
		    break;
		case DOUBLE:
		    Double doubleValue = Double.valueOf(getValue(right));
		    termQuery = NumericRangeQuery.newDoubleRange(column.getColumnName(), doubleValue ,Double.MAX_VALUE, true, true);
		    break; 

	    } 
	    rootQuery.add(termQuery,org.apache.lucene.search.BooleanClause.Occur.MUST);
	    
	}else if(where instanceof LikeExpression){
	    LikeExpression likeExpression = (LikeExpression) where;
	    Column column = (Column) likeExpression.getLeftExpression();
	    Expression right =  likeExpression.getRightExpression();
	    String keywords = QueryParser.escape(getValue(right)); 
	    Query query  =  new FuzzyQuery(new Term(column.getColumnName(),keywords));
	    //JdbcParameter jdbcParameter = (JdbcParameter) likeExpression.getRightExpression();
	    
	    //qb.keyword().onFields("name").matching(keywords ).createQuery();
	    //query.setBoost(3.0f);//这个匹配最重要 
	    rootQuery.add(query, org.apache.lucene.search.BooleanClause.Occur.MUST); 
	}else{
	    throw new Exception(String.format("no support sql grammar : %s", where.getClass().getName()));
	}
	
	
    }
    
    private String getValue(Expression right ) throws Exception{
	if(right instanceof JdbcParameter){
		return param[paramIndexNow++].toString();
	}else if(right instanceof StringValue){
		StringValue  stringValue=(StringValue) right;
		return stringValue.getNotExcapedValue();
	}
	throw new Exception(String.format("error right Expression type:%s", right.getClass().getName()));
    }
    
    
    private SortField[] handleOrder(List<OrderByElement> list) throws Exception{
	List<SortField> asortfield =new ArrayList<SortField>();
	if(null ==list){
	    return null;
	}
	for (int i = 0; i <list.size(); i++) { 
	    OrderByElement orderEle = list.get(i);
	    //System.out.println(orderEle.isAsc());
	    Column column = (Column) orderEle.getExpression();
	    //false = asc  true = desc
	    
	    FieldInfo fieldInfo = getFieldInfo(column.getColumnName()); 
	    FieldType fieldType = fieldInfo.getFieldType(); 
	    int type = SortField.SCORE;
	    switch(fieldType){
		case STRING: 
		    type =SortField.STRING;
		    break;
		case INT:
		    type=SortField.INT;
		    break;
		case LONG:
		    type=SortField.LONG;
		    break; 
		case FLOAT:
		    type=SortField.FLOAT;
		    break;
		case DOUBLE:
		    type=SortField.DOUBLE;
		    break;
		default:
		    throw new Exception();

	    }
	    
	    asortfield.add(new SortField(column.getColumnName(),type,!orderEle.isAsc()));
	    //System.out.println(column.getColumnName()); 
	}
	return asortfield.toArray(new SortField[]{});
    }
    
    private FieldInfo getFieldInfo(String columnName) throws Exception{
	FieldInfo fieldInfo = fieldInfos.get(columnName);
	if(null==fieldInfo){
	    throw new NotFoundException(columnName);
	}
	return fieldInfo;
    }

}
