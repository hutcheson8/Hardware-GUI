package hardware;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

/**
 * This class represents a record of a past transaction between a customer and
 * employee of an item.
 * 
 * @author Peter Wesley Hutcheson
 * @version 1.0
 * @since 1.0
 */
public class Transaction implements Serializable {
	private static final Logger LOGGER = Logger.getGlobal();
	private static final long serialVersionUID = -7056694461443042602L;
	private Date date;
	private final FormLine itemIDFormLine = new FormLine("Item ID",
			"Please enter a valid item ID. (Refer to table for valid options)") {
		private static final long serialVersionUID = -6786024040782492162L;

		@Override
		protected boolean verify() {
			for (Item i : items) {
				if (i.ID.equals(getInput().toUpperCase()))
					return true;
			}
			return false;
		}
	};
	private final Form form = new Form(new FormLine[] { itemIDFormLine, new FormLine("Quantity",
			"Please enter a positive integer quantity less than or equal to the remaining stock.") {
		private static final long serialVersionUID = -2206426118718946096L;

		@Override
		protected boolean verify() {
			try {
				Item matchedItem = null;
				for (Item i : items) {
					if (i.ID.equals(itemIDFormLine.getInput().toUpperCase()))
						matchedItem = i;
				}
				int quantity = Integer.parseInt(getInput());
				return quantity > 0 && matchedItem.quantity >= quantity;
			} catch (Exception e) {
				LOGGER.warning("User has not entered an int when int was expected.");
				return false;
			}
		}
	}, new FormLine("Customer ID", "Please enter a valid customer ID. (Refer to table for valid options)") {
		private static final long serialVersionUID = 4100519412911479339L;

		@Override
		protected boolean verify() {
			int input;
			try {
				input = Integer.parseInt(getInput());
			} catch (Exception e) {
				LOGGER.warning("User has not entered an int when int was expected.");
				return false;
			}
			for (User u : users) {
				if (u.ID == input && u instanceof Customer)
					return true;
			}
			return false;
		}
	}, new FormLine("Employee ID", "Please enter a valid employee ID. (Refer to table for valid options)") {
		private static final long serialVersionUID = 6012418387238609499L;

		@Override
		protected boolean verify() {
			int input;
			try {
				input = Integer.parseInt(getInput());
			} catch (Exception e) {
				LOGGER.warning("User has not entered an int when int was expected.");
				return false;
			}
			for (User u : users) {
				if (u.ID == input && u instanceof Employee)
					return true;
			}
			return false;
		}
	} });
	private String itemID;
	private final ArrayList<Item> items;
	private int quantity, customerID, employeeID;
	private final ArrayList<User> users;

	/**
	 * This constructor creates a new transaction.
	 * 
	 * @param items
	 *            The ArrayList of items, same as held by Main.
	 * @param users
	 *            The ArrayList of users, same as held by Main.
	 * @throws CancelException
	 *             when the user selects "Cancel."
	 */
	public Transaction(ArrayList<Item> items, ArrayList<User> users) throws CancelException {
		this.items = items;
		this.users = users;
		date = new Date();
		String[] result = form.result();
		itemID = result[0].toUpperCase();
		quantity = Integer.parseInt(result[1]);
		Item matchedItem = null;
		for (Item i : items) {
			if (i.ID.equals(itemID))
				matchedItem = i;
		}
		matchedItem.quantity -= quantity;
		customerID = Integer.parseInt(result[2]);
		employeeID = Integer.parseInt(result[3]);
	}

	/**
	 * This method calculates a String Array to be used in the creation of a
	 * table row.
	 * 
	 * @return a representation of this object as an array of Strings.
	 */
	public Object[] toStringArray() {
		return new String[] { itemID, date.toString(), quantity + "", customerID + "", employeeID + "" };
	}
}