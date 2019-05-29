import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class BulkUserInsertMain {

    public static void main(String[] args) throws Exception {
        new BulkUserInsertMain().run();
    }

    private void run() throws Exception {
        long initTime = System.currentTimeMillis();
        try (Connection connection = DriverManager
                .getConnection("jdbc:mysql://localhost/test2",
                        "root", "geocom")) {
            for (int count = 0; count <= 100000; count++) {
                    if (count % 10000 == 0)
                        connection.setAutoCommit(false);
                    try (PreparedStatement stmt = connection.prepareStatement(
                            "insert into employee (emp_id, fname, lname) values (?,?,?)")) {
                        stmt.setInt(1, count);
                        stmt.setString(2,"Name " + count);
                        stmt.setString(3, "Lname " + count);

                        stmt.execute();

                    }

                    if (count % 10000 == 0)
                        connection.commit();

                    if (count % 10000 == 0)
                        System.out.println("Cantidad : " + count + " tiempo en millis :" + (System.currentTimeMillis() - initTime));
                }
            }
        }
}
