package edu.cmu.sei.kalki.dc.database;

import edu.cmu.sei.kalki.dc.rulebooks.AlertConditionTester;
import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.listeners.InsertHandler;
import edu.cmu.sei.ttg.kalki.models.*;

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
