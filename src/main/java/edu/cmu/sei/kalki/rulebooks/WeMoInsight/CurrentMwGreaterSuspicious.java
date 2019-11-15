package edu.cmu.sei.kalki.rulebooks.WeMoInsight;

import com.deliveredtechnologies.rulebook.annotation.Rule;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;
import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

import java.util.List;

@Rule()
public class CurrentMwGreaterSuspicious extends RulebookRule {

    public CurrentMwGreaterSuspicious() {}

    @Override
    public boolean conditionIsTrue() {
        setAlertCondition("wemo-current-mw-greater-low-suspicious");

        String stateCondition = alertCondition.getVariables().get("state");
        if(!device.getCurrentState().getName().equals(stateCondition))
            return false;

        double threshold = Double.valueOf(alertCondition.getVariables().get("currentmw"));
        int duration = Integer.parseInt(alertCondition.getVariables().get("duration"));

        List<DeviceStatus> statuses = Postgres.findDeviceStatusesOverTime(device.getId(), duration, "minute");
        for(DeviceStatus s: statuses){
            double currentPower = Double.valueOf(s.getAttributes().get("currentpower"));
            if(currentPower < threshold) // a value was < alert condition in last X minutes, so not suspicious
                return false;
        }

        alertInfo = "Current mw was greater than "+threshold+" for last "+duration+" minutes";
        return true;
    }
}
