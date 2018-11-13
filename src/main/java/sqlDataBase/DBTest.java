package sqlDataBase;
import static java.lang.System.out;

public class DBTest{

    //Demo displays the funcionality of this database
    public static void main(String[] args){
        DataBase db = createAndInsert();
        update(db);
        select(db);
        createIndex(db);
        delete(db);
        
        out.println("That concludes a basic demo of this SQl database's functionality");
    }
    
    //display create and insert functionality
    private static DataBase createAndInsert(){
        DataBase db = new DataBase();
        out.println("This program will test the functionality of this SQL database.");
        out.println("Please open the CMD into the full screen to properly view the results");
        out.println("First, we will create a table, and print out the resultSet.");
        String q = "CREATE TABLE YCStudent"
		+ "("
		+ " BannerID int,"
		+ " SSNum int UNIQUE,"
		+ " FirstName varchar(255),"
		+ " LastName varchar(255) NOT NULL,"
		+ " GPA decimal(4,8) DEFAULT 0.00,"
		+ " CurrentStudent boolean DEFAULT true,"
		+ " PRIMARY KEY (BannerID)"
		+ ");";
        printResult(q,db);
        out.println("");
        out.println("");

        out.println("Our table is still empty. Lets add some random data with insert row!");
        out.println("This time we will show the resultSet only after finishing all the insertions.");

        q =  "INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,973);" ;
        printQuery(q,db);
        q =  "INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Josh','Newman',3.0, true    ,800012346,784);"; 
        printQuery(q,db);
        q =  "INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('David','Berg',4.3, true    ,800015345,875);" ;
        printQuery(q,db);
        q =  "INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Avigdor','Spontanity',2.3, false    ,810012345,776);" ;
        printQuery(q,db);
        q =  "INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ernie','Bert',4.0, false    ,800002345,777);" ;
        printQuery(q,db);
        q =  "INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni2',1.3, true    ,800999345,778);" ;
        printQuery(q,db);
        q =  "INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ryan','Platypusberg',0, false    ,870012345,273);" ;
        printQuery(q,db);
        q =  "INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Gettysburg','Adresswitz',2.0, true    ,888012345,773);"  ;      
        printQuery(q,db);
        q =  "INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Sandy','Jackson',1.2, false    ,100012345,201);" ;
        printQuery(q,db);
        q =  "INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Berg','Berger',3.3, false    ,800012388,713);" ;
        ResultSet r = printQuery(q,db);    
        out.println("");
        out.println("That should be enough. Now For the Results");
        r.print();
        out.println("");

        out.println("To actually see the entire table, lets select everything and see the result.(The Parser unfortunately does not give them in order)");
        
        q = "Select * from YCStudent";
        printResult(q,db);

        
        return db;
        
    }
    
    //display update functionality. Should be called after creatAndInsert()
    private static void update(DataBase db){
        String q;
        out.println("");
        out.println("Now lets test Update.");
        out.println("First lets update with a simple where condition");
        q = "UPDATE YCStudent SET LastName='Goldfeder' WHERE SSNum > 720;";
        printResult(q,db);
        out.println("lets select all to see what changed.");
        q = "Select * from YCStudent;";
        printResult(q,db);
        
        out.println("Lets get a little more complex...");
        q = "UPDATE YCStudent SET LastName='Bergmanfeld',FirstName = 'Steven' WHERE SSNum < 720 AND FirstName <> 'Ryan';";
        printResult(q,db);
        out.println("lets select all to see what changed.");
        q = "Select * from YCStudent;";
        printResult(q,db);

        out.println("We can also update every row!");
        q = "UPDATE YCStudent SET CurrentStudent = true;";
        printResult(q,db);
        out.println("lets select all to see what changed.");
        q = "Select * from YCStudent;";
        printResult(q,db);
    }
    
