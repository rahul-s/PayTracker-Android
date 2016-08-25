package com.creativecapsule.paytracker.Repository.Common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.creativecapsule.paytracker.Models.Common.BaseModel;
import com.creativecapsule.paytracker.Utility.Common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by soniya on 3/30/15.
 */
public class SQLiteImplementation extends SQLiteOpenHelper implements DatabaseProtocol{

    private static final String DEBUG_TAG = "SQLiteImplementation";

    private static SQLiteDatabase.CursorFactory DEFAULT_CURSOR_FACTORY_VALUE = null;

    private static final String STRING_ARRAY_SEPERATOR = "__";

    private Context context;
    private String databasePath, databaseName;
    private SQLiteDatabase sqLiteDatabase;

    // region SQLiteOpenHelper Methods
    public SQLiteImplementation(Context context, String name, int version) {
        super(context, name, DEFAULT_CURSOR_FACTORY_VALUE, version);

        this.databasePath = context.getDatabasePath(name).getPath();
        this.databaseName = name;
        this.context = context;

        try {
            createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Database instance created
        this.sqLiteDatabase = openDataBase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //TODO : onCreate Implementation if required
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO : onUpgrade Implementation if required
    }

    /**
     * Opens database
     *
     * @return returns database if found with the specified name, null otherwise
     */
    public SQLiteDatabase openDataBase() {

        try {
            String database = this.databasePath;
            return SQLiteDatabase.openDatabase(database, null, SQLiteDatabase.OPEN_READWRITE);

        } catch (SQLiteException exception) {

            return null;
        }
    }

    /**
     * Checks if database exists
     *
     * @return TRUE  if database exists, false otherwise
     */
    private boolean dbExist() {

        File dbFile = context.getDatabasePath(this.databasePath);
        return dbFile.exists();
    }

    /**
     * Copies database from Assets to system path
     *
     * @throws IOException
     */
    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = this.context.getAssets().open(this.databaseName);

        // Path to the just created empty db
        String database = this.databasePath;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(database);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }


    /**
     * Creates a database
     *
     * @throws IOException
     */
    private void createDatabase() throws IOException {

        if (!dbExist()) {

            // Create an empty SQLite file.
            this.getReadableDatabase();

            // Copies database
            copyDataBase();
        }

    }
    // endregion

