package edu.cmu.sei.kalki.rulebooks.WeMoInsight;

import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

@Rule()
public class TimeOn extends RulebookRule {

    public TimeOn(){ }

    public boolean conditionIsTrue(){
        setAlertCondition("wemo-time-on");

        int onTimeThreshold = Integer.valueOf(alertCondition.getVariables().get("today_on_time"));

        // today_on_time is in seconds
        int onTime = Integer.valueOf(status.getAttributes().get("today_on_time"));

        if (onTime > onTimeThreshold){
            return true;
        }

        return false;
    }

}
