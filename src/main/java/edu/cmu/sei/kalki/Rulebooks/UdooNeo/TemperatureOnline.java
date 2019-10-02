package edu.cmu.sei.kalki.rulebooks.UdooNeo;

import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

@Rule()
public class TemperatureOnline extends RulebookRule {

    public TemperatureOnline(){ }

    public boolean conditionIsTrue(){

        setAlertCondition("unts-temperature-online");
        return false;
    }

}