    // region DatabaseProtocol Methods
    @Override
    public boolean saveObject(Object object) {
        // New Generated primary key
        long identifier = -1;

        //Log.d(DEBUG_TAG, "Saving "+object.getClass().getSimpleName());
        ContentValues contentValues = new ContentValues();

        Field[] fields = Common.concatArrays(object.getClass().getDeclaredFields(), object.getClass().getSuperclass().getDeclaredFields());

        // get all the fields from the model class
        for (Field field : fields) {

            // Set accessibility for the field
            field.setAccessible(true);

            // Ignore identifier as that will be auto incremented
            if (!field.getName().equals("identifier")) {
                // Update the content value for selected field
                //Log.d(DEBUG_TAG, "saving object:" + object.getClass().getSimpleName() + " field:" + field.getName() + " type:" + field.getType().getSimpleName());
                updateContentValueObject(contentValues, object, field);
            }
        }

        try {
            identifier = this.sqLiteDatabase.insert(object.getClass().getSimpleName(), null, contentValues);
            //Log.d(DEBUG_TAG, "Object saved with identifier:" + identifier);
            ((BaseModel)object).setIdentifier((int) identifier);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Success if primary key is returned, false otherwise
        return (identifier != -1);
    }

    @Override
    public Object getObjectById(int identifier, Class aClass) {
        // Create an empty object to return
        Object object = Common.createObjectForClass(aClass);

        String whereClause = "identifier = ?";
        String[] whereArgs = new String[]{String.valueOf(identifier)};

        // Read the records for the class
        try {
            //Log.d(DEBUG_TAG, "cursor opened");
            Cursor cursor = this.sqLiteDatabase.query(aClass.getSimpleName(), null, whereClause, whereArgs, null, null, null);
            cursor.moveToFirst();

            // Read while the end is not reached
            if (!cursor.isAfterLast()) {
                updateObjectWithCursor(cursor, object);
            }
            else {
                object = null;
            }
            cursor.close();
            //Log.d(DEBUG_TAG, "cursor closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public List<Object> getObjectsFromClass(Class aClass) {
        ArrayList<Object> objects = new ArrayList<Object>();

        // Read the records for the class
        //Log.d(DEBUG_TAG, "cursor opened");
        try {
            Cursor cursor = this.sqLiteDatabase.query(aClass.getSimpleName(), null, null, null, null, null, null);
            cursor.moveToFirst();

            // Read while the end is not reached
            while (!cursor.isAfterLast()) {

                //Extract an object from cursor
                Object object = Common.createObjectForClass(aClass);
                updateObjectWithCursor(cursor, object);

                objects.add(object);
                // Fetch next object from cursor
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.d(DEBUG_TAG, "cursor closed");
        return objects;
    }

    @Override
    public List<Object> getObjectsFromClassWithClauses(Class aClass, List<String> clauses) {

        ArrayList<Object> objects = new ArrayList<Object>();
        String whereClause = "";


        for (String clause : clauses) {
            whereClause = whereClause.equals("") ? clause : whereClause + " AND " + clause;
        }

        // Read the records for the class
        //Log.d(DEBUG_TAG, "cursor opened");
        try {
            Cursor cursor = this.sqLiteDatabase.query(aClass.getSimpleName(), null, whereClause, null, null, null, null);
            cursor.moveToFirst();

            // Read while the end is not reached
            while (!cursor.isAfterLast()) {

                //Extract an object from cursor
                Object object = Common.createObjectForClass(aClass);
                updateObjectWithCursor(cursor, object);

                objects.add(object);
                // Fetch next object from cursor
                cursor.moveToNext();
            }
            cursor.close();
            //Log.d(DEBUG_TAG, "cursor closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objects;
    }

    @Override
    public List<Object> rawGetQuery(Class aClass, String sqlQuery){

        ArrayList<Object> objects = new ArrayList<Object>();
        try {
            //Log.d(DEBUG_TAG, "cursor opened");
            Cursor cursor = this.sqLiteDatabase.rawQuery(sqlQuery, null);

            if(cursor.moveToFirst()) {
                // Read while the end is not reached
                while (!cursor.isAfterLast()) {

                    //Extract an object from cursor
                    Object object = Common.createObjectForClass(aClass);
                    updateObjectWithCursor(cursor, object);

                    objects.add(object);
                    // Fetch next object from cursor
                    cursor.moveToNext();
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.d(DEBUG_TAG, "cursor closed");
        return objects;
    }

    @Override
    public boolean updateObject(Object object) {
        // New Generated primary key
        long identifier = -1;

        String whereClause = "identifier = ?";
        String[] whereArgs = new String[]{String.valueOf(((BaseModel) object).getIdentifier())};

        ContentValues contentValues = new ContentValues();

        Field[] fields = Common.concatArrays(object.getClass().getDeclaredFields(), object.getClass().getSuperclass().getDeclaredFields());

        // get all the fields from the model class
        for (Field field : fields) {

            // Set accessibility for the field
            field.setAccessible(true);

            // Update the content value for selected field
            updateContentValueObject(contentValues, object, field);
        }

        try {
            identifier = this.sqLiteDatabase.update(object.getClass().getSimpleName(), contentValues, whereClause, whereArgs);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Success if primary key is returned, false otherwise
        return (identifier != -1);
    }

    @Override
    public boolean deleteObject(Object object) {

        String whereClause = "identifier = ?";
        String[] whereArgs = new String[]{String.valueOf(((BaseModel) object).getIdentifier())};

        int result = this.sqLiteDatabase.delete(object.getClass().getSimpleName(), whereClause, whereArgs);

        return (result != -1);
    }

    @Override
    public boolean deleteAllObjectsFromClass(String className) {

        int result = this.sqLiteDatabase.delete(className, "1", null);

        return (result != -1);
    }
    //endregion

    // region Helper Methods

    /**
     * Updates an objects using values from supplied cursor
     *
     * @param cursor - Cursor to etch values
     * @param object - Object to apply values
     */
    private void updateObjectWithCursor(Cursor cursor, Object object) {
        // Get all the properties for the class
        Field[] fields = Common.concatArrays(object.getClass().getDeclaredFields(), object.getClass().getSuperclass().getDeclaredFields());

        // Traverse through all the properties and retrieve value for the property from the cursor
        for (Field field : fields) {

            field.setAccessible(true);

            // Initial Column Name
            String columnName = field.getName();

            // index of the column in cursor
            int index = cursor.getColumnIndex(columnName);

            // If valid index then retrieve data for the index and set it
            if (index > -1) {

                try {
                    Class fieldClass = field.getType();

                    if (fieldClass.equals(String.class)) {
                        field.set(object, cursor.getString(index));
                    } else if (fieldClass.equals(int.class)) {
                        field.setInt(object, cursor.getInt(index));
                    } else if (fieldClass.equals(Integer.class)) {
                        field.set(object, cursor.getInt(index));
                    } else if (fieldClass.equals(Double.class)) {
                        field.set(object, cursor.getDouble(index));
                    } else if (fieldClass.equals(float.class)) {
                        field.setFloat(object, cursor.getFloat(index));
                    } else if (fieldClass.equals(long.class)) {
                        field.setLong(object, cursor.getLong(index));
                    } else if (fieldClass.equals(double.class)) {
                        field.setDouble(object, cursor.getDouble(index));
                    } else if (fieldClass.equals(byte[].class)) {
                        field.set(object, cursor.getBlob(index));
                    } else if (fieldClass.equals(Date.class)) {
                        //convert date from millisecond(long) to Date
                        if (cursor.getLong(index) == 0) {
                            field.set(object, null);
                        }
                        else {
                            field.set(object, new Date(cursor.getLong(index)));
                        }
                    } else if (fieldClass.equals(boolean.class)) {
                        field.set(object, cursor.getInt(index) != 0);
                    } else if(fieldClass.equals(String[].class)) {

                        String[] arr = null;
                        if(cursor.getString(index) != null && !cursor.getString(index).equals("")) {
                           arr = cursor.getString(index).split(STRING_ARRAY_SEPERATOR);
                        }
                        field.set(object, arr);
                    } else if (BaseModel.class.isAssignableFrom(fieldClass)) {
                        //is a one to one relation. needs to be fetched from another table using the identifier.
                        int identifier = cursor.getInt(index);
                        field.set(object, getObjectById(identifier, fieldClass));
                    } else if (List.class.isAssignableFrom(fieldClass)) {
                        ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                        Class<?> listItemClass = (Class<?>) stringListType.getActualTypeArguments()[0];
                        //Log.d(DEBUG_TAG, "List of type:" + listItemClass);
                        if (BaseModel.class.isAssignableFrom(listItemClass)) {
                            //is a one to many relation. items needs to be fetched from another table using the identifier and added to arraylist.
                            ArrayList<BaseModel> valueList = new ArrayList<BaseModel>();
                            String[] arr = null;
                            if(cursor.getString(index) != null && !cursor.getString(index).equals("")) {
                                arr = cursor.getString(index).split(STRING_ARRAY_SEPERATOR);
                                //Log.d(DEBUG_TAG, "List of type:" + listItemClass + " identifier values:"+cursor.getString(index) + " Array separator:" + STRING_ARRAY_SEPERATOR);
                                for ( int i=0 ; i< arr.length ; i++) {
                                    String identifier = arr[i];
                                    //Log.d(DEBUG_TAG, "getting object for identifier:"+identifier);
                                    valueList.add((BaseModel)getObjectById(Integer.parseInt(identifier), listItemClass));
                                }
                            }
                            if (valueList.size() > 0) {
                                field.set(object, valueList);
                            }
                        }
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Retrieves value assigned to a field in an object
     *
     * @param object - Model Object
     * @param field  - field or variable inside the Model object
     * @return value assigned to field or null otherwise
     */
    private static Object getValueForField(Object object, Field field) {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates content value object with the supplied values
     *
     * @param contentValues - Content Value object to update
     * @param object        - Parent Object to fetch values
     * @param field         - field to fetch value
     */
    private void updateContentValueObject(ContentValues contentValues, Object object, Field field) {

        String columnName = field.getName();
        Object columnValue = getValueForField(object, field);

        Class fieldClass = field.getType();

        //if (columnValue == null) return;
        if (columnName.equals("shadow$_monitor_"))  return;                     // shadow$_monitor_ is a field added in api 21 to class "Object".
        if (fieldClass.equals(String.class)) {
            if (columnValue == null) return;
            contentValues.put(columnName, String.valueOf(columnValue));
        } else if (fieldClass.equals(int.class)) {
            if (columnValue == null) return;
            contentValues.put(columnName,(Integer) columnValue);
        } else if (fieldClass.equals(Integer.class)) {
            if (columnValue == null) return;
            contentValues.put(columnName, (Integer) columnValue);
        } else if (fieldClass.equals(Double.class)) {
            if (columnValue == null) return;
            contentValues.put(columnName, (Double) columnValue);
        } else if (fieldClass.equals(float.class)) {
            if (columnValue == null) return;
            contentValues.put(columnName, (Float) columnValue);
        } else if (fieldClass.equals(long.class)) {
            if (columnValue == null) return;
            contentValues.put(columnName, (Long) columnValue);
        } else if (fieldClass.equals(double.class)) {
            if (columnValue == null) return;
            contentValues.put(columnName, (Double) columnValue);
        } else if (fieldClass.equals(byte[].class)) {
            if (columnValue == null) return;
            contentValues.put(columnName, (byte[]) columnValue);
        } else if (fieldClass.equals(Date.class)) {
            //save date in milliseconds(long).
            if (columnValue == null) return;
            contentValues.put(columnName, ((Date)columnValue).getTime());
        } else if (fieldClass.equals(boolean.class)) {
            if (columnValue == null) return;
            contentValues.put(columnName, Boolean.valueOf(String.valueOf(columnValue)));
        } else if(fieldClass.equals(String[].class)) {
            String str = "";
            if(columnValue != null) {
                String[] value = (String[]) columnValue;
                for (int i = 0; i < value.length; i++) {
                    str = str + value[i];
                    // Do not append comma at the end of last element
                    if (i < value.length - 1) {
                        str = str + STRING_ARRAY_SEPERATOR;
                    }
                }
            }
            contentValues.put(columnName, str);
        } else if (BaseModel.class.isAssignableFrom(fieldClass)) {
            //for one to one relation store identifier from the other class.
            //Log.d(DEBUG_TAG, "relation found of type:"+fieldClass.getSimpleName());
            if(columnValue != null) {
                contentValues.put(columnName, ((BaseModel)columnValue).getIdentifier());
            }
            else {
                contentValues.put(columnName, 0);
            }
        } else if (List.class.isAssignableFrom(fieldClass)) {
            //Log.d(DEBUG_TAG, "Saving list type");
            try {
                List<BaseModel> valueList = (List<BaseModel>) columnValue;
                String valueString = "";
                for (int i = 0; i < valueList.size(); i++) {
                    // concatenate all the identifiers into a single string
                    BaseModel valueObject = valueList.get(i);
                    if (i == 0) {
                        valueString = valueString + valueObject.getIdentifier();
                    }
                    else {
                        valueString = valueString + STRING_ARRAY_SEPERATOR + valueObject.getIdentifier();
                    }
                }
                contentValues.put(columnName, valueString);
            } catch (Exception e) {
                //List is not of BaseModel type
            }

        }
    }
    // endregion
}
