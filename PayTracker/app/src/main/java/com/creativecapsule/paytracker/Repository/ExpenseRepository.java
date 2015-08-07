package com.creativecapsule.paytracker.Repository;

import android.content.Context;

import com.creativecapsule.paytracker.Models.Expense;
import com.creativecapsule.paytracker.Models.Outing;
import com.creativecapsule.paytracker.Repository.Common.BaseRepository;
import com.creativecapsule.paytracker.Repository.Common.SQLiteImplementation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahul on 30/06/15.
 */
public class ExpenseRepository extends BaseRepository {
    private static final String TABLE_NAME = "Expense";

    private static final String COLUMN_KEY_OUTING = "outing";

    public static boolean save (Context context, Expense expense){

        boolean status = false;
        SQLiteImplementation sqLiteImplementation = new SQLiteImplementation(context, DATABASE_NAME, DATABASE_VERSION);

        List<String> clauses = new ArrayList<String>();
        String clause = KEY_OBJECT_ID + "=" + "'" + expense.getIdentifier() + "'";
        clauses.add(clause);

        List<Object> expenses = sqLiteImplementation.getObjectsFromClassWithClauses(Expense.class,clauses);
        if (expenses == null || expenses.size() == 0) {
            status = sqLiteImplementation.saveObject(expense);
        }
        else {
            Expense savedExpense = (Expense)expenses.get(0);
            savedExpense.setAmount(expense.getAmount());
            savedExpense.setDescription(expense.getDescription());
            savedExpense.setExpenseBy(expense.getExpenseBy());
            savedExpense.setExpenseFor(expense.getExpenseFor());
            savedExpense.setOuting(expense.getOuting());
            savedExpense.setNote(expense.getNote());
            //TODO: update all the fields.
            status = sqLiteImplementation.updateObject(savedExpense);
        }
        return status;
    }

    public static ArrayList<Expense> getExpenses(Context context, Outing outing) {
        ArrayList<Expense> expenses = new ArrayList<>();
        SQLiteImplementation sqLiteImplementation = new SQLiteImplementation(context, DATABASE_NAME, DATABASE_VERSION);
        List<String> clauses = new ArrayList<String>();
        String clause = COLUMN_KEY_OUTING + "=" + "'" + outing.getIdentifier() + "'";
        clauses.add(clause);
        List<Object> expenseObjects = sqLiteImplementation.getObjectsFromClassWithClauses(Expense.class, clauses);
        for (int i = 0; i < expenseObjects.size(); i++) {
            expenses.add((Expense) expenseObjects.get(i));
        }
        return expenses;
    }

    public static Expense getExpense(Context context, int expenseId) {
        Expense expense;
        SQLiteImplementation sqLiteImplementation = new SQLiteImplementation(context, DATABASE_NAME, DATABASE_VERSION);
        expense = (Expense) sqLiteImplementation.getObjectById(expenseId, Expense.class);
        return expense;
    }

    public static void deleteExpense(Context context, int expenseId) {
        SQLiteImplementation sqLiteImplementation = new SQLiteImplementation(context, DATABASE_NAME, DATABASE_VERSION);
        Expense expense = (Expense) sqLiteImplementation.getObjectById(expenseId, Expense.class);
        sqLiteImplementation.deleteObject(expense);
    }

    public static void clearTable(Context context) {
        SQLiteImplementation sqLiteImplementation = new SQLiteImplementation(context, DATABASE_NAME, DATABASE_VERSION);
        sqLiteImplementation.deleteAllObjectsFromClass(TABLE_NAME);
    }
}
