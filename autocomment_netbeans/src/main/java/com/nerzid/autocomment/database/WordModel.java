/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nerzid.autocomment.database;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 *
 * @author nerzid
 */
@Table(value = "Word")
public class WordModel extends Model {

    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_LEMMA = "lemma";
    public static final String COLUMN_POSTAG = "postag";
    public static final String COLUMN_DATA_TYPE = "data_type";
    
    public static final String COLUMN_TEXT_FIELD = "varchar(255)";
    public static final String COLUMN_LEMMA_FIELD = "varchar(255)";
    public static final String COLUMN_POSTAG_FIELD = "varchar(255)";
    public static final String COLUMN_DATA_TYPE_FIELD = "varchar(255)";

    public static final int COLUMN_TEXT_INT = 1;
    public static final int COLUMN_LEMMA_INT = 2;
    public static final int COLUMN_POSTAG_INT = 3;
    public static final int COLUMN_DATA_TYPE_INT = 4;

}
