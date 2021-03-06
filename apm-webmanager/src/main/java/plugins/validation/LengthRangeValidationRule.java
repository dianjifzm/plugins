/**
 * 
 */
package plugins.validation;

/**
 * @author fengmengyue
 *
 */
public class LengthRangeValidationRule implements ValidationRule {
	
	private int min;
	private int max;
	
	public LengthRangeValidationRule(int minLength,int maxLength){
		if(minLength >= maxLength){
			throw new IllegalArgumentException("min is eq or gt than max");
		}
		this.min = minLength;
		this.max = maxLength;
	}

	@Override
	public String type() {
		return "lengRange";
	}

	@Override
	public boolean validate(Object bean, Object value) {
		if(value != null && (value instanceof String)){
			int len = value.toString().trim().length();
			return len >= min && len <= max;
		}
		return true;
	}

	@Override
	public String getErrorMessage() {
		return "长度必须在"+min+"至"+max+"之间";
	}

}
