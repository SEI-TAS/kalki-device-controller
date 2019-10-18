package edu.cmu.sei.kalki.rulebooks.UdooNeo;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.Alert;
import edu.cmu.sei.ttg.kalki.models.AlertCondition;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

import java.util.HashMap;
import java.util.List;

public class ThreeAxisUtil {

    public ThreeAxisUtil () { }

    public static boolean checkRawValues(DeviceStatus status, AlertCondition alertCondition, String sensor) {
        double x = Double.valueOf(status.getAttributes().get(sensor+"X"));
        double y = Double.valueOf(status.getAttributes().get(sensor+"Y"));
        double z = Double.valueOf(status.getAttributes().get(sensor+"Z"));
        double modulus = Math.sqrt(x*x + y*y + z*z);

        double xBound = Double.valueOf(alertCondition.getVariables().get(sensor+"X"));
        double yBound = Double.valueOf(alertCondition.getVariables().get(sensor+"Y"));
        double zBound = Double.valueOf(alertCondition.getVariables().get(sensor+"Z"));
        double modLimit = Double.valueOf(alertCondition.getVariables().get("modulus"));

        if (	ThreeAxisUtil.alertingAxis(x, (xBound*-1), xBound) ||
                ThreeAxisUtil.alertingAxis(y, (yBound*-1), yBound) ||
                ThreeAxisUtil.alertingAxis(z, (zBound*-1), zBound) ||
                ThreeAxisUtil.alertingModulus(modulus, modLimit)) {
            return true;
        }

        return false;
    }

    public static boolean checkAgainstAverages(DeviceStatus status, AlertCondition alertCondition, int deviceId, String sensor) {
        int numStatuses = Integer.valueOf(alertCondition.getVariables().get("average"));
        List<DeviceStatus> lastNStatuses = Postgres.findSubsetNDeviceStatuses(deviceId, numStatuses, status.getId());

        HashMap<String, Double> averages = calculateAverages(lastNStatuses, sensor);

        double x = Math.abs(Double.valueOf(status.getAttributes().get(sensor+"X")));
        double y = Math.abs(Double.valueOf(status.getAttributes().get(sensor+"Y")));
        double z = Math.abs(Double.valueOf(status.getAttributes().get(sensor+"Z")));
        double mod = Math.sqrt(x*x + y*y + z*z);

        if(x > averages.get("x") || y > averages.get("y") || z > averages.get("z") || mod > averages.get("mod")){
            return true;
        }

        return false;
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
