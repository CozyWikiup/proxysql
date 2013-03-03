package xoj.sql.proxy.sybase;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.PooledConnection;

import xoj.sql.proxy.ProxyConfig;
import xoj.sql.proxy.Proxyer;

import com.sybase.jdbc3.jdbc.SybConnectionPoolDataSource;

public class SybConnectionPoolDataSourceProxyer extends SybConnectionPoolDataSource{
  private static final long serialVersionUID=-7994097549932589607L;
  private ProxyConfig proxyConfig=new ProxyConfig();

  public Connection getConnection() throws SQLException{
    Connection connection=super.getConnection();
    connection=(Connection)Proxyer.proxyObject(connection,proxyConfig);
    return connection;
  }

  public Connection getConnection(String arg0,String arg1) throws SQLException{
    Connection connection=super.getConnection(arg0,arg1);
    connection=(Connection)Proxyer.proxyObject(connection,proxyConfig);
    return connection;
  }

  public PooledConnection getPooledConnection() throws SQLException{
    PooledConnection pooledConnection=super.getPooledConnection();
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
  }
}
