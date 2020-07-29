package edu.cmu.sei.kalki.dc.rulebooks;

import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceStatus;

public class RulebookInput {
    private Device device;
    private DeviceStatus status;

    public RulebookInput() {}

    public RulebookInput(Device device, DeviceStatus deviceStatus) {
        this.device = device;
        this.status = deviceStatus;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public DeviceStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceStatus status) {
        this.status = status;
    }
}
