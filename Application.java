import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.security.Policy;

public class Application{
    Application(){
	//Policy.setPolicy(new SandboxSecurityPolicy());
		//        System.setSecurityManager(new SecurityManager());
}

    void readSensitiveFilesAndPhoneHome(){
	//	String filename = new String("applicationFile.txt");
        //FilePermission canWrite = new FilePermission(filename, "write");
	//   SecurityManager security = new SecurityManager();
        //System.setSecurityManager(security);
        try {
            //FilePermission canWrite = new FilePermission(filename, "write");                            
	    //System.setProperty("canWrite", "write");                                                        
	    File pathname = new File("applicationFile.txt");
	    pathname.createNewFile();
	    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(pathname,true));
	    String sentence = "Words in a file!!!";
	    writer.write(' ');
	    int strLength = sentence.length();
	    writer.write(sentence, 0, strLength);
	    writer.close();
        } catch (IOException e) {
            System.out.println("Didn't work");
        }

    }
}
