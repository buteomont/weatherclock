import java.awt.LayoutManager;
import java.io.IOException;
import java.util.Calendar;

import free.david.wc.Moon;
import free.david.wc.WeatherClock;
import free.david.weather.Weather;

public class DebugWeatherClock extends WeatherClock
	{

	protected float moonAge=0;

	public float calculateMoonAge()
		{
		return moonAge%Moon.MOON_MONTH;
		}

	public DebugWeatherClock() throws IOException
		{
		super();
		setWeather(new DebugWeather());
		}

	public DebugWeatherClock(LayoutManager layout)
		{
		super(layout);
		// TODO Auto-generated constructor stub
		}

	public Weather getWeather()
		{
		return super.getWeather();
		}

	public Calendar getTime()
		{
		Calendar time=Calendar.getInstance();
		time.setTimeInMillis(debugTimeMillis);
		return time;
		}

	protected void begin()
		{
		getWeather().stop();
		getWeather().setErrorMessage(null);
		// don't begin update thread in debug mode
		}

	}
