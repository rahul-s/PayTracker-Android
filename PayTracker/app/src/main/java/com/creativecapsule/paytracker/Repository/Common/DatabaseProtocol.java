package com.creativecapsule.paytracker.Repository.Common;

import java.util.List;

/**
 * Created by soniya on 3/30/15.
 */
public interface DatabaseProtocol {

    /**
     * Saves the object to the local database
     *
     * @param object-object to be saved
     * @return - status of the save operation True if success False otherwise
     */
    boolean saveObject(Object object);

    /**
     * Reads all objects from the database
     *
     * @param aClass - class to fetch data for
     * @param sqlQuery - Raw SQL query
     * @return List of Objects fetched from database.
     */
    List<Object> rawGetQuery(Class aClass, String sqlQuery);

    /**
     * Read the object from the database
     *
     * @param identifier - identifier of the object
     * @param aClass     - class to fetch data for
     * @return Object if present in database, null otherwise
     */
    Object getObjectById(int identifier, Class aClass);

    /**
     * Reads all objects from the database
     *
     * @param aClass - class to fetch data for
     * @return List of Objects fetched from database.
     */
    List<Object> getObjectsFromClass(Class aClass);

    /**
     * Reads objects from the database with clause
     *
     * @param aClass  - class to fetch data for
     * @param clauses - Where clauses to filter the records
     * @return List of Objects fetched from database.
     */
    List<Object> getObjectsFromClassWithClauses(Class aClass, List<String> clauses);

    /**
     * Updates the given object in the database
     *
     * @param object - Object to be updated
     * @return - status of the update operation True if success False otherwise
     */
    boolean updateObject(Object object);

    /**
     * deletes the given object from the database
     *
     * @param object - Object to be deleted
     * @return - status of the delete operation True if success False otherwise
     */
    boolean deleteObject(Object object);

    /**
     * deletes all the objects from the table
     *
     * @param className - Table name from which records to be deleted
     * @return - status of the delete operation True if success False otherwise
     */
    boolean deleteAllObjectsFromClass(String className);
}
