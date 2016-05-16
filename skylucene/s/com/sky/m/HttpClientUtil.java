package com.sky.m;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;
import org.junit.Test;   

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
 

/**
 * 抓取工具类
 * @author Administrator
 *
 */
public class HttpClientUtil {
	
	 // 读取超时  
    private final static int SOCKET_TIMEOUT = 10000;  
    // 连接超时  
    private final static int CONNECTION_TIMEOUT = 10000;  
    // 每个HOST的最大连接数量  
    private final static int MAX_CONN_PRE_HOST = 50;  
    // 连接池的最大连接数  
    private final static int MAX_CONN = 1000;   
    /** 闲置连接超时时间, 由bean factory设置，缺省为60秒钟 */
    private final static  int defaultIdleConnTimeout = 60000; 
    // 连接池  
    private final static HttpConnectionManager httpConnectionManager;  
    
    
    private static String WEIXIN="Mozilla/5.0 (iPhone; CPU iPhone OS 6_1_3 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Mobile/10B329 MicroMessenger/6.2.3";
    
    private static String GOOGLE="Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36";
    
    
    
    static {  
        httpConnectionManager = new MultiThreadedHttpConnectionManager();  
        HttpConnectionManagerParams params = httpConnectionManager.getParams();  
        params.setConnectionTimeout(CONNECTION_TIMEOUT);  
        params.setSoTimeout(SOCKET_TIMEOUT);  
        params.setDefaultMaxConnectionsPerHost(MAX_CONN_PRE_HOST);  
        params.setMaxTotalConnections(MAX_CONN);  
        
        // 创建一个线程安全的HTTP连接池 
        IdleConnectionTimeoutThread ict = new IdleConnectionTimeoutThread();
        ict.addConnectionManager(httpConnectionManager);
        ict.setConnectionTimeout(defaultIdleConnTimeout); 
        ict.start();
    }  
     
	public static String charSet ="UTF-8";
	public static String hosts="180.109.111.72";
	public static String port = "37564";
	
