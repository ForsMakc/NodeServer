package student.bazhin.database;


import javax.swing.table.AbstractTableModel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class ADatabase {

    protected String login;
    protected String password;
    protected String databaseName;

    protected Connection dbConnection = null;

    public abstract void init();

    public void init(String databaseName) {
        init();
        this.databaseName += databaseName;
    }

    public void init(String login, String password) {
        init();
        this.login = login;
        this.password = password;
    }

    public void init(String login, String password, String databaseName) {
        this.login = login;
        this.password = password;
        this.databaseName = databaseName;
    }

    public abstract void connect() throws SQLException, ClassNotFoundException;

    public abstract void release();

    public abstract TableModel executeSelect(String query);

    public abstract int executeQuery(String query);

    public class TableModel extends AbstractTableModel {
        protected List<String> mColumnNames;
        protected List<ArrayList<Object>> mTableData;
        protected List<Object> mColumnTypes;

        public TableModel(Class<?>[] types, String[] columns, ArrayList<ArrayList<Object>> tableData) {
            mColumnTypes = new ArrayList<>(types.length);
            mColumnNames = new ArrayList<>(columns.length);
            for (int i = 0; i < columns.length; ++i) {
                mColumnTypes.add(i, types[i]);
                mColumnNames.add(columns[i]);
            }
            setTableData(tableData);
        }

        @Override
        public int getColumnCount() {
            return mColumnNames.size();
        }

        @Override
        public int getRowCount() {
            return mTableData.size();
        }

        public ArrayList<Object> getRow(int index) {
            return mTableData.get(index);
        }

        public void removeRow(int index) {
            mTableData.remove(index);
        }

        public Object getValueAt(int row, String column) {
            return mTableData.get(row).get(getColumnNum(column));
        }

        @Override
        public Object getValueAt(int row, int column) {
            return mTableData.get(row).get(column);
        }

        public String getColumnName(int column) {
            return mColumnNames.get(column);
        }

        public int getColumnNum(String column) {
            return mColumnNames.indexOf(column);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public void setValueAt(Object obj, int row, int column) {

        }

        @Override
        public Class<?> getColumnClass(int col) {
            return (Class<?>) mColumnTypes.get(col);
        }

        void setTableData(ArrayList<ArrayList<Object>> tableData) {
            mTableData = tableData;
        }

        TableModel getTable() {
            return this;
        }
    }

}
