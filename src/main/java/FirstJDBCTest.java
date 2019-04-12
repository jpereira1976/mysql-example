
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FirstJDBCTest {
    String url;
    String user;
    String password;

    public static void main(String[] args) throws Exception {
        FirstJDBCTest test = new FirstJDBCTest();
        test.configureMySQL();
        test.sampleQuery();
        test.deleteEmployee(11);
        test.insertEmployee(11, "Juan", "Gonzalez");
        test.executeQuery("select * from employee where emp_id = 1",
                new ExecuteQueryCallback() {
                    @Override
                    public void execute(ResultSet rs) throws Exception {
                        rs.setFetchSize(1000);
                        while (rs.next()) {
                            System.out.println(rs.getString("fname"));
                        }
                    }
                });
        System.out.println("fin");
    }

    private void deleteEmployee(int id) throws Exception {
        try (Connection connection = DriverManager
                .getConnection(url,
                        user, password)) {


            try (PreparedStatement stmt = connection.prepareStatement(
                    "delete from employee where emp_id = ?")) {
                stmt.setInt(1, id);

                stmt.execute();

            }
        }
    }

    private void insertEmployee(int id, String fname, String lname) throws Exception {
        try (Connection connection = DriverManager
                .getConnection(url,
                        user, password)) {


            try (PreparedStatement stmt = connection.prepareStatement(
                    "insert into employee (emp_id, fname, lname) values (?,?,?)")) {
                stmt.setInt(1, id);
                stmt.setString(2,fname);
                stmt.setString(3, lname);

                stmt.execute();

            }

        }
    }

    private void configurePostgreSQL() {
        url = "jdbc:postgresql://localhost:5432/test2";
        user = "postgres";
        password = "geocom";
    }

    private void configureMySQL() {
        url = "jdbc:mysql://localhost/test2";
        user = "root";
        password = "geocom";
    }

    public void sampleQuery() throws Exception {
        try (Connection connection = DriverManager
                .getConnection(url,
                        user, password)) {


            try (PreparedStatement stmt = connection.prepareStatement("select * from employee where emp_id = ?")) {
                stmt.setInt(1, 1);

                try (ResultSet rs = stmt.executeQuery()) {
                    rs.setFetchSize(1000);
                    while (rs.next()) {
                        System.out.println(rs.getString("fname"));
                    }
                }
            }

        }

    }

    public void executeQuery(String sql, ExecuteQueryCallback callback) throws Exception {
        Connection connection = null;
        try {
            connection =
                    DriverManager
                            .getConnection(url,
                                   user, password);


                try (PreparedStatement stmt = connection.prepareStatement(sql)) {

                    try (ResultSet rs = stmt.executeQuery()) {
                        callback.execute(rs);
                    }
                }
        } finally {
            if (connection != null && !connection.isClosed())
                connection.close();
        }
    }
}
