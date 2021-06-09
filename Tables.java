import javax.swing.*;
import java.awt.*;
import javax.swing.table.*;
import java.util.ArrayList;


public class Tables
{
    private static JPanel pcTab, monitorTab, printerTab, phoneTab, allTab, receiptTab, scannerTab, miscTab, searchTab;
    private static DefaultTableModel allModel, pcModel, monitorModel, printerModel, phoneModel, receiptModel, scannerModel, miscModel, searchModel;
    private static ArrayList<JTable> tabs = new ArrayList<JTable>();
    private static ArrayList<DefaultTableModel> models = new ArrayList<DefaultTableModel>();
    private static ArrayList<int[]> counts = new ArrayList<int[]>();
    public static void genTables()
    {
        //this can be either on object or a string
        ArrayList<ArrayList<String[]>> datas = new ArrayList<ArrayList<String[]>>();
        
        ArrayList<String[]> allData = Sql.selectEverything();
        datas.add(allData);
        ArrayList<String[]> pcData = Sql.filterQuery("hardware_type", "PC");
        datas.add(pcData);
        ArrayList<String[]> monitorData = Sql.filterQuery("hardware_type", "Monitor");
        datas.add(monitorData);
        ArrayList<String[]> printerData = Sql.filterQuery("hardware_type", "Printer");
        datas.add(printerData);
        ArrayList<String[]> phoneData = Sql.filterQuery("hardware_type", "Phone");
        datas.add(phoneData);
        ArrayList<String[]> receiptData = Sql.filterQuery("hardware_type", "Receipt Printer");
        datas.add(receiptData);
        ArrayList<String[]> scannerData = Sql.filterQuery("hardware_type", "Check Scanner");
        datas.add(scannerData);
        ArrayList<String[]> miscData = Sql.filterQuery("hardware_type", "Misc");
        datas.add(miscData);
        ArrayList<String[]> searchData = new ArrayList<String[]>();
        datas.add(searchData);
                
        String tempArr[][]= new String[0][5];
        String column[] = {"Hardware Type", "S/N", "Time in", "Time out", "Description"};   
        
        allTab = new JPanel(new GridLayout());
        allModel = new DefaultTableModel(tempArr, column);
        models.add(allModel);
        prepTable(allData, allModel, allTab);
        
        pcTab = new JPanel(new GridLayout());
        pcModel = new DefaultTableModel(tempArr, column);
        models.add(pcModel);
        prepTable(pcData, pcModel, pcTab);
        
        monitorTab = new JPanel(new GridLayout());
        monitorModel = new DefaultTableModel(tempArr, column);
        models.add(monitorModel);
        prepTable(monitorData, monitorModel, monitorTab);
        
        printerTab = new JPanel(new GridLayout());
        printerModel = new DefaultTableModel(tempArr, column);
        models.add(printerModel);
        prepTable(printerData, printerModel, printerTab);
        
        phoneTab = new JPanel(new GridLayout());
        phoneModel = new DefaultTableModel(tempArr, column);
        models.add(phoneModel);
        prepTable(phoneData, phoneModel, phoneTab);
        
        receiptTab = new JPanel(new GridLayout());
        receiptModel = new DefaultTableModel(tempArr, column);
        models.add(receiptModel);
        prepTable(receiptData, receiptModel, receiptTab);
        
        scannerTab = new JPanel(new GridLayout());
        scannerModel = new DefaultTableModel(tempArr, column);
        models.add(scannerModel);
        prepTable(scannerData, scannerModel, scannerTab);
        
        miscTab = new JPanel(new GridLayout());
        miscModel = new DefaultTableModel(tempArr, column);
        models.add(miscModel);
        prepTable(miscData, miscModel, miscTab);
        
        searchTab = new JPanel(new GridLayout());
        searchModel = new DefaultTableModel(tempArr, column);
        models.add(searchModel);
        prepTable(searchData, searchModel, searchTab);
        
        for (int i = 0; i < datas.size(); i++)
        {
            int inCount = 0;
            int outCount = 0;
            for(int j = 0; j < datas.get(i).size(); j++)
            {
                if (datas.get(i).get(j)[2].compareTo(datas.get(i).get(j)[3]) > 0)
                {
                    inCount++;
                }
                else
                {
                    outCount++;
                }
            }
            counts.add(new int[]{inCount, outCount});
        }
        
    }
    