	@Deprecated
	public static void main(String[] args) {
	  //	System.setProperty("http.proxySet", "false");
		//222.35.73.93  80
		//202.206.100.39  3128
		//221.130.202.206  80
/*		System.setProperty("http.proxySet", "true");
	 System.setProperty("http.proxyHost", "221.130.202.206");
	    System.setProperty("http.proxyPort", "80"); */
/*	    Properties prop = System.getProperties();
		prop.getProperty("http.proxyHost");*/
	//	String urlq="http://www.nuomi.com/api/dailydeal?version=v1&city=anshan&city=alashan&city=anqing&city=anyang&city=aba&city=anshun&city=ali&city=ankang&city=akesu&city=artux&city=altay&city=aomen&city=beijing&city=baoding&city=baotou&city=binzhou&city=bengbu&city=baoji&city=bayannaoer&city=benxi&city=baishan&city=baicheng&city=beihai&city=baise&city=bazhong&city=bijie&city=baoshan&city=baiyin&city=bozhou&city=boertalamongol&city=bayangolmongol&city=changsha&city=chongqing&city=chengdu&city=changchun&city=changzhou&city=chengde&city=cangzhou&city=changzhi&city=chifeng&city=chaoyang&city=chuzhou&city=chaohu&city=chizhou&city=chenzhou&city=chaozhou&city=chuxiong&city=changdu&city=changji&city=changde&city=chongzuo&city=cixi&city=dalian&city=dongguan&city=daqing&city=dongying&city=dezhou&city=datong&city=dandong&city=daxinganling&city=deyang&city=dazhou&city=dali&city=diqing&city=dingxi&city=daidehongjingpo&city=eerduosi&city=emeishan&city=foshan&city=fuzhou&city=fushun&city=fuxin&city=fuyang&city=fuzhou1&city=fangchenggang&city=guangzhou&city=guilin&city=guiyang&city=ganzhou&city=guigang&city=gannan&city=guoluo&city=guyuan&city=guangyuan&city=guangan&city=ganzizhou&city=hangzhou&city=hefei&city=haerbin&city=hohhot&city=haikou&city=huizhou&city=handan&city=huangshi&city=huzhou&city=hegang&city=huaian&city=hengshui&city=hulunbeier&city=huludao&city=heihe&city=huainan&city=huaibei&city=hebi&city=hengyang&city=huaihua&city=heyuan&city=hezhou&city=hechi&city=honghe&city=hanzhong&city=haidong&city=haibei&city=huangnan&city=haixi&city=hami&city=heze&city=huangshan&city=hainantibetan&city=hetian&city=jinan&city=jiaxing&city=jinhua&city=jiangmen&city=jining&city=jilin&city=jincheng&city=jinzhong&city=jinzhou&city=jixi&city=jiamusi&city=jian&city=jiaozuo&city=jingmen&city=jieyang&city=jiayuguan&city=jinchang&city=jiuquan&city=jindezhen&city=jiujiang&city=kunming&city=kunshan&city=kaifeng&city=kelamayi&city=kashgar&city=lanzhou&city=linyi&city=luoyang&city=lianyungang&city=liaocheng&city=langfang&city=liuzhou&city=linfen&city=lvliang&city=liaoyang&city=liaoyuan&city=lishui&city=liuan&city=longyan&city=laiwu&city=luohe&city=loudi&city=luzhou&city=liangshan&city=liupanshui&city=lijiang&city=lincang&city=lasa&city=linxia&city=laibin&city=longnan&city=leshan&city=maoming&city=mudanjiang&city=maanshan&city=meizhou&city=mianyang&city=meishan&city=nanjing&city=ningbo&city=nanchang&city=nanning&city=nantong&city=nanyang&city=nanping&city=ningde&city=nanchong&city=nujianglisuzu&city=naqu&city=nyingchi&city=neijiang&city=putian&city=panjin&city=pingdingshan&city=puyang&city=panzhihua&city=pingliang&city=pingxiang&city=puer&city=qingdao&city=quanzhou&city=qinhuangdao&city=qiqihar&city=qizhou&city=qitaihe&city=qingyuan&city=qinzhou&city=qianxinan&city=qiannan&city=qujing&city=qingyang&city=qiandongnanmiaodongautonomous&city=quzhou&city=rizhao&city=rikaze&city=shanghai&city=shenzhen&city=suzhou&city=shenyang&city=sanya&city=shantou&city=shijiazhuang&city=shaoxing&city=shuozhou&city=siping&city=shuangyashan&city=suihua&city=suqian&city=suzhou1&city=sanming&city=shangrao&city=sanmenxia&city=shangqiu&city=shiyan&city=suizhou&city=shaoyang&city=shaoguan&city=shanwei&city=suining&city=shannan&city=shizuishan&city=shangluo&city=songyuan&city=tianjin&city=taiyuan&city=tangshan&city=taizhou&city=taizhoux&city=taian&city=tongliao&city=tieling&city=tonghua&city=tongling&city=tongren&city=tongchuan&city=tianshui&city=tulufan&city=tacheng&city=tanggu&city=wuhan&city=wuxi&city=wenzhou&city=weihai&city=weifang&city=wuhu&city=wuhai&city=wulanchabu&city=wuzhou&city=wenshan&city=weinan&city=wuwei&city=wuzhong&city=wulumuqi&city=xian&city=xining&city=xiamen&city=xuzhou&city=xiangtan&city=xuchang&city=xianyang&city=xingtai&city=xingan&city=xilinguole&city=xuancheng&city=xinyu&city=xinxiang&city=xinyang&city=xiangfan&city=xiaogan&city=xiangxi&city=xishuangbanna&city=xianggang&city=yinchuan&city=yantai&city=yancheng&city=yangzhou&city=yichang&city=yueyang&city=yangquan&city=yuncheng&city=yingkou&city=yanbian&city=yichun&city=yingtan&city=yichun1&city=yiyang&city=yongzhou&city=yangjiang&city=yunfu&city=yulin1&city=yibin&city=yuxi&city=yanan&city=yulin&city=yushu&city=yili&city=yaan&city=zhengzhou&city=zhongshan&city=zhoushan&city=zhenjiang&city=zibo&city=zhangzhou&city=zhuzhou&city=zhanjiang&city=zaozhuang&city=zhangjiakou&city=zhoukou&city=zhumadian&city=zhuhai&city=zhaoqing&city=zigong&city=zunyi&city=zhaotong&city=zhangye&city=zhangjiajie&city=zhongwei&city=ziyang";
	
		new HttpClientUtil().getStringByUrl2();
		String key ="庆园丰收鱼庄(海天店)";
		try {
			//key=URLDecoder.decode(key, "UTF-8");
			key=URLEncoder.encode(key,"GB2312");
			System.out.println(key);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//http://image.baidu.com/i?tn=baiduimage&word=%E5%BA%86%E5%9B%AD%E4%B8%B0%E6%94%B6%E9%B1%BC%E5%BA%84%28%E6%B5%B7%E5%A4%A9%E5%BA%97%29&z=0&lm=-1&ct=201326592&cl=2
		String urlq="http://image.baidu.com/i?tn=baiduimage&word="+key+"&z=0&lm=-1&ct=201326592&cl=2";//&city=anyang&city=aba&city=anshun&city=ali&city=ankang&city=akesu&city=artux&city=altay&city=aomen&city=beijing&city=baoding&city=baotou&city=binzhou&city=bengbu&city=baoji&city=bayannaoer&city=benxi&city=baishan&city=baicheng&city=beihai&city=baise&city=bazhong&city=bijie&city=baoshan&city=baiyin&city=bozhou&city=boertalamongol&city=bayangolmongol&city=changsha&city=chongqing&city=chengdu&city=changchun&city=changzhou&city=chengde&city=cangzhou&city=changzhi&city=chifeng&city=chaoyang&city=chuzhou&city=chaohu&city=chizhou&city=chenzhou&city=chaozhou&city=chuxiong&city=changdu&city=changji&city=changde&city=chongzuo&city=cixi&city=dalian&city=dongguan&city=daqing&city=dongying&city=dezhou&city=datong&city=dandong&city=daxinganling&city=deyang&city=dazhou&city=dali&city=diqing&city=dingxi&city=daidehongjingpo&city=eerduosi&city=emeishan&city=foshan&city=fuzhou&city=fushun&city=fuxin&city=fuyang&city=fuzhou1&city=fangchenggang&city=guangzhou&city=guilin&city=guiyang&city=ganzhou&city=guigang&city=gannan&city=guoluo&city=guyuan&city=guangyuan&city=guangan&city=ganzizhou&city=hangzhou&city=hefei&city=haerbin&city=hohhot&city=haikou&city=huizhou&city=handan&city=huangshi&city=huzhou&city=hegang&city=huaian&city=hengshui&city=hulunbeier&city=huludao&city=heihe&city=huainan&city=huaibei&city=hebi&city=hengyang&city=huaihua&city=heyuan&city=hezhou&city=hechi&city=honghe&city=hanzhong&city=haidong&city=haibei&city=huangnan&city=haixi&city=hami&city=heze&city=huangshan&city=hainantibetan&city=hetian&city=jinan&city=jiaxing&city=jinhua&city=jiangmen&city=jining&city=jilin&city=jincheng&city=jinzhong&city=jinzhou&city=jixi&city=jiamusi&city=jian&city=jiaozuo&city=jingmen&city=jieyang&city=jiayuguan&city=jinchang&city=jiuquan&city=jindezhen&city=jiujiang&city=kunming&city=kunshan&city=kaifeng&city=kelamayi&city=kashgar&city=lanzhou&city=linyi&city=luoyang&city=lianyungang&city=liaocheng&city=langfang&city=liuzhou&city=linfen&city=lvliang&city=liaoyang&city=liaoyuan&city=lishui&city=liuan&city=longyan&city=laiwu&city=luohe&city=loudi&city=luzhou&city=liangshan&city=liupanshui&city=lijiang&city=lincang&city=lasa&city=linxia&city=laibin&city=longnan&city=leshan&city=maoming&city=mudanjiang&city=maanshan&city=meizhou&city=mianyang&city=meishan&city=nanjing&city=ningbo&city=nanchang&city=nanning&city=nantong&city=nanyang&city=nanping&city=ningde&city=nanchong&city=nujianglisuzu&city=naqu&city=nyingchi&city=neijiang&city=putian&city=panjin&city=pingdingshan&city=puyang&city=panzhihua&city=pingliang&city=pingxiang&city=puer&city=qingdao&city=quanzhou&city=qinhuangdao&city=qiqihar&city=qizhou&city=qitaihe&city=qingyuan&city=qinzhou&city=qianxinan&city=qiannan&city=qujing&city=qingyang&city=qiandongnanmiaodongautonomous&city=quzhou&city=rizhao&city=rikaze&city=shanghai&city=shenzhen&city=suzhou&city=shenyang&city=sanya&city=shantou&city=shijiazhuang&city=shaoxing&city=shuozhou&city=siping&city=shuangyashan&city=suihua&city=suqian&city=suzhou1&city=sanming&city=shangrao&city=sanmenxia&city=shangqiu&city=shiyan&city=suizhou&city=shaoyang&city=shaoguan&city=shanwei&city=suining&city=shannan&city=shizuishan&city=shangluo&city=songyuan&city=tianjin&city=taiyuan&city=tangshan&city=taizhou&city=taizhoux&city=taian&city=tongliao&city=tieling&city=tonghua&city=tongling&city=tongren&city=tongchuan&city=tianshui&city=tulufan&city=tacheng&city=tanggu&city=wuhan&city=wuxi&city=wenzhou&city=weihai&city=weifang&city=wuhu&city=wuhai&city=wulanchabu&city=wuzhou&city=wenshan&city=weinan&city=wuwei&city=wuzhong&city=wulumuqi&city=xian&city=xining&city=xiamen&city=xuzhou&city=xiangtan&city=xuchang&city=xianyang&city=xingtai&city=xingan&city=xilinguole&city=xuancheng&city=xinyu&city=xinxiang&city=xinyang&city=xiangfan&city=xiaogan&city=xiangxi&city=xishuangbanna&city=xianggang&city=yinchuan&city=yantai&city=yancheng&city=yangzhou&city=yichang&city=yueyang&city=yangquan&city=yuncheng&city=yingkou&city=yanbian&city=yichun&city=yingtan&city=yichun1&city=yiyang&city=yongzhou&city=yangjiang&city=yunfu&city=yulin1&city=yibin&city=yuxi&city=yanan&city=yulin&city=yushu&city=yili&city=yaan&city=zhengzhou&city=zhongshan&city=zhoushan&city=zhenjiang&city=zibo&city=zhangzhou&city=zhuzhou&city=zhanjiang&city=zaozhuang&city=zhangjiakou&city=zhoukou&city=zhumadian&city=zhuhai&city=zhaoqing&city=zigong&city=zunyi&city=zhaotong&city=zhangye&city=zhangjiajie&city=zhongwei&city=ziyang";
		
		long startime = System.currentTimeMillis();
		/* 1 生成 HttpClinet 对象并设置参数*/
		HttpClient httpClient=new HttpClient();
		//设置代理服务器的ip地址和端口  
		httpClient.getHostConfiguration().setProxy("221.12.169.242 ", Integer.valueOf("8080"));  
				//使用抢先认证  
		httpClient.getParams().setAuthenticationPreemptive(true);  
		//设置 Http 连接超时为5秒
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
		
		GetMethod getMethod = new GetMethod(urlq);
		//设置 get 请求超时为 5 秒
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,SOCKET_TIMEOUT);
		//设置请求重试处理，用的是默认的重试处理：请求三次
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
		          new DefaultHttpMethodRetryHandler());
		getMethod.setRequestHeader("Connection", "close"); //解决 linux下大量close_wait
		try {
			//执行链接
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "+ getMethod.getStatusLine());
			}
			InputStream in = getMethod.getResponseBodyAsStream();
			
