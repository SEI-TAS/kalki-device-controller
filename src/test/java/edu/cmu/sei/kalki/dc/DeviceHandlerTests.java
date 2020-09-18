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

import edu.cmu.sei.kalki.db.daos.StageLogDAO;
import edu.cmu.sei.kalki.db.models.DataNode;
import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceType;
import edu.cmu.sei.kalki.db.models.StageLog;

import edu.cmu.sei.kalki.dc.database.DeviceHandler;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

public class DeviceHandlerTests extends BaseTest{
    private Device testDevice;

    @Mock
    private IoTInterfaceAPI ioTInterfaceAPI;

    @InjectMocks
    private DeviceHandler newDeviceHandler = new DeviceHandler(true);

    @InjectMocks
    private DeviceHandler updateDeviceHandler = new DeviceHandler(false);

    @BeforeEach
    public void setup() {
        super.setup();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void handleNewInsertionNewDevice(){
        newDeviceHandler.handleNewInsertion(testDevice.getId());

        Mockito.verify(ioTInterfaceAPI).sendNewDeviceInfo(Mockito.argThat((Device device) -> device.getId() == testDevice.getId()));

        List<StageLog> stageLogList = StageLogDAO.findAllStageLogs();
        assertEquals(0, stageLogList.size(), String.format("A stage log was inserted but shouldn't have been. Count: {}", stageLogList.size()));
    }

    @Test
    public void handleNewInsertionUpdateDevice(){
        testDevice.setSamplingRate(0);
        testDevice.insertOrUpdate();

        updateDeviceHandler.handleNewInsertion(testDevice.getId());

        Mockito.verify(ioTInterfaceAPI).sendUpdatedDeviceInfo(Mockito.argThat((Device device) -> device.getId() == testDevice.getId()));

        List<StageLog> stageLogList = StageLogDAO.findAllStageLogs();
        assertEquals(1, stageLogList.size(), String.format("The number of stage logs inserted was not equal to 1. Count: {}", stageLogList.size()));

        StageLog log = stageLogList.get(0);
        StageLog testLog = new StageLog(testDevice.getCurrentState().getId(), StageLog.Action.INCREASE_SAMPLE_RATE, StageLog.Stage.REACT, "Device updated: "+testDevice.getId());
        assertEquals(testLog.getDeviceSecurityStateId(), log.getDeviceSecurityStateId());
        assertEquals(testLog.getAction(), log.getAction());
        assertEquals(testLog.getStage(), log.getStage());
        assertEquals(testLog.getInfo(), log.getInfo());
    }

    public void insertData() {
        DeviceType type = new DeviceType(-1, "name");
        type.insert();

        DataNode dataNode = new DataNode("Test Node", "localhost");
        dataNode.insert();

        testDevice = new Device("1", "1", type, "ip", 1,1, dataNode, "");
        testDevice.insert();
    }
}
