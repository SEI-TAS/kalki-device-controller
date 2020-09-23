/*
 * Kalki - A Software-Defined IoT Security Platform
 * Copyright 2020 Carnegie Mellon University.
 * NO WARRANTY. THIS CARNEGIE MELLON UNIVERSITY AND SOFTWARE ENGINEERING INSTITUTE MATERIAL IS FURNISHED ON AN "AS-IS" BASIS. CARNEGIE MELLON UNIVERSITY MAKES NO WARRANTIES OF ANY KIND, EITHER EXPRESSED OR IMPLIED, AS TO ANY MATTER INCLUDING, BUT NOT LIMITED TO, WARRANTY OF FITNESS FOR PURPOSE OR MERCHANTABILITY, EXCLUSIVITY, OR RESULTS OBTAINED FROM USE OF THE MATERIAL. CARNEGIE MELLON UNIVERSITY DOES NOT MAKE ANY WARRANTY OF ANY KIND WITH RESPECT TO FREEDOM FROM PATENT, TRADEMARK, OR COPYRIGHT INFRINGEMENT.
 * Released under a MIT (SEI)-style license, please see license.txt or contact permission@sei.cmu.edu for full terms.
 * [DISTRIBUTION STATEMENT A] This material has been approved for public release and unlimited distribution.  Please see Copyright notice for non-US Government use and distribution.
 * This Software includes and/or makes use of the following Third-Party Software subject to its own license:
 * 1. Google Guava (https://github.com/google/guava) Copyright 2007 The Guava Authors.
 * 2. JSON.simple (https://code.google.com/archive/p/json-simple/) Copyright 2006-2009 Yidong Fang, Chris Nokleberg.
 * 3. JUnit (https://junit.org/junit5/docs/5.0.1/api/overview-summary.html) Copyright 2020 The JUnit Team.
 * 4. Play Framework (https://www.playframework.com/) Copyright 2020 Lightbend Inc..
 * 5. PostgreSQL (https://opensource.org/licenses/postgresql) Copyright 1996-2020 The PostgreSQL Global Development Group.
 * 6. Jackson (https://github.com/FasterXML/jackson-core) Copyright 2013 FasterXML.
 * 7. JSON (https://www.json.org/license.html) Copyright 2002 JSON.org.
 * 8. Apache Commons (https://commons.apache.org/) Copyright 2004 The Apache Software Foundation.
 * 9. RuleBook (https://github.com/deliveredtechnologies/rulebook/blob/develop/LICENSE.txt) Copyright 2020 Delivered Technologies.
 * 10. SLF4J (http://www.slf4j.org/license.html) Copyright 2004-2017 QOS.ch.
 * 11. Eclipse Jetty (https://www.eclipse.org/jetty/licenses.html) Copyright 1995-2020 Mort Bay Consulting Pty Ltd and others..
 * 12. Mockito (https://github.com/mockito/mockito/wiki/License) Copyright 2007 Mockito contributors.
 * 13. SubEtha SMTP (https://github.com/voodoodyne/subethasmtp) Copyright 2006-2007 SubEthaMail.org.
 * 14. JSch - Java Secure Channel (http://www.jcraft.com/jsch/) Copyright 2002-2015 Atsuhiko Yamanaka, JCraft,Inc. .
 * 15. ouimeaux (https://github.com/iancmcc/ouimeaux) Copyright 2014 Ian McCracken.
 * 16. Flask (https://github.com/pallets/flask) Copyright 2010 Pallets.
 * 17. Flask-RESTful (https://github.com/flask-restful/flask-restful) Copyright 2013 Twilio, Inc..
 * 18. libvirt-python (https://github.com/libvirt/libvirt-python) Copyright 2016 RedHat, Fedora project.
 * 19. Requests: HTTP for Humans (https://github.com/psf/requests) Copyright 2019 Kenneth Reitz.
 * 20. netifaces (https://github.com/al45tair/netifaces) Copyright 2007-2018 Alastair Houghton.
 * 21. ipaddress (https://github.com/phihag/ipaddress) Copyright 2001-2014 Python Software Foundation.
 * DM20-0543
 *
 */
package edu.cmu.sei.kalki.dc;

import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceCommand;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

/**
 * Implements the commands to send requests to the IoTInterface's API.
 */
public class IoTInterfaceAPI
{
    private static final String LOG_ID = "[IoTInterfaceAPI] ";
    private final int ATTEMPTS = 15;

    private static final String PORT = "5050";
    private static final String BASE_URL = "/iot-interface-api";
    private static final String SEND_COMMAND = "/send-command";
    private static final String NEW_DEVICE = "/new-device";
    private static final String UPDATE_DEVICE = "/update-device";

    private Logger logger = Logger.getLogger("device-controller");

    /**
     * Returns the base URL.
     * @param serverIP
     * @return
     */
    private String getBaseURL(String serverIP) {
        return "http://" + serverIP + ":" + PORT + BASE_URL;
    }

    /**
     * Sends device information for a new device.
     * @param dev
     */
    public void sendNewDeviceInfo(Device dev) {
        sendDeviceInfo(dev, NEW_DEVICE);
    }

    /**
     * Sends updated device information for an existing device.
     * @param dev
     */
    public void sendUpdatedDeviceInfo(Device dev) {
        sendDeviceInfo(dev, UPDATE_DEVICE);
    }

    /**
     * Sends device information.
     * @param dev
     * @param endpoint
     */
    private void sendDeviceInfo(Device dev, String endpoint) {
        logger.info("Sending device info to IoTInterface: " + dev.toString());
        JSONObject json = new JSONObject(dev.toString());
        for(int i=0; i<ATTEMPTS; i++){
            if(this.sendToIotInterface(dev, endpoint, json)) {
                break;
            }
            else {
                try {
                    logger.severe(LOG_ID + "Attempting to reconnect...");
                    sleep(1000);
                } catch (InterruptedException ex) {
                    logger.severe(LOG_ID + "Error waiting to retry sending info: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Sends a group of commands.
     * @param dev
     * @param comms
     */
    public void sendCommands(Device dev, List<DeviceCommand> comms) {
        JSONObject json = new JSONObject();
        json.put("command-list", comms);
        json.put("device",new JSONObject(dev.toString()));
        this.sendToIotInterface(dev, SEND_COMMAND, json);
    }

    /**
     * Sends a request to the IoT Interface.
     * @param dev
     * @param endpoint
     * @param payload
     * @return
     */
    private boolean sendToIotInterface(Device dev, String endpoint, JSONObject payload) {
        String fullUrlString = getBaseURL(dev.getDataNode().getIpAddress()) + endpoint;

        try {
            logger.info(LOG_ID + " Sending to " + fullUrlString + ": " + payload.toString());
            URL fullUrl = new URL(fullUrlString);
            HttpURLConnection httpCon = (HttpURLConnection) fullUrl.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");

            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            out.write(payload.toString());
            out.close();
            httpCon.getInputStream();

            return true;
        } catch (Exception e) {
            logger.severe(LOG_ID + "Error sending message to IoT Interface API " + fullUrlString + ": " + dev.toString());
            logger.severe(e.getMessage());
            return false;
        }
    }
}
