import java.io.File;
import java.lang.reflect.Field;

import net.minecraft.client.Minecraft; 

public class Start
{

	public static void main(String[] args)
	{
		try
		{
			// set new minecraft data folder to prevent it from using the .minecraft folder
			// this makes it a portable version
			Field f = Minecraft.class.getDeclaredField("minecraftDir");
			Field.setAccessible(new Field[] { f }, true);
			f.set(null, new File("jars"));
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
		Minecraft mc = null;
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
		
		// the variable mc now holds a reference to the minecraft main thread object, have fun :)
	}
	
}
