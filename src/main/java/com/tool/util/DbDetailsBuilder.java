package com.tool.util;

public class DbDetailsBuilder {

    public static DbInfo fromStoreCode(String code) {

        String country = code.substring(0, 2);
        String storeNo = code.substring(2);

        String dbName = "PIP" + storeNo;
        String user = "pip";
        String pass = "pg4posprod";
        String port = "5432";

        String host;
        String dbPath;

        if (storeNo.length() == 4) {
            host = "R" + country + storeNo + "-lx2020.ikea.com";

            // 4‑digit store: want pip0 + 4 digits (01094 from 1094)
            int storeInt = Integer.parseInt(storeNo);
            String padded = String.format("%05d", storeInt);            // "%05d" is a format specifier: % = start of the specifier.0 = pad with zeros instead of spaces.5 = total width should be 5 characters.d = format the value as a decimal integer
            dbPath = "pip" + padded + "?";                                  //"pip01234"
        } else if (storeNo.length() == 3) {
            // Existing behavior: DK121 -> RETDK121-lx2020.ikea.com / pip00121?
            host = "RET" + country + storeNo + "-lx2020.ikea.com";

            int storeInt = Integer.parseInt(storeNo);
            String padded = String.format("%05d", storeInt);
            dbPath = "pip" + padded + "?";                                     // "pip00123?"
        } else {
            throw new IllegalArgumentException("Unexpected store number: " + storeNo);
        }

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
