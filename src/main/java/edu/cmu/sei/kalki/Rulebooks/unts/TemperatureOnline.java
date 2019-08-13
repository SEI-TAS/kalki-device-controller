package edu.cmu.sei.kalki.rulebooks.unts;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
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

        setAlertName("unts-temperature-online");
        return false;
    }

}