			File tmp = new File("F:/tuan/"+key+".html");
			if (!tmp.exists()) {
				//tmp.mkdirs();//创建目录
				tmp.createNewFile(); //创建文件
			}
			
			FileOutputStream out = new FileOutputStream(tmp);
			int b1 = 0;

		 	while ((b1 = in.read( )) != -1) {
				out.write(b1);
			}
			in.close();
			out.flush();
			out.close();
			long lasttime =System.currentTimeMillis();
			
			System.out.println(lasttime -startime );
			// 读取内容
			// byte[] responseBody = getMethod.getResponseBody();
			// 处理内容
			// String html = new String(responseBody);
			// System.out.println(html);
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			System.out.println("Please check your provided http address!");
			e.printStackTrace();
		} catch (IOException e) {
			// 发生网络异常
			e.printStackTrace();
		} finally {
			/* 6 .释放连接 */
			getMethod.releaseConnection();
		}
	}
	
	public static String getStringByUrl(String get_url,String cookie,boolean isProxy){
		return getStringByUrl(null,  get_url,charSet,null, cookie,isProxy,true);
	}
	
	public static String getStringByUrl(String get_url,boolean isProxy){
		return getStringByUrl(null,  get_url,charSet,null, null,isProxy,true);
	}
	
	public static String getStringByUrl(HttpClient httpClient,String get_url,String referer,boolean isProxy){
		return getStringByUrl(httpClient,  get_url,charSet, referer,null,isProxy,true);
	}
	
	public static String getStringByUrl(HttpClient httpClient,String get_url,String referer,boolean isProxy,boolean isChangeProxy){
		return getStringByUrl(httpClient,  get_url,charSet, referer,null,isProxy,isChangeProxy);
	}
	
	private static long lastTime=0l;
	
	public static Boolean changeProxy(){
		try {
			long thisTime  = System.currentTimeMillis();
			/*if((thisTime -lastTime) <= (1000*60) ){//一分钟换一次%E6%B8%A9%E5%B7%9E
				return true;
			}*/
			String url ="http://www.71https.com/getapi.ashx?ddh=784661816645969&num=1&area="+URLEncoder.encode("温州", "UTF-8")+"&yys=1&am=0&mt=6&fm=text&filter=on";
			String ip_port= getStringByUrl(url,false); 
			if(null != ip_port){
				ip_port =ip_port.replace("\r\n", "").trim();
			}
			hosts = ip_port.split(":")[0];
			port = ip_port.split(":")[1];
			System.out.println("IP : "+hosts +"   port :"+port);
			lastTime=System.currentTimeMillis();
			return true;
		} catch (Exception e) { 
			e.printStackTrace();
		}
		return false;
	}
	 
	
	public static String post(String uri,Map<String,String> param){ 
	    HttpClient httpClient = new HttpClient();
	    httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
	    PostMethod postMethod = new PostMethod(uri);
	    postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,SOCKET_TIMEOUT);
	    postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler());
	    Iterator<Entry<String,String>> it=  param.entrySet().iterator();
	    while(it.hasNext()){
		 Entry<String ,String> entry =it.next();
		 postMethod.addParameter(entry.getKey(), entry.getValue());
	    }
	    try {
		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		
		int statusCode = httpClient.executeMethod(postMethod);
		if (statusCode != HttpStatus.SC_OK) {
			System.err.println("Method failed: "+ postMethod.getStatusLine());
			return "";
		}
		InputStream in = postMethod.getResponseBodyAsStream();
		int b1 = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
	 	while ((b1 = in.read()) != -1) {
			out.write(b1);
		}
	 	byte[] b = out.toByteArray();
	 	String htm  = new String(b,charSet);
		in.close();
		out.flush();
		out.close();
		return  htm;
	    } catch (HttpException e) { 
		e.printStackTrace();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    return "";
	    
	}
	
	public static String getStringByUrl(HttpClient httpClient, String get_url,String charSet,String referer,String cookie,boolean isProxy,boolean isChangeProxy){ 
		if(null==httpClient){
			httpClient=new HttpClient();
		}
		if(null == get_url ){
		    return "";
		}
		if(get_url.startsWith("//")){ //规则修改
		    get_url="http:"+get_url;
		} 
		
		if(isProxy){
			if(isChangeProxy){
				if(changeProxy()){
					//httpClient.getState().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM), new UsernamePasswordCredentials(username, password));
					//httpClient.getParams().setAuthenticationPreemptive(true);
					//设置代理服务器的ip地址和端口  
					httpClient.getHostConfiguration().setProxy(hosts, Integer.valueOf(port));  
					//使用抢先认证
					httpClient.getParams().setAuthenticationPreemptive(true);  
				}
			}else{
				httpClient.getHostConfiguration().setProxy(hosts, Integer.valueOf(port));  
				//使用抢先认证
				httpClient.getParams().setAuthenticationPreemptive(true);  
			}
		}
		//设置 Http 连接超时为5秒
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
		GetMethod getMethod = new GetMethod(get_url);
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,SOCKET_TIMEOUT);
		//设置请求重试处理，用的是默认的重试处理：请求三次
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler());
		//压制警告
		//getMethod.getParams().setParameter("http.protocol.cookie-policy",CookiePolicy.BROWSER_COMPATIBILITY); 
		//getMethod.setRequestHeader("Connection", "keep-alive"); //解决 linux下大量close_wait
		
		if(null !=referer){
			getMethod.setRequestHeader("Referer", referer); 
		}
		if(null!=cookie){
			getMethod.setRequestHeader("Cookie", cookie);
			
		}
		//getMethod.setRequestHeader("Accept", "*/*");
		/*getMethod.setRequestHeader("Accept-Encoding", "gzip,deflate,sdch");
		getMethod.setRequestHeader("Accept-Language", "zh-CN,zh;q=0.8");
		getMethod.setRequestHeader("Cache-Control", "max-age=0");
		getMethod.setRequestHeader("Connection", "keep-alive");
		
		getMethod.setRequestHeader("Cookie", "cna=/rB5DAErAnECAXPrUozuQsfO; uc3=nk2=2%2BSCwJIRL5sNxUUMwzY%3D&id2=W8COuyoC%2BqIb&vt3=F8dATSPfPsxGtJaanwA%3D&lg2=VT5L2FSpMGV7TQ%3D%3D; lgc=%5Cu76D6%5Cu8FC7%5Cu5929%5Cu7A7A%5Cu7684%5Cu68A6%5Cu60F3; tracknick=%5Cu76D6%5Cu8FC7%5Cu5929%5Cu7A7A%5Cu7684%5Cu68A6%5Cu60F3; _cc_=UtASsssmfA%3D%3D; tg=0; v=0; cookie2=1bbbe2d11369a19424532ddb9892f411; mt=ci=-1_0; t=141d3ecd573316521856607fc8109396; isg=03188150168D602D80FD4F0C930557B2; x=e%3D1%26p%3D*%26s%3D0%26c%3D0%26f%3D0%26g%3D0%26t%3D0%26__ll%3D-1%26_ato%3D0; _tb_token_=e9806f7661eb3; uc1=cookie14=UoW3twAhNrXSSg%3D%3D; ucn=center");
		getMethod.setRequestHeader("Host", "mdskip.taobao.com");
		getMethod.setRequestHeader("Referer", "http://detail.tmall.com/item.htm?spm=a1z10.1.w8272611-3101332884.2.VPz9iw&id=40664000413");  
		*/ 
		
		//getMethod.setRequestHeader("Accept",  "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		//getMethod.setRequestHeader("Accept-Encoding", "gzip, deflate, sdch");*********
		//getMethod.setRequestHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
		//getMethod.setRequestHeader("Cache-Control", "max-age=0");
		//getMethod.setRequestHeader("Connection", "keep-alive");
		//getMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36");
		getMethod.setRequestHeader("User-Agent", WEIXIN);
		
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "+ getMethod.getStatusLine());
				return "";
			}
			InputStream in = getMethod.getResponseBodyAsStream();
			int b1 = 0;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
		 	while ((b1 = in.read()) != -1) {
				out.write(b1);
			}
		 	byte[] b = out.toByteArray();
		 	String htm  = new String(b,charSet);
			in.close();
			out.flush();
			out.close();
			return htm;
		} catch (HttpException e) { 
			e.printStackTrace();
		} catch (Exception e) { 
			e.printStackTrace();
		}finally{
			if(null != getMethod){
				getMethod.releaseConnection();
			}
		}
		
		return "";
		
	}
	
	@Deprecated
	@Test
	public  String getStringByUrl(){
		
		String key ="庆园丰收鱼庄(海天店)";
		try {
			//key=URLDecoder.decode(key, "UTF-8");
			
			key=URLEncoder.encode(key,"GB2312");
			System.out.println(key);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//http://image.baidu.com/i?tn=baiduimage&word=%E5%BA%86%E5%9B%AD%E4%B8%B0%E6%94%B6%E9%B1%BC%E5%BA%84%28%E6%B5%B7%E5%A4%A9%E5%BA%97%29&z=0&lm=-1&ct=201326592&cl=2
		String get_url="http://image.baidu.com/i?tn=baiduimage&word="+key+"&z=0&lm=-1&ct=201326592&cl=2";//&city=anyang&city=aba&city=anshun&city=ali&city=ankang&city=akesu&city=artux&city=altay&city=aomen&city=beijing&city=baoding&city=baotou&city=binzhou&city=bengbu&city=baoji&city=bayannaoer&city=benxi&city=baishan&city=baicheng&city=beihai&city=baise&city=bazhong&city=bijie&city=baoshan&city=baiyin&city=bozhou&city=boertalamongol&city=bayangolmongol&city=changsha&city=chongqing&city=chengdu&city=changchun&city=changzhou&city=chengde&city=cangzhou&city=changzhi&city=chifeng&city=chaoyang&city=chuzhou&city=chaohu&city=chizhou&city=chenzhou&city=chaozhou&city=chuxiong&city=changdu&city=changji&city=changde&city=chongzuo&city=cixi&city=dalian&city=dongguan&city=daqing&city=dongying&city=dezhou&city=datong&city=dandong&city=daxinganling&city=deyang&city=dazhou&city=dali&city=diqing&city=dingxi&city=daidehongjingpo&city=eerduosi&city=emeishan&city=foshan&city=fuzhou&city=fushun&city=fuxin&city=fuyang&city=fuzhou1&city=fangchenggang&city=guangzhou&city=guilin&city=guiyang&city=ganzhou&city=guigang&city=gannan&city=guoluo&city=guyuan&city=guangyuan&city=guangan&city=ganzizhou&city=hangzhou&city=hefei&city=haerbin&city=hohhot&city=haikou&city=huizhou&city=handan&city=huangshi&city=huzhou&city=hegang&city=huaian&city=hengshui&city=hulunbeier&city=huludao&city=heihe&city=huainan&city=huaibei&city=hebi&city=hengyang&city=huaihua&city=heyuan&city=hezhou&city=hechi&city=honghe&city=hanzhong&city=haidong&city=haibei&city=huangnan&city=haixi&city=hami&city=heze&city=huangshan&city=hainantibetan&city=hetian&city=jinan&city=jiaxing&city=jinhua&city=jiangmen&city=jining&city=jilin&city=jincheng&city=jinzhong&city=jinzhou&city=jixi&city=jiamusi&city=jian&city=jiaozuo&city=jingmen&city=jieyang&city=jiayuguan&city=jinchang&city=jiuquan&city=jindezhen&city=jiujiang&city=kunming&city=kunshan&city=kaifeng&city=kelamayi&city=kashgar&city=lanzhou&city=linyi&city=luoyang&city=lianyungang&city=liaocheng&city=langfang&city=liuzhou&city=linfen&city=lvliang&city=liaoyang&city=liaoyuan&city=lishui&city=liuan&city=longyan&city=laiwu&city=luohe&city=loudi&city=luzhou&city=liangshan&city=liupanshui&city=lijiang&city=lincang&city=lasa&city=linxia&city=laibin&city=longnan&city=leshan&city=maoming&city=mudanjiang&city=maanshan&city=meizhou&city=mianyang&city=meishan&city=nanjing&city=ningbo&city=nanchang&city=nanning&city=nantong&city=nanyang&city=nanping&city=ningde&city=nanchong&city=nujianglisuzu&city=naqu&city=nyingchi&city=neijiang&city=putian&city=panjin&city=pingdingshan&city=puyang&city=panzhihua&city=pingliang&city=pingxiang&city=puer&city=qingdao&city=quanzhou&city=qinhuangdao&city=qiqihar&city=qizhou&city=qitaihe&city=qingyuan&city=qinzhou&city=qianxinan&city=qiannan&city=qujing&city=qingyang&city=qiandongnanmiaodongautonomous&city=quzhou&city=rizhao&city=rikaze&city=shanghai&city=shenzhen&city=suzhou&city=shenyang&city=sanya&city=shantou&city=shijiazhuang&city=shaoxing&city=shuozhou&city=siping&city=shuangyashan&city=suihua&city=suqian&city=suzhou1&city=sanming&city=shangrao&city=sanmenxia&city=shangqiu&city=shiyan&city=suizhou&city=shaoyang&city=shaoguan&city=shanwei&city=suining&city=shannan&city=shizuishan&city=shangluo&city=songyuan&city=tianjin&city=taiyuan&city=tangshan&city=taizhou&city=taizhoux&city=taian&city=tongliao&city=tieling&city=tonghua&city=tongling&city=tongren&city=tongchuan&city=tianshui&city=tulufan&city=tacheng&city=tanggu&city=wuhan&city=wuxi&city=wenzhou&city=weihai&city=weifang&city=wuhu&city=wuhai&city=wulanchabu&city=wuzhou&city=wenshan&city=weinan&city=wuwei&city=wuzhong&city=wulumuqi&city=xian&city=xining&city=xiamen&city=xuzhou&city=xiangtan&city=xuchang&city=xianyang&city=xingtai&city=xingan&city=xilinguole&city=xuancheng&city=xinyu&city=xinxiang&city=xinyang&city=xiangfan&city=xiaogan&city=xiangxi&city=xishuangbanna&city=xianggang&city=yinchuan&city=yantai&city=yancheng&city=yangzhou&city=yichang&city=yueyang&city=yangquan&city=yuncheng&city=yingkou&city=yanbian&city=yichun&city=yingtan&city=yichun1&city=yiyang&city=yongzhou&city=yangjiang&city=yunfu&city=yulin1&city=yibin&city=yuxi&city=yanan&city=yulin&city=yushu&city=yili&city=yaan&city=zhengzhou&city=zhongshan&city=zhoushan&city=zhenjiang&city=zibo&city=zhangzhou&city=zhuzhou&city=zhanjiang&city=zaozhuang&city=zhangjiakou&city=zhoukou&city=zhumadian&city=zhuhai&city=zhaoqing&city=zigong&city=zunyi&city=zhaotong&city=zhangye&city=zhangjiajie&city=zhongwei&city=ziyang";
	
		HttpClient httpClient=new HttpClient();
		 
			//设置代理服务器的ip地址和端口  
		httpClient.getHostConfiguration().setProxy(hosts, Integer.valueOf(port));  
			//使用抢先认证  
		httpClient.getParams().setAuthenticationPreemptive(true);  
		 
		//设置 Http 连接超时为5秒
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
		GetMethod getMethod = new GetMethod(get_url);
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,SOCKET_TIMEOUT);
		//设置请求重试处理，用的是默认的重试处理：请求三次
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
		          new DefaultHttpMethodRetryHandler());
		//压制警告
		//host minus domain may not contain any dots
		getMethod.getParams().setParameter("http.protocol.cookie-policy",CookiePolicy.BROWSER_COMPATIBILITY);
		getMethod.setRequestHeader("Connection", "close"); //解决 linux下大量close_wait
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "+ getMethod.getStatusLine());
			}
			InputStream in = getMethod.getResponseBodyAsStream();
			int b1 = 0;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
		 	while ((b1 = in.read()) != -1) {
				out.write(b1);
			}
		 	byte[] b = out.toByteArray();
		 	String htm  = new String(b,"UTF-8");
			in.close();
			out.flush();
			out.close();
			 
			return htm;
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(null != getMethod){
				getMethod.releaseConnection();
			}
		}
		
		return "";
		
	}
	
	@Deprecated
	public static String getStringByUrl2(String get_url,boolean isProxy){ 
		if(isProxy){ 
			if(changeProxy()){
				 System.setProperty("http.proxySet", "true");
				 System.setProperty("http.proxyHost", hosts); 
				 System.setProperty("http.proxyPort", port);
			}
		   
		} 
		if(get_url.startsWith("//")){
		    get_url="http:"+get_url;
		}
		HttpURLConnection httpCon = null;
		try {
			URL url = new URL(get_url);
			httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod("GET");
			httpCon.setConnectTimeout(CONNECTION_TIMEOUT);
			httpCon.setReadTimeout(SOCKET_TIMEOUT);
			httpCon.setDoInput(true);
			
			//httpCon.setRequestProperty("Accept", "*/*");
			/*httpCon.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
			httpCon.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
			httpCon.setRequestProperty("Cache-Control", "max-age=0");
			httpCon.setRequestProperty("Connection", "keep-alive"); 
			httpCon.setRequestProperty("Cookie", "cna=/rB5DAErAnECAXPrUozuQsfO; uc3=nk2=2%2BSCwJIRL5sNxUUMwzY%3D&id2=W8COuyoC%2BqIb&vt3=F8dATSPfPsxGtJaanwA%3D&lg2=VT5L2FSpMGV7TQ%3D%3D; lgc=%5Cu76D6%5Cu8FC7%5Cu5929%5Cu7A7A%5Cu7684%5Cu68A6%5Cu60F3; tracknick=%5Cu76D6%5Cu8FC7%5Cu5929%5Cu7A7A%5Cu7684%5Cu68A6%5Cu60F3; _cc_=UtASsssmfA%3D%3D; tg=0; v=0; cookie2=1bbbe2d11369a19424532ddb9892f411; mt=ci=-1_0; t=141d3ecd573316521856607fc8109396; isg=03188150168D602D80FD4F0C930557B2; x=e%3D1%26p%3D*%26s%3D0%26c%3D0%26f%3D0%26g%3D0%26t%3D0%26__ll%3D-1%26_ato%3D0; _tb_token_=e9806f7661eb3; uc1=cookie14=UoW3twAhNrXSSg%3D%3D; ucn=center");
			httpCon.setRequestProperty("Host", "mdskip.taobao.com");
			httpCon.setRequestProperty("Referer", "http://detail.tmall.com/item.htm?spm=a1z10.1.w8272611-3101332884.2.VPz9iw&id=40664000413"); 
			httpCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.72 Safari/537.36");
			*/
			if(httpCon .getResponseCode() == 200){
				InputStream in = httpCon.getInputStream();
				int b1 = 0;
				ByteArrayOutputStream out = new ByteArrayOutputStream();
			 	while ((b1 = in.read()) != -1) {
					out.write(b1);
				}
			 	String htm  = new String(out.toByteArray(),"GBK");
			 	in.close();
				out.flush();
				out.close();
				return htm;
			}
		} catch (Exception e) {
			System.out.println("连接超时");
		} finally{
			if(null != httpCon){
				httpCon.disconnect();
			}
		}
		return "";
	} 
	
	@Deprecated
	@Test
	public void getStringByUrl2( ){
		String key ="庆园丰收鱼庄(海天店)";
		try {
			//key=URLDecoder.decode(key, "UTF-8");
			key=URLEncoder.encode(key,"GB2312");
			System.out.println(key);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//http://image.baidu.com/i?tn=baiduimage&word=%E5%BA%86%E5%9B%AD%E4%B8%B0%E6%94%B6%E9%B1%BC%E5%BA%84%28%E6%B5%B7%E5%A4%A9%E5%BA%97%29&z=0&lm=-1&ct=201326592&cl=2
		String get_url="http://image.baidu.com/i?tn=baiduimage&word="+key+"&z=0&lm=-1&ct=201326592&cl=2";//&city=anyang&city=aba&city=anshun&city=ali&city=ankang&city=akesu&city=artux&city=altay&city=aomen&city=beijing&city=baoding&city=baotou&city=binzhou&city=bengbu&city=baoji&city=bayannaoer&city=benxi&city=baishan&city=baicheng&city=beihai&city=baise&city=bazhong&city=bijie&city=baoshan&city=baiyin&city=bozhou&city=boertalamongol&city=bayangolmongol&city=changsha&city=chongqing&city=chengdu&city=changchun&city=changzhou&city=chengde&city=cangzhou&city=changzhi&city=chifeng&city=chaoyang&city=chuzhou&city=chaohu&city=chizhou&city=chenzhou&city=chaozhou&city=chuxiong&city=changdu&city=changji&city=changde&city=chongzuo&city=cixi&city=dalian&city=dongguan&city=daqing&city=dongying&city=dezhou&city=datong&city=dandong&city=daxinganling&city=deyang&city=dazhou&city=dali&city=diqing&city=dingxi&city=daidehongjingpo&city=eerduosi&city=emeishan&city=foshan&city=fuzhou&city=fushun&city=fuxin&city=fuyang&city=fuzhou1&city=fangchenggang&city=guangzhou&city=guilin&city=guiyang&city=ganzhou&city=guigang&city=gannan&city=guoluo&city=guyuan&city=guangyuan&city=guangan&city=ganzizhou&city=hangzhou&city=hefei&city=haerbin&city=hohhot&city=haikou&city=huizhou&city=handan&city=huangshi&city=huzhou&city=hegang&city=huaian&city=hengshui&city=hulunbeier&city=huludao&city=heihe&city=huainan&city=huaibei&city=hebi&city=hengyang&city=huaihua&city=heyuan&city=hezhou&city=hechi&city=honghe&city=hanzhong&city=haidong&city=haibei&city=huangnan&city=haixi&city=hami&city=heze&city=huangshan&city=hainantibetan&city=hetian&city=jinan&city=jiaxing&city=jinhua&city=jiangmen&city=jining&city=jilin&city=jincheng&city=jinzhong&city=jinzhou&city=jixi&city=jiamusi&city=jian&city=jiaozuo&city=jingmen&city=jieyang&city=jiayuguan&city=jinchang&city=jiuquan&city=jindezhen&city=jiujiang&city=kunming&city=kunshan&city=kaifeng&city=kelamayi&city=kashgar&city=lanzhou&city=linyi&city=luoyang&city=lianyungang&city=liaocheng&city=langfang&city=liuzhou&city=linfen&city=lvliang&city=liaoyang&city=liaoyuan&city=lishui&city=liuan&city=longyan&city=laiwu&city=luohe&city=loudi&city=luzhou&city=liangshan&city=liupanshui&city=lijiang&city=lincang&city=lasa&city=linxia&city=laibin&city=longnan&city=leshan&city=maoming&city=mudanjiang&city=maanshan&city=meizhou&city=mianyang&city=meishan&city=nanjing&city=ningbo&city=nanchang&city=nanning&city=nantong&city=nanyang&city=nanping&city=ningde&city=nanchong&city=nujianglisuzu&city=naqu&city=nyingchi&city=neijiang&city=putian&city=panjin&city=pingdingshan&city=puyang&city=panzhihua&city=pingliang&city=pingxiang&city=puer&city=qingdao&city=quanzhou&city=qinhuangdao&city=qiqihar&city=qizhou&city=qitaihe&city=qingyuan&city=qinzhou&city=qianxinan&city=qiannan&city=qujing&city=qingyang&city=qiandongnanmiaodongautonomous&city=quzhou&city=rizhao&city=rikaze&city=shanghai&city=shenzhen&city=suzhou&city=shenyang&city=sanya&city=shantou&city=shijiazhuang&city=shaoxing&city=shuozhou&city=siping&city=shuangyashan&city=suihua&city=suqian&city=suzhou1&city=sanming&city=shangrao&city=sanmenxia&city=shangqiu&city=shiyan&city=suizhou&city=shaoyang&city=shaoguan&city=shanwei&city=suining&city=shannan&city=shizuishan&city=shangluo&city=songyuan&city=tianjin&city=taiyuan&city=tangshan&city=taizhou&city=taizhoux&city=taian&city=tongliao&city=tieling&city=tonghua&city=tongling&city=tongren&city=tongchuan&city=tianshui&city=tulufan&city=tacheng&city=tanggu&city=wuhan&city=wuxi&city=wenzhou&city=weihai&city=weifang&city=wuhu&city=wuhai&city=wulanchabu&city=wuzhou&city=wenshan&city=weinan&city=wuwei&city=wuzhong&city=wulumuqi&city=xian&city=xining&city=xiamen&city=xuzhou&city=xiangtan&city=xuchang&city=xianyang&city=xingtai&city=xingan&city=xilinguole&city=xuancheng&city=xinyu&city=xinxiang&city=xinyang&city=xiangfan&city=xiaogan&city=xiangxi&city=xishuangbanna&city=xianggang&city=yinchuan&city=yantai&city=yancheng&city=yangzhou&city=yichang&city=yueyang&city=yangquan&city=yuncheng&city=yingkou&city=yanbian&city=yichun&city=yingtan&city=yichun1&city=yiyang&city=yongzhou&city=yangjiang&city=yunfu&city=yulin1&city=yibin&city=yuxi&city=yanan&city=yulin&city=yushu&city=yili&city=yaan&city=zhengzhou&city=zhongshan&city=zhoushan&city=zhenjiang&city=zibo&city=zhangzhou&city=zhuzhou&city=zhanjiang&city=zaozhuang&city=zhangjiakou&city=zhoukou&city=zhumadian&city=zhuhai&city=zhaoqing&city=zigong&city=zunyi&city=zhaotong&city=zhangye&city=zhangjiajie&city=zhongwei&city=ziyang";
		
		//System.setProperty("http.proxySet", "true");
		System.setProperty("http.proxyHost", "222.73.228.7"); 
		System.setProperty("http.proxyPort", "80");
		HttpURLConnection httpCon = null;
		try {
			URL url = new URL(get_url);
			httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod("GET");
			httpCon.setConnectTimeout(CONNECTION_TIMEOUT);
			httpCon.setReadTimeout(SOCKET_TIMEOUT);
			httpCon.setDoInput(true);
			if(httpCon .getResponseCode() == 200){
				InputStream in = httpCon.getInputStream();
				int b1 = 0;
				ByteArrayOutputStream out = new ByteArrayOutputStream();
			 	while ((b1 = in.read()) != -1) {
					out.write(b1);
				}
			 	String htm  = new String(out.toByteArray(),"UTF-8");
			 	in.close();
				out.flush();
				out.close();
				System.out.println(htm);
				 
			}
		} catch (Exception e) {
			System.out.println("连接超时");
		} finally{
			if(null != httpCon){
				httpCon.disconnect();
			}
		}
		 
	}
	
	
	@Deprecated
	public static File getFilebyUrl(String url,String filename,boolean isProxy){ 
	    	if(null == url ){
		    return null;
		}
	    	if(url.startsWith("//")){
	    	    url="http:"+url;
		}
		/* 1 生成 HttpClinet 对象并设置参数*/
		HttpClient httpClient=new HttpClient(httpConnectionManager);
		if(isProxy){
			if(changeProxy()){
				//设置代理服务器的ip地址和端口  
				httpClient.getHostConfiguration().setProxy(hosts, Integer.valueOf(port));  
				//使用抢先认证  
				httpClient.getParams().setAuthenticationPreemptive(true);  
			}
		}
		//设置 Http 连接超时为5秒
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
		GetMethod getMethod = new GetMethod(url);
		//设置 get 请求超时为 5 秒
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,SOCKET_TIMEOUT);
		//设置请求重试处理，用的是默认的重试处理：请求三次
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler());
		getMethod.setRequestHeader("Connection", "close"); //解决 linux下大量close_wait
		getMethod.getParams().setParameter("http.protocol.cookie-policy",CookiePolicy.BROWSER_COMPATIBILITY); 
		File tmp = new File(filename);
		try {
			if(!tmp.exists()){
				tmp.createNewFile(); //创建文件
			}
			//执行链接
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "+ getMethod.getStatusLine());
			}
			InputStream in = getMethod.getResponseBodyAsStream(); 
			FileOutputStream out = new FileOutputStream(tmp);
			int b1 = 0;
		 	while ((b1 = in.read()) != -1) {
				out.write(b1);
			}
			in.close();
			out.flush();
			out.close(); 
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			tmp=null;
			System.out.println("Please check your provided http address!");
			e.printStackTrace();
		} catch (Exception e) {
			// 发生网络异常
			tmp=null;
			e.printStackTrace(); 
		} finally {
			/* 6 .释放连接 */
			if(null !=getMethod){
				getMethod.releaseConnection();
			}
		} 
		return tmp;
	}
	

	
	public static File getFilebyUrl2(String strUrl,String filename,boolean isProxy){
	    	if(null == strUrl ){
		    return null;
		}
	    	if(strUrl.startsWith("//")){
	    	    strUrl="http:"+strUrl;
		}
		if(isProxy){ 
			if(changeProxy()){
				System.setProperty("http.proxySet", "true");
				 System.setProperty("http.proxyHost", hosts); 
				    System.setProperty("http.proxyPort", port);
			}
		   
		}
		File tmp = null;
		HttpURLConnection httpCon = null;
		try {
			//InputStream errorStream = null;
			URL url = new URL(strUrl);
			httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod("GET");
			httpCon.setConnectTimeout(CONNECTION_TIMEOUT);
			httpCon.setReadTimeout(SOCKET_TIMEOUT);
			httpCon.setDoInput(true);
			//httpCon.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			/*httpCon.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
			httpCon.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
			httpCon.setRequestProperty("Connection", "keep-alive");
			httpCon.setRequestProperty("Cookie", "pgv_pvi=6062742528; access_log=a1ffb5db51e8cc9f655d25984dd1eca2; refer_na=1382503948630/utm_source=baidu; _jzqosr=oD1dwSujjepY; _jzqz=1.1380870797.1382503948.1.jzqsr=29_514145_1021747|jzqct=22052_131763_1135523_10506025_10506025000.-; c_name_VR_tmp=1SMy1zMw1Kqh1nAH1BtN1zHq1HS81dC61HYx157Y1HS41qZf; _jzqy=1.1378998535.1383188161.25.jzqsr=baidu|jzqct=%E7%B3%AF%E7%B1%B3%E5%9B%A2%E8%B4%AD%E7%BD%91.jzqsr=baidu|jzqct=%E5%8D%9A%E5%A1%94%E6%8B%89%E8%92%99%E5%8F%A4%20%E5%8C%BA%E5%8F%B7; key_visit=801e7d692b86b8a24cb593f85881adc6; areaCode=800010000; _jzqx=1.1380870797.1383190558.8.jzqsr=baidu%2Ecom|jzqct=/s.jzqsr=google%2Ecom%2Ehk|jzqct=/; _ga=GA1.2.726298349.1378998535; __utma=1.726298349.1378998535.1383188161.1383190558.31; __utmz=1.1383190558.31.24.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); __utmv=1.|3=%E5%85%B6%E4%BB%96323_%E6%8A%BD%E5%A5%96_683=0=1; _jzqa=1.1452020144501081900.1378998535.1383188161.1383190558.23; _qzja=1.470225309.1378998551853.1383188160728.1383190560292.1383188160728.1383190560292..0.0.39.18; nm_code=0; nm_key=3aa878bd0e79d3555420197e083db8fa.1");
			httpCon.setRequestProperty("Host", "www.nuomi.com");*/
			httpCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.72 Safari/537.36");
			if(httpCon .getResponseCode() == 200){
				InputStream inputStream = httpCon.getInputStream();
				tmp = new File(filename);
				if (!tmp.exists()) {
					//tmp.mkdirs();//创建目录
					tmp.createNewFile(); //创建文件
				}
				FileOutputStream out = new FileOutputStream(tmp);
				
				int b1 = 0;
			 	while ((b1 = inputStream.read()) != -1) {
					out.write(b1);
				}
			 	inputStream.close();
				out.flush();
				out.close();
			}
		} catch (Exception e) { 
			e.printStackTrace();
			return null;
		} finally{
			if(null !=httpCon ){
				httpCon.disconnect();
			}
				
		}
		
		
		return tmp;
	}
	
 
	
	/**
	 * 
	 * @param url
	 * @param parts
	 * @return
	 */
	public static String uploadFile(String url,Part[] parts){ 
		HttpClient client = new HttpClient(httpConnectionManager);
		PostMethod postMethod = new PostMethod(url); 
		try { 
			// FilePart：用来上传文件的类 
			// 对于MIME类型的请求，httpclient建议全用MulitPartRequestEntity进行包装 
			MultipartRequestEntity mre = new MultipartRequestEntity(parts,	postMethod.getParams()); 
			postMethod.setRequestEntity(mre); 
			client.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);// 设置连接时间
			int status = client.executeMethod(postMethod);
			if (status == HttpStatus.SC_OK) {
				String responseStr = postMethod.getResponseBodyAsString();
				if(null==responseStr || "".equals(responseStr.trim())){
					return null;
				}
				return responseStr;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 释放连接 
			postMethod.releaseConnection(); 
		}
		return null;
	}
	
	/**
	 * 文件上传类
	 * @param url 
	 * @param file 文件
	 * @param String fileName 文件名  (格式: "1.png" )
	 * @param String filePath 文件存储的路径 ( 格式: "32131/12312/")
	 * @param String fileType 文件类型  (格式: "png" ) 
	 * FileUpload up=(FileUpload) JSONObject.toBean(jb, FileUpload.class); 
	 * @return
	 */
	public static String uploadFile(String url,File file,String fileName ){
		if (!file.exists()) {
			return null;
		}
		// FilePart：用来上传文件的类
		try {
			FilePart fp = new FilePart(fileName, file);
			//StringPart isCompress=new StringPart("isCompress", "false"); 
			StringPart isCompre = new StringPart("isCompre","2");
			Part[] parts = {fp,isCompre};
			//解析json
			String s= uploadFile(url,parts);  
			//
			JSONObject jsonObj = JSON.parseObject(s); 
			int code =jsonObj.getInteger("code"); 
			if(code==1){
			    JSONObject jsonData= jsonObj.getJSONObject("data");
			    String resultUrl = jsonData.getString("url"); 
			    return resultUrl;
			}
		} catch (Exception e) { 
			e.printStackTrace();
		}  
		return null;
	}
	
	
	
	/**
	 * 文件上传类
	 * @param url 
	 * @param file 文件
	 * @param String fileName 文件名  (格式: "1.png" )
	 * @param String filePath 文件存储的路径 ( 格式: "32131/12312/")
	 * @param String fileType 文件类型  (格式: "png" )
	 * 
	 * FileUpload up=(FileUpload) JSONObject.toBean(jb, FileUpload.class); 
	 * @return
	 */
	public static String uploadToWiShangquan(String url,File file){
		if (!file.exists()) {
			return null;
		} 
		// FilePart：用来上传文件的类
		try {
			FilePart fp = new FilePart("file", file);
			//StringPart isCompress=new StringPart("isCompress", "false");
			
			StringPart isCompre = new StringPart("isCompre","2"); 
			StringPart isCutImage = new StringPart("isCutImage","1");
			
			Part[] parts = {fp,isCompre,isCutImage};
			//解析json
			String s= uploadFile(url,parts); 
			JSONObject fileUpload =JSONObject.parseObject(s);
			
			 
			if(null == fileUpload){
				return null;
			}
			if(fileUpload.getInteger("code")==1){
			    JSONObject data = fileUpload.getJSONObject("data");
			    return data.getString("url");
			}
		} catch (Exception e) { 
			e.printStackTrace();
		}  
		return null;
	}
	
	
	
	
}
