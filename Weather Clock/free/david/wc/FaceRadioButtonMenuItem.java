package free.david.wc;

import javax.swing.*;

public class FaceRadioButtonMenuItem extends JRadioButtonMenuItem
	{
	private String faceURL="";
	
	public FaceRadioButtonMenuItem()
		{
		super();
		}

	public FaceRadioButtonMenuItem(String text)
		{
		super(text);
		}

	public FaceRadioButtonMenuItem(String text, String faceURL)
		{
		super(text);
		setFaceURL(faceURL);
		}

	public String getFaceURL()
		{
		return faceURL;
		}

	public void setFaceURL(String faceURL)
		{
		this.faceURL=faceURL;
		}

	}
