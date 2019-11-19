package edu.cmu.sei.kalki.rulebooks.AllDevices;

import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;
import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

import java.util.List;

@Rule()
public class DeviceUnavailable extends RulebookRule {

    @Override
    public boolean conditionIsTrue() {
        setAlertCondition("device-unavailable");

        List<DeviceStatus> deviceStatuses = Postgres.findSubsetNDeviceStatuses(device.getId(), 1, status.getId());

        // if diff between timestamps is > 5*sampling rate
        if(deviceStatuses.size() > 0){
            long timestamp2 = status.getTimestamp().getTime();
            long timestamp1 = deviceStatuses.get(0).getTimestamp().getTime();
            if(Math.abs(timestamp2 - timestamp1) > 5*device.getSamplingRate()) {
                alertInfo = "Timestamp difference: " + (Math.abs(timestamp2 - timestamp1));
                return true;
            }

        }

        return false;
    }
}
