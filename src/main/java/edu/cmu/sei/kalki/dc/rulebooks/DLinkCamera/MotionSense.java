package edu.cmu.sei.kalki.dc.rulebooks.DLinkCamera;

import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Device;
import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.dc.rulebooks.RulebookRule;

import java.util.Map;

@Rule()
public class MotionSense extends RulebookRule {

    public MotionSense(){ }

    public boolean conditionIsTrue(){
        setAlertCondition("dlc-motion-sense");

        boolean conditionIsTrue = false; // condition: motion_sense == true && PHLE.isOn == false

        boolean motionDetected = Boolean.parseBoolean(status.getAttributes().get("motion_detected"));
        boolean motionCondition = Boolean.parseBoolean(alertCondition.getVariables().get("motion_detected"));
        if(motionDetected == motionCondition){
            for(Map.Entry<Device, DeviceStatus> entry: device.statusesOfSameGroup().entrySet()){
                Device d = entry.getKey();
                DeviceStatus s = entry.getValue();
                boolean isOn = Boolean.parseBoolean(s.getAttributes().get("isOn"));
                boolean isOnCondition = Boolean.parseBoolean(s.getAttributes().get("isOn"));
                if(isOn == isOnCondition){ // a light is off
                    conditionIsTrue = true;
                    alertInfo = "Motion sensed and a light is off!";
                }
            }
        }



        return conditionIsTrue;
    }

}
