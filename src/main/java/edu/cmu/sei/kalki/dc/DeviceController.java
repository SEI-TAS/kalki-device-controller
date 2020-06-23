package edu.cmu.sei.kalki.dc;

import edu.cmu.sei.kalki.db.utils.Config;
import edu.cmu.sei.kalki.db.utils.LoggerSetup;
import edu.cmu.sei.kalki.dc.api.ApiServerStartup;
import edu.cmu.sei.kalki.dc.database.DatabaseListener;
import edu.cmu.sei.kalki.db.database.Postgres;

import java.util.logging.Level;
import java.util.logging.Logger;


public class DeviceController {
    private static Logger logger = Logger.getLogger("device-controller");

    public static void main(String[] args) {
        try
        {
            Config.load("config.json");
            LoggerSetup.setup();

            Postgres.initializeFromConfig();
            Postgres.setLoggingLevel(Level.OFF);

            DatabaseListener listener = new DatabaseListener();
            listener.start();
            logger.info("[DeviceController] Database listener started.");

            ApiServerStartup.start();
            logger.info("[DeviceController] ApiServerStartup started");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
