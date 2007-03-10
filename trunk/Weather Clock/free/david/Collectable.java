package free.david;

import java.util.Properties;

/**
 * Classes of this type can collect persistent settings and return them
 * to an outside class. A Properties object must be passed in for the 
 * settings to be collected into.
 *
 */
public interface Collectable
	{
	public void collectSettings(Properties settings);
	}
