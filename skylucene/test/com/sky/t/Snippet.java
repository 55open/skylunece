package com.sky.t;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;


import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.Parenthesis;
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

public class Snippet {
    
    @SuppressWarnings("rawtypes")
    public static void main(String[] args) {
	try {
	    CCJSqlParserManager pm = new CCJSqlParserManager();
	    String sql = "SELECT * FROM MY_TABLE1, MY_TABLE2, (SELECT * FROM MY_TABLE3) LEFT OUTER JOIN MY_TABLE4 "
		    + " WHERE ID = (SELECT MAX(ID) FROM MY_TABLE5) AND ID2 IN (SELECT * FROM MY_TABLE6)";
	    
	    String sql2 ="select * from User where i1=? AND (i2=? or (i3!=? and i4=?)) OR (i5=? or i6=?) AND i7<? AND i8<=? AND i9>? AND i10>=? AND i11 like ? Order By id Asc,name desc";
	    
	    net.sf.jsqlparser.statement.Statement statement = pm.parse(new StringReader(sql2));
	    /*
	     * now you should use a class that implements StatementVisitor to
	     * decide what to do based on the kind of the statement, that is
	     * SELECT or INSERT etc. but here we are only interested in SELECTS
	     */
	    if (statement instanceof Select) {
		Select selectStatement = (Select) statement; 
		TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
		PlainSelect sb= (PlainSelect) selectStatement.getSelectBody();
		
		
		Expression where =sb.getWhere();
		exp(where,null);
		
		List<OrderByElement> list = sb.getOrderByElements();
		exp2(list);
		
		System.out.println(sb);
		List tableList = tablesNamesFinder.getTableList(selectStatement);
		for (Iterator iter = tableList.iterator(); iter.hasNext();) {
		    System.out.println(iter.next().toString());
		}
	    }
	} catch (JSQLParserException e) {
	    e.printStackTrace();
	}
    }
    
    
    public  static void exp( Expression where,Expression link){ 
	System.out.println(where.getClass().getName());
	if(where instanceof AndExpression){
	    AndExpression andWhere=(AndExpression)where;  
	    if(null!=link){
		if(link instanceof AndExpression){
		    System.out.println("AndExpression"+andWhere.toString());
		}else if(link instanceof OrExpression){
		    System.out.println("OrExpression"+andWhere.toString());
		}
	    }
	    
	    exp(andWhere.getLeftExpression(),andWhere);
	    exp(andWhere.getRightExpression(),andWhere); 
	}else if(where instanceof OrExpression){
	    OrExpression orWhere=(OrExpression)where;
	    //System.out.println("or: "+orWhere.toString());
	    if(null!=link){
		if(link instanceof AndExpression){
		    System.out.println("AndExpression"+orWhere.toString());
		}else if(link instanceof OrExpression){
		    System.out.println("OrExpression"+orWhere.toString());
		}
	    }
	    exp(orWhere.getLeftExpression(),orWhere);
	    exp(orWhere.getRightExpression(),orWhere); 
	}else if(where instanceof Parenthesis){
	    Parenthesis orWhere=(Parenthesis)where;
	    System.out.println("pa: "+orWhere.toString());
	    exp(orWhere.getExpression(),null); 
	}else if(where instanceof EqualsTo){
	    EqualsTo equalsTo=(EqualsTo)where;
	    
	    if(null!=link){
		if(link instanceof AndExpression){
		    System.out.println("AndExpression"+equalsTo.toString());
		}else if(link instanceof OrExpression){
		    System.out.println("OrExpression"+equalsTo.toString());
		}
	    } 
	    System.out.println("eq: "+equalsTo.toString());
	    Column column =(Column) equalsTo.getLeftExpression();
	    JdbcParameter jdbcParam =(JdbcParameter) equalsTo.getRightExpression();
	    System.out.println(column.getColumnName()+"		"+jdbcParam.toString());
	    //NotEqualsTo equalsTo2 =(NotEqualsTo) where;
	} else if(where instanceof MinorThan){
	    MinorThan minorThan = (MinorThan) where;
	     System.out.println(minorThan);
	}else if(where instanceof MinorThanEquals){
	    
	    
	}else if(where instanceof GreaterThan){
	    
	    
	}else if(where instanceof GreaterThanEquals){
	    
	    
	}else if(where instanceof LikeExpression){
	    LikeExpression minorThan = (LikeExpression) where;
	    System.out.println(minorThan);
	}
	
	
    }
    
    
    public static void exp2(List<OrderByElement> list){
	for (int i = 0; i <list.size(); i++) { 
	    OrderByElement orderEle = list.get(i);
	    System.out.println(orderEle.isAsc());
	    Column column = (Column) orderEle.getExpression();
	    System.out.println(column.getColumnName()); 
	}
    }
    
}
