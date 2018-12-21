package hardware;

/**
 * This class represents small hardware objects for sale at a hardware store.
 * 
 * @author Peter Wesley Hutcheson
 * @version 1.0
 * @since 1.0
 */
public class HardwareItem extends Item {
	private static final String[] CATEGORIES = { "Door&Window", "Cabinet&Furniture", "Fasteners", "Structural",
			"Other" };
	private static final long serialVersionUID = -2581325354044554553L;
	private byte category;
	private final Form form = new Form(new FormLine[] { basicLines[0], basicLines[1], basicLines[2], basicLines[3],
			new FormLine("Category", "Please enter a category from " + CATEGORIES[0] + ", " + CATEGORIES[1] + ", "
					+ CATEGORIES[2] + ", or " + CATEGORIES[3]) {
				private static final long serialVersionUID = -8803145486470982669L;

				@Override
				protected boolean verify() {
					return whatCategory(getInput()) != -1;
				}
			} });

	/**
	 * This constructor shows a form and uses the resulting user input to
	 * initialize its fields.
	 * 
	 * @throws CancelException
	 *             when the user presses "Cancel" on the form.
	 */
	public HardwareItem() throws CancelException {
		String[] result = form.result();
		ID = result[0].toUpperCase();
		name = result[1];
		quantity = Integer.parseInt(result[2]);
		price = Float.parseFloat(result[3]);
		category = whatCategory(result[4]);
	}

	/**
	 * This method creates a String Array representation of this object, to be
	 * used in the creation of a table row.
	 * 
	 * @see hardware.Item#toStringArray()
	 * @return the String Array representation of this object.
	 */
	@Override
	public String[] toStringArray() {
		String[] value = super.toStringArray();
		value[4] = "Category: " + CATEGORIES[category];
		return value;
	}

	/**
	 * This method finds which hardware category a String corresponds to.
	 * 
	 * @param categoryString
	 *            the string to be tested, (typically "Door&Window",
	 *            "Cabinet&Furniture", "Fasteners", "Structural", or "Other")
	 * @return a byte representing the category represented by the string
	 *         parameter, or -1 if the string does not match any category
	 */
	public static byte whatCategory(String categoryString) {
		for (int x = 0; x < CATEGORIES.length; x++) {
			if (CATEGORIES[x].equals(categoryString))
				return (byte) x;
		}
		return -1;
	}
}