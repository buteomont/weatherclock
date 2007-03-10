package free.david.weather;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import free.david.Collectable;
import free.david.wc.WeatherListener;

public abstract class Weather extends Thread implements Collectable
	{
	private List	listeners		=new Vector();
	protected String	city	="Nashville International Airport";	// the city being watched
	protected String	stateName="TN";// the state in which the city is
	protected String	country="US";// the country in which the state or city is
	protected int clouds=0; //0-5 = clear - overcast
	protected String intensity="";
	private int		windDirection;
	private String	barometerDirection="";
	private String	remarks="";
	private int		temperature;
	private int		dewpoint;
	private int		windSpeed;
	private float	barometer;
	private int		windChill;
	private int		windGusts;
	private int		visibility;
	private String  visibilityUnits="miles";
	private int		updatePeriod	=10;										// minutes between updates
	private String	errorMessage	=null;
	private boolean	windChillAvailable=false;
	private SortedSet cityList=new TreeSet();
	private SortedMap countryList=new TreeMap();//key=country code, val=country name
	private SortedMap stateList=new TreeMap(); //key=stateName, val=URL
	private Map defaultCities=new HashMap(); //key=stateName, val=city
	protected Properties specifics;
	public Throwable lastException=null;
	private boolean updateInProgress=false;
	private String stationTimestamp="";
	
	public Weather()
		{
		super();
		try
			{
			init();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			System.exit(-1);
			}
		}

	public Weather(Properties specifics)
		{
		super();
		this.specifics=specifics;
		try
			{
			init();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			System.exit(-1);
			}
		}

	protected abstract String translateSky();
	protected abstract String translateBarometer(String code);
	protected abstract void update(String rawData);
	protected abstract SortedSet loadCityList(String data);
	protected abstract void initStage2();
	public abstract void setStateName(String stateName) throws IOException;
	public abstract void setCountry(String country) throws IOException;
	public abstract String getStationURL();
	public abstract String getCloudCover();
	public abstract String getPrecipitation();
	public abstract String getIntensity();
	public abstract void setIntensity(String intensity);
    public abstract float assessSky();
    public abstract int assessRain(); //return number of drops for clock face
    public abstract int assessFog(); //return number of fog banks for clock face
    public abstract int assessSnow(); //return number of snowflakes for clock face
    public abstract int assessLightning(); //return number of lightning bolts for clock face
    /*
     * Returns a Map of country code/station list
     */
    public abstract Map getCountries();
    /*
     * Returns a Map of state code/station list
     */
    public abstract Map getStates();
    /*
     * Returns a Map of station city/ICAO codes
     */
    public abstract Map getCities();

	public abstract String getDefaultININame();

	private void init() throws Exception
	{
	if (getSpecifics()==null)// put in some defaults
		setSpecifics(new Properties()); // use our own defaults

//	for (Enumeration states=getSpecifics().propertyNames(); states.hasMoreElements();)
//		{
//		String key=(String)states.nextElement();
//		if (!key.startsWith("statelist"))
//			continue;
//		else
//			{
//			StringTokenizer tok=new StringTokenizer(getSpecifics().getProperty(key), ",");
//			String newState=null;
//			if (tok.hasMoreTokens()) newState=tok.nextToken();
//			if (tok.hasMoreTokens()) getStateList().put(newState, tok.nextToken());
//			if (tok.hasMoreTokens()) getDefaultCities().put(newState, tok.nextToken());
//			}
//		}
	city=getSpecifics().getProperty("city", "Nashville International Airport");
	stateName=getSpecifics().getProperty("state", "TN");
	country=getSpecifics().getProperty("country", "US");
	setUpdatePeriod(Integer.parseInt(getSpecifics().getProperty("updatePeriod", "10")));
	initStage2();
	setPriority(NORM_PRIORITY-2);
	}

	public static int cardinalToDegrees(String compassPoint)
		{
		compassPoint=compassPoint.toLowerCase();
		if (compassPoint.equals("e")) return 0;
		if (compassPoint.equals("se")) return (int)Math.toDegrees(2*Math.PI*(1/8d));
		if (compassPoint.equals("s")) return (int)Math.toDegrees(2*Math.PI*(2/8d));
		if (compassPoint.equals("sw")) return (int)Math.toDegrees(2*Math.PI*(3/8d));
		if (compassPoint.equals("w")) return (int)Math.toDegrees(2*Math.PI*(4/8d));
		if (compassPoint.equals("nw")) return (int)Math.toDegrees(2*Math.PI*(5/8d));
		if (compassPoint.equals("n")) return (int)Math.toDegrees(2*Math.PI*(6/8d));
		if (compassPoint.equals("ne")) return (int)Math.toDegrees(2*Math.PI*(7/8d));
		return 0;
		}

	/**
	 * @return String representing the current time and date, followed
	 * by a tab character.
	 */
	public static String timeStamp()
		{
		return Calendar.getInstance().getTime().toString()+"\t";
		}

	public void addWeatherListener(WeatherListener listener)
		{
		listeners.add(listener);
		}

	public void run()
		{
		do
			{
			try
				{
				refresh();
				}
			catch (IOException e)
				{
				setErrorMessage("IOException while fetching url\n"+getStationURL()+".\nCheck log for details.");
				lastException=e;
//				if (lastValidStation.length()>0)
//					setCity(lastValidStation);
				tellEveryone();
				System.out.println(timeStamp()+"IOException while fetching url "+getStationURL()+". Error is "+e.getMessage());
				}
			catch (Throwable t)
				{
				setErrorMessage("Unknown exception while fetching url\n"+getStationURL()+".\nCheck log for details.");
				lastException=t;
//				if (lastValidStation.length()>0)
//					setCity(lastValidStation);
				tellEveryone();
				System.out.println(timeStamp()+"Unknown exception while fetching url "+getStationURL()+". Error is "+t.getMessage());
				t.printStackTrace();
				}
			finally
				{
				try
					{
					sleep(getUpdatePeriod() * 60000);
					}
				catch (InterruptedException e)
					{
					//No problem, probably just changed the update period
					}
				}
			} while (true);
		}

	public synchronized void refresh() throws IOException
		{
		reload();
		setErrorMessage(null); //no errors occurred
		tellEveryone();
		}

	/**
	 * Inform all of our listeners about a change.
	 */
	private void tellEveryone()
		{
		WeatherListener[] ears=(WeatherListener[])listeners.toArray(new WeatherListener[0]);
		for (int i=0;i<ears.length;i++)
			{
			ears[i].inform(this);
			}
		}

	public void reload() throws IOException
		{
		setUpdateInProgress(true);
		tellEveryone();
		if (getStationURL()!=null)
			{
			String data=getPage(getStationURL());
			//do a sanity check on the data.  Discard it if it fails.
			if (data.length()>50 //approx min length
						&& data.indexOf("/")>0) //should have at least one of these
				update(data); //update registers
			else
				{
				setUpdateInProgress(false);
				tellEveryone();
				System.out.println(timeStamp()+"Not updated - data failed sanity check: "+data.replace('\n', '^'));
				}
			}
		setUpdateInProgress(false);
		}

	/**
	 * Returns the text of a web page, or throw an exception.
	 * 
	 * @param java.net.URL
	 *            url - A URL object pointing to the desired page.
	 * @return java.lang.String
	 * @throws IOException
	 */
	public String getPage(String url) throws IOException
		{
		System.out.println(timeStamp()+"Fetching page "+url);
		StringBuffer html=new StringBuffer();
		String cr="\n";
		URL pageURL=new URL(url);
		String line=null;

		java.io.BufferedReader dis=new java.io.BufferedReader(new java.io.InputStreamReader(pageURL.openConnection()
					.getInputStream()));
		while ((line=dis.readLine()) != null)
			{
			html.append(line);
			html.append(cr);
			}
		return html.toString();
		}

	/**
	 * Returns a String within another String that is a number
	 * 
	 * @param original
	 *            String The orignal string to be parsed.
	 * @return int
	 */
	public final String extractNumber(String original)
		{
		String number="";
		if (original != null)
			{
			original=original.trim();
			String possibleNumber="";
			while (original.length() > 0)
				{
				original=original.substring(possibleNumber.length());
				if (original.length() == 0) // done?
					{
					if (isADecimalNumber(possibleNumber)) // ends with a number?
						number=possibleNumber;
					break;
					}
				possibleNumber="";
				for (int i=0; i < original.length(); i++)
					{
					possibleNumber+=new String(new char[] {original.charAt(i)});
					if (!isADecimalNumber(possibleNumber))
						if (possibleNumber.length() > 1) // we have a number plus a non-number
							{
							number=possibleNumber.substring(0, possibleNumber.length() - 1);
							break;
							}
						else
							break;
					else
						continue;
					}
				}
			}
		return number;
		}

	/**
	 * Tests a String to see if it can be converted to a floating point number.
	 * 
	 * @param possibleNumber
	 *            String representation of a number, or not.
	 * @return boolean
	 */
	public final boolean isADecimalNumber(String possibleDecimalNumber)
		{
		boolean isNumeric=false;
		if (possibleDecimalNumber!=null)
			{
			try
				{
				if (possibleDecimalNumber.endsWith("F")|| possibleDecimalNumber.endsWith("f"))
					throw new NumberFormatException();
				Float.valueOf(possibleDecimalNumber); // Test for numeric
				isNumeric=true;
				}
			catch (NumberFormatException e)
				{
				// Must not be a number
				}
			}
		return isNumeric;
		}

	public float getBarometer()
		{
		return barometer;
		}

	public void setBarometer(float barometer)
		{
		this.barometer=barometer;
		}

	public int getDewpoint()
		{
		return dewpoint;
		}

	public void setDewpoint(int dewpoint)
		{
		this.dewpoint=dewpoint;
		}

	public int getHumidity()
		{
		return relativeHumidity(getTemperature(), getDewpoint(), true);
		}

	public String getSky()
		{
		return translateSky();
		}

	/**
	 * Density of clouds, 0=clear, 5=overcast
	 * @param sky
	 */
	public void setSky(int sky)
		{
		this.clouds=sky;
		}

	public int getTemperature()
		{
		return temperature;
		}

	public void setTemperature(int temperature)
		{
		this.temperature=temperature;
		}

	public int getWindChill()
		{
		return windChill;
		}

	public void setWindChill(int windChill)
		{
		this.windChill=windChill;
		}

	public String getWindDirectionText()
		{
		return translateWind(getWindDirection());
		}

	public int getWindSpeed()
		{
		return windSpeed;
		}

	public void setWindSpeed(int windSpeed)
		{
		this.windSpeed=windSpeed;
		}

	public String getCity()
		{
		return city;
		}

	public void setCity(String city)
		{
		if (!this.city.equals(city))
			{
			this.city=city;
			setBarometerDirection("");
			interrupt();
			}
		}

	public int getUpdatePeriod()
		{
		return updatePeriod;
		}

	public void setUpdatePeriod(int updatePeriod)
		{
		this.updatePeriod=updatePeriod;
		if (specifics!=null)
			specifics.put("updatePeriod", updatePeriod+"");
		}

	public void setErrorMessage(String message)
		{
		this.errorMessage=message;
		}

	public String getBarometerDirection()
		{
		return translateBarometer(barometerDirection);
		}

	public String getRawBarometerDirection()
		{
		return barometerDirection;
		}

	public void setBarometerDirection(String barometerDirection)
		{
		this.barometerDirection=barometerDirection;
		}

	public List getListeners()
		{
		return listeners;
		}

	public void setListeners(List listeners)
		{
		this.listeners=listeners;
		}

	public String getRemarks()
		{
		return remarks;
		}

	public void setRemarks(String remarks)
		{
		this.remarks=remarks;
		}

	public boolean isWindChillAvailable()
		{
		return windChillAvailable;
		}

	public void setWindChillAvailable(boolean wciAvail)
		{
		this.windChillAvailable=wciAvail;
		}

	public int getWindGusts()
		{
		return windGusts;
		}

	public void setWindGusts(int windGusts)
		{
		this.windGusts=windGusts;
		}

	public SortedSet getCityList()
		{
		return cityList;
		}

	public void setCityList(SortedSet cityList)
		{
		this.cityList=cityList;
		}

	public int getVisibility()
		{
		return visibility;
		}

	public void setVisibility(int visibility)
		{
		this.visibility=visibility;
		}

	public boolean isVisibilityAvailable()
		{
		return getVisibility()>=0;
		}

	public String getStateName()
		{
		return stateName;
		}

	public SortedMap getStateList()
		{
		return stateList;
		}

	public void setStateList(SortedMap stateList)
		{
		this.stateList=stateList;
		}

	public Map getDefaultCities()
		{
		return defaultCities;
		}

	public Properties getSpecifics()
		{
		return specifics;
		}

	public void setSpecifics(Properties specifics)
		{
		this.specifics=specifics;
		}

	public int getWindDirection()
		{
		return windDirection;
		}

	public void setWindDirection(int windDirection)
		{
		this.windDirection=windDirection;
		}

	protected String translateWind(int direction)
		{
		if (direction<0) return "variable";
		double radir=Math.toRadians(direction);
		radir+=2*Math.PI*(1/16d); //offset around zero
		if (radir>=2*Math.PI) radir-=2*Math.PI;
		if (radir<2*Math.PI*(1/8d)) return "east";
		if (radir<2*Math.PI*(2/8d)) return "southeast";
		if (radir<2*Math.PI*(3/8d)) return "south";
		if (radir<2*Math.PI*(4/8d)) return "southwest";
		if (radir<2*Math.PI*(5/8d)) return "west";
		if (radir<2*Math.PI*(6/8d)) return "northwest";
		if (radir<2*Math.PI*(7/8d)) return "north";
		if (radir<2*Math.PI) return "northeast";
		return "N/A";
		}

	public SortedMap getCountryList()
		{
		return countryList;
		}

	public void setCountryList(SortedMap countryList)
		{
		this.countryList=countryList;
		}

	public String getCountry()
		{
		return country;
		}

	public String getErrorMessage()
		{
		return errorMessage;
		}

	public String getVisibilityUnits()
		{
		return visibilityUnits;
		}

	public void setVisibilityUnits(String visibilityUnits)
		{
		this.visibilityUnits=visibilityUnits;
		}

	public boolean isUpdateInProgress()
		{
		return updateInProgress;
		}

	public void setUpdateInProgress(boolean updateInProgress)
		{
		this.updateInProgress=updateInProgress;
		}

	public String getStationTimestamp()
		{
		return stationTimestamp;
		}

	public void setStationTimestamp(String stationTimestamp)
		{
		this.stationTimestamp=stationTimestamp;
		}

	/**
	 * Calculate the relative humidity based on temperature and dewpoint.
	 * @param temperature
	 * @param dewpoint
	 * @param fahrenheit
	 * @return
	 */
	public static int relativeHumidity(int temperature, int dewpoint, boolean fahrenheit)
		{
		double temp=(double)temperature;
		double dew=(double)dewpoint;
		//convert to Celsius
		if (fahrenheit)
			{
			temp=(5d/9d)*(temp-32);
			dew=(5d/9d)*(dew-32);
			}
		//convert to Kelvin
		temp+=273.15;
		dew+=273.15;
		
		int rh =(int)(100 * vaporPressure(dew)/vaporPressure(temp));
		return rh;
		}

	private static double vaporPressure(double temperature)
		{
		return Math.exp(21.564d - 5420d/temperature);
		}

	public void collectSettings(Properties settings)
		{
		try{settings.put("country",getCountry());}catch (Exception e){e.printStackTrace();}
		try{settings.put("state",getStateName());}catch (Exception e){e.printStackTrace();}
		try{settings.put("city",getCity());}catch (Exception e){e.printStackTrace();}
		}	
	}
