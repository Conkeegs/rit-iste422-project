import javax.swing.*;   

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateDDLMySQL extends EdgeConvertCreateDDL {

   protected String databaseName;
   //this array is for determining how MySQL refers to datatypes
   protected StringBuffer sb;
   protected boolean showGuis = true;
   
   public static Logger logger = LogManager.getLogger(EdgeConvertCreateDDL.class.getName());
   public static Logger timeLogger = LogManager.getLogger("timer." + EdgeConvertCreateDDL.class.getName());

   public CreateDDLMySQL(EdgeTable[] inputTables, EdgeField[] inputFields) {
      this(inputTables, inputFields, true);
   }

   public CreateDDLMySQL(EdgeTable[] inputTables, EdgeField[] inputFields, boolean showGuis) {
      super(inputTables, inputFields);
      this.showGuis = showGuis;
      sb = new StringBuffer();
   } //CreateDDLMySQL(EdgeTable[], EdgeField[])
   
   public CreateDDLMySQL() { //default constructor with empty arg list for to allow output dir to be set before there are table and field objects
      
   }
   
   public void createDDL() {
      timeLogger.info("Constructor called.");
      EdgeConvertGUI.setReadSuccess(true);
      final String[] strDataType = {"VARCHAR", "BOOL", "INT", "DOUBLE"};
      databaseName = generateDatabaseName().replaceAll(" ", "");
      sb.append("CREATE DATABASE " + databaseName + ";\r\n");
      sb.append("USE " + databaseName + ";\r\n");
      for (int boundCount = 0; boundCount <= maxBound; boundCount++) { //process tables in order from least dependent (least number of bound tables) to most dependent
         for (int tableCount = 0; tableCount < numBoundTables.length; tableCount++) { //step through list of tables
            if (numBoundTables[tableCount] == boundCount) { //
               sb.append("CREATE TABLE " + tables[tableCount].getName() + " (\r\n");
               logger.debug("creating table"+tables[tableCount].getName());
               int[] nativeFields = tables[tableCount].getNativeFieldsArray();
               int[] relatedFields = tables[tableCount].getRelatedFieldsArray();
               boolean[] primaryKey = new boolean[nativeFields.length];
               int numPrimaryKey = 0;
               int numForeignKey = 0;
               for (int nativeFieldCount = 0; nativeFieldCount < nativeFields.length; nativeFieldCount++) { //print out the fields
                  EdgeField currentField = getField(nativeFields[nativeFieldCount]);
                  sb.append("\t" + currentField.getName() + " " + strDataType[currentField.getDataType()]);
                  if (currentField.getDataType() == 0) { //varchar
                     sb.append("(" + currentField.getVarcharValue() + ")"); //append varchar length in () if data type is varchar
                  }
                  if (currentField.getDisallowNull()) {
                     sb.append(" NOT NULL");
                  }
                  if (!currentField.getDefaultValue().equals("")) {
                     if (currentField.getDataType() == 1) { //boolean data type
                        sb.append(" DEFAULT " + convertStrBooleanToInt(currentField.getDefaultValue()));
                     } else { //any other data type
                        sb.append(" DEFAULT " + (currentField.getDataType() == 0 ? "'" + currentField.getDefaultValue().replaceAll("\"|'", "") + "'" : currentField.getDefaultValue()));
                     }
                  }
                  if (currentField.getIsPrimaryKey()) {
                     primaryKey[nativeFieldCount] = true;
                     numPrimaryKey++;
                  } else {
                     primaryKey[nativeFieldCount] = false;
                  }
                  if (currentField.getFieldBound() != 0) {
                     numForeignKey++;
                  }
                  // write "," at the end of the current line if CONSTRAINT_(name)_PK, CONSTRAINT_(name)_PK, or the next field will be written in the next line
                  if (numPrimaryKey > 0 || numForeignKey > 0 || nativeFieldCount < nativeFields.length - 1) {
                     sb.append(",");
                  }
                  sb.append("\r\n"); // end of field - (fixed bug by removing "," and put into the if-else condition above to avoid writting "," after the last column)
               }
               if (numPrimaryKey > 0) { // table has primary key(s)
                  sb.append("CONSTRAINT " + tables[tableCount].getName() + "_PK PRIMARY KEY (");
                  for (int i = 0; i < primaryKey.length; i++) {
                     if (primaryKey[i]) {
                        sb.append(getField(nativeFields[i]).getName());
                        numPrimaryKey--;
                        if (numPrimaryKey > 0) {
                           sb.append(", ");
                        }
                     }
                  }
                  sb.append(")");
                  if (numForeignKey > 0) {
                     sb.append(",");
                  }
                  sb.append("\r\n");
               }
               if (numForeignKey > 0) { //table has foreign keys
                  int currentFK = 1;
                  for (int i = 0; i < relatedFields.length; i++) {
                     if (relatedFields[i] != 0) {
                        sb.append("CONSTRAINT " + tables[tableCount].getName() + "_FK" + currentFK +
                                  " FOREIGN KEY(" + getField(nativeFields[i]).getName() + ") REFERENCES " +
                                  getTable(getField(nativeFields[i]).getTableBound()).getName() + "(" + getField(relatedFields[i]).getName() + ")");
                        if (currentFK < numForeignKey) {
                           sb.append(",\r\n");
                        }
                        currentFK++;
                     }
                  }
                  sb.append("\r\n");
               }
               sb.append(");\r\n\r\n"); //end of table
            }
         }
      }
      timeLogger.info("Constructor ended.");
   }

   protected int convertStrBooleanToInt(String input) { //MySQL uses '1' and '0' for boolean types
      timeLogger.info("method convertStrBooleanToInt called.");
      if (input.equals("true")) {
         timeLogger.info("method convertStrBooleanToInt ended.");
         return 1;
      } else {
         timeLogger.info("method convertStrBooleanToInt ended.");
         return 0;
      }
   }
   
   public String generateDatabaseName() { //prompts user for database name
      timeLogger.info("method generateDatabaseName called.");
      String dbNameDefault = "MySQLDB";
      //String databaseName = "";

      do {
         if (this.showGuis) {
            databaseName = (String)JOptionPane.showInputDialog(
                       null,
                       "Enter the database name:",
                       "Database Name",
                       JOptionPane.PLAIN_MESSAGE,
                       null,
                       null,
                       dbNameDefault);
         }
         if (databaseName == null) {
            EdgeConvertGUI.setReadSuccess(false);
            return "";
         }
         if (databaseName.equals("")) {
            if (this.showGuis) {
               JOptionPane.showMessageDialog(null, "You must select a name for your database.");
            }
         }
      } while (databaseName.equals(""));
      timeLogger.info("method generateDatabaseName ended.");
      return databaseName;
   }
   
   public String getDatabaseName() {
      return databaseName;
   }
   
   public String getProductName() {
      return "MySQL";
   }

   public String getSQLString() {
      createDDL();
      return sb.toString();
   }
   
}//EdgeConvertCreateDDL
