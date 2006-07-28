package free.david.weather;

import java.util.StringTokenizer;

public class MetarStationType1 extends MetarStation
	{
	private MetarStationType1()
		{
		}

	public MetarStationType1(String stationData)
		{
		super(stationData);
		}

	protected void parse(String stationData)
		{
		// return tokens from tokenizer so that empty values can be detected
		StringTokenizer tok=new StringTokenizer(stationData, ";", true);
		setBlockNumber(nextToken(tok));
		setStationNumber(nextToken(tok));
		setIcaoIndicator(nextToken(tok));
		setPlaceName(nextToken(tok));
		setStateCode(nextToken(tok));
		setCountryCode(nextToken(tok));
		setWmoRegion(nextToken(tok));
		setStationLatitude(nextToken(tok));
		setStationLongitude(nextToken(tok));
		setUpperAirLatitude(nextToken(tok));
		setUpperAirLongitude(nextToken(tok));
		setStationElevation(nextToken(tok));
		setUpperAirElevation(nextToken(tok));
		setRbsnIndicator(nextToken(tok));
		}

	private String nextToken(StringTokenizer tok)
		{
		String val=tok.hasMoreTokens()?tok.nextToken():null;
		if (val==null||val.equals(";"))
			val=null;
		else if (tok.hasMoreTokens()) tok.nextToken(); // discard separator
		return val;
		}
	}
