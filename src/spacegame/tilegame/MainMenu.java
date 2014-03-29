package spacegame.tilegame;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import spacegame.graphics.Animation;
import spacegame.graphics.ScreenManager;
import spacegame.graphics.Sprite;
import spacegame.input.GameAction;
import spacegame.input.InputManager;
import spacegame.tilegame.SpaceMenu.InputComponent;
import spacegame.tilegame.sprites.Player;
import spacegame.tilegame.sprites.Ship;
import spacegame.tilegame.sprites.Turret;

public class MainMenu implements ActionListener, ChangeListener{
	
	JFrame frame;
	ScreenManager screen;
	InputManager inputManager;
	private GameManager parent;
	
	//initial menu
	private JButton configButton;
	
	//ship menu
	JTabbedPane tabbedShipMenu;
	
	//build menu
	JTabbedPane tabbedBuildMenu;
	JButton buildTurretButton;
	JButton sndPlayerButton;
	
	//main menu
	public boolean displayMainMenu;
	JTabbedPane tabbedMainMenu;
	
	//input mapping menu
	JPanel inputMapMenu;
    private JButton okButton;
    private List inputs;
	
	private static final String INPUT_MAP_INSTRUCTIONS =
	        "<html>Click an action's input box to change it's keys." +
	        "<br>An action can have at most three keys associated " +
	        "with it.<br>Press Backspace to clear an action's keys.";

	public MainMenu(GameManager p){
		init(p);
	}
	
	private void init(GameManager p){
		
		parent = p;
		this.screen = p.screen;
		this.inputManager = p.inputManager;
		configButton = createButton("config", "Change Settings");
		frame = screen.getFullScreenWindow();
		
		 inputs = new ArrayList();
	     tabbedMainMenu = createMainMenu();
	     tabbedShipMenu = createShipMenu();
	     tabbedBuildMenu = createBuildMenu();
	     displayMainMenu = false;
	     
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
	     
	     // add the dialog to the "modal dialog" layer of the
	     // screen's layered pane.
	     screen.getFullScreenWindow().getLayeredPane().add(tabbedMainMenu,
	         JLayeredPane.MODAL_LAYER);
	     
	     screen.getFullScreenWindow().getLayeredPane().add(tabbedShipMenu,
		         JLayeredPane.MODAL_LAYER);
	     
	     screen.getFullScreenWindow().getLayeredPane().add(tabbedBuildMenu,
		         JLayeredPane.MODAL_LAYER);
	}
	
	private JTabbedPane createMainMenu(){
		UIManager.put("TabbedPane.contentOpaque", false);
        
		JTabbedPane menu = new JTabbedPane();
		menu.setOpaque(false);
		menu.setBackground(new Color(0,0,0,5));

		inputMapMenu = createInputMapMenu();
		menu.addTab("Input Map", null, inputMapMenu,
		                  "Does nothing");
		menu.setMnemonicAt(0, KeyEvent.VK_1);

		JComponent panel2 = makeTextPanel("Panel #2");
		menu.addTab("Tab 2", null, panel2,
		                  "Does twice as much nothing");
		menu.setMnemonicAt(1, KeyEvent.VK_2);

		JComponent panel3 = makeTextPanel("Panel #3");
		menu.addTab("Tab 3", null, panel3,
		                  "Still does nothing");
		menu.setMnemonicAt(2, KeyEvent.VK_3);

		JComponent panel4 = makeTextPanel(
		        "Panel #4 (has a preferred size of 410 x 50).");
		panel4.setPreferredSize(new Dimension(410, 50));
		menu.addTab("Tab 4", null, panel4,
		                      "Does nothing at all");
		menu.setMnemonicAt(3, KeyEvent.VK_4);
		
		// create the dialog border
        Border border =
            BorderFactory.createLineBorder(Color.black);
        
		menu.setBorder(border);
		menu.setVisible(false);
		
		menu.setSize(menu.getPreferredSize());

	        // center the dialog
		menu.setLocation(
	            (screen.getWidth() - menu.getWidth()) / 2,
	            (screen.getHeight() - menu.getHeight()) / 2);
		return menu;
	}
	
