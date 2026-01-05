package com.tool.dao;

import com.tool.util.DbConnectionUtil;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.*;

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

        try (Connection conn = DbConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(AVGQUERY)) {

            ResultSet rs = ps.executeQuery(); //row

            while (rs.next()) {
                long ts = rs.getLong("batch_timestamp");

                LocalDateTime dt = Instant.ofEpochMilli(ts)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                int secondsOfDay = dt.toLocalTime().toSecondOfDay(); // 0..86399[web:231][web:237]
                sum += secondsOfDay;
                count++;
            }

            if (count == 0) return "No POSLOG found";

            long avgMillis = sum / count;

            LocalTime avgTime = LocalTime.ofSecondOfDay(avgMillis);

            return avgTime.toString(); // e.g., 2025-01-26T22:37:13

        } catch (Exception e) {}

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
