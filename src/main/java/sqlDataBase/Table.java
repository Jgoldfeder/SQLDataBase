package sqlDataBase;
import java.util.ArrayList;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.*;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery.*;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery.FunctionName.*;
import java.util.Objects;
import java.util.HashMap;
import java.util.Arrays;
import java.util.HashSet;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition.Operator;
public class Table{
    //all the table data
    private ArrayList<ColumnDescription> descriptions;
    private ArrayList<Row> table;
    private ArrayList<Entry> defaultValues;
    private HashMap<String,Integer> columnNameIndices;
    private String name;
    private ColumnDescription    primaryKeyColumn;
    private HashMap<String,ColumnIndex> columnIndex;
    private boolean usedIndexing = false;
    
    //create a table with the create table query
    Table(CreateTableQuery query){
        descriptions = new ArrayList<>(Arrays.asList(query.getColumnDescriptions()));
        primaryKeyColumn = query.getPrimaryKeyColumn();
        name = query.getTableName();
        table = new ArrayList<>();
        
        //initialize map of column names and indices
        //initialize default values for all
        processColumnDescriptions(query.getColumnDescriptions());        
    }
    
    
    //this stores the column names with associated indices in the columnNames map,
    //and sets the defaultValues to the appropriate value. That means null unless otherwise specified.
    private void processColumnDescriptions(ColumnDescription[] description){        
        columnNameIndices = new HashMap<>();
        columnIndex = new HashMap<>();
        defaultValues = new ArrayList<>(); 
        for(int i = 0;i < description.length; i++){
            //associate names with indices
            columnNameIndices.put(description[i].getColumnName(),i);
            //if column is primarykey, index it, otherwise don't
            if(description[i] == primaryKeyColumn){
                ColumnIndex<Entry> index = new ColumnIndex<>(description[i].getColumnName());
                columnIndex.put(description[i].getColumnName(),index);
            }
            
            setDefaultValue(description[i]);
                
        }
    }
    
    //set the default value of a column
    private void setDefaultValue(ColumnDescription description){
            switch(description.getColumnType()){
                case INT:
                    if(description.getHasDefault()){
                        defaultValues.add(new IntegerEntry(description.getDefaultValue()));
                    }else{
                        defaultValues.add(new IntegerEntry());
                    }
                    break;
                case VARCHAR:
                    if(description.getHasDefault()){
                        defaultValues.add(new VarCharEntry(description.getDefaultValue()));
                    }else{
                        defaultValues.add(new VarCharEntry());
                    }                    
                    break;
                case DECIMAL:
                    if(description.getHasDefault()){
                        defaultValues.add(new DecimalEntry(description.getDefaultValue()));
                    }else{
                        defaultValues.add(new DecimalEntry());
                    }
                    break;
                case BOOLEAN:
                    if(description.getHasDefault()){
                        defaultValues.add(new BooleanEntry(description.getDefaultValue()));
                    }else{
                        defaultValues.add(new BooleanEntry());
                    }
                    break;
            }       
    }
    
    
    //used to verify that varchars and decimals are not too large, throwing exceptions if not
    private void matchesSize(ColumnValuePair[] pairs){
        
        for(ColumnValuePair pair: pairs){
            String name = pair.getColumnID().getColumnName();
            int index = columnNameIndices.get(name);
            ColumnDescription description = descriptions.get(index);
            if(description.getColumnType() == ColumnDescription.DataType.VARCHAR ){
                int maxLength = description.getVarCharLength();
                //subtract 2 because the quotes don't count
                int varCharLength = pair.getValue().length()-2;
                if(varCharLength > maxLength){
                    throw new IllegalArgumentException("Varchar length cannot exceed the maximum length set forth when the table was created.");
                }
            }
            if(description.	getColumnType() == ColumnDescription.DataType.DECIMAL ){
                int wholeNum = description.getWholeNumberLength();
                int fracNum = description.getFractionLength();
                String string =pair.getValue();
                String[] parts = string.split(".");
                if(parts.length >2){
                    throw new IllegalArgumentException("Could not parse decimal.");
                }
                int wholeLength = (parts.length>1) ? parts[0].length(): string.length();
                int fracLength = (parts.length>1) ? parts[1].length() : 0;
                if((wholeLength> wholeNum)||(fracLength > fracNum)){
                    throw new IllegalArgumentException("Decimal did not fit constrains imposed in create table query");
                }
            }   
        }
    }    
    
    
    //insert a row into this table with an insert row query
    void insertRow(InsertQuery query){
        usedIndexing = false;
        ColumnValuePair[] pairs = query.getColumnValuePairs();
        Entry[] data = convertPairsToEntryArray(pairs);
        
        //verify if row data contains invalid data ie. data that breaks the constraints imposed by the user.
        //possible constraints are: is unique and not null. PrimaryKey column has both constraints
        //other constraints are the maximal size of a varchar and decimal
        matchesSize(pairs);
        for(int i =0; i < descriptions.size(); i++){
            //verify if column can't be null that entry indeed is not
            if( (data[i].getValue() == null) && ((descriptions.get(i).isNotNull())||(descriptions.get(i)==primaryKeyColumn)) ){
                throw new IllegalArgumentException("a not null column (or a primary column) cannot be set to NULL");
            }
            //verify if column must be unique that it is
            if((descriptions.get(i).isUnique()||(descriptions.get(i)==primaryKeyColumn) )&& isInColumn(descriptions.get(i).getColumnName(),data[i])){
                throw new IllegalArgumentException("a unique column (or a primary column)cannot be given a value already extant in that column");          
            }
            
        }        
        Row row = new Row(data,columnNameIndices);
        
        //if any column is indexed, add this row to its btree
        for(ColumnDescription description:descriptions){
            ColumnIndex index = columnIndex.get(description.getColumnName());
            if(index != null){
                //this column is indexed
                index.addRow(row);
            }
        }
        
        table.add(row);
    }
   
