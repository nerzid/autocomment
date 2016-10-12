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

    public static void createIfNotExists() throws SQLException {
        PreparedStatement psmt = conn.prepareStatement(""
                + "CREATE TABLE IF NOT EXISTS Word("
                + WordGroupModel.COLUMN_TEXT + " " + WordGroupModel.COLUMN_TEXT_FIELD + ","
                + WordGroupModel.COLUMN_LEMMA + " " + WordGroupModel.COLUMN_LEMMA_FIELD + ","
                + WordGroupModel.COLUMN_POSTAG + " " + WordGroupModel.COLUMN_POSTAG_FIELD + ","
                + WordGroupModel.COLUMN_DATA_TYPE + " " + WordGroupModel.COLUMN_DATA_TYPE_FIELD + ")"
        );
        
        psmt.execute();
    }

    public static void main(String[] args) {
        try {
            Database.open();
            createIfNotExists();
            System.out.println("Database was successfully created.");
        } catch (SQLException ex) {
            System.out.println("Database couldn't be created.");
        } finally {
            Database.close();
        }
    }
}
