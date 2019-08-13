package edu.cmu.sei.kalki.database;

import edu.cmu.sei.ttg.kalki.listeners.InsertHandler;
import edu.cmu.sei.ttg.kalki.listeners.InsertListener;
import edu.cmu.sei.ttg.kalki.models.Device;
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
    }
}
