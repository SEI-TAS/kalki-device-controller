package edu.cmu.sei.kalki.dc.api;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import java.util.logging.Logger;

public class ApiServerStartup {
    private static Logger logger = Logger.getLogger("device-controller");

    private static final String API_URL = "/device-controller-api";
    private static final int SERVER_PORT = 9090;
    /**
     * Starts a Jetty server, with handler for notifications
     */
    public static void start() {
        try {
            Server httpServer = new Server(SERVER_PORT);
            ServletContextHandler handler = new ServletContextHandler(httpServer, API_URL);
            handler.addServlet(DeviceStatusServlet.class, "/new-status");
            handler.addServlet(StageLogServlet.class, "/new-stage-log");
            httpServer.start();

            logger.info("[ApiServerStartup] Server started at URI: "+httpServer.getURI().toString());
        } catch (Exception e) {
            logger.severe("[ApiServerStartup] Error starting Device Controller API Server:");
            logger.severe(e.getMessage());
        }
    }
}
