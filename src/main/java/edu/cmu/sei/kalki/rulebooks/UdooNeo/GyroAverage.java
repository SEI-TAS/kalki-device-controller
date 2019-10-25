package edu.cmu.sei.kalki.rulebooks.UdooNeo;

import com.deliveredtechnologies.rulebook.annotation.Rule;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

@Rule()
public class GyroAverage extends RulebookRule {

    public GyroAverage() {}

    @Override
    public boolean conditionIsTrue() {
        setAlertCondition("unts-gyro-avg");

        String stateCondition = alertCondition.getVariables().get("state");
        if(!device.getCurrentState().getName().equals(stateCondition))
            return false;

        return ThreeAxisUtil.checkAgainstAverages(alertCondition, device.getId(), "gyroscope");
    }
}
