import java.sql.*;

public class JDBCConnectionTest {
    String url = "jdbc:mysql://localhost/cuentas_db";
    String user = "root";
    String password = "geocom";

    public static void main(String[] args) throws Exception {
        JDBCConnectionTest test = new JDBCConnectionTest();
        test.crearCuentas();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    test.transferir(1234, 1235, 20000.0);
                } catch (SQLException throwables) {
                    throw new RuntimeException(throwables);
                }
            }
        }, "transferencia").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    test.comprar(1234, 30000.0);
                } catch (SQLException throwables) {
                    throw new RuntimeException(throwables);
                }
            }
        }, "compra").start();

    }

    private void crearCuentas() throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager
                    .getConnection(url,
                            user, password);

            connection.setAutoCommit(false);//begin_transaction

            crearCuenta(connection, 1234, "Juan Perez", 100000.0);
            crearCuenta(connection, 1235, "Marcela Perez", 100000.0);

            connection.commit();
        } catch (Exception e) {
            connection.rollback();
        }

    }

    private void comprar(int idCuenta, double valor) throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager
                    .getConnection(url,
                            user, password);

            connection.setAutoCommit(false);//begin_transaction

            double saldo = saldo(connection, idCuenta);

            actualizarSaldo(connection, idCuenta, saldo - valor );

            connection.commit();
        } catch (Exception e) {
            connection.rollback();
        }
    }

    private void transferir(int idOrigen, int idDestino, double valor) throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager
                    .getConnection(url,
                            user, password);
            connection.setAutoCommit(false);

            double saldoOrigen = saldo(connection, idOrigen);

            double saldoDestino = saldo(connection, idDestino);

            actualizarSaldo(connection, idOrigen, saldoOrigen - valor);

            actualizarSaldo(connection, idDestino, saldoDestino + valor);

            connection.commit();
        } catch (Exception e) {
            connection.rollback();
        }
    }

    private void actualizarSaldo(Connection connection, int idCuenta, double saldoCuenta) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("update cuentas set saldo = ? where id = ?", ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_UPDATABLE)) {
            ps.setDouble(1, saldoCuenta);
            ps.setInt(2, idCuenta);
            ps.execute();
        }
    }

    private double saldo(Connection connection, int idCuenta) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("select * from cuentas where id = ? for update")) {
            ps.setInt(1, idCuenta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("saldo");
                } else
                    throw new RuntimeException();
            }
        }
    }

    private void crearCuenta(Connection connection, int id, String nombre, double saldo) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "insert into cuentas (id, nombre, saldo) values(?, ?, ?)");
        ps.setInt(1, id);
        ps.setString(2, nombre);
        ps.setDouble(3, saldo);

        ps.execute();
    }
}
