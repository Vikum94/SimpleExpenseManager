package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by root on 11/19/16.
 */

public class AndroidDB extends SQLiteOpenHelper{
    static int database_version = 1;

    public AndroidDB(Context context){
        super(context, DBTable.dbName, null, database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ACCOUNT (" + DBTable.AccountsTable.accountNo + " TEXT PRIMARY KEY, "
                + DBTable.AccountsTable.bankName + " TEXT" + DBTable.AccountsTable.accountHolderName + " TEXT"
                + DBTable.AccountsTable.balance + " REAL" + " )");
        db.execSQL("CREATE TABLE TRANSACTION (" + DBTable.TransactionsTable.date + " INT PRIMARY KEY, "
                + "FOREIGN KEY (" + DBTable.TransactionsTable.accountNo + ") REFERENCES "
                + DBTable.TransactionsTable.expenseType + " TEXT"
                + DBTable.TransactionsTable.amount + " REAL" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ACCOUNT");
        db.execSQL("DROP TABLE IF EXISTS TRANSACTION");
        onCreate(db);
    }

}
