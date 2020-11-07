/**
 * Code by by Jake Hum, Edward Marozzi, Fei Yuan - Group 4
 */
package game;
/**
 * An exception thrown when a player breaks a rule
 */
@SuppressWarnings("serial")
public class BrokeRuleException extends Exception {
	public BrokeRuleException(String violation) {
		super(violation);
	}
}
