package edu.cmu.sei.kalki.rulebooks.UdooNeo;

import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

@Rule()
public class TemperatureOnline extends RulebookRule {

    public TemperatureOnline(){

    }

    public void finalize()
            throws Throwable{
    }

    public boolean conditionIsTrue(){

        setAlertCondition("UdooNeo-temperature-online");
        return false;
    }

}
