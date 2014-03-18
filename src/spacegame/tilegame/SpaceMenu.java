package spacegame.tilegame;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import spacegame.graphics.ScreenManager;
import spacegame.input.GameAction;
import spacegame.input.InputManager;

public class SpaceMenu implements ActionListener{
	
	private static final String KEYBOARD_INSTRUCTIONS =
	        "<html>Click an action's input box to change it's keys." +
	        "<br>An action can have at most three keys associated " +
	        "with it.<br>Press Backspace to clear an action's keys.";
	
	
	ScreenManager screen;
	InputManager inputManager;
	private JPanel menuSpace;
	private JButton configButton;
	
	public JPanel dialog;
    private JButton okButton;
    private List inputs;
    private GameManager parent;
	
	public SpaceMenu(GameManager gm){
		parent = gm;
		this.screen = gm.screen;
		this.inputManager = gm.inputManager;
		menuSpace = new JPanel();
		configButton = createButton("config", "Change Settings");
		JFrame frame = screen.getFullScreenWindow();
		
		Container contentPane = frame.getContentPane();
		
        // make sure the content pane is transparent
        if (contentPane instanceof JComponent) {
            ((JComponent)contentPane).setOpaque(false);
        }
        
        // add components to the screen's content pane
        contentPane.setLayout(new FlowLayout(FlowLayout.LEFT));
        contentPane.add(configButton);

        // explicitly layout components (needed on some systems)
        frame.validate();
        
        inputs = new ArrayList();
        dialog = createDialogMenu();
        
     // add the dialog to the "modal dialog" layer of the
        // screen's layered pane.
        screen.getFullScreenWindow().getLayeredPane().add(dialog,
            JLayeredPane.MODAL_LAYER);
	}
	
	private JPanel createDialogMenu() {
		 // create the list of GameActions and mapped keys
        JPanel configPanel = new JPanel(new GridLayout(5,2,2,2));
        addActionConfig(configPanel, parent.moveLeft);
        addActionConfig(configPanel, parent.moveRight);

        // create the panel containing the OK button
        JPanel bottomPanel = new JPanel(new FlowLayout());
        okButton = new JButton("OK");
        okButton.setFocusable(false);
        okButton.addActionListener(this);
        bottomPanel.add(okButton);

        // create the panel containing the instructions.
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel(KEYBOARD_INSTRUCTIONS));

        // create the dialog border
        Border border =
            BorderFactory.createLineBorder(Color.black);

        // create the config dialog.
        dialog = new JPanel(new BorderLayout());
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(configPanel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setBorder(border);
        dialog.setVisible(false);
        dialog.setSize(dialog.getPreferredSize());

        // center the dialog
        dialog.setLocation(
            (screen.getWidth() - dialog.getWidth()) / 2,
            (screen.getHeight() - dialog.getHeight()) / 2);

        return dialog;
	}

	/**
	    Creates a Swing JButton. The image used for the button is
	    located at "../images/menu/" + name + ".png". The image is
	    modified to create a "default" look (translucent) and a
	    "pressed" look (moved down and to the right).
	    <p>The button doesn't use Swing's look-and-feel and
	    instead just uses the image.
	*/
	public JButton createButton(String name, String toolTip) {
	
	    // load the image
	    String imagePath = "images/menu/" + name + ".png";
	    ImageIcon iconRollover = new ImageIcon(imagePath);
	    int w = iconRollover.getIconWidth();
	    int h = iconRollover.getIconHeight();
	
	    // get the cursor for this button
	    Cursor cursor =
	        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	
	    // make translucent default image
	    Image image = screen.createCompatibleImage(w, h,
	        Transparency.TRANSLUCENT);
	    Graphics2D g = (Graphics2D)image.getGraphics();
	    Composite alpha = AlphaComposite.getInstance(
	        AlphaComposite.SRC_OVER, .5f);
	    g.setComposite(alpha);
	    g.drawImage(iconRollover.getImage(), 0, 0, null);
	    g.dispose();
	    ImageIcon iconDefault = new ImageIcon(image);
	
	    // make a pressed iamge
	    image = screen.createCompatibleImage(w, h,
	        Transparency.TRANSLUCENT);
	    g = (Graphics2D)image.getGraphics();
	    g.drawImage(iconRollover.getImage(), 2, 2, null);
	    g.dispose();
	    ImageIcon iconPressed = new ImageIcon(image);
	
	    // create the button
	    JButton button = new JButton();
	    button.addActionListener(this);
	    button.setIgnoreRepaint(true);
	    button.setFocusable(false);
	    button.setToolTipText(toolTip);
	    button.setBorder(null);
	    button.setContentAreaFilled(false);
	    button.setCursor(cursor);
	    button.setIcon(iconDefault);
	    button.setRolloverIcon(iconRollover);
	    button.setPressedIcon(iconPressed);
	
	    return button;
	}
	
