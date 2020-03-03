package edu.cmu.sei.kalki.dc.rulebooks;

import edu.cmu.sei.kalki.db.daos.AlertConditionDAO;
import edu.cmu.sei.kalki.db.daos.DeviceDAO;
import edu.cmu.sei.kalki.db.database.Postgres;
import edu.cmu.sei.kalki.db.models.AlertCondition;
import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceStatus;

import java.util.List;
import java.util.logging.Logger;

// Rulebook imports, for later reference
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.model.runner.RuleBookRunner;

public class AlertConditionTester {
    private static Logger logger = Logger.getLogger("device-controller");

    public static void testDeviceStatus(DeviceStatus status){
        Device device = DeviceDAO.findDevice(status.getDeviceId());
        List<AlertCondition> alertConditionList = AlertConditionDAO.findAlertConditionsByDevice(status.getDeviceId());
        NameValueReferableMap factMap = prepareFactMap(device, status, alertConditionList);
        RuleBookRunner deviceSpecific = prepareRulebook(device.getType().getName());
        RuleBookRunner allDevices = prepareRulebook("AllDevices");

        logger.info("[AlertConditionTester] Running rulebooks for DeviceStatus: "+status.getId());
        deviceSpecific.run(factMap);
        allDevices.run(factMap);
    }


    private static NameValueReferableMap prepareFactMap(Device device, DeviceStatus status, List<AlertCondition> alertConditionList) {
        NameValueReferableMap facts = new FactMap();
        facts.setValue("device", device);
        facts.setValue("status", status);
        facts.setValue("alert-conditions", alertConditionList);
        return facts;
    }

    private static RuleBookRunner prepareRulebook(String deviceType) {
        RuleBookRunner ruleBookRunner = null;
        deviceType = deviceType.replaceAll("\\s","");
        logger.info("[AlertConditionTester] "+deviceType+" rulebook selected");
        ruleBookRunner = new RuleBookRunner("edu.cmu.sei.kalki.rulebooks."+deviceType);

        return ruleBookRunner;
    }
}
