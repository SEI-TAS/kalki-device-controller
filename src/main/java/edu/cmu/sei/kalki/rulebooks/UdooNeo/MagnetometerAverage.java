package edu.cmu.sei.kalki.rulebooks.UdooNeo;

import com.deliveredtechnologies.rulebook.annotation.Rule;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

@Rule()
public class MagnetometerAverage extends RulebookRule {

    public MagnetometerAverage() {}

    @Override
    public boolean conditionIsTrue() {
        setAlertCondition("unts-magnetometer-avg");

        String stateCondition = alertCondition.getVariables().get("state");
        if(!device.getCurrentState().getName().equals(stateCondition))
            return false;

        ThreeAxisResult result = ThreeAxisUtil.checkAgainstAverages(alertCondition, device.getId(), "magnetometer");
        alertInfo = result.getAlertInfo();
        return result.isConditionIsTrue();
    }
}
