package com.tool.dao;

import com.tool.util.DbConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.HashSet;

public class BatchDao {

    private static final String AVGQUERY =
            "SELECT batch_timestamp FROM batch WHERE batch_type = 'POSLOG'AND EXTRACT(DOW FROM to_timestamp(batch_timestamp / 1000.0)) BETWEEN 1 AND 5 ORDER BY batch_timestamp DESC LIMIT 22";

    private static final String TODAYQUERY =
            "SELECT batch_timestamp FROM batch " +
                    "WHERE batch_type = 'POSLOG' " +
                    "ORDER BY batch_timestamp DESC LIMIT 1";

    private static final String VERSIONQUERY = "SELECT Version();";

    public String GetDBVersion() {
        try (Connection conn = DbConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(VERSIONQUERY)) {
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString(1);
            }
            System.out.println(rs);
        } catch (Exception e) {}
        return "error";
    }

    public String getAveragePoslogTimestamp() {
        long sum = 0;
        int count = 0;

        // Change holidays
        Set<LocalDate> holidays = new HashSet<>();
        holidays.add(LocalDate.of(2025, 12, 25));
        holidays.add(LocalDate.of(2025, 12, 26));
        holidays.add(LocalDate.of(2025, 12, 31));
        holidays.add(LocalDate.of(2026, 1, 1));

        try (Connection conn = DbConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(AVGQUERY)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                long ts = rs.getLong("batch_timestamp");

                LocalDateTime dt = Instant.ofEpochMilli(ts)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                LocalDate date = dt.toLocalDate();
                if (holidays.contains(date)) {
                    // skip these specific dates
                    continue;
                }

                int secondsOfDay = dt.toLocalTime().toSecondOfDay();
                sum += secondsOfDay;
                count++;
            }

            if (count == 0) return "No POSLOG found";

            long avgSeconds = sum / count;
            LocalTime avgTime = LocalTime.ofSecondOfDay(avgSeconds);

            return avgTime.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "No POSLOG found";
    }

    public String checkLatestPoslogDate() {
        try (Connection conn = DbConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(TODAYQUERY)) {

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
