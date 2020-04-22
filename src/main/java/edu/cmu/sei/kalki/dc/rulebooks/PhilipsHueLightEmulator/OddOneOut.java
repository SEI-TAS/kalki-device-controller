package edu.cmu.sei.kalki.dc.rulebooks.PhilipsHueLightEmulator;

import java.util.Map;

import edu.cmu.sei.kalki.dc.rulebooks.RulebookRule;
import edu.cmu.sei.kalki.db.models.DeviceStatus;
import edu.cmu.sei.kalki.db.models.Device;
import com.deliveredtechnologies.rulebook.annotation.*;

@Rule()
public class OddOneOut extends RulebookRule
{

    public OddOneOut(){ }

    public boolean conditionIsTrue(){
        boolean conditionIsTrue = true;
        setAlertCondition("phle-odd-one-out");
        alertInfo = "This light was the only one on in its group.";
        // if this device is OFF
        if(!Boolean.parseBoolean(status.getAttributes().get("isOn"))) {
            // get other devices of same type, check their status
            Map<Device, DeviceStatus> statuses = device.statusesOfSameType();
            if(statuses.size() == 1) //there is only this device
                return false;
            for(Map.Entry<Device, DeviceStatus> entry: device.statusesOfSameType().entrySet()){
                Device d = entry.getKey();
                DeviceStatus s = entry.getValue();
                boolean on = Boolean.parseBoolean(s.getAttributes().get("isOn"));

                if(device.getId() != d.getId() && !on){ // another light is also off
                    conditionIsTrue = false;
                    break;
                }
            }
        } else {
            conditionIsTrue = false;
        }

        return conditionIsTrue;
    }
}
