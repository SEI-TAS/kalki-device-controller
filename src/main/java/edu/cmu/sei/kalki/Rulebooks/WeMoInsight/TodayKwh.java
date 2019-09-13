package edu.cmu.sei.kalki.rulebooks.WeMoInsight;

import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

@Rule()
public class TodayKwh extends RulebookRule {

    public TodayKwh(){ }

    public boolean conditionIsTrue(){
        setAlertCondition("WeMoInsight-today-kwh");
        double todayKwHThreshold = Double.valueOf(alertCondition.getVariables().get("today_kwh"));
        double todayKwH = Double.valueOf(status.getAttributes().get("today_kwh"));

        if (todayKwH > todayKwHThreshold){
            return true;
        }

        return false;
    }

}
