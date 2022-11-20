import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.Test;

public class EdgeConvertFileParserTest {
	@Test
	public void givenEdgeParser_testConstructorFileAndParseFilePropertiesAreEqual() {
		try {
            // valid edge file
            File constructorFile = new File("edge_files/Courses.edg");
            EdgeParser edgFileParser = new EdgeParser(constructorFile, false);

            // make sure file passed in to constructor and 'parseFile' are equal
            assertTrue(Arrays.equals(Files.readAllBytes(constructorFile.toPath()), Files.readAllBytes(edgFileParser.parseFile.toPath())));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
	}

    @Test
	public void givenSaveParser_testConstructorFileAndParseFilePropertiesAreEqual() {
		try {
            // valid edge file
            File constructorFile = new File("save_files/Courses.edg.sav");
            SaveParser saveFileParser = new SaveParser(constructorFile, false);

            // make sure file passed in to constructor and 'parseFile' are equal
            assertTrue(Arrays.equals(Files.readAllBytes(constructorFile.toPath()), Files.readAllBytes(saveFileParser.parseFile.toPath())));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
	}

    @Test
    public void givenEdgeParser_testEdgeFileWithRelationInsideOfStyleReturnsFalse() {
        File constructorFile = new File("edge_files/RelationInStyle.edg");
        EdgeParser edgFileParser = new EdgeParser(constructorFile, false);

        // openFile() should return false since 'RelationInStyle.edg' contains a 'Style' parameter starting with 'Relation'
        assertFalse(edgFileParser.openFile());
    }

    @Test
    public void givenEdgeParser_testEdgeFileWithEmptyTextParameterReturnsFalse() {
        File constructorFile = new File("edge_files/EmptyTextParam.edg");
        EdgeParser edgFileParser = new EdgeParser(constructorFile, false);

        // openFile() should return false since 'EmptyTextParam.edg' contains a 'Text' parameter that is an empty String ""
        assertFalse(edgFileParser.openFile());
    }

    @Test
    public void givenEdgeParser_testEdgeFileWithDuplicateTables() {
        File constructorFile = new File("edge_files/DuplicateTables.edg");
        EdgeParser edgFileParser = new EdgeParser(constructorFile, false);

        // openFile() should return false since 'DuplicateTables.edg' contains two 'Text' parameters that are the same (duplicate tables)
        assertFalse(edgFileParser.openFile());
    }

    @Test
    public void givenEdgeParser_testValidEdgeFileReturnsTrueAndArrayListsNotEmpty() {
        // valid edge file
        File constructorFile = new File("edge_files/Courses.edg");
        int numTablesInEdgeFile = 3;
        int numFieldsInEdgeFile = 7;
        int numConnectorsInEdgeFile = 10;

		EdgeParser edgFileParser = new EdgeParser(constructorFile, false);

        // openFile() should return true since 'Courses.edg' is a valid edge file
        assertTrue(edgFileParser.openFile());

        // alTables size() should be same amount of tables in edge file
        assertSame(numTablesInEdgeFile, edgFileParser.alTables.size());

        // alFields size() should be same amount of fields in edge file
        assertSame(numFieldsInEdgeFile, edgFileParser.alFields.size());

        // alConnectors size() should be same amount of connectors in edge file
        assertSame(numConnectorsInEdgeFile, edgFileParser.alConnectors.size());
    }

    @Test
    public void givenEdgeParser_testEdgeFileWithCompositeAttributesInConnectorReturnsFalse() {
        File constructorFile = new File("edge_files/CompositeAttributes.edg");
        EdgeParser edgFileParser = new EdgeParser(constructorFile, false);

        // openFile() should return false since 'CompositeAttributes.edg' contains a 'Connector' whose endpoints lead to the same 'Figure'
        assertFalse(edgFileParser.openFile());
    }

    @Test
    public void givenEdgeParser_testEdgeFileWithManyToManyInConnectorReturnsFalse() {
        File constructorFile = new File("edge_files/ManyToMany.edg");
        EdgeParser edgFileParser = new EdgeParser(constructorFile, false);

        // openFile() should return false since 'ManyToMany.edg' contains a 'Connector' with a many-to-many relationship between its endpoints
        assertFalse(edgFileParser.openFile());
    }

    @Test
    public void givenSaveParser_testArrayListContainsSameNumberOfFieldsAsSaveFile() {
         File constructorFile = new File("save_files/Courses.edg.sav");
         int numFieldsInEdgeFile = 7;
 
         SaveParser saveFileParser = new SaveParser(constructorFile, false);
 
        // openFile() should return true since 'Courses.edg.sav' is a valid sav file
        assertTrue(saveFileParser.openFile());

        // alFields size() should be same amount of fields in sav file
        assertSame(numFieldsInEdgeFile, saveFileParser.alFields.size());
    }

    @Test
    public void givenEdgeParser_testArraysNotNullAndArraysSameSizeAsArrayLists() {
        File constructorFile = new File("edge_files/Courses.edg");

		EdgeParser edgFileParser = new EdgeParser(constructorFile, false);

        // openFile() should return true since 'Courses.edg' is a valid edge file
        assertTrue(edgFileParser.openFile());

        // assert that these 3 arrays are not null
        assertNotNull(edgFileParser.tables);
        assertNotNull(edgFileParser.fields);
        assertNotNull(edgFileParser.connectors);

        // assert that arrays and their corresponding ArrayLists are the same size
        assertEquals(edgFileParser.tables.length, edgFileParser.alTables.size());
        assertEquals(edgFileParser.fields.length, edgFileParser.alFields.size());
        assertEquals(edgFileParser.connectors.length, edgFileParser.alConnectors.size());
    }

    @Test
    public void givenEdgeParser_testOpenFileReturnsFalseWhenFileDoesNotExist() {
        File constructorFile = new File("doesnotexist/doesnotexist/doesnotexist.edg");
        EdgeParser edgFileParser = new EdgeParser(constructorFile, false);

        // openFile() should return false, since 'doesnotexist/doesnotexist/doesnotexist.edg' is not an actual file
        assertFalse(edgFileParser.openFile());
    }

    @Test
    public void givenEdgeParser_testOpenFileReturnsFalseWhenFileIsDirectory() {
        File constructorFile = new File("edge_files");
        EdgeParser edgFileParser = new EdgeParser(constructorFile, false);

        // openFile() should return false, since 'edge_files' is a directory
        assertFalse(edgFileParser.openFile());
    }

    @Test
    public void givenSaveParser_testOpenFileReturnsTrueWithValidSaveFile() {
        File constructorFile = new File("save_files/Courses.edg.sav");
        SaveParser saveFileParser = new SaveParser(constructorFile, false);

        // openFile() should return true since 'Courses.edg.sav' is a valid sav file
        assertTrue(saveFileParser.openFile());
    }

    @Test
    public void givenEdgeParser_testOpenFileReturnsFalseWithIncorrectExtensionType() {
        File constructorFile = new File("save_files/Courses.png");
        EdgeParser edgFileParser = new EdgeParser(constructorFile, false);

        // openFile() should return false since 'Courses.png' is not a valid file extension for the program
        assertFalse(edgFileParser.openFile());
    }

    @Test
    public void givenSaveParser_testOpenFileReturnsFalseWithIncorrectExtensionType() {
        File constructorFile = new File("save_files/Courses.png");
        EdgeParser edgFileParser = new EdgeParser(constructorFile, false);

        // openFile() should return false since 'Courses.png' is not a valid file extension for the program
        assertFalse(edgFileParser.openFile());
    }
}
