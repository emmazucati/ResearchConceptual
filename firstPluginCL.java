//package PluginFolder;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.PrivilegedExceptionAction;

import java.util.jar.Manifest;

import sun.misc.Resource;

public class firstPluginCL extends URLClassLoader {
    /*public PluginClassLoader(URL jarFileUrl) {
	super(new URL[] {jarFileUrl});
	System.out.println("FInishes PLuginClassLoader constructor");
	}*/

    //To give access to some file for all code loaded by class loader, regardless of policy
    /*
      @Override
    protected PermissionCollection getPermissions(CodeSource codesource) {
        Permissions permissions = new Permissions();
        permissions.add(new FilePermission("/some/file", "read"));
        return permissions;
    }
    */

    public firstPluginCL(String jarPath) throws MalformedURLException {
	super(new URL[] {new File(jarPath).toURI().toURL()});
    }


    /**
     * Finds and loads the class with the specified name from the URL search
     * path. Any URLs referring to JAR files are loaded and opened as needed
     * until the class is found.
     *
     * @param name the name of the class
     * @return the resulting class
     * @exception ClassNotFoundException if the class could not be found,
     *            or if the loader is closed.
     * @exception NullPointerException if {@code name} is {@code null}.
     */
    @Override
    protected Class<?> findClass(final String name)throws ClassNotFoundException
    {
        final Class<?> result;
        try {
            result = AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>() {
	        public Class<?> run() throws ClassNotFoundException {
		    String path = name.replace('.', '/').concat(".class");
		    Resource res = ucp.getResource(path, false);
		    if (res != null) {
			try {
			    return defineClass(name, res);
			} catch (IOException e) {
			    throw new ClassNotFoundException(name, e);
			}
		    } else {
			return null;
		    }
		}
		}, acc);
        } catch (java.security.PrivilegedActionException pae) {
            throw (ClassNotFoundException) pae.getException();
        }
        if (result == null) {
            throw new ClassNotFoundException(name);
        }
        return result;
    }


    /*
     * Defines a Class using the class bytes obtained from the specified
     * Resource. The resulting Class must be resolved before it can be
     * used.
     */
    private Class<?> defineClass(String name, Resource res) throws IOException {
        long t0 = System.nanoTime();
        int i = name.lastIndexOf('.');
        URL url = res.getCodeSourceURL();
        if (i != -1) {
            String pkgname = name.substring(0, i);
            // Check if package already loaded.
            Manifest man = res.getManifest();
            //changed b/c java api 8 doesn't have this function
	    //definePackageInternal(pkgname, man, url);
	    definePackage(pkgname, man, url);
        }
        // Now read the class bytes and define the class
        java.nio.ByteBuffer bb = res.getByteBuffer();
        if (bb != null) {
            // Use (direct) ByteBuffer:
            CodeSigner[] signers = res.getCodeSigners();
            
	    //EDITED
	    //CodeSource cs = new CodeSource(url, signers);
            CodeSourceName cs = new CodeSourceName(url, signers, name);
	    sun.misc.PerfCounter.getReadClassBytesTime().addElapsedTimeFrom(t0);
            return defineClass(name, bb, cs);
        } else {
            byte[] b = res.getBytes();
            // must read certificates AFTER reading bytes.
            CodeSigner[] signers = res.getCodeSigners();
	    
	    //EDITED
            //CodeSource cs = new CodeSource(url, signers);
	    CodeSourceName cs = new CodeSourceName(url, signers, name);
            sun.misc.PerfCounter.getReadClassBytesTime().addElapsedTimeFrom(t0);
            return defineClass(name, b, 0, b.length, cs);
        }
    }

}
