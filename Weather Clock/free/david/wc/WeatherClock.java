package free.david.wc;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;

import free.david.weather.MetarWeather;
import free.david.weather.Weather;

public class WeatherClock extends Clock implements WeatherListener,
												ComponentListener,
												MouseListener,
												MouseMotionListener
    {
    private static final long serialVersionUID=809703545713396433L;
    private Weather weather             =null;
    private Color compassColor          =Color.RED.darker();
    private String  compassFontFace     ="COMIC SANS MS";
    private int     compassFontStyle    =Font.BOLD;
    private int     compassFontSizeDivisor      =20;
    private List rainDrops              =new Vector();
    private List snowFlakes             =new Vector();
    private List fogBanks             	=new Vector();
    private List lightningBolts        	=new Vector();
    private int rainDropWidthDivisor    =40;
    private Properties settings;
    private String imageURL             ="http://www.newschannel5.com/skycam/radar_Metro.jpg";
    private boolean faceImage           =false;
    private Image faceImg               =null;
    private Image oldFaceImg            =null;
    private int faceImageFetchStage     =0; //0=idle, 1=fetching, 2=fetched
    private boolean raining             =false;
    private boolean snowing             =false;
    private boolean foggy             	=false;
    private boolean lightning			=false;
    private boolean error=false;
    private Moon moon;
	private boolean outFetching			=false;
	private String[] imageURLList;
	private boolean randomImage			=false;
	private boolean drawMiniMoon		=true;
	private Rectangle miniMoonArea		=null;
	private boolean draggingMiniMoon	=false;
	private int dragMoonOffsetX			=0;
	private int dragMoonOffsetY			=0;
	private int oldHeight				=0;
	private int oldWidth				=0;
	private int newHeight				=0;
	private int newWidth				=0;

//    public static final long NEW_MOON_DATE=3:12 on dec 31,2005
    public static final float MOON_MONTH=29.5306f;//synodic month is 29.5306 days

    public WeatherClock()
        {
        super();
        initialize();
        }

    private Rectangle sizeMiniMoon()
		{
		if (oldWidth<=0 || oldHeight<=0)
			return new Rectangle((int)(getWidth()*.9)/2,
					(int)(getHeight()*.9)/3,
						getWidth()/10,
						getHeight()/10);
		else
			{
			Rectangle oldMM=getMiniMoonArea();
			double xFactor=(double)getWidth()/oldWidth;
			double yFactor=(double)getHeight()/oldHeight;
			return new Rectangle((int)(oldMM.x*xFactor),
								 (int)(oldMM.y*yFactor),
								 getWidth()/10,
								 getHeight()/10);
			}
		}

	public WeatherClock(Properties specifics)
        {
        super();
        this.settings=specifics;
        initialize();
        }

    public WeatherClock(LayoutManager layout)
        {
        super(layout);
        initialize();
        }

    public Color getCompassColor()
        {
        return compassColor;
        }

    public void setCompassColor(Color compassColor)
        {
        this.compassColor=compassColor;
        }

    /**
     * This method initializes this
     *
     */
    private void initialize()
        {
        super.setToolTipText("Weather is temporarily unavailable.");
        this.setLayout(null);
        this.setVisible(true);
        this.setBackground(new Color(255,255,255,0));
        if (settings!=null)
            {
            String face=settings.getProperty("useImage", "The Moon");
            setFaceImage(face.equalsIgnoreCase("The Moon")?false:true);
            setRandomImage(face.equalsIgnoreCase("Random Image")?true:false);
            setBackgroundTransparent(isFaceImage());
            String imgurl=settings.getProperty("imageURL");
            if (imgurl!=null)
                {
                int urlend=imgurl.indexOf(" ");
                if (urlend>0)
                    imgurl=imgurl.substring(urlend);
                setImageURL(imgurl);
                }
            setFaceValid(false);
            }
        this.add(getMoon(), null);
        addComponentListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        getWeather().start();
        }

    /**
     * @return String representing the current time and date, followed
     * by a tab character.
     */
    public static String timeStamp()
        {
        return Calendar.getInstance().getTime().toString()+"\t";
        }

    public synchronized Weather getWeather()
        {
        if (weather==null)
            {
            if (settings!=null)
                weather=new MetarWeather(settings);
            else
                weather=new MetarWeather();
            weather.addWeatherListener(this);
            }
        return weather;
        }

    public void setWeather(Weather weather)
        {
        this.weather=weather;
        }

    public String getCompassFontFace()
        {
        return compassFontFace;
        }

    public void setCompassFontFace(String compassFontFace)
        {
        this.compassFontFace=compassFontFace;
        }

    public int getCompassFontSize()
        {
        return (int)sizeup(compassFontSizeDivisor);
        }

    public int getCompassFontStyle()
        {
        return compassFontStyle;
        }

    public void setCompassFontStyle(int compassFontStyle)
        {
        this.compassFontStyle=compassFontStyle;
        }

    public void inform(Weather weather)
        {
        if (weather.getErrorMessage()!=null)
            {
            setToolTipText("<html>Error while updating weather for "
                +weather.getCity()+", "+(weather.getCountry().equals("US")?weather.getStateName():weather.getCountry())+"."
                +"<br><br>Error is:<br><br>"
                +substitute(weather.getErrorMessage(),"\n","<br>"));
            weather.setErrorMessage(null);
            setError(true);
            }
        else
            {
            setError(false);
            if (weather.isUpdateInProgress())
                {
                setToolTipText("Weather update in progress");
                }
            else
                {
                float h=0, s=0, b=100;

                //saturation based on sky
                s=getWeather().assessSky();
                
                //see if there's any weather to show
                createRain(getWeather().assessRain());
                createFog(getWeather().assessFog());
                createSnow(getWeather().assessSnow());
                createLightning(getWeather().assessLightning());

                //hue based on temperature
                //hue should be from 0.0 (hot-110 deg.) to
                //0.66 (cold - minus 10 deg.)
                //skip 0.15 thru 0.51 because it's too green
                h=(weather.getTemperature())+10; //-10 deg. is minimum
                float skipStart=0.18f;
                float skipEnd=0.48f;
                h=(h/120f)*(0.66f-(skipEnd-skipStart));
                if (h>skipStart) h+=(skipEnd-skipStart);
                h=0.66f-h;

                //brightness is highest at noon
                int t=getTime().get(Calendar.HOUR_OF_DAY)*60+getTime().get(Calendar.MINUTE);
                int noon=720; //12*60
                if (t>noon) t=noon-(t-noon);
                b=(float)t/(float)noon;

                setFaceColor(Color.getHSBColor(h, s, b));

                StringTokenizer ut=new StringTokenizer(timeStamp());
                ut.nextToken();ut.nextToken();ut.nextToken();
                String tm=ut.nextToken();
                setToolTip(tm.substring(0, tm.lastIndexOf(":")));
                faceImageFetchStage=0; // fetch a whole new face image
                setFaceValid(false); //should cause face to get redrawn on next paint
                }
            }
        }


    private void createRain(int dropCount)
        {
        if (rainDrops.size()==dropCount)
            return;
        dryOut(); //kill any existing RainDrop threads
        rainDrops=new Vector();
        for (int i=0;i<dropCount;i++)
            {
            try
                {
                rainDrops.add(new RainDrop((Component)this,
                                        (int)(Math.random()*getWidth()),
                                        getRaindropWidth(),
                                        true,
                                        getFaceColor().darker()));
                }
            catch (Exception e)
                {
                e.printStackTrace();
                }
            }
        setRaining(dropCount>0);
        }

    private void createFog(int fogBankCount)
        {
        if (fogBanks.size()==fogBankCount)
            return;
        liftFog(); //kill any existing FogBank threads
        fogBanks=new Vector();
        for (int i=0;i<fogBankCount;i++)
            {
            try
                {
                fogBanks.add(new FogBank((Component)this, Color.WHITE));
                }
            catch (Exception e)
                {
                e.printStackTrace();
                }
            }
        setFoggy(fogBankCount>0);
        }

    private void createLightning(int lightningBoltCount)
        {
        if (lightningBolts.size()==lightningBoltCount)
            return;
        stopLightning(); //kill any existing LightningBolt threads
        lightningBolts=new Vector();
        for (int i=0;i<lightningBoltCount;i++)
            {
            try
                {
                lightningBolts.add(new LightningBolt((Component)this));
                }
            catch (Exception e)
                {
                e.printStackTrace();
                }
            }
        setLightning(lightningBoltCount>0);
        }

    private void createSnow(int flakeCount)
        {
        if (snowFlakes.size()==flakeCount)
            return;
        stopSnowing(); //kill any existing SnowFlake threads
        snowFlakes=new Vector();
        for (int i=0;i<flakeCount;i++)
            {
            try
                {
                snowFlakes.add(new SnowFlake((Component)this,
                                        (int)(Math.random()*getWidth()),
                                        getRaindropWidth(),
                                        true,
                                        Color.WHITE));
                }
            catch (Exception e)
                {
                e.printStackTrace();
                }
            }
        setSnowing(flakeCount>0);
        }

    private void dryOut()
        {
        for (int drops=rainDrops.size();
             drops>0;
             drops--)
            {
            RainDrop drop=(RainDrop)rainDrops.remove(0);
            drop.setWet(false);
            }
        setRaining(false);
        }

    private void stopLightning()
        {
        for (int bolts=lightningBolts.size();
             bolts>0;
             bolts--)
            {
            LightningBolt bolt=(LightningBolt)lightningBolts.remove(0);
            bolt.setStormy(false);
            }
        setLightning(false);
        }

    private void liftFog()
        {
        for (int banks=fogBanks.size();
        	 banks>0;
        	 banks--)
            {
            FogBank bank=(FogBank)fogBanks.remove(0);
            bank.setFoggy(false);
            }
        setFoggy(false);
        }

    private void stopSnowing()
        {
        for (int flakes=snowFlakes.size();
                flakes>0;
                flakes--)
            {
            SnowFlake flake=(SnowFlake)snowFlakes.remove(0);
            flake.setSnowy(false);
            }
        setSnowing(false);
        }

    private float getRaindropWidth()
        {
        return sizeup(rainDropWidthDivisor);
        }

    private void setToolTip(String updateTime)
        {
        StringBuffer buf=new StringBuffer("<html>");
        buf.append("<b><center>Current conditions in <a href='>")
            .append(weather.getStationURL()).append("'>")
            .append(weather.getCity()).append(",&nbsp;&nbsp;")
            .append(weather.getCountry().equals("US")?
                    weather.getStateName():
                    translateCountry(weather.getCountry()))
            .append(" (").append(weather.getCities().get(weather.getCity())).append(")")
            .append("</a>").append("</center></b>");
        buf.append("<br><small><strong>As of ").append(weather.getStationTimestamp()).append(":<br></strong></small>");
        buf.append("<br>The sky is ");
        buf.append(weather.getSky());
        if (weather.getPrecipitation().length()>0)
            buf.append(", with ").append(weather.getPrecipitation());
        buf.append(".")
            .append("<br>Temperature is ").append(weather.getTemperature()>-99?weather.getTemperature()+" degrees.":"not available.");
        if (weather.isWindChillAvailable())
            buf.append("<br>Wind chill is ").append(weather.getWindChill()).append(" degrees.");
        if (weather.getDewpoint()>-99)
            buf.append("<br>Dew point is ").append(weather.getDewpoint()>-99?weather.getDewpoint()+" degrees.":"not available.");
        if (weather.getDewpoint()>-99 && weather.getTemperature()>-99)
        	buf.append("<br>Relative humidity is ").append(weather.getHumidity()).append(" percent.");
        if (weather.getWindSpeed()>0)
            {
            if ("variable".equalsIgnoreCase(weather.getWindDirectionText()))
                buf.append("<br>Winds are variable");
            else
                buf.append("<br>Wind is out of the ").append(weather.getWindDirectionText());
            buf.append(" at ").append(weather.getWindSpeed()).append(" knots");
            if (weather.getWindGusts()>0)
                buf.append(", with gusts up to ").append(weather.getWindGusts()).append(" knots.");
            else buf.append(".");
            }
        else buf.append("<br>Winds are calm.");
        buf.append("<br>Barometric pressure is ").append(weather.getBarometer()).append(weather.getBarometer()<20?" millibars":" inches of mercury");
        if (weather.getBarometerDirection().length()>0)
            buf.append(" and ").append(weather.getBarometerDirection());
        buf.append(".");
        if (weather.isVisibilityAvailable())
            {
            buf.append("<br>Visibility is ");
            if ((weather.getVisibilityUnits().equalsIgnoreCase("miles") && weather.getVisibility()==10)
            	||(weather.getVisibilityUnits().equalsIgnoreCase("meters") && weather.getVisibility()==9999))
            	buf.append("unlimited.");
            else
            	buf.append(weather.getVisibility()).append(" ").append(weather.getVisibilityUnits()).append(".");
            }
        if (weather.getRemarks()!=null && !weather.getRemarks().startsWith("WCI") && !weather.getRemarks().startsWith("VSB") && weather.getRemarks().length()>0)
            buf.append("<br>").append(weather.getRemarks());
        buf.append("<br>There is a ").append(getMoonType()).append(" moon.");
        buf.append("<br><div align=right><small><small>Fetched at ").append(updateTime).append("</small></small></div>");
        buf.append("</html>");
        setToolTipText(buf.toString());
        }

    private String getMoonType()
        {
        float age=calculateMoonAge();
        float phase=MOON_MONTH/32;
        if (age<phase*2 || age>phase*30) return "new";
        else if (age<phase*7) return "waxing crescent";
        else if (age<phase*9) return "first quarter";
        else if (age<phase*14) return "waxing gibbous";
        else if (age<phase*18) return "full";
        else if (age<phase*23) return "waning gibbous";
        else if (age<phase*25) return "last quarter";
        else if (age<phase*30) return "waning crescent";
        else return "";
        }

    /**
     * Calculate the moon phase.  This algorithm is based on the fact
     * that there was a new moon on December 31, 2005.
     *
     * @return int representing percentage of moon illuminated. (0-100)
     */
    public float calculateMoonAge()
        {
        Calendar newMoon=Calendar.getInstance();
        newMoon.clear();
        newMoon.set(2005,11,31,03,12,00); //update if we find more accurate data
        Calendar today=Calendar.getInstance();
        float diff=(float)(today.getTimeInMillis()-newMoon.getTimeInMillis())/86400000.0f;//convert to days
        return diff%MOON_MONTH; //synodic month is 29.5306 days
        }


    public void paint(Graphics g)
        {
        super.paint(g);
        if (isFoggy())
            drawFog((Graphics2D)g);
        if (isRaining())
            drawRain((Graphics2D)g);
        if (isSnowing())
            drawSnow((Graphics2D)g);
        if (isLightning())
            drawLightning((Graphics2D)g);
        if (isError())
            {
            g.setFont(new Font("Times New Roman",Font.BOLD, getNumeralFontSize()*2));
            g.setColor(Color.RED);
            g.drawString("!", (getWidth()/2)-(g.getFontMetrics().charWidth('!')/2), getHeight()/3);
            }
        //redraw the minimoon if it is outside the bezel
        if (!new Ellipse2D.Double(0,0,getWidth(),getHeight()).contains(getMiniMoonArea()))
        	{
        	g.setClip(null); //clear clipping area
        	moonFace((Graphics2D)g, false);
        	}
        }

    protected void drawFace(Graphics2D g)
        {
        g.clip(new Ellipse2D.Double(0,0,getWidth(),getHeight()));
        if (isFaceImage())
        	{
            getMoon().setVisible(false);
        	imageFace(g);
        	}
        else
            moonFace(g,true);
//            getMoon().setVisible(true);
        }

    private void imageFace(Graphics2D g)
        {
        setBackgroundTransparent(true);
        if (faceImageFetchStage<2)
            {
            if (oldFaceImg!=null)
            	g.drawImage(oldFaceImg, 0, 0, getWidth(), getHeight(), null);
            moonFace(g,false);
            g.setColor(Color.BLACK);
            g.setXORMode(Color.WHITE);
            g.drawString("Loading Image...", getWidth()/3, getHeight()/4);
            g.setPaintMode();
            }
        if (faceImageFetchStage==0)
        	synchronized(getWeather())
			{
			if (faceImageFetchStage==0)
				{
	            faceImageFetchStage=1; //fetching
	            
	            if (isRandomImage())
	        		setImageURL(getImageURLList()[(int)Math.round(Math.random()*(getImageURLList().length-1))]);

	            Thread imageFetcher=getImageFetcher();
	            if (imageFetcher!=null) imageFetcher.start();
				}    
            }
        else if (faceImageFetchStage==1)
            {
            if (oldFaceImg!=null)
            	g.drawImage(oldFaceImg, 0, 0, getWidth(), getHeight(), null);
            moonFace(g,false);
            return;  //new image not ready to draw yet
            }
        else
            {
            faceImageFetchStage=0; //got the image, reset everything
            if (faceImg!=null)
                {
                g.drawImage(faceImg, 0, 0, getWidth(), getHeight(), null);
                moonFace(g,false);
                }
            }
        }

    private Thread getImageFetcher()
    	{
    	if (!outFetching)
    		{
    		outFetching=true;
	        return new Thread()
		        {
		        public void run()
		            {
		            try
		                {
		                System.out.println(timeStamp()+"Fetching face image from "+getImageURL()+".");
		                URL url=new URL(getImageURL());
		                faceImg=Toolkit.getDefaultToolkit().createImage(url);
		                int timeout=0;
		                while (!Toolkit.getDefaultToolkit().prepareImage(faceImg,-1,-1,null) && timeout++<10)
		                    {
		                    try{Thread.sleep(1000);}
		                    catch (InterruptedException e){}
		                    }
		                if (faceImg.getWidth(null)>0)
		                    {
		                    faceImageFetchStage=2;  //got the image
		                    oldFaceImg=faceImg;
		                    setFaceValid(false); //it's ready to draw
		                    repaint();
		                    }
		                else
		                    {
		                    faceImageFetchStage=0;  //try again from the top
		                    setFaceValid(false); //redo next time around
		                    }
		                }
		            catch (MalformedURLException e)
		                {
		                e.printStackTrace();
		                System.out.println(timeStamp()+"Failed fetching new image.");
		                faceImageFetchStage=0; //reset to try again
		                }
		            finally
			            {
			            outFetching=false;
			            }
		            }
		        };
    		}
		else return null;
    	}
    
    
    private void moonFace(Graphics2D g, boolean fullFace)
        {
        if (!fullFace && !isDrawMiniMoon())
        	return;
        g.setColor(getFaceColor());
		int width=getWidth();
		int height=getHeight();
        int w=getBezelWidth();
        int xOffset=0;
        int yOffset=0;
        if (!fullFace) //draw mini-moon?
        	{
        	width=getMiniMoonArea().width;
        	height=getMiniMoonArea().height;
        	w=0;
        	xOffset=getMiniMoonArea().x;
        	yOffset=getMiniMoonArea().y;
        	}
        float age=calculateMoonAge();
        Color color1=getFaceColor();
        Color color2=getFaceColor().brighter();
        Color originalColor=getFaceColor();
        float qm=MOON_MONTH/4;
        int quarter=qm>age?1:qm*2>age?2:qm*3>age?3:4;
        if (quarter==1 || quarter==4)
            {
            color2=color1;
            color1=color1.brighter();
            }
        setFaceColor(color1);

       	super.drawFace(g);
        Shape originalClip=g.getClip();
        if (!fullFace)
        	{
        	g.clip(new Ellipse2D.Double(xOffset,
				 yOffset,
				 width,height));
        	g.setColor(color1);
        	g.fillRect(xOffset, yOffset, width, height);
        	}
        g.setColor(color2);
        int ovWidth=Math.abs((int)(Math.cos((age/MOON_MONTH)*(Math.PI*2d))*width));
        g.fillOval(xOffset+(width-ovWidth)/2,
            yOffset+w-1,
            ovWidth,
            height+2-w*2);

        g.fillRect(xOffset+((quarter==1||quarter==3)?0:width/2), yOffset, width/2, height);
        setFaceColor(originalColor);
        g.setClip(originalClip);
        }

    protected void drawNumerals(Graphics2D g)
        {
        g.setXORMode(getFaceColor());
        super.drawNumerals(g);
        g.setPaintMode();
        if (getWeather().getWindSpeed()>0)
            drawCompass(g);
        }

    protected void drawRain(Graphics2D g)
        {
        g.clip(new Ellipse2D.Double(0,0,getWidth(),getHeight()));
//        if (!isFaceValid()) //need to recalculate drops
//            {
//            dryOut();
//            inform(getWeather());
//            }

        if (rainDrops!=null)
            for (Iterator drips=rainDrops.iterator();drips.hasNext();)
                ((RainDrop)drips.next()).paint(g);
        }

    protected void drawFog(Graphics2D g)
        {
        g.clip(new Ellipse2D.Double(0+getBezelWidth(),0+getBezelWidth(),
        							getWidth()-getBezelWidth()*2,getHeight()-getBezelWidth()*2));
//        if (!isFaceValid()) //need to recalculate fog banks
//            {
//            fogBanks.clear();
//            inform(getWeather());
//            }

        if (fogBanks!=null)
            {
            for (Iterator banks=fogBanks.iterator();banks.hasNext();)
                ((FogBank)banks.next()).paint(g);
            }
        }

    protected void drawSnow(Graphics2D g)
        {
        g.clip(new Ellipse2D.Double(0+getBezelWidth(),0+getBezelWidth(),
			getWidth()-getBezelWidth()*2,getHeight()-getBezelWidth()*2));
//        if (!isFaceValid()) //need to recalculate drops
//            {
//            snowFlakes.clear();
//            inform(getWeather());
//            }

        if (snowFlakes!=null)
            for (Iterator flakes=snowFlakes.iterator();flakes.hasNext();)
                ((SnowFlake)flakes.next()).paint(g);
        }

    protected void drawLightning(Graphics2D g)
        {
        g.clip(new Ellipse2D.Double(0+getBezelWidth(),0+getBezelWidth(),
			getWidth()-getBezelWidth()*2,getHeight()-getBezelWidth()*2));

        if (lightningBolts!=null)
            for (Iterator bolts=lightningBolts.iterator();bolts.hasNext();)
                ((LightningBolt)bolts.next()).paint(g);
        }

    protected void drawCompass(Graphics2D g)
        {
        g.setColor(getCompassColor());
        g.setFont(new Font(getCompassFontFace(),getCompassFontStyle(), getCompassFontSize()));

        drawCompassPoint(g, 0, "E");
        drawCompassPoint(g, Math.PI/2, "S");
        drawCompassPoint(g, Math.PI, "W");
        drawCompassPoint(g, Math.PI/2d*3d, "N");
        }

    /**
     * @param g
     * @param polar
     * @param point
     */
    private void drawCompassPoint(Graphics2D g, double polar, String point)
        {
        //point=":";
        Rectangle2D bounds=g.getFontMetrics().getStringBounds(point, g);//.getBounds2D();
        int compassXRadius=(int)(getWidth()/2-bounds.getWidth()/2)-getBezelWidth();
        int compassYRadius=(int)(getHeight()/2-bounds.getHeight()/2)-getBezelWidth();
        double x=(compassXRadius*Math.cos(polar))+getWidth()/2-bounds.getCenterX();
        double y=(compassYRadius*Math.sin(polar))+getHeight()/2-bounds.getCenterY();
        g.drawString(point, (int)x, (int)y);
//          g.drawRect( (int)x, (int)y, (int)bounds.getWidth(), (int)bounds.getHeight());
        }

    //show the wind by changing the color and size of a triangle in
    //the ring of minute dots.
    protected void drawMinuteMarks(Graphics2D g)
        {
        //draw the minute marks just like the superclass, but don't do the
        //dots at the compass points
        g.setColor(getMinuteColor());
        int xRadius=(getWidth()/2-getBezelWidth()-getMinuteRadius()*4);
        int yRadius=(getHeight()/2-getBezelWidth()-getMinuteRadius()*4);
        int ticks=0;
        for (double polar=0;
            ticks++<60;
            polar+=0.10472) //0.10472=2*pi/60
            {
            if (getWeather().getWindSpeed()>0 && (ticks-1)%15==0)
                continue; //Replace dots with compass points if wind blowing
            int dot=getMinuteRadius();
            if ((((double)ticks-1)%5d)==0)
                dot*=3; //heavy dot every 5 minutes
            double x=(xRadius*Math.cos(polar)+getWidth()/2)-dot/2;
            double y=(yRadius*Math.sin(polar)+getHeight()/2)-dot/2;
            g.fillOval((int)x, (int)y, dot, dot);
            }

        if (getWeather().getWindDirection()>=0)
            {
            //draw the wind speed triangle
            drawTriangle(g, getWeather().getWindSpeed(), false);

            //draw the wind gusts triangle
            drawTriangle(g, getWeather().getWindGusts(), true);
            }
        }

    private void drawTriangle(Graphics2D g, int speed, boolean dashed)
        {
        double size=getIndicatorSize(speed);
        double polar=Math.toRadians((double)getWeather().getWindDirection());
        int xRadius=(int)(getWidth()/2-getBezelWidth()-getMinuteRadius()*4-size/2);
        int yRadius=(int)(getHeight()/2-getBezelWidth()-getMinuteRadius()*4-size/2);
        double x=(xRadius*Math.cos(polar)+getWidth()/2);
        double y=(yRadius*Math.sin(polar)+getHeight()/2);
        Shape triangle=buildWindIndicator(polar, (int)x, (int)y, (int)size);
        if (dashed)
            {
            Stroke oldStroke=g.getStroke();
            g.setStroke(new BasicStroke(2.0f,
                 BasicStroke.CAP_BUTT,
                 BasicStroke.JOIN_MITER,
                 3f, new float[]{3f}, 0.0f));
            g.setColor(Color.MAGENTA);
            g.draw(triangle);
            g.setStroke(oldStroke);
            }
        else
            {
            g.setColor(Color.RED);
            g.fill(triangle);
            }
        }

    private double getIndicatorSize(int speed)
        {
        double baseSize=Math.max(1.3, getMinuteRadius())*2; //base triangle size=2x minute dot
        return baseSize*(speed/2); //scale size for wind speed
        }

    protected double getWindDirectionAngle()
        {
        double polar=0;
        String windDir=getWeather().getWindDirectionText();
        if (windDir.equalsIgnoreCase("e"))
            polar=0;
        else if (windDir.equalsIgnoreCase("se"))
            polar=2*Math.PI*(1/8d);
        else if (windDir.equalsIgnoreCase("s"))
            polar=2*Math.PI*(2/8d);
        else if (windDir.equalsIgnoreCase("sw"))
            polar=2*Math.PI*(3/8d);
        else if (windDir.equalsIgnoreCase("w"))
            polar=2*Math.PI*(4/8d);
        else if (windDir.equalsIgnoreCase("nw"))
            polar=2*Math.PI*(5/8d);
        else if (windDir.equalsIgnoreCase("n"))
            polar=2*Math.PI*(6/8d);
        else if (windDir.equalsIgnoreCase("ne"))
            polar=2*Math.PI*(7/8d);
        else return -1;
        return polar;
        }



    private Shape buildWindIndicator(double angle, int xOffset, int yOffset, int size)
        {
        //build the base triangle at zero degrees
        int[] x=new int[3];
        int[] y=new int[3];
        x[0]=size/2;
        y[0]=0-size/2;
        x[1]=size/2;
        y[1]=size/2;
        x[2]=0-size/2;
        y[2]=0;
        Shape pointer=new Polygon(x,y,3);
        AffineTransform tr=new AffineTransform();
        tr.setToRotation(angle);
        pointer=tr.createTransformedShape(pointer);
        tr.setToTranslation(xOffset, yOffset);
        pointer=tr.createTransformedShape(pointer);
        return pointer;
        }

    public void componentHidden(ComponentEvent e){} //who cares

    public void componentMoved(ComponentEvent e){} //who cares

    public void componentResized(ComponentEvent e)
        {
        oldHeight=newHeight;
        oldWidth=newWidth;
        newWidth=getWidth();
        newHeight=getHeight();
        updateMiniMoon();
        setFaceValid(false);
        }

    private void updateMiniMoon()
		{
		setMiniMoonArea(sizeMiniMoon());
		
		}

	public void componentShown(ComponentEvent e){} //who cares

    public void setFaceColor(Color faceColor)
        {
        super.setFaceColor(faceColor);
//        dryOut(); //make potential rain drops the right color
//        assessSky();
        }

    public boolean isFaceImage()
        {
        return faceImage;
        }

    public void setFaceImage(boolean faceImage)
        {
        this.faceImage=faceImage;
        setBackgroundTransparent(faceImage);
        }

    public String getImageURL()
        {
        return imageURL;
        }

    public void setImageURL(String imageURL)
        {
        this.imageURL=imageURL;
        }

    public boolean isRaining()
        {
        return raining;
        }

    public void setRaining(boolean raining)
        {
        this.raining=raining;
        }

    public String translateCountry(String countryCode)
        {
        return (String)getWeather().getCountryList().get(countryCode);
        }

    public boolean isError()
        {
        return error;
        }

    public void setError(boolean error)
        {
        this.error=error;
        }

    /**
     * Replaces all occurrances of a string <b>key</b>
     *  within another string <b>original</b> with a third string <b>text</b>.
     * <br>Not case sensitive. <B>text</B> may be null.
     *
     * @return java.lang.String
     */
    public final static String substitute(String original, String key, String text)
        {
        if (original!=null && key!=null)
            {
            text=(text==null?"":text);
            String uOriginal=original.toUpperCase();
            StringBuffer modified=new StringBuffer();
            key=key.toUpperCase();
            int adjustment=key.length();
            int keyLocation;
            int startLocation=0;
            while ((keyLocation=uOriginal.indexOf(key,startLocation))>=0)
                {
                modified.append(original.substring(startLocation,keyLocation));
                modified.append(text);
                startLocation=keyLocation+adjustment;
                }
            original=modified.toString()+original.substring(startLocation);
            }
        return original;
        }

    public boolean isSnowing()
        {
        return snowing;
        }

    public void setSnowing(boolean snowing)
        {
        this.snowing=snowing;
        }

	public Properties getSettings()
		{
		return settings;
		}

	protected Moon getMoon()
		{
		if (moon==null)
			{
			moon=new Moon();
			moon.setDarkSideColor(getFaceColor());
			moon.setBounds(new java.awt.Rectangle(99,5,1,1));
			moon.setLightSideColor(getFaceColor().brighter());
			moon.setSize(getWidth()-getBezelWidth()*2, getHeight()-getBezelWidth()*2);
			}
		return moon;
		}

	public boolean isFoggy()
		{
		return foggy;
		}

	public void setFoggy(boolean foggy)
		{
		this.foggy=foggy;
		}

	public boolean isLightning()
		{
		return lightning;
		}

	public void setLightning(boolean lightning)
		{
		this.lightning=lightning;
		}

	public String[] getImageURLList()
		{
		if (imageURLList==null)
			imageURLList=new String[]{getImageURL()};
		return imageURLList;
		}

	public void setImageURLList(String[] imageURLList)
		{
		this.imageURLList=imageURLList;
		if (isRandomImage())
			setImageURL(getImageURLList()[(int)Math.round(Math.random()*(getImageURLList().length-1))]);
		}

	public boolean isRandomImage()
		{
		return randomImage;
		}

	public void setRandomImage(boolean randomImage)
		{
		this.randomImage=randomImage;
		}

	public void setDrawMiniMoon(boolean drawMiniMoon)
		{
		this.drawMiniMoon=drawMiniMoon;
		}

	/**
	 * @return Returns the drawMiniMoon.
	 */
	public boolean isDrawMiniMoon()
		{
		return drawMiniMoon;
		}

	/**
	 * @return Returns the miniMoonArea.
	 */
	public Rectangle getMiniMoonArea()
		{
		return miniMoonArea;
		}

	/**
	 * @param miniMoonArea The miniMoonArea to set.
	 */
	public void setMiniMoonArea(Rectangle miniMoonArea)
		{
		this.miniMoonArea=miniMoonArea;
		}

	public void mouseClicked(MouseEvent e){}

	public void mouseEntered(MouseEvent e){}

	public void mouseExited(MouseEvent e){}

	public void mousePressed(MouseEvent e)
		{
		int mousex=e.getPoint().x;
		int mousey=e.getPoint().y;
		Rectangle moon=getMiniMoonArea();
		dragMoonOffsetX=mousex-moon.x;
		dragMoonOffsetY=mousey-moon.y;
		draggingMiniMoon=	mousex >=moon.x &&
							mousex <=(moon.x+moon.width) &&
							mousey >=moon.y &&
							mousey <=(moon.y+moon.height);
		}

	public void mouseReleased(MouseEvent e)
		{
		setMiniMoonLocation(e);
		draggingMiniMoon=false;
		}

	private void setMiniMoonLocation(MouseEvent e)
		{
		if (draggingMiniMoon)
			{
			getMiniMoonArea().x=Math.max(0,e.getPoint().x-dragMoonOffsetX);
			getMiniMoonArea().y=Math.max(0,e.getPoint().y-dragMoonOffsetY);
			getMiniMoonArea().x=Math.min(getWidth()-getMiniMoonArea().width,getMiniMoonArea().x);
			getMiniMoonArea().y=Math.min(getHeight()-getMiniMoonArea().height,getMiniMoonArea().y);
			setFaceValid(false);
			}
		}

	public void mouseDragged(MouseEvent e)
		{
		if (draggingMiniMoon)
			{
			setMiniMoonLocation(e);
			repaint();
			}
		}

	public void mouseMoved(MouseEvent e)
		{
		// TODO Auto-generated method stub
		
		}

//  /* (non-Javadoc)
//   * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
//   */
//  public void hyperlinkUpdate(HyperlinkEvent evt)
//      {
//      if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
//        try {
//          pane.setPage(evt.getURL());
//        }
//        catch (Exception e) {
//        }
//          if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
//          showMessageDialog(null, "You click the link with the URL " + e.getURL());
//      }
//      else if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED) {
//          tooltip = editorPane.getToolTipText();
//          editorPane.setToolTipText(evt.getURL().toExternalForm());
//      } else if (evt.getEventType() == HyperlinkEvent.EventType.EXITED) {
//          //something when leaving
//      }
//      }


    } // @jve:decl-index=0:visual-constraint="10,10"