	private JTabbedPane createBuildMenu(){
		JTabbedPane menu = new JTabbedPane();
		JComponent panel1 = makeBuildMenu();
		menu.addTab("Build", null, panel1,
		                  "Build Useful Tools");
		menu.setMnemonicAt(0, KeyEvent.VK_1);

		JComponent panel2 = makeTextPanel("Panel #2");
		menu.addTab("Tab 2", null, panel2,
		                  "Does twice as much nothing");
		menu.setMnemonicAt(1, KeyEvent.VK_2);

		JComponent panel3 = makeTextPanel("Panel #3");
		menu.addTab("Tab 3", null, panel3,
		                  "Still does nothing");
		menu.setMnemonicAt(2, KeyEvent.VK_3);

		JComponent panel4 = makeTextPanel(
		        "Panel #4 (has a preferred size of 410 x 50).");
		panel4.setPreferredSize(new Dimension(200, 100));
		menu.addTab("Tab 4", null, panel4,
		                      "Does nothing at all");
		menu.setMnemonicAt(3, KeyEvent.VK_4);
		
		// create the dialog border
        Border border =
            BorderFactory.createLineBorder(Color.black);
        
		menu.setBorder(border);
		menu.setVisible(false);
		
		menu.setSize(menu.getPreferredSize());

		menu.setLocation(
	            (screen.getWidth() - menu.getWidth()) / 2,
	            (screen.getHeight() - menu.getHeight()) / 2);
		return menu;
	}
	
	private JTabbedPane createShipMenu(){
		JTabbedPane menu = new JTabbedPane();
		JComponent panel1 = makeShipSliderMenu();
		menu.addTab("Ship Adjustments", null, panel1,
		                  "Adjust Ship Specs");
		menu.setMnemonicAt(0, KeyEvent.VK_1);

		JComponent panel2 = makeTextPanel("Panel #2");
		menu.addTab("Tab 2", null, panel2,
		                  "Does twice as much nothing");
		menu.setMnemonicAt(1, KeyEvent.VK_2);

		JComponent panel3 = makeTextPanel("Panel #3");
		menu.addTab("Tab 3", null, panel3,
		                  "Still does nothing");
		menu.setMnemonicAt(2, KeyEvent.VK_3);

		JComponent panel4 = makeTextPanel(
		        "Panel #4 (has a preferred size of 410 x 50).");
		panel4.setPreferredSize(new Dimension(200, 100));
		menu.addTab("Tab 4", null, panel4,
		                      "Does nothing at all");
		menu.setMnemonicAt(3, KeyEvent.VK_4);
		
		// create the dialog border
        Border border =
            BorderFactory.createLineBorder(Color.black);
        
		menu.setBorder(border);
		menu.setVisible(false);
		
		menu.setSize(menu.getPreferredSize());

		menu.setLocation(
	            (screen.getWidth() - menu.getWidth()) / 2,
	            (screen.getHeight() - menu.getHeight()) / 2);
		return menu;
	}
	
	private JPanel makeBuildMenu(){
		JPanel buildMenu = new JPanel(new GridLayout(0,1));
		buildTurretButton = new JButton("Build Turret");
		sndPlayerButton = new JButton("2nd Player");
		buildTurretButton.addActionListener(this);
		sndPlayerButton.addActionListener(this);
		buildMenu.add(buildTurretButton);
		buildMenu.add(sndPlayerButton);
		return buildMenu;
	}
	
	public JPanel makeShipSliderMenu(){
		JPanel sliderMenu = new JPanel(new GridLayout(0,1));
		JSlider powerSlider = createShipSlider("power");
		JSlider speedSlider = createShipSlider("speed");
		JSlider hitpointSlider = createShipSlider("hitpoint");
		
		JLabel powerLabel = new JLabel("Power");
		JLabel speedLabel = new JLabel("Speed");
		JLabel hitpointLabel = new JLabel("Hit Points");
				
		sliderMenu.add(powerLabel);
		sliderMenu.add(powerSlider);
		sliderMenu.add(speedLabel);
		sliderMenu.add(speedSlider);
		sliderMenu.add(hitpointLabel);
		sliderMenu.add(hitpointSlider);
		return sliderMenu;
	}
	
	public void updateShipSliders(JPanel sliderMenu){
		
		JLabel label = ((JLabel)sliderMenu.getComponent(0));
		double power = parent.getMap().getPlayer().power;
		label.setText("Power: " + power);
		
		label = ((JLabel)sliderMenu.getComponent(2));
		double speed = parent.getMap().getPlayer().speed;
		label.setText("Speed: " + speed);
		
		label = ((JLabel)sliderMenu.getComponent(4));
		double hitPoints = parent.getMap().getPlayer().getHitpoints();
		label.setText("Hit Points: " + hitPoints);
		
	}
	
