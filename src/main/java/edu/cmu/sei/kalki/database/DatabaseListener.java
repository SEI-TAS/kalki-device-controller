package edu.cmu.sei.kalki.database;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.listeners.InsertHandler;
import edu.cmu.sei.ttg.kalki.listeners.InsertListener;
import edu.cmu.sei.ttg.kalki.models.Device;

import java.util.List;
import java.util.logging.Logger;

public class DatabaseListener {
    private Logger logger = Logger.getLogger("device-controller");

    public void start(String apiUrl) {
        InsertListener.startListening();
        InsertListener.clearHandlers();
        logger.info("[DatabaseListener] apiUrl: "+apiUrl);
        InsertListener.addHandler("deviceinsert", new DeviceHandler(apiUrl+"/new-device"));
        InsertListener.addHandler("deviceupdate", new DeviceHandler(apiUrl+"/update-device"));
        InsertListener.addHandler("devicesecuritystateinsert", new NewSecurityStateHandler(apiUrl));
        logger.info("[DatabaseListener] Initialized 3 database listeners.");

        // get devices already inserted in system
        List<Device> deviceList = Postgres.findAllDevices();
        DeviceHandler tempHandler = new DeviceHandler(apiUrl+"/new-device");
        for (Device d: deviceList){
            tempHandler.handleNewInsertion(d.getId());
        }
    }
}
