package sqlDataBase.test;

import net.sf.jsqlparser.JSQLParserException;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.*;
import static org.junit.Assert.*;
import org.junit.Test;
import sqlDataBase.*;

public class SQLTest{
    private DataBase testDataBase;
    private ResultSet r;
    private static String massiveString = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";

    @Test    
    public void createQueryTest(){
        testDataBase = new DataBase();            
        String query = "CREATE TABLE YCStudent"
            + "("
            + " BannerID int,"
            + " SSNum int UNIQUE,"
            + " FirstName varchar(255),"
            + " LastName varchar(255) NOT NULL,"
            + " GPA decimal(4,8) DEFAULT 0.00,"
            + " CurrentStudent boolean DEFAULT true,"
            + " PRIMARY KEY (BannerID)"
            + ");";
        r=testDataBase.execute(query);
        //make sure it contains no actual data
        assertEquals(0,r.getResults().length);  
        //make sure the correct number of columns where added
        assertEquals(6,r.getColumnNames().length);  
    }
    
    @Test    
    public void createQueryTestWithoutPrimaryKey(){
        testDataBase = new DataBase();            
        String query = "CREATE TABLE YCStudent"
            + "("
            + " BannerID int,"
            + " SSNum int UNIQUE,"
            + " FirstName varchar(255),"
            + " LastName varchar(255) NOT NULL,"
            + " GPA decimal(1,2) DEFAULT 0.00,"
            + " CurrentStudent boolean DEFAULT true,"
            + ""
            + ");";
        r=testDataBase.execute(query);
        //make sure it contains no actual data
        assertEquals(1,r.getResults().length);  
        //make sure the correct number of columns where added
        assertEquals(1,r.getColumnNames().length);  
        assertEquals("SuccessStatus",r.getColumnNames()[0]);
        assertEquals(false,r.getResults()[0][0]);
    }       
    
    @Test
    public void insertRowTest(){
        createQueryTest();
        
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810012345,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        
        r=testDataBase.execute("SELECT * FROM YCStudent");
        assertEquals(2,r.getResults().length);
        assertEquals(6,r.getColumnNames().length);
        
    }

    @Test
    public void insertRowToBigVarCharTest(){
        createQueryTest();
        
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('"+massiveString+"','Almoni',4.0, false    ,800012345,123);");
        assertEquals(false,((Boolean)(r.getResults()[0][0])));  

    }
    
