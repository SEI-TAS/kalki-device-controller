package edu.cmu.sei.kalki.rulebooks;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.AlertCondition;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

// Rulebook imports, for later reference
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.model.runner.RuleBookRunner;

public class AlertConditionTester {
    private static Logger logger = Logger.getLogger("device-controller");
    private static String untsName;
    private static String phleName;
    private static String wemoName;
    private static String dlinkName;

    public static void testDeviceStatus(DeviceStatus status){
        Device device = Postgres.findDevice(status.getDeviceId());
        List<AlertCondition> alertConditionList = Postgres.findAlertConditionsByDevice(status.getDeviceId());
        NameValueReferableMap factMap = prepareFactMap(device, status, alertConditionList);
        RuleBookRunner ruleBookRunner = prepareRulebook(device.getType().getName());
        setDeviceTypes();

        logger.info("[AlertConditionTester] Running rulebook for DeviceStatus: "+status.getId());
        ruleBookRunner.run(factMap);
    }

    private static void setDeviceTypes() {
        try{
            Properties prop = new Properties();
            String fileName = "device-type.config";
            InputStream is = AlertConditionTester.class.getResourceAsStream("/device-type.config");
            prop.load(is);

            untsName = prop.getProperty("UNTS");
            phleName = prop.getProperty("PHLE");
            wemoName = prop.getProperty("WEMO");
            dlinkName = prop.getProperty("DLC");
        }
        catch(IOException e){
            logger.severe("Error reading device-types from config.");
        }
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

        if (deviceType.equals(dlinkName)) {
            logger.info("[AlertConditionTester] "+dlinkName+" rulebook selected");
            ruleBookRunner = new RuleBookRunner("edu.cmu.sei.kalki.rulebooks.dlc");
        } else if (deviceType.equals(untsName)) {
            logger.info("[AlertConditionTester] "+untsName+" rulebook selected");
            ruleBookRunner = new RuleBookRunner("edu.cmu.sei.kalki.rulebooks.unts");
        } else if (deviceType.equals(wemoName)) {
            logger.info("[AlertConditionTester] "+wemoName+" rulebook selected");
            ruleBookRunner = new RuleBookRunner("edu.cmu.sei.kalki.rulebooks.wemo");
        } else if (deviceType.equals(phleName)) {
            logger.info("[AlertConditionTester] "+phleName+" rulebook selected");
            ruleBookRunner = new RuleBookRunner("edu.cmu.sei.kalki.rulebooks.phle");
        } else {
            logger.severe("System not configured to test statuses for DeviceType: " + deviceType);
        }

        return ruleBookRunner;
    }
}
