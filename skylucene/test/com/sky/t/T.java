package com.sky.t;

import org.junit.Test;

import com.skylucene.core.model.SkyNode;

public class T {
    
    /**
     
      <node>
      <name>i1<name>
      <operator>=</operator>
      <value>?</value>
      </node>
      <node> 
      
      </node>
     
     */
   
    
    @Test
    public void test(){
	String sql ="FROM User WHERE i1=? AND (i2=? or i3=?)";
	String sql2=sql.split("FROM ")[1];
	String[] sql3=sql2.split("WHERE ");
	System.out.println(sql3[0]);
	System.out.println(sql3[1]);
	String whereSql = sql3[1];
	while(-1!=whereSql.indexOf("  ")){
	    whereSql =  whereSql.replaceAll("  ", " "); 
	}
	//sql2.indexOf("");
	char[] whereSqlChar = whereSql.toCharArray();
	StringBuffer one=null;
	SkyNode mian =new SkyNode();
	for (int i = 0; i < whereSqlChar.length; i++) { 
	    if(' ' == whereSqlChar[i]){
		//判断后面4个字符有没有  and 
		String ch=null;
		try {
		    ch = new String(whereSqlChar,i+1,4);
		} catch (Exception e) { 
		    break;
		}
		
		if(-1!=ch.toLowerCase().indexOf("and ")){
		    
		}else if((-1!=ch.toLowerCase().indexOf("("))){
		    System.out.println(ch);
		}else if((-1!=ch.toLowerCase().indexOf("or"))){
		    System.out.println(ch);
		}else {
		    System.out.println(ch);
		}
	    }
	    
	    
	    
	}
	
    }

}
