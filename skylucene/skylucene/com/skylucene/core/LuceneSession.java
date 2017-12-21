package com.skylucene.core;
 

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.skylucene.core.annotation.SkyDocumentId;
import com.skylucene.core.annotation.SkyField;
import com.skylucene.core.annotation.SkyIndexed;
import com.skylucene.core.annotation.SkyNumericField;
import com.skylucene.core.config.Config;
import com.skylucene.core.model.FieldInfo;
import com.skylucene.core.model.FieldType;

public class LuceneSession<T > { 
    private File	index_file	= null;
    private String	index_path_str	= null;
    private IndexWriter indexWriter	= null;
    private IndexReader indexReader	= null;
    private String	id_fiel_name	= null;
    private String	table_name	= null;
    private Class<?>	table_class	= null;
    private Map<String, FieldInfo> fieldInfos = new HashMap<String, FieldInfo>();
    private Analyzer	analyzer	= null; 
    
    protected LuceneSession(Class<?> t) throws Exception { 
	 loadInitInfo(t);
    }
    
    
    private void loadTableInfo(Class<?> modelClazz)throws Exception{
	SkyIndexed indexed = modelClazz.getAnnotation(SkyIndexed.class);
	if(null==indexed){
	    throw new Exception("This isn't a document model.");
	}
	table_class=modelClazz;
	Method[] modelMethod= modelClazz.getDeclaredMethods(); 
	
	for (Method method : modelMethod) {
	    FieldInfo fieldInfo = new FieldInfo();
	    String methodName = method.getName();
	    if(methodName.startsWith("get")){  
		Class<?> get_method_return_type = method.getReturnType();
		methodName = methodName.substring(3, methodName.length());
		Method writeMethod = modelClazz.getDeclaredMethod("set"+methodName, get_method_return_type);
		if(null==writeMethod){
		    throw new Exception(String.format("doesn't have get method :set%s",methodName));
		}
		fieldInfo.setFieldWriteMethod(writeMethod);
		
		methodName = methodName.substring(0, 1).toLowerCase()+methodName.substring(1, methodName.length());
		
		FieldType fieldType =null;
		if("java.lang.String".equals(get_method_return_type.getName())){ 
		    fieldType= FieldType.STRING;
		}else if("java.lang.Integer".equals(get_method_return_type.getName()) || "int".equals(get_method_return_type.getName()) ){ 
		    fieldType=FieldType.INT;
		}else if("java.lang.Double".equals(get_method_return_type.getName())  || "double".equals(get_method_return_type.getName())){
		    fieldType=FieldType.DOUBLE;
		}else if("java.lang.Float".equals(get_method_return_type.getName())  || "float".equals(get_method_return_type.getName())){
		    fieldType=FieldType.FLOAT;
		}else if("java.lang.Long".equals(get_method_return_type.getName()) || "long".equals(get_method_return_type.getName())){
		    fieldType=FieldType.LONG; 
		}else{//如果都不是   抛异常 
		    throw new Exception(String.format("该类型无法识别 : %s", get_method_return_type.getName()) );
		}
		
		SkyDocumentId method_DocumentId = method.getAnnotation(SkyDocumentId.class);
		SkyField method_Field = method.getAnnotation(SkyField.class);
		if (null != method_Field) {
		    if (method_Field.index().equals(SkyField.Index.YES)) {
			fieldInfo.setIndex(true);
		    } else {
			fieldInfo.setIndex(false);
		    }
		    if (method_Field.analyze().equals(SkyField.Analyze.YES)) {
			fieldInfo.setAnalyze(true);
		    } else {
			fieldInfo.setAnalyze(false);
		    }
		    if (method_Field.store().equals(SkyField.Store.YES)) {
			fieldInfo.setStore(true);
		    } else {
			fieldInfo.setStore(false);
		    }
		}  
		
		fieldInfo.setFieldType(fieldType);
		fieldInfo.setFieldReadMethod(method);
		
		SkyNumericField method_num_Field=method.getAnnotation(SkyNumericField.class);
		fieldInfo.setNumber(false);
		if(null!=method_num_Field) {
		    fieldInfo.setNumber(true);
		} 
		
		if(null!=method_DocumentId){
		    id_fiel_name=methodName;
		    fieldInfo.setId(true); 
		    fieldInfos.put(methodName, fieldInfo);
		} 
		 
		if(null == method_DocumentId &&  null!=method_Field){
		    fieldInfo.setId(false);
		    FieldInfo fieldInfo2= fieldInfos.get(methodName);
		    if(null!=fieldInfo2){
			fieldInfo.setFieldWriteMethod(fieldInfo2.getFieldWriteMethod()); 
		    } 
		    fieldInfos.put(methodName, fieldInfo);
		}
		
	    } 
	} 
	table_name= modelClazz.getSimpleName(); 
    }
    
