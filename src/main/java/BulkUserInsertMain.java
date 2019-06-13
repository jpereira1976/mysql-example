import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class BulkUserInsertMain {
    private static final int BLOCKS = 1000;
    private static final int BLOCK_SIZE = 1000;

    public static void main(String[] args) throws Exception {
        new BulkUserInsertMain().run();
    }

    private DataSource createPool() {
        BasicDataSource pool = new BasicDataSource();
        pool.setUrl("jdbc:mysql://localhost/test2");
        pool.setUsername("root");
        pool.setPassword("geocom");
        pool.setMaxWaitMillis(5000);
        pool.setMaxIdle(4);
        pool.setMaxTotal(10);
        pool.setTimeBetweenEvictionRunsMillis(60000);
        return pool;
    }

    private void run() throws Exception {
        DataSource datasource = createPool();

            for (int blockCount = 0; blockCount <= BLOCKS; blockCount++) {

                long initTime = System.currentTimeMillis();


                try (Connection connection = datasource.getConnection()) {

                    connection.setAutoCommit(false);


                    for (int count = 0; count < BLOCK_SIZE; count ++) {
                        int id = blockCount*BLOCKS*BLOCK_SIZE + count;
                        try (PreparedStatement stmt = connection.prepareStatement(
                                "insert into employee (emp_id, fname, lname) values (?,?,?)")) {
                            stmt.setInt(1, id);
                            stmt.setString(2, "Name " + id);
                            stmt.setString(3, "Lname " + id);

                            stmt.execute();

                        }
                    }

                    connection.commit();
                    System.out.println("Cantidad : " + ((blockCount + 1)*BLOCK_SIZE) + " tiempo en millis :" + (System.currentTimeMillis() - initTime));

                }

            }
        }
}
