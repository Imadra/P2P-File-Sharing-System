import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Random;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class ExampleGUI extends JFrame implements ActionListener{
    private JButton search;  //Buttons
    private JButton dload;
    private JButton close;  
    
    private JList jl;   // List that will show found files
    private JLabel label; //Label "File Name
    private JTextField tf,tf2; // Two textfields: one is for typing a file name, the other is just to show the selected file
    DefaultListModel listModel; // Used to select items in the list of found files
    
    String str[]={"1","3","4","7","check"}; // Files information
    
    public ExampleGUI(){
        super("Example GUI");
        setLayout(null);
        setSize(500,600);
        
        label=new JLabel("File name:");
        label.setBounds(50,50, 80,20);
        add(label);
        
        tf=new JTextField();
        tf.setBounds(130,50, 220,20);
        add(tf);
        
        search=new JButton("Search");
        search.setBounds(360,50,80,20);
        search.addActionListener(this);
        add(search);
     
        listModel = new DefaultListModel();
        jl=new JList(listModel);
        
        JScrollPane listScroller = new JScrollPane(jl);
        listScroller.setBounds(50, 80,300,300);
        
        add(listScroller);
        
        dload=new JButton("Download");
        dload.setBounds(200,400,130,20);
        dload.addActionListener(this);
        add(dload);
     
        tf2=new JTextField();
        tf2.setBounds(200,430,130,20);
        add(tf2);
        
        close=new JButton("Close");
        close.setBounds(360,470,80,20);
        close.addActionListener(this);
        add(close);
        
        setVisible(true);
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource()==search){ //If search button is pressed show 25 randomly generated file info in text area 
            String fileName=tf.getText();
            Random r=new Random();
            for (int i = 0; i < 25; i++) {
                listModel.insertElementAt(fileName+" "+str[r.nextInt(str.length)],i);
            }
        } 
        else if(e.getSource()==dload){   //If download button is pressed get the selected value from the list and show it in text field
            tf2.setText(jl.getSelectedValue().toString()+" donwloaded");
        }
        else if(e.getSource()==close){ //If close button is pressed exit
            System.exit(0);
        }
      
    }
    public static void main(String[] args) {
        ExampleGUI ex=new ExampleGUI();
        ex.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the window if x button is pressed
    }
}