    private synchronized void loadInitInfo(Class<?> t) throws  Exception{
	if(null==table_name){
	    loadTableInfo(t);
	}
	
	if(null==index_file){
	    File index_path = new File(Config.INDEX_ROOT_PATH);
	    if (!index_path.exists()){
		index_path.mkdirs();
	    }
	    index_path_str =Config.INDEX_ROOT_PATH+File.separator+table_name;
	    index_file = new File(index_path_str);
	    if (!index_file.exists()){
		index_file.mkdirs();
	    }
	}
	if(null==analyzer){
	    analyzer = (Analyzer) Config.ANALYZER_IMPL_CLAZZ.newInstance();
	} 
    } 
    
    


    private synchronized IndexWriter openIndexWriter() throws Exception { 
	if(null!=indexWriter){
	    return indexWriter;
	}
	IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_36,analyzer);
	//索引 设置为追加或者覆盖
	indexWriterConfig.setOpenMode(OpenMode.CREATE);
	LogMergePolicy mergePolicy = new LogByteSizeMergePolicy();
	mergePolicy.setMergeFactor(5);//设置合并文档的数量
	mergePolicy.setUseCompoundFile(true);
	indexWriterConfig.setMergePolicy(mergePolicy);
	
	indexWriter = new IndexWriter(FSDirectory.open(index_file), indexWriterConfig);
	return indexWriter;
    }
    
    public synchronized void indexWriterClose(){
	if(null!=indexWriter){
	    try {
		indexWriter.close();
		indexWriter=null;
	    }  catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    
    private synchronized IndexReader openIndexReader()throws Exception {
	if(null!=indexReader){
	    return indexReader;
	}
	try{
	    indexReader = IndexReader.open(FSDirectory.open(index_file));
	}catch(IndexNotFoundException e){
	    System.out.println(e.getMessage());
	}
	
	return indexReader;
    }
    
    private synchronized void indexReaderClose(){
	if(null!=indexReader){
	    try {
		indexReader.close();
		indexReader=null;
	    } catch (Exception e) { 
		e.printStackTrace();
	    }
	}
    }
    
/*    private String getFieldNameByMethod(String methodName){
	methodName = methodName.substring(3, methodName.length());
	methodName = methodName.substring(0, 1).toLowerCase()+methodName.substring(1, methodName.length());
	return methodName;
    }*/
    
    private LuceneSqlHandler getLuceneSqlHandler(){
	try {
	    LuceneSqlHandler luceneSqlHandler = (LuceneSqlHandler)Config.LUCENE_SQL_HANDLER_IMPL_CLAZZ.newInstance();
	    return luceneSqlHandler.initTable(table_class, fieldInfos);
	} catch (InstantiationException e) { 
	    e.printStackTrace();
	} catch (IllegalAccessException e) { 
	    e.printStackTrace();
	}
	return null;
    }
    
    

    private Document buildDocument(T model) throws Exception {
	Document document = new Document();
	for (String methodName : fieldInfos.keySet()) {
	    FieldInfo fieldInfo = fieldInfos.get(methodName);
	    
	    Field.Index fieldIndex = Field.Index.NOT_ANALYZED;
	    Field.Store fieldStore = Field.Store.NO;
	    boolean fieldIndexBool = false;
	    if (fieldInfo.isId()) {
		fieldIndex	= Field.Index.ANALYZED;
		fieldStore	= Field.Store.YES; 
		fieldIndexBool	= true;
	    }else{  
		    if (fieldInfo.isIndex()) {
			
		    } else {
			
		    }
		    if (fieldInfo.isAnalyze()) {
			fieldIndex	= Field.Index.ANALYZED;
			fieldIndexBool	= true;
		    } else {
			fieldIndex	= Field.Index.NOT_ANALYZED;
			fieldIndexBool	= false;
		    }
		    if (fieldInfo.isStore()) {
			fieldStore = Field.Store.YES;
		    } else {
			fieldStore = Field.Store.NO;
		    } 
	    }
	     
	    Fieldable field =null;
	    Method method = fieldInfo.getFieldReadMethod();
	    String fielValue = null==method.invoke(model)?null:method.invoke(model).toString(); 
	    if(null==fielValue){
		  continue;
	    }
	    if(fieldInfo.isNumber()){
		FieldType fieldType =fieldInfo.getFieldType();
		switch(fieldType){ 
		case INT:
		    field = new NumericField(methodName,fieldStore, fieldIndexBool).setIntValue(Integer.valueOf(fielValue));
		    break;
		case DOUBLE:
		    field = new NumericField(methodName,fieldStore, fieldIndexBool).setDoubleValue(Double.valueOf(fielValue));
		    break;
		case FLOAT:
		    field = new NumericField(methodName,fieldStore, fieldIndexBool).setFloatValue(Float.valueOf(fielValue));
		    break;
		case LONG:
		    field = new NumericField(methodName,fieldStore, fieldIndexBool).setLongValue(Long.valueOf(fielValue));
		    break;
		} 
	    }else{
		field = new Field(methodName,fielValue, fieldStore, fieldIndex);
	    }
	    document.add(field);
	}
	return document;
    }
    
    public  boolean save(T model){
	return save(model, true);
    }
    
    public synchronized boolean save(T model,boolean isClose){
	IndexWriter  writer =null;
	try {
	    
	    FieldInfo fieldInfo = fieldInfos.get(id_fiel_name); 
	    T oldModel = get(fieldInfo.getFieldReadMethod().invoke(model)); 
	    if(null==oldModel){
		writer = openIndexWriter();
		Document doc = buildDocument(model);
		writer.addDocument(doc);
	    }
	    return true;
	} catch (IOException e) { 
	    e.printStackTrace();
	} catch (Exception e) { 
	    e.printStackTrace();
	} finally {
	    if(isClose && null!=writer){
		try {
		    indexWriterClose();
		}  catch ( Exception e) {
		    e.printStackTrace();
		}
	    }
	}
	return false;
    }
    
    public boolean delete(T model){
	return delete(model, true);
    }
    //通过id
    public synchronized boolean delete(T model,boolean isClose){
	IndexWriter  writer =null;
	try { 
	     writer = openIndexWriter();
	     //Directory directory = FSDirectory.open(index_file); 
	     // 注意：这里的第一个参数不能变 
	     //
	     FieldInfo fieldInfo = fieldInfos.get(id_fiel_name);
	     FieldType fieldType = fieldInfo.getFieldType();
	     if(fieldInfo.isNumber()){
		     switch(fieldType){ 
			case INT:
			    Integer intValue = Integer.valueOf(fieldInfo.getFieldReadMethod().invoke(model).toString());
			    Query termQuery = NumericRangeQuery.newIntRange(id_fiel_name, intValue ,intValue , true, true);
			    writer.deleteDocuments(termQuery);
			    break;
			case LONG:
			    Long longValue = Long.valueOf(fieldInfo.getFieldReadMethod().invoke(model).toString());
			    termQuery = NumericRangeQuery.newLongRange(id_fiel_name,longValue , longValue , true, true);
			    writer.deleteDocuments(termQuery);
			    break;
			case FLOAT:
			    Float floatValue = Float.valueOf(fieldInfo.getFieldReadMethod().invoke(model).toString());
			    termQuery = NumericRangeQuery.newFloatRange(id_fiel_name, floatValue,floatValue , true, true);
			    writer.deleteDocuments(termQuery);
			    break;
			case DOUBLE:
			    Double doubleValue = Double.valueOf(fieldInfo.getFieldReadMethod().invoke(model).toString());
			    termQuery = NumericRangeQuery.newDoubleRange(id_fiel_name, doubleValue ,doubleValue, true, true);
			    writer.deleteDocuments(termQuery);
			    break;
			case STRING:
			    
			    break;
		    }  
	     }else{
		    Term term = new Term(id_fiel_name,fieldInfo.getFieldReadMethod().invoke(model).toString());
		    writer.deleteDocuments(term);
	     } 
	     
	     //writer.close();
	     //directory.close(); // 这两个关闭是必需的
	     return true;
	}   catch (Exception e) { 
	    e.printStackTrace();
	}finally {
	    if(isClose && null!=writer){
		try {
		    indexWriterClose();
		}  catch ( Exception e) {
		    e.printStackTrace();
		}
	    }
	}
        // 删除完成 
	return false;
    }
    
    
    public boolean update(T model){
	return update(model, true);
    }
    
    public synchronized boolean update(T model,boolean isClose){
	IndexWriter  writer =null;
	try { 
	     writer = openIndexWriter();
	     Term term = new Term(id_fiel_name,fieldInfos.get(id_fiel_name).getFieldReadMethod().invoke(model).toString());
	     writer.deleteDocuments(term);
	     Document doc = buildDocument(model);
	     writer.addDocument(doc);
	     
	     return true;
	} catch (Exception e) { 
	    e.printStackTrace();
	}finally {
	    if(isClose && null!=writer){
		try {
		    indexWriterClose();
		}  catch ( Exception e) {
		    e.printStackTrace();
		}
	    }
	}
	return false;
    }
    
    


    
/*    public List<T> find(String[] param ,String key){
	try {
	    IndexReader reader = openIndexReader();
	    IndexSearcher searcher = new IndexSearcher(reader);
	    String[] fields = param;
	    BooleanClause.Occur[] flags = {BooleanClause.Occur.SHOULD,BooleanClause.Occur.SHOULD};
	    Query query = MultiFieldQueryParser.parse(Version.LUCENE_36,key,fields,flags,analyzer);
	    Sort sort = null;
	    SortField asortfield[] = null; 
	    
	    
	} catch (Exception e) { 
	    e.printStackTrace();
	}
	return null;
    }*/
    
    public T get(Object id){
	List<T> list = find("FROM "+table_name +" WHERE "+id_fiel_name+"=?",new Object[]{id},1,1);
	if(null!=list && list.size()>=1){
	    return list.get(0);
	}
	return null;
    }
    
    
    public List<T> find(String[] names,Object[] param,Integer pageNo,Integer row){
	BooleanQuery rootQuery =new BooleanQuery();
	for (int i = 0; i < param.length; i++) {
	    TermQuery termQuery = new TermQuery(new Term(names[i], param[i].toString()));
	    rootQuery.add(termQuery, org.apache.lucene.search.BooleanClause.Occur.MUST);
	}
	return find(rootQuery, null,pageNo, row);
    }
    

    
    public List<T> find(String sql,Object[] param,Integer pageNo,Integer row){
	try {
	    BooleanQuery rootQuery =  new BooleanQuery();
	    if(-1==sql.toLowerCase().indexOf("order by")){
		    sql +=" Order By "+id_fiel_name+" asc";
	    }
	    Sort sort =new Sort(); 
	    getLuceneSqlHandler().handle(rootQuery,sort,sql, param);
	    return find(rootQuery, sort,pageNo, row);
	} catch (Exception e) { 
	    e.printStackTrace();
	}
	return null;
    }
    
    @SuppressWarnings("unchecked")
    public List<T> find(BooleanQuery rootQuery,Sort sort,Integer pageNo,Integer row){
	
	try {
	    IndexReader reader = openIndexReader();
	    if(null ==reader){
		return null;
	    }
	    IndexSearcher searcher = new IndexSearcher(reader);
	    
	    /*for (int i = 0; i < param.length; i++) {
		 TermQuery termQuery = new TermQuery(new Term(names[i], param[i].toString()));
		 rootQuery.add(termQuery, org.apache.lucene.search.BooleanClause.Occur.MUST); 
	    }*/ 
	    
	    //排序
	    /*SortField asortfield[] = new SortField[]{ new SortField("title",SortField.SCORE,false)};
	    Sort sort = new Sort(asortfield);*/
	    
	    
	    TopDocs tdocs = null;
	    if(null==sort){
		tdocs=searcher.search(rootQuery,pageNo*row);
	    }else{
		tdocs=searcher.search(rootQuery,pageNo*row,sort);
	    }
	    
	    //总量
	    //int total = tdocs.totalHits;
	    
	    ScoreDoc[] scoreDocs =  tdocs.scoreDocs;
	    List<T> objs = new ArrayList<T>();
	    for (int i = (pageNo-1)*row; i < scoreDocs.length; i++) { 
		Document doc = searcher.doc(scoreDocs[i].doc);
		if(null==doc){
		    continue;
		}
		T obj = (T) table_class.newInstance();
		for (String methodName : fieldInfos.keySet()) {
		    FieldInfo fieldInfo = fieldInfos.get(methodName);
		    Method writeMethod = fieldInfo.getFieldWriteMethod();
		    FieldType fieldType =fieldInfo.getFieldType();
		    String value= doc.get(methodName);
		    if(null!=value){
			 switch(fieldType){
	        		case STRING: 
	        		    writeMethod.invoke(obj,value );
	        		    break;
	        		case INT:
	        		    writeMethod.invoke(obj, Integer.valueOf(value));
	        		    break;
	        		case DOUBLE:
	        		    writeMethod.invoke(obj, Double.valueOf(value));
	        		    break;
	        		case FLOAT:
	        		    writeMethod.invoke(obj, Float.valueOf(value));
	        		    break;
	        		case LONG:
	        		    writeMethod.invoke(obj, Long.valueOf(value));
	        		    break; 
			    }
		    }
		   
		}
		objs.add(obj);
	    } 
	    return objs;
	} catch (Exception e) { 
	    e.printStackTrace();
	}
	return null; 
    }

    /*    private Document buildDocument(T model)throws Exception{
	Document document = new Document(); 
	Class<?> modelClazz = model.getClass();
	SkyIndexed indexed = modelClazz.getAnnotation(SkyIndexed.class);
	if(null==indexed){
	    throw new Exception("This isn't a document model.");
	}
	java.lang.reflect.Method[] modelMethod= modelClazz.getDeclaredMethods();
	for (java.lang.reflect.Method method : modelMethod) {
	    String methodName = method.getName();
	    if(methodName.startsWith("get")){
		SkyDocumentId method_DocumentId = method.getAnnotation(SkyDocumentId.class);
		methodName = methodName.substring(3, methodName.length());
		methodName = methodName.substring(0, 1).toLowerCase()+methodName.substring(1, methodName.length());
		
		Field.Index fieldIndex = Field.Index.NOT_ANALYZED;
		Field.Store fieldStore = Field.Store.NO; 
		if(null!=method_DocumentId){
		    fieldIndex = Field.Index.ANALYZED;
		    fieldStore = Field.Store.YES; 
		    Field field = new Field(methodName,   method.invoke(model).toString(), fieldStore,fieldIndex); 
		    document.add(field);
		}
		SkyField method_Field =method.getAnnotation(SkyField.class);
		if(null!=method_Field){
		    if(method_Field.index().equals(SkyField.Index.YES)){ 
			fieldIndex = Field.Index.ANALYZED;
		    }else{
			fieldIndex = Field.Index.NOT_ANALYZED;
		    }
		    
		    if(method_Field.analyze().equals(SkyField.Analyze.YES)){
			
		    }else{
			
		    }
		    
		    if(method_Field.store().equals(SkyField.Store.YES)){
			fieldStore = Field.Store.YES; 
		    }else{
			fieldStore = Field.Store.NO; 
		    }
		}
		
		Field field = new Field(methodName,method.invoke(model).toString(), fieldStore,fieldIndex); 
		document.add(field);
	    }
	}
	return document;
}*/

}
