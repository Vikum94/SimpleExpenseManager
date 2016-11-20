package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.AndroidDB;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.DBTable;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * Created by root on 11/19/16.
 */

public class PersistentAccountDAO implements AccountDAO{
    private SQLiteOpenHelper dbhelper;

    public PersistentAccountDAO(Context context){
        dbhelper = new AndroidDB(context);
    }


    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase sqdb = dbhelper.getReadableDatabase();

        String query = String.format("SELECT %s from ACCOUNT",
                DBTable.AccountsTable.accountNo);

        List<String> numbers = new ArrayList<>();

        final Cursor cur_point = sqdb.rawQuery(query, null);

        if(cur_point.moveToFirst()){
            do{
                numbers.add(cur_point.getString(0));
            } while(cur_point.moveToNext());
        }
        cur_point.close();
        return numbers;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase sqdb = dbhelper.getReadableDatabase();

        String query = String.format("SELECT %s %s %s %s FROM ACCOUNT",
                DBTable.AccountsTable.accountNo,
                DBTable.AccountsTable.bankName,
                DBTable.AccountsTable.accountHolderName,
                DBTable.AccountsTable.balance);

        List<Account> accounts = new ArrayList<>();

        final Cursor cur_point = sqdb.rawQuery(query, null);

        if(cur_point.moveToFirst()){
            do{
                accounts.add(new Account(cur_point.getString(0), cur_point.getString(1), cur_point.getString(2), cur_point.getDouble(3)));
            }while(cur_point.moveToNext());
        }
        cur_point.close();
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase sqdb = dbhelper.getReadableDatabase();

        String query = String.format("SELECT %s %s %s %s from ACCOUNT WHERE %s = ?",
                DBTable.AccountsTable.accountNo,
                DBTable.AccountsTable.bankName,
                DBTable.AccountsTable.accountHolderName,
                DBTable.AccountsTable.balance,
                DBTable.AccountsTable.accountNo);

        final Cursor cur_point = sqdb.rawQuery(query, null);

        if(!cur_point.moveToFirst()){
            throw new InvalidAccountException("Account " + accountNo + " is invalid");
        }

        Account acc = new Account(cur_point.getString(0), cur_point.getString(1), cur_point.getString(2), cur_point.getDouble(3));
        cur_point.close();
        return acc;
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase sqdb = dbhelper.getWritableDatabase();

        String query = String.format("INSERT OR IGNORE INTO ACCOUNT (%s %s %s %s) VALUES ? ? ? ?",
                DBTable.AccountsTable.accountNo,
                DBTable.AccountsTable.bankName,
                DBTable.AccountsTable.accountHolderName,
                DBTable.AccountsTable.balance);

        sqdb.execSQL(query, new Object[]{
                account.getAccountNo(),
                account.getBankName(),
                account.getAccountHolderName(),
                account.getBalance()});

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        getAccount(accountNo);

        SQLiteDatabase sqdb = dbhelper.getWritableDatabase();

        String query = String.format("DELETE FROM ACCOUNT WHERE %S = ?",
                DBTable.AccountsTable.accountNo);

        sqdb.execSQL(query, new Object[]{accountNo});
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        getAccount(accountNo);

        SQLiteDatabase sqdb = dbhelper.getWritableDatabase();

        String query = null;
        switch (expenseType){
            case EXPENSE: query = "UPDATE ACCOUNT SET %s = %s - ? WHERE %s = ?";
            case INCOME: query = "UPDATE ACCOUNT SET %s = %s + ? WHERE %s = ?";
        }

        query = String.format(query,
                DBTable.AccountsTable.balance,
                DBTable.AccountsTable.balance,
                DBTable.AccountsTable.accountNo);
        sqdb.execSQL(query, new Object[]{accountNo, amount});
    }
}
