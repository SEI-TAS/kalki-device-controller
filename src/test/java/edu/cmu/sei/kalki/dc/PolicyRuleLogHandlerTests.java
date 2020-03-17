package edu.cmu.sei.kalki.dc;

import edu.cmu.sei.kalki.db.models.*;
import edu.cmu.sei.kalki.dc.database.PolicyRuleLogHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

public class PolicyRuleLogHandlerTests extends BaseTest {
    private DeviceType type;
    private Device testDevice;
    private PolicyRule policyRule;
    private PolicyRuleLog policyRuleLog;

    @Mock
    private IoTInterfaceAPI ioTInterfaceAPI = new IoTInterfaceAPI();

    @InjectMocks
    PolicyRuleLogHandler policyRuleLogHandler = new PolicyRuleLogHandler();

    @BeforeEach
    public void setup() {
        super.setup();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void handleNewInsertionNoGroupOneCommand() {
        DeviceCommand command = new DeviceCommand("One command", type.getId());
        command.insert();

        DeviceCommandLookup lookup = new DeviceCommandLookup(command.getId(), policyRule.getId());
        lookup.insert();

        policyRuleLog = new PolicyRuleLog(policyRule.getId(), testDevice.getId());
        policyRuleLog.insert();

        policyRuleLogHandler.handleNewInsertion(policyRuleLog.getId());

        Mockito.verify(ioTInterfaceAPI).sendCommands(
                Mockito.argThat((Device d) -> d.getId() == testDevice.getId()),
                (Mockito.argThat((List<DeviceCommand> commands) -> commands.size() == 1)));
    }

    @Test
    public void handleNewInsertionNoGroupNoCommand() {

        policyRuleLog = new PolicyRuleLog(policyRule.getId(), testDevice.getId());
        policyRuleLog.insert();

        policyRuleLogHandler.handleNewInsertion(policyRuleLog.getId());

        Mockito.verify(ioTInterfaceAPI, Mockito.never()).sendCommands(Mockito.any(Device.class), Mockito.any(List.class));
    }

    @Test
    public void handleNewInsertionGroupOneCommand() {
        Group g = new Group("Test group");
        g.insert();
        testDevice.setGroupId(g);
        testDevice.insertOrUpdate();

        Device devicetwo = new Device("device two", "second test", type.getId(), g.getId(), "ip", 1,1,1);
        devicetwo.insertOrUpdate();

        DeviceCommand command = new DeviceCommand("One command", type.getId());
        command.insert();

        DeviceCommandLookup lookup = new DeviceCommandLookup(command.getId(), policyRule.getId());
        lookup.insert();

        policyRuleLog = new PolicyRuleLog(policyRule.getId(), testDevice.getId());
        policyRuleLog.insert();

        policyRuleLogHandler.handleNewInsertion(policyRuleLog.getId());

        Mockito.verify(ioTInterfaceAPI, Mockito.times(2)).sendCommands(Mockito.any(Device.class), Mockito.any(List.class));

    }

    @Test
    public void handleNewInsertionGroupNoCommand() {
        Device devicetwo = new Device("device two", "second test", type, "ip", 1,1);
        devicetwo.insertOrUpdate();

        policyRuleLog = new PolicyRuleLog(policyRule.getId(), testDevice.getId());
        policyRuleLog.insert();

        policyRuleLogHandler.handleNewInsertion(policyRuleLog.getId());

        Mockito.verify(ioTInterfaceAPI, Mockito.never()).sendCommands(Mockito.any(Device.class), Mockito.any(List.class));
    }

    public void insertData() {
        type = new DeviceType(-1, "Device Type");
        type.insert();

        testDevice = new Device("Name", "Description", type, "ip", 1,1);
        testDevice.insert();

        AlertType alertType = new AlertType("alert type", "a test type", "TEST");
        alertType.insert();

        StateTransition stateTransition = new StateTransition(1,2);
        stateTransition.insert();

        PolicyCondition policyCondition = new PolicyCondition(1, Arrays.asList(alertType.getId()));
        policyCondition.insert();

        policyRule = new PolicyRule(stateTransition.getId(), policyCondition.getId(), type.getId(), 2);
        policyRule.insert();

    }
}
