
//package task_manager;

import com.emmanual.swing.Table;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


class Task<T> {
    private T taskName;
    private T taskCategory;
    private T taskDescription;
    private T taskStatus;
    

    public Task(T taskName, T taskCategory, T taskDescription, T taskStatus) {
        this.taskName = taskName;
        this.taskCategory = taskCategory;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
    }

    public T getTaskName() {
        return taskName;
    }

    public void setTaskName(T taskName) {
        this.taskName = taskName;
    }

    public T getTaskCategory() {
        return taskCategory;
    }

    public void setTaskCategory(T taskCategory) {
        this.taskCategory = taskCategory;
    }

    public T getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(T taskDescription) {
        this.taskDescription = taskDescription;
    }

    public T getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(T taskStatus) {
        this.taskStatus = taskStatus;
    }
}

class TaskManager {
    private ArrayList<Task<String>> tasks;
    

    public TaskManager() {
        this.tasks = new ArrayList<>();
    }

    public void addTask(Task<String> task) {
        tasks.add(task);
    }

    public void readTasksFromDatabase() {
        try {
            java.sql.Connection conn;
            // Connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/taskmanager", "root", "egMT5@me");
            Statement statement = conn.createStatement();

            // Execute the query
            ResultSet rs = statement.executeQuery("SELECT task_name, task_category, task_description, task_status FROM task_information");

            // Create a Task object for each row in the result set
            while (rs.next()) {
                String taskName = rs.getString("task_name");
                String taskCategory = rs.getString("task_category");
                String taskDescription = rs.getString("task_description");
                String taskStatus = rs.getString("task_status");

                Task<String> task = new Task<>(taskName, taskCategory, taskDescription, taskStatus);
                addTask(task);
            }

            // Close the connection
            conn.close();
        } catch (SQLException ex) {
            System.out.println("Exception occurred: " + ex.getMessage());
        }
    }

    public void writeTasksToFile() {
        try {
            // Create a file output stream
            FileOutputStream fileOut = new FileOutputStream("../databaseData.txt");

            // Create a header for the table
            String header = String.format("%-45s %-40s %-120s %-20s\n", "Task Name", "Task Category", "Task Description", "Task Status");
            fileOut.write(header.getBytes());

            // Write each task to the file
            for (Task<String> task : tasks) {
                String row = String.format("%-45s %-40s %-120s %-20s\n", task.getTaskName(), task.getTaskCategory(), task.getTaskDescription(), task.getTaskStatus());
                fileOut.write(row.getBytes());
            }

            // Close the file output stream
            fileOut.close();
        } catch (IOException ex) {
            System.out.println("Exception occurred: " + ex.getMessage());
        }
    }
    
    public void displayTasks(JTextArea textArea, JMenuItem menuItem) {
         // Creating an ActionListener for display_FileInfo
    menuItem.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        FileInputStream inputStream = null;
        try {
            File database_txtfile = new File("../databaseData.txt");
            inputStream = new FileInputStream(database_txtfile);
            byte[] bytes = new byte[(int) database_txtfile.length()];
            inputStream.read(bytes);
            String content = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        
            textArea.setText(content);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
         } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
});
    }
    }


public final class Task_Manager_GUI extends javax.swing.JFrame {
    CardLayout cardLayout;
    
    //creating public variables for Connection and statement
    public Statement statement;
    
    //private variables for username, url and pwd
    
    private String url = "jdbc:mysql://localhost:3306/taskmanager";
    private String username = "root";
    private String pwd = "egMT5@me";
    private String sql;
    
    java.sql.Connection conn;
    
    
    
    public Task_Manager_GUI() { 
        initComponents();
        
         //try-catch to create connection
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url,username, pwd);//establish connection
            statement = conn.createStatement();
           
