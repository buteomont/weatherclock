package free.david.wc;

import java.awt.*;

public class SnowFlake
	{
	private int				yPos;
	private float			flakeSize;
	private Color			flakeColor=new Color(0,0,0);
	private Component		glass;
	private int				xPos;
	private boolean			ready	=false;
	private boolean			snowy	=true;
	private boolean			xRandom	=false;
	private long 			updateRate=(long)(Math.random()*1000)+100;		

	/**
	 * Hide the default constructor
	 */
	private SnowFlake(){}

	/**
	 * This is the only constructor
	 * @throws Exception
	 */
	public SnowFlake(Component glassPane, int horizStartPosition, float flakeSize, boolean randomX, Color dripColor)
		throws Exception
		{
		super();
		setGlass(glassPane);
		setXPos(horizStartPosition);
		setFlakeSize(flakeSize);
		setXRandom(randomX);
		setFlakeColor(dripColor);
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
					while (isSnowy())
						{
						yPos=0;
						updateRate=(long)(Math.random()*1000)+100;
						if (isXRandom()) setXPos((int)(Math.random()*getGlass().getWidth()));
						while (yPos<getGlass().getHeight()&&isSnowy())
							{
							try
								{
								sleep(updateRate);
								}
							catch (InterruptedException e)
								{
								}

							setYPos((int)(getYPos()+getFlakeSize()));
							double xoffset=Math.random();
							if (xoffset<0.34d)
								setXPos((int)(getXPos()-getFlakeSize()));
							else if (xoffset>0.67d)
								setXPos((int)(getXPos()+getFlakeSize()));
							getGlass().repaint();
							}
						}
					}
			}.start();
		ready=true;
		}

	public void paint(Graphics g)
		{
		if (ready)
			{
			Graphics2D g2=(Graphics2D)g;
			g2.setColor(getFlakeColor());
			g2.fillOval((int)(getXPos()-getFlakeSize()/2), getYPos(), (int)getFlakeSize(), (int)getFlakeSize());
			}
		}

	protected void paintBorder(Graphics g)
		{
		// no border
		// super.paintBorder(g);
		}

	public int getYPos()
		{
		return yPos;
		}

	public void setYPos(int flakeY)
		{
		this.yPos=flakeY;
		}

	public Component getGlass()
		{
		return glass;
		}

	public void setGlass(Component glass)
		{
		this.glass=glass;
		}

	public int getXPos()
		{
		return xPos;
		}

	public void setXPos(int pos)
		{
		xPos=pos;
		}

	public float getFlakeSize()
		{
		return flakeSize;
		}

	public void setFlakeSize(float flakeSize)
		{
		this.flakeSize=flakeSize;
		}

	public boolean isXRandom()
		{
		return xRandom;
		}

	public void setXRandom(boolean random)
		{
		xRandom=random;
		}

	public Color getFlakeColor()
		{
		return flakeColor;
		}

	public void setFlakeColor(Color dripColor)
		{
		this.flakeColor=dripColor;
		}

	public boolean isSnowy()
		{
		return snowy;
		}

	public void setSnowy(boolean snowy)
		{
		this.snowy=snowy;
		}

	}
