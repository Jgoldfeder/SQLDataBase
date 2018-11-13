package sqlDataBase;
import java.util.Objects;

public class IntegerEntry implements Entry{
    private Integer value;
    
    //create an integer entry valued at null
    IntegerEntry(){
        value = null;
    }
    
    //create an integer entry valued at the string parsed
    IntegerEntry(String entry){
        if(entry.equals("NULL")||(entry.equals("null"))){
            value = null;
        }else{
            value = Integer.parseInt(entry);
        }
    }
    
    //create a new IntegerEntry valued at entry
    IntegerEntry(Integer entry){
        value = entry;
    }    
    //get this entry's value
    public Object getValue(){
       return value; 
    }
    //get the type this entry stores
    public Class getType(){
        return Integer.class;
    }
    
    //entry is immutable. This creates and returns a new entry valued at the string s parsed. Useful when creating a new row from default values    
    public Entry setValue(String s){
        return new IntegerEntry(s);
    }
    
    //print the value
    public void print(){
        System.out.printf("%-25d",value);
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
		IntegerEntry other = (IntegerEntry) object;
		
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
        //if so, throw an exception, unless the type is of DecimalEntry, in which case we perform a conversion
        if(entry instanceof DecimalEntry){
            Double operand = (Double)entry.getValue();
            Double doubleValue = value.doubleValue();
            return doubleValue.compareTo(operand);
        }
        if(entry.getClass() != this.getClass()){
            throw new IllegalArgumentException("Cannot compare "+entry.getClass() +" to "+this.getClass() +".");
        }
        Integer operand = (Integer)entry.getValue();
        return value.compareTo(operand);
    }
}