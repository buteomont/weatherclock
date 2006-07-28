package free.david.wc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import javax.swing.*;

import free.david.weather.Weather;

public class CityMenu extends JMenu implements ActionListener, PropertyChangeListener
	{
	private ButtonGroup cityGroup=new ButtonGroup();
	private String selectedStation=null;
	private SortedSet stationList=null;
	
	public CityMenu()
		{
		super();
		initialize();
		}

	public CityMenu(String s)
		{
		super(s);
		initialize();
		}

	public CityMenu(String s, boolean b)
		{
		super(s, b);
		initialize();
		}

	public CityMenu(Action a)
		{
		super(a);
		initialize();
		}

	public CityMenu(SortedSet stationList, String selectedStation)
		{
		super();
		setStationList(stationList);
		setSelectedStation(selectedStation);
		initialize();
		}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize()
		{
		setText("Station");
		populate();
		}

	public void populate()
		{
		if (getStationList()==null) return;
		JMenu currentMenu=this;
		JMenu rootMenu=this;
		int itemCount=0;
		String alphaList=""; //top level menu, A, B, C, etc

		removeAll();
		setCityGroup(new ButtonGroup());
		for (Iterator cities=getStationList().iterator();
				cities.hasNext();)
			{
			String city=(String)cities.next();
			if (!city.substring(0, 1).equalsIgnoreCase(alphaList))
				{
				alphaList=city.substring(0, 1).toUpperCase();
				currentMenu=rootMenu;
				itemCount=0;
				JMenu newMenu=new JMenu(alphaList);
				currentMenu.add(newMenu);
				currentMenu=newMenu;
				}
			JRadioButtonMenuItem m=new JRadioButtonMenuItem(city);
			m.setActionCommand("newStation");
			currentMenu.add(m);
			getCityGroup().add(m);
			m.addActionListener(this);
			if (m.getText().equals(getSelectedStation()))
				m.setSelected(true);
			if (++itemCount>15 && cities.hasNext())
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
		if (e.getActionCommand().equals("newStation"))
			{
			JRadioButtonMenuItem m=(JRadioButtonMenuItem)e.getSource();
			setSelectedStation(m.getText());
			e.setSource(this);
			
			ActionListener[] als=(ActionListener[])getListeners(ActionListener.class);
			for (int i=0;i<als.length;i++)
				als[i].actionPerformed(e);
			}
		}

	public ButtonGroup getCityGroup()
		{
		return cityGroup;
		}

	public void setCityGroup(ButtonGroup stateGroup)
		{
		this.cityGroup=stateGroup;
		}

	public void propertyChange(PropertyChangeEvent evt)
		{
		for (Enumeration menus=getCityGroup().getElements();menus.hasMoreElements();)
			{
			JRadioButtonMenuItem m=(JRadioButtonMenuItem)menus.nextElement();
			if (m.getText().equals(evt.getNewValue()))
				{
				m.setSelected(true);
				break;
				}
			}
		}

	public String getSelectedStation()
		{
		return selectedStation;
		}

	public void setSelectedStation(String selectedStation)
		{
		this.selectedStation=selectedStation;
		}

	public SortedSet getStationList()
		{
		return stationList;
		}

	public void setStationList(SortedSet stationList)
		{
		if (getStationList()!=null && getStationList().equals(stationList))
			return;
		else
			{
			this.stationList=stationList;
			populate();
			}
		}

	

	}
