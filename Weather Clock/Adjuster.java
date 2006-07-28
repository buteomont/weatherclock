import java.awt.LayoutManager;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Adjuster extends JPanel implements ChangeListener
	{
	private String title="";
	private JLabel adjName = null;
	private JLabel adjValue = null;
	private JSlider adjustment = null;

	public Adjuster()
		{
		super();
		initialize();
		}

	public Adjuster(boolean arg0)
		{
		super(arg0);
		initialize();
		}

	public Adjuster(LayoutManager arg0)
		{
		super(arg0);
		initialize();
		}

	public Adjuster(LayoutManager arg0, boolean arg1)
		{
		super(arg0, arg1);
		initialize();
		}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
		{
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setHgap(5);
		adjValue = new JLabel();
		adjValue.setText("50");
		adjValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		adjValue.setPreferredSize(new java.awt.Dimension(25,16));
		adjName = new JLabel();
		adjName.setText("name");
		this.setLayout(borderLayout);
		this.setSize(300, 44);
		this.add(adjName, java.awt.BorderLayout.WEST);
		this.add(adjValue, java.awt.BorderLayout.EAST);
		this.add(getAdjustment(), java.awt.BorderLayout.CENTER);
		}

	/**
	 * This method initializes adjustment	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	public JSlider getAdjustment()
		{
		if (adjustment == null)
			{
			adjustment=new JSlider();
			adjustment.setPaintTicks(true);
			adjustment.setPaintLabels(true);
//			adjustment.setMajorTickSpacing(10);
//			adjustment.setMinorTickSpacing(5);
			adjustment.addChangeListener(this);
			}
		return adjustment;
		}

	public void stateChanged(ChangeEvent arg0)
		{
		adjValue.setText(adjustment.getValue()+"");
		}


	/* (non-Javadoc)
	 * @see javax.swing.JSlider#addChangeListener(javax.swing.event.ChangeListener)
	 */
	public void addChangeListener(ChangeListener arg0)
		{
		getAdjustment().addChangeListener(arg0);
		}

	/* (non-Javadoc)
	 * @see javax.swing.JSlider#getValue()
	 */
	public int getValue()
		{
		return getAdjustment().getValue();
		}

	/* (non-Javadoc)
	 * @see javax.swing.JSlider#setMaximum(int)
	 */
	public void setMaximum(int arg0)
		{
		getAdjustment().setMaximum(arg0);
		}

	/* (non-Javadoc)
	 * @see javax.swing.JSlider#setMinimum(int)
	 */
	public void setMinimum(int arg0)
		{
		getAdjustment().setMinimum(arg0);
		}

	/* (non-Javadoc)
	 * @see javax.swing.JSlider#setPaintLabels(boolean)
	 */
	public void setPaintLabels(boolean arg0)
		{
		getAdjustment().setPaintLabels(arg0);
		}

	/* (non-Javadoc)
	 * @see javax.swing.JSlider#setPaintTicks(boolean)
	 */
	public void setPaintTicks(boolean arg0)
		{
		getAdjustment().setPaintTicks(arg0);
		}

	/* (non-Javadoc)
	 * @see javax.swing.JSlider#setPaintTrack(boolean)
	 */
	public void setPaintTrack(boolean arg0)
		{
		getAdjustment().setPaintTrack(arg0);
		}

	/* (non-Javadoc)
	 * @see javax.swing.JSlider#setSnapToTicks(boolean)
	 */
	public void setSnapToTicks(boolean arg0)
		{
		getAdjustment().setSnapToTicks(arg0);
		}

	/* (non-Javadoc)
	 * @see javax.swing.JSlider#setValue(int)
	 */
	public void setValue(int arg0)
		{
		getAdjustment().setValue(arg0);
		}

	/**
	 * @return Returns the title.
	 */
	public String getTitle()
		{
		return title;
		}

	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title)
		{
		this.title=title;
		adjName.setText(title);
		getAdjustment().setName(title);
		}

	public void setMajorTickSpacing(int arg0)
		{
		adjustment.setMajorTickSpacing(arg0);
		}

	public void setMinorTickSpacing(int arg0)
		{
		adjustment.setMinorTickSpacing(arg0);
		}
	
	}  //  @jve:decl-index=0:visual-constraint="10,10"