    /**
	    Adds a label containing the name of the GameAction and an
	    InputComponent used for changing the mapped keys.
	*/
	private void addActionConfig(JPanel configPanel,
	    GameAction action)
	{
	    JLabel label = new JLabel(action.getName(), JLabel.RIGHT);
	    InputComponent input = new InputComponent(action);
	    configPanel.add(label);
	    configPanel.add(input);
	    inputs.add(input);
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
	        if (e.getSource() == configButton || e.getSource() == okButton) {
	            // hides the config dialog
	            parent.configAction.tap();
	        }
		
	}
	
	/**
	    Resets the text displayed in each InputComponent, which
	    is the names of the mapped keys.
	*/
	private void resetInputs() {
	    for (int i=0; i<inputs.size(); i++) {
	        ((InputComponent)inputs.get(i)).setText();
	    }
	}
	
	
	
	
	/**
	    The InputComponent class displays the keys mapped to a
	    particular action and allows the user to change the mapped
	    keys. The user selects an InputComponent by clicking it,
	    then can press any key or mouse button (including the
	    mouse wheel) to change the mapped value.
	*/
	class InputComponent extends JTextField  {
	
	    private GameAction action;
	
	    /**
	        Creates a new InputComponent for the specified
	        GameAction.
	    */
	    public InputComponent(GameAction action) {
	        this.action = action;
	        setText();
	        enableEvents(KeyEvent.KEY_EVENT_MASK |
	            MouseEvent.MOUSE_EVENT_MASK |
	            MouseEvent.MOUSE_MOTION_EVENT_MASK |
	            MouseEvent.MOUSE_WHEEL_EVENT_MASK);
	    }
	
	
	    /**
	        Sets the displayed text of this InputComponent to the
	        names of the mapped keys.
	    */
	    private void setText() {
	        String text = "";
	        List list = inputManager.getMaps(action);
	        if (list.size() > 0) {
	            for (int i=0; i<list.size(); i++) {
	                text+=(String)list.get(i) + ", ";
	            }
	            // remove the last comma
	            text = text.substring(0, text.length() - 2);
	        }
	
	        // make sure we don't get deadlock
	        synchronized (getTreeLock()) {
	            setText(text);
	        }
	
	    }
	
	
	    /**
	        Maps the GameAction for this InputComponent to the
	        specified key or mouse action.
	    */
	    private void mapGameAction(int code, boolean isMouseMap) {
	        if (inputManager.getMaps(action).size() >= 3) {
	            inputManager.clearMap(action);
	        }
	        if (isMouseMap) {
	            inputManager.mapToMouse(action, code);
	        }
	        else {
	            inputManager.mapToKey(action, code);
	        }
	        resetInputs();
	        screen.getFullScreenWindow().requestFocus();
	    }
	
	
	    // alternative way to intercept key events
	    protected void processKeyEvent(KeyEvent e) {
	        if (e.getID() == e.KEY_PRESSED) {
	            // if backspace is pressed, clear the map
	            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE &&
	                inputManager.getMaps(action).size() > 0)
	            {
	                inputManager.clearMap(action);
	                setText("");
	                screen.getFullScreenWindow().requestFocus();
	            }
	            else {
	                mapGameAction(e.getKeyCode(), false);
	            }
	        }
	        e.consume();
	    }
	
	
	    // alternative way to intercept mouse events
	    protected void processMouseEvent(MouseEvent e) {
	        if (e.getID() == e.MOUSE_PRESSED) {
	            if (hasFocus()) {
	                int code = InputManager.getMouseButtonCode(e);
	                mapGameAction(code, true);
	            }
	            else {
	                requestFocus();
	            }
	        }
	        e.consume();
	    }
	
	
	    // alternative way to intercept mouse events
	    protected void processMouseMotionEvent(MouseEvent e) {
	        e.consume();
	    }
	
	
	    // alternative way to intercept mouse events
	    protected void processMouseWheelEvent(MouseWheelEvent e) {
	        if (hasFocus()) {
	            int code = InputManager.MOUSE_WHEEL_DOWN;
	            if (e.getWheelRotation() < 0) {
	                code = InputManager.MOUSE_WHEEL_UP;
	            }
	            mapGameAction(code, true);
	        }
	        e.consume();
	    }
	}

}
