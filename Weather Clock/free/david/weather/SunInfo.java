package free.david.weather;
import java.util.Calendar;

public class SunInfo
	{
	private int sunriseHour;
	private int sunriseMinute;
	private int sunsetHour;
	private int sunsetMinute;
	private int dayLengthHour;
	private int dayLengthMinute;
	private int month;
	private int day;
	private int year;
	private int offsetFromGMT=-5;
	private double latitude;
	private double longitude;
	private final double	pi		=Math.PI;
	private final double	degrees	=180/pi;
	private final double	radians	=pi/180;

	public SunInfo()
		{
		super();
		}
	
	public SunInfo(int month, int day, int year, double latitude, double longitude)
		{
		super();
		this.month=month;
		this.day=day;
		this.year=year;
		this.latitude=latitude;
		this.longitude=longitude;
//		calculate();
		}

	public SunInfo(double latitude, double longitude)
		{
		super();
		Calendar c=Calendar.getInstance();
		this.month=c.get(Calendar.MONTH)+1;
		this.day=c.get(Calendar.DAY_OF_MONTH);
		this.year=c.get(Calendar.YEAR);
		this.latitude=latitude;
		this.longitude=longitude;
//		calculate();
		}

	/**
	 * @param args
	 */
	public static void main(String[] args)
		{
		SunInfo sr=new SunInfo();
		Calendar c=Calendar.getInstance();
		System.out.println("For "+c.getTime().toString()+":");
		System.out.println("Rise: "+sr.getSunRiseSet(36.19, -86.78, sr.getOffsetFromGMT(), true, c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH)));
		System.out.println("Set: "+sr.getSunRiseSet(36.19, -86.78, sr.getOffsetFromGMT(), false, c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH)));

		}

	// ':::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	// '::: Returns an angle in range of 0 to (2 * pi) :::
	// ':::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	private double getRange(double x)
		{
		double temp1=x/(2*pi);
		double temp2=(2*pi)*(temp1-Math.floor(temp1));
		if (temp2<0) temp2=(2*pi)+temp2;
		return temp2;

		}

	// ':::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	// '::: Returns 24 hour time from decimal time :::
	// ':::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	private String getMilitaryTime(double decimalTime, int gmtOffset)
		{
		// Handle 24-hour time wrap
		if (decimalTime+gmtOffset<0) decimalTime=decimalTime+24;
		if (decimalTime+gmtOffset>24) decimalTime=decimalTime-24;

		double temp1=Math.abs(decimalTime+gmtOffset);
		int temp2=(int)temp1;
		temp1=60*(temp1-temp2);
		temp1=temp2*100+temp1+.5;
		String temp3="0000"+((int)Math.floor(temp1));
		temp3=temp3.substring(temp3.length()-4);
		return temp3.substring(0, 2)+":"+temp3.substring(2);
		}

	// This routine does all the real work 
	public String getSunRiseSet(double latitude, 
								double longitude,
								int zoneRelativeGMT,
								boolean rise,
								int year, int month, int day)
		{
		if (Math.abs(latitude)>63){ return "{invalid latitude}"; }

		// An altitude of -0.833 is generally accepted as the angle of
		// the sun at which sunrise/sunset occurs. It is not exactly
		// zero because of refraction effects of the earth's atmosphere
		// and because the sun is not a point source.
		double altitude=-0.833;

		int rs=rise?1:-1;

		double Ephem2000Day=367*year-7*(year+(month+9)/12)/4+275*month/9+day-730531.5;

		double utold=pi;
		double utnew=0;
		double sinalt=Math.sin(altitude*radians);// solar altitude
		double sinphi=Math.sin(latitude*radians);// viewer's latitude
		double cosphi=Math.cos(latitude*radians);
		longitude=longitude*radians; // viewer's longitude

		int ct=0;
		while (Math.abs(utold-utnew)>.001&&ct++<35)
			{
			utold=utnew;
			double days=Ephem2000Day+utold/(2*pi);
			double t=days/36525;
			// These 'magic' numbers are orbital elements of the sun, and should
			// not be changed
			double l=getRange(4.8949504201433+628.331969753199*t);
			double g=getRange(6.2400408+628.3019501*t);
			double ec=.033423*Math.sin(g)+.00034907*Math.sin(2.*g);
			double lambda=l+ec;
			double e=-1*ec+.0430398*Math.sin(2.*lambda)-.00092502*Math.sin(4.*lambda);
			double obl=.409093-.0002269*t;

			// Obtain ASIN of (SIN(obl) * SIN(lambda))
			double delta=Math.sin(obl)*Math.sin(lambda);
			delta=Math.atan(delta/(Math.sqrt(1-delta*delta)));

			double gha=utold-pi+e;
			double cosc=(sinalt-sinphi*Math.sin(delta))/(cosphi*Math.cos(delta));
			double correction=0;
			if (cosc>1)
				correction=0;
			else if (cosc<-1)
				correction=pi;
			else
				correction=Math.atan((Math.sqrt(1-cosc*cosc))/cosc);

			utnew=getRange(utold-(gha+longitude+rs*correction));
			}

//		return getMilitaryTime(utnew*degrees/15, zoneRelativeGMT);
		return getMilitaryTime((utnew*degrees/15)-(rs*12), zoneRelativeGMT); //off by 12 hrs for some reason. DEP
		}

	public int getDay()
		{
		return day;
		}

	public void setDay(int day)
		{
		this.day=day;
		}

	public int getDayLengthHour()
		{
		return dayLengthHour;
		}

	public void setDayLengthHour(int dayLengthHour)
		{
		this.dayLengthHour=dayLengthHour;
		}

	public int getDayLengthMinute()
		{
		return dayLengthMinute;
		}

	public void setDayLengthMinute(int dayLengthMinute)
		{
		this.dayLengthMinute=dayLengthMinute;
		}

	public double getDegrees()
		{
		return degrees;
		}

	public double getLatitude()
		{
		return latitude;
		}

	public void setLatitude(double latitude)
		{
		this.latitude=latitude;
		}

	public double getLongitude()
		{
		return longitude;
		}

	public void setLongitude(double longitude)
		{
		this.longitude=longitude;
		}

	public int getMonth()
		{
		return month;
		}

	public void setMonth(int month)
		{
		this.month=month;
		}

	public int getSunriseHour()
		{
		return sunriseHour;
		}

	public void setSunriseHour(int sunriseHour)
		{
		this.sunriseHour=sunriseHour;
		}

	public int getSunriseMinute()
		{
		return sunriseMinute;
		}

	public void setSunriseMinute(int sunriseMinute)
		{
		this.sunriseMinute=sunriseMinute;
		}

	public int getSunsetHour()
		{
		return sunsetHour;
		}

	public void setSunsetHour(int sunsetHour)
		{
		this.sunsetHour=sunsetHour;
		}

	public int getSunsetMinute()
		{
		return sunsetMinute;
		}

	public void setSunsetMinute(int sunsetMinute)
		{
		this.sunsetMinute=sunsetMinute;
		}

	public int getYear()
		{
		return year;
		}

	public void setYear(int year)
		{
		this.year=year;
		}

	public int getOffsetFromGMT()
		{
		return offsetFromGMT;
		}

	public void setOffsetFromGMT(int offsetFromGMT)
		{
		this.offsetFromGMT=offsetFromGMT;
		}

	}