	private JSlider createShipSlider(String s){
		
		if(s.equals("power")){
			ShipSlider powerSlider;
			powerSlider = new ShipSlider("power", Ship.POWER_MIN, 
					Ship.POWER_MAX, parent.getMap().getPlayer().power);
			//Create the label table
			Hashtable labelTable = new Hashtable();
			labelTable.put( new Integer( Ship.POWER_MIN ), new JLabel(String.valueOf(Ship.POWER_MIN)) );
			labelTable.put( new Integer( Ship.POWER_MAX/2 ), new JLabel(String.valueOf(Ship.POWER_MAX/2)));
			labelTable.put( new Integer( Ship.POWER_MAX ), new JLabel(String.valueOf(Ship.POWER_MAX)) );
			powerSlider.setLabelTable( labelTable );
			powerSlider.setPaintLabels(true);
			powerSlider.addChangeListener(this);
			powerSlider.setMajorTickSpacing(100);
			powerSlider.setPaintTicks(true);
			return powerSlider;
		}else if(s.equals("speed")){
			ShipSlider speedSlider;
			speedSlider = new ShipSlider("speed", Ship.SPEED_MIN, 
					Ship.SPEED_MAX, parent.getMap().getPlayer().speed);
			//Create the label table
			Hashtable labelTable = new Hashtable();
			labelTable.put( new Integer( Ship.SPEED_MAX ), new JLabel(String.valueOf(Ship.SPEED_MAX)) );
			labelTable.put( new Integer( Ship.SPEED_MAX/2 ), new JLabel(String.valueOf(Ship.SPEED_MAX/2)));
			labelTable.put( new Integer( Ship.SPEED_MAX ), new JLabel(String.valueOf(Ship.SPEED_MAX)) );
			speedSlider.setLabelTable( labelTable );
			speedSlider.setPaintLabels(true);
			speedSlider.addChangeListener(this);
			speedSlider.setMajorTickSpacing(100);
			speedSlider.setPaintTicks(true);
			return speedSlider;
		}else{
			ShipSlider hitpointSlider;
			hitpointSlider = new ShipSlider("hitpoint", Ship.HITPOINT_MIN, 
					Ship.HITPOINT_MAX, parent.getMap().getPlayer().getHitpoints());
			//Create the label table
			Hashtable labelTable = new Hashtable();
			labelTable.put( new Integer( Ship.HITPOINT_MAX ), new JLabel(String.valueOf(Ship.HITPOINT_MAX)) );
			labelTable.put( new Integer( Ship.HITPOINT_MAX/2 ), new JLabel(String.valueOf(Ship.HITPOINT_MAX/2)));
			labelTable.put( new Integer( Ship.HITPOINT_MAX ), new JLabel(String.valueOf(Ship.HITPOINT_MAX)) );
			hitpointSlider.setLabelTable( labelTable );
			hitpointSlider.setPaintLabels(true);
			hitpointSlider.addChangeListener(this);
			hitpointSlider.setMajorTickSpacing(100);
			hitpointSlider.setPaintTicks(true);
			return hitpointSlider;
		}
	}

	public void setMenuLocation(JTabbedPane menu, int x, int y){
		 // center the dialog
		menu.setLocation(x, y);
	}
	
	protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
	
	private JPanel createInputMapMenu(){
		JPanel inputMapMenu;
		JPanel configPanel = new JPanel(new GridLayout(4,2,2,2));
		addActionConfig(configPanel, parent.configAction);
		addActionConfig(configPanel, parent.laser);
		addActionConfig(configPanel, parent.fire);
		addActionConfig(configPanel, parent.moveUp);
		addActionConfig(configPanel, parent.moveDown);
        addActionConfig(configPanel, parent.moveLeft);
        addActionConfig(configPanel, parent.moveRight);
        addActionConfig(configPanel, parent.speedBoost);
        addActionConfig(configPanel, parent.shipMenuAction);
        addActionConfig(configPanel, parent.menuAction);
        addActionConfig(configPanel, parent.exit);

        // create the panel containing the OK button
        JPanel bottomPanel = new JPanel(new FlowLayout());
        okButton = new JButton("OK");
        okButton.setFocusable(false);
        okButton.addActionListener(this);
        bottomPanel.add(okButton);

        // create the panel containing the instructions.
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel(INPUT_MAP_INSTRUCTIONS));

