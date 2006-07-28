package free.david.wc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.Calendar;

import javax.swing.JPanel;

public class Moon extends JPanel
	{
    public static final float MOON_MONTH=29.5306f;//synodic month is 29.5306 days
	private Color darkSideColor=Color.GRAY;
	private Color lightSideColor=Color.WHITE;
	private Calendar calendar; 
	
	/**
	 * This is the default constructor
	 */
	public Moon()
		{
		super();
		initialize();
		}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
		{
		this.setLayout(null);
		this.setSize(200, 200);
		this.setBackground(new Color(255,255,255,0)); //transparent background
		}

	public Calendar getCalendar()
		{
		return calendar;
		}

	public void setCalendar(Calendar calendar)
		{
		this.calendar=calendar;
		}

	public Color getDarkSideColor()
		{
		return darkSideColor;
		}

	public void setDarkSideColor(Color darkSideColor)
		{
		this.darkSideColor=darkSideColor;
		}

	public Color getLightSideColor()
		{
		return lightSideColor;
		}

	public void setLightSideColor(Color lightSideColor)
		{
		this.lightSideColor=lightSideColor;
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
    	Calendar today=getCalendar();
        if (today==null)
        	today=Calendar.getInstance();
        float diff=(float)(today.getTimeInMillis()-newMoon.getTimeInMillis())/86400000.0f;//convert to days
        return diff%MOON_MONTH; //synodic month is 29.5306 days
        }

    public String getMoonType()
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
    

	public void paint(Graphics gg)
        {
        Graphics2D g=(Graphics2D)gg;
		int width=getWidth();
		int height=getHeight();
        int xOffset=0;
        int yOffset=0;
        float age=calculateMoonAge();
        Color color1=getDarkSideColor();
        Color color2=getLightSideColor();
        float qm=MOON_MONTH/4;
        int quarter=qm>age?1:qm*2>age?2:qm*3>age?3:4;
        if (quarter==1 || quarter==4)
            {
            Color temp=color2;
            color2=color1;
            color1=temp;
            }
        Shape originalClip=g.getClip();
    	g.clip(new Ellipse2D.Double(xOffset,
   				 yOffset,
   				 width,height));
       	g.setColor(color1);
        g.fillRect(xOffset, yOffset, width, height);
        g.setColor(color2);
        int ovWidth=Math.abs((int)(Math.cos((age/MOON_MONTH)*(Math.PI*2d))*width));
        g.fillOval(xOffset+(width-ovWidth)/2,
            yOffset,
            ovWidth,
            height+2);

        g.fillRect(xOffset+((quarter==1||quarter==3)?0:width/2), yOffset, width/2, height);
        g.setClip(originalClip);
        }

	}  //  @jve:decl-index=0:visual-constraint="10,10"
