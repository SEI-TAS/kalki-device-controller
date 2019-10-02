package edu.cmu.sei.kalki.rulebooks.WeMoInsight;

import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Device;
import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

import java.util.Map;

@Rule()
public class CurrentMwSameGroup extends RulebookRule {

    public CurrentMwSameGroup(){ }

    public boolean conditionIsTrue(){
        setAlertCondition("wemo-current-mw-same-group");
        double currentmw = Double.valueOf(status.getAttributes().get("currentpower"));
        double groupAverage = 0;
        try {
            groupAverage = groupAverage();
        } catch (Exception e){
            return false;
        }

        if(currentmw > (groupAverage + 20)){
            return true;
        }
        return false;
    }

    private double groupAverage() throws Exception {
        // get group statuses
        Map<Device, DeviceStatus> groupStatuses = device.statusesOfSameGroup();
        int numDevices = 0;
        int sum = 0;
        for(Map.Entry<Device,DeviceStatus> entry: groupStatuses.entrySet()){
            Device d = entry.getKey();
            DeviceStatus s = entry.getValue();
            // ensure correct device type and
            // don't include device in question
            if (d.getType().getId() == device.getType().getId() && d.getId() != device.getId()) {
                sum += Integer.parseInt(s.getAttributes().get("currentpower"));
                numDevices++;
            }
        }

        if (numDevices < 1)
            throw new Exception("No devices of same type in the group.");

        double avg = sum/numDevices;
        return avg;
    }

}
