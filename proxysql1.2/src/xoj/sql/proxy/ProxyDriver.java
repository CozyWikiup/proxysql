package xoj.sql.proxy;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * <p>
 * JDBC连接驱动代理
 * </p>
 * 
 * <pre>
 * 以驱动的方式提供给使用方，在底层实现连接的代理管理功能，向使用者隐藏
 * 实现过程以及无缝的过渡
 * 你需要了解下列参数：
 * proxy.driver= （必须）实际要连接到的数据库驱动程序，当然该驱动程序也必须存在于环境路径中
 * proxy.url= （必须）实际要连接到的数据库的JDBC连接使用的URL
 * proxy.encoding= （可选）实际连接到的数据库所使用的编码，指定该参数后，所有的对于该数
 * 据库的访问操作所产生或获得的字符串将被使用该编码进行转码，该动作是被多个代理程序包装，
 * 并很好的隐藏的，你不必关心，你只需要确定你所需要连接到的数据库的编码
 * user=username
 * password=password
 * ...
 * （可选）你可以追加任意多个属性，这些属性将被直接送到实际连接的数据库驱动代理程序不会使用这些参数
 *     你有三个途径设置这些参数，你可以选用其中的部分或全部来设置这些参数.
 *     首先，你可以创建一个"xxx.properties"属性文件，并存在与环境路径中（xxx代表你的属性文件名）然后
 * 在URL中直接指定这个属性文件名："jdbc:proxy:xxx"
 *     其次，你可以在URL最后跟上类似"?key=value&key=value…………"的字符串，代理程序将把其中的每个
 * "key=value"当做一个属性名/值对，添加到配置信息中。
 *     再者，你可以使用DriverManger.getConnection(String url,Properties propertis);方法为这个连接附
 * 加一个属性集
 *     所有这三个途径载入顺序为：属性文件、URL附加参数、连接时的属性集，后载入的属性如果与之前的重复，那
 * 么将覆盖之前的匹配；最终这些参数中的三个以"proxy."开头的参数将被特殊处理，其余将全部直接送入数据库
 * JDBC驱动程序。
 *     典型的例子：
 * 1)
 * ...
 * Connection conn=null;
 * try{
 *   Class.forName("xoj.sql.proxy.ProxyDriver");
 *   conn=DriverManager.getConnection("jdbc:proxy:xxx");
 * }catch(ClassNotFoundException e){
 *   e.printStackTrace();
 * }catch(SQLException e){
 *   e.printStackTrace();
 * }
 * ...
 * 
 * 2)
 * ...
 * Connection conn=null;
 * try{
 *   Class.forName("xoj.sql.proxy.ProxyDriver");
 *   conn=DriverManager.getConnection("jdbc:proxy:?user=username&password=password");
 * }catch(ClassNotFoundException e){
 *   e.printStackTrace();
 * }catch(SQLException e){
 *   e.printStackTrace();
 * }
 * ...
 *  
 * 3)
 * ...
 * Connection conn=null;
 * Properties properties=……
 * ...
 * try{
 *   Class.forName("xoj.sql.proxy.ProxyDriver");
 *   conn=DriverManager.getConnection("jdbc:proxy:",properties);
 * }catch(ClassNotFoundException e){
 *   e.printStackTrace();
 * }catch(SQLException e){
 *   e.printStackTrace();
 * }
 * ...
 * 
 * 4)
 * ...
 * Connection conn=null;
 * Properties properties=……
 * ...
 * try{
 *   Class.forName("xoj.sql.proxy.ProxyDriver");
 *   conn=DriverManager.getConnection("jdbc:proxy:xxx?user=username&password=password",properties);
 * }catch(ClassNotFoundException e){
 *   e.printStackTrace();
 * }catch(SQLException e){
 *   e.printStackTrace();
 * }
 * ...
 *  
 *     总之，你应该把它当作一个数据库驱动程序来使用
 * </pre>
 * <p>
 * Copyright (c) 2006 CozyWikiup shanghai
 * </p>
 * @author 沉香
 * @version 1.2
 */
public class ProxyDriver implements Driver{
  public final static String UrlFront="jdbc:proxy:";
  /**
   * 确保该类被加载时，向驱动程序管理器DriverManager注册该驱动
   */
  static{
    try{
      DriverManager.registerDriver(new ProxyDriver());
    }catch(SQLException sqlex){
      sqlex.printStackTrace();
    }
  }

  /**
   * 取主版本号
   * @return int 版本号，返回的将是xoj.sql.proxy.ProxyDriver驱动的版本号，而非实际连接的驱动版本号
   */
  public int getMajorVersion(){
    return 1;
  }