    //displays select functionality. Should be called after update()
    private static void select(DataBase db){
        String q;
        out.println("");
        out.println("Now lets test out some of the Select functionality.");
        out.println("First lets try a simple order by.");
        q = "Select SSNum from YCStudent ORDER BY SSNum";
        printResult(q,db);
        
        out.println("We can also reverse the order.");
        q = "Select SSNum from YCStudent ORDER BY SSNum DESC";
        printResult(q,db);        
        
        out.println("Lets get a little more complex...");
        q = "Select SSNum, FirstName, LastName from YCStudent ORDER BY LastName, FirstName DESC, SSNum";
        printResult(q,db);

        out.println("Lets do a quick display of some functions");
        q = "Select AVG(SSNum), MAX(FirstName), MIN(LastName), COUNT(LastName),COUNT(distinct LastName),SUM(BannerID) from YCStudent;";
        printResult(q,db);
        q = "Select AVG(SSNum), SSNum from YCStudent;";
        printResult(q,db);
        out.println("");
        out.println("Now we will do select distinct");
        q = "Select distinct LastName from YCStudent;";
        printResult(q,db);
        q = "Select distinct FirstName,LastName from YCStudent;";
        printResult(q,db);        
        out.println("");
        out.println("Lets try out some operators. First lets take a look at everything:");
        q = "Select LastName,SSNum from YCStudent;";
        printResult(q,db);
        out.println("");

        q = "Select LastName,SSNum from YCStudent WHERE SSNum = 713;";
        printResult(q,db);        
        out.println("");
           
        q = "Select LastName,SSNum from YCStudent WHERE SSNum > 713;";
        printResult(q,db);
        out.println("");

        q = "Select LastName,SSNum from YCStudent WHERE SSNum < 713;";
        printResult(q,db);
        out.println("");
        
        q = "Select LastName,SSNum from YCStudent WHERE SSNum <> 713;";
        printResult(q,db);
        out.println("");
        
        q = "Select LastName,SSNum from YCStudent WHERE SSNum >= 713;";
        printResult(q,db);
        out.println("");
        
        q = "Select LastName,SSNum from YCStudent WHERE SSNum <= 713;";
        printResult(q,db);        
        out.println("");
    }
    
    //displays createIndex functionality. Should be called after select
    private static void createIndex(DataBase db){
        String q;
        out.println("");
        out.println("Now lets try create index.");
        out.println("To verify it worked, instead of printing just the result set, we will also ");
        out.println("print a boolean stored in the resultSet which is only set to true if B-Tree indexing was used");

        out.println("First lets do a simple select and see that there is no indexing");
        q = "Select SSNum from YCStudent WHERE SSNum > 450 ORDER BY SSNum";
        ResultSet r = printResult(q,db);
        out.println("USED INDEXING: " + r.getUsedIndexing());
        out.println("");
        out.println("Now lets add indexing and see what happens.");
        
        q = "CREATE INDEX SSNum_Index on YCStudent (SSNum);";
        printResult(q,db);
        
        out.println("");
        out.println("Now lets try again. There should be indexing this time.");
        
        q = "Select SSNum from YCStudent WHERE SSNum > 450 ORDER BY SSNum";
        r = printResult(q,db);   
        out.println("USED INDEXING: " + r.getUsedIndexing());
        
        
    }
    
    //displays delete functionality. Should be called after createIndex;
    private static void delete(DataBase db){
        String q;
        out.println("");
        out.println("Now lets try delete.");
        q = "Delete YCStudent WHERE LastName = 'Bergmanfeld';";
        printResult(q,db);
        out.println("lets select all to see what changed.");
        q = "Select * from YCStudent;";
        printResult(q,db);
        
        q = "Delete YCStudent WHERE LastName = 'Goldfeder' AND SSNum < 800;";
        printResult(q,db);
        out.println("lets select all to see what changed.");
        q = "Select * from YCStudent;";
        printResult(q,db);
        
        out.println("Finally, lets delete all rows.");
        q = "Delete YCStudent;";
        printResult(q,db);
        out.println("lets select all to see what changed.");
        q = "Select * from YCStudent;";
        printResult(q,db);
        out.println("");
        out.println("");

    }
    
    
    //execute and print query and result
    private static ResultSet printResult(String query,DataBase db){
        ResultSet result = db.execute(query);
        out.println(query);
        result.print();
        return result;
    }
    //just execute and print query
    private static ResultSet printQuery(String query,DataBase db){
        ResultSet result = db.execute(query);
        out.println(query);
        return result;
    }

}  