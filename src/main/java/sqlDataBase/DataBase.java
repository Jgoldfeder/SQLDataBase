package sqlDataBase;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.*;
import net.sf.jsqlparser.JSQLParserException;
import java.util.ArrayList;
import java.util.HashMap;

public class DataBase {
    //store all the tables associated with the database
    private HashMap<String,Table> tables;
    
    //create a database
    public DataBase(){
        tables = new HashMap<>();
    }
     
    //The public API
    public ResultSet execute(String SQL){
        try{
        SQLParser parser = new SQLParser();
        SQLQuery query = parser.parse(SQL);
        return executeQuery(query);
        }catch(Exception e){
            return new ResultSet(new JSQLParserException("The parser could not parse the query",e));
        }
    }
    
    //find out what the query was to process it properly
    private ResultSet executeQuery(SQLQuery query){     
        if(query instanceof CreateTableQuery){
            return createTable(  (CreateTableQuery) query);
        }
        if(query instanceof InsertQuery){
            return insertRow(  (InsertQuery) query);
        }
        if(query instanceof UpdateQuery){
            return update((UpdateQuery)query);
        }            
        if(query instanceof DeleteQuery){
            return delete((DeleteQuery)query);
        }
        if(query instanceof SelectQuery){
            return select((SelectQuery)query);
        }
        if(query instanceof CreateIndexQuery){
            return createIndex((CreateIndexQuery)query);
        }
        
        throw new IllegalArgumentException("Could not process query: query-type not recognized");
    }
    
    //get results from creating a table
    private ResultSet createTable(CreateTableQuery query){
        Table table = null;
        try{
            table = new Table(query);
            tables.put(query.getTableName(), table);
        }catch(Exception e){
            return new ResultSet(e);
        }
        return new ResultSet(table);
    }
    
    //get results from inserting a row
    private ResultSet insertRow(InsertQuery query){
        boolean usedIndexing = false;
        try{
            Table table = tables.get(query.getTableName());
            if(table == null){
                throw new IllegalArgumentException("Can't insert into a non-existing table");
            }
            table.insertRow(query);
            usedIndexing = table.lastQueryUsedIndexing();
        }catch(Exception e){
            return new ResultSet(e);
        }
        ResultSet r = new ResultSet();
        r.setUsedIndexing(usedIndexing);
        return r;
    }
    
    //get results from updating a table
    private ResultSet update(UpdateQuery query){
        boolean usedIndexing = false;
        try{
            Table table = tables.get(query.getTableName());
            if(table == null){
            throw new IllegalArgumentException("Can't update a non-existing table");
        }
            table.update(query);
            usedIndexing = table.lastQueryUsedIndexing();
        }catch(Exception e){
            return new ResultSet(e);
        }
        ResultSet r = new ResultSet();
        r.setUsedIndexing(usedIndexing);
        return r;
    }
    
    //get results from deleting from a table
    private ResultSet delete(DeleteQuery query){      
        boolean usedIndexing = false;
        try{
            Table table = tables.get(query.getTableName());
            if(table == null){
                throw new IllegalArgumentException("Can't delete from a non-existing table");
            }
            table.delete(query);
            usedIndexing = table.lastQueryUsedIndexing();
        }catch(Exception e){
            return new ResultSet(e);
        }  
        ResultSet r = new ResultSet();
        r.setUsedIndexing(usedIndexing);
        return r;
    }   
    
    //get results from selecting from a table
    private ResultSet select(SelectQuery query){
        boolean usedIndexing = false;
        ResultSet resultSet = null;
        try{
            String[] tableNames = query.getFromTableNames();
            if(tableNames.length > 1){
                throw new IllegalArgumentException("This implementation does not (as per specs) support multi table select");
            }
            Table table = tables.get(tableNames[0]);
            if(table == null){
                throw new IllegalArgumentException("Can't delete from a non-existing table");
            }
            resultSet = table.select(query);
            usedIndexing = table.lastQueryUsedIndexing();
        }catch(Exception e){
            return new ResultSet(e);
        }
        resultSet.setUsedIndexing(usedIndexing);
        return resultSet;
    }

    //get results from creating an index on a column in a table
    private ResultSet createIndex(CreateIndexQuery query){
        try{
            Table table = tables.get(query.getTableName());
            if(table == null){
                throw new IllegalArgumentException("Can't delete from a non-existing table");
            }
            table.createIndex(query);
        }catch(Exception e){
            return new ResultSet(e);
        }  
        return new ResultSet();
        
    }
}
