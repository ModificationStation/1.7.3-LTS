// PUBLIC DOMAIN
// created by Searge

import java.io.File;
import java.lang.reflect.Field;

//import Minecraft;

public class Start
{

	public static void main(String[] args)
	{
		String folder = "jars";

		try
		{
			// set new minecraft data folder to prevent it from using the .minecraft folder
			// this makes it a portable version
			Field f = Minecraft.class.getDeclaredField("dataFolder");
			Field.setAccessible(new Field[] { f }, true);
			f.set(null, new File(folder));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		
		// start minecraft game application
		Minecraft.main(args);
		
		// enumerate all threads in this process
		Thread[] threads = new Thread[256];
		int count = java.lang.Thread.enumerate(threads);
		
		// find the minecraft main thread
		Minecraft mc;
		for(int i = 0; i < count; ++i)
		{
			// compare thread name to find the main thread
			if(threads[i].getName().equals("Minecraft main thread"))
			{
				try
				{
					// get access to minecraft main thread object
					Field f = Thread.class.getDeclaredField("target");
					Field.setAccessible(new Field[] { f }, true);
					mc = (Minecraft)f.get(threads[i]);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					System.exit(0);
				}
				break;
			}
		}
		
		// the variable mc now holds a reference to the minecraft main core class, anything
        // should be accessible from there... have fun :)
	}
	
}
