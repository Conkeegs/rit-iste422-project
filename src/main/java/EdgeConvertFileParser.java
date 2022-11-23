import java.io.*;
import java.util.*;
import javax.swing.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class EdgeConvertFileParser {
   //private String filename = "test.edg";
   protected File parseFile;
   protected FileReader fr;
   protected BufferedReader br;
   protected String currentLine;
   protected ArrayList alTables, alFields, alConnectors;
   protected EdgeTable[] tables;
   protected EdgeField[] fields;
   protected EdgeConnector[] connectors;
   protected boolean showGuis = true;
   protected int numFigure, numConnector, numFields, numTables, numNativeRelatedFields;
   public static final String EDGE_ID = "EDGE Diagram File"; //first line of .edg files should be this
   public static final String SAVE_ID = "EdgeConvert Save File"; //first line of save files should be this
   public static final String DELIM = "|";
   
   public static Logger logger = LogManager.getLogger(EdgeConvertFileParser.class.getName());
   public static Logger timeLogger = LogManager.getLogger("timer." + EdgeConvertFileParser.class.getName());
   
   /**
    * Class used for opening .edg files
    * @param constructorFile the .edg file to open
    * @param showGuis whether or not to show GUI frontend (e.g. JOptionPanes) (default = true)
    */
   public EdgeConvertFileParser(File constructorFile, boolean showGuis) {
      timeLogger.info("Constructor called.");
      logger.debug(String.format("Constructor called with file: %s", constructorFile.getAbsolutePath()));

      this.showGuis = showGuis;
      numFigure = 0;
      numConnector = 0;
      alTables = new ArrayList();
      alFields = new ArrayList();
      alConnectors = new ArrayList();
      parseFile = constructorFile;

      timeLogger.info("Constructor ended.");
   }
   
   protected boolean resolveConnectors() { //Identify nature of Connector endpoints
      timeLogger.info("resolveConnectors() called.");

      int endPoint1, endPoint2;
      int fieldIndex = 0, table1Index = 0, table2Index = 0;

      logger.debug(String.format("Looping through connectors of length: %d", connectors.length));

      for (int cIndex = 0; cIndex < connectors.length; cIndex++) {
         endPoint1 = connectors[cIndex].getEndPoint1();
         endPoint2 = connectors[cIndex].getEndPoint2();
         fieldIndex = -1;

         logger.debug(String.format("Current connector endpoint 1: %d", endPoint1));
         logger.debug(String.format("Current connector endpoint 2: %d", endPoint2));

         logger.debug(String.format("Looping through fields of length: %d", fields.length));

         for (int fIndex = 0; fIndex < fields.length; fIndex++) { //search fields array for endpoints
            if (endPoint1 == fields[fIndex].getNumFigure()) { //found endPoint1 in fields array
               logger.debug(String.format("Endpoint 1 of fields found at field index '%d', named '%s'.", fIndex, fields[fIndex].getName()));

               connectors[cIndex].setIsEP1Field(true); //set appropriate flag
               fieldIndex = fIndex; //identify which element of the fields array that endPoint1 was found in
            }
            if (endPoint2 == fields[fIndex].getNumFigure()) { //found endPoint2 in fields array
               logger.debug(String.format("Endpoint 2 of fields found at field index '%d', named '%s'.", fIndex, fields[fIndex].getName()));

               connectors[cIndex].setIsEP2Field(true); //set appropriate flag
               fieldIndex = fIndex; //identify which element of the fields array that endPoint2 was found in
            }
         }

         logger.debug("Finished looping through fields.");

         logger.debug(String.format("Looping through tables of length: %d", tables.length));

         for (int tIndex = 0; tIndex < tables.length; tIndex++) { //search tables array for endpoints
            if (endPoint1 == tables[tIndex].getNumFigure()) { //found endPoint1 in tables array
               logger.debug(String.format("Endpoint 1 of tables found at table index '%d', named '%s'.", tIndex, tables[tIndex].getName()));

               connectors[cIndex].setIsEP1Table(true); //set appropriate flag
               table1Index = tIndex; //identify which element of the tables array that endPoint1 was found in
            }

            if (endPoint2 == tables[tIndex].getNumFigure()) { //found endPoint2 in tables array
               logger.debug(String.format("Endpoint 2 of tables found at table index '%d', named '%s'.", tIndex, tables[tIndex].getName()));

               connectors[cIndex].setIsEP2Table(true); //set appropriate flag
               table2Index = tIndex; //identify which element of the tables array that endPoint2 was found in
            }
         }

         logger.debug("Finished looping through tables.");
         
         if (connectors[cIndex].getIsEP1Field() && connectors[cIndex].getIsEP2Field()) { //both endpoints are fields, implies lack of normalization
            logger.info("Failed to read file.");
            logger.warn("Composite attributes found in Edge Diagrammer file. Lack of normalization.");
            logger.debug(String.format("File %s contains composite attributes.", parseFile.getAbsolutePath()));

            if (this.showGuis) {
               JOptionPane.showMessageDialog(null, "The Edge Diagrammer file\n" + parseFile + "\ncontains composite attributes. Please resolve them and try again.");
            }

            EdgeConvertGUI.setReadSuccess(false); //this tells GUI not to populate JList components

            timeLogger.info("resolveConnectors() ended.");

            return false; //stop processing list of Connectors
         }

         if (connectors[cIndex].getIsEP1Table() && connectors[cIndex].getIsEP2Table()) { //both endpoints are tables
            if ((connectors[cIndex].getEndStyle1().indexOf("many") >= 0) &&
                (connectors[cIndex].getEndStyle2().indexOf("many") >= 0)) { //the connector represents a many-many relationship, implies lack of normalization
               logger.info("Failed to read file.");
               logger.warn("Many-to-many relationship found in connector. Lack of normalization.");
               logger.debug(String.format("Many-to-many relation found in connectors. EdgeConnector.getEndStyle1() returned: %s. EdgeConnector.getEndStyle2() returned: %s", connectors[cIndex].getEndStyle1(), connectors[cIndex].getEndStyle2()));

               if (this.showGuis) {
                  JOptionPane.showMessageDialog(null, "There is a many-many relationship between tables\n\"" + tables[table1Index].getName() + "\" and \"" + tables[table2Index].getName() + "\"" + "\nPlease resolve this and try again.");
               }

               EdgeConvertGUI.setReadSuccess(false); //this tells GUI not to populate JList components

               timeLogger.info("resolveConnectors() ended.");

               return false; //stop processing list of Connectors
            } else { //add Figure number to each table's list of related tables
               logger.debug(String.format("Adding related table to table 1. Table 1 is '%s'. The related table is '%s'.", tables[table1Index].getName(), tables[table2Index].getName()));
               logger.debug(String.format("Adding related table to table 2. Table 2 is '%s'. The related table is '%s'.", tables[table2Index].getName(), tables[table1Index].getName()));

               tables[table1Index].addRelatedTable(tables[table2Index].getNumFigure());
               tables[table2Index].addRelatedTable(tables[table1Index].getNumFigure());
               continue; //next Connector
            }
         }
         
         if (fieldIndex >=0 && fields[fieldIndex].getTableID() == 0) { //field has not been assigned to a table yet
            logger.debug(String.format("fieldIndex '%s' has not been assigned a table yet.", fields[fieldIndex].getName()));

            if (connectors[cIndex].getIsEP1Table()) { //endpoint1 is the table
               logger.debug(String.format("End point 1, index '%d', is the table.", cIndex));

               logger.debug(String.format("Adding native field '%s' to table 1, named '%s'.", fields[fieldIndex].getName(), tables[table1Index].getName()));
               logger.debug(String.format("Setting table id to '%d' for field '%s'.", tables[table1Index].getNumFigure(), fields[fieldIndex].getName()));

               tables[table1Index].addNativeField(fields[fieldIndex].getNumFigure()); //add to the appropriate table's field list
               fields[fieldIndex].setTableID(tables[table1Index].getNumFigure()); //tell the field what table it belongs to
            } else { //endpoint2 is the table
               logger.debug("End point 2 is the table.");

               logger.debug(String.format("Adding native field '%s' to table 2, named '%s'.", fields[fieldIndex].getName(), tables[table2Index].getName()));
               logger.debug(String.format("Setting table id to '%d' for field '%s'.", tables[table2Index].getNumFigure(), fields[fieldIndex].getName()));

               tables[table2Index].addNativeField(fields[fieldIndex].getNumFigure()); //add to the appropriate table's field list
               fields[fieldIndex].setTableID(tables[table2Index].getNumFigure()); //tell the field what table it belongs to
            }
         } else if (fieldIndex >=0) { //field has already been assigned to a table
            logger.info("Failed to read file.");
            logger.debug(String.format("Field '%s' has already been assigned a table.", fields[fieldIndex].getName()));
            logger.warn(String.format("The attribute %s is connected to multiple tables.\nPlease resolve this and try again.", fields[fieldIndex].getName()));

            if (this.showGuis) {
               JOptionPane.showMessageDialog(null, "The attribute " + fields[fieldIndex].getName() + " is connected to multiple tables.\nPlease resolve this and try again.");
            }

            EdgeConvertGUI.setReadSuccess(false); //this tells GUI not to populate JList components

            timeLogger.info("resolveConnectors() ended.");

            return false; //stop processing list of Connectors
         }
      } // connectors for() loop

      logger.debug("Finished looping through connectors");

      timeLogger.info("resolveConnectors() ended.");
      return true;
   } // resolveConnectors()

   protected void makeArrays() { //convert ArrayList objects into arrays of the appropriate Class type
      timeLogger.info("makeArrays() called.");

      if (alTables != null) {
         logger.debug("alTables property is not null.");

         tables = (EdgeTable[])alTables.toArray(new EdgeTable[alTables.size()]);
      }
      if (alFields != null) {
         logger.debug("alFields property is not null.");

         fields = (EdgeField[])alFields.toArray(new EdgeField[alFields.size()]);
      }
      if (alConnectors != null) {
         logger.debug("alConnectors property is not null.");
         
         connectors = (EdgeConnector[])alConnectors.toArray(new EdgeConnector[alConnectors.size()]);
      }

      timeLogger.info("makeArrays() ended.");
   }
   
   protected boolean isTableDup(String testTableName) {
      timeLogger.info("isTableDup() called.");
      logger.debug(String.format("isTableDup() called with testTableName: %s", testTableName));

      logger.debug(String.format("About to loop through alTables property of size: %d", alTables.size()));

      for (int i = 0; i < alTables.size(); i++) {
         EdgeTable tempTable = (EdgeTable)alTables.get(i);
         
         logger.debug(String.format("Current EdgeTable name in loop: %s", tempTable.getName()));

         if (tempTable.getName().equals(testTableName)) {
            logger.warn("Duplicate table found.");
            logger.debug(String.format("Duplicate table name: %s", tempTable.getName()));

            timeLogger.info("isTableDup() ended.");
            logger.debug("Finished looping through alTables property.");
            return true;
         }
      }

      logger.debug("Finished looping through alTables property.");

      timeLogger.info("isTableDup() ended.");
      return false;
   }
   
   public EdgeTable[] getEdgeTables() {
      logger.info("Getting edge tables.");

      return tables;
   }
   
   public EdgeField[] getEdgeFields() {
      logger.info("Getting edge fields.");

      return fields;
   }

   protected abstract boolean parseFile(File constructorFile) throws IOException;
   
   public abstract boolean openFile();
} // EdgeConvertFileHandler
