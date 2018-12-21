package hardware;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * This class creates the GUI, saves the files, and starts threads associated
 * with the management and memorization of users, items, and transactions in a
 * hardware store.
 * 
 * @author Peter Wesley Hutcheson
 * @version 1.0
 * @since 1.0
 */
public class Main implements Serializable {

	enum TableType {
		Items, Search, Transactions, Users
	}

	private static JScrollPane centerScrollPane;
	private static final String DATABASE_FILENAME = "database.srl";

	private static final Logger LOGGER = Logger.getGlobal();
	private static JFrame mainWindow;
	private static JTextField searchTermField;
	private static final long serialVersionUID = -8400643378888310221L;
	private static Thread userInputThread;
	private ArrayList<Item> items = new ArrayList<Item>();
	private int nextUserID = 1;
	private ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	private ArrayList<User> users = new ArrayList<User>();

	private Main() {
		run();
	}

	private void run() {
		FileHandler fileHandler = null;
		try {
			fileHandler = new FileHandler("hardware.log");
		} catch (Exception e) {
			LOGGER.warning("Problem opening log file.");
		}
		fileHandler.setFormatter(new SimpleFormatter());
		LOGGER.addHandler(fileHandler);
		Form addQuantityForm = new Form(new FormLine[] {
				new FormLine("Item ID", "Please enter the item ID of the item you would like to add quantity to.") {
					private static final long serialVersionUID = -2975724386976863186L;

					@Override
					protected boolean verify() {
						Item matchedItem = null;
						for (Item i : items) {
							if (i.ID.equals(getInput().toUpperCase())) {
								matchedItem = i;
							}
						}
						return matchedItem != null;
					}
				}, new FormLine("Quantity", "Please enter a positive integer value.") {
					private static final long serialVersionUID = -8849318554130149662L;

					@Override
					protected boolean verify() {
						try {
							return Integer.parseInt(getInput()) > 0;
						} catch (Exception e) {
							LOGGER.warning("User has not entered an int when int was expected.");
							return false;
						}
					}
				} }), deleteItemForm = new Form(new FormLine[] { new FormLine("Item ID",
						"Please enter an item ID for the item you wish to delete. (Use the table)") {

					private static final long serialVersionUID = 2507906576056436247L;

					@Override
					protected boolean verify() {
						for (Item i : items) {
							if (i.ID.equals(getInput().toUpperCase()))
								return true;
						}
						return false;
					}
				} }), selectUserForm = new Form(new FormLine[] {
						new FormLine("User ID", "Please choose an existing user ID. (Use the table.)") {
							private static final long serialVersionUID = -6298858757408858894L;

							@Override
							protected boolean verify() {
								try {
									for (User u : users) {
										if (u.ID == Integer.parseInt(getInput())) {
											return true;
										}
									}
								} catch (Exception e) {
									LOGGER.warning("User has not entered an int when int was expected.");
								}
								return false;
							}
						} });
		mainWindow = new JFrame("Hardware Store");
		mainWindow.setLayout(new BorderLayout());
		mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainWindow.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				LOGGER.info("User has closed the window. ");
				LOGGER.info("Now saving " + DATABASE_FILENAME);
				save();
				System.exit(0);
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
			}
		});
		updateTable(TableType.Items);
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout());
		JLabel dropdownLabel = new JLabel("Table Type: ");
		topPanel.add(dropdownLabel);
		JComboBox<TableType> dropdown = new JComboBox<TableType>();
		dropdown.addItem(TableType.Items);
		dropdown.addItem(TableType.Users);
		dropdown.addItem(TableType.Transactions);
		dropdown.addItem(TableType.Search);
		dropdown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				LOGGER.info("User has clicked dropdown menu, updating table.");
				updateTable((TableType) dropdown.getSelectedItem());
			}
		});
		topPanel.add(dropdown);
		JLabel searchLabel = new JLabel("Item Search Term: ");
		topPanel.add(searchLabel);
		searchTermField = new JTextField(20);
		topPanel.add(searchTermField);
		JButton searchSubmit = new JButton("Go");
		searchSubmit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				LOGGER.info("User has clicked search submit button, updating table.");
				dropdown.setSelectedIndex(3);
				updateTable((TableType) dropdown.getSelectedItem());
			}
		});
		topPanel.add(searchSubmit);
		mainWindow.add(topPanel, NORTH);
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout());
		bottomPanel.add(new JLabel("Add a New: "));
		JButton addItem = new JButton("Item"), addUser = new JButton("User"),
				addTransaction = new JButton("Transaction");
		ActionListener addButtonListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LOGGER.info("User has clicked a button to add something to the database.");
				if (userInputThread == null || !userInputThread.isAlive()) {
					userInputThread = new Thread() {
						public void run() {
							LOGGER.info("Thread started.");
							String type = ((JButton) e.getSource()).getText();
							boolean choseUser = false;
							try {
								switch (type) {
								case "Item":
									LOGGER.info("User has chosen to create a new item.");
									items.add(Item.newItem());
									dropdown.setSelectedIndex(0);
									break;
								case "User":
									LOGGER.info("User has chosen to create a new user.");
									choseUser = true;
									users.add(User.newUser(nextUserID++));
									dropdown.setSelectedIndex(1);
									break;
								case "Transaction":
									LOGGER.info("User has chosen to create a new transaction.");
									transactions.add(new Transaction(items, users));
									dropdown.setSelectedIndex(2);
								default:
								}
								updateTable((TableType) dropdown.getSelectedItem());
							} catch (CancelException exc) {
								if (choseUser)
									nextUserID--;
							}
							LOGGER.info("Thread ended.");
						}
					};
					userInputThread.start();
				} else {
					LOGGER.warning("Thread is already running!");
				}
			}
		};
		addItem.addActionListener(addButtonListener);
		addUser.addActionListener(addButtonListener);
		addTransaction.addActionListener(addButtonListener);
		bottomPanel.add(addItem);
		bottomPanel.add(addUser);
		bottomPanel.add(addTransaction);
		bottomPanel.add(new JLabel("Change Existing: "));
		JButton addQuantity = new JButton("Add Quantity to an Item"), deleteItem = new JButton("Delete an Item"),
				updateUser = new JButton("Update a User");
		ActionListener specialFormActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LOGGER.info("User has chosen to change an existing item.");
				if (userInputThread == null || !userInputThread.isAlive()) {
					userInputThread = new Thread() {
						@Override
						public void run() {
							LOGGER.info("Thread started.");
							try {
								String[] result;
								Item matchedItem = null;
								if (e.getSource() == addQuantity) {
									LOGGER.info("User has chosen to add a quantity to an item.");
									result = addQuantityForm.result();
									for (Item i : items) {
										if (i.ID.equals(result[0].toUpperCase())) {
											matchedItem = i;
										}
									}
									matchedItem.quantity += Integer.parseInt(result[1]);
									dropdown.setSelectedIndex(0);
								} else if (e.getSource() == deleteItem) {
									LOGGER.info("User has chosen to delete an item.");
									String idToDelete = deleteItemForm.result()[0];
									Item itemToDelete = null;
									for (Item i : items) {
										if (i.ID.equals(idToDelete.toUpperCase()))
											itemToDelete = i;
									}
									items.remove(itemToDelete);
									dropdown.setSelectedIndex(0);
								} else if (e.getSource() == updateUser) {
									LOGGER.info("User has chosen to update a user.");
									User selectedUser = null;
									String theID = selectUserForm.result()[0];
									for (User cur : users) {
										if (cur.ID == Integer.parseInt(theID))
											selectedUser = cur;
									}
									String[] selectedUserStringArray = selectedUser.toStringArray();
									String[] initializationVector = new String[selectedUserStringArray.length - 1];
									for (int x = 1; x < selectedUserStringArray.length; x++) {
										initializationVector[x - 1] = (selectedUserStringArray[x]);
									}
									users.remove(selectedUser);
									if (selectedUser instanceof Employee) {
										users.add(new Employee(selectedUser.ID, initializationVector));
									} else {
										users.add(new Customer(selectedUser.ID, initializationVector));
									}
									dropdown.setSelectedIndex(1);
									users.sort(null);
								}
								updateTable((TableType) dropdown.getSelectedItem());
							} catch (CancelException e) {
							}
							LOGGER.info("Thread ended.");
						}
					};
					userInputThread.start();
				} else {
					LOGGER.warning("Thread is already running!");
				}
			}
		};
		addQuantity.addActionListener(specialFormActionListener);
		deleteItem.addActionListener(specialFormActionListener);
		updateUser.addActionListener(specialFormActionListener);
		bottomPanel.add(addQuantity);
		bottomPanel.add(deleteItem);
		bottomPanel.add(updateUser);
		mainWindow.add(bottomPanel, SOUTH);
		mainWindow.pack();
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setVisible(true);
	}

	private void save() {
		try {
			ObjectOutputStream databaseOutStream = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(DATABASE_FILENAME)));
			databaseOutStream.writeObject(this);
			databaseOutStream.close();
		} catch (Exception e) {
			LOGGER.warning("Problem opening database file for saving.");
		}
	}

	private void updateTable(TableType type) {
		if (centerScrollPane != null)
			mainWindow.remove(centerScrollPane);
		Object[][] tableData;
		JTable nextTable = null;
		switch (type) {
		case Items:
			items.sort(null);
			tableData = new Object[items.size()][5];
			for (int x = 0; x < items.size(); x++) {
				tableData[x] = items.get(x).toStringArray();
			}
			nextTable = new JTable(tableData, new String[] { "ID", "Name", "Quantity", "Price", "Other Info" });
			break;
		case Users:
			tableData = new Object[users.size()][5];
			for (int x = 0; x < users.size(); x++) {
				User u = users.get(x);
				if (u instanceof Employee) {
					String[] s = u.toStringArray();
					s[4] = "$" + s[4];
					tableData[x] = s;
				} else {
					tableData[x] = u.toStringArray();
				}
			}
			nextTable = new JTable(tableData,
					new String[] { "ID", "First Name", "Last Name", "Phone #/SSN", "Address/Salary" });
			break;
		case Transactions:
			tableData = new Object[transactions.size()][5];
			for (int x = 0; x < transactions.size(); x++) {
				tableData[x] = transactions.get(x).toStringArray();
			}
			nextTable = new JTable(tableData,
					new String[] { "Item ID", "Sale Date", "Quantity", "Customer ID", "Employee ID" });
			break;
		case Search:
			ArrayList<Item> searchResults = new ArrayList<Item>();
			for (Item i : items) {
				if (i.toString().toUpperCase().contains(searchTermField.getText().toUpperCase())) {
					searchResults.add(i);
				}
			}
			tableData = new Object[searchResults.size()][5];
			for (int x = 0; x < searchResults.size(); x++) {
				tableData[x] = searchResults.get(x).toStringArray();
			}
			nextTable = new JTable(tableData, new String[] { "ID", "Name", "Quantity", "Price", "Other Info" });
		default:
		}
		nextTable.setEnabled(false);
		centerScrollPane = new JScrollPane(nextTable);
		mainWindow.add(centerScrollPane, CENTER);
		mainWindow.validate();
		mainWindow.pack();
	}

	/**
	 * This method reads in a Main object from the database file and runs it, or
	 * if none exists, creates a new Main object and runs that instead.
	 * 
	 * @param args
	 *            the arguments passed from the JVM, which are ignored.
	 */
	public static void main(String[] args) {
		try {
			ObjectInputStream databaseInStream = new ObjectInputStream(
					new BufferedInputStream(new FileInputStream(DATABASE_FILENAME)));
			((Main) databaseInStream.readObject()).run();
			databaseInStream.close();
		} catch (Exception e) {
			LOGGER.warning(DATABASE_FILENAME + " does not exist, starting new instance.");
			new Main();
		}
	}
}