            System.out.print("Connected to DB!!");
        }catch(ClassNotFoundException | SQLException e){
            JOptionPane.showMessageDialog(null, e);//printing message to dialog box
            System.out.print(e.getMessage());
        }    
        
        
        Component [] components = this.getContentPane().getComponents();
        for(Component component: components){
        
            if(component instanceof JPanel){
                
                ((JButton) component).setUI(new BasicButtonUI());
                ((JButton) component).setFocusPainted(false);
            }
        }
         cardLayout = (CardLayout) (rightPane_CardLayout.getLayout());
    }

    
    //inner class to set background image on Panel
    static class BackgroundPanel extends JPanel {
        private BufferedImage backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = ImageIO.read(new File(imagePath));
            } catch (IOException e) {
                System.err.println("Error loading background image: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
            }
        }
    }

    //creating local variable
    JMenuItem display_FileInfo;
    
    //creating local variable to store temporary data
    int sql_TaskID;
    String sql_TaskName;
    
   
       
    //creating a style Method for Gui
    public void Task_Styling(Task_Manager_GUI frmTaskGui){
        
        //calling table method
        setDatabase_tableData(sqlTableData);
        
        //setting title
        frmTaskGui.setTitle("Task Manager");
        
        //Background of the Search bar
        frmTaskGui.SearchBar.setBackground(new Color(255,255,255,255));
        frmTaskGui.UIE_SearchBar.setBackground(new Color(255,255,255,255));
        
        //Background color for button
        frmTaskGui.btnUpdateTask.setBackground(new Color(60,63,65,255));
        frmTaskGui.btnFilter.setBackground(new Color(60,63,65,255));
        frmTaskGui.btnInsertTask.setBackground(new Color(60,63,65,255));
        frmTaskGui.btnClearAll.setBackground(new Color(60,63,65,255));
        frmTaskGui.btnSave_asTxtFile.setBackground(new Color(60,63,65,255));
        frmTaskGui.btnRefresh.setBackground(new Color(60,63,65,255));
        
        
        // -------------------- SETTING BACKGROUND COLOR OF TABBED PANES -----------------------//
        //setting the background color of Insert right pane
        frmTaskGui.DashBoard_Panel.setBackground(new Color(242,242,242,255));
        frmTaskGui.DashBoard_Panel.setOpaque(true);
        
        //setting the background color of Update right pane
        frmTaskGui.UIElements_Panel.setBackground(new Color(242,242,242,255));
        frmTaskGui.UIElements_Panel.setOpaque(true);
        
        //setting the background color of Save and read text file right pane
        frmTaskGui.FileSettings_Panel.setBackground(new Color(242,242,242,255));
        frmTaskGui.FileSettings_Panel.setOpaque(true);
        
     
        
        //setting the background color of file settings > dataDisplay_textArea right pane
        frmTaskGui.displayFile_data.setBackground(new Color(242,242,242,255));
        frmTaskGui.displayFile_data.setOpaque(true);
        frmTaskGui.displayFile_data.setBorder(BorderFactory.createLineBorder(new Color(242,242,242,255), 10));//removes the border
        
        
        //setting the background color of left pane
        frmTaskGui.leftPane.setBackground(new Color(60,63,65,255));
        
       
        
        //setting the background color for panel 1, 2, 3, 4, 5
        frmTaskGui.btnDashBoard.setBackground(new Color(60,63,65,255));
        frmTaskGui.btnDashBoard.setOpaque(true);

        frmTaskGui.btnUIElements.setBackground(new Color(60,63,65,255));
        frmTaskGui.btnUIElements.setOpaque(true);
        
        frmTaskGui.btnFile_Settings.setBackground(new Color(60,63,65,255));
        frmTaskGui.btnFile_Settings.setOpaque(true);
        
  
        
        // --------------- HOVER EFFECTS ON PANELS ------------------//
        //creating hover effect for Insert Panel
        btnDashBoard.addMouseListener(new MouseAdapter(){
            //when cursor enters label
             @Override
             public void mouseEntered(MouseEvent e) {
            // Change appearance on hover (e.g., set background color)
//              btnDashBoard.setBackground(new Color(0,0,0,60));
        }

            @Override
            public void mouseExited(MouseEvent e) {
                // Revert to default appearance
//                  btnDashBoard.setBackground(new Color(60,63,65,255));
            }
        });
        
        //creating hover effect for Update Panel
        btnUIElements.addMouseListener(new MouseAdapter(){
            //when cursor enters label
             @Override
             public void mouseEntered(MouseEvent e) {
//            // Change appearance on hover (e.g., set background color)
//            btnUIElements.setBackground(new Color(0,0,0,60));
        }

            @Override
            public void mouseExited(MouseEvent e) {
                // Revert to default appearance
//                btnUIElements.setBackground(new Color(60,63,65,255));
            }
        });
        
        //creating hover effect for Save and Read File Panel
        btnFile_Settings.addMouseListener(new MouseAdapter(){
            //when cursor enters label
             @Override
             public void mouseEntered(MouseEvent e) {
            // Change appearance on hover (e.g., set background color)
//              btnFile_Settings.setBackground(new Color(0,0,0,60));
        }

            @Override
            public void mouseExited(MouseEvent e) {
                // Revert to default appearance
//                  btnFile_Settings.setBackground(new Color(60,63,65,255));
            }
        });
        
        
    
        
        
    // -------------------------- CLICK EVENT FOR UIElement TABLE -------------- //
    sqlTableData_UIE.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    setupTableClickHandler(sqlTableData_UIE);
                    retrieve_ID_data();
                    
                    setting_GUI_Elements();
                } catch (SQLException ex) {
                    Logger.getLogger(Task_Manager_GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
    // --------------------------- CLICK EVENT FOR REFRESH --------------------- //
    frmTaskGui.btnRefresh.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                setDatabase_tableData(sqlTableData_UIE);
            }
    }); 
        
        
    // --------------------------- FILE PROPERTIES ------------------------------ //
    //INSTANTIATING THE MENU BAR
    JMenuBar menubar = new JMenuBar();
    menubar.setSize(1300, 35);
    menubar.setBackground(new Color(242,242,242,255));
    menubar.setOpaque(true);
    
    //creating an instance of Menu
    JMenu fileMenu = new JMenu("File");
    JMenu export = new JMenu("Export");
  
    //adding menu items to fileMenu
    display_FileInfo = new JMenuItem("File Info"); 
    JMenuItem file_Properties = new JMenuItem("File Properties"); 
    
    //adding menuitem to Export
    JMenuItem csv = new JMenuItem("CSV"); 
    
    //add display_FileInfo menuItem to fileMENU
    fileMenu.add(display_FileInfo);
    fileMenu.add(file_Properties);
    
    //adding menuItem to export file menu
    export.add(csv);
    
    //adding file menu to the menubar
    menubar.add(fileMenu);
    menubar.add(export);
    
    
    //adding menu bar to the container
    menuBar_Container.add(menubar);
    
    // --------------------------- FILE INFO Event LISTENER ------------------------------ //
    // Creating an ActionListener for display_FileInfo
    display_FileInfo.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        TaskManager taskManager = new TaskManager();
        taskManager.displayTasks(frmTaskGui.displayFile_data,display_FileInfo);
        
    }
});


    // --------------------------- FILE PROPERTIES Event LISTENER ------------------------------ //
    // Creating an ActionListener for file_Properties
    file_Properties.addActionListener(new ActionListener(){
    @Override
    public void actionPerformed(ActionEvent e) {
        
        //obtaining the file path
        Path path = Paths.get("../databaseData.txt");
        try {
            BasicFileAttributes file_att = Files.readAttributes(path, BasicFileAttributes.class);
            
            //for the creation time
            long creationTime_Milli = file_att.creationTime().toMillis();
            
            //DateTimeFormater for time
            SimpleDateFormat time_formater = new SimpleDateFormat("HH:mm:ss");
            
            //DateTimeFormater for date
            SimpleDateFormat date_formater = new SimpleDateFormat("yyyy-MM-dd");
            
            //creating string variable for creation time
            String creation_time = time_formater.format(new Date(creationTime_Milli));
            
            //creating string variable for creation date
            String creation_date = date_formater.format(new Date(creationTime_Milli));
            
            String properties = "File Properties: " + "\n \n" + "File name: " + path.getFileName().toString() + "\n" + "File size: " + Files.size(path) + "bytes" 
                    +"\n"+ "File Location: " + Files.getFileStore(path) + "\n" + "Created Time: " + creation_time + "\n" + "Creation Date: " + creation_date ;
            //displaying to the textarear
            frmTaskGui.displayFile_data.setText(properties);
            
        } catch (IOException ex) {
            Logger.getLogger(Task_Manager_GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    });
    
    
    // -------------------------- EXPORTING FILE ------------------------------------------------ //
    //Creating an ActionListener for file_Properties
   csv.addActionListener(new ActionListener(){
    @Override
    public void actionPerformed(ActionEvent e) {
        //variable for txt file
        String txtFile = "../databaseData.txt";
        
        //variable for csv file
        String csvFile = "../databaseData.csv";
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(txtFile));
             BufferedWriter writer = Files.newBufferedWriter(Paths.get(csvFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into individual values using a delimiter
                String[] values = line.split(",");

                // Write the values to the CSV file
                for (int i = 0; i < values.length; i++) {
                    writer.write(values[i]);
                    if (i < values.length - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }
            JOptionPane.showMessageDialog(null, "File Exported as (.csv)");//printing message to dialog box
        } catch (IOException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
        
    }   
});
   
   // -------------------------- RADIO BUTTON SELECTION  ------------------------------------------------ //
   //creating an instance of class List__GUI
    if(radNo.isSelected()){
        btnFilter.setEnabled(false);//deativated filter button
    }else{
        btnFilter.setEnabled(true);//deativated filter button
    }
    
    // radio button logic
     if(radNo.isSelected()){
       radYes.setSelected(false); 
    }else{
        if(radYes.isSelected()){
       radNo.setSelected(false);
        }
    }
    

} 
    
    //creating a method for the cards to display images
    public void card_background(JPanel card, String file_name){
         
        String file_path = String.format("./src/icon/%s", file_name);
        Task_Manager_GUI.BackgroundPanel bc = new Task_Manager_GUI.BackgroundPanel(file_path);
        
        // Set the layout manager of the existing panel to BorderLayout
        card.setLayout(new BorderLayout());

        // Add the BackgroundPanel instance to the existing panel
        card.add(bc, BorderLayout.CENTER);
        bc.setPreferredSize(new Dimension(359,194));
    }
    
    //creating a method to check what button is clicked
    public void isBtnClicked(String Panel_name){
        //Conditional statement to determine the panel 
        if(Panel_name == "UIElements_Panel"){
            DashBoard_Panel.setVisible(false);//setting dashboard to false
            FileSettings_Panel.setVisible(false);//setting dashboard to false
            UIElements_Panel.setVisible(true);//setting UIElements to true
            cardLayout.show(rightPane_CardLayout, "UIElements_Panel");
        }else if(Panel_name == "FileSettings_Panel"){
            DashBoard_Panel.setVisible(false);//setting dashboard to false
            UIElements_Panel.setVisible(false);//setting UIElements to false
            FileSettings_Panel.setVisible(true);//setting FileSettings to true
            cardLayout.show(rightPane_CardLayout, "FileSettings_Panel");
        }else if(Panel_name == "TaskReports_Panel"){
            DashBoard_Panel.setVisible(false);//setting dashboard to false
            UIElements_Panel.setVisible(false);//setting UIElements to false
            FileSettings_Panel.setVisible(false);//setting File Settings to false
            cardLayout.show(rightPane_CardLayout, "TaskReports_Panel");
        }else if(Panel_name == "ChartData_Panel") {
            DashBoard_Panel.setVisible(false);//setting dashboard to false
            UIElements_Panel.setVisible(false);//setting UIElements to false
            FileSettings_Panel.setVisible(false);//setting File Settings to false
            cardLayout.show(rightPane_CardLayout, "ChartData_Panel");
        }else{
            UIElements_Panel.setVisible(false);//setting UIElements to false
            FileSettings_Panel.setVisible(false);//setting File Settings to false
            DashBoard_Panel.setVisible(true);//setting dashboard to true
            cardLayout.show(rightPane_CardLayout, "DashBoard_Panel");
        }
        
    }
    
    // Custom cell renderer class
    class CustomCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        // Center align the text
        setHorizontalAlignment(JLabel.CENTER);
        
        // Set blue color
        setForeground(Color.WHITE);
        
         // Add border around the text with increased top border thickness
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 5),
            new MatteBorder(new Insets(0, 50, 0, 50), Color.WHITE)
        ));
        
        // Set the preferred size of the component
        setPreferredSize(new Dimension(200, 110)); // adjust the width and height as needed
        
        // Check the value of the last column and change background color accordingly
        if (value.toString().equals("IN PROCESS")) {
            setBackground(new Color(182,117,245,255));
        } else if (value.toString().equals("COMPLETED")) {
            setBackground(new Color(232,201,62,255));
        } else {
            setBackground(new Color(135,135,248,255));
        }
        
        return this;
    }
}
    
    //creating method to display databse data in table
    public void setDatabase_tableData(Table table) {
        sql = "SELECT * FROM task_information";
        
        try{
            PreparedStatement preparedStmt = conn.prepareStatement(sql);
            ResultSet rs = preparedStmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            
            //setting row count
            model.setRowCount(0);
            while(rs.next()){
                model.addRow(new String[]{rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});
            }
            
           // Center align the last column, add border and blue color
            table.getColumnModel().getColumn(3).setCellRenderer(new CustomCellRenderer());
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
    }
    
    
    // ------------------- METHOD FOR TABLE SELECTION -------------------//
     public void setupTableClickHandler(JTable table) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // Check for single click
                    int selectedRow = table.getSelectedRow();
                    String task_value = (String) table.getValueAt(selectedRow, 0);

                    // Store the selected value only if it's different
                    if (!task_value.equals(sql_TaskName)) {
                        sql_TaskName = task_value;
                        System.out.println("Selected value: " + task_value);
                    }
                }
            }
        });
    }
      
    // ------------------------- CREATING A METHODS FOR FILTERED SEARCH  --------------//

     //creating method for getting selected status
       private String getSelectedStatus() {
        if (radInProcess.isSelected()) {
            return "IN PROCESS";
        } else if (rad_OnHold.isSelected()) {
            return "ON HOLD";
        } else if (radCompleted.isSelected()) {
            return "COMPLETED";
        } else {
            return "";
        }
    }
       
       
    // ------------------------- METHOD FOR INSERTING DATA TO DATABASE ---------//
    public void insert_Data(){
        
        try{
           
         sql = "INSERT INTO task_information (task_name, task_category, task_description, task_status) VALUES (?, ?, ?, ?)";
          PreparedStatement preparedStatement  = conn.prepareStatement(sql) ;
         
            // Set parameters using user input
            preparedStatement.setString(1, txtTaskName.getText());
            preparedStatement.setString(2, txtCategory.getText()); 
            preparedStatement.setString(3, txtDescription.getText());
            preparedStatement.setString(4,getSelectedStatus());
            
            // Execute the query
            preparedStatement.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Data Successfully added to database.");
            System.out.print("Data Successfully added to database.");
       }catch(SQLException e){
           JOptionPane.showMessageDialog(null,"Database Error data could not be inserted!");
           System.out.print("Database Error data could not be inserted!");
           JOptionPane.showMessageDialog(null,e.getMessage());
       }
    }
    
    // -------------------------- METHOD TO CHECK IF TEXTBOXES IS EMPTY OR CONTAINS A NUMBER ----------------- //
    public void isNum_isEmpty(String comp_text1, String comp_text2, String comp_text3){
        // ------------------------- SWITCH CASE TO CHECK CONDITIONS---------------------//
            // Check if the text is a number
            if (comp_text1.matches("-?\\d+(\\.\\d+)?") || comp_text2.matches("-?\\d+(\\.\\d+)?") || comp_text3.matches("-?\\d+(\\.\\d+)?") ) {
                JOptionPane.showMessageDialog(null, "A Text field or text Area contains a number!");
            } 
            // Check if the text is empty
            else if (comp_text1.trim().length() == 0 || comp_text2.trim().length() == 0 || comp_text3.trim().length() == 0) {
                JOptionPane.showMessageDialog(null, "A Text field or text Area is empty!");
            } 
            else {
                System.out.println("Text is valid.");
               //after all the above the following will be executed:
                update_Data();//allow the data to be updated
            }
    };
    
     public void isNum_isEmpty_insert(String comp_text1, String comp_text2, String comp_text3){
        // ------------------------- SWITCH CASE TO CHECK CONDITIONS---------------------//
            // Check if the text is a number
            if (comp_text1.matches("-?\\d+(\\.\\d+)?") || comp_text2.matches("-?\\d+(\\.\\d+)?") || comp_text3.matches("-?\\d+(\\.\\d+)?") ) {
                JOptionPane.showMessageDialog(null, "A Text field or text Area contains a number!");
            } 
            // Check if the text is empty
            else if (comp_text1.trim().length() == 0 || comp_text2.trim().length() == 0 || comp_text3.trim().length() == 0) {
                JOptionPane.showMessageDialog(null, "A Text field or text Area is empty!");
            } 
            else {
                System.out.println("Text is valid.");
               //after all the above the following will be executed:
                insert_Data();//allow the data to be updated
            }
    };
    
     // ------------------------- METHOD FOR UPDATING DATA TO DATABASE ---------//
    public void update_Data(){
    try{
        sql = "UPDATE task_information SET task_name = ?, task_category = ?, task_description = ?, task_status = ? WHERE task_id = ?";
        PreparedStatement preparedStatement  = conn.prepareStatement(sql) ;
        
        // Set parameters using user input
        preparedStatement.setString(1, txtTaskName.getText());
        preparedStatement.setString(2, txtCategory.getText()); 
        preparedStatement.setString(3, txtDescription.getText());
        preparedStatement.setString(4, getSelectedStatus());
        preparedStatement.setInt(5, sql_TaskID); // converted string to int
        
        // Execute the query
        preparedStatement.executeUpdate();
        
        JOptionPane.showMessageDialog(null, "Data Successfully updated in database.");
        System.out.print("Data Successfully updated in database.");
    }catch(SQLException e){
        JOptionPane.showMessageDialog(null,"Database Error data could not be updated!");
        System.out.print("Database Error data could not be updated!");
    }
}
    
    // ------------------------- METHOD FOR RETRIEVE SELECTED DATA FROM TABLE ---------//
    public void retrieve_ID_data() throws SQLException{
        int selectedRow = sqlTableData_UIE.getSelectedRow();
        if (selectedRow >= 0) {
            try{
                 sql = "SELECT task_id FROM task_information WHERE task_name = ?";
                 PreparedStatement preparedStatement  = conn.prepareStatement(sql) ;
   
                  preparedStatement.setString(1, sql_TaskName);
                  
                //Executing query
                ResultSet result = preparedStatement.executeQuery();
                
                if (result.next()) {
                    sql_TaskID = result.getInt("task_id");
                    System.out.println("Selected ID: " + sql_TaskID);
                }

            } catch (SQLException ex) {
                System.err.println("Error retrieving ID: " + ex.getMessage());
            }
        }
     
     
    }
    
    // ------------------------ Fillinging GUI Elements based on ID ------------------ //
    public void setting_GUI_Elements(){
         int selectedRow = sqlTableData_UIE.getSelectedRow();
        if (selectedRow >= 0) {
            try{
                 sql = "SELECT task_name, task_category, task_description, task_status FROM task_information WHERE task_id = ?";
                 PreparedStatement preparedStatement  = conn.prepareStatement(sql) ;
   
                  preparedStatement.setInt(1, sql_TaskID);
                  
                //Executing query
                ResultSet result = preparedStatement.executeQuery();
            
                
                if (result.next()) {
                    
                    //setting data to elements
                    String task_name = result.getString("task_name");
                    String task_category = result.getString("task_category");
                    String task_description = result.getString("task_description");
                    String task_status = result.getString("task_status");
                    
                    
                    //setting the value for txtTaskName
                    txtTaskName.setText(task_name);
                    //setting the value for txtCategory
                    txtCategory.setText(task_category);
                    //setting the value for txtDescription
                    txtDescription.setText(task_description);
                    
                   
                    //if statement to determine radio button
                    if(task_status.equals("IN PROCESS")){//when equal to required text radio button is selected to chosen one
                        radInProcess.setSelected(true);//set radio button inprocess to true
                        radCompleted.setSelected(false);//set radio button inprocess to false
                        rad_OnHold.setSelected(false);//set radio button inprocess to false
                    }else if(task_status.equals("COMPLETED")){
                        radCompleted.setSelected(true);//set radio button inprocess to true
                        radInProcess.setSelected(false);//set radio button inprocess to false
                        rad_OnHold.setSelected(false);//set radio button inprocess to false
                    }else{
                       rad_OnHold.setSelected(true);//set radio button inprocess to true
                       radCompleted.setSelected(false);//set radio button inprocess to false
                       radInProcess.setSelected(false);//set radio button inprocess to false
                    }
                    
                    System.out.println("Selected data added to GUI elements.");
                    
                }

            } catch (SQLException ex) {
                System.err.println("Error setting data to gui elements.: " + ex.getMessage());
            }
        }
    }
      
         // ---------------------- METHOD FOR SINGLE ENTRY SEARCH ----------------------- //
        public void single_search(){
        
             try {
            //creating an instance of class List__GUI
            Task_Manager_GUI frmTaskGui = new Task_Manager_GUI();

            DefaultTableModel model = (DefaultTableModel) sqlTableData_UIE.getModel();

            // Get the search input from the user
            String search_value = txtSearch_TaskData.getText();

            //Establishing JDBC Connection
            java.sql.Connection conn = DriverManager.getConnection(url, username, pwd);

            //preparing SQL query
            String query = "SELECT * FROM task_information WHERE task_name = ? or task_category = ? or task_status = ?";

            // Prepare the statement
            PreparedStatement preparedStatement = conn.prepareStatement(query);

            // Set the parameters
            preparedStatement.setString(1, search_value);
            preparedStatement.setString(2, search_value);
            preparedStatement.setString(3, search_value);

            //Executing query
            ResultSet rs = preparedStatement.executeQuery();

            //setting row count
            model.setRowCount(0);
            while(rs.next()){
                String[] taskData = new String[]{rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)};
                model.addRow(taskData);
            }

            frmTaskGui.sqlTableData_UIE.getColumnModel().getColumn(3).setCellRenderer(new CustomCellRenderer());

            //closing resources
            rs.close();
            preparedStatement.close();
            conn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Can't be Filtered!");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        
        }
        
        // ------------------------------- METHOD TO SEARCH FOR TASK MANAGER STUFF ---------------- //
        public void taskManager_searchSystem(JTextField textField){
            String text = textField.getText();

            if(text.equalsIgnoreCase("Crud Operations") || text.equalsIgnoreCase("Database Operations") || text.equalsIgnoreCase("UI Elements") || text.equalsIgnoreCase("Save As") ){
                total_status();
                setDatabase_tableData(sqlTableData_UIE);
                
                DashBoard_Panel.setVisible(false);//setting dashboard to false
                FileSettings_Panel.setVisible(false);//setting dashboard to false
                UIElements_Panel.setVisible(true);//setting UIElements to true
                cardLayout.show(rightPane_CardLayout, "UIElements_Panel");
                
                //setting textbox to empty
                textField.setText("");

            }else if(text.equalsIgnoreCase("Dash board") || text.equalsIgnoreCase("Dashboard")){
                UIElements_Panel.setVisible(false);//setting UIElements to false
                FileSettings_Panel.setVisible(false);//setting File Settings to false
                DashBoard_Panel.setVisible(true);//setting dashboard to true
                cardLayout.show(rightPane_CardLayout, "DashBoard_Panel");
                
                //setting textbox to empty
                textField.setText("");

            }else if(text.equalsIgnoreCase("File Settings") || text.equalsIgnoreCase("File Properties") || text.equalsIgnoreCase("Export") || text.equalsIgnoreCase("File Information") 
                    || text.equalsIgnoreCase("CSV")){
                DashBoard_Panel.setVisible(false);//setting dashboard to false
                UIElements_Panel.setVisible(false);//setting UIElements to false
                FileSettings_Panel.setVisible(true);//setting FileSettings to true
                cardLayout.show(rightPane_CardLayout, "FileSettings_Panel");
                
                //setting textbox to empty
                textField.setText("");

            }else{
                JOptionPane.showMessageDialog(null,"Invalid search request!");
            }
        }
        
        
        // ------------------------------ METHOD TO DETERMINE THE TOTAL NUMBER OF STATUS----------- //
        public void total_status(){
            
            // Initialize counter variables
            int counterComplete = 0;
            int counterOnHold = 0;
            int counterInProcess = 0;
            
             try {
                // Connect to the database
                java.sql.Connection conn = DriverManager.getConnection(url, username, pwd);

                // Create a statement to query the database
                Statement stmt = conn.createStatement();

                // Query the task_information table
                ResultSet results = stmt.executeQuery("SELECT task_status FROM task_information");

                // Iterate over the results and update the counter variables
                while (results.next()) {
                    String taskStatus = results.getString("task_status");
                    if (taskStatus.equalsIgnoreCase("ON HOLD")) {
                        counterOnHold++;
                    } else if (taskStatus.equalsIgnoreCase("COMPLETED")) {
                        counterComplete++;
                    } else if (taskStatus.equalsIgnoreCase("IN PROCESS")) {
                        counterInProcess++;
                    }
                }

                // Print the results
                lblOnHoldCounter.setText(Integer.toString(counterOnHold));//display counter value
                lblCompletedCounter.setText(Integer.toString(counterComplete));//display counter value
                lblProgressCounter.setText(Integer.toString(counterInProcess));//display counter value

                // Close the ResultSet and Statement objects
                results.close();
                stmt.close();

                // Close the database connection
                conn.close();
            } catch (SQLException e) {
                System.out.println("Error connecting to database: " + e.getMessage());
            }
        }
        
        // ------------------------------ METHOD TO DETERMINE THE TOTAL NUMBER OF STATUS----------- //
        public void percentage_status(){
            
            // Initialize counter variables
            int counterComplete = 0;
            int counterOnHold = 0;
            int counterInProcess = 0;
            
            //total of all status
            int totalStatus = 0;
            
            // Initialize percantage variables
            double percentageComplete = 0.0;
            double percentageOnHold = 0.0;
            double percentageInProcess = 0.0;
            
             try {
                // Connect to the database
                java.sql.Connection conn = DriverManager.getConnection(url, username, pwd);

                // Create a statement to query the database
                Statement stmt = conn.createStatement();

                // Query the task_information table
                ResultSet results = stmt.executeQuery("SELECT task_status FROM task_information");

                // Iterate over the results and update the counter variables
                while (results.next()) {
                    String taskStatus = results.getString("task_status");
                    if (taskStatus.equalsIgnoreCase("ON HOLD")) {
                        counterOnHold++;
                    } else if (taskStatus.equalsIgnoreCase("COMPLETED")) {
                        counterComplete++;
                    } else if (taskStatus.equalsIgnoreCase("IN PROCESS")) {
                        counterInProcess++;
                    }
                    totalStatus++; // increment total status count
                }
                
                // Calculate percentages
                if (totalStatus > 0) {
                    percentageComplete = ((double) counterComplete / totalStatus) * 100;
                    percentageOnHold = ((double) counterOnHold / totalStatus) * 100;
                    percentageInProcess = ((double) counterInProcess / totalStatus) * 100;
                }

                // Display percentages
                lblHold_percent.setText(String.format("%.2f%%", percentageOnHold));
                lblComp_percent.setText(String.format("%.2f%%", percentageComplete));
                lblIn_Process_per.setText(String.format("%.2f%%", percentageInProcess));


                // Close the ResultSet and Statement objects
                results.close();
                stmt.close();

                // Close the database connection
                conn.close();
            } catch (SQLException e) {
                System.out.println("Error connecting to database: " + e.getMessage());
            }
        }
        
   

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        SplitPane_ForCards = new javax.swing.JSplitPane();
        leftPane = new javax.swing.JPanel();
        lblTask = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnUIElements = new javax.swing.JButton();
        btnDashBoard = new javax.swing.JButton();
        btnFile_Settings = new javax.swing.JButton();
        rightPane_CardLayout = new javax.swing.JPanel();
        DashBoard_Panel = new javax.swing.JPanel();
        SearchBar = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtSeachElement = new javax.swing.JTextField();
        card2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        lblIn_Process_per = new javax.swing.JLabel();
        lblProgressCounter = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        card1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        lblHold_percent = new javax.swing.JLabel();
        lblOnHoldCounter = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        card3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblCompletedCounter = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblComp_percent = new javax.swing.JLabel();
        panelBorder1 = new com.emmanual.swing.PanelBorder();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        sqlTableData = new com.emmanual.swing.Table();
        UIElements_Panel = new javax.swing.JPanel();
        UIE_SearchBar = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtUIE_Search = new javax.swing.JTextField();
        panelBorder2 = new com.emmanual.swing.PanelBorder();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        sqlTableData_UIE = new com.emmanual.swing.Table();
        jLabel19 = new javax.swing.JLabel();
        txtSearch_TaskData = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        btnSave_asTxtFile = new javax.swing.JButton();
        btnRefresh = new javax.swing.JLabel();
        panelBorder3 = new com.emmanual.swing.PanelBorder();
        radYes = new javax.swing.JRadioButton();
        jLabel17 = new javax.swing.JLabel();
        rad_OnHold = new javax.swing.JRadioButton();
        btnUpdateTask = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jSeparator6 = new javax.swing.JSeparator();
        txtTaskName = new javax.swing.JTextField();
        txtCategory = new javax.swing.JTextField();
        radCompleted = new javax.swing.JRadioButton();
        btnFilter = new javax.swing.JButton();
        btnClearAll = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        radInProcess = new javax.swing.JRadioButton();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        radNo = new javax.swing.JRadioButton();
        btnInsertTask = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        FileSettings_Panel = new javax.swing.JPanel();
        menuBar_Container = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        displayFile_data = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        SplitPane_ForCards.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        SplitPane_ForCards.setDividerSize(1);

        lblTask.setFont(new java.awt.Font("Bookman Old Style", 1, 24)); // NOI18N
        lblTask.setForeground(new java.awt.Color(204, 204, 204));
        lblTask.setText("taskify");

        jSeparator1.setBackground(new java.awt.Color(255, 255, 255));

        btnUIElements.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnUIElements.setForeground(new java.awt.Color(204, 204, 204));
        btnUIElements.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/2.png"))); // NOI18N
        btnUIElements.setText("         UI Elements");
        btnUIElements.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnUIElements.setBorderPainted(false);
        btnUIElements.setContentAreaFilled(false);
        btnUIElements.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUIElements.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        btnUIElements.setFocusPainted(false);
        btnUIElements.setSelected(true);
        btnUIElements.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUIElementsActionPerformed(evt);
            }
        });

        btnDashBoard.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnDashBoard.setForeground(new java.awt.Color(204, 204, 204));
        btnDashBoard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/1.png"))); // NOI18N
        btnDashBoard.setText("         Dash Board");
        btnDashBoard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnDashBoard.setBorderPainted(false);
        btnDashBoard.setContentAreaFilled(false);
        btnDashBoard.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDashBoard.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        btnDashBoard.setFocusPainted(false);
        btnDashBoard.setSelected(true);
        btnDashBoard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDashBoardActionPerformed(evt);
            }
        });

        btnFile_Settings.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnFile_Settings.setForeground(new java.awt.Color(204, 204, 204));
        btnFile_Settings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/9.png"))); // NOI18N
        btnFile_Settings.setText("        File Settings");
        btnFile_Settings.setToolTipText("");
        btnFile_Settings.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnFile_Settings.setBorderPainted(false);
        btnFile_Settings.setContentAreaFilled(false);
        btnFile_Settings.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFile_Settings.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        btnFile_Settings.setFocusPainted(false);
        btnFile_Settings.setSelected(true);
        btnFile_Settings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFile_SettingsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout leftPaneLayout = new javax.swing.GroupLayout(leftPane);
        leftPane.setLayout(leftPaneLayout);
        leftPaneLayout.setHorizontalGroup(
            leftPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnUIElements, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnFile_Settings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnDashBoard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(leftPaneLayout.createSequentialGroup()
                .addGroup(leftPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(leftPaneLayout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(leftPaneLayout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(lblTask)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        leftPaneLayout.setVerticalGroup(
            leftPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPaneLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(lblTask)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addComponent(btnDashBoard, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnUIElements, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(btnFile_Settings, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(734, Short.MAX_VALUE))
        );

        SplitPane_ForCards.setLeftComponent(leftPane);

        rightPane_CardLayout.setLayout(new java.awt.CardLayout());

        DashBoard_Panel.setForeground(new java.awt.Color(204, 204, 204));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/search.png"))); // NOI18N
        jLabel1.setText("  Search Here..");

        txtSeachElement.setBackground(new java.awt.Color(255, 255, 255));
        txtSeachElement.setBorder(null);
        txtSeachElement.setSelectionColor(new java.awt.Color(255, 255, 255));
        txtSeachElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSeachElementActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout SearchBarLayout = new javax.swing.GroupLayout(SearchBar);
        SearchBar.setLayout(SearchBarLayout);
        SearchBarLayout.setHorizontalGroup(
            SearchBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SearchBarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtSeachElement))
        );
        SearchBarLayout.setVerticalGroup(
            SearchBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SearchBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtSeachElement, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Increased by");

        lblIn_Process_per.setForeground(new java.awt.Color(255, 255, 255));
        lblIn_Process_per.setText("0%");

        lblProgressCounter.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        lblProgressCounter.setForeground(new java.awt.Color(255, 255, 255));
        lblProgressCounter.setText("0");

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Total Tasks In Progress:");

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/check.png"))); // NOI18N

        javax.swing.GroupLayout card2Layout = new javax.swing.GroupLayout(card2);
        card2.setLayout(card2Layout);
        card2Layout.setHorizontalGroup(
            card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(card2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(card2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblIn_Process_per, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(lblProgressCounter, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(156, Short.MAX_VALUE))
        );
        card2Layout.setVerticalGroup(
            card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(card2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblProgressCounter)
                .addGap(47, 47, 47)
                .addGroup(card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblIn_Process_per))
                .addContainerGap(47, Short.MAX_VALUE))
        );

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Increased by");

        lblHold_percent.setForeground(new java.awt.Color(255, 255, 255));
        lblHold_percent.setText("0%");

        lblOnHoldCounter.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        lblOnHoldCounter.setForeground(new java.awt.Color(255, 255, 255));
        lblOnHoldCounter.setText("0");

        jLabel9.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Total Tasks On Hold:");

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/pause.png"))); // NOI18N

        javax.swing.GroupLayout card1Layout = new javax.swing.GroupLayout(card1);
        card1.setLayout(card1Layout);
        card1Layout.setHorizontalGroup(
            card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(card1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addGroup(card1Layout.createSequentialGroup()
                        .addGroup(card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblOnHoldCounter, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblHold_percent, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(143, Short.MAX_VALUE))
        );
        card1Layout.setVerticalGroup(
            card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(card1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblOnHoldCounter)
                .addGap(47, 47, 47)
                .addGroup(card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lblHold_percent))
                .addContainerGap(47, Short.MAX_VALUE))
        );

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/checking.png"))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Total Tasks Completed:");

        lblCompletedCounter.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        lblCompletedCounter.setForeground(new java.awt.Color(255, 255, 255));
        lblCompletedCounter.setText("0");

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Increased by");

        lblComp_percent.setForeground(new java.awt.Color(255, 255, 255));
        lblComp_percent.setText("0%");

        javax.swing.GroupLayout card3Layout = new javax.swing.GroupLayout(card3);
        card3.setLayout(card3Layout);
        card3Layout.setHorizontalGroup(
            card3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(card3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(card3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(card3Layout.createSequentialGroup()
                        .addGroup(card3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(lblCompletedCounter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(card3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblComp_percent, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(195, 195, 195))))
        );
        card3Layout.setVerticalGroup(
            card3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(card3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblCompletedCounter)
                .addGap(47, 47, 47)
                .addGroup(card3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblComp_percent))
                .addContainerGap(47, Short.MAX_VALUE))
        );

        panelBorder1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel11.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel11.setText("Database Table Data");

        sqlTableData.setBackground(new java.awt.Color(255, 255, 255));
        sqlTableData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Task Name", "Category", "Description", "Task Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(sqlTableData);

        javax.swing.GroupLayout panelBorder1Layout = new javax.swing.GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1130, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelBorder1Layout.setVerticalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        javax.swing.GroupLayout DashBoard_PanelLayout = new javax.swing.GroupLayout(DashBoard_Panel);
        DashBoard_Panel.setLayout(DashBoard_PanelLayout);
        DashBoard_PanelLayout.setHorizontalGroup(
            DashBoard_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SearchBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(DashBoard_PanelLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(DashBoard_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelBorder1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DashBoard_PanelLayout.createSequentialGroup()
                        .addComponent(card1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54)
                        .addComponent(card2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45)
                        .addComponent(card3, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(100, Short.MAX_VALUE))
        );
        DashBoard_PanelLayout.setVerticalGroup(
            DashBoard_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DashBoard_PanelLayout.createSequentialGroup()
                .addComponent(SearchBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(93, 93, 93)
                .addGroup(DashBoard_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(card3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(63, 63, 63)
                .addComponent(panelBorder1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        rightPane_CardLayout.add(DashBoard_Panel, "card2");

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/search.png"))); // NOI18N
        jLabel13.setText("  Search Here..");

        txtUIE_Search.setBackground(new java.awt.Color(255, 255, 255));
        txtUIE_Search.setBorder(null);
        txtUIE_Search.setSelectionColor(new java.awt.Color(255, 255, 255));
        txtUIE_Search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUIE_SearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout UIE_SearchBarLayout = new javax.swing.GroupLayout(UIE_SearchBar);
        UIE_SearchBar.setLayout(UIE_SearchBarLayout);
        UIE_SearchBarLayout.setHorizontalGroup(
            UIE_SearchBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UIE_SearchBarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtUIE_Search))
        );
        UIE_SearchBarLayout.setVerticalGroup(
            UIE_SearchBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UIE_SearchBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtUIE_Search, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        panelBorder2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel12.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel12.setText("Database Table Data");

        sqlTableData_UIE.setBackground(new java.awt.Color(255, 255, 255));
        sqlTableData_UIE.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Task Name", "Category", "Description", "Task Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(sqlTableData_UIE);

        jLabel19.setText("Search Data Here...");

        txtSearch_TaskData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearch_TaskDataActionPerformed(evt);
            }
        });

        jLabel15.setBackground(new java.awt.Color(102, 102, 102));
        jLabel15.setForeground(new java.awt.Color(102, 102, 102));
        jLabel15.setText("ℹ️Please Note: Click on the cell you wish to update! ");

        btnSave_asTxtFile.setForeground(new java.awt.Color(255, 255, 255));
        btnSave_asTxtFile.setText("Save As");
        btnSave_asTxtFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSave_asTxtFileActionPerformed(evt);
            }
        });

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/refresh.png"))); // NOI18N
        btnRefresh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout panelBorder2Layout = new javax.swing.GroupLayout(panelBorder2);
        panelBorder2.setLayout(panelBorder2Layout);
        panelBorder2Layout.setHorizontalGroup(
            panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBorder2Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSave_asTxtFile, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19))
                    .addComponent(jScrollPane2)
                    .addGroup(panelBorder2Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 234, Short.MAX_VALUE)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSearch_TaskData, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(btnRefresh)))
                .addContainerGap())
        );
        panelBorder2Layout.setVerticalGroup(
            panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtSearch_TaskData, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel12)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15)
                    .addComponent(btnSave_asTxtFile))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        panelBorder3.setBackground(new java.awt.Color(255, 255, 255));

        radYes.setBackground(new java.awt.Color(255, 255, 255));
        radYes.setForeground(new java.awt.Color(102, 102, 102));
        radYes.setText("Yes");
        radYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radYesActionPerformed(evt);
            }
        });

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Bookman Old Style", 1, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(102, 102, 102));
        jLabel17.setText("Description:");

        rad_OnHold.setBackground(new java.awt.Color(255, 255, 255));
        rad_OnHold.setForeground(new java.awt.Color(102, 102, 102));
        rad_OnHold.setText("On Hold");
        rad_OnHold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rad_OnHoldActionPerformed(evt);
            }
        });

        btnUpdateTask.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdateTask.setText("Update Task");
        btnUpdateTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateTaskActionPerformed(evt);
            }
        });

        txtDescription.setBackground(new java.awt.Color(242, 242, 242));
        txtDescription.setColumns(20);
        txtDescription.setRows(5);
        jScrollPane5.setViewportView(txtDescription);

        jSeparator6.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);

        txtTaskName.setBackground(new java.awt.Color(242, 242, 242));

        txtCategory.setBackground(new java.awt.Color(242, 242, 242));

        radCompleted.setBackground(new java.awt.Color(255, 255, 255));
        radCompleted.setForeground(new java.awt.Color(102, 102, 102));
        radCompleted.setText("Completed");
        radCompleted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radCompletedActionPerformed(evt);
            }
        });

        btnFilter.setForeground(new java.awt.Color(255, 255, 255));
        btnFilter.setText("Filter Search");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

        btnClearAll.setForeground(new java.awt.Color(255, 255, 255));
        btnClearAll.setText("Clear All");
        btnClearAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearAllActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Bookman Old Style", 1, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(102, 102, 102));
        jLabel20.setText("Task Status:");

        radInProcess.setBackground(new java.awt.Color(255, 255, 255));
        radInProcess.setForeground(new java.awt.Color(102, 102, 102));
        radInProcess.setSelected(true);
        radInProcess.setText("In Process");
        radInProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radInProcessActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Bookman Old Style", 1, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(102, 102, 102));
        jLabel21.setText("Category: ");

        jLabel22.setFont(new java.awt.Font("Bookman Old Style", 1, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(102, 102, 102));
        jLabel22.setText("Filter search on this data ?");

        radNo.setBackground(new java.awt.Color(255, 255, 255));
        radNo.setForeground(new java.awt.Color(102, 102, 102));
        radNo.setSelected(true);
        radNo.setText("No");
        radNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radNoActionPerformed(evt);
            }
        });

        btnInsertTask.setForeground(new java.awt.Color(255, 255, 255));
        btnInsertTask.setText("Insert Task");
        btnInsertTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertTaskActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Bookman Old Style", 1, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(102, 102, 102));
        jLabel18.setText("Task Name: ");

        javax.swing.GroupLayout panelBorder3Layout = new javax.swing.GroupLayout(panelBorder3);
        panelBorder3.setLayout(panelBorder3Layout);
        panelBorder3Layout.setHorizontalGroup(
            panelBorder3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder3Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(panelBorder3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addGroup(panelBorder3Layout.createSequentialGroup()
                        .addGroup(panelBorder3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addComponent(jLabel21))
                        .addGap(41, 41, 41)
                        .addGroup(panelBorder3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCategory)
                            .addComponent(txtTaskName, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel20)
                    .addGroup(panelBorder3Layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addGroup(panelBorder3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(radInProcess)
                            .addComponent(rad_OnHold)
                            .addComponent(radCompleted)))
                    .addComponent(btnFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelBorder3Layout.createSequentialGroup()
                        .addComponent(radYes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(radNo)))
                .addGap(36, 36, 36)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelBorder3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addGroup(panelBorder3Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addGroup(panelBorder3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 496, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelBorder3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(btnClearAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(panelBorder3Layout.createSequentialGroup()
                                    .addComponent(btnUpdateTask, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnInsertTask, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(123, Short.MAX_VALUE))
        );
        panelBorder3Layout.setVerticalGroup(
            panelBorder3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelBorder3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTaskName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addGap(18, 18, 18)
                .addGroup(panelBorder3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addGap(18, 18, 18)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radInProcess)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rad_OnHold)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radCompleted)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelBorder3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radYes)
                    .addComponent(radNo))
                .addGap(18, 18, 18)
                .addComponent(btnFilter)
                .addGap(10, 10, 10))
            .addGroup(panelBorder3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBorder3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBorder3Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelBorder3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnUpdateTask)
                            .addComponent(btnInsertTask))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnClearAll)
                        .addContainerGap())
                    .addGroup(panelBorder3Layout.createSequentialGroup()
                        .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4))))
        );

        javax.swing.GroupLayout UIElements_PanelLayout = new javax.swing.GroupLayout(UIElements_Panel);
        UIElements_Panel.setLayout(UIElements_PanelLayout);
        UIElements_PanelLayout.setHorizontalGroup(
            UIElements_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(UIE_SearchBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, UIElements_PanelLayout.createSequentialGroup()
                .addContainerGap(163, Short.MAX_VALUE)
                .addGroup(UIElements_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelBorder3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelBorder2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42))
        );
        UIElements_PanelLayout.setVerticalGroup(
            UIElements_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UIElements_PanelLayout.createSequentialGroup()
                .addComponent(UIE_SearchBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panelBorder2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panelBorder3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
        );

        rightPane_CardLayout.add(UIElements_Panel, "card3");

        javax.swing.GroupLayout menuBar_ContainerLayout = new javax.swing.GroupLayout(menuBar_Container);
        menuBar_Container.setLayout(menuBar_ContainerLayout);
        menuBar_ContainerLayout.setHorizontalGroup(
            menuBar_ContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1305, Short.MAX_VALUE)
        );
        menuBar_ContainerLayout.setVerticalGroup(
            menuBar_ContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 35, Short.MAX_VALUE)
        );

        displayFile_data.setColumns(20);
        displayFile_data.setRows(5);
        jScrollPane6.setViewportView(displayFile_data);

        javax.swing.GroupLayout FileSettings_PanelLayout = new javax.swing.GroupLayout(FileSettings_Panel);
        FileSettings_Panel.setLayout(FileSettings_PanelLayout);
        FileSettings_PanelLayout.setHorizontalGroup(
            FileSettings_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menuBar_Container, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane6)
        );
        FileSettings_PanelLayout.setVerticalGroup(
            FileSettings_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FileSettings_PanelLayout.createSequentialGroup()
                .addComponent(menuBar_Container, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 930, Short.MAX_VALUE))
        );

        rightPane_CardLayout.add(FileSettings_Panel, "card4");

        SplitPane_ForCards.setRightComponent(rightPane_CardLayout);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SplitPane_ForCards)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SplitPane_ForCards)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnUIElementsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUIElementsActionPerformed
        //directing user to UIElements panel when this buttn is clicked
        isBtnClicked("UIElements_Panel");
        
        //calling table method
          setDatabase_tableData(sqlTableData_UIE);
        
    }//GEN-LAST:event_btnUIElementsActionPerformed

    private void btnDashBoardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashBoardActionPerformed
        //directing user to Dash Baord panel when this buttn is clicked
        isBtnClicked("DashBoard_Panel");
           
        //calling table method
        setDatabase_tableData(sqlTableData);
        
        total_status();
        percentage_status();
    }//GEN-LAST:event_btnDashBoardActionPerformed

    private void btnFile_SettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFile_SettingsActionPerformed
        //directing user to File Properties panel when this buttn is clicked
        isBtnClicked("FileSettings_Panel");
    }//GEN-LAST:event_btnFile_SettingsActionPerformed

    private void btnUpdateTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateTaskActionPerformed
           // ERROR HANDLING ON GUI Components
            isNum_isEmpty(txtTaskName.getText(), txtCategory.getText(), txtDescription.getText()); 
    }//GEN-LAST:event_btnUpdateTaskActionPerformed

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
                                                  
        try  {
            //creating an instance of class List__GUI
            Task_Manager_GUI frmTaskGui = new Task_Manager_GUI();

            DefaultTableModel model = (DefaultTableModel) sqlTableData_UIE.getModel();

            // Get the search input from the user
            String task_name = txtTaskName.getText(); 
            String task_category = txtCategory.getText(); 
            String task_status = getSelectedStatus(); 

            //Establishing JDBC Connection
            java.sql.Connection conn = DriverManager.getConnection(url, username, pwd);

            //preparing SQL query
            String query = "SELECT * FROM task_information WHERE ";
            java.util.List<String> params = new java.util.ArrayList<>();

            boolean isFirstCondition = true;
            
             boolean useAndOperator = true; // or false
             
            // Add conditions to the query based on user input
            if (!task_name.isEmpty()) {
                query += "task_name = ? ";
                params.add(task_name);
                isFirstCondition = false;
            }
            if (!task_category.isEmpty()) {
                if (isFirstCondition) {
                    query += "task_category = ? ";
                } else {
                    query += (useAndOperator ? "AND " : "OR ") + "task_category = ? ";
                }
                params.add(task_category);
                isFirstCondition = false;
            }
            if (!task_status.isEmpty()) {
                if (isFirstCondition) {
                    query += "task_status = ? ";
                } else {
                    query += (useAndOperator ? "AND " : "OR ") + "task_status = ? ";
                }
                params.add(task_status);
                isFirstCondition = false;
            }

            // Prepare the statement
            PreparedStatement preparedStatement = conn.prepareStatement(query);

            // Set the parameters
            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setString(i + 1, params.get(i));
            }
            //Executing query
            ResultSet rs = preparedStatement.executeQuery();

            // Create a HashMap to store the tasks by category
            java.util.HashMap<String, java.util.List<String[]>> taskMap = new HashMap<>();

            //setting row count
            model.setRowCount(0);
            while(rs.next()){
                String[] taskData = new String[]{rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)};
                String category = rs.getString(3); // assuming category is the 3rd column
                if (!taskMap.containsKey(category)) {
                    taskMap.put(category, new ArrayList<>());
                }
                taskMap.get(category).add(taskData);
                model.addRow(taskData);
            }

            frmTaskGui.sqlTableData_UIE.getColumnModel().getColumn(3).setCellRenderer(new CustomCellRenderer());

            //closing resources
            rs.close();
            preparedStatement.close();
            conn.close();


        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Can't be Filtered!");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }//GEN-LAST:event_btnFilterActionPerformed

    private void btnInsertTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertTaskActionPerformed
       // ERROR HANDLING ON GUI Components
            isNum_isEmpty_insert(txtTaskName.getText(), txtCategory.getText(), txtDescription.getText());  
    }//GEN-LAST:event_btnInsertTaskActionPerformed

    private void btnSave_asTxtFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSave_asTxtFileActionPerformed
        TaskManager taskManager = new TaskManager();
        taskManager.readTasksFromDatabase();
        taskManager.writeTasksToFile();
        JOptionPane.showMessageDialog(null,"Database data Successfully saved as text file.");

    }//GEN-LAST:event_btnSave_asTxtFileActionPerformed

    private void radInProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radInProcessActionPerformed
      // radio button logic
        if(radInProcess.isSelected()){
          rad_OnHold.setSelected(false);
          radCompleted.setSelected(false); 
        }
    }//GEN-LAST:event_radInProcessActionPerformed

    private void radYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radYesActionPerformed
        // radio button logic
       if(radYes.isSelected()){
         radNo.setSelected(false);
          btnFilter.setEnabled(true);//deativated filter button
       }
    
    }//GEN-LAST:event_radYesActionPerformed

    private void radNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radNoActionPerformed
         // radio button logic
       if(radNo.isSelected()){
         radYes.setSelected(false);
          btnFilter.setEnabled(false);//deativated filter button
       }
    }//GEN-LAST:event_radNoActionPerformed

    private void rad_OnHoldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rad_OnHoldActionPerformed
        // radio button logic
        if(rad_OnHold.isSelected()){
          radInProcess.setSelected(false);
          radCompleted.setSelected(false); 
        }
    }//GEN-LAST:event_rad_OnHoldActionPerformed

    private void radCompletedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radCompletedActionPerformed
         // radio button logic
       if(radCompleted.isSelected()){
         radInProcess.setSelected(false);
         rad_OnHold.setSelected(false);
       }
    }//GEN-LAST:event_radCompletedActionPerformed

    private void txtSearch_TaskDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearch_TaskDataActionPerformed
        single_search();
    }//GEN-LAST:event_txtSearch_TaskDataActionPerformed

    private void txtUIE_SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUIE_SearchActionPerformed
        //doing system search
        taskManager_searchSystem(txtUIE_Search);
    }//GEN-LAST:event_txtUIE_SearchActionPerformed

    private void txtSeachElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSeachElementActionPerformed
        //doing system search
        taskManager_searchSystem(txtSeachElement);
    }//GEN-LAST:event_txtSeachElementActionPerformed

    private void btnClearAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearAllActionPerformed
        //clearing the text boxes and area box
        txtTaskName.setText("");
        txtCategory.setText("");
        txtDescription.setText("");
    }//GEN-LAST:event_btnClearAllActionPerformed

   
    public static void main(String args[]) throws IOException {
     
        //creating an instance of class List__GUI
        Task_Manager_GUI frmTaskGui = new Task_Manager_GUI();
       
        //setting background image of Card1
        frmTaskGui.card_background(frmTaskGui.card1, "img_Blue.png");
        
        //setting background image of Card2
        frmTaskGui.card_background(frmTaskGui.card2, "img_Purple.png");
        
        //setting background image of Card3
        frmTaskGui.card_background(frmTaskGui.card3, "img_Yellow.png");
        
        frmTaskGui.total_status();
        frmTaskGui.percentage_status();
        
        
        //calling the gui styling method
        frmTaskGui.Task_Styling(frmTaskGui);
   
       
        //setting visibility of gui
        frmTaskGui.setVisible(true);
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
//                new Task_Manager_GUI().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel DashBoard_Panel;
    private javax.swing.JPanel FileSettings_Panel;
    private javax.swing.JPanel SearchBar;
    private javax.swing.JSplitPane SplitPane_ForCards;
    private javax.swing.JPanel UIE_SearchBar;
    private javax.swing.JPanel UIElements_Panel;
    private javax.swing.JButton btnClearAll;
    private javax.swing.JButton btnDashBoard;
    private javax.swing.JButton btnFile_Settings;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnInsertTask;
    private javax.swing.JLabel btnRefresh;
    private javax.swing.JButton btnSave_asTxtFile;
    private javax.swing.JButton btnUIElements;
    private javax.swing.JButton btnUpdateTask;
    private javax.swing.JPanel card1;
    private javax.swing.JPanel card2;
    private javax.swing.JPanel card3;
    private javax.swing.JTextArea displayFile_data;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JLabel lblComp_percent;
    private javax.swing.JLabel lblCompletedCounter;
    private javax.swing.JLabel lblHold_percent;
    private javax.swing.JLabel lblIn_Process_per;
    private javax.swing.JLabel lblOnHoldCounter;
    private javax.swing.JLabel lblProgressCounter;
    private javax.swing.JLabel lblTask;
    private javax.swing.JPanel leftPane;
    private javax.swing.JPanel menuBar_Container;
    private com.emmanual.swing.PanelBorder panelBorder1;
    private com.emmanual.swing.PanelBorder panelBorder2;
    private com.emmanual.swing.PanelBorder panelBorder3;
    private javax.swing.JRadioButton radCompleted;
    private javax.swing.JRadioButton radInProcess;
    private javax.swing.JRadioButton radNo;
    private javax.swing.JRadioButton radYes;
    private javax.swing.JRadioButton rad_OnHold;
    public static javax.swing.JPanel rightPane_CardLayout;
    private com.emmanual.swing.Table sqlTableData;
    private com.emmanual.swing.Table sqlTableData_UIE;
    private javax.swing.JTextField txtCategory;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtSeachElement;
    private javax.swing.JTextField txtSearch_TaskData;
    private javax.swing.JTextField txtTaskName;
    private javax.swing.JTextField txtUIE_Search;
    // End of variables declaration//GEN-END:variables
}
