package free.david.wc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;

public class RainDrop
	{
	private int	beginDrip;
	private int	endDrip;
	private float dripWidth;
	private int dripLength;
	private Color dripColor;
	private Color dripClearColor;
	private Component glass;
	private int xPos;
	private boolean ready=false;
	private boolean wet=true;
	private boolean xRandom=false;
	protected GradientPaint dripBottom;
	
	/**
	 * Hide the default constructor
	 */
	private RainDrop() 
		{}
	/**
	 * This is the only constructor
	 * @throws Exception 
	 */
	public RainDrop(Component glassPane, 
					int horizStartPosition,
					float dripWidth,
					boolean randomX,
					Color dripColor) throws Exception
		{
		super();
		setGlass(glassPane);
		setXPos(horizStartPosition);
		setDripWidth(dripWidth);
		setXRandom(randomX);
		setDripColor(dripColor);
		initialize();
		}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
		{
		dripBottom=new GradientPaint(getDripWidth()/2, getDripWidth()/2, Color.WHITE,
					 getDripWidth()/2, getDripWidth(), getDripColor(),true);
		new Thread()
			{
			public void run()
				{
				while(isWet())
					{
					beginDrip=(int)(Math.random()*getGlass().getHeight());
					endDrip=beginDrip;
					dripLength=(int)(Math.random()*(getGlass().getHeight()-beginDrip));
					if (isXRandom())setXPos((int)(Math.random()*getGlass().getWidth()));
					while (beginDrip<getGlass().getHeight() && isWet())
						{
						try{sleep((long)(Math.random()*5000));}
						catch (InterruptedException e){}
						
						endDrip+=Math.min((getGlass().getHeight())/10,
									(Math.random()*(getGlass().getHeight()/5)));
						if (endDrip-beginDrip>dripLength)
							beginDrip=endDrip-dripLength;
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
			GradientPaint drip=new GradientPaint(getXPos(), getBeginDrip(), dripClearColor, getXPos(), getEndDrip(), getDripColor());
			Paint p=g2.getPaint();
			g2.setPaint(drip);
			Stroke s=g2.getStroke();
			g2.setStroke(new BasicStroke(getDripWidth(),
										BasicStroke.CAP_ROUND,
										BasicStroke.JOIN_BEVEL));
	
			g2.drawLine(getXPos(), getBeginDrip(), getXPos(), getEndDrip());
			g2.setPaint(dripBottom);
			g2.setStroke(s);
			g2.fillOval((int)(getXPos()-getDripWidth()/2), getEndDrip(), (int)getDripWidth(), (int)getDripWidth());
			g2.setPaint(p);
			}
		}

	protected void paintBorder(Graphics g)
		{
		// no border
		// super.paintBorder(g);
		}

	public int getBeginDrip()
		{
		return beginDrip;
		}

	public void setBeginDrip(int beginDrip)
		{
		this.beginDrip=beginDrip;
		}

	public int getEndDrip()
		{
		return endDrip;
		}

	public void setEndDrip(int endDrip)
		{
		this.endDrip=endDrip;
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
	public float getDripWidth()
		{
		return dripWidth;
		}
	public void setDripWidth(float dripWidth)
		{
		this.dripWidth=dripWidth;
		}
	public int getDripLength()
		{
		return dripLength;
		}
	public void setDripLength(int dripLength)
		{
		this.dripLength=dripLength;
		}
	public boolean isXRandom()
		{
		return xRandom;
		}
	public void setXRandom(boolean random)
		{
		xRandom=random;
		}
	public Color getDripColor()
		{
		return dripColor;
		}
	public void setDripColor(Color dripColor)
		{
		this.dripColor=dripColor;
		int r=dripColor.brighter().getRed();
		int g=dripColor.brighter().getGreen();
		int b=dripColor.brighter().getBlue();
		this.dripClearColor=new Color(r,g,b,0);
		}
	public boolean isWet()
		{
		return wet;
		}
	public void setWet(boolean wet)
		{
		this.wet=wet;
		}

	} // @jve:decl-index=0:visual-constraint="10,10"
