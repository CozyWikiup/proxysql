package xoj.sql.proxy.db2;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import javax.sql.PooledConnection;

import xoj.sql.proxy.ProxyConfig;
import xoj.sql.proxy.Proxyer;

import com.ibm.db2.jcc.DB2BaseDataSource;
import com.ibm.db2.jcc.DB2ConnectionPoolDataSource;

public class DB2ConnectionPoolDataSourceProxyer extends DB2ConnectionPoolDataSource{
  private static final long serialVersionUID=-7177651700430910465L;
  private ProxyConfig proxyConfig=new ProxyConfig();

  public DB2ConnectionPoolDataSourceProxyer(){
    super();
  }

  public int getConnectionReuseProtocol(){
    return super.getConnectionReuseProtocol();
  }

  public PooledConnection getPooledConnection() throws SQLException{
    PooledConnection pooledConnection=super.getPooledConnection();
    pooledConnection=(PooledConnection)Proxyer.proxyObject(pooledConnection,proxyConfig);
    return pooledConnection;
  }

  public PooledConnection getPooledConnection(DB2BaseDataSource arg0,String arg1,String arg2) throws SQLException{
    PooledConnection pooledConnection=super.getPooledConnection(arg0,arg1,arg2);
    pooledConnection=(PooledConnection)Proxyer.proxyObject(pooledConnection,proxyConfig);
    return pooledConnection;
  }

  public PooledConnection getPooledConnection(Object arg0) throws SQLException{
    PooledConnection pooledConnection=super.getPooledConnection(arg0);
    pooledConnection=(PooledConnection)Proxyer.proxyObject(pooledConnection,proxyConfig);
    return pooledConnection;
  }

  public PooledConnection getPooledConnection(String arg0,String arg1) throws SQLException{
    PooledConnection pooledConnection=super.getPooledConnection(arg0,arg1);
    pooledConnection=(PooledConnection)Proxyer.proxyObject(pooledConnection,proxyConfig);
    return pooledConnection;
  }

  public synchronized void setProxyEncoding(String encoding) throws SQLException,UnsupportedEncodingException{
    if(encoding==null) return;
    "".getBytes(encoding);
    proxyConfig.setEncoding(encoding);
    super.getProperties().setProperty("proxyEncoding",encoding);
  }
}