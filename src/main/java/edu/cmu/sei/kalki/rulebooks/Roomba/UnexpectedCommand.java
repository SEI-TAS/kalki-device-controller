package edu.cmu.sei.kalki.rulebooks.Roomba;

import com.deliveredtechnologies.rulebook.annotation.Rule;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;

import java.util.Calendar;

@Rule
public class UnexpectedCommand extends RulebookRule {

    @Override
    public boolean conditionIsTrue() {
        setAlertCondition("roomba-unexpected-command");

        if(!hasDailyCycle() && isCleaning()) // shouldn't be cleaning today and is
            return true;
        else if(hasDailyCycle() && isCleaning()) // should be cleaning today
            return shouldntBeCleaning(); // whether it is cleaning at the correct time
        else
            return false;
    }

    private boolean hasDailyCycle() {
        if(status.getAttributes().get("daily_cycle").equals("start"))
            return true;
        else
            return false;
    }

    private boolean isCleaning() {
        if(status.getAttributes().get("current_cycle").equals("clean"))
            return true;
        else
            return false;
    }

    /**
     * Assuming cleaning takes one hour, determine if current time is greater than 1 hour of scheduled time
     * @return false if current time < 1 hour of scheduled time. true otherwise
     */
    private boolean shouldntBeCleaning() {
        int scheduleHour = Integer.parseInt(status.getAttributes().get("daily_hour"));
        int scheduleMins = Integer.parseInt(status.getAttributes().get("daily_minute"));

        Calendar currCalendar = Calendar.getInstance();
        int currHour = currCalendar.get(Calendar.HOUR_OF_DAY);
        int currMins = currCalendar.get(Calendar.MINUTE);

        if(currHour < scheduleHour) // cleaning before scheduled time
            return false;
        else if (currHour - scheduleHour > 1) // cleaning over an hour after scheduled time
            return false;
        else if ((currHour - scheduleHour == 1) && (currMins > scheduleMins)) // cleaning over an hour after scheduled time
            return false;
        else
            return true;
    }
}
