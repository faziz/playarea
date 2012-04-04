/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.faziz.playarea;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author faziz
 */
public class NewEmptyJUnitTest {
    
    public NewEmptyJUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
     @Test
     public void testArrayIndex() {
         int[] x = new int[3];
         try{
             System.out.println("test: " + x[4]);
         }catch(ArrayIndexOutOfBoundsException ex){
             System.out.println(ex);
         }
         
     }
}
