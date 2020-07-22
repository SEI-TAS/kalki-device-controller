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

import edu.cmu.sei.kalki.db.daos.DeviceStatusDAO;
import edu.cmu.sei.kalki.db.daos.StageLogDAO;
import edu.cmu.sei.kalki.db.models.*;
import edu.cmu.sei.kalki.dc.api.ApiServerStartup;
import org.json.JSONObject;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

public class ServletTests extends BaseTest {
    private final String apiUrl = "http://0.0.0.0:9090/device-controller-api";

    private DeviceSecurityState deviceSecurityState;

    @Test
    public void newStatusTest() {
        DeviceStatus status = new DeviceStatus(1,new HashMap<>(), new Timestamp(System.currentTimeMillis()));
        JSONObject object = new JSONObject(status.toString());

        int response = sendToApiEndpoint(object, apiUrl + "/new-status");
        assertEquals(200, response, String.format("Invalid response. Expected {} but got {}", 200, response));

        List<DeviceStatus> statusList = DeviceStatusDAO.findAllDeviceStatuses();
        assertEquals(1, statusList.size()); // should only be 1 status

        DeviceStatus result = statusList.get(0);

        // all properties should be the same except the id
        assertNotEquals(status.getId(), result.getId());
        assertEquals(status.getDeviceId(), result.getDeviceId());
        assertEquals(status.getTimestamp(), result.getTimestamp());
        assertEquals(status.getAttributes().toString(), result.getAttributes().toString());
    }

    @Test
    public void newStageLogTest() {
        StageLog log = new StageLog(deviceSecurityState.getId(), StageLog.Action.OTHER, StageLog.Stage.STIMULUS, "info");
        JSONObject object = new JSONObject(log.toString());

        int response = sendToApiEndpoint(object, apiUrl + "/new-stage-log");
        assertEquals(200, response, String.format("Invalid response. Expected {} but got {}", 200, response));

        List<StageLog> stageLogList = StageLogDAO.findAllStageLogs();
        assertEquals(1, stageLogList.size(), String.format("More than one stage log found. Expected {} but got {}", 1, stageLogList.size()));

        StageLog result = stageLogList.get(0);

        // all properties should be the same except id
        assertNotEquals(log.getId(), result.getId());
        assertEquals(log.getDeviceSecurityStateId(), result.getDeviceSecurityStateId());
        assertNotEquals(log.getTimestamp(), result.getTimestamp()); //Timestamp should be assigned on insertion
        assertEquals(log.getAction(), result.getAction());
        assertEquals(log.getStage(), result.getStage());
        assertEquals(log.getInfo(), result.getInfo());
    }

    private int sendToApiEndpoint(JSONObject payload, String apiEndpoint){
        try {
            URL fullUrl = new URL(apiEndpoint);
            HttpURLConnection httpCon = (HttpURLConnection) fullUrl.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");
            httpCon.setRequestProperty("Accept", "application/json");

            try(OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream())){
                out.write(payload.toString());
            }

            httpCon.disconnect();
            return httpCon.getResponseCode();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public void insertData() {
        ApiServerStartup.start();

        DeviceType type = new DeviceType(-1, "Description");
        type.insert();

        DataNode dataNode = new DataNode("Test Node", "localhost");
        dataNode.insert();

        Device device = new Device("Hi", "desc", type, "ip", 1, 1, dataNode);
        device.insert();

        deviceSecurityState = device.getCurrentState();
    }
}
