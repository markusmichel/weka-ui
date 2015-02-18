/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author markus
 */
public class ArffFileTest {
    
    public ArffFileTest() {
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
     * isArffFile valid returns true on valid arff file
     */
    @Test
    public void testIsArffFileValidWithValidArffFile() {
        System.out.println("isArffFileValid with valid arff file");
        
        File validArffFile;
        URL arffFileUrl = getClass().getResource("valid.arff");
        ArffFile instance = new ArffFile(arffFileUrl.getPath());
        
        boolean expResult = true;
        boolean result = instance.isArffFileValid();
        
        assertEquals(expResult, result);
    }
    
    /**
     * isArffFile valid returns false on invalid arff file
     */
    @Test
    public void testIsArffFileValidWithInvalidArffFile() {
        System.out.println("isArffFileValid with invalid arff file");
        
        File validArffFile;
        URL arffFileUrl = getClass().getResource("invalid.arff");
        ArffFile instance = new ArffFile(arffFileUrl.getPath());
        
        boolean expResult = false;
        boolean result = instance.isArffFileValid();
        
        assertEquals(expResult, result);
    }
    
    /**
     * isArffFile valid returns false on invalid arff file
     */
    @Test
    public void testIsArffFileValidWithEmptyArffFile() {
        System.out.println("isArffFileValid with empty arff file");
        
        File validArffFile;
        URL arffFileUrl = getClass().getResource("empty.arff");
        ArffFile instance = new ArffFile(arffFileUrl.getPath());
        
        boolean expResult = true;
        boolean result = instance.isArffFileValid();
        
        assertEquals(expResult, result);
    }

    /**
     * Test of saveEmptyArffFile method, of class ArffFile.
     */
    @Test
    public void testSaveEmptyArffFile() {
        System.out.println("saveEmptyArffFile");
        
        File validArffFile;
        URL arffFileUrl = getClass().getResource("valid.arff");
        ArffFile instance = new ArffFile(arffFileUrl.getPath());
        
        File destFile = new File("tmp_test_file.arff");
        try {
            instance.saveEmptyArffFile(destFile);
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
        
        assertTrue("Empty arff file exists", destFile.exists());
        instance.setFile(destFile);
        assertTrue("Dest file is valid arff file", instance.isArffFileValid() == true);
        
        destFile.delete();
    }
    
}
