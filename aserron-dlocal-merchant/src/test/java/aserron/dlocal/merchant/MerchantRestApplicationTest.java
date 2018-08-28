package aserron.dlocal.merchant;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *  @DisabledIf(
     expression = "#{systemProperties['os.name'].toLowerCase().contains('mac')}",
     reason = "Disabled on Mac OS"
 )
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Ignore
public class MerchantRestApplicationTest {
    
    
    @Test
    public void contextLoads() {
    }
    
    public MerchantRestApplicationTest() {
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
     * Test of main method, of class MerchantRestApplication.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        MerchantRestApplication.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
