import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class EdgeConvertFileParserTest {
    // @Rule
    // public ExpectedException exceptionRule = ExpectedException.none();

    @Before
	public void setUp() throws Exception {
		// testObj = new EdgeConnector("1|2|3|testStyle1|testStyle2");
	}

	@Test
	public void testConstructorFileAndParseFilePropertiesAreEqual() {
        // valid edge file
        File constructorFile = new File("edge_files/Courses.edg");

		try {
            EdgeConvertFileParser edgFileParser = new EdgeConvertFileParser(constructorFile, false, false);
            Field parseFileField = EdgeConvertFileParser.class.getDeclaredField("parseFile");

            // make 'parseFile' field accessible
            parseFileField.setAccessible(true);

            File parseFile = (File)parseFileField.get(edgFileParser);

            // make sure file passed in to constructor and 'parseFile' are equal
            assertTrue(Arrays.equals(Files.readAllBytes(constructorFile.toPath()), Files.readAllBytes(parseFile.toPath())));
        } catch (NoSuchFieldException nsfe) {
            nsfe.printStackTrace();
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
	}

    @Test
    public void testEdgeFileWithRelationInsideOfStyleReturnsFalse() {
        File constructorFile = new File("edge_files/RelationInStyle.edg");
        EdgeConvertFileParser edgFileParser = new EdgeConvertFileParser(constructorFile, false, false);

        // openFile() should return false since 'RelationInStyle.edg' contains a 'Style' parameter starting with 'Relation'
        assertFalse(edgFileParser.openFile(constructorFile));
    }

    @Test
    public void testEdgeFileWithEmptyTextParameterReturnsFalse() {
        File constructorFile = new File("edge_files/EmptyTextParam.edg");
        EdgeConvertFileParser edgFileParser = new EdgeConvertFileParser(constructorFile, false, false);

        // openFile() should return false since 'EmptyTextParam.edg' contains a 'Text' parameter that is an empty String ""
        assertFalse(edgFileParser.openFile(constructorFile));
    }

    @Test
    public void testEdgeFileWithDuplicateTables() {
        File constructorFile = new File("edge_files/DuplicateTables.edg");
        EdgeConvertFileParser edgFileParser = new EdgeConvertFileParser(constructorFile, false, false);

        // openFile() should return false since 'DuplicateTables.edg' contains two 'Text' parameters that are the same (duplicate tables)
        assertFalse(edgFileParser.openFile(constructorFile));
    }

    @Test
    public void testValidEdgeFileReturnsTrueAndArrayListsNotEmpty() {
        // valid edge file
        File constructorFile = new File("edge_files/Courses.edg");
        int numTablesInEdgeFile = 3;
        int numFieldsInEdgeFile = 7;
        int numConnectorsInEdgeFile = 10;

		try {
            EdgeConvertFileParser edgFileParser = new EdgeConvertFileParser(constructorFile, false, false);
            Field alTablesField = EdgeConvertFileParser.class.getDeclaredField("alTables");
            Field alFieldsField = EdgeConvertFileParser.class.getDeclaredField("alFields");
            Field alConnectorsField = EdgeConvertFileParser.class.getDeclaredField("alConnectors");

            // make 'alTablesField' and 'alConnectorsField' fields accessible
            alTablesField.setAccessible(true);
            alFieldsField.setAccessible(true);
            alConnectorsField.setAccessible(true);

            ArrayList alTables = (ArrayList)alTablesField.get(edgFileParser);
            ArrayList alFields = (ArrayList)alFieldsField.get(edgFileParser);
            ArrayList alConnectors = (ArrayList)alConnectorsField.get(edgFileParser);

            // openFile() should return true since 'Courses.edg' is a valid edge file
            assertTrue(edgFileParser.openFile(constructorFile));

            // alTables size() should be same amount of tables in edge file
            assertSame(numTablesInEdgeFile, alTables.size());

            // alFields size() should be same amount of fields in edge file
            assertSame(numFieldsInEdgeFile, alFields.size());

            // alConnectors size() should be same amount of connectors in edge file
            assertSame(numConnectorsInEdgeFile, alConnectors.size());
        } catch (NoSuchFieldException nsfe) {
            nsfe.printStackTrace();
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        }
    }
}
