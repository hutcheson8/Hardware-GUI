package hardware;

import java.util.logging.Logger;

/**
 * This exception is thrown whenever a user presses cancel, it ends the
 * Main.userInputThread thread, opening that slot for more user input.
 * 
 * @author Peter Wesley Hutcheson
 * @version 1.0
 * @since 1.0
 */
public class CancelException extends Exception {
	private static final Logger LOGGER = Logger.getGlobal();
	private static final long serialVersionUID = 3964208534928270426L;

	/**
	 * This exception logs itself and then is thrown when the user selects
	 * "Cancel" while operating on the Main.userInputThread.
	 * 
	 */
	public CancelException() {
		LOGGER.warning("User has cancelled operation, terminating thread.");
	}
}