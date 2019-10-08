package edu.cmu.sei.kalki.rulebooks.UdooNeo;

import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

@Rule()
public class Magnetometer extends RulebookRule {
    private final double magXLowerBound = 80.0;
    private final double magXUpperBound = 90.0;
    private final double magYLowerBound = 80.0;
    private final double magYUpperBound = 90.0;
    private final double magZLowerBound = 90.0;
    private final double magZUpperBound = 110.0;
    private final double magModLimit = 168.226;

    private final double coefficient = 0.1; // converts raw readings to micro Teslas

    public Magnetometer(){ }

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
        setAlertCondition("unts-magnetometer");

        String stateCondition = alertCondition.getVariables().get("state");
        if(!device.getCurrentState().getName().equals(stateCondition))
            return false;

        return ThreeAxisUtil.checkRawValues(status, alertCondition, "magnetometer");
    }

}
