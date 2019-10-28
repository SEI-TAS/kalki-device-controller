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

        List<DeviceStatus> deviceStatuses = Postgres.findSubsetNDeviceStatuses(device.getId(),status.getId(), 1);
        System.out.println("\nstatuses length: "+deviceStatuses.size());

        // if diff between timestamps is > 5*sampling rate
        if(deviceStatuses.size() > 0){
            long timestamp2 = status.getTimestamp().getTime();
            long timestamp1 = deviceStatuses.get(0).getTimestamp().getTime();
            logger.info("\ntimestamp2: "+ timestamp2);
            logger.info("timestamp1: "+ timestamp1);
            logger.info("diff:"+(timestamp2-timestamp1));
            logger.info("samplerate: "+device.getSamplingRate());
            if(Math.abs(timestamp2 - timestamp1) > 5*device.getSamplingRate())
                return true;
        }

        return false;
    }
}