        // create the dialog border
        Border border =
            BorderFactory.createLineBorder(Color.black);

        // create the config dialog.
        inputMapMenu = new TransparentPanel(new BorderLayout());
        inputMapMenu.add(topPanel, BorderLayout.NORTH);
        inputMapMenu.add(configPanel, BorderLayout.CENTER);
        inputMapMenu.add(bottomPanel, BorderLayout.SOUTH);
        inputMapMenu.setBorder(border);
        inputMapMenu.setVisible(false);
        inputMapMenu.setSize(inputMapMenu.getPreferredSize());

        // center the dialog
        inputMapMenu.setLocation(
            (screen.getWidth() - inputMapMenu.getWidth()) / 2,
            (screen.getHeight() - inputMapMenu.getHeight()) / 2);

        return inputMapMenu;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	        if (e.getSource() == configButton || e.getSource() == okButton) {
	            // hides the config dialog
	            parent.configAction.tap();
	        }
	        
	        if(e.getSource() == buildTurretButton){
	        	addTurret();
	        }
	        
	        if(e.getSource() == sndPlayerButton){
	        	Animation[] animation = new Animation[1];
	        	Animation a = parent.resourceManager.createPlanetAnim((Image)parent.resourceManager.planetImages.get(0));
	    		animation[0] = (Animation) a;
	    		
	            Ship player2 = new Ship(parent.resourceManager, animation);
	            player2.setX(parent.getMap().getPlayer().getX());
	            player2.setY(parent.getMap().getPlayer().getY());
	        	parent.getMap().setPlayer2(player2);
	        }
	        
	        parent.screen.getFullScreenWindow().requestFocus();
		
	}
	
	public void addTurret(){
		float x = parent.getMap().getPlayer().getX();//-parent.renderer.offX;
    	float y = parent.getMap().getPlayer().getY();//-parent.renderer.offY;
    	Animation[] animation = new Animation[1];
    	Animation a = parent.resourceManager.createPlanetAnim((Image)parent.resourceManager.planetImages.get(0));
		animation[0] = (Animation) a;
		
		//target debugging, add a player near to see if target changes correctly.
		//Player s = (Player) parent.resourceManager.playerSprite.clone();
    	//s.setX(x);
    	//s.setY(y+20);
    	//parent.getMap().addSprite(s);
		
    	Turret turret = new Turret(parent.getMap().getPlayer(), x, y, 1, animation);
    	parent.getMap().addSprite(turret);
    	
    	
    	//System.out.println(x + ":" +y);
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		ShipSlider source = (ShipSlider)e.getSource();
		if (!source.getValueIsAdjusting()) {
			if(source.name.equals("power")){
				int power = (int)source.getValue();
		        parent.getMap().getPlayer().power = power;
			}else if(source.name.equals("speed")){
				int speed = (int)source.getValue();
				parent.getMap().getPlayer().speed = speed;
			}else if(source.name.equals("hitpoint")){
				int hitpoints = (int)source.getValue();
				parent.getMap().getPlayer().setHitpoints(hitpoints);
			}
	    }
		
		parent.screen.getFullScreenWindow().requestFocus();
	}
	
	//methods for initial menu
	
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
	
	//methods for main menu
	
	
	//methods for input mapping menu
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
	
	public class ShipSlider extends JSlider{
		public String name;
		
		public ShipSlider(String name, int min, int max, int current){
			super(JSlider.HORIZONTAL, min, max, current);	
			this.name = name;
		}
	}
	
	private class TransparentPanel extends JPanel {
	    {
	        setOpaque(false);
	    }
	    public TransparentPanel(GridLayout gridLayout) {
	    	super(gridLayout);
			// TODO Auto-generated constructor stub
		}
		public TransparentPanel(FlowLayout flowLayout) {
			super(flowLayout);
			// TODO Auto-generated constructor stub
		}
		public TransparentPanel(BorderLayout borderLayout) {
			super(borderLayout);
			// TODO Auto-generated constructor stub
		}
		public void paintComponent(Graphics2D g) {
	       // g.setColor(new Color(0,0,0,125));
	        Rectangle r = g.getClipBounds();
	        g.fillRect(r.x, r.y, r.width, r.height);
	        super.paintComponent(g);
	    }
	}



	
}
