package sqlDataBase;
import java.util.Objects;

public class BooleanEntry implements Entry{
    private Boolean value;
    //construct a boolean entry with a null value
    BooleanEntry(){
        value = null;
    }  
    
    //construct a boolean entry that parses the string for its value
    BooleanEntry(String entry){
        if(entry.equals("NULL")||(entry.equals("null"))){
            value = null;
        }else{
            value = Boolean.parseBoolean(entry);
        }
    }
    
    //construct a boolean entry whose value is entry
    BooleanEntry(Boolean entry){
        value = entry;
    }
    
    //get the value of this BooleanEntry
    public Object getValue(){
       return value; 
    }
    
    //get the Class of its type (Boolean.class)
    public Class getType(){
        return Boolean.class;
    }
     
    //Entries are imutable. This creates a new Entry of the same type with the specified string to be parsed as its value 
    //and returns it to the user. Useful when insertinf rows when we already know their default values
    public Entry setValue(String s){
        return new BooleanEntry(s);
    }
    
    //print the value
    public void print(){
        System.out.printf("%-25b",value);
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
		BooleanEntry other = (BooleanEntry) object;
		
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
            throw new IllegalArgumentException("Cannot compare "+entry.getClass()+ " to "+this.getClass() +".");
        }
        Boolean operand = (Boolean)entry.getValue();
        return value.compareTo(operand);
    }
}