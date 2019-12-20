import java.io.File;

import java.lang.Class;
import java.lang.ClassLoader;
import java.lang.reflect.InvocationTargetException;
import java.lang.SecurityManager;

import java.net.MalformedURLException;
//import java.net.URLClassLoaderChild;
import java.net.URI;
import java.net.URL;

import java.security.AccessControlException;
import java.security.Policy;
import java.security.ProtectionDomain;

//import PluginFolder.Plugin;
//import PluginFolder.PluginClassLoader;

public class diffPolicy{
    public static void main(String[] args)
    {
	SandboxSecurityPolicy test = new SandboxSecurityPolicy();
	int temp = 2;
	temp = test.foo();
	System.out.println("please be 5: " + temp);
	//Set security policy and security manager during application initialization
	//	Policy.getPolicy();
	if(System.getSecurityManager() != null)
	    throw new Error("Security manager is already set");
	//	Policy.setPolicy(new SandboxSecurityPolicy());
	Policy.setPolicy(new SandboxSecurityPolicy());
	System.out.println("setPolicy called");
	System.setSecurityManager(new SecurityManager()); 
	//Policy.setPolicy(new SandboxSecurityPolicy());
	System.out.println("setSecurityManager called");
	try{
	    //   File pathname = new File("Plugin.jar");
	    //URI pathnameURI = pathname.toURI();
	    //URL urlTest = pathnameURI.toURL();
	    //Load a plugin
	    ClassLoader pluginLoader = new URLClassLoaderChild("Plugin.jar");
	    System.out.println("URLClassLoaderChild constructor");

	    try{
		Class <?> pluginClass = pluginLoader.loadClass("Plugin");
		System.out.println("loadClass called");
		Class <?> pluginClass2 = pluginLoader.loadClass("Plugin2");
		
		try{
		   
		    //System.out.println("First line");
			IPlugin plugin = (IPlugin) pluginClass.newInstance();
			IPlugin plugin2 = (IPlugin) pluginClass2.newInstance();
			//System.out.println("Initialized plugin");
			ProtectionDomain pluginPD = plugin.getClass().getProtectionDomain();
			ProtectionDomain pluginPD2 = plugin2.getClass().getProtectionDomain();
			//System.out.println("pluginPD: " + pluginPD);
			try{
			    plugin.readSensitiveFilesAndPhoneHome(); // Will throw java.lang.SecurityException
			    plugin2.readSensitiveFilesAndPhoneHome();
			}catch(AccessControlException e){
			    System.out.println("Didn't write to plugin file");
			}
		}catch(InstantiationException | IllegalAccessException e){
		    System.out.println("newInstance of Plugin didn't work");
		    e.printStackTrace();
		}
	    }catch(ClassNotFoundException e){
		System.out.println("Weird Class<?> thing didn't work");
		e.printStackTrace();
	    }
	}catch(MalformedURLException e){
	    System.out.println("Fake URL didn't work");
	    e.printStackTrace();
	}
	
	Application application = new Application();
	ProtectionDomain appPD = application.getClass().getProtectionDomain();
	//System.out.println("appPD: " + appPD);
	application.readSensitiveFilesAndPhoneHome(); // OK
    }
}
