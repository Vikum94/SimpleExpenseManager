package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.AndroidDB;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.DBTable;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by root on 11/19/16.
 */

public class PersistentTransactionDAO implements TransactionDAO{
    private SQLiteOpenHelper dbhelper;

    public PersistentTransactionDAO(Context context){
            dbhelper = new AndroidDB(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase sqdb = dbhelper.getWritableDatabase();

        String query = String.format("INSERT OR IGNORE INTO TRANSACTION (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
                DBTable.TransactionsTable.date,
                DBTable.TransactionsTable.accountNo,
                DBTable.TransactionsTable.expenseType,
                DBTable.TransactionsTable.amount);

        sqdb.execSQL(query, new Object[]{
                date.getTime(),
                accountNo,
                expenseType,
                amount});
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase sqdb = dbhelper.getWritableDatabase();
        String query = String.format("SELECT %s, %s, %s, %s FROM TRANSACTION)",
                DBTable.TransactionsTable.date,
                DBTable.TransactionsTable.accountNo,
                DBTable.TransactionsTable.expenseType,
                DBTable.TransactionsTable.amount);

        List<Transaction> history = new ArrayList<>();

        final Cursor cur_point = sqdb.rawQuery(query, null);

        if (cur_point.moveToFirst()) {
            do {
                history.add(new Transaction(
                        new Date(cur_point.getLong(0)),
                        cur_point.getString(1),
                        Enum.valueOf(ExpenseType.class, cur_point.getString(2)),
                        cur_point.getDouble(3)));
            } while (cur_point.moveToNext());
        }
        cur_point.close();

        return history;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase sqdb = dbhelper.getWritableDatabase();
        String query = String.format("SELECT %s, %s, %s, %s FROM TRANSACTION LIMIT %s)",
                DBTable.TransactionsTable.date,
                DBTable.TransactionsTable.accountNo,
                DBTable.TransactionsTable.expenseType,
                DBTable.TransactionsTable.amount,
                limit);

        List<Transaction> history = new ArrayList<>();

        final Cursor cur_point = sqdb.rawQuery(query, null);

        if (cur_point.moveToFirst()) {
            do {
                history.add(new Transaction(
                        new Date(cur_point.getLong(0)),
                        cur_point.getString(1),
                        Enum.valueOf(ExpenseType.class, cur_point.getString(2)),
                        cur_point.getDouble(3)));
            } while (cur_point.moveToNext());
        }
        cur_point.close();

        return history;
    }
}
