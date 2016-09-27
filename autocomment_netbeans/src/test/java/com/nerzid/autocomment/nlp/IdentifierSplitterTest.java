/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nerzid.autocomment.nlp;

import com.nerzid.autocomment.nlp.IdentifierSplitter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
public class IdentifierSplitterTest {

    public IdentifierSplitterTest() {
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
     * Test of split method, of class IdentifierSplitter.
     */
    @Test
    public void testSplit1() {
        System.out.println("split-1");
        String identifier = "_isTo-beHere-_";
        List<String> expResult = Arrays.asList("is", "to", "be", "here");
        List<String> result = IdentifierSplitter.split(identifier);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of split method, of class IdentifierSplitter.
     */
    @Test
    public void testSplit2() {
        System.out.println("split-2");
        String identifier = "RGBtoGRAY";
        List<String> expResult = Arrays.asList("rgb", "to", "gray");
        List<String> result = IdentifierSplitter.split(identifier);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of split method, of class IdentifierSplitter.
     */
    @Test
    public void testSplit3() {
        System.out.println("split-3");
        String identifier = "canProduceSuch_results";
        List<String> expResult = Arrays.asList("can", "produce", "such", "results");
        List<String> result = IdentifierSplitter.split(identifier);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testSplit4() {
        System.out.println("split-4");
        String identifier = "isItKillable-likeThis";
        List<String> expResult = Arrays.asList("is", "it", "killable", "like", "this");
        List<String> result = IdentifierSplitter.split(identifier);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testSplit5(){
        System.out.println("split-5");
        String identifier = "convertInt32toFloat64";
        List<String> expResult = Arrays.asList("convert", "int32", "to", "float64");
        List<String> result = IdentifierSplitter.split(identifier);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testSplit6(){
        System.out.println("split-6");
        String identifier = "convertINT32ToDouble16";
        List<String> expResult = Arrays.asList("convert", "int32", "to", "double16");
        List<String> result = IdentifierSplitter.split(identifier);
        assertEquals(expResult, result);
    }

    /**
     * Test of getPunctuations method, of class IdentifierSplitter.
     */
    @Test
    public void testGetPunctuations() {
        System.out.println("getPunctuations");
        char[] expResult = {'-', '_'};
        char[] result = IdentifierSplitter.getPunctuations();
        assertArrayEquals(expResult, result);
    }
}
