package edu.cmu.sei.kalki.rulebooks.unts;

import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

@Rule()
public class Temperature extends RulebookRule {

    public Temperature(){ }

    /**
     * UNTS DeviceStatus.attributes
     * {
     *     accelerometerX: "",
     *     accelerometerY: "",
     *     accelerometerZ: "",
     *     gyroscopeX: "",
     *     gyroscopeY: "",
     *     gyroscopeZ: "",
     *     magnetometerX: "",
     *     magnetometerY: "",
     *     magnetometerZ: "",
     *     tempmax: "",
     *     tempmax_hyst: "",
     *     tempinput: ""
     * }
     *
     *
     * @return
     */
    public boolean conditionIsTrue(){
        setAlertCondition("unts-temperature");
        double temp = Double.valueOf(status.getAttributes().get("temp_input"));
        double tempLowerBound = Double.valueOf(alertCondition.getVariables().get("temp_input_lower"));
        double tempUpperBound = Double.valueOf(alertCondition.getVariables().get("temp_input_upper"));
        if (temp < tempLowerBound || temp > tempUpperBound){
            return true;
        }
        return false;
    }

}
