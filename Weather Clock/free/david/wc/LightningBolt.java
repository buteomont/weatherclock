package free.david.wc;

import java.awt.*;

public class LightningBolt
	{
	private boolean		ready		=false;
	private boolean		stormy		=true;
	private boolean 	flash		=false;
	private boolean 	flashed		=false;
	private Component	glass;
	private Color		boltColor	=Color.WHITE;
	private Color		originalColor;

	/**
	 * Hide the default constructor
	 */
	private LightningBolt()
		{
		}

	/**
	 * This is the only constructor
	 * 
	 * @throws Exception
	 */
	public LightningBolt(Component glassPane) throws Exception
		{
		super();
		setGlass(glassPane);
		initialize();
		}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
		{
		new Thread()
			{
				public void run()
					{
					while (isStormy())
						{
						delay((long)(Math.random()*20000)+10000);
						
						int strokes=Math.random()>.5?2:1;
						while (strokes-->0)
							{
							if (!isStormy()) break;
							strike();
							if (!isStormy()) break;
							strike();
							}
						}
					}
				private void strike()
					{
					delay((long)(Math.random()*200));
					flash=true;
					flashed=false;
					getGlass().repaint();
					while (!flashed) delay(5); //give fast machines a chance to redraw
					flash=false;
					}
				private void delay(long milliseconds)
					{
					try{sleep(milliseconds);}
					catch (InterruptedException e){}
					}
			}.start();
		ready=true;
		}

	public void paint(Graphics g)
		{
		if (ready && flash)
			{
			Graphics2D g2=(Graphics2D)g;
			g2.setXORMode(getBoltColor());
			g2.fillRect(0, 0, getGlass().getWidth(), getGlass().getHeight());
			g2.setPaintMode();
			flashed=true;
			}
		}

	protected void paintBorder(Graphics g)
		{
		// no border
		// super.paintBorder(g);
		}

	public boolean isReady()
		{
		return ready;
		}

	public void setReady(boolean ready)
		{
		this.ready=ready;
		}

	public boolean isStormy()
		{
		return stormy;
		}

	public void setStormy(boolean stormy)
		{
		this.stormy=stormy;
		}

	public Color getBoltColor()
		{
		return boltColor;
		}

	public void setBoltColor(Color boltColor)
		{
		this.boltColor=boltColor;
		}

	public Component getGlass()
		{
		return glass;
		}

	public void setGlass(Component glass)
		{
		this.glass=glass;
		}

	public Color getOriginalColor()
		{
		return originalColor;
		}

	public void setOriginalColor(Color originalColor)
		{
		this.originalColor=originalColor;
		}

	}
