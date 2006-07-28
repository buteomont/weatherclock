package free.david.wc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedMap;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

public class StateMenu extends JMenu implements ActionListener, PropertyChangeListener
	{
	protected ButtonGroup stateGroup=new ButtonGroup();
	private String selectedState="";
	private SortedMap stateMap;
	
	public StateMenu()
		{
		super();
		initialize();
		}

	public StateMenu(String s)
		{
		super(s);
		initialize();
		}

	public StateMenu(String s, boolean b)
		{
		super(s, b);
		initialize();
		}

	public StateMenu(Action a)
		{
		super(a);
		initialize();
		}

	public StateMenu(SortedMap stateMap, String selectedState)
		{
		super();
		setStateMap(stateMap);
		setSelectedState(selectedState);
		initialize();
		}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize()
		{
		setText("State");
		populate();
		}

	private void populate()
		{
		if (getStateMap()==null) return;
		JMenu currentMenu=this;
		int itemCount=0;
		for (Iterator states=getStateMap().keySet().iterator();
				states.hasNext();)
			{
			JRadioButtonMenuItem m=new JRadioButtonMenuItem((String)states.next());
			currentMenu.add(m);
			getStateGroup().add(m);
			m.addActionListener(this);
			m.setActionCommand("newState");
			if (m.getText().equals(getSelectedState()))
				m.setSelected(true);
			if (++itemCount>15 && states.hasNext())
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
		if (e.getActionCommand().equals("newState"))
			{
			JRadioButtonMenuItem m=(JRadioButtonMenuItem)e.getSource();
			setSelectedState(m.getText());
			
			//notify everyone
			e.setSource(this);
			ActionListener[] als=(ActionListener[])getListeners(ActionListener.class);
			for (int i=0;i<als.length;i++)
				als[i].actionPerformed(e);
			
			}
//		else if (e.getActionCommand().equals("newCountry"))
//			{
//			if (getWeather().getCountry().equals("US"))
//				{
//				for (Enumeration buttons=getStateGroup().getElements();buttons.hasMoreElements();)
//					{
//					JRadioButtonMenuItem button=(JRadioButtonMenuItem)buttons.nextElement();
//					if (button.getText().equals(getWeather().getStateName()))
//						{
//						button.setSelected(true);
//						break;
//						}
//					}
//				}
//			}
		}

	public ButtonGroup getStateGroup()
		{
		return stateGroup;
		}

	public void setStateGroup(ButtonGroup stateGroup)
		{
		this.stateGroup=stateGroup;
		}

	public void propertyChange(PropertyChangeEvent evt)
		{
		//  Update the menu from the weather object.
		for (Enumeration menus=getStateGroup().getElements();menus.hasMoreElements();)
			{
			JRadioButtonMenuItem m=(JRadioButtonMenuItem)menus.nextElement();
			if (m.getText().equals(evt.getNewValue()))
				{
				m.setSelected(true);
				break;
				}
			}
		}

	public String getSelectedState()
		{
		return selectedState;
		}

	public void setSelectedState(String selectedState)
		{
		this.selectedState=selectedState;
		}

	public SortedMap getStateMap()
		{
		return stateMap;
		}

	public void setStateMap(SortedMap stateMap)
		{
		this.stateMap=stateMap;
		}

	

	}