    private static void prepTable(ArrayList<String[]> arr, DefaultTableModel model, JPanel panel)
    {
        JTable jt = new JTable(model)
        {
            public boolean isCellEditable(int row,int col)
            {
                Object o = getValueAt(row,col);  
                return false;  
                    
            }  
        }; 
        jt.getTableHeader().setReorderingAllowed(false);
        //jt.getColumnModel().getColumn(0).setPreferredWidth(20);
        tabs.add(jt);
        jt.getColumnModel().getColumn(0).setPreferredWidth(100);
        jt.getColumnModel().getColumn(1).setPreferredWidth(250);
        jt.getColumnModel().getColumn(2).setPreferredWidth(170);
        jt.getColumnModel().getColumn(3).setPreferredWidth(170);
        jt.getColumnModel().getColumn(4).setPreferredWidth(500);
        for (int i = 0; i < arr.size(); i++)
        {
            model.addRow(arr.get(i));
        }
        panel.add((new JScrollPane(jt)));
    }
    
    public static void updateCount(int tabIdx, boolean in, int val)
    {
        int inZero = 1;
        if(in)
        {
            inZero = 0;
        }
        counts.get(tabIdx)[inZero] += val;
        if(tabIdx == MainFrame.getTp().getSelectedIndex())
        {
            MainFrame.getInField().setText("" + counts.get(tabIdx)[0]);
            MainFrame.getOutField().setText("" + counts.get(tabIdx)[1]);
        }
    }
    
    public static ArrayList<int[]> getCounts()
    {
        return counts;
    }
    
    public static void search(String type)
    {
        ArrayList<String[]> arr = new ArrayList<String[]>();
        if(type.equals("S/N"))
        {
            arr = Sql.filterQuery("serial_num", MainFrame.getSearchInput());
        }
        else
        {
            arr = Sql.filterByYearQuery(MainFrame.getSearchInput());
        }
        if(searchModel.getRowCount() > 0)
        {
            for (int i = searchModel.getRowCount() - 1; i > -1; i--)
            {
                searchModel.removeRow(i);
            }
        }
        counts.get(models.size()-1)[0] = 0;
        counts.get(models.size()-1)[1] = 0;
        for (int i = 0; i < arr.size(); i++)
        {
            searchModel.addRow(arr.get(i));
            updateCount(models.size() - 1, arr.get(i)[2].compareTo(arr.get(i)[3]) > 0, 1);
            //updateCount(models.size() - 1, arr.get(i)[2].compareTo(arr.get(i)[3]) <= 0, -1);
        }
        //Add search to interface next
    }
    
    public static void export()
    {
        //code to export data to csv
    }
    
    public static JPanel getMonitorTab()
    {
            return monitorTab;
    }
    
    public static JPanel getPcTab()
    {
            return pcTab;
    }
    
    public static JPanel getPrinterTab()
    {
            return printerTab;
    }
    
    public static JPanel getPhoneTab()
    {
            return phoneTab;
    }
    
    public static JPanel getAllTab()
    {
            return allTab;
    }
    
    public static JPanel getReceiptTab()
    {
        return receiptTab;
    }
    
    public static JPanel getScannerTab()
    {
        return scannerTab;
    }
    
    public static JPanel getMiscTab()
    {
        return miscTab;
    }
    
    public static JPanel getSearchTab()
    {
            return searchTab;
    }
    
    public static ArrayList<JTable> getJTables()
    {
            return tabs;
    }
    
    public static ArrayList<DefaultTableModel> getModels()
    {
        return models;
    }
}
