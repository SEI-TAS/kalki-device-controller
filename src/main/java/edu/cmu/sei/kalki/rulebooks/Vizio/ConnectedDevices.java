package edu.cmu.sei.kalki.rulebooks.Vizio;

import com.deliveredtechnologies.rulebook.annotation.Rule;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

@Rule
public class ConnectedDevices extends RulebookRule {

    @Override
    public boolean conditionIsTrue() {
        setAlertCondition("vizio-connected-devices");

        return VizioUtil.connectedDevices(status, alertCondition);
    }
}
