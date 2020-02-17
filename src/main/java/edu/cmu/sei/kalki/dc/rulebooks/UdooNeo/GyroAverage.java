package edu.cmu.sei.kalki.dc.rulebooks.UdooNeo;

import com.deliveredtechnologies.rulebook.annotation.Rule;
import edu.cmu.sei.kalki.dc.rulebooks.RulebookRule;

@Rule()
public class GyroAverage extends RulebookRule {

    public GyroAverage() {}

    @Override
    public boolean conditionIsTrue() {
        setAlertCondition("unts-gyro-avg");

        String stateCondition = alertCondition.getVariables().get("state");
        if(!device.getCurrentState().getName().equals(stateCondition))
            return false;

        ThreeAxisResult result = ThreeAxisUtil.checkAgainstAverages(alertCondition, device.getId(), "gyroscope");
        alertInfo = result.getAlertInfo();
        return result.isConditionIsTrue();
    }
}
