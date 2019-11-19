package edu.cmu.sei.kalki.rulebooks.UdooNeo;

public class ThreeAxisResult {
    private boolean conditionIsTrue;
    private String alertInfo;

    public ThreeAxisResult() {}

    public ThreeAxisResult(boolean isTrue, String alertInfo) {
        this.conditionIsTrue = isTrue;
        this.alertInfo = alertInfo;
    }

    public boolean isConditionIsTrue() {
        return conditionIsTrue;
    }

    public void setConditionIsTrue(boolean conditionIsTrue) {
        this.conditionIsTrue = conditionIsTrue;
    }

    public String getAlertInfo() {
        return alertInfo;
    }

    public void setAlertInfo(String alertInfo) {
        this.alertInfo = alertInfo;
    }
}