    //The pairs are given as only the values in the row that the user wants to set.
    //The rest are set to default, or if there is no default, null. If a value is 
    //constricted to not null and the user doesn't specify it and it has no default,
    //we must throw an exception
    //This method creates an Entry array properly padded with the defaults and the 
    //nulls in the proper order 
    private Entry[] convertPairsToEntryArray(ColumnValuePair[] pairs){
        Entry[] entries = (Entry[]) defaultValues.toArray(new Entry[defaultValues.size()]);
        for(ColumnValuePair pair: pairs){
            String columnName = pair.getColumnID().getColumnName();
            String value = pair.getValue();
            Integer index = columnNameIndices.get(columnName);
            
            //check for invalid column
            if(index == null){
                throw new IllegalArgumentException("Column name "+ columnName +" does not match any known column name.");
            }
            
            //this does not modify default value array, as entries are  immutable (see entry interface)            
            entries[index] = entries[index].setValue(value);
        }
        return entries;
    }
    
    //checks if a certain entry is already found in a column
    //useful to check for uniqueness before adding an entry to a column
    private boolean isInColumn(String columnName,Entry e){
        //if column is indexed, verify using B-Trees        
        ColumnIndex index = columnIndex.get(columnName);
        if(index != null){
            //column is indexed
            usedIndexing = true;
            return !index.isUnique(e);
        }
        
        for(Row row: table){
            if(row.get(columnName).equals(e)){
                return true;
            }
        }
        return false;
    }
    
