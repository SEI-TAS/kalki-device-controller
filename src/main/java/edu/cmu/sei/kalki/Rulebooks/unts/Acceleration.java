package edu.cmu.sei.kalki.rulebooks.unts;

import com.deliveredtechnologies.rulebook.annotation.*;

import java.util.Map;


@Rule()
public class Acceleration extends ThreeAxisRule {

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

		double accelX = Double.valueOf(status.getAttributes().get("accelerometerX"));
		double accelY = Double.valueOf(status.getAttributes().get("accelerometerY"));
		double accelZ = Double.valueOf(status.getAttributes().get("accelerometerZ"));

		double accelXBound = Double.valueOf(alertCondition.getVariables().get("accelerometerX"));
		double accelYBound = Double.valueOf(alertCondition.getVariables().get("accelerometerY"));
		double accelZBound = Double.valueOf(alertCondition.getVariables().get("accelerometerZ"));
		double accelModLimit = Double.valueOf(alertCondition.getVariables().get("modulus"));
		if (	alertingAxis(accelX, (accelXBound*-1), accelXBound) ||
				alertingAxis(accelY, (accelYBound*-1), accelYBound) ||
				alertingAxis(accelZ, (accelZBound*-1), accelZBound) ||
				alertingModulus(accelX, accelY, accelZ, accelModLimit)) {
			return true;
		}

		return false;
	}
}
