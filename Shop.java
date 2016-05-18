import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Anet on 21.04.2016.
 * JDBC lesson. Created 3 table: customers, products, orders.
 */
public class Shop {

    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/shop";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "root";

    static ArrayList<String> listSimb; //list of alphabet

    static Connection conn;

    public static void main(String[] args) {
        int choice = -1;
        createListSimbols();
        try {
            conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            initDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        do{
            choice = getChoice();
            if (choice != 0){
                getSelected(choice);
            }
        }
        while ( choice !=  0);
        System.exit(0);
    }


    /**
     * Creation tables.
     * Customers: (int) id, (string) name
     * Products: (int) id, (string) name
     * Orders: (int) id, (int) customer id, (int) product id
     * @throws SQLException
     */
    private static void initDB() throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.execute("DROP TABLE IF EXISTS Customers");
            st.execute("CREATE TABLE Customers (cust_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, cust_name VARCHAR(20) NOT NULL)");
            st.execute("DROP TABLE IF EXISTS Products");
            st.execute("CREATE TABLE Products (prod_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, prod_name VARCHAR(20) NOT NULL)");
            st.execute("DROP TABLE IF EXISTS Orders");
            st.execute("CREATE TABLE Orders (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, cust_id INT, prod_id INT)");
        } finally {
            st.close();
        }
    }

    /**
     * Main panel
     * @return number of method
     */
    public static int getChoice(){
        int ch;
        String choice = JOptionPane.showInputDialog(null,
                        "1. Add customer\n" +
                        "2. Generate customers\n" +
                        "3. Add product\n" +
                        "4. Generate products\n" +
                        "5. Delete customer\n" +
                        "6. Delete product\n" +
                        "7. Create order\n" +
                        "8. View customers\n" +
                        "9. View products\n" +
                        "10. View orders\n" +
                        "0. Exit\n\n" +
                        "Enter your choice");
        ch = Integer.parseInt(choice);
        return ch;
    }

    /**
     * Calling a number by the method
     * @param choice
     */
    public static void getSelected(int choice){
        try {
            switch (choice) {
                case 1:
                    addElement("Customers", null, "cust");
                    break;
                case 2:
                    generateElements("Customers", "cust");
                    break;
                case 3:
                    addElement("Products", null, "prod");
                    break;
                case 4:
                    generateElements("Products", "prod");
                    break;
                case 5:
                    deleteElement("Customers","cust");
                    break;
                case 6:
                    deleteElement("Products","prod");
                    break;
                case 7:
                    createOrder();
                    break;
                case 8:
                    viewElements("SELECT * FROM Customers");
                    break;
                case 9:
                    viewElements("SELECT * FROM Products");
                    break;
                case 10:
                    viewElements("SELECT * FROM Orders");
                    break;
                default:
                    return;
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Checking for an element in the database.
     * If the item is not present, it is created.
     * @param type Table
     * @param prefics
     * @param name Name of column without prefics
     * @throws SQLException
     */
    private static void check(String type, String prefics, String name) throws SQLException {
        String str=new String("SELECT "+prefics+"_id FROM "+type+" WHERE "+prefics+"_name = '"+name+"'");
        PreparedStatement ps = conn.prepareStatement(str);
        try {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
            }else {
                addElement(type, name, prefics);
            }
        } finally {
            ps.close();
        }
    }


    /**
     * Inserted new order
     * @throws SQLException
     */
    private static void createOrder() throws SQLException {
        String nameCust = JOptionPane.showInputDialog(null,"Enter name of customer:");
        String nameProd = JOptionPane.showInputDialog(null, "Enter name of product:");
        check("Customers","cust",nameCust);
        check("Products","prod",nameProd);
        String inquiry = new String("INSERT INTO Orders (cust_id, prod_id) VALUES((SELECT cust_id FROM Customers WHERE cust_name = '"+nameCust+"'), (SELECT prod_id FROM Products WHERE prod_name = '"+nameProd+"'));");
        PreparedStatement ps = conn.prepareStatement(inquiry);
        ps.executeUpdate();
        JOptionPane.showMessageDialog(null, "Data Inserted into Table");
    }

    /**
     * Output table on the console.
     * @param str Query
     * @throws SQLException
     */
    private static void viewElements(String str) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(str);
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ResultSetMetaData md = rs.getMetaData();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    System.out.print(md.getColumnName(i) + "\t\t");
                }
                System.out.println();
                while (rs.next()) {
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        System.out.print(rs.getString(i) + "\t\t");
                    }
                    System.out.println();
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
        JOptionPane.showMessageDialog(null,"Data Inserted into Table");
    }

    /**
     *
     * @param type Table
     * @param name Name of column without prefix
     * @param pref Prefix
     * @throws SQLException
     */
    private static void addElement(String type, String name, String pref) throws SQLException {
        if (name==null){
            name = JOptionPane.showInputDialog(null,"Enter name:");
        }
        String inquiry = new String("INSERT INTO "+type+" ("+pref+"_name) VALUES('"+name+"')");
        PreparedStatement ps = conn.prepareStatement(inquiry);
        try {
            ps.executeUpdate();
        } finally {
            ps.close();
        }
        JOptionPane.showMessageDialog(null,"Data Inserted into Table");
    }


    /**
     * Create and fill an array of Latin letters
     */
    private static void createListSimbols(){
        listSimb =new ArrayList<>();
        for (char c = 'a';c<= 'z';c++){
            String s = new String();
            s +=c;
            listSimb.add(s);
            //Исключаем лишние символы ( ],[,...)
            if (c == 'Z') c = 'a'-1;
        }
    }

    /**
     * Generation of the name.
     * @return Name
     */
    private static StringBuffer randomString(){
        StringBuffer strTemp=new StringBuffer();
        final Random random = new Random();
        int numChar=random.nextInt(6);
        strTemp.append(listSimb.get((int) (Math.random() * listSimb.size())).toUpperCase());
        for(int j=0;j<numChar;j++){
            strTemp.append(listSimb.get((int) (Math.random() * listSimb.size())));
        }
        return strTemp;
    }

    /**
     * Generation new random elements.
     * @param type Table
     * @param prefics Prefix
     * @throws SQLException
     */
    private static void generateElements(String type, String prefics) throws SQLException {
        String sNumber = JOptionPane.showInputDialog(null, "Enter number of "+type+":");
        int number = Integer.parseInt(sNumber);
        for (int i=0;i<number;i++) {
            String name = new String(randomString());
            String inquiry = new String("INSERT INTO "+type+" ("+prefics+"_name) VALUES('"+name+"')");
            PreparedStatement ps = conn.prepareStatement(inquiry);
            try {
                ps.executeUpdate(); // for INSERT, UPDATE & DELETE
            } finally {
                ps.close();
            }
        }
        JOptionPane.showMessageDialog(null,"Data Inserted into Table");
    }


    /**
     * Removing an element from the table.
     * @param type Table
     * @param prefics Prefix
     * @throws SQLException
     */
    private static void deleteElement(String type, String prefics) throws SQLException {

        String name = JOptionPane.showInputDialog(null, "Enter name of "+type+":");
        String inquiry2 = new String("DELETE FROM "+type+" WHERE "+prefics+"_name = '"+name+"'");
        PreparedStatement ps = conn.prepareStatement(inquiry2);
        try {
            ps.executeUpdate();
        } finally {
            ps.close();
        }
        JOptionPane.showMessageDialog(null,"Data deleted from Table");
    }
}
