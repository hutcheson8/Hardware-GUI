package hardware;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.logging.Logger;

/**
 * This abstract class represents the information basal to all items in the
 * hardware store.
 * 
 * @author Peter Wesley Hutcheson
 * @version 1.0
 * @since 1.0
 */
public abstract class Item implements Serializable, Comparable<Item> {
	private static final Logger LOGGER = Logger.getGlobal();
	private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("0.00");
	private static final long serialVersionUID = -8698502419721443470L;
	private static final ClassQueryDialog WHICH_CLASS = new ClassQueryDialog("Appliance", "Hardware Item");
	/**
	 * This FormLine array initializes with each new Item object and contains
	 * the FormLine objects required to create each new Item form.
	 */
	protected final FormLine[] basicLines = { new FormLine("ID", "Please enter 5 alphanumeric characters.") {
		private static final long serialVersionUID = -6274479331492230210L;

		@Override
		public boolean verify() {
			return getInput().matches("\\A[a-zA-Z0-9]{5}");
		}
	}, new FormLine("Name", "Please enter a name with at least 1 character.") {
		private static final long serialVersionUID = 7216330426632971840L;

		@Override
		public boolean verify() {
			return getInput().length() > 0;
		}
	}, new FormLine("Quantity", "Please enter a positive integer quantity.") {
		private static final long serialVersionUID = -4916840030745645284L;

		@Override
		public boolean verify() {
			int q;
			try {
				q = Integer.parseInt(getInput());
			} catch (Exception e) {
				LOGGER.warning("User has not entered an int when int was expected.");
				return false;
			}
			return q > 0;
		}
	}, new FormLine("Price", "Please enter a price with at most 2 decimal digit accuracy.") {
		private static final long serialVersionUID = -4611442074458503251L;

		@Override
		public boolean verify() {
			float q;
			try {
				q = Float.parseFloat(getInput());
			} catch (Exception e) {
				LOGGER.warning("User has not entered a float when float was expected.");
				return false;
			}
			return q > 0 && (PRICE_FORMAT.format(q).equals(q + "") || PRICE_FORMAT.format(q).equals(q + "0"));
		}
	} };
	/**
	 * The ID of this Item, 5 alphanumeric characters.
	 */
	protected String ID;
	/**
	 * The name of this Item.
	 */
	protected String name;
	/**
	 * The price of this Item.
	 */
	protected float price;
	/**
	 * The quantity of items represented by this Item object.
	 */
	protected int quantity;

	/**
	 * This overwritten method facilitates the sorting of items according to
	 * their Item ID.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Item i) {
		return ID.compareTo(i.ID);
	}

	/**
	 * Used for searching items by name.
	 * 
	 * @return The name of this Item.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * This method calculates a String Array representation of this object, to
	 * be used in the creation of a table row.
	 * 
	 * @return the String Array representation of this object.
	 */
	public String[] toStringArray() {
		return new String[] { ID, name, quantity + "", getFormattedPrice(), null };
	}

	private String getFormattedPrice() {
		return '$' + PRICE_FORMAT.format(price);
	}

	/**
	 * This method constructs a new Item of a class which is chosen by user
	 * input from a ClassQueryDialog.
	 * 
	 * @return the resulting Item object.
	 * @throws CancelException
	 *             when the user presses "Cancel."
	 */
	public static Item newItem() throws CancelException {
		if (WHICH_CLASS.isClass1())
			return new Appliance();
		return new HardwareItem();
	}
}