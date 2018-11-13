package sqlDataBase;
import java.util.ArrayList;
import java.util.Arrays;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnID;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition.Operator;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnValuePair;
import java.util.HashMap;

public class Row{
    private ArrayList<Entry> row;
    private HashMap<String,Integer> columnNameIndices;
    private String comparableColumnName = null;
    
    //construct a row  filled with data.
    //the HashMap should be correctly filled with a map that matches the rows column names to their corresponding numerical indices
    Row(Entry[] data,HashMap<String,Integer> columnNameIndices){
        row = new ArrayList<>(Arrays.asList(data));
        this.columnNameIndices = columnNameIndices;
    }
    
    //get Entry at index
    Entry get(int index){
        return row.get(index);
    }
    
    //get Entry at column columnName
    Entry get(String columnName){
        Integer index = columnNameIndices.get(columnName);
        if(index == null){
            return null;
        }
        return row.get(index);
    } 
    
    //get all entries in this row, in order
    Entry[] toArray(){
        return row.toArray(new Entry[0]);
    }
    
    //convert operand from the type it was given as into an actual data type
    private Object processOperand(Object operand){
        Object returnValue = null;
        if(operand instanceof Condition){
            returnValue = isTrue((Condition)operand);
        }else if(operand instanceof ColumnID){
            returnValue = columnToValue((ColumnID)operand);
        }else if(operand instanceof String){
            returnValue = convertStringToDataType((String)operand);
        }
        return returnValue;
    }
    
    //true if this row satisfies the condition, false otherwise
    public Boolean isTrue(Condition c){
        //as per https://www.w3schools.com/sql/sql_update.asp if there is no where clause, condition is true
        if(c == null){
            return true;
        }             
        //operands can be one of three possible types. Either a: condition, string, ColumnId. 
        //The string can be one of four types: VarChar, int, decimal, boolean. Strings with '###' are Varchar. True and false are Boolean. Decimal of there is a decimal point.  
        //Conditions evaluate to booleans. ColumnIDs can be evaluated to one of those four types as well if we assume we are talking about a specific row (Which we always are)
        //Thus possible operands are: Boolean, VarChar(string), decimal(float), Integer.
        //Operators are only valid if both operands are of the same data type (except maybe for float and int).
        //Furthermore, AND and OR are only supported on booleans.
        //step 1: reduce until we have only those 4 types
        //step 2: throw an exception if the types are different
        //step 3:run comparable on the types
        //step 4: switch through possible operators to see what to return based on the comparable results
        //        make sure to not allow && and || on inappropriate types
        
        //process operands
        Object leftOperand = processOperand(c.getLeftOperand());
        Object rightOperand = processOperand(c.getRightOperand());
        
        //if one operand is a Double and one an Integer, perform a conversion
        if((rightOperand instanceof Double)&&(leftOperand instanceof Integer)){
            leftOperand = ((Integer)leftOperand).doubleValue();
        }
        if((rightOperand instanceof Integer)&&(leftOperand instanceof Double)){
            rightOperand =((Integer) rightOperand).doubleValue();
        }
        //throw exception if operands are incompatible
        //first  make sure if operand is null we still know its column type, as a null in a VarChar column is still incompatible with an Integer
        Class l = leftOperand instanceof Entry ? ((Entry)leftOperand).getType() : leftOperand.getClass();
        Class r = rightOperand instanceof Entry ? ((Entry)rightOperand).getType() : rightOperand.getClass();

        if(!(l.equals(r))){
            throw new IllegalArgumentException("Operands must evaluate to compatible data types. These were:"+l+" and "+r +" valued at:"+leftOperand+" and "+rightOperand);
        }
        //evaluate boolean expressions
        if(c.getOperator() == Operator.AND){
            if(leftOperand instanceof Boolean){
                return (Boolean)leftOperand && (Boolean)rightOperand;
            }
            throw new IllegalArgumentException("AND operator can only act on boolean values");
        }
        if(c.getOperator() == Operator.OR){
            if(leftOperand instanceof Boolean){
                return (Boolean)leftOperand || (Boolean)rightOperand;
            }
            throw new IllegalArgumentException("AND operator can only act on boolean values");
        }
        //at this point operator must be a comparison operator. Perform comparison and return true or false based on the assertion of the operator
        int comparison = -1;
        //if leftoperand is null, ie an Entry whose value is null, comparison should equal -1. Otherwise evaluate
        if(!(leftOperand instanceof Entry)){
            comparison = ((Comparable) leftOperand).compareTo(leftOperand.getClass().cast(rightOperand));
        }
        return evaluate(c.getOperator(),comparison);
    }
    