    //update table with update query
    void update(UpdateQuery query){
        usedIndexing = false;
        //make sure sizes of varchars and decimals are ok
        matchesSize(query.getColumnValuePairs());
        
        //if update violates non-null column, throw an exception
        if(violatesNotNull(query.getColumnValuePairs())){
            throw new IllegalArgumentException("Update query may not put null into a non-null or primary key column.");
        }
        if(violatesUnique(query.getColumnValuePairs())){
            throw new IllegalArgumentException("Update query may not put non-unique entries into a unique or primary key column.");
        }
        
        //use B-Trees if applicable, otherwise do a linear search
        ArrayList<Row> rowsToUpdate;        
        if(isFullyIndexed(query.getWhereCondition())){
            rowsToUpdate = filter(table,query.getWhereCondition());
        }else{
            rowsToUpdate = new ArrayList<>();
            for(Row row: table){
                if(row.isTrue(query.getWhereCondition())){
                    rowsToUpdate.add(row);
                }
            }
        }
        
        //if rowsToUpdate is of a size larger than one, and updating a unique column, thie violates unique and an exception must be thrown
        if(rowsToUpdate.size()>1){
            for(ColumnValuePair p :query.getColumnValuePairs()){
                int index = columnNameIndices.get(p.getColumnID().getColumnName());
                if(descriptions.get(index).isUnique() ||(descriptions.get(index).equals(primaryKeyColumn))){
                    throw new IllegalArgumentException("Update query may not put non-unique entries into a unique or primary key column.");
                }
            }
        }
        
        //delete all rows from b-trees, update them, and re-add them
        removeIndexes(rowsToUpdate);

        for(Row row: rowsToUpdate){
            row.update(query.getColumnValuePairs());            
        }
        addIndexes(rowsToUpdate);
    }
    
    //verify columnValue pairs don't have a null on a non-null column
    private boolean violatesNotNull(ColumnValuePair[] pairs){
        for(ColumnValuePair p:pairs){
           if(p.getValue().equals("NULL") || p.getValue().equals("null")){
                int index = columnNameIndices.get(p.getColumnID().getColumnName());
                if(descriptions.get(index).isNotNull() ||(descriptions.get(index).equals(primaryKeyColumn))){
                    return true;
                }
           }                       
        }
        return false;
    }
    
    //verify update does not violate unique
    private boolean violatesUnique(ColumnValuePair[] pairs){
        for(ColumnValuePair p:pairs){
            int index = columnNameIndices.get(p.getColumnID().getColumnName());
            if(descriptions.get(index).isUnique() ||(descriptions.get(index).equals(primaryKeyColumn))){
                Entry entry = defaultValues.get(index).setValue(p.getValue());
                if(isInColumn(p.getColumnID().getColumnName(),entry)){
                    return true;
                }
            }
            return false;
        }
        return false;
        
        
    }
    
    
    //remove all B-Tree indexes on these rows
    private void removeIndexes(ArrayList<Row> rows){
        for(ColumnDescription description: descriptions){
            ColumnIndex index = columnIndex.get(description.getColumnName());
            if(index != null){
                index.deleteRows(rows);
            }
        }
    }
    
    //add these rows to all B-Tree indexes
    private void addIndexes(ArrayList<Row> rows){
        for(ColumnDescription description: descriptions){
            ColumnIndex index = columnIndex.get(description.getColumnName());
            if(index != null){
                index.addRows(rows);
            }
        }
    }
    
    //remove all indexes on the row
    private void removeIndex(Row row){
        for(ColumnDescription description: descriptions){
            ColumnIndex index = columnIndex.get(description.getColumnName());
            if(index != null){
                index.deleteRow(row);
            }
        }
    }
    
    //delete from the table with a delete query
    void delete(DeleteQuery query){
        usedIndexing = false;
        //if we can, use B-Trees
        ArrayList<Row> rowsToDelete;        
        if(isFullyIndexed(query.getWhereCondition())){
            rowsToDelete = filter(table,query.getWhereCondition());
            HashSet<Row> hs = new HashSet<>(rowsToDelete);
            for(int i = 0; i < table.size();i++){
                if(hs.contains(table.get(i))){
                    removeIndex(table.get(i));
                    table.remove(i);
                    //since indices shifted due to the deletion, the current index must be looked at again
                    i--;
                }
                
            }
        }
        else{        
            for(int i = 0; i < table.size();i++){
                if(table.get(i).isTrue(query.getWhereCondition())){
                    removeIndex(table.get(i));
                    table.remove(i);
                    //since indices shifted due to the deletion, the current index must be looked at again
                    i--;
                }
                
            }
        }
    }
    
