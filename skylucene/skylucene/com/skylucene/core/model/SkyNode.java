package com.skylucene.core.model;

public class SkyNode {
    
  //select * from table where i1=? AND (i2=? or i3=?)
    
    private String name;
    private String operator;//运算符  = >= <=
    private Object value; 
    private int    valueIndex;
    
    
    private String sqlOperator; //or and
    
    private SkyNodeType nodeType;
    
    private SkyNode nextNode;
    private SkyNode[] childNodes;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getOperator() {
        return operator;
    }
    public void setOperator(String operator) {
        this.operator = operator;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    
    public int getValueIndex() {
        return valueIndex;
    }
    public void setValueIndex(int valueIndex) {
        this.valueIndex = valueIndex;
    }
    public String getSqlOperator() {
        return sqlOperator;
    }
    public void setSqlOperator(String sqlOperator) {
        this.sqlOperator = sqlOperator;
    }
    public SkyNode getNextNode() {
        return nextNode;
    }
    public void setNextNode(SkyNode nextNode) {
        this.nextNode = nextNode;
    }
    public SkyNode[] getChildNodes() {
        return childNodes;
    }
    public void setChildNodes(SkyNode[] childNodes) {
        this.childNodes = childNodes;
    }
    public SkyNodeType getNodeType() {
        return nodeType;
    }
    public void setNodeType(SkyNodeType nodeType) {
        this.nodeType = nodeType;
    }
     
    
    
    
    
    
    
}
