package com.tool.util;

public class DbDetailsBuilder {

    public static DbInfo fromStoreCode(String code) {

        String country = code.substring(0, 2);
        String storeNo = code.substring(2);

        String dbName = "PIP" + storeNo;
        String user = "pip";
        String pass = "pg4posprod";
        String port = "5432";

        String host = "RET" + country + storeNo + "-lx2020.ikea.com";
        String dbPath = "pip00" + storeNo + "?";

        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbPath;

        return new DbInfo(url, user, pass, dbName);
    }

    public static class DbInfo {
        public final String url;
        public final String username;
        public final String password;
        public final String dbName;

        public DbInfo(String url, String username, String password, String dbName) {
            this.url = url;
            this.username = username;
            this.password = password;
            this.dbName = dbName;
        }
    }
}
