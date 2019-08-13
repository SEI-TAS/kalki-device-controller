package edu.cmu.sei.kalki.rulebooks;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.AlertCondition;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Alert;

import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
/**
 * @author camazzotta
 * @version 1.0
 * @created 11-Feb-2019 9:42:10 AM
 */
@Rule()
public abstract class RulebookRule {
	protected Logger logger = Logger.getLogger("device-controller");
	protected String alertName;

	@Given("device")
	protected Device device;

	@Given("status")
	protected DeviceStatus status;

	@Given("alert-conditions")
	protected List<AlertCondition> alertConditions;

	@Result
	protected HashMap<String, String> result;
	
	public RulebookRule(){}

	/**
	 * Rule-specific logic
	 */
	public abstract boolean conditionIsTrue();

	@Then
	public void then(){
		logger.info("[RulebookRule] Alert triggered: "+alertName+" for status: "+status.getId());
		Alert alert = new Alert(alertName, status.getId(), 1);
		alert.insert();
	}

	@When
	public boolean when(){
		return conditionIsTrue();
	}

	protected void setAlertName(String name){
		alertName = name;
	}
}//end GenericRule
