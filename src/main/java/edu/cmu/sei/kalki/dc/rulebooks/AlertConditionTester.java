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
package edu.cmu.sei.kalki.dc.rulebooks;

import com.sun.org.apache.xpath.internal.operations.Bool;
import edu.cmu.sei.kalki.db.daos.*;
import edu.cmu.sei.kalki.db.models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

// Rulebook imports, for later reference
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;
import com.deliveredtechnologies.rulebook.lang.RuleBuilder;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.Auditor;
import com.deliveredtechnologies.rulebook.model.RuleStatus;

public class AlertConditionTester {
    private static Logger logger = Logger.getLogger("device-controller");
    private HashMap<Integer, List<RuleBook>> rulebooks;

    public AlertConditionTester(){
        this.rulebooks = new HashMap<>();
    }

    public void testDeviceStatus(DeviceStatus status){
        Device device = DeviceDAO.findDevice(status.getDeviceId());
        List<AlertContext> alertContexts = AlertContextDAO.findAlertContextsByDevice(status.getDeviceId());

        List<RuleBook> ruleBookRunnerList = rulebooks.get(device.getId());
        NameValueReferableMap factMap = prepareFactMap(device, status);

        if (ruleBookRunnerList == null){ // Rulebooks don't exist so create
            List<RuleBook> newRulebookRunnerList = new ArrayList<>();

            for(AlertContext alertContext: alertContexts){
                RuleBook newRulebook = createRulebook(alertContext);
                newRulebookRunnerList.add(newRulebook);
            }
            rulebooks.put(device.getId(), newRulebookRunnerList);
            ruleBookRunnerList = newRulebookRunnerList;
        }

        for(RuleBook rulebook: ruleBookRunnerList){
            rulebook.run(factMap);
            rulebook.getResult().ifPresent(result -> {
                String[] resArray = result.toString().split(",");
                int contextId = Integer.valueOf(resArray[0]);
                int resultValue = Integer.valueOf(resArray[1]);

                // Map a rulebook to related alert context
                for (AlertContext context: alertContexts) {
                    if (context.getId() == contextId) {
                        String logicalOperator = context.getLogicalOperator();

                        if (logicalOperator.equals(AlertContext.LogicalOperator.AND)) { // check that all rules returned true
                            Auditor auditor = (Auditor)rulebook;
                            Map<String, RuleStatus> rules = auditor.getRuleStatusMap();
                            int numRules = rules.size();

                            if (resultValue == numRules) // All rules returned true
                                insertAlert(context, status.getId());
                        }
                        else {
                            if (resultValue > 0) // At least one condition returned true
                                insertAlert(context, status.getId());
                        }

                    }
                }
            });
        }

        logger.info("[AlertConditionTester] Running rulebooks for DeviceStatus: "+status.getId());

    }

    private void insertAlert(AlertContext context, int statusId) {
        AlertTypeLookup lookup = AlertTypeLookupDAO.findAlertTypeLookup(context.getAlertTypeLookupId());

        Alert alert = new Alert(context.getDeviceId(), context.getAlertTypeName(), lookup.getAlertTypeId(), "Alert generated via status: "+statusId);
        alert.insert();
    }

    private NameValueReferableMap prepareFactMap(Device device, DeviceStatus status) {
        NameValueReferableMap facts = new FactMap();
        facts.setValue("input", new RulebookInput(device, status));
        return facts;
    }

    private RuleBook createRulebook(AlertContext alertContext) {
        RuleBook<Object> ruleBook = new CoRRuleBook<>();
        for(AlertCondition alertCondition: alertContext.getConditions()) {
            ruleBook.addRule(generateRule(alertCondition));
        }
        ruleBook.setDefaultResult(alertContext.getId()+",0");
        return ruleBook;
    }

    private Rule generateRule(AlertCondition alertCondition){
        return RuleBuilder.create().withFactType(RulebookInput.class).withResultType(String.class)
                .when(facts -> {
                    RulebookInput input = facts.getOne();

                    // Get required number of statuses for the alert condition
                    int numStatuses = alertCondition.getNumStatues();
                    List<DeviceStatus> statusList = new ArrayList<>();

                    DeviceStatus status = input.getStatus();
                    int condDeviceId = alertCondition.getDeviceId();
                    if (status.getDeviceId() != condDeviceId) { // condition referrs to another device
                        statusList = DeviceStatusDAO.findNDeviceStatuses(condDeviceId, numStatuses);
                    } else {
                        statusList = DeviceStatusDAO.findNDeviceStatuses(status.getDeviceId(), numStatuses);
                    }

                    if (statusList.size() < numStatuses) // not enough statuses for this condition
                        return false;

                    // Determine how to use status(es) value
                    String attribute = alertCondition.getAttributeName();
                    String calculation = alertCondition.getCalculation();

                    String currentValue = "";
                    if (calculation.equals(AlertCondition.Calculation.AVERAGE.convert())) {
                        int sum = calcSum(statusList, attribute);
                        int avg = sum / numStatuses;
                        currentValue = String.valueOf(avg);
                    }
                    else if (calculation.equals(AlertCondition.Calculation.SUM.convert())) {
                        int sum = calcSum(statusList, attribute);
                        currentValue = String.valueOf(sum);
                    }
                    else if (calculation.equals(AlertCondition.Calculation.NONE.convert())) {
                        currentValue = statusList.get(0).getAttributes().get(attribute);
                    }
                    else {
                        return false;
                    }


                    // Do comparison
                    String compOperator = alertCondition.getCompOperator();
                    String thresholdValue = alertCondition.getThresholdValue();

                    if (compOperator.equals(AlertCondition.ComparisonOperator.EQUAL.convert())){
                        return currentValue.equals(thresholdValue);
                    } else {
                        Double currVal = Double.valueOf(currentValue);
                        Double thresVal = Double.valueOf(thresholdValue);

                        if (compOperator.equals(AlertCondition.ComparisonOperator.GREATER.convert())) {
                            return currVal > thresVal;
                        }
                        else if (compOperator.equals(AlertCondition.ComparisonOperator.GREATER_OR_EQUAL.convert())) {
                            return currVal >= thresVal;
                        }
                        else if (compOperator.equals(AlertCondition.ComparisonOperator.LESS.convert())) {
                            return currVal < thresVal;
                        }
                        else if (compOperator.equals(AlertCondition.ComparisonOperator.LESS_OR_EQUAL.convert())) {
                            return currVal <= thresVal;
                        }
                        else {
                            return false;
                        }
                    }

                })
                .then((facts, result) -> {
                    // result is of format: contextId, result
                    String[] res = result.getValue().split(",");
                    String contextId = res[0];
                    int curResult = Integer.valueOf(res[1]) + 1;
                    result.setValue(contextId+","+curResult);
                })
                .build();
    }

    private int calcSum(List<DeviceStatus> statusList, String attribute) {
        int sum = 0;
        for(DeviceStatus tempStatus: statusList) {
            sum += Integer.valueOf(tempStatus.getAttributes().get(attribute));
        }
        return sum;
    }
}
