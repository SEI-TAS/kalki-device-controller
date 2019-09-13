package edu.cmu.sei.kalki.rulebooks.PhilipsHueLightEmulator;

import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

@Rule()
public class TimeOff extends RulebookRule {

    public TimeOff(){ }

    public boolean conditionIsTrue(){
        setAlertCondition("PhilipsHueLightEmulator-time-off");

        double lastOffCondition = Double.parseDouble(alertCondition.getVariables().get("time-last-change"));
        double lastOff = Double.parseDouble(status.getAttributes().get("time-last-change"));
        // this status is OFF && time last change > condition
        if(!Boolean.parseBoolean(status.getAttributes().get("isOn")) && (lastOff > lastOffCondition)) {
            return true;
        }
        return false;
    }

}
