package free.david.wc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Calendar;

import javax.swing.JPanel;


public class Clock extends JPanel {
	private Color bezelColor=Color.DARK_GRAY;
	private Color faceColor=Color.WHITE;
	private Color numeralColor=Color.BLUE;
	private Color minuteColor=Color.BLACK;
	private Color hourHandColor=Color.BLACK;
	private Color minuteHandColor=Color.BLACK;
	private Color secondHandColor=Color.RED;
	private Color shadowColor=Color.RED;
	
	private int bezelWidthDivisor=40;
	private int numeralDivisor=11;	//18-point font
	private int numeralFontStyle=Font.BOLD;
	private int minuteRadiusDivisor=100; //radius of dots marking minutes
	private int hourHandWidthDivisor=25;
	private int minuteHandWidthDivisor=50;
	private int secondHandWidthDivisor=200;
	private int shadowOffsetXDivisor=-50; //X distance from center for shadows 
	private int shadowOffsetYDivisor=50; //Y distance from center for shadows 
	private String numeralFontFace="Old English Text MT";
	private boolean faceValid=false;
	private boolean backgroundTransparent=true;
	protected Image face;
	
	public long debugTimeMillis=1136959200109l;
	
	private class Coords
		{
		int x1,x2,y1,y2,xoffset,yoffset;
		private Coords(int x1, int y1, int x2, int y2)
			{
			this.x1=x1;
			this.x2=x2;
			this.y1=y1;
			this.y2=y2;
			}
		}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -156919353493674126L;
	public Clock() {
	super();
	initialize();
}

	public Clock(LayoutManager layout) {
		super(layout);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(200,200);
		this.setPreferredSize(new java.awt.Dimension(200,200));
		this.setBackground(java.awt.Color.lightGray);
//		this.setBackground(new Color(255,255,255,0)); //transparent background
		this.setDoubleBuffered(true);
		begin();
	}

	/**
	 * Start keeping time
	 */
	protected void begin()
		{
		new Thread()
			{
			public void run() 
				{
				while(true)
					{
					try 
						{
						sleep(1000);
						}
					catch (InterruptedException e) 
						{
						e.printStackTrace();
						}
					repaint();
					}
				}
			}.start();
		}

	public void paint(Graphics g) 
		{
		Graphics2D g2=(Graphics2D)g;
//		g2.clearRect(0,0,getWidth(),getHeight());
		g2.drawImage(getFace(), 0, 0, null);
		drawHands(g2);
		}

	protected void drawHands(Graphics2D g) 
		{
		Calendar time=getTime();
		Coords hour=getHourHandCoords(time);
		Coords minute=getMinuteHandCoords(time);
		Coords seconds=getSecondHandCoords(time);
		int xoffset=getShadowOffsetX();
		int yoffset=getShadowOffsetY();
		
		//draw the shadows. Use a slightly darker face color with an alpha channel of 25
		setShadowColor(new Color((getFaceColor().darker().getRGB()&0x00ffffff)|0x80000000,true));
		hour.xoffset=xoffset;
		hour.yoffset=yoffset;
		drawHourHand(g, hour, getShadowColor());
		minute.xoffset=xoffset;
		minute.yoffset=yoffset;
		drawMinuteHand(g, minute, getShadowColor());
		seconds.xoffset=xoffset;
		seconds.yoffset=yoffset;
		drawSecondHand(g, seconds, getShadowColor());
		
		//draw the hands
		hour.xoffset=0;
		hour.yoffset=0;
		drawHourHand(g, hour, getHourHandColor());
		minute.xoffset=0;
		minute.yoffset=0;
		drawMinuteHand(g, minute, getMinuteHandColor());
		seconds.xoffset=0;
		seconds.yoffset=0;
		drawSecondHand(g, seconds, getSecondHandColor());
		}

	/**
	 * Local time only for now - may update later for 
	 * other times.
	 * @return Calendar
	 */
	public Calendar getTime()
		{
		Calendar time=Calendar.getInstance();
		return time;
		}

