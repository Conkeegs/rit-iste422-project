import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveParser extends EdgeConvertFileParser {
    public static Logger logger = LogManager.getLogger(SaveParser.class.getName());
    public static Logger timeLogger = LogManager.getLogger("timer." + SaveParser.class.getName());

    /**
    * Class used for opening .sav files
    * @param constructorFile the .sav file to open
    */
    public SaveParser(File constructorFile) {
        super(constructorFile, true);
    }
   
   /**
    * Class used for opening .sav files
    * @param constructorFile the .sav file to open
    * @param showGuis whether or not to show GUI frontend (e.g. JOptionPanes) (default = true)
    */
   public SaveParser(File constructorFile, boolean showGuis) {
      super(constructorFile, showGuis);
   }

    protected boolean parseFile(File inputFile) throws IOException { //this method is unclear and confusing in places
        timeLogger.info("parseFile() called.");
  
        StringTokenizer stTables, stNatFields, stRelFields, stField;
        EdgeTable tempTable;
        EdgeField tempField;
        String fieldName;

        currentLine = br.readLine();
        currentLine = br.readLine(); //this should be "Table: "

        logger.debug(String.format("Current line in save file is: %s", currentLine));
        logger.debug("About to check for 'Table: ' string inside of save file.");

        logger.info("Reading save file.");

        while (currentLine.startsWith("Table: ")) {
            logger.info("Parsing save file tables.");
            logger.debug(String.format("Current line in save file is: %s", currentLine));
    
            numFigure = Integer.parseInt(currentLine.substring(currentLine.indexOf(" ") + 1)); //get the Table number
            currentLine = br.readLine(); //this should be "{"
            currentLine = br.readLine(); //this should be "TableName"
            tableName = currentLine.substring(currentLine.indexOf(" ") + 1);
    
            logger.debug(String.format("Current line in save file is: %s", currentLine));
            logger.debug(String.format("Current table number in loop is: %d", numFigure));
            logger.debug(String.format("Current table name in loop is: %s", tableName));
    
            logger.debug("Creating new EdgeTable.");
    
            tempTable = new EdgeTable(numFigure + DELIM + tableName);
            
            currentLine = br.readLine(); //this should be the NativeFields list
            stNatFields = new StringTokenizer(currentLine.substring(currentLine.indexOf(" ") + 1), DELIM);
            numFields = stNatFields.countTokens();
    
            logger.debug(String.format("Current line in save file is: %s", currentLine));
            logger.debug(String.format("Current number of fields in loop: %d", numFields));
    
            logger.debug(String.format("Looping through current table fields of length: %d", numFields));
    
            for (int i = 0; i < numFields; i++) {
                String nextToken = stNatFields.nextToken();
    
                logger.debug(String.format("Adding native field to EdgeTable: %s", nextToken));
    
                tempTable.addNativeField(Integer.parseInt(nextToken));
            }
    
            logger.debug("Finished looping through current table fields.");
            
            currentLine = br.readLine(); //this should be the RelatedTables list
            stTables = new StringTokenizer(currentLine.substring(currentLine.indexOf(" ") + 1), DELIM);
            numTables = stTables.countTokens();
    
            logger.debug(String.format("Current line in save file is: %s", currentLine));
            logger.debug(String.format("Current number of related tables in loop is: %d", numTables));
    
            logger.debug(String.format("Looping through current related tables of length: %d", numTables));
    
            for (int i = 0; i < numTables; i++) {
                String nextTable = stTables.nextToken();
    
                logger.debug(String.format("Adding related table to EdgeTable: %s", nextTable));
    
                tempTable.addRelatedTable(Integer.parseInt(nextTable));
            }
    
            logger.debug("Finished looping through current related tables.");
    
            tempTable.makeArrays();
            
            currentLine = br.readLine(); //this should be the RelatedFields list
            stRelFields = new StringTokenizer(currentLine.substring(currentLine.indexOf(" ") + 1), DELIM);
            numFields = stRelFields.countTokens();
    
            logger.debug(String.format("Current line in save file is: %s", currentLine));
            logger.debug(String.format("Current number of related fields in loop is: %d", numFields));
    
            logger.debug(String.format("Looping through current related fields of length: %d", numFields));
    
            for (int i = 0; i < numFields; i++) {
                String nextRelatedField = stRelFields.nextToken();
    
                logger.debug(String.format("Adding related field to EdgeTable: %s", nextRelatedField));
    
                tempTable.setRelatedField(i, Integer.parseInt(nextRelatedField));
            }
    
            logger.debug("Finished looping through current relatedfields.");
    
            logger.debug(String.format("Adding table '%s' to alTables ArrayList.", tempTable.getName()));
    
            alTables.add(tempTable);
            currentLine = br.readLine(); //this should be "}"
            currentLine = br.readLine(); //this should be "\n"
            currentLine = br.readLine(); //this should be either the next "Table: ", #Fields#
    
            logger.debug(String.format("Current line in save file is: %s", currentLine));
    
            logger.info("Done parsing save file tables.");
        
        }

        logger.debug("Finished checking for 'Table: ' string inside of save file.");

        logger.debug("About to check for fields inside of save file.");

        while ((currentLine = br.readLine()) != null) {
            logger.info("Parsing save file fields.");
    
            stField = new StringTokenizer(currentLine, DELIM);
            numFigure = Integer.parseInt(stField.nextToken());
            fieldName = stField.nextToken();
    
            logger.debug(String.format("Current line in save file is: %s", currentLine));
            logger.debug(String.format("Current figure number in loop is: %d", numFigure));
            logger.debug(String.format("Current field name in loop is: %s", fieldName));
    
            logger.debug("Creating new EdgeField.");
    
            tempField = new EdgeField(numFigure + DELIM + fieldName);
            tempField.setTableID(Integer.parseInt(stField.nextToken()));
    
            logger.debug(String.format("Setting table ID in EdgeField to: %d", tempField.getTableID()));
    
            tempField.setTableBound(Integer.parseInt(stField.nextToken()));
    
            logger.debug(String.format("Setting table bound in EdgeField to: %d", tempField.getTableBound()));
    
            tempField.setFieldBound(Integer.parseInt(stField.nextToken()));
    
            logger.debug(String.format("Setting field bound in EdgeField to: %d", tempField.getFieldBound()));
    
            tempField.setDataType(Integer.parseInt(stField.nextToken()));
    
            logger.debug(String.format("Setting data type in EdgeField to: %d", tempField.getDataType()));
    
            tempField.setVarcharValue(Integer.parseInt(stField.nextToken()));
    
            logger.debug(String.format("Setting varchar value in EdgeField to: %d", tempField.getVarcharValue()));
    
            tempField.setIsPrimaryKey(Boolean.valueOf(stField.nextToken()).booleanValue());
    
            logger.debug(String.format("Setting field as primary in EdgeField: %b", tempField.getIsPrimaryKey()));
    
            tempField.setDisallowNull(Boolean.valueOf(stField.nextToken()).booleanValue());
    
            logger.debug(String.format("Setting disallow null in EdgeField to: %b", tempField.getDisallowNull()));

            if (stField.hasMoreTokens()) { //Default Value may not be defined
                tempField.setDefaultValue(stField.nextToken());
    
                logger.debug(String.format("Setting default value in EdgeField to: %s", tempField.getDefaultValue()));
            }
    
            logger.debug(String.format("Adding field '%s' to alFields ArrayList.", tempField.getName()));
    
            alFields.add(tempField);
    
            logger.info("Done parsing save file fields.");
        
        }
  
        logger.info("Finished reading save file.");
        logger.debug("Finished checking for fields inside of save file.");
  
        timeLogger.info("parseFile() ended.");
        return true;
     } // parseFile()

     public boolean openFile() {
        timeLogger.info("openFile() called.");
        logger.debug(String.format("openFile() called with file: %s", this.parseFile.getAbsolutePath()));
  
        try {
           fr = new FileReader(this.parseFile);
           br = new BufferedReader(fr);
  
           //test for what kind of file we have
           currentLine = br.readLine().trim();
           numLine++;
  
           if (currentLine.startsWith(SAVE_ID) == false) { //the file chosen is not a .sav file
                logger.warn("Unrecognized file format chosen.");

                logger.info("Warning user of unrecognized file format.");
                
                if (this.showGuis) {
                    JOptionPane.showMessageDialog(null, "Unrecognized file format");
                }

                timeLogger.info("SaveParser.openFile() ended.");
                return false;
            }

            logger.debug(String.format("Save file's first line is: %s", currentLine));

            boolean parseFileSuccessful = this.parseFile(this.parseFile); //parse the file

            br.close();
            this.makeArrays(); //convert ArrayList objects into arrays of the appropriate Class type

            timeLogger.info("SaveParser.openFile() ended.");
            return parseFileSuccessful;
        } // try
        catch (FileNotFoundException fnfe) {
           logger.error(String.format("Cannot find: \"%s\".", this.parseFile.getAbsolutePath()));
           logger.trace(String.format("Cannot find: \"%s\".", this.parseFile.getAbsolutePath()));
  
           timeLogger.info("SaveParser.openFile() ended.");
           return false;
        } // catch FileNotFoundException
        catch (IOException ioe) {
           logger.error(String.format("Error reading file: \"%s\".", this.parseFile.getAbsolutePath()));
           logger.trace(String.format("Cannot reading file: \"%s\".", this.parseFile.getAbsolutePath()));
  
           timeLogger.info("SaveParser.openFile() ended.");
           return false;
        } // catch IOException
     } // openFile()
}
