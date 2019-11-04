package edu.cmu.sei.kalki.rulebooks.UdooNeo;

import com.deliveredtechnologies.rulebook.annotation.Rule;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

@Rule()
public class AccelerationAverage extends RulebookRule {

    public AccelerationAverage() {}

    @Override
    public boolean conditionIsTrue() {
        setAlertCondition("unts-acceleration-avg");

        String stateCondition = alertCondition.getVariables().get("state");
        if(!device.getCurrentState().getName().equals(stateCondition))
            return false;

        return ThreeAxisUtil.checkAgainstAverages(alertCondition, device.getId(),"accelerometer");
    }
}
