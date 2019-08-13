package edu.cmu.sei.kalki;

import edu.cmu.sei.kalki.api.ApiServerStartup;
import edu.cmu.sei.kalki.database.DatabaseListener;
import edu.cmu.sei.ttg.kalki.database.Postgres;

import java.util.logging.Level;
import java.util.logging.Logger;


public class DeviceController {
    private static Logger logger = Logger.getLogger("device-controller");

    public static void main(String[] args) {
        Postgres.initialize("localhost", "5432", "kalkidb", "kalkiuser", "kalkipass");
        Postgres.setLoggingLevel(Level.OFF);

        DatabaseListener listener = new DatabaseListener();
        listener.start();
        logger.info("[DeviceController] Database listener started.");

        ApiServerStartup.start();
        logger.info("[DeviceController] ApiServerStartup started");
    }
}
