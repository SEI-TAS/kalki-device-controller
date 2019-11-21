package edu.cmu.sei.kalki.rulebooks.PhilipsHueLightEmulator;

import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;
import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

import java.util.List;

@Rule()
public class TimeOn extends RulebookRule {

    public TimeOn(){ }

    /*
      Condition: The light is on and the DLink in the group hasn't detected motion within "time-last-change"
     */
    public boolean conditionIsTrue(){
        setAlertCondition("phle-time-on");
        int lastOffCondition = Integer.parseInt(alertCondition.getVariables().get("time-last-change"));
        alertInfo = "Light is on and motion has been detected within " + lastOffCondition +" minutes.";

        // this status is ON && time last change > condition
        if(Boolean.parseBoolean(status.getAttributes().get("isOn"))) {
            List<DeviceStatus> phleStatuses = Postgres.findDeviceStatusesOverTime(device.getId(), lastOffCondition, "minute");
            for(DeviceStatus s: phleStatuses) {
                if(!Boolean.parseBoolean(s.getAttributes().get("isOn"))) //light was off in specified period
                    return false;
            }

            List<Device> devicesInGroup = Postgres.findDevicesByGroup(device.getGroup().getId());
            List<DeviceStatus> dlinkStatuses = null;

            // find the dlink and get statuses for last T minutes
            for(Device d: devicesInGroup){
                if(d.getType().getName().equals("DLink Camera")){
                    dlinkStatuses = Postgres.findDeviceStatusesOverTime(d.getId(), lastOffCondition, "minute");
                    break;
                }
            }

            // if it hasn't detected motion
            for (DeviceStatus ds: dlinkStatuses){
                if (!Boolean.parseBoolean(ds.getAttributes().get("motion_detected")))
                    return true;
            }
        }
        return false;
    }

}
