import java.net.URL;

import java.security.CodeSigner;
import java.security.CodeSource;

import java.util.Objects;

public class CodeSourceName extends CodeSource{

    private String className;

    //    CodeSourceName(){}

    public CodeSourceName(URL url, CodeSigner[] signers, String name){
	super(url, signers);
	className = name;
    }

    String getName(){
	return className;
    }

    @Override
    public boolean equals(Object obj) {
	if(obj == this)
	    return true;
	if(!(obj instanceof CodeSource))
	    return false;
	CodeSourceName cs = (CodeSourceName) obj;
	if(this.getName() != cs.getName())
	    return false;
	return super.equals(obj);
    }

    @Override
    public int hashCode(){
	return Objects.hash(super.hashCode(), className);
    }

}
