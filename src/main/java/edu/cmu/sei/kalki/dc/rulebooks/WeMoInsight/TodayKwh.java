package edu.cmu.sei.kalki.dc.rulebooks.WeMoInsight;

import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.dc.rulebooks.RulebookRule;

@Rule()
public class TodayKwh extends RulebookRule
{

    public TodayKwh(){ }

    public boolean conditionIsTrue(){
        setAlertCondition("wemo-today-kwh");
        double todayKwHThreshold = Double.valueOf(alertCondition.getVariables().get("today_kwh"));
        double todayKwH = Double.valueOf(status.getAttributes().get("today_kwh"));

        if (todayKwH > todayKwHThreshold){
            alertInfo = "Today kwh was greater than "+todayKwHThreshold;
            return true;
        }

        return false;
    }

}
