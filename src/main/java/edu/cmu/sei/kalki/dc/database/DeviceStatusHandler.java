package edu.cmu.sei.kalki.dc.database;

import edu.cmu.sei.kalki.db.daos.DeviceStatusDAO;
import edu.cmu.sei.kalki.dc.rulebooks.AlertConditionTester;
import edu.cmu.sei.kalki.db.database.Postgres;
import edu.cmu.sei.kalki.db.listeners.InsertHandler;
import edu.cmu.sei.kalki.db.models.*;

import java.util.logging.Logger;

public class DeviceStatusHandler implements InsertHandler {
    private Logger logger = Logger.getLogger("device-controller");

    public DeviceStatusHandler() {}

    @Override
    public void handleNewInsertion(int newStatusId) {
        logger.info("[DeviceStatusHandler] Received new device status.");
        DeviceStatus status = DeviceStatusDAO.findDeviceStatus(newStatusId);
        AlertConditionTester.testDeviceStatus(status);
    }
}
