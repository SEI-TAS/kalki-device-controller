package edu.cmu.sei.kalki.dc.rulebooks.UdooNeo;

import com.deliveredtechnologies.rulebook.annotation.Rule;
import edu.cmu.sei.kalki.dc.rulebooks.RulebookRule;

@Rule()
public class AccelerationAverage extends RulebookRule {

    public AccelerationAverage() {}

    @Override
    public boolean conditionIsTrue() {
        setAlertCondition("unts-acceleration-avg");

        String stateCondition = alertCondition.getVariables().get("state");
        if(!device.getCurrentState().getName().equals(stateCondition))
            return false;

        ThreeAxisResult result = ThreeAxisUtil.checkAgainstAverages(alertCondition, device.getId(),"accelerometer");
        alertInfo = result.getAlertInfo();
        return result.isConditionIsTrue();
    }
}
