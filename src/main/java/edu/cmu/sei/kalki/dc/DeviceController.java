package edu.cmu.sei.kalki.dc;

import edu.cmu.sei.kalki.dc.api.ApiServerStartup;
import edu.cmu.sei.kalki.dc.database.DatabaseListener;
import edu.cmu.sei.kalki.dc.utils.Config;
import edu.cmu.sei.ttg.kalki.database.Postgres;

import java.util.logging.Level;
import java.util.logging.Logger;


public class DeviceController {
    private static Logger logger = Logger.getLogger("device-controller");

    public static void main(String[] args) {
        try
        {
            Config.load("config.json");

            String dbHost = Config.data.get("db_host");
            String dbPort = Config.data.get("db_port");
            String dbName = Config.data.get("db_name");
            String dbUser = Config.data.get("db_user");
            String dbPass = Config.data.get("db_password");
            Postgres.initialize(dbHost, dbPort, dbName, dbUser, dbPass);
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
