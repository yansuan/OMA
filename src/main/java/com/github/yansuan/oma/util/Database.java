/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.yansuan.oma.util;

import com.github.yansuan.oma.model.DbaDataFilesInfo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author user
 */
public class Database {
    private String driver = "oracle.jdbc.driver.OracleDriver";
    private Connection conn = null;
    private String url = null;

    private Database() {
        //ignore
    }

    public static Database newInstance(String url) {
        Database db = new Database();
        db.url = url;
        return db;
    }

    // 获得连接对象
    private synchronized Connection getConn() {
        if (conn == null) {
            try {
                Class.forName(driver);
                conn = DriverManager.getConnection(url);
            } catch (ClassNotFoundException e) {
                System.out.println("Oracle jdbc driver not found.");
            } catch (SQLException e) {
                System.out.println("Database connect failed.");
            }
        }

        return conn;
    }

    //执行查询语句
    public void query(String sql, boolean isSelect) throws SQLException {
        PreparedStatement pstmt;

        try {
            pstmt = getConn().prepareStatement(sql);
            //建立一个结果集，用来保存查询出来的结果
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                System.out.println(name);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<DbaDataFilesInfo> getDbaDataFiles() throws SQLException {
        PreparedStatement pstmt = null;
        String sql = "select d.tablespace_name,d.file_name,ROUND(d.bytes/1024/1024,0) mb,t.bigfile from dba_data_files d,dba_tablespaces t where t.tablespace_name=d.tablespace_name and d.tablespace_name not in ('SYSTEM','USERS','TEMP','SYSAUX') and t.contents not in ('UNDO') order by d.tablespace_name asc";

        List<DbaDataFilesInfo> result = new ArrayList<DbaDataFilesInfo>();
        ResultSet rs = null;
        try {
            pstmt = getConn().prepareStatement(sql);
            //建立一个结果集，用来保存查询出来的结果
            rs = pstmt.executeQuery();
            while (rs.next()) {
                DbaDataFilesInfo info = new DbaDataFilesInfo();
                info.setFileName(rs.getString("file_name"));
                info.setTablespaceName(rs.getString("tablespace_name"));
                info.setMb(rs.getDouble("mb"));
                info.setBigfile(rs.getString("bigfile"));

                result.add(info);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
        }

        return result;
    }

    public void query(String sql) throws SQLException {
        PreparedStatement pstmt;
        pstmt = getConn().prepareStatement(sql);
        pstmt.execute();
        pstmt.close();
    }

    //关闭连接
    public void close() {
        try {
            getConn().close();
        } catch (SQLException e) {

        }
    }
}
