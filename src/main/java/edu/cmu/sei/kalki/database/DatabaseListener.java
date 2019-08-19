package edu.cmu.sei.kalki.database;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.listeners.InsertHandler;
import edu.cmu.sei.ttg.kalki.listeners.InsertListener;
import edu.cmu.sei.ttg.kalki.models.Device;

import java.util.List;
import java.util.logging.Logger;

public class DatabaseListener {
    private Logger logger = Logger.getLogger("device-controller");

    public void start() {
        InsertListener.startListening();
        InsertListener.clearHandlers();

        InsertListener.addHandler("deviceinsert", new DeviceHandler("/new-device"));
        InsertListener.addHandler("deviceupdate", new DeviceHandler("/update-device"));
        InsertListener.addHandler("devicesecuritystateinsert", new NewSecurityStateHandler());
        logger.info("[DatabaseListener] Initialized 3 database listeners.");

        // get devices already inserted in system
        List<Device> deviceList = Postgres.findAllDevices();
        DeviceHandler tempHandler = new DeviceHandler("/new-device");
        for (Device d: deviceList){
            tempHandler.handleNewInsertion(d.getId());
        }
    }
}
