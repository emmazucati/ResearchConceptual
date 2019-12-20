//import java.net.URLClassLoaderChild;

import java.security.AllPermission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;

public class SandboxSecurityPolicy extends Policy {
    
    @Override
    public PermissionCollection getPermissions(ProtectionDomain domain) {
	System.out.println("getPermissions was called");
	if (isPlugin(domain)) {
	    //can cast CodeSource to CodeSourceName here
            return pluginPermissions(domain);
        }
        else {
	    return addAll();
        }        
    }
    
    private boolean isPlugin(ProtectionDomain domain) {
	return domain.getClassLoader() instanceof URLClassLoaderChild;
    }
    
    //alternate plugInPermission example
    /*
    private PermissionCollection pluginPermissions() {
        Permissions permissions = new Permissions();
        permissions.add(new FilePermission("/my-application/plugin-workspace/*", "read,write"));
        return permissions;
    }
    */

    //this is what the person making the topology is writing somewhere
    //that person is also deciding what permissions are given
    //the person writing the bolts has to follow those permissions
    private PermissionCollection pluginPermissions(ProtectionDomain domain) {
	Permissions permissions = new Permissions(); // No permissions
	CodeSourceName grabName = (CodeSourceName) domain.getCodeSource();
	if(grabName.getName() == "Plugin")
	    permissions.add(new AllPermission());
	return permissions;
    }
    
    private PermissionCollection addAll() {
	Permissions permissions = new Permissions();
	permissions.add(new AllPermission());
	return permissions;
    }

    public int foo(){
	return 5;
    }
}
