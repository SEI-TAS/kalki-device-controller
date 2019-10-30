package edu.cmu.sei.kalki.rulebooks.Vizio;

import com.deliveredtechnologies.rulebook.annotation.Rule;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

@Rule
public class InputSource extends RulebookRule {

    @Override
    public boolean conditionIsTrue() {
        setAlertCondition("vizio-input-source");

        return VizioUtil.inputChanged(device.getId(), status);
    }
}
