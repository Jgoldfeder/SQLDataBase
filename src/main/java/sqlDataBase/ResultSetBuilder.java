package sqlDataBase;
import java.util.*;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.*;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery.*;


class ResultSetBuilder{
    private HashMap<String,Integer> columnNameIndices = new HashMap<>();
    private ArrayList<Row> rows = new ArrayList<>();
    private ArrayList<String> columnNames = new ArrayList<>();
    private boolean allColumns = false;
    
    //add columns to the result set to be built
    void addColumns(ColumnID[] columns,ArrayList<FunctionInstance> functions){
        
        for(int i = 0;i<columns.length;i++){
            String name = getColumnName(columns[i],functions);
            columnNameIndices.put(name,i);
            columnNames.add(name);
        }
    }
    
    //alternate constructor for when there is a * and we must specify its column names 
    void addColumns(ColumnID[] columns,String[] starColumns,ArrayList<FunctionInstance> functions){
        //this code is a little confusing. We want to, if we find a star, insert all the star columns
        //and then afterwards continue inserting the regular ones listed. So after inserting the star columns, ie all collumns
        // in the table, we will have incremented j by that much. Thus we add j when inserting the column index, as once we have 
        //added the star columns the proper index will have been that much more than i
        //by not setting j to zero at the start of the for loop, we ensure that star columns are added only once, even if the 
        //user specified it multiple times
        int j = 0;
        for(int i = 0;i<columns.length;i++){
            if(columns[i].getColumnName().equals("*")){
                for(;j<starColumns.length;j++){
                    columnNameIndices.put(starColumns[j],i);
                    columnNames.add(starColumns[j]);
                }
                continue;
            }
            String name = getColumnName(columns[i],functions);
            //if this column already exists, ie if user types MAX(col), MAX(col), we 
            //can't have two columns with the same name. So get rid of one
            if(columnNameIndices.get(name)==null){
                //this is a new columnName
                columnNameIndices.put(name,i+j);
                columnNames.add(name);
            }
            
        }
    }    
    
    //this method finds the name of a given column, if it has a function the function is made part of its name
    //for example a column named col with an AVG function becomes named "AVG(col)"
    private String getColumnName(ColumnID column,ArrayList<FunctionInstance> functions){
        for(FunctionInstance function:functions){
            if(function.column == column){
                String distinct = function.isDistinct ? "distinct " : "";
                return function.function.name()+"("+distinct+column.getColumnName()+")";
            }
        }
        return column.getColumnName();
    }

    //add a row to the resultset to be built
    void addRow(Row row){
        ArrayList<Entry> entries = new ArrayList<>();
        
        //extract all columns that are in the row        
        for(int i=0;i<columnNames.size();i++){
            entries.add(row.get(columnNames.get(i)));
        }
        //check if the row has any non null values
        boolean hasNotNull = false;
        for(Entry entry:entries){
            if(entry != null){
                hasNotNull = true;
                break;
            }
        }
        if(hasNotNull){
            rows.add(new Row(entries.toArray(new Entry[0]),columnNameIndices));
        }
    }

    //add multiple rows to the result set to be built
    void addRows(ArrayList<Row> rowlist){
        for(Row row:rowlist){
            addRow(row);
        }
    }

    //fill a column with a value. Used for select functions
    void fillColumn(String columnName, Entry entry){
        if(rows.size()==0){
            //we must add a new row
            rows.add(new Row(new Entry[columnNames.size()],columnNameIndices));
        } 
        for(Row row:rows){
            row.set(columnName,entry);          
        }        
    }

    //build and return the resultSet
    ResultSet build(){
        Class[] columnTypes = new Class[columnNames.size()];
        if(rows.size()==0){
            return new ResultSet(columnNames.toArray(new String[0]),rows,null);
        }
        Entry[] row = rows.get(0).toArray();
        for(int i=0;i<row.length;i++){
            columnTypes[i] = row[i].getType();
        }
        return new ResultSet(columnNames.toArray(new String[0]),rows,columnTypes);
    }

}    