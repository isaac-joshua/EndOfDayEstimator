# EndOfDayEstimator

# EOD POSLOG Estimator

Small Java tool that connects to PostgreSQL store databases and prints the typical POSLOG generation time for each store.

## What it does

- Reads store codes from `input.txt` (one code per line).
- Builds the correct JDBC URL for each store based on country and store number.
- Connects to the store database using PostgreSQL JDBC.
- Fetches recent POSLOG batch timestamps and calculates an average timestamp.
- Determines if the End Of Day is completed based on the availability of the POSLOG file for the current day.
- Writes results to `AverageTimings.txt` and `TodaysEOD.txt`.

## Usage

1. Put your store codes in `input.txt`, one per line.
2. Build the project: mvn clean package
3. Run: java -cp target/eod-estimator-1.0-SNAPSHOT.jar com.tool.App
4. Check `output.txt` for the report.





