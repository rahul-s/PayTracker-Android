package com.creativecapsule.paytracker.Repository;

import android.content.Context;

import com.creativecapsule.paytracker.Models.Outing;
import com.creativecapsule.paytracker.Repository.Common.BaseRepository;
import com.creativecapsule.paytracker.Repository.Common.SQLiteImplementation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahul on 30/06/15.
 */
public class OutingRepository extends BaseRepository {
    private static final String TABLE_NAME = "Outing";

    public static boolean save (Context context, Outing outing){

        boolean status = false;
        SQLiteImplementation sqLiteImplementation = new SQLiteImplementation(context, DATABASE_NAME, DATABASE_VERSION);

        List<String> clauses = new ArrayList<String>();
        String clause = KEY_OBJECT_ID + "=" + "'" + outing.getIdentifier() + "'";
        clauses.add(clause);

        List<Object> outings = sqLiteImplementation.getObjectsFromClassWithClauses(Outing.class,clauses);
        if (outings == null || outings.size() == 0) {
            status = sqLiteImplementation.saveObject(outing);
        }
        else {
            Outing savedOuting = (Outing)outings.get(0);
            savedOuting.setTitle(outing.getTitle());
            savedOuting.setPersons(outing.getPersons());
            status = sqLiteImplementation.updateObject(savedOuting);
        }
        return status;
    }

    public static ArrayList<Outing> getOutings(Context context) {
        ArrayList<Outing> outings = new ArrayList<>();
        SQLiteImplementation sqLiteImplementation = new SQLiteImplementation(context, DATABASE_NAME, DATABASE_VERSION);
        List<Object> outingObjects = sqLiteImplementation.getObjectsFromClass(Outing.class);
        for (int i = 0; i < outingObjects.size(); i++) {
            outings.add((Outing)outingObjects.get(i));
        }
        return outings;
    }

    public static Outing getOuting(Context context, int outingId) {
        SQLiteImplementation sqLiteImplementation = new SQLiteImplementation(context, DATABASE_NAME, DATABASE_VERSION);

        List<String> clauses = new ArrayList<String>();
        String clause = KEY_OBJECT_ID + "=" + "'" + outingId + "'";
        clauses.add(clause);

        List<Object> outings = sqLiteImplementation.getObjectsFromClassWithClauses(Outing.class,clauses);
        if (outings != null && outings.size() > 0) {
            return (Outing) outings.get(0);
        }
        else {
            return null;
        }
    }

    public static void clearTable(Context context) {
        SQLiteImplementation sqLiteImplementation = new SQLiteImplementation(context, DATABASE_NAME, DATABASE_VERSION);
        sqLiteImplementation.deleteAllObjectsFromClass(TABLE_NAME);
    }
}
