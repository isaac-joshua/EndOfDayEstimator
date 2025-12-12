package com.tool;

import com.tool.dao.Batch2Dao;
import com.tool.util.DbConnectionUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class EodCheck {

    public static void main(String[] args) {

        try (BufferedReader br = new BufferedReader(new FileReader("input.txt"));
             BufferedWriter out = new BufferedWriter(new FileWriter("output.txt"))) {

            String code;
            while ((code = br.readLine()) != null) {
                code = code.trim();

                if (code.isEmpty()) {
                    break;
                }

                DbConnectionUtil.configure(code);

                Batch2Dao dao = new Batch2Dao();
                String ts = dao.checkLatestPoslogDate();

                out.write("Store Code: " + code);
                out.newLine();
                out.write(ts);
                out.newLine();
                out.write("-------------------------------------");
                out.newLine();

                System.out.println("Processed store " + code);
            }

        } catch (IOException e) {
            System.out.println("Error reading/writing file: " + e.getMessage());
        }
    }
}