    //create a new index with a create index query
    void createIndex(CreateIndexQuery query){
        usedIndexing = false;
        String columnName = query.getColumnName();
        //if column is already indexed, return
        if(columnIndex.get(name)!= null){
            return;
        }
        ColumnIndex index = new ColumnIndex(columnName);
        columnIndex.put(columnName,index);
        index.addRows(table);
    }
    
    //filter out the table with WHERE, using B-Trees if we can
    private ArrayList<Row> getRowsWhere(Condition c){
        ArrayList<Row> whereRows = new ArrayList<>();
        if(isFullyIndexed(c)){
            //use b-tree
            whereRows = filter(table,c);
        }else{
            //must search linearly
            for(Row row: table){
                if(row.isTrue(c)){
                    whereRows.add(row);
                }               
            }
        }
        return whereRows;
    }
    
    //get distinct rows from a list of rows, in the same order they were given
    private ArrayList<Row> getDistinct(ArrayList<Row> results){
        HashSet<Row> distinctRows = new HashSet<>();
        ArrayList<Row> distinctResults = new ArrayList<>();
        for(Row row:results){
            if(distinctRows.contains(row)){
                continue;
            }
            distinctRows.add(row);
            distinctResults.add(row);
        }
        return distinctResults;   
    }
    
    //get a list of all columns, which must be added to the results in the event the query contained a '*' 
    private String[] getStarColumns(){
        String[] starColumns = new String[descriptions.size()];
        for(int i = 0;i<starColumns.length;i++){
            starColumns[i] = descriptions.get(i).getColumnName();
        }      
        return starColumns;
    }
    
    
    //get the results of a select query on this table
    ResultSet select(SelectQuery query){
        usedIndexing = false;        
        ArrayList<Row> results = new ArrayList<>();
        ColumnID[] columns = query.getSelectedColumnNames();
        ArrayList<FunctionInstance> functions = query.getFunctions();
        Condition condition = query.getWhereCondition();                
        ResultSetBuilder resultBuilder = new ResultSetBuilder();

        resultBuilder.addColumns(columns,getStarColumns(),functions);
        //filter out the data with WHERE, using B-Trees if we can
        ArrayList<Row> whereRows = getRowsWhere(query.getWhereCondition());
        //sort results with Order By
        OrderBy[] orderBys = query.getOrderBys();
        if(orderBys.length != 0){
            whereRows = sort(whereRows,orderBys);
        }
        //select the columns we want
        for(Row row: whereRows){
            //create new row from columns
            Row selectedRow = row.select(columns);
            results.add(selectedRow);          
        }    
        //apply Distinct
        if(query.isDistinct()){
            results = getDistinct(results);
        }       
        resultBuilder.addRows(results);
        //apply functions
        for(FunctionInstance f:functions){
            Entry entry = evaluateFunction(f,results);
            String distinct = f.isDistinct ? "distinct ": "";
            String columnName = f.function.name()+"("+distinct+f.column.getColumnName()+")";
            resultBuilder.fillColumn(columnName,entry);
        }
               
        return resultBuilder.build();
    }
    
    //This evaluates a given function on a given set of Rows
    private Entry evaluateFunction(FunctionInstance f,ArrayList<Row> results){
        Entry[] column = getColumn(results,f.column.getColumnName());
        switch(f.function){
            
            case AVG:
                return evaluateAVG(column);
            case COUNT:
                //this is the only function that can have the distinct keyword
                if(f.isDistinct){
                    return evaluateDistinctCount(column);
                }
                return evaluateCOUNT(column);
            case MAX:
                return evaluateMAX(column);
            case MIN:
                return evaluateMIN(column);
            //last case is sum, which we leave as default to make the compiler happy
            default:
                return evaluateSUM(column);        
        }
    }
    
    //this method extracts a column as an Entry[] for an ArrayList of Row objects
    private Entry[] getColumn(ArrayList<Row> results,String columnName){
        Entry[] column = new Entry[results.size()];
        for(int i = 0;i< column.length;i++){
            column[i] = results.get(i).get(columnName);
        }
        return column;
    }
    
