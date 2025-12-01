package com.tool.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbConnectionUtil {

    private static DbDetailsBuilder.DbInfo dbInfo;

    public static void configure(String storeCode) {
        dbInfo = DbDetailsBuilder.fromStoreCode(storeCode);
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(dbInfo.url, dbInfo.username, dbInfo.password);
    }
}
