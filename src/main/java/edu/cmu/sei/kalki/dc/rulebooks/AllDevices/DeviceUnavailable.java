package edu.cmu.sei.kalki.dc.rulebooks.AllDevices;

import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.db.daos.DeviceStatusDAO;
import edu.cmu.sei.kalki.dc.rulebooks.RulebookRule;
import edu.cmu.sei.kalki.db.database.Postgres;
import edu.cmu.sei.kalki.db.models.DeviceStatus;

import java.util.List;

@Rule()
public class DeviceUnavailable extends RulebookRule {

    @Override
    public boolean conditionIsTrue() {
        setAlertCondition("device-unavailable");

        //get last 5 statuses
        List<DeviceStatus> deviceStatuses = DeviceStatusDAO.findNDeviceStatuses(device.getId(), 5);

        // ensure there are at least 5 statuses
        if(deviceStatuses.size() < 5)
            return false;

        //check that all 5 are null
        for(DeviceStatus status: deviceStatuses){
            if(!status.getAttributes().isEmpty()) //if one is not null, device is available
                return false;
        }

        return true;
    }
}
