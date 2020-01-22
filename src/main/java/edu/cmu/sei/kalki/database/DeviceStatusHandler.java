package edu.cmu.sei.kalki.database;

import edu.cmu.sei.kalki.rulebooks.AlertConditionTester;
import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.listeners.InsertHandler;
import edu.cmu.sei.ttg.kalki.models.*;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

public class DeviceStatusHandler implements InsertHandler {
    private Logger logger = Logger.getLogger("device-controller");

    public DeviceStatusHandler() {}

    @Override
    public void handleNewInsertion(int newStatusId) {
        logger.info("[DeviceStatusHandler] Received new device status.");
        DeviceStatus status = Postgres.findDeviceStatus(newStatusId);
        AlertConditionTester.testDeviceStatus(status);
    }
}
