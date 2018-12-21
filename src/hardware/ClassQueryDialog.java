package hardware;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;

/**
 * This class extends JDialog in order to ask the user which of two classes they
 * would like to create an object for.
 * 
 * @author Peter Wesley Hutcheson
 * @version 1.0
 * @since 1.0
 */
public class ClassQueryDialog extends JDialog {
	private static final Logger LOGGER = Logger.getGlobal();
	private static final long serialVersionUID = 578406964070149300L;
	private boolean cancelled = false;
	private volatile Boolean selectedClass;

	/**
	 * This constructor creates a dialog which will ask the user which of two
	 * classes they would like to create an object for.
	 * 
	 * @param class1
	 *            The first class as an option.
	 * @param class2
	 *            The second class as an option.
	 */
	public ClassQueryDialog(String class1, String class2) {
		super();
		setResizable(false);
		setLayout(new FlowLayout());
		JButton btn1 = new JButton(class1), btn2 = new JButton(class2), cancel = new JButton("Cancel");
		ActionListener dialogListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = ((JButton) e.getSource()).getText();
				if (s == class1) {
					LOGGER.info("User selected class " + class1 + ".");
					selectedClass = true;
				} else if (s == class2) {
					LOGGER.info("User selected class " + class2 + ".");
					selectedClass = false;
				} else if (s == "Cancel") {
					cancelled = true;
					selectedClass = true;
				}
			}
		};
		btn1.addActionListener(dialogListener);
		btn2.addActionListener(dialogListener);
		cancel.addActionListener(dialogListener);
		add(btn1);
		add(btn2);
		add(cancel);
		pack();
		setLocationRelativeTo(null);
	}

	/**
	 * This method shows the dialog and tells which class the user selected.s
	 * 
	 * @return true, if the user selected class 1, false if the user selected
	 *         class 2, does not return and simply throws a CancelException if
	 *         the user pressed "Cancel"
	 * @throws CancelException
	 *             when the user presses "Cancel"
	 */
	public boolean isClass1() throws CancelException {
		selectedClass = null;
		cancelled = false;
		setVisible(true);
		while (selectedClass == null) {
		}
		setVisible(false);
		boolean value = selectedClass;
		if (cancelled)
			throw new CancelException();
		return value;
	}
}