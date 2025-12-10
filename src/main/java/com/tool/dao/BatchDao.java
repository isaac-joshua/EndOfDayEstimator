package com.tool.dao;

import com.tool.util.DbConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class BatchDao {

    private static final String QUERY =
            "SELECT batch_timestamp FROM batch WHERE batch_type = 'POSLOG' ORDER BY batch_timestamp DESC LIMIT 1";

    public String getAveragePoslogTimestamp() {
        long sum = 0;
        int count = 0;

        try (Connection conn = DbConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(QUERY)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                long ts = rs.getLong("batch_timestamp");
                sum += ts;
                count++;
            }

            if (count == 0) return "No POSLOG found";

            long avgMillis = sum / count;

            LocalDateTime avgDateTime = Instant.ofEpochMilli(avgMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            return avgDateTime.toString(); // e.g., 2025-01-26T22:37:13

        } catch (Exception e) {}

        return "No POSLOG found";
    }
}
