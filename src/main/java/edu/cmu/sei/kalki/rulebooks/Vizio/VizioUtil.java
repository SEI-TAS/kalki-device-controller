package edu.cmu.sei.kalki.rulebooks.Vizio;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.AlertCondition;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

import java.util.List;

public class VizioUtil {

    public static boolean inputChanged(int deviceId, DeviceStatus currentStatus) {
        List<DeviceStatus> previousStatuses = Postgres.findSubsetNDeviceStatuses(deviceId, 1, currentStatus.getId());

        if (previousStatuses != null){
            String previousInput = previousStatuses.get(0).getAttributes().get("current-input");
            String currentInput = currentStatus.getAttributes().get("current-input");

            if(!currentInput.equals(previousInput)) // input has changed
                return true;

        }

        return false;
    }

    public static boolean connectedDevices(DeviceStatus currentStatus, AlertCondition condition) {
        int connectedDevicesThreshold = Integer.valueOf(condition.getVariables().get("connected_devices"));
        int connectedDevices = Integer.valueOf(currentStatus.getAttributes().get("input-list-size"));

        if(connectedDevices > connectedDevicesThreshold)
            return true;

        return false;
    }
}
