package free.david.wc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;

public class FaceMenu extends JMenu implements ActionListener, PropertyChangeListener
	{
	ButtonGroup imageGroup=new ButtonGroup();
	private JDialog newImagejDialog = null;  //  @jve:decl-index=0:visual-constraint="245,10"
	private JPanel newImageDialog = null;
	private JTextField newImageTextField = null;
	private JButton newImageOKButton = null;
	private Properties props;
	private JFrame mainWindow;
	private boolean somethingEntered=false;
	private JPanel namePanel = null;
	private JLabel nameLabel = null;
	private JTextField newImageNameTextField = null;
	private JPanel urlPanel = null;
	private JPanel okPanel = null;
	private JLabel urlLabel = null;
	private String selectedImageURL="";
	private boolean randomImage=false;
	
	public FaceMenu()
		{
		super();
		}

	public FaceMenu(String s)
		{
		super(s);
		}

	public FaceMenu(String s, boolean b)
		{
		super(s, b);
		}

	public FaceMenu(Action a)
		{
		super(a);
		}

	public FaceMenu(Properties props, JFrame mainWindow)
		{
		super();
		setMainWindow(mainWindow);
		initialize(props);
		}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize(Properties props)
		{
		setProps(props);
		setText("Face");
		populate(props);
		}

	public void populate(Properties props)
		{
		JMenu currentMenu=this;
		int itemCount=0;
		removeAll();
		
		//Add the "new image" option
		JMenuItem mi=new JMenuItem("New...");
		mi.addActionListener(this);
		mi.setActionCommand("newImage");
		currentMenu.add(mi);

		//Add the "random image" option
		JRadioButtonMenuItem ri=new JRadioButtonMenuItem("Random Selection");
		ri.addActionListener(this);
		ri.setActionCommand("randomImage");
		getImageGroup().add(ri);
		if (props.getProperty("useImage","").equals("Random Image"))
			{
			ri.setSelected(true);
			setRandomImage(true);
			}
		currentMenu.add(ri);

		//Moon is special case
		JRadioButtonMenuItem m=new JRadioButtonMenuItem("The Moon");
		m.addActionListener(this);
		m.setActionCommand("moon");
		getImageGroup().add(m);
		if (props.getProperty("useImage","The Moon").equals("The Moon"))
			m.setSelected(true);
		currentMenu.add(m);

		//Get all other image items from the ini file
		for (Enumeration i=props.propertyNames();i.hasMoreElements();)
			{
			String key=(String)i.nextElement();
			if (key.startsWith("imageChoice"))
				{
				String face=props.getProperty(key);
				String faceName=null;
				int urlEnd=face.indexOf(" ");
				if (urlEnd>0) 
					{
					faceName=face.substring(urlEnd).trim();
					face=face.substring(0, urlEnd);
					}
				else faceName=face; //use the URL if no name given
				m=new FaceRadioButtonMenuItem(faceName, face);
				currentMenu.add(m);
				getImageGroup().add(m);
				m.addActionListener(this);
				m.setActionCommand("faceImage");
				m.setToolTipText(face);
				if (props.getProperty("useImage","none").equalsIgnoreCase(face))
					m.setSelected(true);
				if (++itemCount>15 && i.hasMoreElements())
					{
					itemCount=0;
					JMenu newMenu=new JMenu("More");
					currentMenu.add(newMenu);
					currentMenu=newMenu;
					}
				}
			}
		
		}
	
	public void actionPerformed(ActionEvent e)
		{
		if (e.getActionCommand().equals("newImage"))
			{
			//open a dialog for the new image
			somethingEntered=false;
			getNewImagejDialog().setLocation(getMainWindow().getX()+20, getMainWindow().getY()+20);
			getNewImagejDialog().setVisible(true);
	
			if (somethingEntered)
				{
				//commandeer this event and propagate it to our listeners
				tellEveryone(e);
				}
			}
		else if (e.getActionCommand().equals("faceImage"))
			{
			setSelectedImageURL(((FaceRadioButtonMenuItem)e.getSource()).getFaceURL());
			tellEveryone(e);
			}
		else if (e.getActionCommand().equals("randomImage"))
			{
			setRandomImage(true);
			tellEveryone(e);
			}
		else if (e.getActionCommand().equals("moon"))
			{
			setSelectedImageURL("The Moon");
			tellEveryone(e);
			}
		}

	/**
	 * @param e
	 */
	private void tellEveryone(ActionEvent e)
		{
		e.setSource(this);
		ActionListener[] als=(ActionListener[])getListeners(ActionListener.class);
		for (int i=0;i<als.length;i++)
			als[i].actionPerformed(e);
		}

	public ButtonGroup getImageGroup()
		{
		return imageGroup;
		}

	public void setImageGroup(ButtonGroup imageGroup)
		{
		this.imageGroup=imageGroup;
		}

	public String[] getAllImageURLs()
		{
		String[] urls=new String[getImageGroup().getButtonCount()-2]; //-2 for non-face buttons
		int cursor=0;
		for (Enumeration buttons=getImageGroup().getElements();buttons.hasMoreElements();)
			{
			JMenuItem but=(JMenuItem)buttons.nextElement();
			if (but instanceof FaceRadioButtonMenuItem)
				urls[cursor++]=((FaceRadioButtonMenuItem)but).getFaceURL();
			}
		return urls;
		}
	/**
	 * This method initializes newImagejDialog	
	 * 	
	 * @return javax.swing.JDialog	
	 */
	private JDialog getNewImagejDialog()
		{
		if (newImagejDialog==null)
			{
			newImagejDialog=new JDialog();
			newImagejDialog.setSize(new java.awt.Dimension(267,128));
			newImagejDialog.setModal(true);
			newImagejDialog.setTitle("Add New Image");
			newImagejDialog.setContentPane(getNewImageDialog());
			}
		return newImagejDialog;
		}

	/**
	 * This method initializes newImageDialog	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNewImageDialog()
		{
		if (newImageDialog==null)
			{
			newImageDialog=new JPanel();
			newImageDialog.setLayout(new BorderLayout());
			newImageDialog.add(getNamePanel(), java.awt.BorderLayout.NORTH);
			newImageDialog.add(getUrlPanel(), java.awt.BorderLayout.CENTER);
			newImageDialog.add(getOkPanel(), java.awt.BorderLayout.SOUTH);
			}
		return newImageDialog;
		}

	/**
	 * This method initializes newImageTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNewImageTextField()
		{
		if (newImageTextField==null)
			{
			newImageTextField=new JTextField();
			newImageTextField.setPreferredSize(new java.awt.Dimension(180,20));
			}
		return newImageTextField;
		}

	/**
	 * This method initializes newImageOKButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getNewImageOKButton()
		{
		if (newImageOKButton==null)
			{
			newImageOKButton=new JButton();
			newImageOKButton.setText("OK");
			newImageOKButton.addActionListener(new ActionListener()
				{
				public void actionPerformed(ActionEvent e)
					{
					//get the url
					String newName=getNewImageNameTextField().getText();
					String newURL=getNewImageTextField().getText();
					if (newURL!=null && newURL.length()>10)
						{
						somethingEntered=true;
						//add it to the properties object
						addChoiceToProps(newName, newURL);
						
						//add it to the menu and select it
						addNewMenuItem(newName, newURL);
						
						//use the new image
						setSelectedImageURL(newURL);


						//load the image and display it
						ActionEvent nae=new ActionEvent(getParent(),ActionEvent.ACTION_PERFORMED,"newImage");
						ActionListener[] als=(ActionListener[])getParent().getListeners(ActionListener.class);
						for (int i=0;i<als.length;i++)
							als[i].actionPerformed(nae);
						}
					else somethingEntered=false; //must have just closed dialog
												
					//clear the text field and hide the dialog
					getNewImageTextField().setText("");
					getNewImageNameTextField().setText("");
					getNewImagejDialog().setVisible(false);
					}

				});
			}
		return newImageOKButton;
		}

	private void addChoiceToProps(String newName, String newURL)
		{
		String entryName=null;
		for (Iterator names=getProps().keySet().iterator();names.hasNext();)
			{
			String name=(String)names.next();
			if (name.startsWith("imageChoice"))
				{
				String val=getProps().getProperty(name);
				if (val.indexOf(" ")>0)
					{
					val=val.substring(val.indexOf(" ")).trim();
					if (val.equals(newName))
						entryName=name;
					}
				}
			}
		if (entryName==null) //it's not already there, make a new one
			entryName="imageChoice"+getNextNumber("imageChoice");
		getProps().put(entryName, newURL+" "+newName);
		}
	
	protected int getNextNumber(String baseName)
		{
		for (int i=1;i<1000;i++)
			{
			if (getProps().getProperty(baseName+i)==null)
				return i;
			}
		return -1;
		}

	public Properties getProps()
		{
		return props;
		}

	public void setProps(Properties props)
		{
		this.props=props;
		}

	public JFrame getMainWindow()
		{
		return mainWindow;
		}

	public void setMainWindow(JFrame mainWindow)
		{
		this.mainWindow=mainWindow;
		}

	/**
	 * This method initializes namePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNamePanel()
		{
		if (namePanel==null)
			{
			nameLabel = new JLabel();
			nameLabel.setText("Name:");
			namePanel=new JPanel();
			namePanel.add(nameLabel, null);
			namePanel.add(getNewImageNameTextField(), null);
			}
		return namePanel;
		}

	/**
	 * This method initializes newImageNameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNewImageNameTextField()
		{
		if (newImageNameTextField==null)
			{
			newImageNameTextField=new JTextField();
			newImageNameTextField.setPreferredSize(new java.awt.Dimension(180,20));
			}
		return newImageNameTextField;
		}

	/**
	 * This method initializes urlPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getUrlPanel()
		{
		if (urlPanel==null)
			{
			urlLabel = new JLabel();
			urlLabel.setText("URL:");
			urlPanel=new JPanel();
			urlPanel.add(urlLabel, null);
			urlPanel.add(getNewImageTextField(), null);
			}
		return urlPanel;
		}

	/**
	 * This method initializes okPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getOkPanel()
		{
		if (okPanel==null)
			{
			okPanel=new JPanel();
			okPanel.add(getNewImageOKButton(), null);
			}
		return okPanel;
		}

	private void addNewMenuItem(String newName, String newURL)
		{
		FaceRadioButtonMenuItem item=null;
		for (Enumeration buttons=getImageGroup().getElements();buttons.hasMoreElements();)
			{
			Object button=buttons.nextElement();
			if (button instanceof FaceRadioButtonMenuItem 
					&& ((FaceRadioButtonMenuItem)button).getText().equals(newName))
				{
				item=(FaceRadioButtonMenuItem)button;
				item.setFaceURL(newURL);
				break;
				}
			}
		if (item==null)
			{
			item=new FaceRadioButtonMenuItem(newName, newURL);
			item.addActionListener(this);
			item.setActionCommand("faceImage");
			getImageGroup().add(item);
			item.setSelected(true);
			add(item);
			}
		}

	public void propertyChange(PropertyChangeEvent evt)
		{
		//  Update the menu from the weatherClock object.
		for (Enumeration menus=getImageGroup().getElements();menus.hasMoreElements();)
			{
			FaceRadioButtonMenuItem m=(FaceRadioButtonMenuItem)menus.nextElement();
			if (m.getFaceURL().equals(evt.getNewValue()))
				{
				m.setSelected(true);
				break;
				}
			}
		}

	public String getSelectedImageURL()
		{
		return selectedImageURL;
		}

	public void setSelectedImageURL(String selectedImageURL)
		{
		this.selectedImageURL=selectedImageURL;
		}

	public boolean isRandomImage()
		{
		return randomImage;
		}

	public void setRandomImage(boolean randomImage)
		{
		this.randomImage=randomImage;
		}

	}
