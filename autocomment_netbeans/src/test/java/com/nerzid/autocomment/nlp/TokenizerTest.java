/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nerzid.autocomment.nlp;

import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nerzid
 */
public class TokenizerTest {

    public TokenizerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of split method, of class Tokenizer.
     */
    @Test
    public void split_DashAndUnderDash_NoDashes() {
        System.out.println("com.nerzid.autocomment.nlp.Tokenizer.split_DashAndUnderDash_NoDashes");
        String identifier = "_isTo-beHere-_";
        List<String> expResult = Arrays.asList("is", "to", "be", "here");
        List<String> result = Tokenizer.split(identifier);
        assertEquals(expResult, result);
    }

    /**
     * Test of split method, of class Tokenizer.
     */
    @Test
    public void split_UpperCaseLettersRepeatedly_UpperCaseLettersGrouped() {
        System.out.println("com.nerzid.autocomment.nlp.Tokenizer.split_UpperCaseLettersRepeatedly_UpperCaseLettersGrouped");
        String identifier = "RGBtoGRAY";
        List<String> expResult = Arrays.asList("rgb", "to", "gray");
        List<String> result = Tokenizer.split(identifier);
        assertEquals(expResult, result);
    }

    /**
     * Test of split method, of class Tokenizer.
     */
    @Test
    public void split_LowerCaseLetterBeforeUnderDash_NewTokenAfterUnderDash() {
        System.out.println("com.nerzid.autocomment.nlp.Tokenizer.split_LowerCaseLetterBeforeUnderDash_NewTokenAfterUnderDash");
        String identifier = "canProduceSuch_results";
        List<String> expResult = Arrays.asList("can", "produce", "such", "results");
        List<String> result = Tokenizer.split(identifier);
        assertEquals(expResult, result);
    }

    @Test
    public void split_LowerCaseLetterBeforeDash_NewTokenAfterDash() {
        System.out.println("com.nerzid.autocomment.nlp.Tokenizer.split_LowerCaseLetterBeforeDash_NewTokenAfterDash");
        String identifier = "isItKillable-likeThis";
        List<String> expResult = Arrays.asList("is", "it", "killable", "like", "this");
        List<String> result = Tokenizer.split(identifier);
        assertEquals(expResult, result);
    }

    @Test
    public void split_NumbersLowerCaseBeforeToken_NumbersAndTokenAreOneSingleToken() {
        System.out.println("com.nerzid.autocomment.nlp.Tokenizer.split_NumbersLowerCaseBeforeToken_NumbersAndTokenAreOneSingleToken");
        String identifier = "convertInt32toFloat64";
        List<String> expResult = Arrays.asList("convert", "int32", "to", "float64");
        List<String> result = Tokenizer.split(identifier);
        assertEquals(expResult, result);
    }

    @Test
    public void split_NumbersBeforeUpperCaseToken_NumbersAndTokenAreOneSingleTokenTotal() {
        System.out.println("com.nerzid.autocomment.nlp.Tokenizer.split_NumbersBeforeUpperCaseToken_NumbersAndTokenAreOneSingleTokenTotal");
        String identifier = "convertINT32ToDouble16";
        List<String> expResult = Arrays.asList("convert", "int32", "to", "double16");
        List<String> result = Tokenizer.split(identifier);
        assertEquals(expResult, result);
    }

    /**
     * Test of simplifyDataType method, of class Tokenizer
     */
    @Test
    public void simplifyDataType_GivenArrayAsParam_OutputIsCollectionOfObjectAsString() {
        System.out.println("com.nerzid.autocomment.nlp.Tokenizer.simplifyDataType_GivenArrayAsParam_OutputIsCollectionOfObjectAsString");
        String data_type = "java.io.File[]";
        String expResult = "1D Array of File";
        String result = Tokenizer.simplifyDataType(data_type);
        assertEquals(expResult, result);
    }

    @Test
    public void simplifyDataType_GivenMapAsParam_OutputIsCollectionOfValueObjectAsString() {
        System.out.println("com.nerzid.autocomment.nlp.Tokenizer.simplifyDataType_GivenMapAsParam_OutputIsCollectionOfValueObjectAsString");
        String data_type = "java.util.SortedMap<java.lang.String, java.nio.charset.Charset>";
        String expResult = "1D Map of Charset";
        String result = Tokenizer.simplifyDataType(data_type);
        assertEquals(expResult, result);
    }

    @Test
    public void simplifyDataType_GivenObjectAsParam_OutputIsObjectAsString() {
        System.out.println("com.nerzid.autocomment.nlp.Tokenizer.simplifyDataType_GivenObjectAsParam_OutputIsObjectAsString");
        String data_type = "java.nio.charset.Charset";
        String expResult = "Charset";
        String result = Tokenizer.simplifyDataType(data_type);
        assertEquals(expResult, result);
    }

    /**
     * This is not the best output. It'll need another look in the future.
     */
    @Test
    public void simplifyDataType_GivenMapEntryInSetAsParam_OutputIsCollectionOfObjectAsString() {
        System.out.println("com.nerzid.autocomment.nlp.Tokenizer.simplifyDataType_GivenMapEntryInSetAsParam_OutputIsCollectionOfObjectAsString");
        String data_type = "java.util.Set<java.util.Map.Entry<java.lang.String, java.lang.Object>>";
        String expResult = "2D Map of Object";
        String result = Tokenizer.simplifyDataType(data_type);
        assertEquals(expResult, result);
    }
    
    @Test
    public void simplifyDataType_NotAllPunctuationsRemoved_CollectionTagsExist() {
        System.out.println("com.nerzid.autocomment.nlp.Tokenizer.simplifyDataType_NotAllPunctuationsRemoved_CollectionTagsExist");
        String data_type = "java.util.List<java.util.List<boolean>>";
        String expResult = "2D Collection of boolean";
        String result = Tokenizer.simplifyDataType(data_type);
        assertEquals(expResult, result);
    }
}
