//package java.net;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
//import java.net.MyURLClassLoader;
import java.net.URL;

import java.security.AccessControlContext;
import java.security.CodeSigner;
import java.security.CodeSource;

import java.util.jar.Manifest;

import sun.misc.Resource;

public class URLClassLoaderChild extends MyURLClassLoader{
    /*
    public URLClassLoaderChild(URL[] urls, ClassLoader parent){
	super(urls, parent);
    }

    URLClassLoaderChild(URL[] urls, ClassLoader parent, AccessControlContext acc){
	super(urls, parent, acc);
    }

    public URLClassLoaderChild(URL[] urls){
	super(urls);
    }

    URLClassLoaderChild(URL[] urls, AccessControlContext acc){
	super(urls, acc);
    }

    public URLClassLoaderChild(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory){
	super(urls, parent, factory);
    }*/

    public URLClassLoaderChild(String jarPath) throws MalformedURLException{
        super(new URL[] {new File(jarPath).toURI().toURL()});
    }
    
    @Override
    protected Class<?> defineClass(String name, Resource res) throws IOException{
	long t0 = System.nanoTime();
        int i = name.lastIndexOf('.');
        URL url = res.getCodeSourceURL();
        if (i != -1) {
            String pkgname = name.substring(0, i);
            // Check if package already loaded.                          
            Manifest man = res.getManifest();
            definePackageInternal(pkgname, man, url);
        }
        // Now read the class bytes and define the class           
        java.nio.ByteBuffer bb = res.getByteBuffer();
        if (bb != null) {
            // Use (direct) ByteBuffer:                  
            CodeSigner[] signers = res.getCodeSigners();
            //CHANGED
	    //CodeSource cs = new CodeSource(url, signers);
	    System.out.println("Name in defineClass()  " + name);
	    CodeSourceName cs = new CodeSourceName(url, signers, name);
            sun.misc.PerfCounter.getReadClassBytesTime().addElapsedTimeFrom(t0);
            return defineClass(name, bb, cs);
        } else {
            byte[] b = res.getBytes();
            // must read certificates AFTER reading bytes.             
            CodeSigner[] signers = res.getCodeSigners();
	    //CHANGED
            //CodeSource cs = new CodeSource(url, signers);
	    System.out.println("Name in defineClass()  " + name);
	    CodeSourceName cs = new CodeSourceName(url, signers, name);
            sun.misc.PerfCounter.getReadClassBytesTime().addElapsedTimeFrom(t0);
            return defineClass(name, b, 0, b.length, cs);
        }

    }
}
