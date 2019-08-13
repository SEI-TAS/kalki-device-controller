package edu.cmu.sei.kalki.database;

import edu.cmu.sei.ttg.kalki.listeners.InsertHandler;
import edu.cmu.sei.ttg.kalki.listeners.InsertListener;
import edu.cmu.sei.ttg.kalki.models.Device;

public class DatabaseListener {
    public void start() {
        InsertListener.startListening();
        InsertListener.clearHandlers();

        InsertListener.addHandler("deviceinsert", new DeviceHandler("/new-device"));
        InsertListener.addHandler("deviceupdate", new DeviceHandler("/update-device"));
        InsertListener.addHandler("devicesecuritystateinsert", new NewSecurityStateHandler());

    }
}
