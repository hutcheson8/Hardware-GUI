package hardware;

import java.util.logging.Logger;

/**
 * This class represents employees working at a hardware store.
 * 
 * @author Peter Wesley Hutcheson
 * @version 1.0
 * @since 1.0
 */
public class Employee extends User {
	private static final Logger LOGGER = Logger.getGlobal();
	private static final long serialVersionUID = -3139314890369541999L;
	private final Form form = new Form(
			new FormLine[] { basicLines[0], basicLines[1], new FormLine("SSN", "Please enter a 9 digit integer.") {
				private static final long serialVersionUID = 4905308045461775352L;

				@Override
				protected boolean verify() {
					return getInput().matches("\\d{9}");
				}
			}, new FormLine("Salary", "Please enter a number for the salary.") {
				private static final long serialVersionUID = -7256671107552664565L;

				@Override
				protected boolean verify() {
					try {
						Float.parseFloat(getInput());
					} catch (Exception e) {
						LOGGER.warning("User has not entered a float when float was expected.");
						return false;
					}
					return true;
				}
			} });
	private float salary;
	private int ssn;

	/**
	 * This constructor creates a new Employee object using id and also by
	 * showing a form and using the resulting user input to initialize its
	 * fields.
	 * 
	 * @param id
	 *            the user ID for this new Employee object.
	 * @throws CancelException
	 *             when the user presses "Cancel" in the form.
	 */
	public Employee(int id) throws CancelException {
		ID = id;
		String[] result = form.result();
		firstName = result[0];
		lastName = result[1];
		ssn = Integer.parseInt(result[2]);
		salary = Float.parseFloat(result[3]);
	}

	/**
	 * This constructor creates a new Employee object using id and also by
	 * showing a form and using the resulting user input to initialize its
	 * fields. This particular constructor also initializes the form's textfield
	 * values using initializationVector.
	 * 
	 * @param id
	 *            the user ID for this new Employee object.
	 * @param initializationVector
	 *            the vector containing the fields for the existing Employee
	 *            object to be updated.
	 * @throws CancelException
	 *             when the user presses "Cancel" in the form.
	 */
	public Employee(int id, String[] initializationVector) throws CancelException {
		form.initialize(initializationVector);
		ID = id;
		String[] result = form.result();
		firstName = result[0];
		lastName = result[1];
		ssn = Integer.parseInt(result[2]);
		salary = Float.parseFloat(result[3]);
	}

	/**
	 * This method calculates a String array representation of this object to be
	 * used in the creation of a table row.
	 * 
	 * @see hardware.User#toStringArray()
	 * @return the String array representation of this object.
	 */
	@Override
	public String[] toStringArray() {
		String[] value = super.toStringArray();
		value[3] = padSSN();
		value[4] = "" + salary;
		return value;
	}

	private String padSSN() {
		String sssn = ssn + "";
		int zerosToAdd = 9 - sssn.length();
		for (int x = 0; x < zerosToAdd; x++) {
			sssn = '0' + sssn;
		}
		return sssn;
	}
}