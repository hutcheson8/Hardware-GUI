package hardware;

import java.io.Serializable;

/**
 * This abstract class represents the information basal to both User subclasses.
 * 
 * @author Peter Wesley Hutcheson
 * @version 1.0
 * @since 1.0
 */
public abstract class User implements Serializable, Comparable<User> {
	private static final long serialVersionUID = -6645417989631435674L;
	private static final ClassQueryDialog WHICH_CLASS = new ClassQueryDialog("Employee", "Customer");
	/**
	 * These FormLine objects are initialized and used in forms for both
	 * subclasses.
	 */
	protected final FormLine[] basicLines = {
			new FormLine("First Name", "Please enter a name with at least 1 character.") {
				private static final long serialVersionUID = -3334071895206549038L;

				@Override
				protected boolean verify() {
					return getInput().length() > 0;
				}
			}, new FormLine("Last Name", "Please enter a name with at least 1 character.") {
				private static final long serialVersionUID = 1841496380608411639L;

				@Override
				protected boolean verify() {
					return getInput().length() > 0;
				}
			} };
	/**
	 * This string represents the user's first name.
	 */
	protected String firstName;
	/**
	 * This string represents the user's last name.
	 */
	protected String lastName;
	/**
	 * This int is the user's ID number.
	 */
	protected int ID;

	/**
	 * This method helps to sort users by their ID number.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(User u) {
		return ID - u.ID;
	}

	/**
	 * This method calculates a String array to be used in the creation of a
	 * table row.
	 * 
	 * @return a representation of this object as a String array.
	 */
	public String[] toStringArray() {
		return new String[] { ID + "", firstName, lastName, null, null };
	}

	/**
	 * This method updates values stored here when the user chooses to update a
	 * user.
	 * 
	 * @param newValues
	 *            A String array containing new values for updating.
	 */
	protected void update(String[] newValues) {
		firstName = newValues[0];
		lastName = newValues[1];
	}

	/**
	 * This method constructs a new User object with a type determined by the
	 * user input from a ClassQueryDialog object.
	 * 
	 * @param id
	 *            The User ID for the new User object.
	 * @return The generated User object.
	 * @throws CancelException
	 *             When the user presses "Cancel."
	 */
	public static User newUser(int id) throws CancelException {
		if (WHICH_CLASS.isClass1())
			return new Employee(id);
		return new Customer(id);
	}
}