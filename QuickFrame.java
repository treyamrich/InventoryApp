import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

public class QuickFrame implements ActionListener
{
    private JFrame quickFrame;
    private JLabel hardware, sn, desc, prev;
    private JComboBox dropDown;
    private JTextField snField;
    private JTextArea description, prevEntry;
    private JRadioButton rIn, rOut;
    private JButton submit;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    private Date timeNow;
    private String prevHard, prevSN, prevDesc, prevCheck, prevTime;
    private String[] dropDownSelect= {"PC", "Monitor", "Printer", "Phone", "Receipt Printer", "Check Scanner", "Misc"};

    public QuickFrame()
    {
        //Frame setup and Initial empty previous entry box setup
        quickFrame = new JFrame("Add Entry");
        quickFrame.setSize(360, 720);
        quickFrame.setResizable(false);
        quickFrame.setLayout(null);
        prevHard = "";
        prevSN = "";
        prevDesc = "";
        prevCheck = "";
        prevTime = "";
        
        //hardware label
        hardware = new JLabel("Hardware",JLabel.LEFT);
        hardware.setBounds(10, 10, 100, 50);
        hardware.setFont(new Font("defaultFont", Font.PLAIN, 18));
        quickFrame.add(hardware);
        
        //dropdown menu
        dropDown = new JComboBox(dropDownSelect);
        dropDown.setBounds(10, 50, 200, 30);
        quickFrame.add(dropDown);
        
        //Serial Number Label
        sn = new JLabel("Serial Number:");
        sn.setBounds(10, 80, 200, 50);
        sn.setFont(new Font("defaultFont", Font.PLAIN, 18));
        quickFrame.add(sn);
        
        //serial number text field
        snField = new JTextField(10);
        snField.setBounds(10, 120, 320, 30);
        quickFrame.add(snField);
        
        //Description Label 
        desc = new JLabel("Description:");
        desc.setBounds(10, 150, 100, 50);
        desc.setFont(new Font("defaultFont", Font.PLAIN, 18));
        quickFrame.add(desc);
        
        //The text area for description
        description = new JTextArea();
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(description);
        descScroll.setBounds(10, 190, 320, 100);
        quickFrame.add(descScroll);
        
        //Check-in, check-out buttons
        ButtonGroup checkOption = new ButtonGroup();
        rIn = new JRadioButton("Check-in");
        rOut = new JRadioButton("Check-out");
        rIn.setBounds(40, 300, 200, 30);
        rOut.setBounds(40, 330, 200, 30);
        rIn.setFont(new Font("defaultFont", Font.PLAIN, 16));
        rOut.setFont(new Font("defaultFont", Font.PLAIN, 16));
        checkOption.add(rIn);
        checkOption.add(rOut);
        quickFrame.add(rIn);
        quickFrame.add(rOut);
        
        //Submit button
        submit = new JButton("Submit");
        submit.setBounds(120, 370, 100, 50);
        submit.addActionListener(this);
        quickFrame.add(submit);
        
        //previous entry label
        prev = new JLabel("Previous Entry:");
        prev.setBounds(10, 440, 300, 30);
        prev.setFont(new Font("defaultFont", Font.PLAIN, 18));
        quickFrame.add(prev);
        
        //Uneditable text area for previous entry
        prevEntry = new JTextArea("Date & Time: " + prevTime
                                    + "\nHardware Type: " + prevHard
                                    + "\nS/N: " + prevSN
                                    + "\nCheck-in/out: " + prevCheck
                                    + "\nDescription: " + prevDesc);
        prevEntry.setLineWrap(true);
        prevEntry.setWrapStyleWord(true);
        prevEntry.setEditable(false);
        JScrollPane prevScroll = new JScrollPane(prevEntry);
        prevScroll.setBounds(10, 470, 320, 150);
        quickFrame.add(prevScroll);

        //Prevents window from beign spammed open or closed
        quickFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.setEntryOpen(false);
            }
        });
        
    }
    
    public JFrame getQuickFrame()
    {
        return quickFrame;
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        //Submit Button
        if(e.getSource() == submit)
        {
            boolean normal = true;
            //Blank S/N field will throw a popup
            if (snField.getText().equals(""))
            {
                JOptionPane.showMessageDialog(null, "You have not entered a S/N", "Warning!", JOptionPane.WARNING_MESSAGE);
                normal = false;
            }
            //If neither radio button are selected, throw a popup
            else if (!rIn.isSelected() && !rOut.isSelected())
            {
                JOptionPane.showMessageDialog(null, "You have not selected an in/out option", "Warning!", JOptionPane.WARNING_MESSAGE);
                normal = false;
            }
            //Successful entry
            else
            {
                //Initializes variables
                String inTime = "";
                String outTime = "";
                
                //grabs information needed to make entry
                timeNow = new Date(System.currentTimeMillis());
                prevTime = sdf.format(timeNow);
                prevHard = dropDown.getSelectedItem().toString();
                prevSN = snField.getText();
                prevDesc = description.getText();
                if(rIn.isSelected())
                {
                    prevCheck = rIn.getText();
                    inTime = prevTime;
                }
                else if(rOut.isSelected())
                {
                    prevCheck = rOut.getText();
                    outTime = prevTime;
                }
                
                //Checks if there are any overlapping serial number
                ArrayList<String[]> oldData = Sql.filterQuery("serial_num", prevSN);   
                if(oldData.size() > 0)
                {
                    //If checkin has been selected on an item with the same serial number twice, same with checkout
                    if ((inTime.compareTo(outTime) > 0 && oldData.get(0)[2].compareTo(oldData.get(0)[3]) > 0)
                    || (inTime.compareTo(outTime) < 0 && oldData.get(0)[2].compareTo(oldData.get(0)[3]) < 0))
                    {
                        //Throws a popup inquiring if its okay to override data
                        String dialogue = "";
                        if(rIn.isSelected())
                        {
                            dialogue = "Checked-in";
                            outTime = oldData.get(0)[3];
                        }
                        else if (rOut.isSelected())
                        {
                            dialogue = "Checked-out";
                            inTime = oldData.get(0)[2];
                        }
                        JDialog.setDefaultLookAndFeelDecorated(true);
                        int response = JOptionPane.showConfirmDialog(null, "You've previously " + dialogue + " this S/N. Override Data?",
                                                             "Are You Sure?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if(response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION)
                        {
                            System.out.println("No/close button clicked");
                            normal = false;
                        }
                        else if (response == JOptionPane.YES_OPTION)
                        {
                            Sql.deleteQuery(prevSN);
                            Sql.insertQuery(prevHard, prevSN, inTime, outTime, prevDesc);
                            updateTables(prevHard, prevSN, inTime, outTime, prevDesc);
                            prevEntry.setText("Date & Time: " + prevTime
                              + "\nHardware Type: " + prevHard
                              + "\nS/N: " + prevSN
                              + "\nCheck-in/out: " + prevCheck
                              + "\nDescription: " + prevDesc);
                        }
                    }
                    else
                    {
                        //if there is the same serial number in the database but simply an update
                        if(rIn.isSelected())
                        {
                            outTime = oldData.get(0)[3];
                        }
                        else if (rOut.isSelected())
                        {
                            inTime = oldData.get(0)[2];
                        }
                        Sql.deleteQuery(prevSN);
                        String newDesc = prevDesc + " \nOld Description: " + oldData.get(0)[4];
                        Sql.insertQuery(prevHard, prevSN, inTime, outTime, newDesc);
                        Tables.updateCount(dropDown.getSelectedIndex() + 1, rIn.isSelected(), 1);
                        Tables.updateCount(dropDown.getSelectedIndex() + 1, rOut.isSelected(), -1);
                        Tables.updateCount(0, rIn.isSelected(), 1);
                        Tables.updateCount(0, rOut.isSelected(), -1);
                        updateTables(prevHard, prevSN, inTime, outTime, newDesc);
                        prevEntry.setText("Date & Time: " + prevTime
                              + "\nHardware Type: " + prevHard
                              + "\nS/N: " + prevSN
                              + "\nCheck-in/out: " + prevCheck
                              + "\nDescription: " + prevDesc);
                    }
                }
                //A new entry
                else
                {
                    Sql.insertQuery(prevHard, prevSN, inTime, outTime, prevDesc);
                    Tables.updateCount(dropDown.getSelectedIndex() + 1, rIn.isSelected(), 1);
                    Tables.updateCount(0, rIn.isSelected(), 1);
                    updateTables(prevHard, prevSN, inTime, outTime, prevDesc);
                    prevEntry.setText("Date & Time: " + prevTime
                              + "\nHardware Type: " + prevHard
                              + "\nS/N: " + prevSN
                              + "\nCheck-in/out: " + prevCheck
                              + "\nDescription: " + prevDesc);
                }
            }
            //Clears text field and area
            if (normal)
            {
                description.setText("");
                snField.setText("");
            }
        }
    }
    
    //Updates tables
    private void updateTables(String hardTable, String snTable, String inTable, String outTable, String descTable)
    {
        ArrayList<DefaultTableModel> models = Tables.getModels();
        for(int i = 1; i <= dropDownSelect.length; i++)
        {
            if(hardTable.equals(dropDownSelect[i - 1]))
            {
                DefaultTableModel theModel = models.get(i);
                for (int j = 0; j < theModel.getRowCount(); j++)
                {
                    if(theModel.getValueAt(j, 1).toString().equals(snTable))
                    {
                        theModel.removeRow(j);
                        break;
                    }
                }
                theModel.insertRow(0, new String[]{hardTable, snTable, inTable, outTable, descTable});
            }
        }
        DefaultTableModel allTable = models.get(0);
        for (int i = 0; i < allTable.getRowCount(); i++)
        {
            if(allTable.getValueAt(i, 1).toString().equals(snTable))
            {
                allTable.removeRow(i);
                break;
            }
        }
        allTable.insertRow(0, new String[]{hardTable, snTable, inTable, outTable, descTable});
    }
    
    
}
