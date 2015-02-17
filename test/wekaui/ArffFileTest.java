/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import weka.core.Instances;

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
        ArffFile instance = new ArffFile();
        
        File validArffFile;
        URL arffFileUrl = getClass().getResource("valid.arff");
        try {
            validArffFile = new File(arffFileUrl.toURI());
            instance.setFile(validArffFile);
        } catch (Exception ex) {
            fail("invalid arff file doesn't exist on disk");
        }
        
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
        ArffFile instance = new ArffFile();
        
        File validArffFile;
        URL arffFileUrl = getClass().getResource("invalid.arff");
        try {
            validArffFile = new File(arffFileUrl.toURI());
            instance.setFile(validArffFile);
        } catch (Exception ex) {
            fail("invalid arff file doesn't exist on disk");
        }
        
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
        ArffFile instance = new ArffFile();
        
        File validArffFile;
        URL arffFileUrl = getClass().getResource("empty.arff");
        try {
            validArffFile = new File(arffFileUrl.toURI());
            instance.setFile(validArffFile);
        } catch (Exception ex) {
            fail("invalid arff file doesn't exist on disk");
        }
        
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
        ArffFile instance = new ArffFile();
        
        File validArffFile;
        URL arffFileUrl = getClass().getResource("valid.arff");
        try {
            validArffFile = new File(arffFileUrl.toURI());
            instance.setFile(validArffFile);
        } catch (Exception ex) {
            fail("invalid arff file doesn't exist on disk");
        }
        
        File destFile = new File("test.arff");
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
