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
package edu.cmu.sei.kalki.dc.rulebooks.PhilipsHueLightEmulator;

import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.db.daos.DeviceDAO;
import edu.cmu.sei.kalki.db.daos.DeviceStatusDAO;
import edu.cmu.sei.kalki.dc.rulebooks.RulebookRule;
import edu.cmu.sei.kalki.db.database.Postgres;
import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceStatus;

import java.util.List;

@Rule()
public class TimeOn extends RulebookRule
{

    public TimeOn(){ }

    /*
      Condition: The light is on and the DLink in the group hasn't detected motion within "time-last-change"
     */
    public boolean conditionIsTrue(){
        setAlertCondition("phle-time-on");
        int lastOffCondition = Integer.parseInt(alertCondition.getVariables().get("time-last-change"));
        alertInfo = "Light is on and motion hasn't been detected within " + lastOffCondition +" minutes.";

        // this status is ON && time last change > condition
        if(Boolean.parseBoolean(status.getAttributes().get("isOn"))) {
            List<DeviceStatus> phleStatuses = DeviceStatusDAO.findDeviceStatusesOverTime(device.getId(), status.getTimestamp(), lastOffCondition, "minute");

            logger.info("[TimeOn] Statuses returned: "+phleStatuses.size());
            long latestTimestamp = phleStatuses.get(phleStatuses.size()-1).getTimestamp().getTime();
            long earliestTimestamp = phleStatuses.get(0).getTimestamp().getTime();
            if(lessThanThreshold(latestTimestamp, earliestTimestamp, lastOffCondition)) { //not enough statuses to trigger alert
                logger.info("[TimeOn] Difference in statuses is < threshold: "+((latestTimestamp-earliestTimestamp)));
                return false;
            }
            for(DeviceStatus s: phleStatuses) {
                if(!Boolean.parseBoolean(s.getAttributes().get("isOn"))) { //light was off in specified period
                    logger.info("[TimeOn] Light was off in specified period");
                    return false;
                }
            }

            List<Device> devicesInGroup = DeviceDAO.findDevicesByGroup(device.getGroup().getId());
            List<DeviceStatus> dlinkStatuses = null;

            // find the dlink and get statuses for last T minutes
            for(Device d: devicesInGroup){
                if(d.getType().getName().equals("DLink Camera")){
                    dlinkStatuses = DeviceStatusDAO.findDeviceStatusesOverTime(d.getId(), status.getTimestamp(), lastOffCondition, "minute");
                    break;
                }
            }

            // no statuses, so no motion detected
            if(dlinkStatuses.size() == 0) {
                return true;
            }

            // if it hasn't detected motion
            for (DeviceStatus ds: dlinkStatuses){
                if (!Boolean.parseBoolean(ds.getAttributes().get("motion_detected")))
                    return true;
            }
        }
        return false;
    }

    private boolean lessThanThreshold(long timestampLatest, long timestampEarliest, int threshold) {
        double diff = (timestampLatest - timestampEarliest) / 60000;

        if(diff < threshold) {
            return true;
        } else {
            return false;
        }
    }
}
