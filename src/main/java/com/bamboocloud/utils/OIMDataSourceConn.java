package com.bamboocloud.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.stereotype.Component;


import lombok.extern.slf4j.Slf4j;

/**
 * @Description TODO
 * @author liufangwei@bamboocloud.com
 * @Date 2021年11月10日 上午10:22:39
 * @version 1.0.0
 * 
 */
@Slf4j
@Component
public class OIMDataSourceConn {
	public Connection conn = null;

//	@NacosValue(value = "${crc.ldap.datasource.url}", autoRefreshed = true)
//	private String DBCONURL = "t3://10.0.62.46:14000,10.0.62.47:14000";
//	@NacosValue(value = "${crc.ldap.datasource.jndiname}", autoRefreshed = true)
//	private String DBSOURCE = "jdbc/operationsDB";

	public Map<String, String> getUsrKeybyusrNam(Connection conn, String usrNam) {
		Map<String, String> resultMap = new ConcurrentHashMap<String, String>();
		String sql = "select USR_KEY,USR_LOGIN,USR_STATUS,USR_EMP_TYPE,USR_EMP_NO from usr where usr_login = '" + usrNam
				+ "'";
		ResultSet dataSet = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			dataSet = pstmt.executeQuery();
			dataSet.last();
			if (dataSet.getRow() == 0) {
				resultMap.put("RET_CODE", "FALSE");
				resultMap.put("RET_ERROR_CODE", "E001");
				resultMap.put("RET_MSG", "没有查询到用户数据");
			} else {
				String status = dataSet.getString("USR_STATUS");
				if (!"Active".equals(status)) {
					resultMap.put("RET_CODE", "FALSE");
					resultMap.put("RET_ERROR_CODE", "E002");
					resultMap.put("RET_MSG", "账号被禁用");
				} else {
					resultMap.put("RET_CODE", "SUCCESS");
				}
				resultMap.put("USR_KEY", dataSet.getString("USR_KEY"));
				resultMap.put("USR_LOGIN", dataSet.getString("USR_LOGIN"));
				resultMap.put("USR_EMP_TYPE", dataSet.getString("USR_EMP_TYPE"));
				resultMap.put("USR_EMP_NO", dataSet.getString("USR_EMP_NO"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				dataSet.close();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return resultMap;
	}

	public boolean CheckUserExistAsy(Connection conn, String userName, String itResourceName) throws Exception {
		boolean flag = false;
		String sql = "SELECT * FROM ASYUSERPROV where USR_LOGIN = ? AND STATUS ='N' and ITRESNAME = ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userName);
			//CRC_IRUN
			pstmt.setString(2, itResourceName);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				flag = true;
			} else {
				flag = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs.close();
			pstmt.close();
		}
		return flag;
	}
	
	public boolean insertIntoUserInfo(Connection conn, String userName, String usr_key, String resName, String itResName, String process_table, String process_target, String time) throws Exception {
	    boolean flag = false;
//	    String resName = ConstantsUtil.RESNAME;
//	    String itResName = ConstantsUtil.ITRESNAME;
//	    String process_table = ConstantsUtil.PROCESS_TABLE;
//	    String process_target = ConstantsUtil.PROCESS_TARGET;
//	    String time = SystemTime.getTime();
	    String sql = "insert into ASYUSERPROV (USR_KEY, USR_LOGIN, RESNAME, ITRESNAME, PROCESS_TABLE, PROCESS_TARGET,STATUS,CREATE_DATE) values(?,?,?,?,?,?,?,?) ";
	    PreparedStatement pstmt = null;
	    try {
	      pstmt = conn.prepareStatement(sql);
	      pstmt.setString(1, usr_key);
	      pstmt.setString(2, userName);
	      //CRC_LDAP_USR
	      pstmt.setString(3, resName);
	      //CRC_IRUN
	      pstmt.setString(4, itResName);
	      //UD_CRC_LDAP
	      pstmt.setString(5, process_table);
	      //UD_CRC_LDAP_TARGET
	      pstmt.setString(6, process_target);
	      pstmt.setString(7, "N");
	      //system date
	      pstmt.setString(8, time);
	      pstmt.executeUpdate();
	      flag = true;
	    } catch (Exception e) {
	      e.printStackTrace();
	      conn.rollback();
	    } finally {
	      pstmt.close();
	    } 
	    return flag;
	  }

	public Connection getConnection(String DBCONURL, String DBSOURCE) throws Exception {
		if (this.conn != null && !this.conn.isClosed())
			return this.conn;
		try {
			log.info("数据源连接信息：" + DBCONURL + ":" + DBSOURCE);
			Hashtable<Object, Object> ht = new Hashtable<Object, Object>();
			ht.put("java.naming.factory.initial", "weblogic.jndi.WLInitialContextFactory");
			ht.put("java.naming.provider.url", DBCONURL);
			InitialContext context = new InitialContext(ht);
			DataSource ds = (DataSource) context.lookup(DBSOURCE);
			if (ds == null) {
				log.error("查询到空数据源！");
				throw new SQLException("查询到空数据源！");
			}
			this.conn = ds.getConnection();
			log.info("数据源连接完成");
		} catch (NamingException e) {
			e.printStackTrace();
			log.error("加载数据库配置信息失败，实例化InitialContext时发生错误：" + e.getMessage());
			throw new Exception("加载数据库配置信息失败，实例化InitialContext时发生错误：" + e.getMessage());
		} catch (SQLException ec) {
			ec.printStackTrace();
			log.error("[错误]连接数据库失败" + ec.getMessage());
			throw new SQLException("[错误]连接数据库失败" + ec.getMessage());
		}
		if (this.conn == null) {
			log.error("连接数据库失败，获取不到数据库连接");
			throw new SQLException("连接数据库失败，获取不到数据库连接");
		}
		return this.conn;
	}
//
//	public static void main(String args[]) {
//		OIMDataSourceConn dd = new OIMDataSourceConn();
//		try {
//			System.out.println(JSONObject.toJSONString(dd.getUsrKeybyusrNam(dd.getConnection(), "lisheng125")));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