  /**
   * 取次版本号
   * @return int 版本号，返回的将是xoj.sql.proxy.ProxyDriver驱动的版本号，而非实际连接的驱动版本号
   */
  public int getMinorVersion(){
    return 2;
  }

  /**
   * 判断是否遵从JDBC规范
   * @return 返回一个是否遵从JDBC规范的布尔值，始终都返回true
   */
  public boolean jdbcCompliant(){
    return true;
  }

  /**
   * 判断某个数据库URL是否能被该驱动程序识别，能够被xoj.sql.proxy.ProxyDriver驱动正确识别的
   * URL必须以jdbc:proxy:开始
   * @param url 数据库URL
   * @return true：可识别的URL，false：不可识别的URL
   */
  public boolean acceptsURL(String url){
    if(url==null){ return false; }
    return url.startsWith(UrlFront);
  }

  /**
   * 根据URL中指定的配置文件和附加的参数，并加入附加的属性池创建数据库连接，并使用代理程序包装该连接
   * URL分为两部分：开头的jdbc:proxy:是必须的，后面的字符串表示一个存在与环境路径上的属性文件如果有?那么?
   * 之后将被解析为以&分隔的多个key=value属性/值对，也将被视为配置参数。
   * @param url 连接用的URL
   * @param properties 附加到该URL的属性池
   * @return 被包装过的连接，但是对外只使用标准的JDBC接口
   */
  public Connection connect(String url,Properties properties) throws SQLException{
    if(!acceptsURL(url)) return null;
    Connection conn=null;
    ProxyConfig proxyConfig=getConfig(url);
    proxyConfig.addJdbcProperties(properties);
    try{
      Class driverClass=Class.forName(proxyConfig.getDriver());
      Driver driver=(Driver)driverClass.newInstance();
      conn=driver.connect(proxyConfig.getUrl(),proxyConfig.getJdbcProperties());
    }catch(ClassNotFoundException e){
      throw new SQLException("找不到指定的驱动程序："+proxyConfig.getDriver());
    }catch(InstantiationException e){
      conn=DriverManager.getConnection(proxyConfig.getUrl(),proxyConfig.getJdbcProperties());
    }catch(IllegalAccessException e){
      conn=DriverManager.getConnection(proxyConfig.getUrl(),proxyConfig.getJdbcProperties());
    }
    conn=(Connection)Proxyer.proxyObject(conn,proxyConfig);
    return conn;
  }

  public DriverPropertyInfo[] getPropertyInfo(String url,Properties info){
    DriverPropertyInfo[] dpis=new DriverPropertyInfo[5];
    dpis[0]=new DriverPropertyInfo("proxy.driver",null);
    dpis[1]=new DriverPropertyInfo("proxy.url",null);
    dpis[2]=new DriverPropertyInfo("proxy.encoding",null);
    dpis[2]=new DriverPropertyInfo("user",null);
    dpis[2]=new DriverPropertyInfo("password",null);
    return dpis;
  }

  /**
   * 取得驱动指定的属性文件中的参数、URL中附加的参数以及附加的属性集，
   * @param url 数据库URL
   * @return properties 返回所有属性-值对的集
   */
  private static ProxyConfig getConfig(String url){
    ProxyConfig proxyConfig=null;
    String cfgString=url.substring(UrlFront.length());
    proxyConfig=new ProxyConfig();
    int firstQm=cfgString.indexOf("?");
    String cfgFile=firstQm==-1?cfgString:cfgString.substring(0,firstQm);
    String cfgAdd=firstQm==-1?"":cfgString.substring(firstQm+1);
    if(cfgFile.length()>0){
      ResourceBundle res=ResourceBundle.getBundle(cfgFile);
      Enumeration enumeration=res.getKeys();
      while(enumeration.hasMoreElements()){
        String key=(String)enumeration.nextElement();
        String value=res.getString(key);
        proxyConfig.setJdbcProperty(key,value);
      }
    }
    while(cfgAdd.length()>0){
      int firstAnd=cfgAdd.indexOf("&");
      String cfgGet=firstAnd==-1?cfgAdd:cfgAdd.substring(0,firstAnd);
      cfgAdd=firstAnd==-1?"":cfgAdd.substring(firstAnd+1);
      int firstAmount=cfgGet.indexOf("=");
      if(firstAmount<1) continue;
      String cfgKey=cfgGet.substring(0,firstAmount);
      String cfgValue=cfgGet.substring(firstAmount+1);
      proxyConfig.setJdbcProperty(cfgKey,cfgValue);
    }
    return proxyConfig;
  }
}
