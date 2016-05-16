package com.test;

import java.util.List;
import org.junit.Test;
import com.model.User;
import com.skylucene.core.LuceneSession;
import com.skylucene.core.LuceneSessionFactory;

public class Test2 {
    
    
    
    @SuppressWarnings("unchecked")
    @Test
    public void test1(){
	LuceneSession<User>  userDao = (LuceneSession<User>) LuceneSessionFactory.getInstance().getLuceneWriter(User.class);
	try {
	    
	    long c = System.currentTimeMillis();
	    for (long i = 1000l; i < 1400; i++) {
		 User u = new User();
		 u.setUsername("我饿偶然看撒地方");
		 u.setPassword("我饿偶然看撒地方");
		 u.setId(i);
		 u.setIsDelete(1);
		 userDao.delete(u,false);
		 //userDao.save(u,false);
	    } 
	    
	    /*
	    User u = new User();
	    u.setUsername("我饿偶然看撒地方");
	    u.setPassword("我饿偶然看撒地方为偶然卡死地方啊适当放宽撒娇东风科技撒旦fasdfasdfasdf");
	    u.setId(7000l);
	    userDao.indexWriterClose();*/
	    System.out.println(System.currentTimeMillis()-c);
	} catch (Exception e) {
	    e.printStackTrace();
	}finally{
	    try {
		userDao.indexWriterClose();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    } 
    
    @SuppressWarnings("unchecked")
    @Test
    public void test3(){
	 LuceneSession<User>  userDao = (LuceneSession<User>) LuceneSessionFactory.getInstance().getLuceneWriter(User.class);
	 try {
	     	
	     	StringBuffer sb =new StringBuffer(); 
	        for (long i = 1000l; i < 1000000; i++) { 
	    		sb.append("10000000000");
	        } 
	        long c = System.currentTimeMillis();
	        User u = new User();
	        u.setId(1001l);
	        u.setUsername(sb.toString());
	        u.setIsDelete(1); 
	        userDao.update(u);
	        System.out.println(System.currentTimeMillis()-c);
	} catch (Exception e) {
	    e.printStackTrace();
	}finally{
	    
	}
    }
    
    
    @SuppressWarnings("unchecked")
    @Test
    public void test2(){ 
	LuceneSession<User>  userDao = (LuceneSession<User>) LuceneSessionFactory.getInstance().getLuceneWriter(User.class);
	List<User> list = userDao.find("FROM User Where id=? ", new Object[]{1000 }, 1, 10);
	System.out.println(list.size());
	User user =userDao.get(1000l);
	System.out.println(user.getUsername());
	/*System.out.println(list);
	List<User> list2 = userDao.find("FROM User Where username like '地方' AND id=? OR password=? Order by id DESC", new Object[]{"1","欧亚",1005l}, 1, 10);
	System.out.println(list2.size());
	System.out.println(list2);*/
    }
    
    //信息检索和关系型数据库的区别
    //1.信息检索无法多表查询. 至少效率上不能保证
    //2.
    
    
    //注意
    //1.是否分词决定了    分词查询的和固定查询的效果
    //使用分词的.使用  =  跟 like 效果一致
    //不使用分词  只能使用 =
    
    //2.多表查询的连接如何实现
    //表1$表2$表3...
    //先信息存储.再查询.
    
    //3.where 条件必须使用  AND 连接
    //解决思路 while 语句
    
    
    
}
