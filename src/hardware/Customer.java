package hardware;

/**
 * This class represents a record of a customer that uses a hardware store.
 * 
 * @author Peter Wesley Hutcheson
 * @version 1.0
 * @since 1.0
 */
public class Customer extends User {
	private static final long serialVersionUID = 7763308262657582054L;
	private final Form form = new Form(new FormLine[] { basicLines[0], basicLines[1],
			new FormLine("Phone Number", "Please enter a phone number in the format \"XXX-XXX-XXXX\"") {
				private static final long serialVersionUID = -5844465642452454033L;

				@Override
				protected boolean verify() {
					return getInput().matches("\\d{3}[-]{1}\\d{3}[-]{1}\\d{4}");
				}
			}, new FormLine("Address", "Please enter an address with at least 1 character.") {
				private static final long serialVersionUID = 9167400530254009190L;

				@Override
				protected boolean verify() {
					return getInput().length() > 0;
				}
			} });
	private String phone, address;

	/**
	 * This constructor makes a form appear and uses the resulting user input to
	 * initialize this object's fields.
	 * 
	 * @param id
	 *            the ID for this new user.
	 * @throws CancelException
	 *             when the user presses "Cancel."
	 */
	public Customer(int id) throws CancelException {
		ID = id;
		String[] result = form.result();
		firstName = result[0];
		lastName = result[1];
		phone = result[2];
		address = result[3];
	}

	/**
	 * This constructor makes a form appear, then it initializes the textfields
	 * of the form with initializationVector. Then, it uses the resulting user
	 * input to initialize this object's fields. This constructor is used when
	 * editing an existing User.
	 * 
	 * @param id
	 *            the ID for this new user.
	 * @param initializationVector
	 *            the String[] containing the original User object values.
	 * @throws CancelException
	 *             when the user presses "Cancel."
	 */
	public Customer(int id, String[] initializationVector) throws CancelException {
		form.initialize(initializationVector);
		ID = id;
		String[] result = form.result();
		firstName = result[0];
		lastName = result[1];
		phone = result[2];
		address = result[3];
	}

	/**
	 * This method calculates a String Array representation of this object to be
	 * used in the creation of a table row.
	 * 
	 * @see hardware.User#toStringArray()
	 * @return the String Array representation of this object.
	 */
	@Override
	public String[] toStringArray() {
		String[] value = super.toStringArray();
		value[3] = phone;
		value[4] = address;
		return value;
	}
}