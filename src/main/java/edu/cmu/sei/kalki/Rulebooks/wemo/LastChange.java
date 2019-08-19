package edu.cmu.sei.kalki.rulebooks.wemo;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

import java.sql.Timestamp;
import java.util.List;

@Rule()
public class LastChange extends RulebookRule {

    public LastChange(){ }

    public boolean conditionIsTrue(){
        setAlertCondition("wemo-last-change");
        int minutes = Integer.valueOf(alertCondition.getVariables().get("lastchange"));
        List<DeviceStatus> statuses = device.lastNSamples(2);

        if (statuses.size() > 1) {
            boolean oneIsOn = Boolean.parseBoolean(statuses.get(0).getAttributes().get("isOn"));
            boolean twoIsOn = Boolean.parseBoolean(statuses.get(1).getAttributes().get("isOn"));

            if(oneIsOn && twoIsOn) {
                // convert timestamp escape format to long
                long ts1 = Timestamp.valueOf(statuses.get(0).getAttributes().get("lastchange")).getTime();
                long ts2 = Timestamp.valueOf(statuses.get(1).getAttributes().get("lastchange")).getTime();

                if(Math.abs(ts1 - ts2) /60000 > minutes){
                    return true;
                }
            }


        }
        return false;
    }

}



