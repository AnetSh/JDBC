import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Anet on 19.04.2016.
 * JDBC lesson. Created table flats that contains district, address, area, number of rooms, price.
 * Implement SELECT request
 */
public class Flat {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/flat";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "root";

    static ArrayList<String> listSimb;

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

    public static int getChoice(){
        int ch;
        String choice = JOptionPane.showInputDialog(null,
                "1. Add flat\n" +
                        "2. Select flats\n" +
                        "3. View flats\n" +
                        "4. Delete flat\n" +
                        "5. Add random flats\n" +
                        "0. Exit\n\n" +
                        "Enter your choice");
        ch = Integer.parseInt(choice);
        return ch;
    }

    public static void getSelected(int choice){
        try {
            switch (choice) {
                case 1:
                    addFlat();
                    break;
                case 2:
                    selectFlat();
                    break;
                case 3:
                    viewFlats("SELECT * FROM Flats");
                    break;
                case 4:
                    deleteFlat();
                    break;
                case 5:
                    addrandomFlats();
                    break;
                default:
                    return;
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void initDB() throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.execute("DROP TABLE IF EXISTS Flats");
            st.execute("CREATE TABLE Flats (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, district VARCHAR(20) NOT NULL, address VARCHAR(20) NOT NULL, area INT, numRoom INT, price INT)");
        } finally {
            st.close();
        }
    }

    private static void viewFlats(String str) throws SQLException {
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
                rs.close(); // rs can't be null according to the docs
            }
        } finally {
            ps.close();
        }
        JOptionPane.showMessageDialog(null,"Data Inserted into Table");
    }

    private static void addFlat() throws SQLException {
        int area, price, numRoom;
        String district = JOptionPane.showInputDialog(null,"Enter district:");
        String address = JOptionPane.showInputDialog(null,"Enter address:");
        String sArea = JOptionPane.showInputDialog(null,"Enter area:");
        String sNumRoom = JOptionPane.showInputDialog(null,"Enter number of rooms:");
        String sPrice = JOptionPane.showInputDialog(null,"Enter price:");
        area = Integer.parseInt(sArea);
        numRoom = Integer.parseInt(sNumRoom);
        price = Integer.parseInt(sPrice);

        PreparedStatement ps = conn.prepareStatement("INSERT INTO Flats (district, address, area, numRoom, price) VALUES(?, ?, ?, ?, ?)");
        try {
            ps.setString(1, district);
            ps.setString(2, address);
            ps.setInt(3, area);
            ps.setInt(4, numRoom);
            ps.setInt(5, price);
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE
        } finally {
            ps.close();
        }
        JOptionPane.showMessageDialog(null,"Data Inserted into Table");
    }

    private static void createListSimbols(){
        // Создаём и заполняем массив латинских букв
        listSimb =new ArrayList<>();
        for (char c = 'a';c<= 'z';c++){
            String s = new String();
            s +=c;
            listSimb.add(s);
            //Исключаем лишние символы ( ],[,...)
            if (c == 'Z') c = 'a'-1;
        }
    }
    private static StringBuffer randomString(){
        StringBuffer strTemp=new StringBuffer();
        final Random random = new Random();
        int numChar=random.nextInt(6);
        strTemp.append(listSimb.get((int) (Math.random() * listSimb.size())).toUpperCase());
        for(int j=0;j<numChar;j++){
            //Вывод случайного элемента из этого массива
            strTemp.append(listSimb.get((int) (Math.random() * listSimb.size())));
        }
        return strTemp;
    }


    private static void addrandomFlats() throws SQLException {
        int number;

        String sNumber = JOptionPane.showInputDialog(null,"Enter number of flats:");
        number = Integer.parseInt(sNumber);
        for (int i=0;i<number;i++) {
            final Random random = new Random();
            String district = new String(randomString());
            String address = new String(randomString() + " " + random.nextInt(50));
            int area = random.nextInt(50);
            int numRoom = random.nextInt(4);
            int price = random.nextInt(50000);

            PreparedStatement ps = conn.prepareStatement("INSERT INTO Flats (district, address, area, numRoom, price) VALUES(?, ?, ?, ?, ?)");
            try {
                ps.setString(1, district);
                ps.setString(2, address);
                ps.setInt(3, area);
                ps.setInt(4, numRoom);
                ps.setInt(5, price);
                ps.executeUpdate(); // for INSERT, UPDATE & DELETE
            } finally {
                ps.close();
            }
        }
        JOptionPane.showMessageDialog(null,"Data Inserted into Table");
    }

    private static void selectFlat() throws SQLException {
        String district = JOptionPane.showInputDialog(null,"Enter district for select:");
        String str=new String("SELECT price FROM Flats WHERE district = ?"+district+";");
        viewFlats(str);
    }

    private static void deleteFlat() throws SQLException {
        String address = JOptionPane.showInputDialog(null,"Enter address:");
        PreparedStatement ps = conn.prepareStatement("DELETE FROM Flats WHERE address = ?");
        try {
            ps.setString(1, address);
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE
        } finally {
            ps.close();
        }
        JOptionPane.showMessageDialog(null,"Data deleted from Table");
    }
}
