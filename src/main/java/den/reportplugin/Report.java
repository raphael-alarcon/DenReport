package den.reportplugin;

import org.bukkit.entity.Player;

public class Report {

    //region Private attributes
    private Player reportedPlayer;
    private Player reportingPlayer;
    private String reason;
    private String dateOfReport;
    private static int incrementingId;
    private int id;
    //endregion

    //region Getters and setters
    public Player getReportedPlayer() {
        return reportedPlayer;
    }

    public void setReportedPlayer(Player reportedPlayer) {
        this.reportedPlayer = reportedPlayer;
    }

    public Player getReportingPlayer() {
        return reportingPlayer;
    }

    public void setReportingPlayer(Player reportingPlayer) {
        this.reportingPlayer = reportingPlayer;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDateOfReport() {
        return dateOfReport;
    }

    public void setDateOfReport(String dateOfReport) {
        this.dateOfReport = dateOfReport;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    //endregion

    /**
     * Constructor for the Report class
     * @param p The player that is being reported
     * @param p2 The player that is reporting
     * @param s The reason for the report
     * @param d The date of the report
     */
    public Report(Player p, Player p2, String s, String d) {
        this.reportedPlayer = p;
        this.reason = s;
        this.dateOfReport = d;
        incrementingId++;
        this.id = incrementingId;
        this.reportingPlayer = p2;
    }

    /**
     * @return String representation of the report.
     */
    @Override
    public String toString() {
        return "Report{" +
                "reportedPlayer=" + reportedPlayer +
                ", reason='" + reason + '\'' +
                ", dateOfReport=" + dateOfReport +
                ", id=" + id +
                '}';
    }
}
