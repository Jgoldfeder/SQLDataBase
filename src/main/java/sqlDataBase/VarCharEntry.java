package sqlDataBase;
import java.util.Objects;

public class VarCharEntry implements Entry{
    private String value;
    //create a new VarCharEntry valued at null
    VarCharEntry(){
        value = null;
    }
    //create a new VarCharEntry valued at the string parsed
    VarCharEntry(String entry){
        if(entry.equals("NULL")||(entry.equals("null"))){
            value = null;
            return;
        }
        
        //string should be encased in single quotes. We want to remove those, and throw an exception if missing
        if(!(entry.startsWith("'") && entry.endsWith("'"))){
            throw new IllegalArgumentException("String must be surrounded by single quotes. For Example: 'String'");
        }
        value = entry.substring(1,entry.length()-1);
    }
    //if we ever want to construct a VarCharEntry from a string without quotes:
    VarCharEntry(String entry,Boolean hasQuotes){
        if(entry.equals("NULL")||(entry.equals("null"))){
            value = null;
            return;
        }
        if(hasQuotes){
            //string should be encased in single quotes. We want to remove those, and throw an exception if missing
            if(!(entry.startsWith("'") && entry.endsWith("'"))){
                throw new IllegalArgumentException("String must be surrounded by single quotes. For Example: 'String'");
            }
            value = entry.substring(1,entry.length()-1);
        }else{
            value = entry;
        }
    }
    
    
    //get the value
    public Object getValue(){
        return value; 
    }
    
    //get the type  of hte value stored
    public Class getType(){
        return String.class;
    }
    
    //entry is immutable. This creates and returns a new entry valued at the string s parsed. Useful when creating a new row from default values      
    public Entry setValue(String s){
        return new VarCharEntry(s);
    }
        
    //print the value
    public void print(){
        System.out.printf("%-25s",value);
    }
    
    @Override
    public int hashCode(){
        return Objects.hashCode(value);
    }
    @Override   
    public boolean equals(Object object){
		if(this == object){
			return true;
		}
		if(object == null){
			return false;
		}
        if((object instanceof Entry)&&(((Entry)object).getValue() == null) &&(value == null) ){
            return true;
        }
        
		if(this.value == null){
            return false;
        }
		if(getClass() != object.getClass()){
			return false;
		}
		VarCharEntry other = (VarCharEntry) object;
		
		if(this.value.equals(other.getValue())){
			return true;
		}
		return false;
	}
    @Override
    public int compareTo(Entry entry){
        //we want to allow comparison of nulls
        if((entry.getValue() == null)&&(this.value==null)){
            return 0;
        }
        if((entry.getValue() != null)&&(this.value==null)){
           return -1; 
        }       
        if((entry.getValue() == null)&&(this.value!=null)){
            return 1;
        }        
        //technically, this entry could be of a different sub-type. 
        //if so, throw an exception
        if(entry.getClass() != this.getClass()){
            throw new IllegalArgumentException("Cannot compare "+entry.getClass() +" to "+this.getClass() +".");
        }
        String operand = (String)entry.getValue();
        return value.compareTo(operand);
    }
}