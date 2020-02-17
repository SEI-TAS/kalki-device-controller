package edu.cmu.sei.kalki.dc.database;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.listeners.InsertListener;
import edu.cmu.sei.ttg.kalki.models.Device;

import java.util.List;
import java.util.logging.Logger;

public class DatabaseListener {
    private Logger logger = Logger.getLogger("device-controller");

    public void start() {
        InsertListener.startListening();
        InsertListener.clearHandlers();
        logger.info("[DatabaseListener] Starting");
        InsertListener.addHandler("deviceinsert", new DeviceHandler(true));
        InsertListener.addHandler("deviceupdate", new DeviceHandler(false));
        InsertListener.addHandler("devicesecuritystateinsert", new NewSecurityStateHandler());
        InsertListener.addHandler("devicestatusinsert",  new DeviceStatusHandler());
        logger.info("[DatabaseListener] Initialized 4 database listeners.");

        // get devices already inserted in system
        List<Device> deviceList = Postgres.findAllDevices();
        DeviceHandler tempHandler = new DeviceHandler(true);
        for (Device d: deviceList){
            tempHandler.handleNewInsertion(d.getId());
        }
    }
}
