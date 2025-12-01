package com.tool;

import com.tool.dao.BatchDao;
import com.tool.util.DbConnectionUtil;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class App {

    public static void main(String[] args) {

        String code = null;

        // Read store code from file
        try (BufferedReader br = new BufferedReader(new FileReader("input.txt"))) {
            code = br.readLine();
            if (code != null) {
                code = code.trim();
            } else {
                System.out.println("Store code not found in the file.");
                return;
            }
        } catch (IOException e) {
            System.out.println("Error reading store.txt: " + e.getMessage());
            return;
        }

        DbConnectionUtil.configure(code);

        BatchDao dao = new BatchDao();
        String ts = dao.getAveragePoslogTimestamp();

        System.out.println("Store Code: " + code);
        System.out.println("Average POSLOG Generated time: " + ts);
    }
}
