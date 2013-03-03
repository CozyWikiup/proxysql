package xoj.sql.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ParameterMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import javax.sql.RowSet;

public class Proxyer implements InvocationHandler{
  private Object obj;
  private ProxyConfig proxyConfig;

  private Proxyer(Object obj,ProxyConfig proxyConfig){
    this.obj=obj;
    this.proxyConfig=proxyConfig;
  }

  public Object invoke(Object obj,Method method,Object[] params) throws Throwable{
    if(params!=null) for(int i=0;i<params.length;i++)
      if(params[i] instanceof String) params[i]=proxyConfig.convertJVM2DB((String)params[i]);
    Object result=null;
    try{
      result=method.invoke(this.obj,params);
    }catch(InvocationTargetException e){
      throw e.getCause();
    }
    if(result==null) return null;
    if(Proxy.isProxyClass(result.getClass())) ;
    else if(result instanceof String) result=proxyConfig.convertDB2JVM((String)result);
    else if(result instanceof Connection) result=proxyObject(result,proxyConfig);
    else if(result instanceof Statement) result=proxyObject(result,proxyConfig);
    else if(result instanceof ResultSet) result=proxyObject(result,proxyConfig);
    else if(result instanceof DataSource) result=proxyObject(result,proxyConfig);
    else if(result instanceof ResultSetMetaData) result=proxyObject(result,proxyConfig);
    else if(result instanceof ParameterMetaData) result=proxyObject(result,proxyConfig);
    else if(result instanceof DatabaseMetaData) result=proxyObject(result,proxyConfig);
    else if(result instanceof RowSet) result=proxyObject(result,proxyConfig);
    return result;
  }

  public static Object proxyObject(Object obj,ProxyConfig proxyConfig){
    if(obj==null) return obj;
    if(Proxy.isProxyClass(obj.getClass())) return obj;
    Class clazz=obj.getClass();
    ClassLoader loader=clazz.getClassLoader();
    Class[] clazzs=getInterfaces(clazz);
    if(clazzs.length==0) return obj;
    Proxyer proxyer=new Proxyer(obj,proxyConfig);
    obj=Proxy.newProxyInstance(loader,clazzs,proxyer);
    return obj;
  }

  private static Class[] getInterfaces(Class clazz){
    List list=new ArrayList();
    parseInterfaces(list,clazz);
    Class[] classs=new Class[list.size()];
    return (Class[])list.toArray(classs);
  }

  private static void parseInterfaces(List list,Class clazz){
    Class[] classs=clazz.getInterfaces();
    for(int i=0;i<classs.length;i++){
      if(list.contains(classs[i])) continue;
      list.add(classs[i]);
    }
    Class superClass=clazz.getSuperclass();
    if(superClass!=null) parseInterfaces(list,superClass);
  }
}
