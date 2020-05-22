package student.bazhin.database;

import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FirebirdDatabase extends ADatabase implements Serializable {

    String DRIVER = "org.firebirdsql.jdbc.FBDriver";

    public void init() {
        login = "SYSDBA";
        password = "masterkey";
        databaseName = "jdbc:firebirdsql:localhost/3050:";
    }

    @Override
    public void connect() throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER);
        dbConnection = DriverManager.getConnection(databaseName,login,password);
    }

    @Override
    public void release() {
        if (dbConnection != null) {
            try {
                dbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized TableModel executeSelect(String query) {
        Statement st = null;
        ResultSet rs = null;
        ArrayList<ArrayList<Object>> data = new ArrayList<>();
        try {
            st = dbConnection.createStatement();
            rs = st.executeQuery(query);
            int columnsCount = rs.getMetaData().getColumnCount();

            String[] columns = new String[columnsCount];
            Class<?>[] types = new Class<?>[columnsCount];
            for (int i = 1; i <= columnsCount; i++) {
                columns[i-1] = rs.getMetaData().getColumnName(i);
                types[i-1] = rs.getMetaData().getColumnClassName(i).getClass();
            }

            while (rs.next()) {
                ArrayList<Object> nextRow = new ArrayList<>(columnsCount);
                for (int i = 1; i <= columnsCount; i++) {
                    nextRow.add(rs.getObject(i));
                }
                data.add(nextRow);
            }

            return new TableModel(types,columns,data).getTable();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public synchronized int executeQuery(String query) {
        Statement st = null;
        try {
            st = dbConnection.createStatement();
            return st.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

}
