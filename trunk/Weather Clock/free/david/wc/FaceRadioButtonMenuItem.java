package free.david.wc;

import javax.swing.*;

public class FaceRadioButtonMenuItem extends JRadioButtonMenuItem
	{
	private String faceURL="";
	private String gotoURL="";
	
	public FaceRadioButtonMenuItem()
		{
		super();
		}

	public FaceRadioButtonMenuItem(String text)
		{
		super(text);
		}

	public FaceRadioButtonMenuItem(String text, String faceURL, String gotoURL)
		{
		super(text);
		setFaceURL(faceURL);
		setGotoURL(gotoURL);
		}

	public String getFaceURL()
		{
		return faceURL;
		}

	public void setFaceURL(String faceURL)
		{
		this.faceURL=faceURL;
		}

	/**
	 * @return Returns the gotoURL.
	 */
	public String getGotoURL()
		{
		return gotoURL;
		}

	/**
	 * @param gotoURL The gotoURL to set.
	 */
	public void setGotoURL(String gotoURL)
		{
		this.gotoURL=gotoURL;
		}

	}
