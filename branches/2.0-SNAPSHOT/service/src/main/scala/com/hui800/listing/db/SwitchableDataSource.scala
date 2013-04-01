package com.hui800.listing.db

import com.mchange.v2.c3p0.ComboPooledDataSource
import com.twitter.logging.Logger
import com.twitter.ostrich.admin.BackgroundProcess
import javax.sql.DataSource
import org.apache.commons.configuration.PropertiesConfiguration

class SwitchableDataSource(conf: PropertiesConfiguration) extends DataSource {
    
  val log = Logger(getClass.getName)
  private var ds: ComboPooledDataSource = null
    
  switch(conf.getString("hui800.listing.currentDataSource"))

  def currentDataSource = {
    conf.getString("hui800.listing.currentDataSource")
  }
  
  def switch(dataSourceId: String) = synchronized {
    val ds = dataSource(dataSourceId)
    ds.getConnection.close
    val oldDs = this.ds
    conf.setProperty("hui800.listing.currentDataSource", dataSourceId)
    conf.save()
    this.ds = ds
    close(oldDs)
  }
  
  private def dataSource(dataSourceId: String) = {
    val ds = new ComboPooledDataSource
    ds.setDriverClass("com.mysql.jdbc.Driver")
    ds.setJdbcUrl(conf.getString(String.format("hui800.listing.dataSource.%s.jdbcUrl", dataSourceId)))
    ds.setUser(conf.getString(String.format("hui800.listing.dataSource.%s.user", dataSourceId)))
    ds.setPassword(conf.getString(String.format("hui800.listing.dataSource.%s.password", dataSourceId)))
    ds
  }
  
  private def close(ds: ComboPooledDataSource) = {
    if (ds != null) {
      BackgroundProcess.spawn("close_data_source_" + ds) {
        log.info("close datasource in 30 seconds: %s", ds.hashCode)
        Thread.sleep(30000)
        ds.close
        log.info("datasource closed: %s", ds.hashCode)
      }
    }
  }

  def getConnection(x$1: java.lang.String, x$2: java.lang.String) = ds.getConnection(x$1, x$2)
  def getConnection = ds.getConnection
  def isWrapperFor(x$1: java.lang.Class[_]) = ds.isWrapperFor(x$1)
  def unwrap[T](x$1: java.lang.Class[T]) = ds.unwrap(x$1)
  def getLoginTimeout = ds.getLoginTimeout
  def setLoginTimeout(x$1: Int) = ds.setLoginTimeout(x$1)
  def setLogWriter(x$1: java.io.PrintWriter) = ds.setLogWriter(x$1)
  def getLogWriter = ds.getLogWriter
}
