package edu.cmu.sei.kalki.rulebooks.UdooNeo;


import com.deliveredtechnologies.rulebook.annotation.*;

@Rule()
public class Gyro extends ThreeAxisRule {
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
        setAlertCondition("UdooNeo-gyro");

        double gyroX = Double.valueOf(status.getAttributes().get("gyroscopeX"));
        double gyroY = Double.valueOf(status.getAttributes().get("gyroscopeY"));
        double gyroZ = Double.valueOf(status.getAttributes().get("gyroscopeZ"));

        double gyroXBound = Double.valueOf(alertCondition.getVariables().get("gyroerometerX"));
        double gyroYBound = Double.valueOf(alertCondition.getVariables().get("gyroerometerY"));
        double gyroZBound = Double.valueOf(alertCondition.getVariables().get("gyroerometerZ"));
        double gyroModLimit = Double.valueOf(alertCondition.getVariables().get("modulus"));
        if (	alertingAxis(gyroX, (gyroXBound*-1), gyroXBound) ||
                alertingAxis(gyroY, (gyroYBound*-1), gyroYBound) ||
                alertingAxis(gyroZ, (gyroZBound*-1), gyroZBound) ||
                alertingModulus(gyroX, gyroY, gyroZ, gyroModLimit)) {
            return true;
        }

        return false;
    }

}
