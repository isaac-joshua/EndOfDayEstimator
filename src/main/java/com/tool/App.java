package com.tool;

import com.tool.dao.BatchDao;
import com.tool.util.DbConnectionUtil;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class App {

    public static void main(String[] args) {

        try (BufferedReader br = new BufferedReader(new FileReader("input.txt"))) {

            String code;
            while ((code = br.readLine()) != null) {
                code = code.trim();
                if (code.isEmpty()) {
                    continue; // skip blank lines
                }

                DbConnectionUtil.configure(code);

                BatchDao dao = new BatchDao();
                String ts = dao.getAveragePoslogTimestamp();

                System.out.println("Store Code: " + code);
                System.out.println("Average POSLOG Generated time: " + ts);
                System.out.println("-------------------------------------");
            }

        } catch (IOException e) {
            System.out.println("Error reading input.txt: " + e.getMessage());
        }
    }
}
