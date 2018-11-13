package sqlDataBase;
import java.util.Objects;

public class DecimalEntry implements Entry{
    private Double value;
    
    //create a decimal entry valued at null
    DecimalEntry(){
        value = null;
    }
    
    //create a decimal entry by parsing the string
    DecimalEntry(String entry){
         if(entry.equals("NULL")||(entry.equals("null"))){
            value = null;
        }else{
            value = Double.parseDouble(entry);
        }
    }
    
    //create a decimal entry valued at entry
    DecimalEntry(Double entry){
        value = entry;
    }
    
    //get the value of this entry
    public Object getValue(){
       return value; 
    }
    
    //get the type of value this entry contains
    public Class getType(){
        return Double.class;
    }
    
    //entry is immutable. This creates and returns a new entry valued at the string s parsed. Useful when creating a new row from default values    
    public Entry setValue(String s){
        return new DecimalEntry(s);
    }
    
    //print thw value
    public void print(){
        System.out.printf("%-25.8f",value);
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
		DecimalEntry other = (DecimalEntry) object;
		
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
        //if so, throw an exception, unless the type is of IntegerEntry, in which case we perform a conversion
        if(entry instanceof IntegerEntry){
            Double operand = ((Integer)(entry.getValue())).doubleValue();
            return value.compareTo(operand);
        }
        if(entry.getClass() != this.getClass()){
            throw new IllegalArgumentException("Cannot compare "+entry.getClass()+ " to "+this.getClass() +".");
        }
        Double operand = (Double)entry.getValue();
        return value.compareTo(operand);
    }
}