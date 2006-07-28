package free.david.weather;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;

public class MetarStation
	{
	/**
	 * Block Number - 2 digits representing the WMO-assigned block.
	 */
	private String	blockNumber;

	/**
	 * Station Number - 3 digits representing the WMO-assigned station.
	 */
	private String	stationNumber;

	/**
	 * ICAO Location Indicator - 4 alphanumeric characters, not all stations in this file have an assigned location indicator. The
	 * value "----" is used for stations that do not have an assigned location indicator.
	 */
	private String	icaoIndicator;

	/**
	 * Place Name - Common name of station location.
	 */
	private String	placeName;

	/**
	 * State Code - 2 character abbreviation (included for stations located in the United States only).
	 */
	private String	stateCode;

	/**
	 * Country Name - Country name is ISO short English form.
	 */
	private String	countryCode;

	/**
	 * WMO Region - digits 1 through 6 representing the corresponding WMO region, 7 stands for the WMO Antarctic region.
	 */
	private String	wmoRegion;

	/**
	 * Station Latitude - DD-MM-SSH where DD is degrees, MM is minutes, SS is seconds and H is N for northern hemisphere or S for
	 * southern hemisphere. The seconds value is omitted for those stations where the seconds value is unknown.
	 */
	private String	stationLatitude;

	/**
	 * Station Longitude DDD-MM-SSH where DDD is degrees, MM is minutes, SS is seconds and H is E for eastern hemisphere or W for
	 * western hemisphere. The seconds value is omitted for those stations where the seconds value is unknown.
	 */
	private String	stationLongitude;

	/**
	 * Upper Air Latitude - DD-MM-SSH where DD is degrees, MM is minutes, SS is seconds and H is N for northern hemisphere or S for
	 * southern hemisphere. The seconds value is omitted for those stations where the seconds value is unknown.
	 */
	private String	upperAirLatitude;

	/**
	 * Upper Air Longitude - DDD-MM-SSH where DDD is degrees, MM is minutes, SS is seconds and H is E for eastern hemisphere or W
	 * for western hemisphere. The seconds value is omitted for those stations where the seconds value is unknown.
	 */
	private String	upperAirLongitude;

	/**
	 * Station Elevation (Ha) - The station elevation in meters. Value is omitted if unknown.
	 */
	private String	stationElevation;

	/**
	 * Upper Air Elevation (Hp) - The upper air elevation in meters. Value is omitted if unknown.
	 */
	private String	upperAirElevation;

	/**
	 * RBSN indicator - P if station is defined by the WMO as belonging to the Regional Basic Synoptic Network, omitted otherwise
	 */
	private String	rbsnIndicator;
	
	/**
	 * Time Zone Offset - The number of hours to add to or subtract from GMT for local time at this station. 
	 */
	private String	timeZoneOffset;
	
	public MetarStation()
		{
		super();
		}

	public MetarStation(String stationData)
		{
		super();
		parse(stationData);
		}

	/**
	 * Parses data from the following format;
	 * ICAO;Country Code;State Code;Station Name;Latitude;Longitude;Elevation;TimeZone Offset;
	 * @param stationData
	 */
	protected void parse(String stationData)
		{
		// return tokens from tokenizer so that empty values can be detected
		StringTokenizer tok=new StringTokenizer(stationData, ";", true);
		setIcaoIndicator(nextToken(tok));
		setCountryCode(nextToken(tok));
		setStateCode(nextToken(tok));
		setPlaceName(nextToken(tok));
		setStationLatitude(nextToken(tok));
		setStationLongitude(nextToken(tok));
		setStationElevation(nextToken(tok));
		setTimeZoneOffset(nextToken(tok));
		}

	private String nextToken(StringTokenizer tok)
		{
		String val=tok.hasMoreTokens()?tok.nextToken():null;
		if (val==null||val.equals(";"))
			val=null;
		else if (tok.hasMoreTokens()) tok.nextToken(); // discard separator
		return val;
		}
	
	public String getBlockNumber()
		{
		return blockNumber;
		}

	public void setBlockNumber(String blockNumber)
		{
		this.blockNumber=blockNumber;
		}

	public String getCountryCode()
		{
		return countryCode;
		}

	public void setCountryCode(String countryName)
		{
		this.countryCode=countryName;
		}

	public String getIcaoIndicator()
		{
		return icaoIndicator;
		}

	public void setIcaoIndicator(String icaoIndicator)
		{
		this.icaoIndicator=icaoIndicator;
		}

	public String getPlaceName()
		{
		return placeName;
		}

	public void setPlaceName(String placeName)
		{
		this.placeName=placeName;
		}

	public String getRbsnIndicator()
		{
		return rbsnIndicator;
		}

	public void setRbsnIndicator(String rsbnIndicator)
		{
		this.rbsnIndicator=rsbnIndicator;
		}

	public String getStateCode()
		{
		return stateCode;
		}

	public void setStateCode(String stateCode)
		{
		this.stateCode=stateCode;
		}

	public String getStationElevation()
		{
		return stationElevation;
		}

	public void setStationElevation(String stationElevation)
		{
		this.stationElevation=stationElevation;
		}

	public String getStationLatitude()
		{
		return stationLatitude;
		}

	public void setStationLatitude(String stationLatitude)
		{
		this.stationLatitude=stationLatitude;
		}

	public String getStationLongitude()
		{
		return stationLongitude;
		}

	public void setStationLongitude(String stationLongitude)
		{
		this.stationLongitude=stationLongitude;
		}

	public String getStationNumber()
		{
		return stationNumber;
		}

	public void setStationNumber(String stationNumber)
		{
		this.stationNumber=stationNumber;
		}

	public String getUpperAirElevation()
		{
		return upperAirElevation;
		}

	public void setUpperAirElevation(String upperAirElevation)
		{
		this.upperAirElevation=upperAirElevation;
		}

	public String getUpperAirLatitude()
		{
		return upperAirLatitude;
		}

	public void setUpperAirLatitude(String upperAirLatitude)
		{
		this.upperAirLatitude=upperAirLatitude;
		}

	public String getUpperAirLongitude()
		{
		return upperAirLongitude;
		}

	public void setUpperAirLongitude(String upperAirLongitude)
		{
		this.upperAirLongitude=upperAirLongitude;
		}

	public String getWmoRegion()
		{
		return wmoRegion;
		}

	public void setWmoRegion(String wmoRegion)
		{
		this.wmoRegion=wmoRegion;
		}

	public String toString()
		{
		return getPlaceName()+", "+(getStateCode()==null?"":getStateCode()+", ")+getCountryCode()+" ("+getIcaoIndicator()+")";
		}

	private String fix(String possibleNull)
		{
		if (possibleNull==null) return "";
		else return possibleNull.trim();
		}
	
	/**
	 * Writes this station to an output stream in the following
	 * format, using a semicolon to separate the fields:
	 * ICAO;Country Code;State Code;Station Name;Latitude;Longitude;Elevation;TimeZone Offset;
	 * It returns the number of records written, i.e., 1 (successful)
	 * or 0 (unsuccessful).
	 * @param out
	 * @return
	 * @throws IOException 
	 */
	public int writeStation(OutputStreamWriter out) throws IOException
		{
		int written=0;
		StringBuffer buf=new StringBuffer();
		buf.append(fix(getIcaoIndicator())).append(";");
		buf.append(fix(getCountryCode())).append(";");
		buf.append(fix(getStateCode())).append(";");
		buf.append(fix(getPlaceName())).append(";");
		buf.append(fix(getStationLatitude())).append(";");
		buf.append(fix(getStationLongitude())).append(";");
		buf.append(fix(getStationElevation())).append(";");
		
		try
			{
			out.write(buf.toString());
			written=1;
			}
		catch (IOException up)
			{
			up.printStackTrace();
			throw up;
			}
		return written;
		}

	public String getTimeZoneOffset()
		{
		return timeZoneOffset;
		}

	public void setTimeZoneOffset(String timeZoneOffset)
		{
		this.timeZoneOffset=timeZoneOffset;
		}
	}
