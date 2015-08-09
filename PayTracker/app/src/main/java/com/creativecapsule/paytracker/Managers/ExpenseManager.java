package com.creativecapsule.paytracker.Managers;

import android.content.Context;

import com.creativecapsule.paytracker.Models.Expense;
import com.creativecapsule.paytracker.Models.Outing;
import com.creativecapsule.paytracker.Models.Person;
import com.creativecapsule.paytracker.Repository.ExpenseRepository;
import com.creativecapsule.paytracker.Repository.OutingRepository;
import com.creativecapsule.paytracker.Repository.PersonRepository;
import com.creativecapsule.paytracker.Utility.PayTrackerApplication;

import java.util.ArrayList;

/**
 * Created by rahul on 30/06/15.
 */
public class ExpenseManager {

    private static ExpenseManager sharedInstance = new ExpenseManager();
    private Context managerContext;

    public static ExpenseManager getSharedInstance() {
        return sharedInstance;
    }

    private ExpenseManager() {
        this.managerContext = PayTrackerApplication.getAppContext();
    }

    //region Public methods

    public Outing getOutingById(int outingId) {
        return OutingRepository.getOuting(managerContext, outingId);
    }

    public ArrayList<Outing> getOutings() {
        return OutingRepository.getOutings(managerContext);
    }

    public void saveOuting(Outing outing) {
        OutingRepository.save(managerContext, outing);
    }

    public Expense getExpense(int expenseId) {
        return ExpenseRepository.getExpense(managerContext, expenseId);
    }

    public ArrayList<Expense> getExpenses(Outing outing) {
        return ExpenseRepository.getExpenses(managerContext, outing);
    }

    public void saveExpense(Expense expense) {
        ExpenseRepository.save(managerContext, expense);
    }

    public void deleteExpense(int expenseId) {
        ExpenseRepository.deleteExpense(managerContext, expenseId);
    }

    //endregion
}
