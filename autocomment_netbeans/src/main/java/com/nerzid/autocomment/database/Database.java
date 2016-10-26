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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javalite.activejdbc.Base;

/**
 *
 * @author nerzid
 */
public class Database {

    public static final String DB_FILE = "C:/sqlite/db/autocomment.db";
    public static Connection conn;

    public static boolean isOpen() {
        return conn != null;
    }

    public static void open() {
        Base.open("org.sqlite.JDBC", "jdbc:sqlite:" + DB_FILE, "", "");
        conn = Base.connection();
    }

    public static void close() {
        Base.close();
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
            psmt.execute();
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
        psmt.execute();
        psmt.close();
    }

    private static void createDataTypeTable() throws SQLException {
        PreparedStatement psmt = conn.prepareStatement(""
                + "CREATE TABLE IF NOT EXISTS" + " "
                + DataTypeModel.TABLE_NAME + "("
                + DataTypeModel.COLUMN_DTID + " " + DataTypeModel.COLUMN_DTID_FIELD + ","
                + DataTypeModel.COLUMN_IDENTIFIER + " " + DataTypeModel.COLUMN_IDENTIFIER_FIELD + ","
                + DataTypeModel.COLUMN_SIMPLIFIED_IDENTIFIER + " " + DataTypeModel.COLUMN_SIMPLIFIED_IDENTIFIER_FIELD + ","
                + DataTypeModel.COLUMN_LEMMA + " " + DataTypeModel.COLUMN_LEMMA_FIELD + ","
                + DataTypeModel.COLUMN_POSTAG + " " + DataTypeModel.COLUMN_POSTAG_FIELD + ")"
        );
        psmt.execute();
        psmt.close();
    }

    public static void main(String[] args) {
        try {
            Database.open();
            createTablesIfNotExist();
            System.out.println("Database was successfully created.");
        } catch (SQLException ex) {
            System.out.println("Database couldn't be created.");
            System.out.println(ex.getMessage());
        } finally {
            Database.close();
        }
    }
}
