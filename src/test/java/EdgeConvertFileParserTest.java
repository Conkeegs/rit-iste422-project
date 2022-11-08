import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
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

            // make 'alTablesField', 'alFieldsField', and 'alConnectorsField' fields accessible
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

    @Test
    public void testEdgeFileWithCompositeAttributesInConnectorReturnsFalse() {
        File constructorFile = new File("edge_files/CompositeAttributes.edg");
        EdgeConvertFileParser edgFileParser = new EdgeConvertFileParser(constructorFile, false, false);

        // openFile() should return false since 'CompositeAttributes.edg' contains a 'Connector' whose endpoints lead to the same 'Figure'
        assertFalse(edgFileParser.openFile(constructorFile));
    }

    @Test
    public void testEdgeFileWithManyToManyInConnectorReturnsFalse() {
        File constructorFile = new File("edge_files/ManyToMany.edg");
        EdgeConvertFileParser edgFileParser = new EdgeConvertFileParser(constructorFile, false, false);

        // openFile() should return false since 'ManyToMany.edg' contains a 'Connector' with a many-to-many relationship between its endpoints
        assertFalse(edgFileParser.openFile(constructorFile));
    }

    @Test
    public void testArrayListContainsSameNumberOfFieldsAsSaveFile() {
         File constructorFile = new File("save_files/Courses.edg.sav");
         int numFieldsInEdgeFile = 7;
 
         try {
             EdgeConvertFileParser edgFileParser = new EdgeConvertFileParser(constructorFile, false, false);
             Field alFieldsField = EdgeConvertFileParser.class.getDeclaredField("alFields");
 
             // make 'allFieldsField' accessible
             alFieldsField.setAccessible(true);
 
             ArrayList alFields = (ArrayList)alFieldsField.get(edgFileParser);
 
             // openFile() should return true since 'Courses.edg.sav' is a valid sav file
             assertTrue(edgFileParser.openFile(constructorFile));
 
             // alFields size() should be same amount of fields in sav file
             assertSame(numFieldsInEdgeFile, alFields.size());
         } catch (NoSuchFieldException nsfe) {
             nsfe.printStackTrace();
         } catch (IllegalAccessException iae) {
             iae.printStackTrace();
         }
    }

    @Test
    public void testArraysNotNullAndArraysSameSizeAsArrayLists() {
        File constructorFile = new File("edge_files/Courses.edg");

		try {
            EdgeConvertFileParser edgFileParser = new EdgeConvertFileParser(constructorFile, false, false);
            Field tablesField = EdgeConvertFileParser.class.getDeclaredField("tables");
            Field fieldsField = EdgeConvertFileParser.class.getDeclaredField("fields");
            Field connectorsField = EdgeConvertFileParser.class.getDeclaredField("connectors");
            Field alTablesField = EdgeConvertFileParser.class.getDeclaredField("alTables");
            Field alFieldsField = EdgeConvertFileParser.class.getDeclaredField("alFields");
            Field alConnectorsField = EdgeConvertFileParser.class.getDeclaredField("alConnectors");

            // set private fields as accessible
            tablesField.setAccessible(true);
            fieldsField.setAccessible(true);
            connectorsField.setAccessible(true);
            alTablesField.setAccessible(true);
            alFieldsField.setAccessible(true);
            alConnectorsField.setAccessible(true);

            ArrayList alTables = (ArrayList)alTablesField.get(edgFileParser);
            ArrayList alFields = (ArrayList)alFieldsField.get(edgFileParser);
            ArrayList alConnectors = (ArrayList)alConnectorsField.get(edgFileParser);

            // openFile() should return true since 'Courses.edg' is a valid edge file
            assertTrue(edgFileParser.openFile(constructorFile));

            EdgeTable[] tables = (EdgeTable[])tablesField.get(edgFileParser);
            EdgeField[] fields = (EdgeField[])fieldsField.get(edgFileParser);
            EdgeConnector[] connectors = (EdgeConnector[])connectorsField.get(edgFileParser);

            // assert that these 3 arrays are not null
            assertNotNull(tables);
            assertNotNull(fields);
            assertNotNull(connectors);

            // assert that arrays and their corresponding ArrayLists are the same size
            assertEquals(tables.length, alTables.size());
            assertEquals(fields.length, alFields.size());
            assertEquals(connectors.length, alConnectors.size());
        } catch (NoSuchFieldException nsfe) {
            nsfe.printStackTrace();
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        }
    }

    @Test
    public void testOpenFileReturnsFalseWhenFileDoesNotExist() {
        File constructorFile = new File("doesnotexist/doesnotexist/doesnotexist.edg");
        EdgeConvertFileParser edgFileParser = new EdgeConvertFileParser(constructorFile, false, false);

        // openFile() should return false, since 'doesnotexist/doesnotexist/doesnotexist.edg' is not an actual file
        assertFalse(edgFileParser.openFile(constructorFile));
    }

    @Test
    public void testOpenFileReturnsFalseWhenFileIsDirectory() {
        File constructorFile = new File("edge_files");
        EdgeConvertFileParser edgFileParser = new EdgeConvertFileParser(constructorFile, false, false);

        // openFile() should return false, since 'edge_files' is a directory
        assertFalse(edgFileParser.openFile(constructorFile));
    }

    @Test
    public void testOpenFileReturnsTrueWithValidSaveFile() {
        File constructorFile = new File("save_files/Courses.edg.sav");
        EdgeConvertFileParser edgFileParser = new EdgeConvertFileParser(constructorFile, false, false);

        // openFile() should return true since 'Courses.edg.sav' is a valid sav file
        assertTrue(edgFileParser.openFile(constructorFile));
    }

    @Test
    public void testOpenFileReturnsFalseWithIncorrectExtensionType() {
        File constructorFile = new File("save_files/Courses.png");
        EdgeConvertFileParser edgFileParser = new EdgeConvertFileParser(constructorFile, false, false);

        // openFile() should return false since 'Courses.png' is not a valid file extension for the program
        assertFalse(edgFileParser.openFile(constructorFile));
    }
}