	protected Coords getSecondHandCoords(Calendar time) 
	{
	int shaftX=getWidth()/2;
	int shaftY=getHeight()/2;
	int second=time.get(Calendar.SECOND);
	double polar=4.712; //4.712 = 12 o'clock position
	polar+=second*0.10472; //0.10472=2*pi/60
	double xRadius=(getWidth()/2-getBezelWidth()-getMinuteRadius()*4)*.75;
	double yRadius=(getHeight()/2-getBezelWidth()-getMinuteRadius()*4)*.75;
	double x=(xRadius*Math.cos(polar)+shaftX);
	double y=(yRadius*Math.sin(polar)+shaftY);
	return new Coords((int)shaftX, (int)shaftY, (int)x, (int)y);
	}

	protected void drawSecondHand(Graphics2D g2, Coords c, Color color) 
	{
	g2.setStroke(new BasicStroke(getSecondHandWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	g2.setColor(color);
	g2.drawLine(c.x1+c.xoffset, c.y1+c.yoffset, c.x2+c.xoffset, c.y2+c.yoffset);
	}

	protected Coords getMinuteHandCoords(Calendar time) 
	{
	int shaftX=getWidth()/2;
	int shaftY=getHeight()/2;
	int minute=time.get(Calendar.MINUTE);
	double polar=4.712; //4.712 = 12 o'clock position
	polar+=minute*0.10472; //0.10472=2*pi/60
	double xRadius=(getWidth()/2-getBezelWidth()-getMinuteRadius()*4)*.75;
	double yRadius=(getHeight()/2-getBezelWidth()-getMinuteRadius()*4)*.75;
	double x=(xRadius*Math.cos(polar)+shaftX);
	double y=(yRadius*Math.sin(polar)+shaftY);
	return new Coords((int)shaftX, (int)shaftY, (int)x, (int)y);
	}

	protected void drawMinuteHand(Graphics2D g2, Coords c, Color color) 
	{
	g2.setStroke(new BasicStroke(getMinuteHandWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	g2.setColor(color);
	g2.drawLine(c.x1+c.xoffset, c.y1+c.yoffset, c.x2+c.xoffset, c.y2+c.yoffset);
	}

	protected Coords getHourHandCoords(Calendar time) 
	{
	int shaftX=getWidth()/2;
	int shaftY=getHeight()/2;
	int hour=time.get(Calendar.HOUR);
	double polar=4.712; //4.712 = 12 o'clock position
	polar+=((hour*60d+time.get(Calendar.MINUTE))/60)*0.523598; //0.523598=2*pi/12
	double xRadius=(getWidth()/2-getBezelWidth()-getMinuteRadius()*4)/2;
	double yRadius=(getHeight()/2-getBezelWidth()-getMinuteRadius()*4)/2;
	double x=(xRadius*Math.cos(polar)+shaftX);
	double y=(yRadius*Math.sin(polar)+shaftY);
	return new Coords((int)shaftX, (int)shaftY, (int)x, (int)y);
	}

	protected void drawHourHand(Graphics2D g2, Coords c, Color color) 
	{
	g2.setStroke(new BasicStroke(getHourHandWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	g2.setColor(color);
	g2.setPaintMode();
	g2.drawLine(c.x1+c.xoffset, c.y1+c.yoffset, c.x2+c.xoffset, c.y2+c.yoffset);
	}

	protected void drawMinuteMarks(Graphics2D g) 
		{
		g.setColor(getMinuteColor());
		int xRadius=(getWidth()/2-getBezelWidth()-getMinuteRadius()*4);
		int yRadius=(getHeight()/2-getBezelWidth()-getMinuteRadius()*4);
		int ticks=0;
      	for (double polar=0; 
      		ticks++<60;
      		polar+=0.10472) //0.10472=2*pi/60
      		{
      		int dot=getMinuteRadius();
      		if ((((double)ticks-1)%5d)==0) dot*=3; //heavy dot every 5 minutes
      		double x=(xRadius*Math.cos(polar)+getWidth()/2)-dot/2;
      		double y=(yRadius*Math.sin(polar)+getHeight()/2)-dot/2;
      		g.fillOval((int)x, (int)y, dot, dot);
      		}
		}

	/**
	 * @param g
	 */
	protected void drawNumerals(Graphics2D g) 
		{
		g.setColor(getNumeralColor());
		g.setFont(new Font(getNumeralFontFace(),getNumeralFontStyle(), getNumeralFontSize()));
		
		int numeralXRadius=(getWidth()-getBezelWidth()-g.getFontMetrics().charWidth('3')-getMinuteRadius()*5)/2;
		numeralXRadius-=g.getFontMetrics().getStringBounds("3", g).getBounds2D().getWidth();
		int numeralYRadius=(getHeight()-getBezelWidth()-g.getFontMetrics().getHeight()-getMinuteRadius()*5)/2;
		numeralYRadius-=g.getFontMetrics().getStringBounds("12", g).getBounds2D().getHeight()/2;

		int ticks=0;
      	for (double polar=5.236; //5.236 = one o'clock position
      		ticks++<12;
      		polar+=0.523598) //0.523598=2*pi/12
      		{
      		String hour=ticks+"";
      		Rectangle2D charBox=g.getFontMetrics().getStringBounds(hour, g);
      		double x=(numeralXRadius*Math.cos(polar)+getWidth()/2)-charBox.getCenterX();
      		double y=(numeralYRadius*Math.sin(polar)+getHeight()/2)-charBox.getCenterY();
      		g.drawString(hour, (int)x, (int)y);
      		}
		}

	/**
	 * @param g
	 */
	protected void drawFace(Graphics2D g)
		{
		if (!isBackgroundTransparent())
			{
			g.setColor(getFaceColor());
			int w=getBezelWidth();
			g.fillOval(w-1, w-1, getWidth()+2-w*2, // +2 causes overlap a bit with bezel
				getHeight()+2-w*2);
			}
		}

	/**
	 * @param g
	 */
	protected void drawBezel(Graphics2D g)
		{
		Stroke stroke=g.getStroke();
		g.setStroke(new BasicStroke(getBezelWidth(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
		g.setColor(getBezelColor());
		g.drawOval(getBezelWidth()/2, getBezelWidth()/2, getWidth()-getBezelWidth(), getHeight()-getBezelWidth());
		g.setStroke(stroke);
		}

	public Color getBezelColor() {
		return bezelColor;
	}

	public void setBezelColor(Color bezelColor) {
		this.bezelColor = bezelColor;
	}

	public int getBezelWidth() {
		return getWidth()/bezelWidthDivisor;
	}

	public int getBezelWidthDivisor() {
		return bezelWidthDivisor;
	}

	public void setBezelWidthDivisor(int bezelWidthDivisor) {
		this.bezelWidthDivisor = bezelWidthDivisor;
	}

	public Color getFaceColor() {
		return faceColor;
	}

	public void setFaceColor(Color faceColor) {
		this.faceColor = faceColor;
	}

	public Color getNumeralColor() {
		return numeralColor;
	}

	public void setNumeralColor(Color numeralColor) {
		this.numeralColor = numeralColor;
	}

	public int getNumeralFontSize() {
		return (int)sizeup(numeralDivisor);
	}

	public int getNumeralDivisor() {
		return numeralDivisor;
	}

	public void setNumeralDivisor(int numeralDivisor) {
		this.numeralDivisor = numeralDivisor;
	}

	public String getNumeralFontFace() {
		return numeralFontFace;
	}

	public void setNumeralFontFace(String numeralFontFace) {
		this.numeralFontFace = numeralFontFace;
	}

	public int getNumeralFontStyle() {
		return numeralFontStyle;
	}

	public void setNumeralFontStyle(int numeralFontStyle) {
		this.numeralFontStyle = numeralFontStyle;
	}

	public int getMinuteRadius() 
		{
		return (int)sizeup(minuteRadiusDivisor);
		}

	public int getMinuteRadiusDivisor() {
		return minuteRadiusDivisor;
	}

	public void setMinuteRadiusDivisor(int minuteRadiusDivisor) {
		this.minuteRadiusDivisor = minuteRadiusDivisor;
	}

	public Color getMinuteColor() {
		return minuteColor;
	}

	public void setMinuteColor(Color minuteColor) {
		this.minuteColor = minuteColor;
	}

	public float getMinuteHandWidth() {
		return sizeup(minuteHandWidthDivisor);
	}

	protected float sizeup(int divisor) {
		int dim=Math.max(getWidth(),getHeight());
		double ratio=(double)getWidth()/getHeight();
		if (ratio>1) ratio=1/ratio;
		return (float) ratio*(dim/divisor);
	}

	public int getMinuteHandWidthDivisor() {
		return minuteHandWidthDivisor;
	}

	public void setMinuteHandWidthDivisor(int minuteHandWidthDivisor) {
		this.minuteHandWidthDivisor = minuteHandWidthDivisor;
	}

	public float getSecondHandWidth() 
		{
		return sizeup(secondHandWidthDivisor);
		}

	public int getSecondHandWidthDivisor() {
		return secondHandWidthDivisor;
	}

	public void setSecondHandWidthDivisor(int secondHandWidthDivisor) {
		this.secondHandWidthDivisor = secondHandWidthDivisor;
	}

	public float getHourHandWidth() {
		return sizeup(hourHandWidthDivisor);
	}

	public int getHourHandWidthDivisor() {
		return hourHandWidthDivisor;
	}

	public void setHourHandWidthDivisor(int hourHandWidthDivisor) {
		this.hourHandWidthDivisor = hourHandWidthDivisor;
	}

	public Color getHourHandColor() {
		return hourHandColor;
	}

	public void setHourHandColor(Color hourHandColor) {
		this.hourHandColor = hourHandColor;
	}

	public Color getMinuteHandColor() {
		return minuteHandColor;
	}

	public void setMinuteHandColor(Color minuteHandColor) {
		this.minuteHandColor = minuteHandColor;
	}

	public Color getSecondHandColor() {
		return secondHandColor;
	}

	public void setSecondHandColor(Color secondHandColor) {
		this.secondHandColor = secondHandColor;
	}
	public int getShadowOffsetXDivisor() {
		return shadowOffsetXDivisor;
	}

	public int getShadowOffsetX() {
		return (int)sizeup(shadowOffsetXDivisor);
	}

	public void setShadowOffsetXDivisor(int shadowOffsetXDivisor) {
		this.shadowOffsetXDivisor = shadowOffsetXDivisor;
	}

	public int getShadowOffsetYDivisor() {
		return shadowOffsetYDivisor;
	}

	public int getShadowOffsetY() {
		return (int)sizeup(shadowOffsetYDivisor);
	}

	public void setShadowOffsetYDivisor(int shadowOffsetYDivisor) {
		this.shadowOffsetYDivisor = shadowOffsetYDivisor;
	}

	public Color getShadowColor() {
		return shadowColor;
	}

	public void setShadowColor(Color shadowColor) {
		this.shadowColor = shadowColor;
	}

	protected boolean isFaceValid()
		{
		return faceValid;
		}

	protected void setFaceValid(boolean faceValid)
		{
		this.faceValid=faceValid;
		}

	public Image getFace()
		{
		if (isFaceValid()==false)
			{
			setFaceValid(true);
			BufferedImage img=new BufferedImage(getWidth(), 
												getHeight(),
												BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g=(Graphics2D)img.getGraphics();
			g.setBackground(Color.WHITE);
			g.clearRect(0,0,getWidth(),getHeight());
			drawFace(g);
			drawBezel(g);
			drawMinuteMarks(g);
			drawNumerals(g);
			setFace(img);
			}
		return face;
		}

	public void setFace(Image face)
		{
		this.face=face;
		}

	public boolean isBackgroundTransparent()
		{
		return backgroundTransparent;
		}

	public void setBackgroundTransparent(boolean backgroundTransparent)
		{
		this.backgroundTransparent=backgroundTransparent;
		}

}  //  @jve:decl-index=0:visual-constraint="10,10"
