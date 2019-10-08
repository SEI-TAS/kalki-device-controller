package edu.cmu.sei.kalki.rulebooks.WeMoInsight;

import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

@Rule()
public class CurrentMwGreater extends RulebookRule {
    private final double currentMwLowThreshold = 17040;
    private final double currentMwHightThreshold = 17050;

    public CurrentMwGreater() { }


    public boolean conditionIsTrue(){
        double currentmw = Double.valueOf(status.getAttributes().get("currentpower"));

        setAlertCondition("wemo-current-mw-greater-high");
        double threshold = Double.valueOf(alertCondition.getVariables().get("currentmw"));
        if(currentmw > threshold) {
            return true;
        }


        setAlertCondition("wemo-current-mw-greater-low");

        String stateCondition = alertCondition.getVariables().get("state");
        if(!device.getCurrentState().getName().equals(stateCondition))
            return false;

        threshold = Double.valueOf(alertCondition.getVariables().get("currentmw"));
        if(currentmw > threshold){
            return true;
        }

        return false;
    }

}
