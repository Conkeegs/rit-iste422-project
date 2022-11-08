import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CreateDDLMySQLTest {
  private StringBuffer nullSB;
    
  @Before
  public void setUp(){
    nullSB = new StringBuffer();
  }
    
  @Test
  public void createNullEDDLTest(){
		CreateDDLMySQL testObj = new CreateDDLMySQL();
    testObj.createDDL();
    assertEquals("Should be same as Null StringBuffer", testObj.sb.toString(), nullSB.toString());
  }

	@Test
	public void convertStrBooleanToIntTest(){
		CreateDDLMySQL testObj = new CreateDDLMySQL();
    assertEquals("Should be 0", testObj.convertStrBooleanToInt("false"), 0);
		assertEquals("Should be 1", testObj.convertStrBooleanToInt("true"), 1);
	}

	@Test
	public void generateDatabaseNameTest(){
		CreateDDLMySQL testObj = new CreateDDLMySQL();
		assertEquals("Null Database should create default name", testObj.generateDatabaseName(), "MySQLDB");
		assertEquals("EdgeConvertGUI readSuccess should be false", EdgeConvertGUI.getReadSuccess(), false);
		assertEquals("generateDatabaseName should return empty string", testObj.generateDatabaseName(), "");
		assertEquals("Name enered by user will be entered as database name", testObj.generateDatabaseName(), "name");
	}

	@Test
	public void getDatabaseNameTest(){
		CreateDDLMySQL testObj = new CreateDDLMySQL();
		String input = testObj.generateDatabaseName();
		assertEquals("Input should be database name", testObj.getDatabaseName(), input);
	}

	@Test
	public void getProductNameTest(){
		CreateDDLMySQL testObj = new CreateDDLMySQL();
		assertEquals("Should return MySQL", testObj.getProductName(), "MySQL");
	}

	@Test
	public void getSQLStringTest(){
		CreateDDLMySQL testObj = new CreateDDLMySQL();
		String str = testObj.getSQLString();
		assertEquals("SQLString should be null", str, nullSB.toString());
	}
}
