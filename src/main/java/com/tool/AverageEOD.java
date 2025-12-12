package com.tool;

import com.tool.dao.BatchDao;
import com.tool.util.DbConnectionUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AverageEOD {

    public static void main(String[] args) {

        try (BufferedReader br = new BufferedReader(new FileReader("input.txt"));
             BufferedWriter out = new BufferedWriter(new FileWriter("AverageTimings.txt"))) {

            String code;
            while ((code = br.readLine()) != null) {
                code = code.trim();

                if (code.isEmpty()) {
                    break;
                }

                DbConnectionUtil.configure(code);

                BatchDao dao = new BatchDao();
                String ts = dao.getAveragePoslogTimestamp();

                out.write("Store Code: " + code);
                out.newLine();
                out.write("Average POSLOG Generated time: " + ts + "UTC");
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
