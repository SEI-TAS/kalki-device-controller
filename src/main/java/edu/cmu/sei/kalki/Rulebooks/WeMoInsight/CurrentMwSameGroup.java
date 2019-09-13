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
        setAlertCondition("WeMoInsight-current-mw-same-group");
        double currentmw = Double.valueOf(status.getAttributes().get("currentpower"));
        double groupAverage = groupAverage();

        if(currentmw > (groupAverage + 20)){
            return true;
        }
        return false;
    }

    private double groupAverage() {
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

        double avg = sum/numDevices;
        return avg;
    }

}
