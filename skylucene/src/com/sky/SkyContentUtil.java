package com.sky;

import java.util.HashMap;
import java.util.Map;

import com.dao.BaseDao;
import com.dao.impl.BaseDaoImpl;
import com.model.User;

public class SkyContentUtil {
     
    private Map<String,Object> content=new HashMap<String, Object>();
    //**
    //实现配置文件
    
    static{
	BaseDao<User> userDao =new BaseDaoImpl<User>(User.class);
	
    }
    
    /**
     * 读取配置文件
     */
    public void loadConfig(){
	
    }
    

}