    //these next six methods evaluate various SQL SELECT functions on a given column  
    private Entry evaluateAVG(Entry[] column){
        //if the array is empty return a null entry
        if(column.length == 0){
            return new IntegerEntry();
        }        
        //Varchar and boolean are not defined for this function
        if(column[0].getType().equals(String.class) ||column[0].getType().equals(Boolean.class)){
            throw new IllegalArgumentException("AVG function is only defined for int and decimal types.");
        }
        Double total = 0.0;
        for(Entry entry:column){
            //the value is either an Integer or Float. Either way, we want to add it to a float
            //we just must get rid of null values first
            if(entry.getValue()==null){
                continue;
            }
            if(entry instanceof DecimalEntry){
                total += (Double) entry.getValue();
            }else{
                total += (Integer) entry.getValue();
            }
        }
        return new DecimalEntry(total/column.length);
    }
    private Entry evaluateCOUNT(Entry[] column){
        return new IntegerEntry(column.length);
    } 
    private Entry evaluateDistinctCount(Entry[] column){
        //unlike by earlier distinct, where we needed the distinct rows
        //in order, here we only care about the absolute number, so we can simple put them in 
        //a hashset and get its size
        HashSet<Entry> distinct= new HashSet<>(Arrays.asList(column));
        return new IntegerEntry(distinct.size());
    }
    private Entry evaluateMAX(Entry[] column){
        //if the array is empty return a null entry
        if(column.length == 0){
            return new IntegerEntry();
        }        
        // Boolean is not defined for this function
        if(column[0].getType().equals(Boolean.class)){
            throw new IllegalArgumentException("MAX function is only defined for int and decimal and varchar types.");
        }
        Entry max = column[0];
        for(Entry entry:column){
            if(entry.compareTo(max)>0){
                max = entry;
            }            
        }
        return max;
    }    
    private Entry evaluateMIN(Entry[] column){
        //if the array is empty return a null entry
        if(column.length == 0){
            return new IntegerEntry();
        }        
        //Boolean is not defined for this function
        if(column[0].getType().equals(Boolean.class)){
            throw new IllegalArgumentException("MIN function is only defined for int and decimal and varchar types.");
        }
        Entry min = column[0];
        for(Entry entry:column){
            if(entry.compareTo(min)<0){
                min = entry;
            }            
        }
        return min;
    }    
    private Entry evaluateSUM(Entry[] column){
        //if the array is empty return a null entry
        if(column.length == 0){
            return new IntegerEntry();
        }        
        //Varchar and boolean are not defined for this function
        if(column[0].getType().equals(String.class) ||column[0].getType().equals(Boolean.class)){
            throw new IllegalArgumentException("SUM function is only defined for int and decimal types.");
        }
        Double total = 0.0;
        for(Entry entry:column){
            //the value is either an Integer or Float. Either way, we want to add it to a float
            //we just must get rid of null values first
            if(entry.getValue()==null){
                continue;
            }
            if(entry instanceof DecimalEntry){
                total += (Double) entry.getValue();
            }else{
                total += (Integer) entry.getValue();
            }
        }
        if(column[0].getType().equals(Integer.class)){
            return new IntegerEntry(total.intValue());
        }
        return new DecimalEntry(total/column.length);

    }        
    
    //sort the rows using the specified orderbys
    private ArrayList<Row> sort(ArrayList<Row> results, OrderBy[] orderBys){
        //when we compare rows, it is crucial to know what column in question we are 
        //dealing with, as otherwise comparison doesn't make sense
        
        
        Row[] sorted = results.toArray(new Row[0]);
        Utility.mergeSort(sorted,orderBys); 
        return new ArrayList<Row>(Arrays.asList(sorted));
    }
    
    //get the column names of this table
    String[] getColumnNames(){
        String[] names = new String[descriptions.size()];
        for(int i = 0;i< names.length;i++){
            names[i] = descriptions.get(i).getColumnName();
        }
        return names;
    }
    //get the column types of this table
    Class[] getColumnTypes(){
        Class[] types = new Class[defaultValues.size()];
        for(int i = 0;i< types.length;i++){
            types[i] = defaultValues.get(i).getType();
        }
        return types;  
    }
    
