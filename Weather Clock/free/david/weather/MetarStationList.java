package free.david.weather;

import java.io.*;
import java.util.*;

public class MetarStationList
	{
	public static final String	STATION_LIST_FILENAME	="METARstations.txt";
	private Map					countries				=new Hashtable();//key:country, value:List of cities
	private Map					states					=new Hashtable();//key:US state, value:List of cities
	private Map					cities					=new Hashtable();//key:city, value:ICAO
	private Map					stations				=new Hashtable();//key:ICAO, value:MetarStation

	private Properties countryCodes;

	public MetarStationList()
		{
		super();
		try
			{
			loadCountryCodes();
			init();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			System.exit(-1);
			}
		}

	/**
	 * Initialize from the combined lists of station information. 
	 * 
	 * @throws IOException 
	 */
	private void init() throws IOException
		{
		String filedat=readFile(STATION_LIST_FILENAME);
		for (StringTokenizer lines=new StringTokenizer(filedat,"\r\n");lines.hasMoreTokens();)
			{
			String st=lines.nextToken();
			if (st.startsWith("*")) continue;
			MetarStation station=new MetarStation(st);
			add(station);
			}
		}
	
	
//	/**
//	 * Initialize from the first list of station information, obtained
//	 * from http://weather.noaa.gov/data/nsd_bbsss.txt
//	 * @throws IOException 
//	 */
//	private void init1() throws IOException
//		{
//		String filedat=readFile(STATION_LIST_FILENAME1);
//		System.out.println(Weather.timeStamp()+"Read "+filedat.length()+" bytes from "+STATION_LIST_FILENAME1);
//		for (StringTokenizer lines=new StringTokenizer(filedat,"\r\n");lines.hasMoreTokens();)
//			{
//			String st=lines.nextToken();
//			MetarStation station=new MetarStationType1(st);
//			add(station);
//			}
//
//		}
//
//	/**
//	 * Initialize from the second list of station information, obtained
//	 * from http://www.rap.ucar.edu/weather/surface/stations.txt
//	 * @throws IOException 
//	 */
//	private void init2() throws IOException
//		{
//		String filedat=readFile(STATION_LIST_FILENAME2);
//		System.out.println(Weather.timeStamp()+"Read "+filedat.length()+" bytes from "+STATION_LIST_FILENAME2);
//		for (StringTokenizer lines=new StringTokenizer(filedat,"\r\n");lines.hasMoreTokens();)
//			{
//			String line=lines.nextToken();
//			if (line.length()<83) continue;
//			if (line.startsWith("!")) continue;
//			if (line.startsWith("CD  STATION")) continue;
//			if (!line.substring(62, 63).equals("X")) continue;
//			MetarStation station=new MetarStationType2(line);
//			add(station);
//			}
//
//		}

	private void add(MetarStation station)
		{
		if (station.getIcaoIndicator()==null)
			return; //no reason to keep one we can't query
		if (station.getIcaoIndicator().equals("----"))
			return; //no reason to keep one we can't query
		if (station.getIcaoIndicator().trim().length() < 4)
			return; //no reason to keep one we can't query
		String country=station.getCountryCode().toUpperCase();

		//convert possible country name to a country code
		if (country.length()>2)
			{ //must be a name
			country=getCountryCodes().getProperty(country,"unknown");
			if (country.length()==2) station.setCountryCode(country);//found it
			}
		if (stations.get(station.getIcaoIndicator())==null)
			{ //don't save dupes
			stations.put(station.getIcaoIndicator(), station);
			if (!countries.containsKey(country))
				countries.put(country, new Vector());
			((List)countries.get(country)).add(station.getPlaceName());
			
			cities.put(station.getPlaceName(), station.getIcaoIndicator());
			if (country.equals("US"))
				{
				if (!states.containsKey(station.getStateCode()))
					states.put(station.getStateCode(), new Vector());
				((List)states.get(station.getStateCode())).add(station.getPlaceName());
				}
			}
		}

	/**
	 * Searches the classpath for a file, reads and returns it as a byte array.
	 *   100% Java compliant (no system calls)
	 *
	 * @param String sourceFileName
	 * @return byte[]
	 */
	public String readFile(String sourceFileName)
	    throws java.io.IOException
	    {
	    //if the file name is fully specified, use the absolute routine instead.
	 	if (sourceFileName!=null && 
	 			(sourceFileName.indexOf(System.getProperty("file.separator"))==0
	 			|| sourceFileName.indexOf(":")==1))
	 		return readFileAbsolute(sourceFileName);
	 	else //use the classpath
	 		{	
		    java.io.InputStream in = null;//090104dep
		    StringBuffer buf = new StringBuffer();
		    try
		        {
				in=getClass().getClassLoader().getResourceAsStream(sourceFileName); //090104dep
				int c=0;
				while((c=in.read())> -1)
					buf.append((char)c);
		        }
		    finally
		        {
		        in.close();
		        }
		    return buf.toString();
	 		}
	    }
	/** 092104dep
	 * Reads a file specified by an absolute pathname and returns it as a byte array.
	 *   100% Java compliant (no system calls)
	 *
	 * @param String fullySpecifiedFileName
	 * @return byte[]
	 */
	public static String readFileAbsolute(String fullySpecifiedFileName)
	    throws java.io.IOException
	    {
	    java.io.FileInputStream in = null;
	    byte[] buf = null;

	    try
	        {
	        in = new java.io.FileInputStream(fullySpecifiedFileName);
	        buf = new byte[in.available()];
	        in.read(buf);
	        }
	    finally
	        {
	        in.close();
	        }
	    return new String(buf);
	    }

	private void loadCountryCodes()
		{
		//get default properties
		setCountryCodes(new Properties());
		ClassLoader cl=this.getClass().getClassLoader();
		try
			{
			InputStream in=cl.getResourceAsStream("countryCodes.ini");
			getCountryCodes().load(in);
			in.close();
			}
		catch (IOException e)
			{
			e.printStackTrace();
			System.out.println(Weather.timeStamp()+"Unable to read initialization file countryCodes.ini.");
			System.exit(-1);
			}
		}

	public Properties getCountryCodes()
		{
		return countryCodes;
		}

	public void setCountryCodes(Properties countryCodes)
		{
		this.countryCodes=countryCodes;
		}

	public Map getCities()
		{
		return cities;
		}

	public Map getCountries()
		{
		return countries;
		}

	public Map getStates()
		{
		return states;
		}

	public Map getStations()
		{
		return stations;
		}

	public void writeStationFile(String filename) throws IOException
		{
		OutputStreamWriter out=new OutputStreamWriter(new FileOutputStream(filename));
		try
			{
			out.write("* File Format:\n* ICAO;Country Code;State Code;Station Name;Latitude;Longitude;Elevation;TimeZone Offset;\n\n");
			SortedSet ord=new TreeSet(getStations().keySet());
			for (Iterator stats=ord.iterator();stats.hasNext();)
				{
				((MetarStation)getStations().get(stats.next())).writeStation(out);
				out.write("\n");
				}
			}
		catch (IOException e)
			{
			e.printStackTrace();
			throw e;
			}
		finally
			{
			out.close();
			}
		}
	}
