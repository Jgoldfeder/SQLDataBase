package sqlDataBase;
import java.util.ArrayList;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.*;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition.Operator;

public class ColumnIndex<T extends Comparable<T>>{
    String columnName;
    BTree<T,ArrayList<Row>> index;
    //create an index on the given column
    public ColumnIndex(String columnName){
        this.columnName = columnName;
        index = new BTree<T,ArrayList<Row>>();
    }
    
    //add a row to be indexed
    public void addRow(Row row){
        T key = (T) row.get(columnName);
        ArrayList<Row> list = index.get(key);
        if(list == null){
            //create an arraylist and add this row
            ArrayList<Row> value = new ArrayList<Row>();
            value.add(row);
            index.put(key,value);
        }else{
            //simple append this row to the arrayList
            list.add(row);
        }
    }

    //remove a row from being indexed
    public void deleteRow(Row row){
        T key = (T) row.get(columnName);
        ArrayList<Row> list = index.get(key);

        if(list != null){

            list.remove(row);
        }
        if(list.isEmpty()){
            //remove list from BTree
            index.delete(key);
        }
    }
    
    //remove multiple rows from being indexed 
    public void deleteRows(ArrayList<Row> rows){
        for(int i = 0; i < rows.size();i++){
            this.deleteRow(rows.get(i));
        }
    }
    
    //add multiple rows to be indexed
    public void addRows(ArrayList<Row> rows){
        for(Row row:rows){
            this.addRow(row);
        }
    }
    
    
    //assume key is left operand
    //get all rows in the B-Tree that satisfy the condition imposed by the operator and key
    public ArrayList<Row> getRows(Operator o,T key){
        ArrayList<ArrayList<Row>> rowsInTree = new ArrayList<>();
        switch(o){
            case EQUALS:
                //this is easy, simply return whatever is at the key 
                return index.get(key);
            case GREATER_THAN:
                rowsInTree.addAll(index.getValuesGreaterThan(key));
                break;
            case GREATER_THAN_OR_EQUALS:
                rowsInTree.addAll(index.getValuesGreaterThan(key));
                rowsInTree.add(index.get(key));
                break;
            case LESS_THAN:
                rowsInTree.addAll(index.getValuesLessThan(key));
                break;
            case LESS_THAN_OR_EQUALS:
                rowsInTree.addAll(index.getValuesLessThan(key));
                rowsInTree.add(index.get(key));
                break;
            case NOT_EQUALS:
                rowsInTree.addAll(index.getValuesLessThan(key));
                rowsInTree.addAll(index.getValuesGreaterThan(key));
                break;
            case OR: //fall through
            case AND:
                throw new IllegalArgumentException("Boolean operators are not defined within Comparable interface");
        }   
        return combine(rowsInTree);
    }
    
    //combine arraylists into one
    private ArrayList<Row> combine(ArrayList<ArrayList<Row>> rowsInTree){
        ArrayList<Row> rows = new ArrayList<>();
        for(ArrayList<Row> r:rowsInTree){
            if(r!=null){
                rows.addAll(r);
            }
        }
        return rows;
        
    }
    
    //use this method to determine if the the entry in this column contained in the row passes in is unique
    public boolean isUnique(T key){
        
        ArrayList<Row> r = index.get(key);
        if((r==null)||(r.isEmpty())){
            return true;
        }
        return false;
    } 
}