/*
 * Copyright 2016 nerzid.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nerzid.autocomment.database;

import com.nerzid.autocomment.exception.FileNotSelected;
import com.nerzid.autocomment.io.FilePicker;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.javalite.activejdbc.Base;

/**
 *
 * @author nerzid
 */
public class Database {

    public static String DB_FILE_NAME = "autocomment.db";

    // DB_FILE_PATH is the path of database.
    // IMPORTANT: this path has to change if your db isn't in that path.
    public static String DB_FILE_PATH = "C:/sqlite/db/" + DB_FILE_NAME;

    public static boolean isSet = false;

    public static Connection conn;

    public static boolean isOpen() {
        return conn != null;
    }

    public static void openIfNot() throws FileNotSelected {
        if (!isOpen()) {
            open();
        }
    }

    /**
     * 
     * @throws FileNotSelected
     * @deprecated Use openIfNot() instead
     */
    @Deprecated
    public static void open() throws FileNotSelected {
        if (!isSet) {
            if (chooseDB()) {
                Base.open("org.sqlite.JDBC", "jdbc:sqlite:" + DB_FILE_PATH, "", "");
                conn = Base.connection();
                isSet = true;
            } 
            return;
        }
        Base.open("org.sqlite.JDBC", "jdbc:sqlite:" + DB_FILE_PATH, "", "");
        conn = Base.connection();
        isSet = true;
    }

    public static void close() {
        Base.close();
        isSet = false;
        conn = null;
    }

    public static void createTablesIfNotExist() throws SQLException {
        createDataTypeTable();
        createMethodTable();
        createParameterTable();
    }

    private static void createMethodTable() {
        PreparedStatement psmt;
        try {
            psmt = conn.prepareStatement(""
                    + "CREATE TABLE IF NOT EXISTS" + " "
                    + MethodModel.TABLE_NAME + "("
                    + MethodModel.COLUMN_MID + " " + MethodModel.COLUMN_MID_FIELD + ","
                    + MethodModel.COLUMN_SIGNATURE + " " + MethodModel.COLUMN_SIGNATURE_FIELD + ","
                    + MethodModel.COLUMN_IDENTIFIER + " " + MethodModel.COLUMN_IDENTIFIER_FIELD + ","
                    + MethodModel.COLUMN_SPLITTED_IDENTIFIER + " " + MethodModel.COLUMN_SPLITTED_IDENTIFIER_FIELD + ","
                    + MethodModel.COLUMN_LEMMA + " " + MethodModel.COLUMN_LEMMA_FIELD + ","
                    + MethodModel.COLUMN_POSTAG + " " + MethodModel.COLUMN_POSTAG_FIELD + ","
                    + MethodModel.COLUMN_FK_DTID + " " + MethodModel.COLUMN_FK_DTID_FIELD + ","
                    + MethodModel.COLUMN_FK_DTID_FIELD_FOREIGNKEY + ")"
            );
            psmt.executeUpdate();
            psmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void createParameterTable() throws SQLException {
        PreparedStatement psmt = conn.prepareStatement(""
                + "CREATE TABLE IF NOT EXISTS" + " "
                + ParameterModel.TABLE_NAME + "("
                + ParameterModel.COLUMN_PID + " " + ParameterModel.COLUMN_PID_FIELD + ","
                + ParameterModel.COLUMN_IDENTIFIER + " " + ParameterModel.COLUMN_IDENTIFIER_FIELD + ","
                + ParameterModel.COLUMN_SPLITTED_IDENTIFIER + " " + ParameterModel.COLUMN_SPLITTED_IDENTIFIER_FIELD + ","
                + ParameterModel.COLUMN_LEMMA + " " + ParameterModel.COLUMN_LEMMA_FIELD + ","
                + ParameterModel.COLUMN_POSTAG + " " + ParameterModel.COLUMN_POSTAG_FIELD + ","
                + ParameterModel.COLUMN_FK_DTID + " " + ParameterModel.COLUMN_FK_DTID_FIELD + ","
                + ParameterModel.COLUMN_FK_MID + " " + ParameterModel.COLUMN_FK_MID_FIELD + ","
                + ParameterModel.COLUMN_FK_DTID_FIELD_FOREIGNKEY + ","
                + ParameterModel.COLUMN_FK_MID_FIELD_FOREIGNKEY + ")"
        );
        psmt.executeUpdate();
        psmt.close();
    }

    private static void createDataTypeTable() {
        try {
            PreparedStatement psmt = conn.prepareStatement(""
                    + "CREATE TABLE IF NOT EXISTS" + " "
                    + DataTypeModel.TABLE_NAME + "("
                    + DataTypeModel.COLUMN_DTID + " " + DataTypeModel.COLUMN_DTID_FIELD + ","
                    + DataTypeModel.COLUMN_IDENTIFIER + " " + DataTypeModel.COLUMN_IDENTIFIER_FIELD + ","
                    + DataTypeModel.COLUMN_SIMPLIFIED_IDENTIFIER + " " + DataTypeModel.COLUMN_SIMPLIFIED_IDENTIFIER_FIELD + ","
                    + DataTypeModel.COLUMN_LEMMA + " " + DataTypeModel.COLUMN_LEMMA_FIELD + ","
                    + DataTypeModel.COLUMN_POSTAG + " " + DataTypeModel.COLUMN_POSTAG_FIELD + ")"
            );
            psmt.executeUpdate();
            psmt.close();
        } catch (SQLException ex) {
            System.out.println("msg: " + ex.getMessage());
        }
    }

    /**
     * 
     * @return
     * @throws FileNotSelected
     * @deprecated Use openIfNot instead
     */
    @Deprecated
    public static boolean chooseDB() throws FileNotSelected {
        JOptionPane.showMessageDialog(null, "Choose Database file with extension .db");
        File f = FilePicker.chooseDBFile();
        if (f == null) {
            return false;
        }
        DB_FILE_PATH = f.getPath();
        System.out.println(DB_FILE_PATH);
        System.out.println("Database choosen.");
        isSet = true;
        return true;
    }

    public static void main(String[] args) throws FileNotSelected {

        // Here we need Database File's path in order to create it there
        DB_FILE_PATH = FilePicker.getFilePath(FilePicker.chooseDir());

        if (DB_FILE_PATH != null) {
            try {
                DB_FILE_PATH = DB_FILE_PATH + "/" + DB_FILE_NAME;
                File db_file = new File(DB_FILE_PATH);
                db_file.createNewFile();

                while (!db_file.exists()) {
                    System.out.println("waitin for file to be created");
                }

                System.out.println("File is created, continuing process...");

                Database.openIfNot();
                createTablesIfNotExist();
                System.out.println("Database was successfully created.");
            } catch (SQLException ex) {
                System.out.println("Database couldn't be created.");
                System.out.println(ex.getMessage());
            } catch (IOException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                Database.close();
            }
        } else {
            System.err.println("Database's file path can not be null");
        }
    }
}
