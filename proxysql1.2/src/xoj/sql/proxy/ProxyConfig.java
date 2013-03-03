package xoj.sql.proxy;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * <pre>代理参数类
 * 该类实例中保存了某一个代理配置文件中的所有信息
 * <p>Copyright (c) 2006 CozyWikiup shanghai</p>
 * @author 沉香
 * @version 1.1
 */
public class ProxyConfig{
  private String driver=new String();
  private String url=new String();
  private String encoding=null;
  private Properties jdbcProperties=new Properties();

  /**
   * 对从java虚拟机传送到数据库的字符串进行转码
   * @param s 操作的字符串
   * @return 转码后的字符串
   */
  public String convertJVM2DB(String s){
    if(s==null) return s;
    if(encoding!=null) try{
      s=new String(s.getBytes(encoding),"ISO-8859-1");
    }catch(UnsupportedEncodingException e){
    }
    return s;
  }

  /**
   * 对从数据库获得的字符串进行转码
   * @param s 操作的字符串
   * @return 转码后的字符串
   */
  public String convertDB2JVM(String s){
    if(s==null) return s;
    if(encoding!=null) try{
      s=new String(s.getBytes("ISO-8859-1"),encoding);
    }catch(UnsupportedEncodingException e){
    }
    return s;
  }

  /**
   * 向配置信息中加入一个额外的集
   * @param properties 参数集
   */
  public void addJdbcProperties(Properties properties){
    Enumeration enumeration=properties.propertyNames();
    while(enumeration.hasMoreElements()){
      String key=(String)enumeration.nextElement();
      if(key==null) continue;
      String value=properties.getProperty(key);
      if(value==null||value.length()==0)continue;
      this.setJdbcProperty(key,value);
    }
  }

  /**
   * 取的要送往数据库的参数集
   * @return 参数集
   */
  public Properties getJdbcProperties(){
    Properties properties=new Properties();
    Enumeration enumeration=jdbcProperties.propertyNames();
    while(enumeration.hasMoreElements()){
      String key=(String)enumeration.nextElement();
      String value=jdbcProperties.getProperty(key);
      if(key!=null&&value!=null) properties.put(key,value);
    }
    return properties;
  }

  /**
   * 取得配置信息中某一个额外的数据库参数
   * @param key 参数名
   * @return 参数值
   */
  public String getJdbcProperty(String key){
    return jdbcProperties.getProperty(key);
  }

  /**
   * 设置配置信息中某一个额外的数据库参数，特定的几个参数：<br>
   * proxy.driver<br>
   * proxy.url<br>
   * proxy.encoding<br>
   * 将被自动存入特定的位置，而不会被送入数据库JDBC驱动
   */
  public void setJdbcProperty(String key,String value){
    if(key==null||value==null) return;
    if("proxy.driver".equalsIgnoreCase(key)) this.setDriver(value);
    else if("proxy.url".equalsIgnoreCase(key)) this.setUrl(value);
    else if("proxy.encoding".equalsIgnoreCase(key)) this.setEncoding(value);
    else jdbcProperties.setProperty(key,value);
  }

  /**
   * 取得配置信息中数据库的编码
   * @return 数据库编码
   */
  public String getEncoding(){
    return this.encoding;
  }

  /**
   * 设置配置信息中数据库的编码
   * @param encoding
   */
  public void setEncoding(String encoding){
    this.encoding=encoding;
  }

  /**
   * 返回配置信息中指定的数据库驱动程序类名
   * @return 驱动类名
   */
  public String getDriver(){
    return driver;
  }

  /**
   * 设置配置信息中数据库驱动程序类名
   * @param driver 驱动类名
   */
  public void setDriver(String driver){
    this.driver=driver;
  }

  /**
   * 取得配置信息中数据库的URL
   * @return 数据库URL字符串
   */
  public String getUrl(){
    return url;
  }

  /**
   * 设置配置信息中数据库的URL
   * @param url 数据库URL字符茶
   */
  public void setUrl(String url){
    this.url=url;
  }
}
