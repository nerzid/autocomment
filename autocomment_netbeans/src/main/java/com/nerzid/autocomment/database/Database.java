/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nerzid.autocomment.database;

import java.sql.Connection;
import java.util.Collection;
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

    public static void main(String[] args) {
//        Sentence sent = new Sentence("has next");
//        System.out.println(sent.lemmas().toString());
//        System.out.println(sent.posTags().toString());
//        open();
//        Word.insert(new Word("deneme", "denemo", "denemz", "public"));
//        close();


    }
}
