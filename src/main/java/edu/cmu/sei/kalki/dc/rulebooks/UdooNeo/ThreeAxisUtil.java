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
package edu.cmu.sei.kalki.dc.rulebooks.UdooNeo;

import edu.cmu.sei.kalki.db.daos.DeviceStatusDAO;
import edu.cmu.sei.kalki.db.models.AlertCondition;
import edu.cmu.sei.kalki.db.models.DeviceStatus;

import java.util.HashMap;
import java.util.List;

public class ThreeAxisUtil {

    public ThreeAxisUtil () { }

    public static ThreeAxisResult checkRawValues(DeviceStatus status, AlertCondition alertCondition, String sensor) {
        HashMap<String, Double> values = new HashMap<>();
        values.put("x", Double.valueOf(status.getAttributes().get(sensor+"X")));
        values.put("y", Double.valueOf(status.getAttributes().get(sensor+"Y")));
        values.put("z", Double.valueOf(status.getAttributes().get(sensor+"Z")));
        values.put("mod", Math.sqrt(Math.pow(values.get("x"), 2) + Math.pow(values.get("y"), 2) + Math.pow(values.get("z"), 2)));

        return compareToConditions(values, alertCondition, sensor);
    }

    public static ThreeAxisResult checkAgainstAverages(AlertCondition alertCondition, int deviceId, String sensor) {
        int numStatuses = Integer.valueOf(alertCondition.getVariables().get("average"));
        List<DeviceStatus> lastNStatuses = DeviceStatusDAO.findNDeviceStatuses(deviceId, numStatuses);

        HashMap<String, Double> averages = calculateAverages(lastNStatuses, sensor);

        return compareToConditions(averages, alertCondition, sensor);
    }

    private static ThreeAxisResult compareToConditions(HashMap<String, Double> values, AlertCondition alertCondition, String sensor) {
        double xBound = Double.valueOf(alertCondition.getVariables().get(sensor+"X"));
        double yBound = Double.valueOf(alertCondition.getVariables().get(sensor+"Y"));
        double zBound = Double.valueOf(alertCondition.getVariables().get(sensor+"Z"));
        double modLimit = Double.valueOf(alertCondition.getVariables().get("modulus"));

        ThreeAxisResult result = new ThreeAxisResult(false, "");
        if (alertingAxis(values.get("x"), (xBound*-1), xBound)){
            result = new ThreeAxisResult(true, "Triggered by x-axis value.");
        }
        else if(alertingAxis(values.get("y"), (yBound*-1), yBound)) {
            result = new ThreeAxisResult(true, "Triggered by y-axis value.");
        }
        else if(alertingAxis(values.get("z"), (zBound*-1), zBound)) {
            result = new ThreeAxisResult(true, "Triggered by z-axis value.");
        }
        else if(alertingModulus(values.get("mod"), modLimit)) {
            result = new ThreeAxisResult(true, "Triggered by calculated modulus.");
        }

        return result;
    }

    private static boolean alertingAxis(double axis, double lowerBound, double upperBound) {
        if( axis < lowerBound || axis > upperBound){
            return true;
        }
        return false;
    }

    private static boolean alertingModulus(double modulus, double limit) {
        if(modulus < (-1 * limit) || modulus > limit) {
            return true;
        }
        return false;
    }

    private static HashMap<String, Double> calculateAverages(List<DeviceStatus> statusList, String sensor) {
        double xSum = 0;
        double ySum = 0;
        double zSum = 0;
        double modSum = 0;
        int numStatus = statusList.size();
        HashMap<String, Double> result = new HashMap<>();

        for(DeviceStatus s: statusList){
            double x = Math.abs(Double.valueOf(s.getAttributes().get(sensor+"X")));
            double y = Math.abs(Double.valueOf(s.getAttributes().get(sensor+"Y")));
            double z = Math.abs(Double.valueOf(s.getAttributes().get(sensor+"Z")));
            xSum+=x;
            ySum+=y;
            zSum+=z;
            modSum += Math.sqrt(x*x + y*y + z*z);
        }

        result.put("x", xSum/numStatus);
        result.put("y", ySum/numStatus);
        result.put("z", zSum/numStatus);
        result.put("mod", modSum/numStatus);

        return result;
    }
}
