package edu.cmu.sei.kalki.rulebooks.UdooNeo;


import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

@Rule()
public class Gyro extends RulebookRule {
    private final double gyroXLowerBound = -45;
    private final double gyroXUpperBound = 45;
    private final double gyroYLowerBound = -60;
    private final double gyroYUpperBound = 60;
    private final double gyroZLowerBound = -15;
    private final double gyroZUpperBound = 15;
    private final double gyroModLimit = 76.5;

    public Gyro(){ }

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
        setAlertCondition("unts-gyro");

        String stateCondition = alertCondition.getVariables().get("state");
        if(!device.getCurrentState().getName().equals(stateCondition))
            return false;

        return ThreeAxisUtil.checkRawValues(status, alertCondition, "gyroscope");
    }

}
