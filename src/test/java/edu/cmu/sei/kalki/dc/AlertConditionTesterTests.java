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

import edu.cmu.sei.kalki.dc.rulebooks.AlertConditionTester;
import edu.cmu.sei.kalki.dc.rulebooks.RulebookInput;
import edu.cmu.sei.kalki.db.models.*;
import edu.cmu.sei.kalki.db.daos.*;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlertConditionTesterTests extends BaseTest {

    private AlertConditionTester alertConditionTester;

    private DeviceType deviceType;
    private DataNode dataNode;
    private Device device;
    private DeviceSensor deviceSensor;
    private AlertType alertType;
    private AlertTypeLookup alertTypeLookup;

    @BeforeEach
    public void setup() {
        super.setup();
    }

    @Test
    public void testTestDeviceStatusEqualsNoAlert() {
        AlertCondition alertCondition = new AlertCondition(device.getId(), deviceSensor.getId(), deviceSensor.getName(), 1, AlertCondition.ComparisonOperator.EQUAL, AlertCondition.Calculation.NONE, device.getId(), "1");
        alertCondition.insert();

        AlertContext alertContext = new AlertContext(device.getId(), device.getName(), AlertContext.LogicalOperator.OR, alertTypeLookup.getId(), alertType.getName());
        alertContext.addCondition(alertCondition);
        alertContext.insert();

        DeviceStatus deviceStatus = genDeviceStatus("0");
        sleep(500);

        alertConditionTester.testDeviceStatus(deviceStatus);

        List<Alert> alertList = AlertDAO.findAlertsByDevice(device.getId());

        assertEquals(0, alertList.size());
    }

    @Test
    public void testTestDeviceStatusEqualsAlert() {
        AlertCondition alertCondition = new AlertCondition(device.getId(), deviceSensor.getId(), deviceSensor.getName(), 1, AlertCondition.ComparisonOperator.EQUAL, AlertCondition.Calculation.NONE, device.getId(), "1");
        alertCondition.insert();

        AlertContext alertContext = new AlertContext(device.getId(), device.getName(), AlertContext.LogicalOperator.OR, alertTypeLookup.getId(), alertType.getName());
        alertContext.addCondition(alertCondition);
        alertContext.insert();

        DeviceStatus deviceStatus = genDeviceStatus("1");
        sleep(500);

        alertConditionTester.testDeviceStatus(deviceStatus);

        List<Alert> alertList = AlertDAO.findAlertsByDevice(device.getId());

        assertEquals(1, alertList.size());
    }

    @Test
    public void testTestDeviceStatusGreaterNoAlert() {
        AlertCondition alertCondition = new AlertCondition(device.getId(), deviceSensor.getId(), deviceSensor.getName(), 1, AlertCondition.ComparisonOperator.GREATER, AlertCondition.Calculation.NONE, device.getId(), "1");
        alertCondition.insert();

        AlertContext alertContext = new AlertContext(device.getId(), device.getName(), AlertContext.LogicalOperator.OR, alertTypeLookup.getId(), alertType.getName());
        alertContext.addCondition(alertCondition);
        alertContext.insert();

        DeviceStatus deviceStatus = genDeviceStatus("0");
        sleep(500);

        alertConditionTester.testDeviceStatus(deviceStatus);

        List<Alert> alertList = AlertDAO.findAlertsByDevice(device.getId());

        assertEquals(0, alertList.size());
    }


    @Test
    public void testTestDeviceStatusGreaterAlert() {
        AlertCondition alertCondition = new AlertCondition(device.getId(), deviceSensor.getId(), deviceSensor.getName(), 1, AlertCondition.ComparisonOperator.GREATER, AlertCondition.Calculation.NONE, device.getId(), "1");
        alertCondition.insert();

        AlertContext alertContext = new AlertContext(device.getId(), device.getName(), AlertContext.LogicalOperator.OR, alertTypeLookup.getId(), alertType.getName());
        alertContext.addCondition(alertCondition);
        alertContext.insert();

        DeviceStatus deviceStatus = genDeviceStatus("2");
        sleep(500);

        alertConditionTester.testDeviceStatus(deviceStatus);

        List<Alert> alertList = AlertDAO.findAlertsByDevice(device.getId());

        assertEquals(1, alertList.size());
    }

    @Test
    public void testTestDeviceStatusGreaterOrEqualNoAlert() {
        AlertCondition alertCondition = new AlertCondition(device.getId(), deviceSensor.getId(), deviceSensor.getName(), 1, AlertCondition.ComparisonOperator.GREATER_OR_EQUAL, AlertCondition.Calculation.NONE, device.getId(), "1");
        alertCondition.insert();

        AlertContext alertContext = new AlertContext(device.getId(), device.getName(), AlertContext.LogicalOperator.OR, alertTypeLookup.getId(), alertType.getName());
        alertContext.addCondition(alertCondition);
        alertContext.insert();

        DeviceStatus deviceStatus = genDeviceStatus("0");
        sleep(500);

        alertConditionTester.testDeviceStatus(deviceStatus);

        List<Alert> alertList = AlertDAO.findAlertsByDevice(device.getId());

        assertEquals(0, alertList.size());
    }


    @Test
    public void testTestDeviceStatusGreaterOrEqualAlert() {
        AlertCondition alertCondition = new AlertCondition(device.getId(), deviceSensor.getId(), deviceSensor.getName(), 1, AlertCondition.ComparisonOperator.GREATER_OR_EQUAL, AlertCondition.Calculation.NONE, device.getId(), "1");
        alertCondition.insert();

        AlertContext alertContext = new AlertContext(device.getId(), device.getName(), AlertContext.LogicalOperator.OR, alertTypeLookup.getId(), alertType.getName());
        alertContext.addCondition(alertCondition);
        alertContext.insert();

        DeviceStatus deviceStatus = genDeviceStatus("1");
        sleep(500);

        alertConditionTester.testDeviceStatus(deviceStatus);

        List<Alert> alertList = AlertDAO.findAlertsByDevice(device.getId());

        assertEquals(1, alertList.size());
    }

    @Test
    public void testTestDeviceStatusLessNoAlert() {
        AlertCondition alertCondition = new AlertCondition(device.getId(), deviceSensor.getId(), deviceSensor.getName(), 1, AlertCondition.ComparisonOperator.LESS, AlertCondition.Calculation.NONE, device.getId(), "1");
        alertCondition.insert();

        AlertContext alertContext = new AlertContext(device.getId(), device.getName(), AlertContext.LogicalOperator.OR, alertTypeLookup.getId(), alertType.getName());
        alertContext.addCondition(alertCondition);
        alertContext.insert();

        DeviceStatus deviceStatus = genDeviceStatus("2");
        sleep(500);

        alertConditionTester.testDeviceStatus(deviceStatus);

        List<Alert> alertList = AlertDAO.findAlertsByDevice(device.getId());

        assertEquals(0, alertList.size());
    }


    @Test
    public void testTestDeviceStatusLessAlert() {
        AlertCondition alertCondition = new AlertCondition(device.getId(), deviceSensor.getId(), deviceSensor.getName(), 1, AlertCondition.ComparisonOperator.LESS, AlertCondition.Calculation.NONE, device.getId(), "1");
        alertCondition.insert();

        AlertContext alertContext = new AlertContext(device.getId(), device.getName(), AlertContext.LogicalOperator.OR, alertTypeLookup.getId(), alertType.getName());
        alertContext.addCondition(alertCondition);
        alertContext.insert();

        DeviceStatus deviceStatus = genDeviceStatus("0");
        sleep(500);

        alertConditionTester.testDeviceStatus(deviceStatus);

        List<Alert> alertList = AlertDAO.findAlertsByDevice(device.getId());

        assertEquals(1, alertList.size());
    }

    @Test
    public void testTestDeviceStatusLessOrEqualNoAlert() {
        AlertCondition alertCondition = new AlertCondition(device.getId(), deviceSensor.getId(), deviceSensor.getName(), 1, AlertCondition.ComparisonOperator.LESS_OR_EQUAL, AlertCondition.Calculation.NONE, device.getId(), "1");
        alertCondition.insert();

        AlertContext alertContext = new AlertContext(device.getId(), device.getName(), AlertContext.LogicalOperator.OR, alertTypeLookup.getId(), alertType.getName());
        alertContext.addCondition(alertCondition);
        alertContext.insert();

        DeviceStatus deviceStatus = genDeviceStatus("2");
        sleep(500);

        alertConditionTester.testDeviceStatus(deviceStatus);

        List<Alert> alertList = AlertDAO.findAlertsByDevice(device.getId());

        assertEquals(0, alertList.size());
    }


    @Test
    public void testTestDeviceStatusLessOrEqualAlert() {
        AlertCondition alertCondition = new AlertCondition(device.getId(), deviceSensor.getId(), deviceSensor.getName(), 1, AlertCondition.ComparisonOperator.LESS_OR_EQUAL, AlertCondition.Calculation.NONE, device.getId(), "1");
        alertCondition.insert();

        AlertContext alertContext = new AlertContext(device.getId(), device.getName(), AlertContext.LogicalOperator.OR, alertTypeLookup.getId(), alertType.getName());
        alertContext.addCondition(alertCondition);
        alertContext.insert();

        DeviceStatus deviceStatus = genDeviceStatus("1");
        sleep(500);

        alertConditionTester.testDeviceStatus(deviceStatus);

        List<Alert> alertList = AlertDAO.findAlertsByDevice(device.getId());

        assertEquals(1, alertList.size());
    }

    @Test
    public void testTestDeviceStatusSumEqualNoAlert() {
        AlertCondition alertCondition = new AlertCondition(device.getId(), deviceSensor.getId(), deviceSensor.getName(), 1, AlertCondition.ComparisonOperator.EQUAL, AlertCondition.Calculation.SUM, device.getId(), "1");
        alertCondition.insert();

        AlertContext alertContext = new AlertContext(device.getId(), device.getName(), AlertContext.LogicalOperator.OR, alertTypeLookup.getId(), alertType.getName());
        alertContext.addCondition(alertCondition);
        alertContext.insert();

        DeviceStatus deviceStatus = genDeviceStatus("0");
        sleep(500);

        alertConditionTester.testDeviceStatus(deviceStatus);

        List<Alert> alertList = AlertDAO.findAlertsByDevice(device.getId());

        assertEquals(0, alertList.size());
    }

    @Test
    public void testTestDeviceStatusSumEqualAlert() {
        AlertCondition alertCondition = new AlertCondition(device.getId(), deviceSensor.getId(), deviceSensor.getName(), 1, AlertCondition.ComparisonOperator.EQUAL, AlertCondition.Calculation.SUM, device.getId(), "1");
        alertCondition.insert();

        AlertContext alertContext = new AlertContext(device.getId(), device.getName(), AlertContext.LogicalOperator.OR, alertTypeLookup.getId(), alertType.getName());
        alertContext.addCondition(alertCondition);
        alertContext.insert();

        DeviceStatus deviceStatus = genDeviceStatus("1");
        sleep(500);

        alertConditionTester.testDeviceStatus(deviceStatus);

        List<Alert> alertList = AlertDAO.findAlertsByDevice(device.getId());

        assertEquals(1, alertList.size());
    }

    @Test
    public void testTestDeviceStatusAverageEqualNoAlert() {
        AlertCondition alertCondition = new AlertCondition(device.getId(), deviceSensor.getId(), deviceSensor.getName(), 1, AlertCondition.ComparisonOperator.EQUAL, AlertCondition.Calculation.AVERAGE, device.getId(), "1");
        alertCondition.insert();

        AlertContext alertContext = new AlertContext(device.getId(), device.getName(), AlertContext.LogicalOperator.OR, alertTypeLookup.getId(), alertType.getName());
        alertContext.addCondition(alertCondition);
        alertContext.insert();

        DeviceStatus deviceStatus = genDeviceStatus("0");
        sleep(500);

        alertConditionTester.testDeviceStatus(deviceStatus);

        List<Alert> alertList = AlertDAO.findAlertsByDevice(device.getId());

        assertEquals(0, alertList.size());
    }

    @Test
    public void testTestDeviceStatusAverageEqualAlert() {
        AlertCondition alertCondition = new AlertCondition(device.getId(), deviceSensor.getId(), deviceSensor.getName(), 1, AlertCondition.ComparisonOperator.EQUAL, AlertCondition.Calculation.AVERAGE, device.getId(), "1");
        alertCondition.insert();

        AlertContext alertContext = new AlertContext(device.getId(), device.getName(), AlertContext.LogicalOperator.OR, alertTypeLookup.getId(), alertType.getName());
        alertContext.addCondition(alertCondition);
        alertContext.insert();

        DeviceStatus deviceStatus = genDeviceStatus("1");
        sleep(500);

        alertConditionTester.testDeviceStatus(deviceStatus);

        List<Alert> alertList = AlertDAO.findAlertsByDevice(device.getId());

        assertEquals(1, alertList.size());
    }

    @Test
    public void testTestDeviceStatusTwoDevicesEqualsNoAlert() {
        Device deviceTwo = new Device("Test device 2", "test description 2", deviceType, "1.1.1.2", 1000, 5000, dataNode, "");
        deviceTwo.insert();

        AlertCondition alertCondition = new AlertCondition(device.getId(), deviceSensor.getId(), deviceSensor.getName(), 1, AlertCondition.ComparisonOperator.EQUAL, AlertCondition.Calculation.NONE, deviceTwo.getId(), "1");
        alertCondition.insert();

        AlertContext alertContext = new AlertContext(device.getId(), device.getName(), AlertContext.LogicalOperator.OR, alertTypeLookup.getId(), alertType.getName());
        alertContext.addCondition(alertCondition);
        alertContext.insert();

        DeviceStatus status = new DeviceStatus(deviceTwo.getId());
        status.addAttribute(deviceSensor.getName(), "0");
        status.insert();

        DeviceStatus deviceStatus = genDeviceStatus("0");
        sleep(500);

        alertConditionTester.testDeviceStatus(deviceStatus);

        List<Alert> alertList = AlertDAO.findAlertsByDevice(device.getId());

        assertEquals(0, alertList.size());
    }

    @Test
    public void testTestDeviceStatusTwoDevicesEqualsAlert() {
        Device deviceTwo = new Device("Test device 2", "test description 2", deviceType, "1.1.1.2", 1000, 5000, dataNode, "");
        deviceTwo.insert();

        AlertCondition alertCondition = new AlertCondition(device.getId(), deviceSensor.getId(), deviceSensor.getName(), 1, AlertCondition.ComparisonOperator.EQUAL, AlertCondition.Calculation.NONE, deviceTwo.getId(), "1");
        alertCondition.insert();

        AlertContext alertContext = new AlertContext(device.getId(), device.getName(), AlertContext.LogicalOperator.OR, alertTypeLookup.getId(), alertType.getName());
        alertContext.addCondition(alertCondition);
        alertContext.insert();

        DeviceStatus status = new DeviceStatus(deviceTwo.getId());
        status.addAttribute(deviceSensor.getName(), "0");
        status.insert();

        DeviceStatus deviceStatus = genDeviceStatus("1");
        sleep(500);

        alertConditionTester.testDeviceStatus(deviceStatus);

        List<Alert> alertList = AlertDAO.findAlertsByDevice(device.getId());

        assertEquals(1, alertList.size());
    }


    public static void sleep(int amt){
        try {
            TimeUnit.MILLISECONDS.sleep(amt);
        } catch(Exception e) { }
    }

    private DeviceStatus genDeviceStatus(String value) {
        DeviceStatus status = new DeviceStatus(device.getId());
        status.addAttribute(deviceSensor.getName(), value);
        status.insert();
        return status;
    }

    public void insertData() {
        alertConditionTester = new AlertConditionTester();

        deviceType = new DeviceType("Test type");
        deviceType.insert();

        deviceSensor = new DeviceSensor("TestSensor", deviceType.getId());
        deviceSensor.insert();

        dataNode = new DataNode("Test data node", "1.1.1.1");
        dataNode.insert();

        device = new Device("Test device", "test description", deviceType, "1.1.1.2", 1000, 5000, dataNode, "");
        device.insert();

        alertType = new AlertType("Test Alert Type", "test alert description", "device controller test");
        alertType.insert();

        List<Integer> alertTypeList = new ArrayList<Integer>();
        alertTypeList.add(alertType.getId());

        alertTypeLookup = new AlertTypeLookup(alertType.getId(), deviceType.getId());
        alertTypeLookup.insert();

        StateTransition stateTransition = new StateTransition(1, 2);
        stateTransition.insert();

        PolicyCondition policyCondition = new PolicyCondition(5, alertTypeList);
        policyCondition.insert();

        PolicyRule policyRule = new PolicyRule(stateTransition.getId(), policyCondition.getId(), deviceType.getId());
        policyRule.insert();
    }
}
