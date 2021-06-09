import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.UIManager.*;
import javax.swing.SwingUtilities;

public class Main 

{
    private static boolean entryOpen;
    private static File logFile;
    private static PrintWriter out;
    
    
    public static void main(String[] args)
    {
 
        try
        {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (Exception e)
        {
        }
        
        
        //FlatIntelliJLaf.install();
        try
        {//edit this line so you can find the csv where you want to
            
            logFile = new File("Log.csv");

            if (logFile.createNewFile())
            {
                System.out.println("A new file was created");
            }
            else
            {
                System.out.println("A file already exists");
            }
        
        }
        catch (IOException e)
        {
             System.out.println("Something is broken");
             e.printStackTrace();
        }
       
        //This will generate the table 'inventory' if it doesn't exist
        Sql.alterTableQuery();
        //Create main frame
        MainFrame main = new MainFrame();
        main.getMainFrame().setVisible(true);
        
    }
    
    public static boolean getEntryOpen()
    {
        return entryOpen;
    }
    
    public static void setEntryOpen(boolean open)
    {
        entryOpen = open;
    }
  
    public static File getLogFile()
    {
        return logFile;
    }
}
