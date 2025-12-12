package com.tool.dao;

import com.tool.util.DbConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Batch2Dao {

    // existing query (latest one)
    private static final String QUERY_LATEST =
            "SELECT batch_timestamp FROM batch " +
                    "WHERE batch_type = 'POSLOG' " +
                    "ORDER BY batch_timestamp DESC LIMIT 1";

    /**
     * Returns a message telling whether the latest POSLOG is from today,
     * in the past, or in the future (based on system default timezone).
     */
    public String checkLatestPoslogDate() {
        try (Connection conn = DbConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(QUERY_LATEST)) {

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                return "No POSLOG found";
            }

            long ts = rs.getLong("batch_timestamp");      // epoch millis
            LocalDateTime latestDateTime = Instant.ofEpochMilli(ts)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();                   // same pattern as your average method[web:39][web:164]

            LocalDate latestDate = latestDateTime.toLocalDate();
            LocalDate today = LocalDate.now(ZoneId.systemDefault());        // current date in system zone[web:193][web:199]

            if (latestDate.isEqual(today)) {
                return "EOD is Done " + latestDateTime;
            } else if (latestDate.isBefore(today)) {
                return "EOD Not yet Done \nLatest POSLOG is from " + latestDateTime;
            } else {
                return "Latest POSLOG is from FUTURE date: " + latestDateTime;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error checking latest POSLOG date";
        }
    }
}