    //get the rows of this table
    ArrayList<Row> getRows(){
        return table;
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
        
    //b trees on indexed columns are only helpful when all columns in the where clause are indexed
    //so we check if that is true
    private boolean isFullyIndexed(Condition c){
        if(c == null){
            //there is no condition
            //b-trees would be pointless
            return false;
        }
        boolean isIndexed = true;
        if(c.getLeftOperand() instanceof Condition){
            isIndexed = isIndexed && isFullyIndexed((Condition)c.getLeftOperand());
        }
        if(c.getRightOperand() instanceof Condition){
            isIndexed = isIndexed && isFullyIndexed((Condition)c.getRightOperand());
        }
        if(c.getLeftOperand() instanceof ColumnID){
            isIndexed = isIndexed && (null != columnIndex.get(((ColumnID)c.getLeftOperand()).getColumnName()));
        }
        if(c.getRightOperand() instanceof ColumnID){
            isIndexed = isIndexed && (null != columnIndex.get(((ColumnID)c.getRightOperand()).getColumnName()));
        }
        //if both operands are columns, such as column1 < column2, return false
        if((c.getRightOperand() instanceof ColumnID) && (c.getLeftOperand() instanceof ColumnID)){
            return false;
        }
        return isIndexed;
    }
    
    //get the intersection of to ArrayList<Row> objects
    private ArrayList<Row> intersection(ArrayList<Row> setA,ArrayList<Row> setB){
        ArrayList<Row> result = new ArrayList<>();
        HashSet<Row> hash = new HashSet<>(setA);
        for(Row row:setB){
            if(hash.contains(row)){
                result.add(row);
            }
        }
        return result;
    }
   
    //get the union of to ArrayList<Row> objects
    private ArrayList<Row> union(ArrayList<Row> setA,ArrayList<Row> setB){
        ArrayList<Row> result = new ArrayList<>(setA);
        HashSet<Row> hash = new HashSet<>(setA);
        for(Row row:setB){
            if(!hash.contains(row)){
                result.add(row);
            }
        }
        return result; 
    }
    
    
    //evauluate the expresion represented by l, the left operand, r the right operand, and the operator
    private boolean evaluateExpression(Object l,Object r,Operator operator){
        if(!(l.getClass().equals(r.getClass()))){
            throw new IllegalArgumentException("Operands must evaluate to compatible data types. These were:"+l.getClass()+" and "+r.getClass() +" valued at:"+l+" and "+r);
        }
        
        int comparison = ((Comparable) l).compareTo(l.getClass().cast(r));
        switch(operator){
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
        //if operator was OR or AND
        throw new IllegalArgumentException("operator and operands don't match");
    }
    
    
    //convert an object obtained from parsing the string given by Condition.get(Left/Right)Operand() into an Entry object
    private Entry convertObjectToEntry(Object o){
        if(o instanceof String){
            return new VarCharEntry((String) o,false);
        }
        if(o instanceof Integer){
            return new IntegerEntry((Integer) o);
        }        
        if(o instanceof Double){
            return new DecimalEntry((Double) o);
        }         
        if(o instanceof Boolean){
            return new BooleanEntry((Boolean) o);
        } 
        throw new IllegalArgumentException("Could not convert Object to known Entry type");
    }
    
    //give back an operator as git from a condition in a more managable form, the actual data, not just a string or another condition 
    private Object parseOperand(Object operand,ArrayList<Row> rows){
        //if operator was given as a string, convert it to appropriate data type
        if(operand instanceof String){
            return convertStringToDataType((String) operand);
        }
        //convert boolean to ArrayList<Row>
        if(operand instanceof Boolean){
            return ((Boolean)operand) ? rows : new ArrayList<Row>();
        }
        //if operator is a condition, recursively parse it into ArrayList<Row>
        if(operand instanceof Condition){
            return filter(rows,(Condition)operand);
        }
        //otherwise it is a ColumnID. We then leave it as is
        return operand;
    }



    //filter out the rows with the given condition
    public ArrayList<Row> filter(ArrayList<Row> rows, Condition c){
        usedIndexing = true;
        //as per https://www.w3schools.com/sql/sql_update.asp if there is no where clause, condition is true
        if(c == null){
            return rows;
        } 
        
        
        //parse operands
        Object leftOperand = parseOperand(c.getLeftOperand(),rows);
        Object rightOperand = parseOperand(c.getRightOperand(),rows);
        //for simplicity
        Operator operator = c.getOperator();
              
        /*first deal with simple case, operator is boolean.
         *if operator is boolean, both operands must either:
         * 1) Booleans which we already converted to ArrayList<Row> as False = empty set, true = universal set
         * 2) Conditions, which we evaluated to: ArrayList<Row> recursively
         * otherwise, throw an exception
         *
        */
        if(operator == Operator.OR){
            if((leftOperand instanceof ArrayList)&&(rightOperand instanceof ArrayList)){
                //we can evaluate the expression
                return union((ArrayList<Row>)leftOperand,(ArrayList<Row>) rightOperand);             
            }
            throw new IllegalArgumentException("Boolean Operators cannot have non-boolean operands");
        }
        if(operator == Operator.AND){
            if((leftOperand instanceof ArrayList)&&(rightOperand instanceof ArrayList)){
                //we can evaluate the expression
                return intersection((ArrayList<Row>)leftOperand,(ArrayList<Row>) rightOperand);             
            }
            throw new IllegalArgumentException("Boolean Operators cannot have non-boolean operands");
        }
        
        /* at this point the operand must be comparison
         * possibilities:
         * 1) both operands are ColumnID: B-trees should not be used (other evaluate condition method is used)
         * 2) both operands are data, either String, Integer, or Double: evaluate as a Boolean and return either a new ArrayList or rows
         * 3) one is a ColumnID, one is data: finally, use b-trees
         *
        */
        
        if((leftOperand instanceof ColumnID)&&(rightOperand instanceof ColumnID)){
            throw new IllegalArgumentException("B-Trees should not have been used on this query");
        }
        if(!(leftOperand instanceof ColumnID)&& !(rightOperand instanceof ColumnID)){
            //evaluate as a boolean
            return evaluateExpression(leftOperand,rightOperand,operator) ? rows : new ArrayList<Row>();
        }
        //if we are still here, one operand is a ColumnID, one is data
        if(leftOperand instanceof ColumnID){
            String name = ((ColumnID) leftOperand).getColumnName();
            ColumnIndex index = columnIndex.get(name);
            if(index == null){
                throw new IllegalArgumentException("Index not found");
            }
            //convert right operand into Entry
            Entry value = convertObjectToEntry(rightOperand);
            //if operands are incompatible, throw an exception 
            if(!value.getType().equals(defaultValues.get(columnNameIndices.get(name)).getType())){
                //if one is a double and one an int, don't throw an exception
                if(!((value.getType().equals (Integer.class)) &&(defaultValues.get(columnNameIndices.get(name)).getType().equals(Double.class)))){
                    if(!((value.getType().equals (Double.class)) &&(defaultValues.get(columnNameIndices.get(name)).getType().equals(Integer.class)))){
                        throw new IllegalArgumentException("Operands must evaluate to compatible data types. Here the column was of a different type than the right operand.");
                    }
                }
            }
            
            //return a copy of the array to avoid an alias bug
            ArrayList<Row> returnVal = (ArrayList<Row>)index.getRows(operator,value);
            return (returnVal == null) ? new ArrayList<Row>() :(ArrayList<Row>) returnVal.clone();
        }
        //column may not be in the right operand
        if(rightOperand instanceof ColumnID){
            throw new IllegalArgumentException("Column may not be a right operand");
        }
        throw new IllegalArgumentException("Condition could not be parsed");
    }
    
    //return true if the most recent query ran on this table used B-Tree indexing, false otherwise. Used for debugging purposes
    public boolean lastQueryUsedIndexing(){
        return usedIndexing;
    }  
}