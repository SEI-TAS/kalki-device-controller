package edu.cmu.sei.kalki.rulebooks.UdooNeo;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

import java.util.List;

@Rule()
public class TemperatureAverage extends RulebookRule {

    public TemperatureAverage(){ }

    public boolean conditionIsTrue(){
        setAlertCondition("unts-temperature-avg");

        String stateCondition = alertCondition.getVariables().get("state");
        if(!device.getCurrentState().getName().equals(stateCondition))
            return false;

        double temp = Double.valueOf(status.getAttributes().get("temp_input"));
        int numStatuses = Integer.valueOf(alertCondition.getVariables().get("average"));

        List<DeviceStatus> lastNStatuses = Postgres.findNDeviceStatuses(device.getId(), numStatuses);
        double avg = calculateAverage(lastNStatuses);

        if(temp > (avg + 2)){
            alertInfo = "Temperature was greater than avg+2 (avg="+avg+")";
            return true;
        }
        else if(temp < (avg -2)) {
            alertInfo = "Temperature was less than avg-2 (avg="+avg+")";
            return true;
        }
        return false;
    }

    private double calculateAverage(List<DeviceStatus> statuses) {
        double sum = 0;
        double num = statuses.size();
        for(DeviceStatus s:statuses){
            sum += Double.valueOf(s.getAttributes().get("temp_input"));
        }
        return sum / num;
    }

}
