import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EdgeParser extends EdgeConvertFileParser {
    protected boolean isEntity = false;
    protected boolean isAttribute = false;
    private boolean isUnderlined = false;

    public static Logger logger = LogManager.getLogger(EdgeParser.class.getName());
    public static Logger timeLogger = LogManager.getLogger("timer." + EdgeParser.class.getName());

    /**
    * Class used for opening .edg files
    * @param constructorFile the .edg file to open
    */
    public EdgeParser(File constructorFile) {
        super(constructorFile, true);
    }
   
   /**
    * Class used for opening .edg files
    * @param constructorFile the .edg file to open
    * @param showGuis whether or not to show GUI frontend (e.g. JOptionPanes) (default = true)
    */
   public EdgeParser(File constructorFile, boolean showGuis) {
      super(constructorFile, showGuis);
   }

    protected boolean parseFile(File inputFile) throws IOException {
        timeLogger.info("parseFile() called.");
  
        logger.info("Reading Edge Diagrammer file.");
        logger.debug("About to read all lines in Edge Diagrammer file.");

        String style;
        int endPoint1, endPoint2;
        String endStyle1, endStyle2;

        while ((currentLine = br.readLine()) != null) {
            currentLine = currentLine.trim();
    
            logger.debug(String.format("Current line in diagrammer file is: %s", currentLine));
    
            if (currentLine.startsWith("Figure ")) { //this is the start of a Figure entry
                logger.info("Parsing diagrammer file Figures.");
    
                numFigure = Integer.parseInt(currentLine.substring(currentLine.indexOf(" ") + 1)); //get the Figure number
                currentLine = br.readLine().trim(); // this should be "{"
                currentLine = br.readLine().trim();
    
                logger.debug(String.format("Current line in diagrammer file is: %s", currentLine));
                logger.debug(String.format("Current figure number in loop is: %d", numFigure));
    
                if (!currentLine.startsWith("Style")) { // this is to weed out other Figures, like Labels
                    logger.debug("Current line does not start with 'Style'. Continuing through loop.");
    
                    continue;
                } else {
                    style = currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\"")); //get the Style parameter
    
                    logger.debug(String.format("Current style parameter in loop is: %s", style));
    
                    if (style.startsWith("Relation")) { //presence of Relations implies lack of normalization
                        logger.info("Failed to read Edge Diagram file.");
                        logger.warn("Style parameter found with Relation. Lack of normalization.");
                        logger.debug("'Relation' keyword found in style parameter.");
    
                        if (this.showGuis) {
                            JOptionPane.showMessageDialog(null, "The Edge Diagrammer file\n" + parseFile + "\ncontains relations.  Please resolve them and try again.");
                        }
    
                        EdgeConvertGUI.setReadSuccess(false);
    
                        timeLogger.info("parseFile() ended.");
                        
                        return false;
                    }
    
                    if (style.startsWith("Entity")) {
                        logger.debug("'Entity' keyword found in style parameter. Setting isEntity to 'true'.");
    
                        isEntity = true;
                    }
    
                    if (style.startsWith("Attribute")) {
                        logger.debug("'Attribute' keyword found in style parameter. Setting isAttribute to 'true'.");
    
                        isAttribute = true;
                    }
    
                    if (!(isEntity || isAttribute)) { //these are the only Figures we're interested in
                        logger.debug("isEntity and isAttribute are both false. Continuing through loop.");
    
                        continue;
                    }
    
                    currentLine = br.readLine().trim(); //this should be Text
                    String text = currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\"")).replaceAll(" ", ""); //get the Text parameter
    
                    logger.debug(String.format("Current line in diagrammer file is: %s", currentLine));
                    logger.debug(String.format("Current text parameter in loop is: %s", text));
    
                    if (text.equals("")) {
                        logger.debug("Text parameter is an empty string.");
                        logger.warn("Certain entities/attributes contain blank names.");
    
                        if (this.showGuis) {
                        JOptionPane.showMessageDialog(null, "There are entities or attributes with blank names in this diagram.\nPlease provide names for them and try again.");
                        }
    
                        EdgeConvertGUI.setReadSuccess(false);
    
                        timeLogger.info("parseFile() ended.");
    
                        return false;
                    }
    
                    int escape = text.indexOf("\\");
    
                    logger.debug(String.format("Escape character found at index '%d' of text parameter.", escape));
    
                    if (escape > 0) { //Edge denotes a line break as "\line", disregard anything after a backslash
                        logger.debug("Escape character position = 0. Ignoring all text that comes after it.");
    
                        text = text.substring(0, escape);
                    }
    
                    logger.debug("About to loop through current record to search for underlined text.");
    
                    do { //advance to end of record, look for whether the text is underlined
                        currentLine = br.readLine().trim();
    
                        logger.debug(String.format("Current line in diagrammer file is: %s", currentLine));
    
                        if (currentLine.startsWith("TypeUnderl")) {
                            logger.debug("'TypeUnder1' keyword found in current record. Setting isUnderlined to 'true'.");
        
                            isUnderlined = true;
                        }
                    } while (!currentLine.equals("}")); // this is the end of a Figure entry
    
                    logger.debug("'}' character found in current record. Finished looping through current record to search for underlined text.");
                    
                    if (isEntity) { //create a new EdgeTable object and add it to the alTables ArrayList
                        logger.debug("isEntity is 'true'. Checking for duplicate tables.");
    
                        if (isTableDup(text)) {
                            logger.debug(String.format("Duplicate tables found in text '%s'.", text));
        
                            if (this.showGuis) {
                                JOptionPane.showMessageDialog(null, "There are multiple tables called " + text + " in this diagram.\nPlease rename all but one of them and try again.");
                            }
        
                            EdgeConvertGUI.setReadSuccess(false);
        
                            timeLogger.info("parseFile() ended.");
        
                            return false;
                        }
    
                        EdgeTable table = new EdgeTable(numFigure + DELIM + text);
    
                        logger.debug(String.format("Adding table '%s' to alTables ArrayList.", table.getName()));
    
                        alTables.add(table);
                    }
    
                    if (isAttribute) { //create a new EdgeField object and add it to the alFields ArrayList
                        logger.debug("isAttribute is 'true'.");
    
                        EdgeField tempField = new EdgeField(numFigure + DELIM + text);
                        tempField.setIsPrimaryKey(isUnderlined);
                        alFields.add(tempField);
    
                        logger.debug(String.format("Setting field '%s' as primary key: '%b'.", tempField.getName(), isUnderlined));
                        logger.debug(String.format("Adding field '%s' to alFields ArrayList.", tempField.getName()));
                    }
    
                    //reset flags
                    isEntity = false;
                    isAttribute = false;
                    isUnderlined = false;
                }
            } // if("Figure")
    
            if (currentLine.startsWith("Connector ")) { //this is the start of a Connector entry
                logger.info("Parsing diagrammer file Connectors.");
    
                numConnector = Integer.parseInt(currentLine.substring(currentLine.indexOf(" ") + 1)); //get the Connector number
    
                logger.debug(String.format("Current Connector number is: %d", numConnector));
    
                currentLine = br.readLine().trim(); // this should be "{"
                currentLine = br.readLine().trim(); // not interested in Style
                currentLine = br.readLine().trim(); // Figure1
    
                logger.debug(String.format("Current line in diagrammer file is: %s", currentLine));
    
                endPoint1 = Integer.parseInt(currentLine.substring(currentLine.indexOf(" ") + 1));
    
                logger.debug(String.format("Current end point 1 is: %d", endPoint1));
    
                currentLine = br.readLine().trim(); // Figure2
    
                logger.debug(String.format("Current line in diagrammer file is: %s", currentLine));
    
                endPoint2 = Integer.parseInt(currentLine.substring(currentLine.indexOf(" ") + 1));
    
                logger.debug(String.format("Current end point 2 is: %d", endPoint2));
    
                currentLine = br.readLine().trim(); // not interested in EndPoint1
                currentLine = br.readLine().trim(); // not interested in EndPoint2
                currentLine = br.readLine().trim(); // not interested in SuppressEnd1
                currentLine = br.readLine().trim(); // not interested in SuppressEnd2
                currentLine = br.readLine().trim(); // End1
    
                logger.debug(String.format("Current line in diagrammer file is: %s", currentLine));
    
                endStyle1 = currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\"")); //get the End1 parameter
    
                logger.debug(String.format("Current end style 1 is: %s", endStyle1));
    
                currentLine = br.readLine().trim(); // End2
    
                logger.debug(String.format("Current line in diagrammer file is: %s", currentLine));
    
                endStyle2 = currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\"")); //get the End2 parameter
    
                logger.debug(String.format("Current end style 2 is: %s", endStyle2));
    
                logger.debug("About to advance to end of current record.");
    
                do { //advance to end of record
                    currentLine = br.readLine().trim();
    
                    logger.debug(String.format("Current line in diagrammer file is: %s", currentLine));
                } while (!currentLine.equals("}")); // this is the end of a Connector entry
    
                logger.debug("'}' character found. Done advancing to end of current record.");
    
                String inputString = numConnector + DELIM + endPoint1 + DELIM + endPoint2 + DELIM + endStyle1 + DELIM + endStyle2;
    
                logger.debug(String.format("Adding new Connector '%s' to alConnectors ArrayList.", inputString));
                
                alConnectors.add(new EdgeConnector(inputString));
            } // if("Connector")
        } // while()
  
        logger.debug("Finished reading all lines in Edge Diagrammer file.");
        logger.info("Finished reading Edge Diagrammer file.");
  
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

            if (currentLine.startsWith(EDGE_ID) == false) { //the file chosen is not an .edg file
                logger.warn("Unrecognized file format chosen.");
 
                logger.info("Warning user of unrecognized file format.");
                
                if (this.showGuis) {
                   JOptionPane.showMessageDialog(null, "Unrecognized file format");
                }
 
                timeLogger.info("EdgeParser.openFile() ended.");
                return false;
            }
    
            logger.debug(String.format("Edge Diagrammer file's first line is: %s", currentLine));
  
            boolean parseFileSuccessful = this.parseFile(this.parseFile); //parse the file

            br.close();
            this.makeArrays(); //convert ArrayList objects into arrays of the appropriate Class type

            boolean resolveConnectorsSuccessful = this.resolveConnectors(); //Identify nature of Connector endpoints

            timeLogger.info("EdgeParser.openFile() ended.");
            return parseFileSuccessful && resolveConnectorsSuccessful;
        } // try
        catch (FileNotFoundException fnfe) {
           logger.error(String.format("Cannot find: \"%s\".", this.parseFile.getAbsolutePath()));
           logger.trace(String.format("Cannot find: \"%s\".", this.parseFile.getAbsolutePath()));
  
           timeLogger.info("EdgeParser.openFile() ended.");
           return false;
        } // catch FileNotFoundException
        catch (IOException ioe) {
           logger.error(String.format("Error reading file: \"%s\".", this.parseFile.getAbsolutePath()));
           logger.trace(String.format("Cannot reading file: \"%s\".", this.parseFile.getAbsolutePath()));
  
           timeLogger.info("EdgeParser.openFile() ended.");
           return false;
        } // catch IOException
     } // openFile()
}
