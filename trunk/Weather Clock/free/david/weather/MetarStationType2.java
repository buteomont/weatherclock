package free.david.weather;

public class MetarStationType2 extends MetarStation
	{
	private String stationData;
	private MetarStationType2()
		{
		}

	public MetarStationType2(String stationData)
		{
		super(stationData);
		}

	protected void parse(String stationData)
		{
		this.stationData=stationData;
		setStateCode(pull(0, 2));
		setPlaceName(pull(3,20));
		setIcaoIndicator(pull(20,26));
		setBlockNumber(pull(32,34));
		setStationNumber(pull(34,37));
		setStationLatitude(pull(38,45).replace(' ', '-'));
		setStationLongitude(pull(47,54).replace(' ', '-'));
		setStationElevation(pull(54,61));
		setCountryCode(stationData.substring(81).trim());
		}
	private String pull(int start, int fin)
		{
		return stationData.substring(start,fin).trim();
		}
	}
