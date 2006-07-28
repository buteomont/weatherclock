package free.david.wc;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

public class FogBank
	{
	private int	beginBank;
	private int	endBank;
	private int bankLength;
	private int bankHeight;
	private Color bankColor;
	private Color bankClearColor;
	private Component glass;
	private int yPos;
	private boolean ready=false;
	private boolean foggy=true;
	private boolean leftToRight=true;
	private GradientPaint fogGradient;
	
	/**
	 * Hide the default constructor
	 */
	private FogBank() 
		{}
	/**
	 * This is the only constructor
	 * @throws Exception 
	 */
	public FogBank(Component glassPane,
					Color fogColor) throws Exception
		{
		super();
		setGlass(glassPane);
		setBankColor(fogColor);
		initialize();
		}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
		{
		leftToRight=Math.random()<=0.5;
		setYPos(((int)Math.random()*getGlass().getHeight()));
		setBankLength(getGlass().getWidth());
		setBankHeight(((int)Math.random()*getGlass().getHeight()));
		new Thread()
			{
			public void run()
				{
				beginBank=((int)Math.random()*getGlass().getWidth());
				endBank=beginBank+getBankLength();
				setYPos((int)Math.random()*getGlass().getHeight());
				while(isFoggy())
					{
					while (isFoggy() && ((leftToRight && beginBank<getGlass().getWidth()) 
										|| (!leftToRight && endBank>0)))
						{
						try{sleep((long)(Math.random()*1000));}
						catch (InterruptedException e){}
						
						beginBank+=(leftToRight?1:-1)*Math.min((getGlass().getWidth())/50,
									(Math.random()*(getGlass().getWidth()/30)));
						endBank=beginBank+getBankLength();
						yPos+=(leftToRight?1:-1)*Math.min((getGlass().getHeight())/50,
									(Math.random()*(getGlass().getHeight()/30)));
						while(yPos<0) yPos+=getGlass().getHeight();
						fogGradient=new GradientPaint(getBeginBank(), getYPos(), bankClearColor, getEndBank()/2, getGlass().getHeight()-getYPos(), getBankColor(),true);
						getGlass().repaint();
						}
					beginBank=leftToRight?0-getBankLength():getGlass().getWidth();
					endBank=beginBank+getBankLength();
					setYPos(leftToRight?0:getGlass().getHeight());
					}
				}
			}.start();
		ready=true;
		}

	public void paint(Graphics g)
		{
		if (ready && fogGradient!=null)
			{
			Graphics2D g2=(Graphics2D)g;
			Paint p=g2.getPaint();
			g2.setPaint(fogGradient);
			g2.fillRect(0, 0, getGlass().getWidth(), getGlass().getHeight());
			g2.setPaint(p);
			}
		}

	protected void paintBorder(Graphics g)
		{
		// no border
		// super.paintBorder(g);
		}

	public int getBeginBank()
		{
		return beginBank;
		}

	public void setBeginBank(int beginBank)
		{
		this.beginBank=beginBank;
		}

	public int getEndBank()
		{
		return endBank;
		}

	public void setEndBank(int endBank)
		{
		this.endBank=endBank;
		}

	public Component getGlass()
		{
		return glass;
		}
	public void setGlass(Component glass)
		{
		this.glass=glass;
		}
	public int getYPos()
		{
		return yPos;
		}
	public void setYPos(int pos)
		{
		yPos=pos;
		}
	public int getBankLength()
		{
		return bankLength;
		}
	public void setBankLength(int bankLength)
		{
		this.bankLength=bankLength;
		}
	public Color getBankColor()
		{
		return bankColor;
		}
	public void setBankColor(Color bankColor)
		{
		int r=bankColor.brighter().getRed();
		int g=bankColor.brighter().getGreen();
		int b=bankColor.brighter().getBlue();
		this.bankColor=new Color(r,g,b,32);
		this.bankClearColor=new Color(r,g,b,0);
		}
	public boolean isFoggy()
		{
		return foggy;
		}
	public void setFoggy(boolean wet)
		{
		this.foggy=wet;
		}
	public int getBankHeight()
		{
		return bankHeight;
		}
	public void setBankHeight(int bankHeight)
		{
		this.bankHeight=bankHeight;
		}
	} // @jve:decl-index=0:visual-constraint="10,10"