    //evaluates a condition in light of the comparison of its 2 values and its operator
    private Boolean evaluate(Operator o, int comparison){
                switch(o){
            case EQUALS:
                return comparison==0 ? true:false;
            case GREATER_THAN:
                return comparison>0 ? true:false;
            case GREATER_THAN_OR_EQUALS:
                return comparison>=0 ? true:false;
            case LESS_THAN:
                return comparison<0 ? true:false;
            case LESS_THAN_OR_EQUALS:
                return comparison<=0 ? true:false;
            case NOT_EQUALS:
                return comparison!=0 ? true:false;          
        }
        return null;
    }
    
    
    
    
    //converts the string to either an Integer, Double, String (removing the quotes) or Boolean
    private Object convertStringToDataType(String s){
        if(s.startsWith("'") && s.endsWith("'")){
            return s.substring(1,s.length()-1);
        }
        try{
            Integer integer = Integer.parseInt(s);
            return integer;
        }catch(NumberFormatException e1){
            try{
                Double d = Double.valueOf(s);
                return d;
            }catch(NumberFormatException e2){
                return Boolean.parseBoolean(s);
            }           
        }        
    }

    //given a columnID, return the value stored in that column in this row
    private Object columnToValue(ColumnID column){
        Object o = row.get(columnNameIndices.get(column.getColumnName())).getValue(); 
        if(o != null){
            return o;
        }
        return row.get(columnNameIndices.get(column.getColumnName())); 
    }
    
    //update this row based on the pairs of columns and values
    public void update(ColumnValuePair[] pairs){
        for(ColumnValuePair pair: pairs){
            int index = columnNameIndices.get(pair.getColumnID().getColumnName());
            String value = pair.getValue();
            Entry entry = row.get(index);
            entry = entry.setValue(value);
            row.set(index,entry);        
        }
        
    }
    
    //return a new row with the selected columns only
    public Row select(ColumnID[] columns){
        ArrayList<Entry> selectedEntries = new ArrayList<>(); 
        HashMap<String,Integer> resultIndices = new HashMap<>();
        for(int i = 0;i<columns.length;i++){
            //if the column is named "*", return everything
            if(columns[i].getColumnName().equals("*")){
                return this;
            }
            if(columnNameIndices.get(columns[i].getColumnName())== null){
                throw new IllegalArgumentException("column named "+ columns[i].getColumnName() +" does not exist");
            }
            selectedEntries.add(row.get(columnNameIndices.get(columns[i].getColumnName())));
            resultIndices.put(columns[i].getColumnName(),i);
        }
        Row result = new Row(selectedEntries.toArray(new Entry[0]),resultIndices);
        return result;
    }
    
    //set a value in this row
    void set(String columnName,Entry e){                
        
        Integer index = columnNameIndices.get(columnName);
        if(index == null){
            throw new IllegalArgumentException("ERROR: ILLEGAL COLUMN NAME!");
        }
        //make sure type is appropriate
        //if the entry is null, we can't infer what the correct type should be, so we must allow it
        if((row.get(index)!=null)&&!e.getType().equals(row.get(index).getType())){
            throw new IllegalArgumentException("ERROR: WRONG DATA TYPE!");
        }
        row.set(index,e);
    }

    
    //this can be confusing, because a row can only be compared to another row in regards to a specific
    //column. The user can specify the comparable column to compare via. This added semantics of comparison is
    //the reason the Row class does not implement comparable    
    public int compareToViaColumn(Row row,String columnName){
       if(columnName == null){
            throw new IllegalStateException("row comparison is undefined because a column has not been specified");
       }      
       return row.get(columnName).compareTo(this.get(columnName));  
    }    
    
    //print this row
    void print(){
        for(Entry entry: row){
            entry.print();
        }
        System.out.println("");
    }
    
    @Override
    public int hashCode(){
        return row.hashCode();
    }
        @Override
    public boolean equals(Object other){
        if(other instanceof Row){
            return row.hashCode() == other.hashCode();
        }
        return false;
    }
}