    @Test
    public void insertRowToManyFractionalDigitsTest(){
        createQueryTest();
        
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.01111111, false    ,800012345,123);");
        assertEquals(false,((Boolean)(r.getResults()[0][0])));  
        
    }
    
    @Test
    public void insertRowToManyWholeDigitsTest(){
        createQueryTest();
        
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',42222.0, false    ,800012345,123);");
        assertEquals(false,((Boolean)(r.getResults()[0][0])));  
        
    }
    
    @Test
    public void testDefaults(){
        createQueryTest();
        
        r=testDataBase.execute("INSERT INTO YCStudent (LastName, BannerID) "
        + "VALUES ('Ploni' ,800012345);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("Select * FROM YCStudent;");
        assertEquals(new Object[][]{{true ,0.00,null,800012345,"Ploni",null}},r.getResults());
    }
    
    @Test
    public void insertIntoNonExtantColumn(){
        createQueryTest();
        
        r=testDataBase.execute("INSERT INTO YCStudent (FAKECOLUMN, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,123);");
        assertEquals(false,((Boolean)(r.getResults()[0][0])));  

    }
    
    @Test 
    public void insertRowNonUniquePrimaryColumnEntryTest(){
        createQueryTest();
        
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,1);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID) "
        + "VALUES ('Ploni1','Almoni1',3.0, false    ,800012345);");
        assertEquals(false,((Boolean)(r.getResults()[0][0])));  
    }
    
    @Test 
    public void insertRowNullPrimaryColumnEntryTest(){
        createQueryTest();
        
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent) "
        + "VALUES ('Ploni1','Almoni1',3.0, false);");
        assertEquals(false,((Boolean)(r.getResults()[0][0])));  
    }
    
    @Test 
    public void insertRowNullInNonNullColumnTest(){
        createQueryTest();
        
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, GPA, CurrentStudent,BannerID) "
        + "VALUES ('Ploni1',3.0, false,800444333);");
        assertEquals(false,((Boolean)(r.getResults()[0][0])));  

    }
    
    @Test 
    public void insertRowNonUniqueIntoUniqueColumnTest(){
        createQueryTest();
        
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,12);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
       
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni1','Almoni1',3.0, false    ,800012345,12);");
        assertEquals(false,((Boolean)(r.getResults()[0][0])));  

    }
    
    @Test
    public void testUpdateAndSelectStarToVerify(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        
        
        
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810012345,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        testDataBase.execute("UPDATE YCStudent SET GPA=3.0,FirstName='Judah' WHERE LastName = 'Almoni' and SSNum=123;");
        r=testDataBase.execute("SELECT * FROM YCStudent");
        assertEquals(new Object[]{false,3.0,"Judah",800012345 ,"Almoni",123},r.getResults()[0]);      
    }
    
    @Test
    public void testUpdateWithIndexingOnPrimaryKey(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        
        r=testDataBase.execute("UPDATE YCStudent SET GPA=3.0,FirstName='Judah' WHERE BannerID=1;");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        r=testDataBase.execute("SELECT * FROM YCStudent ORDER BY BannerID ASC");

        assertEquals(new Object[]{false,3.0,"Judah",1 ,"Almoni",123},r.getResults()[0]);  

        r=testDataBase.execute("SELECT * FROM YCStudent WHERE BannerID > 4 AND BannerID <777777377 ORDER BY BannerID ASC");
        assertEquals(4,r.getResults().length);
    }

    @Test 
    public void testUpdateAll(){
                createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        
        r=testDataBase.execute("UPDATE YCStudent SET GPA=3.0,FirstName='Judah';");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        r=testDataBase.execute("SELECT GPA,FirstName FROM YCStudent");
        for(int i = 0;i<15;i++){
            assertEquals(new Object[]{3.0,"Judah"},r.getResults()[i]);        
        }
    }

    @Test 
    public void testUpdateTooBigVarCharFails(){
                createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        
        r=testDataBase.execute("UPDATE YCStudent SET GPA=3.0,FirstName='"+massiveString+"';");
        assertEquals(false,((Boolean)(r.getResults()[0][0])));      
    }
    
    @Test 
    public void testUpdateToobigFractionalDecimalFails(){
                createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        
        r=testDataBase.execute("UPDATE YCStudent SET GPA=3.121212210,FirstName='Judah';");
        assertEquals(false,((Boolean)(r.getResults()[0][0])));      

    }
    
    @Test 
    public void testUpdateToobigWholeDecimalFails(){
                createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        
        r=testDataBase.execute("UPDATE YCStudent SET GPA=312121221.0,FirstName='Judah';");
        assertEquals(false,((Boolean)(r.getResults()[0][0])));      

    }
       
    @Test
    public void testDelete(){
        createQueryTest();
        testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,123);");
        testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810012345,124);");
        testDataBase.execute("UPDATE YCStudent SET GPA=3.0,FirstName='Judah' WHERE LastName = 'Almoni' and SSNum=123;");
        r=testDataBase.execute("DELETE FROM YCStudent where SSNum > 123");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("SELECT * FROM YCStudent");
        assertEquals(new Object[]{false,3.0,"Judah",800012345 ,"Almoni",123},r.getResults()[0]);    
        assertEquals(1,r.getResults().length);
    }

    @Test 
    public void testSelect(){
        createQueryTest();
        testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('APloni','Almoni',4.0, false    ,800012345,123);");
        testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('BPloni','Almoni',4.0, false    ,800012347,125);");
        testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('CPloni','Almoni',4.0, false    ,800012348,126);");
        testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('APloni','Almoni',4.0, false    ,800012349,127);");
        
        r=testDataBase.execute("SELECT  AVG(GPA),GPA FROM YCStudent ;");
        assertEquals(5,r.getResults().length);
        assertEquals(new Object[]{4.0,4.0},r.getResults()[0]);    
        assertEquals(new Object[]{4.0,4.0},r.getResults()[1]);    
        assertEquals(new Object[]{4.0,4.0},r.getResults()[2]);    
        assertEquals(new Object[]{4.0,4.0},r.getResults()[3]);
        assertEquals(new Object[]{4.0,4.0},r.getResults()[4]);    
        r=testDataBase.execute("SELECT FirstName FROM YCStudent where SSNum<125 ORDER BY FirstName;");
        assertEquals(2,r.getResults().length);
        assertEquals(new Object[]{"APloni"},r.getResults()[0]);    
        assertEquals(new Object[]{"Ploni"},r.getResults()[1]);    
        
        
        r=testDataBase.execute("SELECT SSNum,COUNT(distinct FirstName) FROM YCStudent");
        assertEquals(new Object[]{123,4},r.getResults()[0]);      
        assertEquals(new Object[]{124,4},r.getResults()[1]);
        assertEquals(new Object[]{125,4},r.getResults()[2]);
        assertEquals(new Object[]{126,4},r.getResults()[3]);
        assertEquals(new Object[]{127,4},r.getResults()[4]);

    }


    @Test
    public void testCount(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        r=testDataBase.execute("SELECT COUNT(BannerID) FROM YCStudent");
        assertEquals(new Object[]{15},r.getResults()[0]);  
        assertEquals(1,r.getResults().length);

    }

    @Test
    public void testCountDistinct(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        r=testDataBase.execute("SELECT COUNT(distinct FirstName) FROM YCStudent");
        assertEquals(new Object[]{2},r.getResults()[0]);  
        assertEquals(1,r.getResults().length);

    }

    @Test
    public void testAverage(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        r=testDataBase.execute("SELECT AVG(BannerID) FROM YCStudent");
        assertEquals(new Object[]{647060329.600000},r.getResults()[0]);  
        assertEquals(1,r.getResults().length);

    }

    @Test
    public void testSum(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        r=testDataBase.execute("SELECT SUM(BannerID) FROM YCStudent");
        assertEquals(new Object[]{2147483647},r.getResults()[0]);  
        assertEquals(1,r.getResults().length);

    }

    @Test
    public void testMax(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        r=testDataBase.execute("Select MAX(BannerID) FROM YCStudent ORDER BY BannerID ASC");
        assertEquals(new Object[]{833312345},r.getResults()[0]);  
        assertEquals(1,r.getResults().length);

    }

    @Test
    public void testMin(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        r=testDataBase.execute("SELECT MIN(BannerID) FROM YCStudent ORDER BY BannerID ASC");
        assertEquals(new Object[]{1},r.getResults()[0]);  
        assertEquals(1,r.getResults().length);

    }

    //worthwhile to test this because before there was weird bug where they worked individually but not together
    //still test the functions indivisually to see if there is a fail which one it is 
    @Test
    public void testAllSelectFunctions(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        r=testDataBase.execute("SELECT AVG(BannerID),COUNT(BannerID),COUNT(distinct BannerID),COUNT(distinct FirstName),SUM(BannerID),MAX(BannerID),MIN(BannerID) FROM YCStudent ORDER BY BannerID ASC");
        assertEquals(new Object[]{647060329.600000,15,15,2,2147483647 ,833312345,1},r.getResults()[0]);  
        assertEquals(1,r.getResults().length);

    }

    @Test
    public void testSelectDistinct(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0]))); 
        
        r= testDataBase.execute("SELECT distinct FirstName from YCStudent");
        assertEquals(2,r.getResults().length);       
    }
    
    @Test
    public void testComplexOrderBy(){
              createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Blmoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Clmoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Dlmoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Klmoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        
        
        r=testDataBase.execute("SELECT CurrentStudent,GPA,FirstName,BannerID,LastName,SSNum FROM YCStudent ORDER BY FirstName, LastName, BannerID ASC");
       
        Object[][] res  =new Object[][]{new Object[]{false,4.000000 ,               "Ploni"     ,               111012345          ,      "Almoni" ,                  111},
        new Object[]{false  ,                  4.000000 ,               "Ploni" ,                  400012345      ,         "Almoni"    ,               443},
        new Object[]{false  ,                  4.000000 ,               "Ploni" ,                  500012345      ,         "Almoni"    ,               153},
        new Object[]{false  ,                  4.000000 ,               "Ploni" ,                  660012345      ,         "Almoni"    ,               663},
        new Object[]{false  ,                  4.000000 ,               "Ploni" ,                  800012345      ,         "Almoni"    ,               773},
        new Object[]{false  ,                  4.000000 ,               "Ploni" ,                  800012346      ,         "Almoni"    ,               124},
        new Object[]{false  ,                  4.000000 ,               "Ploni" ,                  800016345       ,         "Almoni"   ,                623},
        new Object[]{false  ,                  4.000000 ,               "Ploni" ,                  800123425      ,         "Almoni"    ,               223},
        new Object[]{false  ,                  4.000000 ,               "Ploni" ,                  800712345      ,         "Almoni"    ,               723},
        new Object[]{false  ,                  4.000000 ,               "Ploni" ,                  833312345      ,         "Almoni"    ,               333},
        new Object[]{false  ,                  4.000000 ,               "Ploni"  ,                  800222245     ,          "Blmoni"   ,                225},
        new Object[]{false  ,                  4.000000 ,               "Ploni"  ,                 1              ,         "Clmoni"    ,               123},
        new Object[]{false  ,                  4.000000 ,               "Ploni"  ,                 812012345      ,         "Dlmoni"    ,               983},
        new Object[]{false  ,                  4.000000 ,               "Ploni"  ,                 777777377      ,         "Klmoni"    ,               984},
        new Object[]{false  ,                  4.000000 ,               "Ploni2" ,                 810654445      ,         "Almoni"    ,               987}};
                        
        assertEquals(res,r.getResults());   
        
        
    }
    
    @Test
    public void testOrderByDesc(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Blmoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Clmoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Dlmoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Klmoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        
        
        r=testDataBase.execute("SELECT CurrentStudent,GPA,FirstName,BannerID,LastName,SSNum FROM YCStudent ORDER BY FirstName DESC, LastName DESC, BannerID DESC");
       
        Object[][] res  =new Object[][]{new Object[]{false,4.000000 ,               "Ploni"     ,               111012345          ,      "Almoni" ,                  111},
        new Object[]{false  ,                  4.000000 ,               "Ploni" ,                  400012345      ,         "Almoni"    ,               443},
        new Object[]{false  ,                  4.000000 ,               "Ploni" ,                  500012345      ,         "Almoni"    ,               153},
        new Object[]{false  ,                  4.000000 ,               "Ploni" ,                  660012345      ,         "Almoni"    ,               663},
        new Object[]{false  ,                  4.000000 ,               "Ploni" ,                  800012345      ,         "Almoni"    ,               773},
        new Object[]{false  ,                  4.000000 ,               "Ploni" ,                  800012346      ,         "Almoni"    ,               124},
        new Object[]{false  ,                  4.000000 ,               "Ploni" ,                  800016345       ,         "Almoni"   ,                623},
        new Object[]{false  ,                  4.000000 ,               "Ploni" ,                  800123425      ,         "Almoni"    ,               223},
        new Object[]{false  ,                  4.000000 ,               "Ploni" ,                  800712345      ,         "Almoni"    ,               723},
        new Object[]{false  ,                  4.000000 ,               "Ploni" ,                  833312345      ,         "Almoni"    ,               333},
        new Object[]{false  ,                  4.000000 ,               "Ploni"  ,                  800222245     ,          "Blmoni"   ,                225},
        new Object[]{false  ,                  4.000000 ,               "Ploni"  ,                 1              ,         "Clmoni"    ,               123},
        new Object[]{false  ,                  4.000000 ,               "Ploni"  ,                 812012345      ,         "Dlmoni"    ,               983},
        new Object[]{false  ,                  4.000000 ,               "Ploni"  ,                 777777377      ,         "Klmoni"    ,               984},
        new Object[]{false  ,                  4.000000 ,               "Ploni2" ,                 810654445      ,         "Almoni"    ,               987}};
        
        Object[][] resReverse = new Object[15][0];
        for(int i =0;i<res.length;i++){
            resReverse[i] = res[14-i];
        }
        
        assertEquals(resReverse,r.getResults());   
        
        
    }
       
    @Test
    public void testDeleteAll(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        r=testDataBase.execute("DELETE FROM YCStudent");
        r=testDataBase.execute("SELECT * FROM YCStudent");
        assertEquals(0,r.getResults().length);
    }
    
    @Test
    public void testDeleteWhere(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        Object[][] beforeDelete = testDataBase.execute("SELECT * FROM YCStudent").getResults();
        testDataBase.execute("DELETE FROM YCStudent WHERE GPA <> 4");
        r=testDataBase.execute("SELECT * FROM YCStudent");
        assertEquals(beforeDelete,r.getResults());
        
        testDataBase.execute("DELETE FROM YCStudent WHERE CurrentStudent = false");
        r=testDataBase.execute("SELECT * FROM YCStudent");
        assertEquals(2,r.getResults().length);

    }
    
    @Test 
    public void orderByColumnNotSelected(){       
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        r= testDataBase.execute("SELECT SSNum FROM YCStudent ORDER BY BannerID");
        Object[][] res = new Object[][]{{123},{111},{443},{153},{663},{984},{773},{124},{623},{223},{225},{723},{987},{983},{333}};
        assertEquals(res,r.getResults());
    }
    
    @Test
    public void selectWhereColumnNotSelected(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.1, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.1, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        r= testDataBase.execute("SELECT SSNum FROM YCStudent WHERE GPA <> 4 ORDER BY BannerID ");
        assertEquals(new Object[][]{{123},{225}},r.getResults());
    }   
    
    @Test 
    public void whereCompareIntegerToDecimal(){
         createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0]))); 
        
        r= testDataBase.execute("SELECT SSNum FROM YCStudent WHERE SSNum > 200.565 ORDER BY SSNum");        
        Object[][] res = new Object[][]{{223},{225},{333},{443},{623},{663},{723},{773},{983},{984},{987}};
        assertEquals(res,r.getResults());
    }

    @Test 
    public void whereCompareIntegerToDecimalOnIndexedColumn(){
         createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0]))); 
        
        r= testDataBase.execute("SELECT BannerID FROM YCStudent WHERE BannerID >= 812012345.001 ORDER BY BannerID");        
        assertEquals(833312345,r.getResults()[0][0]);
    }
   
    @Test
    public void testGreaterThan(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000002,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.1, false    ,1810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0]))); 

        r= testDataBase.execute("SELECT BannerID FROM YCStudent WHERE BannerID > 1810654445 ORDER BY BannerID");        
        assertEquals(new Object[0][0],r.getResults());

        
    }    
    
    @Test
    public void testLessThan(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000002,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.1, false    ,1810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0]))); 

        
        r= testDataBase.execute("SELECT BannerID FROM YCStudent WHERE BannerID < 2 ORDER BY BannerID");        
        assertEquals(new Object[][]{{1}},r.getResults());        
    }    

    @Test
    public void testGreaterThanOrEquals(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000002,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.1, false    ,1810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0]))); 


        r= testDataBase.execute("SELECT BannerID FROM YCStudent WHERE BannerID >= 1810654445 ORDER BY BannerID");        
        assertEquals(new Object[][]{{1810654445}},r.getResults());
    }    

    @Test
    public void testLessThanOrEquals(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000002,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.1, false    ,1810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0]))); 

        r= testDataBase.execute("SELECT BannerID FROM YCStudent WHERE BannerID <= 2 ORDER BY BannerID");        
        assertEquals(new Object[][]{{1},{2}},r.getResults());
    }    

    @Test
    public void testEquals(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000002,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.1, false    ,1810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0]))); 

        r= testDataBase.execute("SELECT GPA FROM YCStudent WHERE GPA = 4.1 ORDER BY BannerID");        
        assertEquals(new Object[][]{{4.1}},r.getResults());       
    }    
                          
    @Test
    public void testNotEquals(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000002,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.1, false    ,1810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0]))); 


        r= testDataBase.execute("SELECT GPA FROM YCStudent WHERE GPA <> 4 ORDER BY BannerID");        
        assertEquals(new Object[][]{{4.1}},r.getResults());        
    }    
      
    @Test
    public void testAnd(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000002,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.1, false    ,1810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0]))); 

        r= testDataBase.execute("SELECT GPA FROM YCStudent WHERE GPA <> 4 AND SSNum = 984 ORDER BY GPA");        
        assertEquals(new Object[0][0],r.getResults());
        
    }    

    @Test
    public void testOr(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000002,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.1, false    ,1810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0]))); 
     
        r= testDataBase.execute("SELECT GPA FROM YCStudent WHERE GPA <> 4 OR SSNum = 984 ORDER BY GPA");        
        assertEquals(new Object[][]{{4.0},{4.1}},r.getResults());

    }    
    
    @Test
    public void testAndOr(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, true    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000002,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('John','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.1, false    ,1810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0]))); 

        r= testDataBase.execute("SELECT GPA FROM YCStudent WHERE (GPA <> 4 OR SSNum = 984) AND (FirstName <> 'John') ORDER BY GPA");        
        assertEquals(new Object[][]{{4.1}},r.getResults());

        
    }    
        
    @Test
    public void verifyIndexingOnInsertIntoPrimaryKeyColumn(){
        
        createQueryTest();
        
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,1);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        assertEquals(true,r.getUsedIndexing());
        
        
        
        
        
        
    }

    @Test
    public void verifyIndexingOnInsertIntoUniqueIndexedColumn(){
        
        createQueryTest();
        testDataBase.execute("CREATE INDEX SSNum_Index on YCStudent (SSNum);");

        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,1);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        assertEquals(true,r.getUsedIndexing());
        
        
        
        
        
        
    }   
    
    @Test 
    public void verifyIndexingOnUpdateWhereWithPrimaryKey(){
                createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        
        r=testDataBase.execute("UPDATE YCStudent SET GPA=3.0,FirstName='Judah' WHERE BannerID=1;");
        
        assertEquals(true,r.getUsedIndexing());
    }
    
    @Test 
    public void verifyNoIndexingOnUpdateWhereWithoutPrimaryKey(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        
        r=testDataBase.execute("UPDATE YCStudent SET GPA=3.0,FirstName='Judah' WHERE BannerID=1 AND CurrentStudent = false;");
        
        assertEquals(true,r.getResults()[0][0]);
        assertEquals(false,r.getUsedIndexing());
    }
       
    @Test
    public void verifyIndexingOnSelectWhereWithPrimaryKey(){
         createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        
        r=testDataBase.execute("Select * from YCStudent WHERE BannerID=1;");
        assertEquals(true,r.getUsedIndexing());
        
    }
    
    @Test
    public void verifyIndexingOnDeleteWhereWithPrimaryKey(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        
        r=testDataBase.execute("Delete from YCStudent WHERE BannerID=1;");
        assertEquals(true,r.getResults()[0][0]);
        assertEquals(true,r.getUsedIndexing());
    }
    
    @Test
    public void verifyNoIndexingOnSelectWithoutPrimaryKey(){
         createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        
        r=testDataBase.execute("Select * from YCStudent WHERE BannerID=1 AND SSNum > 312;");
        assertEquals(new Object[0][0],r.getResults());
        assertEquals(false,r.getUsedIndexing());
        
    }

    @Test
    public void verifyIndexingOnUpdateAfterCreateIndexQuery(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        
        r=testDataBase.execute("UPDATE YCStudent SET GPA=3.0,FirstName='Judah' WHERE BannerID=1 AND SSNum>43;");
        
        assertEquals(true,r.getResults()[0][0]);
        assertEquals(false,r.getUsedIndexing());
        
        r=testDataBase.execute("CREATE INDEX SSNum_Index on YCStudent (SSNum);");
        assertEquals(true,r.getResults()[0][0]);
        
        r=testDataBase.execute("UPDATE YCStudent SET GPA=2.0,FirstName='Yehuda' WHERE BannerID=1 AND SSNum>43;");
        
        assertEquals(true,r.getResults()[0][0]);
        assertEquals(true,r.getUsedIndexing());
        
        
        
    }
    
    @Test
    public void verifyIndexingOnSelectAfterCreateIndexQuery(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        
        r=testDataBase.execute("Select * from YCStudent WHERE BannerID=1 AND SSNum > 312;");
        assertEquals(new Object[0][0],r.getResults());
        
        assertEquals(false,r.getUsedIndexing());
        
        r=testDataBase.execute("CREATE INDEX SSNum_Index on YCStudent (SSNum);");
        assertEquals(true,r.getResults()[0][0]);
        r=testDataBase.execute("Select * from YCStudent WHERE BannerID=1 AND SSNum > 312;");
        assertEquals(new Object[0][0],r.getResults());
        assertEquals(true,r.getUsedIndexing());
        
        
        
    }
    
    @Test
    public void verifyIndexingOnDeleteAfterCreateIndexQuery(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        
        r=testDataBase.execute("Delete from YCStudent WHERE BannerID=1 AND SSNum > 312;");
        assertEquals(true,r.getResults()[0][0]);
        
        assertEquals(false,r.getUsedIndexing());
        
        r=testDataBase.execute("CREATE INDEX SSNum_Index on YCStudent (SSNum);");
        assertEquals(true,r.getResults()[0][0]);
        r=testDataBase.execute("Delete from YCStudent WHERE BannerID=1 AND SSNum > 312;");
        assertEquals(true,r.getResults()[0][0]);
        assertEquals(true,r.getUsedIndexing());
        
        
        
    }
    
    @Test
    public void verifyIndexingWorksWithNullEntries(){
        
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alboni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alaoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alconi',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aldoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Judah','Alroni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aleoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        //index firstName
        r=testDataBase.execute("CREATE INDEX FN_Index on YCStudent (FirstName);");
        assertEquals(true,r.getResults()[0][0]);

        //make sure indexing is working
        r=testDataBase.execute("Select FirstName FROM YCStudent WHERE FirstName > 'Alb'");
        assertEquals(true,r.getUsedIndexing());
        assertEquals(new Object[][]{{"Judah"}},r.getResults());
        
        r=testDataBase.execute("Select FirstName FROM YCStudent WHERE FirstName < 'ZZZZZ' ORDER BY FirstName;");
        assertEquals(true,r.getUsedIndexing());
        assertEquals(new Object[][]{{null},{null},{null},{null},{null},{"Judah"}},r.getResults());
        
    
    }
       
    @Test
    public void verifyErrorWhenWhereOperandOfWrongType(){
        
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,400012345,443);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,833312345,333);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,660012345,663);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800222245,225);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,000000001,123);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));   
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,111012345,111);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,812012345,983);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,777777377,984);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));     
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni2','Almoni',4.0, false    ,810654445,987);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));      

        r = testDataBase.execute("Select * FROM YCStudent WHERE LastName = 4");
        assertEquals(false,r.getResults()[0][0]);
        assertEquals("Operands must evaluate to compatible data types. These were:class java.lang.String and class java.lang.Integer valued at:Almoni and 4",r.getErrorMessage());
        
        
        
    }
    
    @Test
    public void verifySelectWrongWhereColumnTypeFailsEvenWithOnlyNullEntries(){
        
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        r = testDataBase.execute("Select * FROM YCStudent WHERE FirstName = 4");
        assertEquals(false,r.getResults()[0][0]);
        assertEquals("Operands must evaluate to compatible data types. These were:class java.lang.String and class java.lang.Integer valued at:sqlDataBase.VarCharEntry@0 and 4",r.getErrorMessage());
        
        
        
        
        
        
        
    }
    
    @Test
    public void verifyUpdateFailUpdatesNothing(){
        
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName,LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        r = testDataBase.execute("UPDATE YCStudent SET GPA=3.0 WHERE FirstName = false");
        assertEquals(false,r.getResults()[0][0]);
        //make sure nothing changed
        r = testDataBase.execute("SELECT GPA FROM YCStudent");
        assertEquals(new Object[][]{{4.0},{4.0},{4.0},{4.0},{4.0},{4.0}},r.getResults());
     
    }
    
    @Test
    public void verifyUpdateFailUpdatesNothingIndexedColumn(){
        
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName,LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        r = testDataBase.execute("UPDATE YCStudent SET BannerID=4 WHERE BannerID = false");
        assertEquals(false,r.getResults()[0][0]);
        //make sure nothing changed
        r = testDataBase.execute("SELECT BannerID FROM YCStudent");
        assertEquals(new Object[][]{{800012345},{800012346},{800123425},{800712345},{800016345},{500012345}},r.getResults());
     
    }
    
    @Test
    public void verifyUpdateUpdatesIndexes(){
        
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName,LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        r = testDataBase.execute("UPDATE YCStudent SET BannerID=4 WHERE BannerID = 800016345");
        assertEquals(true,r.getResults()[0][0]);
        //make sure nothing changed
        
        r = testDataBase.execute("SELECT BannerID FROM YCStudent");
        assertEquals(new Object[][]{{800012345},{800012346},{800123425},{800712345},{4},{500012345}},r.getResults());
        
        r = testDataBase.execute("SELECT BannerID FROM YCStudent WHERE BannerID =4");
        assertEquals(new Object[][]{{4}},r.getResults());
          
    }
    
    @Test
    public void verifyDeleteUpdatesIndexes(){
        
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName,LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  


        r = testDataBase.execute("Delete FROM YCStudent WHERE BannerID = 800016345 ");
        
        r=testDataBase.execute("SELECT BannerID FROM YCStudent;");
        assertEquals(new Object[][]{{800012345},{800012346},{800123425},{800712345},{500012345}},r.getResults());
        
        r = testDataBase.execute("SELECT BannerID FROM YCStudent WHERE BannerID =800016345");
        assertEquals(new Object[][]{},r.getResults());
          
    }
    
    
    @Test
    public void verifyDeleteFailDeletesNothingIndexedColumn(){
        
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName,LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        r = testDataBase.execute("Delete YCStudent BannerID = false");
        assertEquals(false,r.getResults()[0][0]);
        //make sure nothing changed
        r = testDataBase.execute("SELECT BannerID FROM YCStudent");
        assertEquals(new Object[][]{{800012345},{800012346},{800123425},{800712345},{800016345},{500012345}},r.getResults());
     
    }
          
    @Test
    public void verifyDeleteFailDeletesNothing(){
        
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (FirstName,LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Ploni','Almoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Almoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        r = testDataBase.execute("Delete YCStudent  WHERE FirstName = false");
        assertEquals(false,r.getResults()[0][0]);
        //make sure nothing changed
        r = testDataBase.execute("SELECT GPA FROM YCStudent");
        assertEquals(new Object[][]{{4.0},{4.0},{4.0},{4.0},{4.0},{4.0}},r.getResults());
     
    }
           
    @Test
    public void verifySelectWrongWhereColumnTypeFailsEvenWithOnlyNullEntriesOnIndexedColumn(){
        
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alboni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alaoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alconi',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aldoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alroni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aleoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        //index firstName
        r=testDataBase.execute("CREATE INDEX FN_Index on YCStudent (FirstName);");
        assertEquals(true,r.getResults()[0][0]);

        //make sure indexing is working
        r=testDataBase.execute("Select * FROM YCStudent WHERE FirstName > 'Alb'");
        assertEquals(true,r.getUsedIndexing());
        
        
        
        r = testDataBase.execute("Select * FROM YCStudent WHERE FirstName = 4");
        
        assertEquals(false,r.getResults()[0][0]);
        assertEquals("Operands must evaluate to compatible data types. Here the column was of a different type than the right operand.",r.getErrorMessage());
        
        
        
        
        
        
        
    }
    
    @Test 
    public void updateFailsWhenNonNullIsMadeNull(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alboni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alaoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alconi',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aldoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alroni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aleoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        r = testDataBase.execute("UPDATE YCStudent SET LastName = null");
        assertEquals(false,r.getResults()[0][0]);
        assertEquals("Update query may not put null into a non-null or primary key column.",r.getErrorMessage());
    }

    @Test 
    public void updateFailsWhenAddingExtantValueToUniqueColumn(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alboni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alaoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alconi',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aldoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alroni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aleoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        r = testDataBase.execute("UPDATE YCStudent SET SSNum = 153 WHERE SSNum = 623");
        assertEquals(false,r.getResults()[0][0]);
        assertEquals("Update query may not put non-unique entries into a unique or primary key column.",r.getErrorMessage());
    }
       
    @Test 
    public void updateFailsWhenAddingSameValueTwoOrMoreTimesToUniqueColumn(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alboni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alaoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alconi',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aldoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alroni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aleoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        r = testDataBase.execute("UPDATE YCStudent SET SSNum = 1;");
        assertEquals(false,r.getResults()[0][0]);
        assertEquals("Update query may not put non-unique entries into a unique or primary key column.",r.getErrorMessage());
    }
    
    @Test 
    public void updateFailsWhenAddingNullTwoOrMoreTimesToUniqueColumn(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alboni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alaoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alconi',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aldoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alroni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aleoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        r = testDataBase.execute("UPDATE YCStudent SET SSNum = null;");
        assertEquals(false,r.getResults()[0][0]);
        assertEquals("Update query may not put non-unique entries into a unique or primary key column.",r.getErrorMessage());
    }
    
    @Test 
    public void updateFailsWhenAddingNullWhenItIsAlreadInUniqueColumn(){        
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alboni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alaoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alconi',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (LastName, GPA, CurrentStudent, BannerID) "
        + "VALUES ('Aldoni',4.0, false    ,800712345);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alroni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aleoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        r = testDataBase.execute("UPDATE YCStudent SET SSNum = null WHERE SSNum = 623");
        assertEquals(false,r.getResults()[0][0]);
        assertEquals("Update query may not put non-unique entries into a unique or primary key column.",r.getErrorMessage());

    }
       
    @Test 
    public void updateFailsWhenAddingExtantValueToPrimaryColumn(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alboni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alaoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alconi',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aldoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alroni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aleoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        r = testDataBase.execute("UPDATE YCStudent SET BannerID = 500012345 WHERE SSNum = 623");
        assertEquals(false,r.getResults()[0][0]);
        assertEquals("Update query may not put non-unique entries into a unique or primary key column.",r.getErrorMessage());
    }
            
    @Test 
    public void updateFailsWhenAddingSameValueTwoOrMoreTimesToPrimaryColumn(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alboni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alaoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alconi',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aldoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alroni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aleoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        r = testDataBase.execute("UPDATE YCStudent SET BannerID = 1;");
        assertEquals(false,r.getResults()[0][0]);
        assertEquals("Update query may not put non-unique entries into a unique or primary key column.",r.getErrorMessage());
    }
           
    @Test 
    public void updateFailsWhenPrimaryColumnIsMadeNull(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alboni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alaoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alconi',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aldoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alroni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aleoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        r = testDataBase.execute("UPDATE YCStudent SET BannerID = null");
        assertEquals(false,r.getResults()[0][0]);
        assertEquals("Update query may not put null into a non-null or primary key column.",r.getErrorMessage());
    }
       
    @Test 
    public void updateMakesZeroChangesAnywhereWhenNonNullIsMadeNull(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alboni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alaoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alconi',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aldoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alroni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aleoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        r = testDataBase.execute("UPDATE YCStudent SET LastName = null");
        assertEquals(false,r.getResults()[0][0]);
        assertEquals("Update query may not put null into a non-null or primary key column.",r.getErrorMessage());
        r = testDataBase.execute("SELECT LastName FROM YCStudent");
        assertEquals(new Object[][]{{"Alboni"},{"Alaoni"},{"Alconi"},{"Aldoni"},{"Alroni"},{"Aleoni"}},r.getResults());
        
    }

    @Test 
    public void updateMakesZeroChangesAnywhereWhenPrimaryIsMadeNull(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alboni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alaoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alconi',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aldoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alroni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aleoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        r = testDataBase.execute("UPDATE YCStudent SET BannerID = null");
        assertEquals(false,r.getResults()[0][0]);
        assertEquals("Update query may not put null into a non-null or primary key column.",r.getErrorMessage());
        r = testDataBase.execute("SELECT BannerID FROM YCStudent");
        assertEquals(new Object[][]{{800012345},{800012346},{800123425},{800712345},{800016345},{500012345}},r.getResults());
        
    }
 
    @Test 
    public void updateMakesZeroChangesAnywhereWhenUniqueIsMadeNotUnique(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alboni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alaoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alconi',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aldoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alroni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aleoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        r = testDataBase.execute("UPDATE YCStudent SET SSNum = 1");
        assertEquals(false,r.getResults()[0][0]);
        assertEquals("Update query may not put non-unique entries into a unique or primary key column.",r.getErrorMessage());
        r = testDataBase.execute("SELECT SSNum FROM YCStudent");
        assertEquals(new Object[][]{{773},{124},{223},{723},{623},{153}},r.getResults());
        
    }

    @Test 
    public void updateMakesZeroChangesAnywhereWhenPrimaryIsMadeNotUnique(){
        createQueryTest();
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alboni',4.0, false    ,800012345,773);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alaoni',4.0, false    ,800012346,124);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alconi',4.0, false    ,800123425,223);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent (LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aldoni',4.0, false    ,800712345,723);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Alroni',4.0, false    ,800016345,623);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  
        r=testDataBase.execute("INSERT INTO YCStudent ( LastName, GPA, CurrentStudent, BannerID,SSNum) "
        + "VALUES ('Aleoni',4.0, false    ,500012345,153);");
        assertEquals(true,((Boolean)(r.getResults()[0][0])));  

        r = testDataBase.execute("UPDATE YCStudent SET BannerID = 1");
        assertEquals(false,r.getResults()[0][0]);
        assertEquals("Update query may not put non-unique entries into a unique or primary key column.",r.getErrorMessage());
        r = testDataBase.execute("SELECT BannerID FROM YCStudent");
        assertEquals(new Object[][]{{800012345},{800012346},{800123425},{800712345},{800016345},{500012345}},r.getResults());
        
    }


    
    
}