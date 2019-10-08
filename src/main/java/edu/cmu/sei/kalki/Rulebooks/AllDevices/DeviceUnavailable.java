package edu.cmu.sei.kalki.rulebooks.AllDevices;

import edu.cmu.sei.kalki.rulebooks.RulebookRule;
import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

import java.util.List;

public class DeviceUnavailable extends RulebookRule {

    @Override
    public boolean conditionIsTrue() {
        setAlertCondition("device-unavailable");
        int count = 0;

        List<DeviceStatus> deviceStatuses = Postgres.findNDeviceStatuses(device.getId(), 10);

        // not enough statuses to trigger alert
        if (deviceStatuses.size() <= 5)
            return false;

        // if diff between timestamps is > device's sampling rate more than 4 times
        for(int i=1; i<deviceStatuses.size();i++){
            long timestamp1 = deviceStatuses.get(i).getTimestamp().getTime();
            long timestamp2 = deviceStatuses.get(i-1).getTimestamp().getTime();

            if((timestamp1 - timestamp2) > device.getSamplingRate())
                count++;
        }

        if (count >=  5)
            return true;

        return false;
    }
}
