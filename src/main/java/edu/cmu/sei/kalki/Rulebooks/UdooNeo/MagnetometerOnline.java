package edu.cmu.sei.kalki.rulebooks.UdooNeo;

import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

@Rule()
public class MagnetometerOnline extends RulebookRule {

    public MagnetometerOnline(){

    }

    public void finalize()
            throws Throwable{
    }

    public boolean conditionIsTrue(){

        setAlertCondition("UdooNeo-magnetometer-online");
        return false;
    }

}
