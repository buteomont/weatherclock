package free.david.wc;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.Properties;

import javax.swing.*;

import free.david.weather.Weather;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class WeatherTime extends JFrame implements ActionListener, WeatherListener
	{
	private static final long	serialVersionUID	=1L;
	private JPanel jContentPane=null;
	private JMenuBar jJMenuBar=null;
	private JMenu fileMenu=null;
	private JMenu chooseMenu=null;
	private JMenu helpMenu=null;
	private JMenuItem exitMenuItem=null;
	private JMenuItem aboutMenuItem=null;
	private FaceMenu faceMenu=null;
	private JMenuItem refreshMenuItem=null;
	private WeatherClock weatherClock = null;
	private Properties specifics=null;
	private UpdatePeriodMenu updatePeriodMenu = null;
	private CountryMenu countryMenu = null;
	private StateMenu stateMenu = null;
	private CityMenu cityMenu = null;
	private boolean redirect=true; //redirect standard out to file by defalult
	private MRUMenu recentMenu = null;
	
	public WeatherTime() throws HeadlessException
		{
		super();
		initialize();
		}

	public WeatherTime(boolean redirect) throws HeadlessException
		{
		super();
		this.redirect=redirect;
		initialize();
		}
	
	public WeatherTime(GraphicsConfiguration gc)
		{
		super(gc);
		initialize();
		}

	public WeatherTime(String title) throws HeadlessException
		{
		super(title);
		initialize();
		}

	public WeatherTime(String title, GraphicsConfiguration gc)
		{
		super(title, gc);
		initialize();
		}

	/**
	 * This method initializes weatherClock	
	 * 	
	 * @return free.david.wc.WeatherClock	
	 */
	private WeatherClock getWeatherClock()
		{
		if (weatherClock==null)
			{
			weatherClock=new WeatherClock(specifics);
			weatherClock.setPreferredSize(new java.awt.Dimension(300,300));
			weatherClock.setBounds(new java.awt.Rectangle(0,0,292,285));
			weatherClock.getWeather().addWeatherListener(this);
			if (weatherClock.getWeather().getCountry().equals("US"))
				getStateMenu().setEnabled(true);
			else
				getStateMenu().setEnabled(false);
			
//			getCityMenu().populate(); //refresh the city menu
			}
		return weatherClock;
		}

	/**
	 * This method initializes updatePeriodMenu	
	 * 	
	 * @return free.david.wc.UpdatePeriodMenu	
	 */
	private UpdatePeriodMenu getUpdatePeriodMenu()
		{
		if (updatePeriodMenu==null)
			{
			updatePeriodMenu=new UpdatePeriodMenu(getWeatherClock().getWeather());
			}
		return updatePeriodMenu;
		}

	private CountryMenu getCountryMenu()
		{
		if (countryMenu == null)
			{
			SortedMap countryMap=new TreeMap();
				SortedMap ss=getWeatherClock().getWeather().getCountryList();
				for (Iterator codes=ss.keySet().iterator();
						codes.hasNext();)
					{
					String code=(String)codes.next();
					countryMap.put(ss.get(code), code);
					}
			countryMenu=new CountryMenu(countryMap, getWeatherClock().getWeather().getCountry());
			countryMenu.addActionListener(this);
			addPropertyChangeListener("Country", countryMenu);
			}
		return countryMenu;
		}

	private StateMenu getStateMenu()
		{
		if (stateMenu == null)
			{
			stateMenu=new StateMenu(getWeatherClock().getWeather().getStateList(),getWeatherClock().getWeather().getStateName());
			stateMenu.addActionListener(this);
			addPropertyChangeListener("State", stateMenu);
			}
		return stateMenu;
		}

	/**
	 * This method initializes cityMenu	
	 * 	
	 * @return free.david.wc.CityMenu	
	 */
	private CityMenu getCityMenu()
		{
		if (cityMenu==null)
			{
			Weather w=getWeatherClock().getWeather();
			cityMenu=new CityMenu(w.getCityList(), w.getCity());
			cityMenu.addActionListener(getRecentMenu());
			cityMenu.addActionListener(this);
			addPropertyChangeListener("Station", cityMenu);
			}
		return cityMenu;
		}

	/**
	 * This method initializes recentMenu	
	 * 	
	 * @return free.david.wc.MRUMenu	
	 */
	private MRUMenu getRecentMenu()
		{
		if (recentMenu == null)
			{
			recentMenu=new MRUMenu(specifics, getWeatherClock());
//			recentMenu.addActionListener(getCountryMenu());
//			recentMenu.addActionListener(getStateMenu());
//			recentMenu.addActionListener(getCityMenu());
//			recentMenu.addActionListener(getFaceMenu());
			recentMenu.addActionListener(this);
			}
		return recentMenu;
		}

	/**
	 * @param args
	 */
	public static void main(String[] args)
		{
		WeatherTime application=new WeatherTime(args.length>0&&args[0].equalsIgnoreCase("-noRedirect")?false:true);
		application.setVisible(true);
		}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
		{
		if (redirect)
			try
				{
				PrintStream newOut=new PrintStream(new FileOutputStream("weatherclock.log",true));
				System.setOut(newOut); 
				System.setErr(newOut); 
				}
			catch (FileNotFoundException e)
				{
				e.printStackTrace();
				System.exit(-1);
				}
		System.out.println(WeatherClock.timeStamp()+"WeatherTime started.");
		loadProperties();
		ToolTipManager.sharedInstance().setDismissDelay(25000);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("David's Weather Clock");
		this.setJMenuBar(getJJMenuBar());
		this.setSize(new java.awt.Dimension(300, 340)); //for visual editor
		this.setSize(Integer.parseInt(specifics.getProperty("width")),
					 Integer.parseInt(specifics.getProperty("height")));
		int xPos=(int)getToolkit().getScreenSize().getWidth()/2-(getWidth()/2);
		int yPos=(int)getToolkit().getScreenSize().getHeight()/2-(getHeight()/2);
		if (specifics.containsKey("xPosition"))
			xPos=Integer.parseInt(specifics.getProperty("xPosition"));
		if (specifics.containsKey("yPosition"))
			yPos=Integer.parseInt(specifics.getProperty("yPosition"));
		this.setLocation(xPos,yPos);
		this.setContentPane(getJContentPane());
		
		getWeatherClock().setImageURLList(getFaceMenu().getAllImageURLs());
		newTitle();
		}

	private void loadProperties()
		{
		//get default properties
		Properties defaults=new Properties();
		ClassLoader cl=this.getClass().getClassLoader();
		try
			{
			InputStream in=cl.getResourceAsStream("defaults.ini");
			defaults.load(in);
			in.close();
			}
		catch (IOException e)
			{
			e.printStackTrace();
			System.out.println(WeatherClock.timeStamp()+"Unable to read initialization file defaults.ini.");
			System.exit(-1);
			}
		
		//get customized properties
		specifics = new Properties(defaults);
		try
			{
			FileInputStream in=new FileInputStream("weatherclock.ini");
			if (in!=null)
				{
				specifics.load(in);
				in.close();
				}
			}
		catch (FileNotFoundException e)
			{
			System.out.println(WeatherClock.timeStamp()+"INI file not found.");
			//no problem, we'll make one later
			}
		catch (IOException e)
			{
			e.printStackTrace();
			System.out.println(WeatherClock.timeStamp()+"Error reading initialization file weatherclock.properties.");
			System.exit(-1);
			}

		this.addWindowListener(new WindowListener()
			{
			public void windowOpened(WindowEvent e){}//don't care
			public void windowIconified(WindowEvent e){}//don't care
			public void windowDeiconified(WindowEvent e){}//don't care
			public void windowDeactivated(WindowEvent e){}//don't care
			public void windowClosing(WindowEvent e)
				{
				writeINI();
				System.out.println(WeatherClock.timeStamp()+"Shutting down WeatherTime.");
				}
			public void windowClosed(WindowEvent e){}//don't care
			public void windowActivated(WindowEvent e){}//don't care
			});
		}

	protected void writeINI()
		{
		String face="The Moon";
		if (getWeatherClock().isFaceImage())
			{
			face=getWeatherClock().isRandomImage()?"Random Image":getWeatherClock().getImageURL();
			}
		try{specifics.put("useImage",face);}catch (Exception e){e.printStackTrace();}
		try{specifics.put("width", getWidth()+"");}catch (Exception e){e.printStackTrace();}
		try{specifics.put("height", getHeight()+"");}catch (Exception e){e.printStackTrace();}
		try{specifics.put("xPosition", getLocation().x+"");}catch (Exception e){e.printStackTrace();}
		try{specifics.put("yPosition", getLocation().y+"");}catch (Exception e){e.printStackTrace();}
		try{specifics.put("country",getWeatherClock().getWeather().getCountry());}catch (Exception e){e.printStackTrace();}
		try{specifics.put("state",getWeatherClock().getWeather().getStateName());}catch (Exception e){e.printStackTrace();}
		try{specifics.put("city",getWeatherClock().getWeather().getCity());}catch (Exception e){e.printStackTrace();}
		try{specifics.put("imageURL",getWeatherClock().getImageURL());}catch (Exception e){e.printStackTrace();}

		try
			{
			FileOutputStream out = new FileOutputStream("weatherclock.ini");
			specifics.store(out, "Weatherclock Initialization File");
			out.close();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane()
		{
		if (jContentPane==null)
			{
			jContentPane=new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getWeatherClock(), java.awt.BorderLayout.CENTER);
			}
		return jContentPane;
		}

	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar()
		{
		if (jJMenuBar==null)
			{
			jJMenuBar=new JMenuBar();
			jJMenuBar.add(getFileMenu());
			jJMenuBar.add(getChooseMenu());
			jJMenuBar.add(getHelpMenu());
			}
		return jJMenuBar;
		}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu()
		{
		if (fileMenu==null)
			{
			fileMenu=new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getRefreshMenuItem());
			fileMenu.add(getRecentMenu());
			fileMenu.add(getExitMenuItem());
			}
		return fileMenu;
		}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getChooseMenu()
		{
		if (chooseMenu==null)
			{
			chooseMenu=new JMenu();
			chooseMenu.setText("Choose");
			chooseMenu.add(getUpdatePeriodMenu());
			chooseMenu.add(getFaceMenu());
			chooseMenu.add(getCountryMenu());
			chooseMenu.add(getStateMenu());
			chooseMenu.add(getCityMenu());
			}
		return chooseMenu;
		}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getHelpMenu()
		{
		if (helpMenu==null)
			{
			helpMenu=new JMenu();
			helpMenu.setText("Help");
			helpMenu.add(getAboutMenuItem());
			}
		return helpMenu;
		}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getExitMenuItem()
		{
		if (exitMenuItem==null)
			{
			exitMenuItem=new JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
						{
						System.out.println(WeatherClock.timeStamp()+"Terminating WeatherTime.");
						writeINI();
						System.exit(0);
						}
				});
			}
		return exitMenuItem;
		}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAboutMenuItem()
		{
		if (aboutMenuItem==null)
			{
			aboutMenuItem=new JMenuItem();
			aboutMenuItem.setText("About David's Weather Clock");
			aboutMenuItem.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
						{
						final JDialog abt=new JDialog(WeatherTime.this, "About", true);
						abt.setSize(300, 350);
						abt.setLocation(getX()+20, getY()+20);
						JButton closeButton=new JButton("Close");
						closeButton.addActionListener(new ActionListener()
							{
							public void actionPerformed(ActionEvent arg0)
								{
								abt.dispose();
								}
							});
						Container about=abt.getContentPane();
						about.setLayout(new FlowLayout());
						about.add(new JLabel("<html><b><center>David's Weather Clock<br><font size=-2>by David E. Powell</font></center></b>" 
								+"<br>The moon image on the clock face tells the "
								+"<br>weather as well as the time."
								+"<br>The hue (color) of the clock face"
								+"<br>becomes more red as the temperature rises."
								+"<br>Its saturation (amount of color) changes"
								+"<br>based on the sky conditions. The brightness"
								+"<br>of the moon depends on how close the"
								+"<br>time of day is to noon. The red triangle"
								+"<br> shows the wind speed and direction."
								+"<br><br>You can hover your mouse pointer over"
								+"<br>the clock for more detailed weather"
								+"<br>information."
							));
						about.add(closeButton);
						abt.setVisible(true);
						}
				});
			}
		return aboutMenuItem;
		}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private FaceMenu getFaceMenu()
		{
		if (faceMenu==null)
			{
			faceMenu=new FaceMenu(specifics, this);
			faceMenu.addActionListener(this);
			addPropertyChangeListener("Face", faceMenu);
			}
		return faceMenu;
		}
	
	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getRefreshMenuItem()
		{
		if (refreshMenuItem==null)
			{
			refreshMenuItem=new JMenuItem();
			refreshMenuItem.setText("Force Refresh");
			refreshMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
					Event.CTRL_MASK, true));
			refreshMenuItem.addActionListener(new ActionListener()
							{
								public void actionPerformed(ActionEvent e)
									{
									try
										{
										getWeatherClock().getWeather().refresh();
										}
									catch (IOException e1)
										{
										e1.printStackTrace();
										}
									}
							});
			}
		return refreshMenuItem;
		}

	public void actionPerformed(ActionEvent e)
		{
		Object src=e.getSource();
		String command=e.getActionCommand();
		
		//Check for new face
		if (src==getFaceMenu())
			{
			getWeatherClock().setRandomImage(false);
			if (command.equalsIgnoreCase("moon"))
				{
				getWeatherClock().setFaceImage(false);
				}
			else if (command.equalsIgnoreCase("randomImage"))
				{
				getWeatherClock().setRandomImage(true);
				getWeatherClock().setImageURLList(getFaceMenu().getAllImageURLs());
				getWeatherClock().setFaceImage(true);
				}
			else
				{
				getWeatherClock().setImageURL(getFaceMenu().getSelectedImageURL());
				getWeatherClock().setFaceImage(true);
				}
			getWeatherClock().setFaceValid(false);
			getWeatherClock().repaint();
			}
		
		//Check for new country
		else if (src==getCountryMenu())
			{
			String cntry=getCountryMenu().getSelectedCountry();
			try
				{
				getWeatherClock().getWeather().setCountry(cntry);
				}
			catch (IOException e1)
				{
				e1.printStackTrace();
				}
			newTitle();
			getCityMenu().setSelectedStation(getWeatherClock().getWeather().getCity());
			getCityMenu().setStationList(getWeatherClock().getWeather().getCityList());
			getStateMenu().setEnabled(cntry.equals("US"));
			
			//notify State menu
			PropertyChangeListener[] pls= getPropertyChangeListeners("State");
			for (int i=0;i<pls.length;i++)
				pls[i].propertyChange(new PropertyChangeEvent(this,"State","",getWeatherClock().getWeather().getStateName()));
			
			}
		
		//Check for new state
		else if (src==getStateMenu())
			{
			try
				{
				getWeatherClock().getWeather().setStateName(getStateMenu().getSelectedState());
				}
			catch (IOException e1)
				{
				e1.printStackTrace();
				}
			newTitle();
			getCityMenu().setSelectedStation(getWeatherClock().getWeather().getCity());
			getCityMenu().setStationList(getWeatherClock().getWeather().getCityList());

			//Notify station listeners
			//City
			PropertyChangeListener[] pls= getPropertyChangeListeners("Station");
			for (int i=0;i<pls.length;i++)
				pls[i].propertyChange(new PropertyChangeEvent(this,"Station","",getWeatherClock().getWeather().getCity()));
			
			}
		
		//Check for new city
		else if (src==getCityMenu())
			{
			getWeatherClock().getWeather().setCity(getCityMenu().getSelectedStation());
			newTitle();
			getWeatherClock().getWeather().interrupt();
			}
		else if (src==getRecentMenu())
			{
			MRUMenu m=getRecentMenu();
			Weather weather=getWeatherClock().getWeather();
			try
				{
				weather.setCountry(m.getLastSelectedCountry());
				if (m.getLastSelectedCountry().equals("US"))
					{
					weather.setStateName(m.getLastSelectedState());
					getCityMenu().setStationList(getWeatherClock().getWeather().getCityList());
					getStateMenu().setEnabled(true);
					}
				else
					getStateMenu().setEnabled(false);
				getCityMenu().setStationList(getWeatherClock().getWeather().getCityList());
				weather.setCity(m.getLastSelectedStation());
				if (m.getLastSelectedFace().equals("The Moon"))
					weatherClock.setFaceImage(false);
				else
					{
					weatherClock.setImageURL(m.getLastSelectedFace());
					weatherClock.setFaceImage(true);
					}
				weather.interrupt();
				}
			catch (IOException e1)
				{
				e1.printStackTrace();
				}
			
			//propagate to our listeners
			//Countries
			PropertyChangeListener[] pls= getPropertyChangeListeners("Country");
			for (int i=0;i<pls.length;i++)
				pls[i].propertyChange(new PropertyChangeEvent(this,"Country","",m.getLastSelectedCountry()));

			//States
			pls= getPropertyChangeListeners("State");
			for (int i=0;i<pls.length;i++)
				pls[i].propertyChange(new PropertyChangeEvent(this,"State","",m.getLastSelectedState()));

			//Stations
			pls= getPropertyChangeListeners("Station");
			for (int i=0;i<pls.length;i++)
				pls[i].propertyChange(new PropertyChangeEvent(this,"Station","",m.getLastSelectedStation()));
			
			//Face Images
			pls= getPropertyChangeListeners("Face");
			for (int i=0;i<pls.length;i++)
				pls[i].propertyChange(new PropertyChangeEvent(this,"Face","",m.getLastSelectedFace()));
			}
		}

	private void newTitle()
		{
		Weather w=getWeatherClock().getWeather();
		setTitle(w.getCity()+", "+(w.getCountry().equals("US")?w.getStateName():getWeatherClock().translateCountry(w.getCountry())));
		}

	public void inform(Weather weather)
		{
		newTitle();
		}

//	private void showError(String errorMessage)
//		{
//		final JDialog win=new JDialog(this,true);
//		win.setTitle("Error");
//		win.setLocation(getX()+10, getY()+10);
//		win.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//		win.setVisible(true);
//		JLabel msg=new JLabel();
//		msg.setText("<html><body>"+WeatherClock.substitute(errorMessage,"\n", "<br>")+"</body></html>");
//		msg.setHorizontalAlignment(SwingConstants.CENTER);
//		win.getContentPane().add(new JLabel("   "), BorderLayout.WEST);
//		win.getContentPane().add(new JLabel("   "), BorderLayout.NORTH);
//		win.getContentPane().add(msg, BorderLayout.CENTER);
//		JPanel pan=new JPanel(new FlowLayout());
//		win.getContentPane().add(pan,BorderLayout.SOUTH);
//		pan.add(new JButton("Close")
//			{
//			protected void fireActionPerformed(ActionEvent event)
//				{
//				win.dispose();
//				}
//			}, BorderLayout.SOUTH);
//		win.pack();
//		}
	}  //  @jve:decl-index=0:visual-constraint="10,10"
