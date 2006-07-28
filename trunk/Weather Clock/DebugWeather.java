import java.io.IOException;
import java.util.List;

import free.david.weather.MetarWeather;

public class DebugWeather extends MetarWeather
	{

	public DebugWeather() throws IOException
		{
		super();
		}

	public DebugWeather(String stationURL) throws IOException
		{
		super();
		}

	public void run()
		{
		}

	public void setRain(List precip, String obscuration, String description, String intensity)
		{
		setPrecipList(precip);
		setIntensity(intensity);
		setDescription(description);
		setObscuration(obscuration);
		}

	}
