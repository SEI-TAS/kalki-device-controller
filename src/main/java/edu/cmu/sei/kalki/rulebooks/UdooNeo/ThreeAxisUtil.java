package edu.cmu.sei.kalki.rulebooks.UdooNeo;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.Alert;
import edu.cmu.sei.ttg.kalki.models.AlertCondition;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

import java.util.HashMap;
import java.util.List;

public class ThreeAxisUtil {

    public ThreeAxisUtil () { }

    public static ThreeAxisResult checkRawValues(DeviceStatus status, AlertCondition alertCondition, String sensor) {
        HashMap<String, Double> values = new HashMap<>();
        values.put("x", Double.valueOf(status.getAttributes().get(sensor+"X")));
        values.put("y", Double.valueOf(status.getAttributes().get(sensor+"Y")));
        values.put("z", Double.valueOf(status.getAttributes().get(sensor+"Z")));
        values.put("mod", Math.sqrt(Math.pow(values.get("x"), 2) + Math.pow(values.get("y"), 2) + Math.pow(values.get("z"), 2)));

        return compareToConditions(values, alertCondition, sensor);
    }

    public static ThreeAxisResult checkAgainstAverages(AlertCondition alertCondition, int deviceId, String sensor) {
        int numStatuses = Integer.valueOf(alertCondition.getVariables().get("average"));
        List<DeviceStatus> lastNStatuses = Postgres.findNDeviceStatuses(deviceId, numStatuses);

        HashMap<String, Double> averages = calculateAverages(lastNStatuses, sensor);

        return compareToConditions(averages, alertCondition, sensor);
    }

    private static ThreeAxisResult compareToConditions(HashMap<String, Double> values, AlertCondition alertCondition, String sensor) {
        double xBound = Double.valueOf(alertCondition.getVariables().get(sensor+"X"));
        double yBound = Double.valueOf(alertCondition.getVariables().get(sensor+"Y"));
        double zBound = Double.valueOf(alertCondition.getVariables().get(sensor+"Z"));
        double modLimit = Double.valueOf(alertCondition.getVariables().get("modulus"));

        ThreeAxisResult result = new ThreeAxisResult(false, "");
        if (alertingAxis(values.get("x"), (xBound*-1), xBound)){
            result = new ThreeAxisResult(true, "Triggered by x-axis value.");
        }
        else if(alertingAxis(values.get("y"), (yBound*-1), yBound)) {
            result = new ThreeAxisResult(true, "Triggered by y-axis value.");
        }
        else if(alertingAxis(values.get("z"), (zBound*-1), zBound)) {
            result = new ThreeAxisResult(true, "Triggered by z-axis value.");
        }
        else if(alertingModulus(values.get("mod"), modLimit)) {
            result = new ThreeAxisResult(true, "Triggered by calculated modulus.");
        }

        return result;
    }

    private static boolean alertingAxis(double axis, double lowerBound, double upperBound) {
        if( axis < lowerBound || axis > upperBound){
            return true;
        }
        return false;
    }

    private static boolean alertingModulus(double modulus, double limit) {
        if(modulus < (-1 * limit) || modulus > limit) {
            return true;
        }
        return false;
    }

    private static HashMap<String, Double> calculateAverages(List<DeviceStatus> statusList, String sensor) {
        double xSum = 0;
        double ySum = 0;
        double zSum = 0;
        double modSum = 0;
        int numStatus = statusList.size();
        HashMap<String, Double> result = new HashMap<>();

        for(DeviceStatus s: statusList){
            double x = Math.abs(Double.valueOf(s.getAttributes().get(sensor+"X")));
            double y = Math.abs(Double.valueOf(s.getAttributes().get(sensor+"Y")));
            double z = Math.abs(Double.valueOf(s.getAttributes().get(sensor+"Z")));
            xSum+=x;
            ySum+=y;
            zSum+=z;
            modSum += Math.sqrt(x*x + y*y + z*z);
        }

        result.put("x", xSum/numStatus);
        result.put("y", ySum/numStatus);
        result.put("z", zSum/numStatus);
        result.put("mod", modSum/numStatus);

        return result;
    }
}
