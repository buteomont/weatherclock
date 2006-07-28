package free.david.wc;

import javax.swing.*;

public class CountryRadioButtonMenuItem extends JRadioButtonMenuItem
	{
	private String countryCode="";
	
	public CountryRadioButtonMenuItem()
		{
		super();
		}

	public CountryRadioButtonMenuItem(String text)
		{
		super(text);
		}

	public CountryRadioButtonMenuItem(String text, String countryCode)
		{
		super(text);
		setCountryCode(countryCode);
		}

	public String getCountryCode()
		{
		return countryCode;
		}

	public void setCountryCode(String countryCode)
		{
		this.countryCode=countryCode;
		}

	}
