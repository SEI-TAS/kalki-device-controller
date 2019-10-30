package edu.cmu.sei.kalki.rulebooks.Vizio;

import com.deliveredtechnologies.rulebook.annotation.Rule;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

@Rule
public class CombinationAlert extends RulebookRule {

    @Override
    public boolean conditionIsTrue(){
        setAlertCondition("vizio-combination-alert");

        if(VizioUtil.inputChanged(device.getId(), status) && VizioUtil.connectedDevices(status, alertCondition))
            return true;
        else
            return false;
    }
}
