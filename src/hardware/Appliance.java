package hardware;

/**
 * This class is a model which represents an appliance item in a hardware store.
 * 
 * @author Peter Wesley Hutcheson
 * @version 1.0
 * @since 1.0
 */
public class Appliance extends Item {
	private static final long serialVersionUID = -6956503668441872944L;
	private static final String[] TYPES = { "Refrigerators", "Washers&Dryers", "Ranges&Ovens", "Small Appliances" };
	private String brand;
	private final Form form = new Form(new FormLine[] { basicLines[0], basicLines[1], basicLines[2], basicLines[3],
			new FormLine("Brand", "Please enter a brand with at least 1 character.") {
				private static final long serialVersionUID = -8996714202555190890L;

				@Override
				protected boolean verify() {
					return getInput().length() > 0;
				}
			}, new FormLine("Type",
					"Please enter the type from " + TYPES[0] + ", " + TYPES[1] + ", " + TYPES[2] + ", or " + TYPES[3]) {
				private static final long serialVersionUID = 5499221046297404914L;

				@Override
				protected boolean verify() {
					return whatType(getInput()) != -1;
				}
			} });
	private byte type;

	/**
	 * This constructor makes a form dialog appear and uses the form input to
	 * initialize the Appliance class variables.
	 * 
	 * @throws CancelException
	 *             This exception is thrown when the user presses cancel on the
	 *             form dialog.
	 */
	public Appliance() throws CancelException {
		String[] result = form.result();
		ID = result[0].toUpperCase();
		name = result[1];
		quantity = Integer.parseInt(result[2]);
		price = Float.parseFloat(result[3]);
		brand = result[4];
		type = whatType(result[5]);
	}

	/**
	 * This overwritten method simply adds the Appliance class specific fields
	 * to the Item.toStringArray() method.
	 * 
	 * @return a string array that represents this object, to be used to create
	 *         a table row.
	 * @see hardware.Item#toStringArray()
	 */
	@Override
	public String[] toStringArray() {
		String[] item = super.toStringArray();
		item[4] = "Brand: " + brand + " Type: " + TYPES[type];
		return item;
	}

	/**
	 * This method finds which Appliance type a String corresponds to.
	 * 
	 * @param typeString
	 *            the string to be tested, (typically "Refrigerators",
	 *            "Washers&Dryers", "Ranges&Ovens", or "Small Appliances")
	 * @return a byte representing the type represented by the string parameter,
	 *         or -1 if the string does not match any type
	 */
	public static byte whatType(String typeString) {
		for (int x = 0; x < TYPES.length; x++) {
			if (TYPES[x].equals(typeString))
				return (byte) x;
		}
		return -1;
	}
}