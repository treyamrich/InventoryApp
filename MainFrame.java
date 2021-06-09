import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//just being used for testing purposes 
import java.util.Scanner;
import java.util.Date;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MainFrame extends JFrame implements ActionListener, ListSelectionListener
{
    private JFrame mainFrame;
    private static JTabbedPane tp;
    private JButton entryButton,deleteButton, filterButton, timeInButton, timeOutButton,searchButton;
    private JComboBox sort, searchOption;
    private JLabel sortLabel, searchLabel, inLabel, outLabel, logoLabel;
    private JTextField search;
    private static String searchInput;
    private JMenuBar mb;
    private JMenu file, view, sortBy;
    private JMenuItem exportFile, importFile, keyFeature;
    private String[] arrColumnHeaders = {"PC", "Monitor", "Printer", "Phone", "Receipt Printer", "Check Scanner", "Misc"};
    private static JTextField inAmt, outAmt;
    private boolean clickRow = true;
    private int logCount = 0;

    public MainFrame()
    {
    
        //creates the main frame
        mainFrame = new JFrame("Inventory Program");
        mainFrame.setSize(1280, 750);
        mainFrame.setResizable(false);
        //mainFrame.setBackground(new java.awt.Color(0,166,166));
   
        // creates the tabbed pane 
        tp = new JTabbedPane();
        tp.setBounds(10,45,1230, 600);        
        
        Tables.genTables();
        
        //adds the tabs
        tp.add("All", Tables.getAllTab());
        tp.add("PC",Tables.getPcTab());  
        tp.add("Monitor",Tables.getMonitorTab());
        tp.add("Printer",Tables.getPrinterTab());
        tp.add("Phone", Tables.getPhoneTab());
        tp.add("Receipt Printer", Tables.getReceiptTab());
        tp.add("Check Scanner", Tables.getScannerTab());
        tp.add("Misc", Tables.getMiscTab());
        tp.add("Search Results", Tables.getSearchTab());
        mainFrame.add(tp);
        
        //JButton that opens the QuickMenu
        entryButton = new JButton("Add Entry");  
        entryButton.setBounds(1100,17,120,30);  
        entryButton.addActionListener(this);
        mainFrame.add(entryButton);  
        
        //JMenu open up file
        mb = new JMenuBar();
        //file option on the JMenuBar
        file = new JMenu("File");

        //Export menu item
        exportFile = new JMenuItem("Export as .csv");
        exportFile.addActionListener(this);
        //Import menu item
        importFile = new JMenuItem("Import .csv file");
        importFile.addActionListener(this);
        
        keyFeature = new JMenuItem("Special Feature");
        keyFeature.addActionListener(this);
        
        //Add to frames
        file.add(exportFile);
        file.add(importFile);
        mb.add(file);
        mainFrame.setJMenuBar(mb);
        
       
        //JButton that deletes the selected tables
        deleteButton = new JButton("Delete");
        deleteButton.setBounds(1100,650,120,30);
        deleteButton.addActionListener(this);
        mainFrame.add(deleteButton);

        //JButton for Time in
        timeInButton = new JButton("Check In");
        timeInButton.setBounds(40, 650, 120, 30);
        timeInButton.addActionListener(this);
        mainFrame.add(timeInButton);
        
        //JButton for Time out 
        timeOutButton = new JButton("Check Out");
        timeOutButton.setBounds(170, 650, 120, 30);
        timeOutButton.addActionListener(this);
        mainFrame.add(timeOutButton);
        
        //textfield for searching
        search = new JTextField();
        search.setBounds(850,17,120,31);
        mainFrame.add(search);
        
        //label for searching
        searchLabel = new JLabel("Search by");
        searchLabel.setBounds(730,17,120,30);
        mainFrame.add(searchLabel);
        
        //Combo Box for Search Option
        String[] differentSearch = {"S/N", "Year"};
        searchOption = new JComboBox(differentSearch);
        searchOption.setBounds(790, 17, 60, 30);
        mainFrame.add(searchOption);
        
        //JButton for searching
        searchButton = new JButton("Search");
        searchButton.setBounds(970,17,100,30);
        searchButton.addActionListener(this);
        mainFrame.add(searchButton);
        
        //Label for in
        inLabel = new JLabel("In: ");
        inLabel.setBounds(640, 21,  30, 10);
        mainFrame.add(inLabel);
        
        //Label for out
        outLabel = new JLabel("Out: ");
        outLabel.setBounds(630, 42, 30, 10);
        mainFrame.add(outLabel);
        
        //text field that displays how many items are in
        inAmt = new JTextField("" + Tables.getCounts().get(0)[0]);
        inAmt.setBounds(660, 15, 60, 25);
        inAmt.setEditable(false);
        mainFrame.add(inAmt);
        
        //text field that displays how many items are out
        outAmt = new JTextField("" + Tables.getCounts().get(0)[1]);
        outAmt.setBounds(660, 35, 60, 25);
        outAmt.setEditable(false);
        mainFrame.add(outAmt);
        
        tp.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e){
                inAmt.setText("" + Tables.getCounts().get(tp.getSelectedIndex())[0]);
                outAmt.setText("" + Tables.getCounts().get(tp.getSelectedIndex())[1]);
            }
        });
        
        for(int i = 0; i < Tables.getJTables().size(); i++)
        {
            Tables.getJTables().get(i).getSelectionModel().addListSelectionListener(this);
        }

        
        mainFrame.setLayout(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Main.setEntryOpen(false);
    }
    public JFrame getMainFrame()
    {
        return mainFrame;
    }
    public static JTextField getInField()
    {
        return inAmt;
    }
    public static JTextField getOutField()
    {
        return outAmt;
    }
    public static JTabbedPane getTp()
    {
        return tp;
    }
    
    public boolean isIn(String timeIn, String timeOut)
    {
        return timeIn.compareTo(timeOut) > 0;
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e)
    {
       for(int i = 0; i < Tables.getJTables().size(); i++)
       {
           if(clickRow)
           {
               if (i == tp.getSelectedIndex())
               {
                    int[] indexes = getSelectedIndexInformation();
                    System.out.println("Its" + Tables.getJTables().get(indexes[0]).getSelectedRow());
                    String timeIn = Tables.getJTables().get(indexes[0]).getValueAt(indexes[1], 2).toString();
                    String timeOut = Tables.getJTables().get(indexes[0]).getValueAt(indexes[1], 3).toString();
                    if(timeIn.compareTo(timeOut) > 0)
                    {
                        timeInButton.setEnabled(false);
                        timeOutButton.setEnabled(true);
                    }
                    else
                    {
                        timeInButton.setEnabled(true);
                        timeOutButton.setEnabled(false);
                    }
               }
           }
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        if(e.getSource() == entryButton && !Main.getEntryOpen())
        {
            Main.setEntryOpen(true);
            QuickFrame quick = new QuickFrame();
            quick.getQuickFrame().setVisible(true);
        }
        
  
        
    
       
        
        if(e.getSource() == deleteButton)
        {
            //Get the selected row/cell
            clickRow = false;
            int indexes[] = getSelectedIndexInformation();
            //If indexes[1] is negative then there is no selected row in a tab
            
            //If current tab is search tab
            boolean searchTab = indexes[0] == 8;
            
            Object objHardwareType = Tables.getJTables().get(indexes[0]).getValueAt(indexes[1], 0);
            String hardwareType = objHardwareType.toString();
           
            try
            {
                
                FileWriter fw = new FileWriter(Main.getLogFile(), logCount != 0);
                PrintWriter out = new PrintWriter(fw);
                
                    for (int i = 0; i < 5; i++)
                    {
                        out.print(Tables.getJTables().get(indexes[0]).getValueAt(indexes[1], i));
                        out.print(",");
                        
                    }
                    out.println();
                    //out.flush;
                    //out.close;
                    out.close();   
            }
            catch (IOException a)
            {
                a.printStackTrace();
            
            }
       
            
            
            //
            Object objSerialNumber = Tables.getJTables().get(indexes[0]).getValueAt(indexes[1], 1);
            String serialNumber = objSerialNumber.toString();
            
            //Find associated tab indexes
            int[] associatedTabIndexes = findAssociatedTabIndexes(indexes[0], hardwareType, serialNumber, searchTab);
            //If there is a current selection in the tab
            if(indexes[1] >= 0) {
                //Update the counter of check in/out items
                String oldTimeIn = Tables.getJTables().get(indexes[0]).getValueAt(indexes[1], 2).toString();
                String oldTimeOut = Tables.getJTables().get(indexes[0]).getValueAt(indexes[1], 3).toString();
                Tables.updateCount(indexes[0], isIn(oldTimeIn, oldTimeOut), -1);
                Tables.updateCount(associatedTabIndexes[0], isIn(oldTimeIn, oldTimeOut), -1);
                if(searchTab)
                {
                    Tables.updateCount(associatedTabIndexes[2], isIn(oldTimeIn, oldTimeOut), -1);
                }
                //Delete data
                deleteData(indexes, associatedTabIndexes, searchTab);
                logCount++;
            }
            clickRow = true;
        }
        
        /*if (e.getSource() == filterButton && sort.getItemAt(sort.getSelectedIndex()) == "Last Week")
        {
            Scanner input = new Scanner(System.in);
            System.out.println("last week is working");
        }
        
        if (e.getSource() == filterButton && sort.getItemAt(sort.getSelectedIndex()) == "Last Two Weeks")
        {
            System.out.println("last two weeks is working");
        }
        
        if (e.getSource() == filterButton && sort.getItemAt(sort.getSelectedIndex()) == "Last Month")
        {
            System.out.println("last month is working");
        }*/
        
        //Timein button action
        if (e.getSource() == timeInButton)
        {
            clickRow = false;
            System.out.println("time in button is working");
            //Get the selected row/cell location
            int indexes[] = getSelectedIndexInformation();
            //If current tab is search tab
            boolean searchTab = indexes[0] == 8;
            //Check if there is a selected row
            //If indexes[1] is negative then there is no selected row
            if (indexes[1] >= 0) {
                //Save data at selected indexes
                String[] savedData = saveData(indexes);
                //Find associated tab indexes
                int[] associatedTabIndexes = findAssociatedTabIndexes(indexes[0], savedData[0], savedData[1], searchTab);
                //Alter timestamp 
                //Since this button is for time in, replace the second index
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                String timeNow = sdf.format(new Date(System.currentTimeMillis()));
                savedData[2] = timeNow;
                //Delete data
                deleteData(indexes, associatedTabIndexes, searchTab);
                //Re-add data
                addData(indexes, savedData, associatedTabIndexes, searchTab);
                //Update the counter of check in/out items
                Tables.updateCount(indexes[0], true, 1);
                Tables.updateCount(associatedTabIndexes[0], true, 1);
                Tables.updateCount(indexes[0], false, -1);
                Tables.updateCount(associatedTabIndexes[0], false, -1);
                if(searchTab)
                {
                    Tables.updateCount(associatedTabIndexes[2], true, 1);
                    Tables.updateCount(associatedTabIndexes[2], false, -1);
                }
            }
            clickRow = true;
        }
        //Time out button action
        if (e.getSource() == timeOutButton)
        {
            clickRow = false;
            System.out.println("time out button is working");
            //Get the selected row/cell
            int indexes[] = getSelectedIndexInformation();
            //If current tab is search tab
            boolean searchTab = indexes[0] == 8;
            //Check if there is a selected row
            //If indexes[1] is negative then there is no selected row
            if (indexes[1] >= 0) {
                //Save data at selected indexes
                String[] savedData = saveData(indexes);
                //Find associated tab indexes (index 0 is tab)
                int[] associatedTabIndexes = findAssociatedTabIndexes(indexes[0], savedData[0], savedData[1], searchTab);
                //Alter timestamp 
                //Since this button is for time in, replace the second index
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                String timeNow = sdf.format(new Date(System.currentTimeMillis()));
                savedData[3] = timeNow;
                //Delete data
                deleteData(indexes, associatedTabIndexes, searchTab);
                //Re-add data 
                addData(indexes, savedData, associatedTabIndexes, searchTab);
                //Update the counter of check in/out items
                Tables.updateCount(indexes[0], false, 1);
                Tables.updateCount(associatedTabIndexes[0], false, 1);
                Tables.updateCount(indexes[0], true, -1);
                Tables.updateCount(associatedTabIndexes[0], true, -1);
                if(searchTab)
                {
                    Tables.updateCount(associatedTabIndexes[2], false, 1);
                    Tables.updateCount(associatedTabIndexes[2], true, -1);
                }
            }
            clickRow = true;
        }
        //search button action
        if (e.getSource() == searchButton)
        {
            clickRow = false;
            searchInput = search.getText();
            String searchType = searchOption.getSelectedItem().toString();
            Tables.search(searchType);
            clickRow = true;
        }
        //JMenu Item Action
        if (e.getSource() == exportFile)
        {
            //Creates a folder selector and returns path
            String filePath = openFile(true); 
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmm");
            String timeNow = sdf.format(new Date(System.currentTimeMillis()));
            try
            {
                //edit this line so you can find the csv where you want to
                File myObj = new File(filePath, "Inventory " + timeNow + ".csv");
                    
                if (myObj.createNewFile())
                {
                    System.out.println("File created: " + myObj.getName());
                }   
                else
                {
                    System.out.println("File alradey exists");
                }
            }
            catch (IOException p)
            {
                System.out.println("an error occurred");
                p.printStackTrace();
            }
    
            try
            {
                FileWriter fw = new FileWriter(filePath + "\\" + "Inventory " + timeNow + ".csv");
                PrintWriter out = new PrintWriter(fw);
                ArrayList<String[]> exportArray = Sql.selectEverything();
                out.print("Hardware Type");
                out.print(",");
                out.print("Serial Number");
                out.print(",");
                out.print("Time In");
                out.print(",");
                out.print("Time Out");
                out.print(",");
                out.print("Description");
                out.println();
                for (int i = 0; i < exportArray.size(); i++)
                {
                    for (int a = 0; a < exportArray.get(0).length; a++)
                    {
                        if(a != 4) {
                            out.print(exportArray.get(i)[a]);
                            out.print(",");
                        } else {
                            out.print(exportArray.get(i)[a]);
                        }
                    }
                    out.println();
                }
                //out.flush;
                //out.close;
                fw.close();
            }
            catch (IOException b)
            {
                System.out.println("an error occurred");
                b.printStackTrace();
            }
        } 
        if(e.getSource() == importFile)
        {
            //Step 1. Select file
            String filePath = openFile(false);
            //Step 2. Traverse file
            ArrayList<String[]> importResults = readFile(filePath);
            //Step 3. Check cleanse data 
            if(isCleanData(importResults)) {
                //Step 4. Add data to tabs and associated tabs
                for(int i = 0; i < importResults.size(); i++)
                {
                    String hardwareType = importResults.get(i)[0];
                    int associatedTabIndex = -1;
                    switch(hardwareType) {
                        case "PC":
                            associatedTabIndex = 1;
                            break;
                        case "Monitor":
                            associatedTabIndex = 2;
                            break;
                        case "Printer":
                            associatedTabIndex = 3;
                            break;
                        case "Phone":
                            associatedTabIndex = 4;
                            break;
                        case "Receipt Printer":
                            associatedTabIndex = 5;
                            break;
                        case "Check Scanner":
                            associatedTabIndex = 6;
                            break;    
                        case "Misc":
                            associatedTabIndex = 7;
                            break;
                }
                
                //Get time
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                String timeNow = sdf.format(new Date(System.currentTimeMillis()));
                //Add two columns to the data given to add the time in
                String[] newImportResults = new String[5];
                newImportResults[0] = importResults.get(i)[0];
                newImportResults[1] = importResults.get(i)[1];
                //Change time in
                newImportResults[2] = timeNow;
                newImportResults[3] = "";
                newImportResults[4] = importResults.get(i)[2];
                //Add data
                addData(new int[]{0}, newImportResults, new int[]{associatedTabIndex}, false);
                
                //UPDATE THE COUNTER
                Tables.updateCount(0, true, 1);
                Tables.updateCount(associatedTabIndex, true, 1);
            }
            } else {
                JOptionPane.showMessageDialog(null, "The CSV file is not in the correct format", "Error!", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    public static String getSearchInput()
    {
        return searchInput;
    }
    //This method is used to open a window to select a file
    //It returns the file path of the file selected
    public String openFile(Boolean isFolder)
    {
        JFileChooser fc = new JFileChooser();
        if(isFolder)
        {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        String filePath = "";
        int i = fc.showOpenDialog(this);
        if(i == JFileChooser.APPROVE_OPTION)
        {
            File f = fc.getSelectedFile();
            filePath = f.getPath();
        }
        return filePath;
    }
    //This method returns an array list of string arrays
    //Precondition: The CSV must have the first column with with PC, Montior, Printer, etc. (Columns = Hardware Type, Serial Number, Description)
    //Postcondition: An array is returned containing arrays of strings
    public ArrayList<String[]> readFile(String filePath)
    {
        ArrayList<String[]> arr = new ArrayList<String[]>();
        try(Scanner reader = new Scanner(new File(filePath));)
        {
            while(reader.hasNextLine()) {
                String line = reader.nextLine();
                //Split the line into 3
                String[] values = line.split(",", 3);
                //Add to array
                String[] tempArr = new String[3];
                for(int i = 0; i < 3; i++) {
                    tempArr[i] = values[i];
                }
                //Add to array list
                arr.add(tempArr);
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        }
        return arr;
    }
    //This method compares an array to the column headers array
    //Precondition: An ArrayList<String[]> must be passed into the method
    //Postcondition: Returns false if atleast one of the rows in the column of hardware_type is not found in arrColumnHeaders, else returns true
    public boolean isCleanData(ArrayList<String[]> arr) {
        boolean isClean = true;
       
        //Check formatting
        for(int i = 0; i < arr.size(); i++) {
            boolean foundInHeaders = false;
            //arrColumnHeaders is declared at the very top of MainFrame.java
            for(int j = 0; j < arrColumnHeaders.length; j++) {
                //If the hardware type tag is found in column headers then return true, else false
                if(arr.get(i)[0].equals(arrColumnHeaders[j])) {
                    foundInHeaders = true;
                }
            }
            if(!foundInHeaders) {
                isClean = false;
            }
        }
        //Remove first array because of headers
        arr.remove(0);
        return isClean;
    }
    //This method returns an array of strings that contains the current selected cell's data
    public String[] saveData(int[] indexes)
    {
        String[] arr = new String[5];
        //Cache temporarily current data
        Object objHardwareType = Tables.getJTables().get(indexes[0]).getValueAt(indexes[1], 0);
        String hardwareType = objHardwareType.toString();
        //
        Object objSerialNumber = Tables.getJTables().get(indexes[0]).getValueAt(indexes[1], 1);
        String serialNumber = objSerialNumber.toString();
        //
        Object objTimeIn = Tables.getJTables().get(indexes[0]).getValueAt(indexes[1], 2);
        String timeIn = objTimeIn.toString();
        //
        Object objTimeOut = Tables.getJTables().get(indexes[0]).getValueAt(indexes[1], 3);
        String timeOut = objTimeOut.toString();
        //
        Object objDesc = Tables.getJTables().get(indexes[0]).getValueAt(indexes[1], 4);
        String desc = objDesc.toString();
        //Add data array
        arr[0] = hardwareType;
        arr[1] = serialNumber;
        arr[2] = timeIn;
        arr[3] = timeOut;
        arr[4] = desc;
        return arr;
    }
    //This method removes data from SQL Database and local interface based off of what is currently selected 
    public void deleteData(int[]indexes, int[]associatedTabIndexes, boolean searchTab)
    {
        //Step 1. Remove from interface
        //indexes[0] is current tab index, indexes[1] is row in tab
        Object objSerialNumber = Tables.getJTables().get(indexes[0]).getValueAt(indexes[1], 1);
        String serialNumber = objSerialNumber.toString();
        if (indexes[1] >= 0)
        {
            //Remove from current tab
            Tables.getModels().get(indexes[0]).removeRow(indexes[1]);
            //Remove from associated tab
            Tables.getModels().get(associatedTabIndexes[0]).removeRow(associatedTabIndexes[1]);
            if(searchTab) {
                Tables.getModels().get(associatedTabIndexes[2]).removeRow(associatedTabIndexes[3]);
            }
        }
        //Step 2. Remove from SQL Database
        Sql.deleteQuery(serialNumber);
    }
    //This method adds data to SQL Database and to the interface of all the tabs
    public void addData(int[]indexes, String[] data, int[] associatedTabIndexes, boolean searchTab)
    {
        //Step 1. Add to interface
        //index[0] is current tab index, indexes[1] is row in tab
        
        //Add to current tab
        Tables.getModels().get(indexes[0]).insertRow(0, new Object[]{data[0], data[1], data[2], data[3], data[4]});
        //Add to associated tab
        Tables.getModels().get(associatedTabIndexes[0]).insertRow(0, new Object[]{data[0], data[1], data[2], data[3], data[4]});
        if(searchTab) {
            Tables.getModels().get(associatedTabIndexes[2]).insertRow(0, new Object[]{data[0], data[1], data[2], data[3], data[4]});
        }
        //Step 2. Add to SQL Database
        Sql.insertQuery(data[0], data[1], data[2], data[3], data[4]);
    }
    //This method returns the index of the selected tab and the index of the row that is selected in the tab
    public int[] getSelectedIndexInformation()
    {
        int[] indexes = new int[2];
        //Current selected tab
        indexes[0] = tp.getSelectedIndex();
        //Current selected row in the selected tab
        indexes[1] = Tables.getJTables().get(tp.getSelectedIndex()).getSelectedRow();
        return indexes;
    }
    //This method finds the associated tab indexes and indexes of the associated tab row based off
    //the information of the current selection's serial number and hardware type
    public int[] findAssociatedTabIndexes(int currentTabIndex, String hardwareType, String serialNum, boolean searchTab)
    {
        int[] arr = new int[4];
        int associatedTabIndex = -1; 
        int associatedTabIndexRow = -1;
        //If the user is in the search tab the second associated tab is always the all tab
        int associatedTabIndex2 = 0;
        int associatedTabIndexRow2 = -1;
        //Check current tab if its all tab then find the associated tab
        if(currentTabIndex == 0 || currentTabIndex == 8) {
            switch(hardwareType) {
                case "PC":
                    associatedTabIndex = 1;
                    break;
                case "Monitor":
                    associatedTabIndex = 2;
                    break;
                case "Printer":
                    associatedTabIndex = 3;
                    break;
                case "Phone":
                    associatedTabIndex = 4;
                    break;
                case "Receipt Printer":
                    associatedTabIndex = 5;
                    break;
                case "Check Scanner":
                    associatedTabIndex = 6;
                    break;    
                case "Misc":
                    associatedTabIndex = 7;
                    break;
            } 
        } else {
            associatedTabIndex = 0;
        }
        //Find the associated tab index row
        for(int i = 0; i < Tables.getModels().get(associatedTabIndex).getRowCount(); i++) {
            //tempVal will be null if the cell is not found in the specificied model
            String tempVal = Tables.getModels().get(associatedTabIndex).getValueAt(i, 1).toString();
            if(tempVal.equals(serialNum))
            {
                //Since model rows start from 0 use i to find the row
                associatedTabIndexRow = i;
            }
        }
        //If searchTab, then it will ALWAYS search the all tab for the cell
        if(searchTab) {
            for(int j = 0; j < Tables.getModels().get(0).getRowCount(); j++) {
                //tempVal will be null if the cell is not found in the specificied model
                String tempVal = Tables.getModels().get(0).getValueAt(j, 1).toString();
                if(tempVal.equals(serialNum))
                {
                    //Since model rows start from 0 use i to find the row
                    associatedTabIndexRow2 = j;
                }
            }
        }
        //Add indexes to array
        arr[0] = associatedTabIndex;
        arr[1] = associatedTabIndexRow;
        arr[2] = associatedTabIndex2;
        arr[3] = associatedTabIndexRow2;
        return arr;
    }
    
    
}