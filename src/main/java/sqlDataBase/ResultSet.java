package sqlDataBase;
import java.util.ArrayList;
import java.util.HashMap;

public class ResultSet{

    private String[] columnNames;
    private ArrayList<Row> rows;
    private Class[] columnTypes;
    private Exception exception = null;
    private boolean usedIndexing = false;
    //create a new ResultSet from the table
    ResultSet(Table table){
        this.columnNames = table.getColumnNames();
        this.rows = table.getRows();
        this.columnTypes = table.getColumnTypes();
    }

    //create a new resultSet by specifying all of its components
    ResultSet(String[] columnNames,ArrayList<Row> rows,Class[] columnTypes){
        this.columnNames = columnNames;
        this.rows = rows;
        this.columnTypes = columnTypes;
    }

    //this should be called when we want to indicate an operation was done successfully,
    //after either CreateIndex, Insert, Update, and Delete
    ResultSet(){
        this.columnNames = new String[]{"SuccessStatus"};
        this.columnTypes = new Class[]{Boolean.class};
        Entry[] entry = new Entry[1];
        entry[0] = new BooleanEntry(true);
        HashMap<String,Integer> columnNameIndices = new HashMap<>();
        columnNameIndices.put(columnNames[0],0);
        Row row = new Row(entry,columnNameIndices);
        this.rows = new ArrayList<>();
        rows.add(row);
    }
    
    //create resultSet after a failed query 
    ResultSet(Exception e){
        this.columnNames = new String[]{"SuccessStatus"};
        this.columnTypes = new Class[]{Boolean.class};
        Entry[] entry = new Entry[1];
        entry[0] = new BooleanEntry(false);
        HashMap<String,Integer> columnNameIndices = new HashMap<>();
        columnNameIndices.put(columnNames[0],0);
        Row row = new Row(entry,columnNameIndices);
        this.rows = new ArrayList<>();
        rows.add(row);
        exception = e;
    }

    //get the error message, if there was one
    public String getErrorMessage(){
         if(exception != null){
            return exception.getMessage();
        }
        return "No Exceptions were thrown!";      
    }
    
    //get the column names of this resultSet
    public String[] getColumnNames(){
        return columnNames;       
    }

    //print the exception stack trace, if the query threw one
    public void printStackTrace(){
        if(exception != null){
            exception.printStackTrace();
        }else{
            System.out.println("No Exceptions were thrown!");
        }
    }
    
    //get the column types, as Class objects
    public Class[] getColumnTypes(){
        return columnTypes;     
    }

    //get the results as a 2 dimensional array of Object
    //possible values are  String,Double,Integer, and Boolean, as well as null.
    //thie should be parsed after getting the column types with getColumnTypes()
    public Object[][] getResults(){

        Object[][] results = new Object[rows.size()][columnNames.length];

        //convert entries to normal types, ie from VarCharEntry to String, etc
        for(int i=0; i< results.length;i++){
            for(int j = 0;j<columnNames.length;j++){
                if(rows.get(i).toArray()[j] instanceof Entry){
                    results[i][j] =((Entry) rows.get(i).toArray()[j]).getValue();
                }
            }
        }
        return results;
    }
    
    //print the results
    public void print(){
        System.out.print("RESULTS:");
        System.out.println("");
        
        printColumnNames();
        for(Row row: rows){
            row.print();
        }
        
        
    }

    //print the columnNames
    private void printColumnNames(){
        for(String name: columnNames){
            System.out.printf("%-25s",name);           
        }        
        System.out.println("");
    }
    
    //used by the table class to properly set if the query used indexing or not
    void setUsedIndexing(boolean bool){
        usedIndexing = bool;
    }
    
    //return true if the query used indexing, false if not
    public boolean getUsedIndexing(){
        return usedIndexing;
    }
}
