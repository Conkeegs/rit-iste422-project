import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

//tests the DDLMySQL class
public class CreateDDLMySQLTest {
  private StringBuffer baseSB;
	private EdgeTable[] et;
	private EdgeField[] ef;

	//before run, creates a basic StringBuffer
	//empty EdgeTable[] and EdgeField[]
  @Before
  public void setUp(){
		String baseStr = "CREATE DATABASE MySQLDB;\r\nUSE MySQLDB;\r\n";
    baseSB = new StringBuffer(baseStr);
		et = new EdgeTable[0];
		ef = new EdgeField[0];
  }

	//tests that the basic StringBuffer is the same as the sample DDLMySQL's StringBuffer
  @Test
  public void createDDLConstructorTest(){
		CreateDDLMySQL testObj = new CreateDDLMySQL(et, ef);
    testObj.createDDL();
		String testStr = testObj.sb.toString();
		
    assertEquals("Should be same as Null StringBuffer", testStr, baseSB.toString());
  }

	//tests that conversion to between string and int works correctly
	@Test
	public void convertStrBooleanToIntTest(){
		CreateDDLMySQL testObj = new CreateDDLMySQL();
		int falseBoolean = testObj.convertStrBooleanToInt("false");
		int trueBoolean = testObj.convertStrBooleanToInt("true");
			
    assertEquals("Should be 0", falseBoolean, 0);
		assertEquals("Should be 1", trueBoolean, 1);
	}

	@Test
	public void generateDatabaseNameTest(){
		CreateDDLMySQL testObj = new CreateDDLMySQL();
		String dbName = testObj.generateDatabaseName();
		boolean readSuccess = EdgeConvertGUI.getReadSuccess();

		assertEquals("Null Database should create default name", dbName, "MySQLDB");
		assertEquals("EdgeConvertGUI readSuccess should be true", readSuccess, true);
	}

	//tests that an inputted database name is saved and gotten by getDatabaseName
	@Test
	public void getDatabaseNameTest(){
		CreateDDLMySQL testObj = new CreateDDLMySQL();
		String input = testObj.generateDatabaseName();
		
		assertEquals("Input should be database name", testObj.getDatabaseName(), input);
	}

	//tests that the default product nameis MySQL
	@Test
	public void getProductNameTest(){
		CreateDDLMySQL testObj = new CreateDDLMySQL();
		String testStr = testObj.getProductName();
		
		assertEquals("Should return MySQL", testStr, "MySQL");
	}

	@Test
	public void getSQLStringTest(){
		CreateDDLMySQL testObj = new CreateDDLMySQL(et, ef);
		String testStr = testObj.getSQLString();
		
		assertEquals("SQLString should be null", testStr, baseSB.toString());
	}
}
