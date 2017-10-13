package org.gproman.model.race;

import java.sql.Timestamp;

import org.gproman.model.PersistentEntity;

public class RaceStatus extends PersistentEntity {

    private boolean   trackInfo          = false;
    private boolean   practice           = false;
    private boolean   qualify1           = false;
    private boolean   qualify2           = false;
    private boolean   setup              = false;
    private boolean   telemetry          = false;
    private Timestamp setupPublished     = null;
    private Timestamp telemetryPublished = null;
    private Timestamp testsPublished     = null;

    public RaceStatus() {
    }

    public boolean isTrackInfo() {
        return trackInfo;
    }

    public void setTrackInfo(boolean trackInfo) {
        this.trackInfo = trackInfo;
    }

    public boolean isPractice() {
        return practice;
    }

    public void setPractice(boolean practice) {
        this.practice = practice;
    }

    public boolean isQualify1() {
        return qualify1;
    }

    public void setQualify1(boolean qualify1) {
        this.qualify1 = qualify1;
    }

    public boolean isQualify2() {
        return qualify2;
    }

    public void setQualify2(boolean qualify2) {
        this.qualify2 = qualify2;
    }

    public boolean isSetup() {
        return setup;
    }

    public void setSetup(boolean setup) {
        this.setup = setup;
    }

    public boolean isTelemetry() {
        return telemetry;
    }

    public void setTelemetry(boolean telemetry) {
        this.telemetry = telemetry;
    }

    public Timestamp getSetupPublished() {
        return setupPublished;
    }

    public void setSetupPublished(Timestamp setupPublished) {
        this.setupPublished = setupPublished;
    }

    public Timestamp getTelemetryPublished() {
        return telemetryPublished;
    }

    public void setTelemetryPublished(Timestamp telemetryPublished) {
        this.telemetryPublished = telemetryPublished;
    }

    public Timestamp getTestsPublished() {
        return testsPublished;
    }

    public void setTestsPublished(Timestamp testsPublished) {
        this.testsPublished = testsPublished;
    }

    @Override
    public String toString() {
        return "RaceStatus [trackInfo=" + trackInfo + ", practice=" + practice + ", qualify1=" + qualify1 + ", qualify2=" + qualify2 + ", setup=" + setup + ", telemetry=" + telemetry + ", setupPublished=" + setupPublished + ", telemetryPublished=" + telemetryPublished + ", testsPublished=" + testsPublished + "]";
    }

}
