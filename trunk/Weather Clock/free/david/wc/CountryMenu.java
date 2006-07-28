package free.david.wc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import javax.swing.*;

public class CountryMenu extends JMenu implements ActionListener, PropertyChangeListener
	{
	private ButtonGroup countryGroup=new ButtonGroup();
	private SortedMap countryMap;
	private String selectedCountry=null;
	
	public CountryMenu()
		{
		super();
		// TODO Auto-generated constructor stub
		}

	public CountryMenu(String s)
		{
		super(s);
		// TODO Auto-generated constructor stub
		}

	public CountryMenu(String s, boolean b)
		{
		super(s, b);
		// TODO Auto-generated constructor stub
		}

	public CountryMenu(Action a)
		{
		super(a);
		// TODO Auto-generated constructor stub
		}

	public CountryMenu(SortedMap countryMap, String selectedCountry)
		{
		super();
		setCountryMap(countryMap);
		setSelectedCountry(selectedCountry);
		initialize();
		}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize()
		{
		setText("Country");
		populate();
		}

	private void populate()
		{
		if (getCountryMap()==null) return;
		JMenu currentMenu=this;
		JMenu rootMenu=this;
		int itemCount=0;
		String alphaList=""; //top level menu, A, B, C, etc
		for (Iterator countries=getCountryMap().keySet().iterator();
				countries.hasNext();)
			{
			String country=(String)countries.next();
			String code=(String)getCountryMap().get(country);
			if (!country.substring(0, 1).equalsIgnoreCase(alphaList))
				{
				alphaList=country.substring(0, 1).toUpperCase();
				currentMenu=rootMenu;
				itemCount=0;
				JMenu newMenu=new JMenu(alphaList);
				currentMenu.add(newMenu);
				currentMenu=newMenu;
				}
			CountryRadioButtonMenuItem m=new CountryRadioButtonMenuItem(country,code);
			m.setActionCommand("newCountry");
			currentMenu.add(m);
			getCountryGroup().add(m);
			m.addActionListener(this);
			if (m.getCountryCode().equals(getSelectedCountry()))
				m.setSelected(true);
			if (++itemCount>26 && countries.hasNext())
				{
				itemCount=0;
				JMenu newMenu=new JMenu("More");
				currentMenu.add(newMenu);
				currentMenu=newMenu;
				}
			}
		}
	
	public void actionPerformed(ActionEvent e)
		{
		if (e.getActionCommand().equals("newCountry")) //must be one of us
			{
			CountryRadioButtonMenuItem m=(CountryRadioButtonMenuItem)e.getSource();
			setSelectedCountry(m.getCountryCode());
			e.setSource(this);
			
			ActionListener[] als=(ActionListener[])getListeners(ActionListener.class);
			for (int i=0;i<als.length;i++)
				als[i].actionPerformed(e);
			
//			getWeather().interrupt();
			}
		}

	public ButtonGroup getCountryGroup()
		{
		return countryGroup;
		}

	public void setCountryGroup(ButtonGroup stateGroup)
		{
		this.countryGroup=stateGroup;
		}
	
	/* Returns a sorted Map with country names for keys, and country codes
	 * for values.
	 */
	private SortedMap getCountryMap()
		{
		return countryMap;
		}

	public void propertyChange(PropertyChangeEvent evt)
		{
		// Update the country from the weather object.
		for (Enumeration menus=getCountryGroup().getElements();menus.hasMoreElements();)
			{
			CountryRadioButtonMenuItem m=(CountryRadioButtonMenuItem)menus.nextElement();
			if (m.getCountryCode().equals(evt.getNewValue()))
				{
				m.setSelected(true);
				break;
				}
			}
		}

	public String getSelectedCountry()
		{
		return selectedCountry;
		}

	public void setSelectedCountry(String selectedCountry)
		{
		this.selectedCountry=selectedCountry;
		}

	public void setCountryMap(SortedMap countryMap)
		{
		this.countryMap=countryMap;
		}
	}
