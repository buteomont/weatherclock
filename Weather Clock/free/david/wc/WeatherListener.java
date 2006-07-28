package free.david.wc;

import java.util.EventListener;

import free.david.weather.Weather;

public interface WeatherListener extends EventListener
	{
	public abstract void inform(Weather weather);
	}
