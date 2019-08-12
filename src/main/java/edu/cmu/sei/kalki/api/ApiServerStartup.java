package edu.cmu.sei.kalki.api;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class ApiServerStartup {
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
            httpServer.start();

            System.out.println("URI: "+httpServer.getURI().toString());
        } catch (Exception e) {
            System.out.println("Error starting IoT Interface API Server");
            e.printStackTrace();
        }
    }
}