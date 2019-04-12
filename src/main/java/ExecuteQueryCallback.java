import java.sql.ResultSet;

public interface ExecuteQueryCallback {

    void execute(ResultSet rs) throws Exception;

}
