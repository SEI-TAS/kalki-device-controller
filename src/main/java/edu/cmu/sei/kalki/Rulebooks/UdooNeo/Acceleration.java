package edu.cmu.sei.kalki.rulebooks.UdooNeo;

import com.deliveredtechnologies.rulebook.annotation.*;
import edu.cmu.sei.kalki.rulebooks.RulebookRule;


@Rule()
public class Acceleration extends RulebookRule {

	public Acceleration(){ }

	/**
	 * UNTS DeviceStatus.attributes
	 * {
	 *     accelerometerX: "",
	 *     accelerometerY: "",
	 *     accelerometerZ: "",
	 *     gyroscopeX: "",
	 *     gyroscopeY: "",
	 *     gyroscopeZ: "",
	 *     magnetometerX: "",
	 *     magnetometerY: "",
	 *     magnetometerZ: "",
	 *     tempmax: "",
	 *     tempmax_hyst: "",
	 *     tempinput: ""
	 * }
	 *
	 *
	 * @return
	 */

	public boolean conditionIsTrue(){
		setAlertCondition("unts-acceleration");

		String stateCondition = alertCondition.getVariables().get("state");
		if(!device.getCurrentState().getName().equals(stateCondition))
			return false;

		return ThreeAxisUtil.checkRawValues(status, alertCondition, "accelerometer");
	}
}
