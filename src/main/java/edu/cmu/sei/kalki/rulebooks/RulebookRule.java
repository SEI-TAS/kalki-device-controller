package edu.cmu.sei.kalki.rulebooks;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.*;

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
	protected AlertCondition alertCondition;
	protected String alertInfo;

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
		logger.info("[RulebookRule] Alert triggered: "+alertCondition.getAlertTypeName()+" for status: "+status.getId());
		AlertTypeLookup atl = Postgres.findAlertTypeLookup(alertCondition.getAlertTypeLookupId());
		Alert alert = new Alert(alertCondition.getAlertTypeName(), status.getId(), atl.getAlertTypeId(), alertInfo);
		alert.insert();
	}

	@When
	public boolean when(){
		return conditionIsTrue();
	}

	protected void setAlertCondition(String name){
		for(AlertCondition c: alertConditions){
			if(c.getAlertTypeName().equals(name)){
				alertCondition = c;
				alertInfo = "";
				break;
			}
		}
	}
}//end GenericRule
