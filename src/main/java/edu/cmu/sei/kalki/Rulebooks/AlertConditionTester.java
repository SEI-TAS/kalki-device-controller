package edu.cmu.sei.kalki.rulebooks;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.AlertCondition;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

import java.util.List;

// Rulebook imports, for later reference
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.model.runner.RuleBookRunner;
import edu.cmu.sei.ttg.kalki.models.DeviceType;

public class AlertConditionTester {
    public static void testDeviceStatus(DeviceStatus status){
        Device device = Postgres.findDevice(status.getDeviceId());
        List<AlertCondition> alertConditionList = Postgres.findAlertConditionsByDevice(status.getDeviceId());
        NameValueReferableMap factMap = prepareFactMap(device, status, alertConditionList);
        RuleBookRunner ruleBookRunner = prepareRulebook(device.getType());
        ruleBookRunner.run(factMap);
    }

    private static NameValueReferableMap prepareFactMap(Device device, DeviceStatus status, List<AlertCondition> alertConditionList) {
        NameValueReferableMap facts = new FactMap();
        facts.setValue("device", device);
        facts.setValue("status", status);
        facts.setValue("alert-conditions", alertConditionList);
        return facts;
    }

    private static RuleBookRunner prepareRulebook(DeviceType deviceType){
        RuleBookRunner ruleBookRunner = null;

        switch (deviceType.getId()){
            case 1: // DLC
                ruleBookRunner = new RuleBookRunner("edu.cmu.sei.kalki.rulebooks.dlc");
                break;
            case 2: // UNTS
                ruleBookRunner = new RuleBookRunner("edu.cmu.sei.kalki.rulebooks.unts");
                break;
            case 3: //WeMo
                ruleBookRunner = new RuleBookRunner("edu.cmu.sei.kalki.rulebooks.wemo");
                break;
            case 4: // PHLE
                ruleBookRunner = new RuleBookRunner("edu.cmu.sei.kalki.rulebooks.phle");
                break;
            default:
                System.out.println("System not configured to test statuses for DeviceType: " + deviceType.getName());
        }

        return ruleBookRunner;
    }
}
