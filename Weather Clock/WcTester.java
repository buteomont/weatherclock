import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import free.david.weather.MetarWeather;

public class WcTester extends JFrame implements ActionListener, ChangeListener
	{

	private JPanel	jContentPane	=null;
	private DebugWeatherClock weatherClock = null;
	private Adjuster time = null;
	private JPanel adjPanel = null;
	private long debugBaseTimeMillis;
	private JPanel skyPanel = null;
	private ButtonGroup skybuttons=new ButtonGroup();
	private JRadioButton heavyRainRadioButton = null;
	private JRadioButton rainjRadioButton = null;
	private JRadioButton ltRainRadioButton = null;
	private Adjuster temperatureAdjuster = null;
	private Adjuster windAdjuster = null;
	private Adjuster phaseAdjuster = null;
	private Adjuster gustAdjuster = null;
	private Adjuster windDirectionAdjuster = null;
	private Adjuster cloudAdjuster = null;
	private JCheckBox rainCheckBox = null;
	private JPanel precipPanel = null;
	private JPanel intensityPanel = null;
	private JCheckBox snowCheckBox = null;
	private JCheckBox fogCheckBox = null;
	private JPanel imagePanel = null;
	private JLabel imageLabel = null;
	private JTextField imageTextField = null;
	private JCheckBox moonCheckBox = null;
	private JCheckBox lightningCheckBox = null;

	public WcTester() throws HeadlessException
		{
		super();
		initialize();
		}

	public WcTester(GraphicsConfiguration arg0)
		{
		super(arg0);
		initialize();
		}

	public WcTester(String arg0) throws HeadlessException
		{
		super(arg0);
		initialize();
		}

	public WcTester(String arg0, GraphicsConfiguration arg1)
		{
		super(arg0, arg1);
		initialize();
		}

	/**
	 * This method initializes weatherClock	
	 * 	
	 * @return free.david.wc.WeatherClock	
	 */
	private DebugWeatherClock getWeatherClock()
		{
		if (weatherClock == null)
			{
			try
				{
				weatherClock=new DebugWeatherClock();
				}
			catch (IOException e)
				{
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			weatherClock.setPreferredSize(new java.awt.Dimension(100,100));
			weatherClock.setError(false);
			weatherClock.setBackgroundTransparent(false);
			weatherClock.setDoubleBuffered(false);
			}
		return weatherClock;
		}

	/**
	 * This method initializes time	
	 * 	
	 * @return Adjuster	
	 */
	private Adjuster getTime()
		{
		if (time == null)
			{
			time=new Adjuster();
			time.setTitle("Time");
			time.addChangeListener(this);
			}
		return time;
		}

	/**
	 * This method initializes adjPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getAdjPanel()
		{
		if (adjPanel == null)
			{
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(8);
			gridLayout.setColumns(1);
			adjPanel=new JPanel();
			adjPanel.setLayout(gridLayout);
			adjPanel.add(getImagePanel(), null);
			adjPanel.add(getCloudAdjuster(), null);
			adjPanel.add(getTime(), null);
			adjPanel.add(getTemperatureAdjuster(), null);
			adjPanel.add(getPhaseAdjuster(), null);
			adjPanel.add(getWindAdjuster(), null);
			adjPanel.add(getWindDirectionAdjuster(), null);
			adjPanel.add(getGustAdjuster(), null);
			}
		return adjPanel;
		}

	/**
	 * This method initializes skyPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSkyPanel()
		{
		if (skyPanel == null)
			{
			skyPanel=new JPanel();
			skyPanel.setLayout(new BorderLayout());
			skyPanel.setPreferredSize(new java.awt.Dimension(95,192));
			skyPanel.add(getPrecipPanel(), java.awt.BorderLayout.NORTH);
			skyPanel.add(getIntensityPanel(), java.awt.BorderLayout.SOUTH);
			
			}
		return skyPanel;
		}

	private JCheckBox getRainCheckBox()
		{
		if (rainCheckBox  == null)
			{
			rainCheckBox=new JCheckBox();
			rainCheckBox.setText("Rain");
			rainCheckBox.addActionListener(this);
			}
		return rainCheckBox;
		}

	private JRadioButton getHeavyRainRadioButton()
	{
	if (heavyRainRadioButton == null)
		{
		heavyRainRadioButton=new JRadioButton();
		heavyRainRadioButton.setText("Heavy");
		heavyRainRadioButton.setHorizontalTextPosition(javax.swing.SwingConstants.TRAILING);
		heavyRainRadioButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		skybuttons.add(heavyRainRadioButton);
		heavyRainRadioButton.addActionListener(this);
		}
	return heavyRainRadioButton;
	}

	/**
	 * This method initializes rainjRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getRainjRadioButton()
		{
		if (rainjRadioButton == null)
			{
			rainjRadioButton=new JRadioButton();
			rainjRadioButton.setText("Normal");
			skybuttons.add(rainjRadioButton);
			rainjRadioButton.addActionListener(this);
			rainjRadioButton.setSelected(true);
			}
		return rainjRadioButton;
		}

	/**
	 * This method initializes ltRainRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getLtRainRadioButton()
		{
		if (ltRainRadioButton == null)
			{
			ltRainRadioButton=new JRadioButton();
			ltRainRadioButton.setText("Light");
			skybuttons.add(ltRainRadioButton);
			ltRainRadioButton.addActionListener(this);
			}
		return ltRainRadioButton;
		}

	/**
	 * This method initializes temperatureAdjuster	
	 * 	
	 * @return Adjuster	
	 */
	private Adjuster getTemperatureAdjuster()
		{
		if (temperatureAdjuster==null)
			{
			temperatureAdjuster=new Adjuster();
			temperatureAdjuster.setTitle("Temp");
			temperatureAdjuster.setMaximum(110);
			temperatureAdjuster.setMajorTickSpacing(20);
			temperatureAdjuster.setMinorTickSpacing(10);
			temperatureAdjuster.setMinimum(-10);
			temperatureAdjuster.addChangeListener(this);
			}
		return temperatureAdjuster;
		}

	/**
	 * This method initializes windAdjuster	
	 * 	
	 * @return Adjuster	
	 */
	private Adjuster getWindAdjuster()
		{
		if (windAdjuster == null)
			{
			BorderLayout borderLayout = new BorderLayout();
			borderLayout.setHgap(0);
			windAdjuster=new Adjuster();
			windAdjuster.setValue(0);
			windAdjuster.setTitle("<html>Wind<br>Speed</html>");
			windAdjuster.setMinimum(0);
			windAdjuster.setPreferredSize(new java.awt.Dimension(240,47));
			windAdjuster.setMajorTickSpacing(10);
			windAdjuster.setMinorTickSpacing(5);
			windAdjuster.setPaintLabels(true);
			windAdjuster.setMaximum(60);
			windAdjuster.addChangeListener(this);
			}
		return windAdjuster;
		}

	/**
	 * This method initializes phaseAdjuster	
	 * 	
	 * @return Adjuster	
	 */
	private Adjuster getPhaseAdjuster()
		{
		if (phaseAdjuster == null)
			{
			phaseAdjuster=new Adjuster();
			phaseAdjuster.setTitle("<html><center>Moon<br>Age</center></html>");
			phaseAdjuster.setValue(7);
			phaseAdjuster.setMajorTickSpacing(5);
			phaseAdjuster.setMinorTickSpacing(1);
			phaseAdjuster.setPaintTicks(true);
			phaseAdjuster.setPaintLabels(true);
			phaseAdjuster.setMaximum(30);
			phaseAdjuster.addChangeListener(this);
			}
		return phaseAdjuster;
		}

	/**
	 * This method initializes gustAdjuster	
	 * 	
	 * @return Adjuster	
	 */
	private Adjuster getGustAdjuster()
		{
		if (gustAdjuster==null)
			{
			gustAdjuster=new Adjuster();
			gustAdjuster.setTitle("<html>Wind<br>Gusts</html>");
			gustAdjuster.setMaximum(60);
			gustAdjuster.setMinorTickSpacing(5);
			gustAdjuster.setMajorTickSpacing(10);
			gustAdjuster.setValue(0);
			gustAdjuster.addChangeListener(this);
			}
		return gustAdjuster;
		}

	/**
	 * This method initializes windDirectionAdjuster	
	 * 	
	 * @return Adjuster	
	 */
	private Adjuster getWindDirectionAdjuster()
		{
		if (windDirectionAdjuster == null)
			{
			windDirectionAdjuster=new Adjuster();
			windDirectionAdjuster.setTitle("<html>Wind<br>Direction</html>");
			windDirectionAdjuster.setMinimum(0);
			windDirectionAdjuster.setMinorTickSpacing(45);
			windDirectionAdjuster.setMajorTickSpacing(90);
			windDirectionAdjuster.setPaintLabels(true);
			windDirectionAdjuster.setPaintTicks(true);
			windDirectionAdjuster.setMaximum(360);
			windDirectionAdjuster.addChangeListener(this);
			}
		return windDirectionAdjuster;
		}

	/**
	 * This method initializes cloudAdjuster	
	 * 	
	 * @return Adjuster	
	 */
	private Adjuster getCloudAdjuster()
		{
		if (cloudAdjuster==null)
			{
			cloudAdjuster=new Adjuster();
			cloudAdjuster.setPreferredSize(new java.awt.Dimension(257,32));
			cloudAdjuster.setMaximum(5);
			cloudAdjuster.setMinimum(0);
			cloudAdjuster.setPaintTicks(true);
			cloudAdjuster.setPaintTrack(true);
			cloudAdjuster.setPaintLabels(true);
			cloudAdjuster.setMajorTickSpacing(1);
			cloudAdjuster.setSnapToTicks(true);
			cloudAdjuster.setValue(0);
			cloudAdjuster.setTitle("<html>Cloud<br>Cover");
			cloudAdjuster.addChangeListener(this);
			}
		return cloudAdjuster;
		}

	/**
	 * This method initializes precipPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPrecipPanel()
		{
		if (precipPanel == null)
			{
			GridLayout gridLayout2 = new GridLayout();
			gridLayout2.setRows(5);
			gridLayout2.setColumns(1);
			precipPanel=new JPanel();
			precipPanel.setPreferredSize(new java.awt.Dimension(82,110));
			precipPanel.setLayout(gridLayout2);
			precipPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED), "Sky", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			precipPanel.add(getRainCheckBox(), null);
			precipPanel.add(getSnowCheckBox(), null);
			precipPanel.add(getFogCheckBox(), null);
			precipPanel.add(getLightningCheckBox(), null);
			}
		return precipPanel;
		}

	/**
	 * This method initializes intensityPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getIntensityPanel()
		{
		if (intensityPanel == null)
			{
			GridLayout gridLayout3 = new GridLayout();
			gridLayout3.setRows(3);
			intensityPanel=new JPanel();
			intensityPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED), "Intensity", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			intensityPanel.setPreferredSize(new java.awt.Dimension(82,82));
			intensityPanel.setLayout(gridLayout3);
			intensityPanel.add(getHeavyRainRadioButton(), null);
			intensityPanel.add(getRainjRadioButton(), null);
			intensityPanel.add(getLtRainRadioButton(), null);
			}
		return intensityPanel;
		}

	/**
	 * This method initializes snowCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getSnowCheckBox()
		{
		if (snowCheckBox == null)
			{
			snowCheckBox=new JCheckBox();
			snowCheckBox.setText("Snow");
			snowCheckBox.addActionListener(this);
			}
		return snowCheckBox;
		}

	/**
	 * This method initializes fogCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getFogCheckBox()
		{
		if (fogCheckBox == null)
			{
			fogCheckBox=new JCheckBox();
			fogCheckBox.setText("Fog");
			fogCheckBox.addActionListener(this);
			}
		return fogCheckBox;
		}

	/**
	 * This method initializes imagePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getImagePanel()
		{
		if (imagePanel == null)
			{
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 2;
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.ipadx = 0;
			gridBagConstraints2.ipady = 0;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.insets = new java.awt.Insets(0,0,0,0);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new java.awt.Insets(0,0,0,5);
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridx = 0;
			imageLabel = new JLabel();
			imageLabel.setText("<html>Image URL:");
			imageLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			imageLabel.setPreferredSize(new java.awt.Dimension(42,24));
			imageLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			imagePanel=new JPanel();
			imagePanel.setPreferredSize(new java.awt.Dimension(329,40));
			imagePanel.setLayout(new GridBagLayout());
			imagePanel.add(imageLabel, gridBagConstraints);
			imagePanel.add(getImageTextField(), gridBagConstraints2);
			imagePanel.add(getMoonCheckBox(), gridBagConstraints1);
			}
		return imagePanel;
		}

	/**
	 * This method initializes imageTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getImageTextField()
		{
		if (imageTextField == null)
			{
			imageTextField=new JTextField();
			imageTextField.setText("http://www.newschannel5.com/skycam/radar_Metro.jpg");
			imageTextField.addActionListener(this);
			}
		return imageTextField;
		}

	/**
	 * This method initializes moonCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getMoonCheckBox()
		{
		if (moonCheckBox==null)
			{
			moonCheckBox=new JCheckBox();
			moonCheckBox.setText("Moon");
			moonCheckBox.setSelected(true);
			moonCheckBox.addActionListener(this);
			}
		return moonCheckBox;
		}

	/**
	 * This method initializes lightningCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getLightningCheckBox()
		{
		if (lightningCheckBox==null)
			{
			lightningCheckBox=new JCheckBox();
			lightningCheckBox.setText("Lightning");
			lightningCheckBox.addActionListener(this);
			}
		return lightningCheckBox;
		}

	/**
	 * @param args
	 */
	public static void main(String[] args)
		{
		WcTester t=new WcTester();
		t.setVisible(true);
		}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
		{
		this.setSize(302, 604);
		this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		this.setContentPane(getJContentPane());
		this.setTitle("JFrame");
		debugBaseTimeMillis=getWeatherClock().debugTimeMillis;
		updateTime();
		updateWindDirection();
		updateTemperature();
		updateMoon();
		updateSky(0);
		}

	/**
   * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane()
		{
		if (jContentPane == null)
			{
			jContentPane=new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getAdjPanel(), java.awt.BorderLayout.SOUTH);
			jContentPane.add(getWeatherClock(), java.awt.BorderLayout.CENTER);
			jContentPane.add(getSkyPanel(), java.awt.BorderLayout.WEST);
			}
		return jContentPane;
		}

	private void updateMoon()
		{
		getWeatherClock().moonAge=getPhaseAdjuster().getValue();
		tellWeather();
		}

	private void updateTemperature()
		{
		getWeatherClock().getWeather().setTemperature(getTemperatureAdjuster().getValue());
		tellWeather();
		}

	private void updateSky(int sky)
		{
		System.out.println("Setting sky to "+sky);
		getWeatherClock().getWeather().setSky(sky);
		tellWeather();
		}

	private void updateIntensity(String intensity)
		{
		getWeatherClock().getWeather().setIntensity(intensity);
		updatePrecipitation();
		}

	private void updateWindSpeed(int windspeed)
		{
		getWeatherClock().getWeather().setWindSpeed(windspeed);
		tellWeather();
		if (getGustAdjuster().getValue()<windspeed && getGustAdjuster().getValue()>0)
			{
			getGustAdjuster().setValue(windspeed);
			updateWindGusts();
			}
		
		}
	private void updateWindGusts()
		{
		getWeatherClock().getWeather().setWindGusts(getGustAdjuster().getValue());
		tellWeather();
		}
	
	private void updateWindDirection()
	{
	getWeatherClock().getWeather().setWindDirection(getWindDirectionAdjuster().getValue());
	tellWeather();
	}

	private void updatePrecipitation()
		{
		List precip=new Vector();
		String obscuration="";
		String intensity="";
		String description="";
		((DebugWeather)getWeatherClock().getWeather()).setRain(precip, obscuration, description, intensity);
		if (getRainCheckBox().isSelected())
			precip.add("rain");
		if (getSnowCheckBox().isSelected())
			precip.add("snow");
		if (getFogCheckBox().isSelected())
			obscuration+="fog ";
		if (getLightningCheckBox().isSelected())
			description=(String)MetarWeather.getDescriptorCodes().get("TS");
		if (getLtRainRadioButton().isSelected())
			intensity="light";
		else if (getHeavyRainRadioButton().isSelected())
			intensity="heavy";
		((DebugWeather)getWeatherClock().getWeather()).setRain(precip, obscuration, description, intensity);
		tellWeather();
		}

	private void tellWeather()
		{
		getWeatherClock().getWeather().setErrorMessage(null);
		getWeatherClock().inform(getWeatherClock().getWeather());
		getWeatherClock().repaint();
		}

	/**
	 * 
	 */
	private void updateTime()
		{
		getWeatherClock().debugTimeMillis=debugBaseTimeMillis+getTime().getValue()*863993l;
		getWeatherClock().repaint();
		}

	public void actionPerformed(ActionEvent e)
		{
		if (e.getSource()==getHeavyRainRadioButton())
			updateIntensity("heavy");
		else if (e.getSource()==getLtRainRadioButton())
			updateIntensity("light");
		else if (e.getSource()==getRainjRadioButton())
			updateIntensity("");
		else if (e.getSource()==getMoonCheckBox())
			updateImage();
		else if (e.getSource()==getImageTextField())
			updateImage();
		else if (e.getSource()==getRainCheckBox())
			updatePrecipitation();
		else if (e.getSource()==getSnowCheckBox())
			updatePrecipitation();
		else if (e.getSource()==getFogCheckBox())
			updatePrecipitation();
		else if (e.getSource()==getLightningCheckBox())
			updatePrecipitation();
		}

	private void updateImage()
		{
		if (getImageTextField().getText().trim().length()==0
			|| getMoonCheckBox().isSelected())
			{
			System.out.println("Setting face to moon.");
			getWeatherClock().setFaceImage(false);
			}
		else
			{
			System.out.println("Setting face to image "+getImageTextField().getText());
			getWeatherClock().setImageURL(getImageTextField().getText());
//			getWeatherClock().setFace(Toolkit.getDefaultToolkit().createImage(getImageTextField().getText()));
			getWeatherClock().setFaceImage(true);
			}
		tellWeather();
		}

	private void showFilePicker()
		{
		FileDialog picker=new FileDialog(this,"Choose Face Image",FileDialog.LOAD);
		picker.show();
		String img=picker.getDirectory()+picker.getFile();
		getImageTextField().setText(img);
		updateImage();
		}

	public void stateChanged(ChangeEvent e)
		{
		if (e.getSource()==getTime().getAdjustment())
			updateTime();
		else if (e.getSource()==getTemperatureAdjuster().getAdjustment())
			updateTemperature();
		else if (e.getSource()==getCloudAdjuster().getAdjustment())
			updateSky(getCloudAdjuster().getValue());
		else if (e.getSource()==getPhaseAdjuster().getAdjustment())
			updateMoon();
		else if (e.getSource()==getWindAdjuster().getAdjustment())
			updateWindSpeed(getWindAdjuster().getValue());
		else if (e.getSource()==getGustAdjuster().getAdjustment())
			updateWindGusts();
		else if (e.getSource()==getWindDirectionAdjuster().getAdjustment())
			updateWindDirection();
		}

	}  //  @jve:decl-index=0:visual-constraint="2,3"
