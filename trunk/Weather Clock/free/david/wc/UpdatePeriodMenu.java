package free.david.wc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import free.david.weather.Weather;

public class UpdatePeriodMenu extends JMenu implements ActionListener
	{
	ButtonGroup updatePeriodGroup=new ButtonGroup();
	Weather weather;
	
	private UpdatePeriodMenu()
		{
		super();
		initialize();
		}

	public UpdatePeriodMenu(String s)
		{
		super(s);
		initialize();
		}

	public UpdatePeriodMenu(String s, boolean b)
		{
		super(s, b);
		initialize();
		}

	public UpdatePeriodMenu(Action a)
		{
		super(a);
		initialize();
		}

	public UpdatePeriodMenu(Weather weather)
		{
		super();
		this.weather=weather;
		initialize();
		}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize()
		{
		setText("Update Period");
		insert(1,"1 Minute");
		insert(5,"5 Minutes");
		insert(10,"10 Minutes");
		insert(30,"30 Minutes");
		insert(60,"1 Hour");
		}

	private void insert(int minutes, String text)
		{
		JRadioButtonMenuItem item=new JRadioButtonMenuItem(text);
		item.setActionCommand(minutes+"");
		item.addActionListener(this);
		updatePeriodGroup.add(item);
		if (weather.getUpdatePeriod()==minutes)
			item.setSelected(true);
		add(item);
		}

	public void actionPerformed(ActionEvent e)
		{
		weather.setUpdatePeriod(Integer.parseInt(e.getActionCommand()));
		weather.interrupt();
		}

	}
