package edu.cmu.sei.kalki.rulebooks.phle;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

@Rule()
public class TimeOff extends RulebookRule {

    public TimeOff(){

    }

    public void finalize()
            throws Throwable{
    }

    public boolean conditionIsTrue(){
        // this status is OFF
        if(!Boolean.parseBoolean(status.getAttributes().get("isOn"))) {

        }
            // query for last change of state
        // if last change > X (120 for now) minutes
        setAlertName("phle-time-off");
        return false;
    }

}
