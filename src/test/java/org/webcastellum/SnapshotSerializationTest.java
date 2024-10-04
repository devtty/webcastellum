package org.webcastellum;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class SnapshotSerializationTest {
    
    private static final String TMPFILETEST = "/tmp/file.test";
 
    @Test
    public void serializationTest(){
        File f = new File(TMPFILETEST);
        assertFalse(f.exists());
        
        Map payload = new HashMap();
        for(int i=0; i<1000; i++){
            IncrementingCounter counter = new IncrementingCounter((long)(Math.random()*10000)+500);
            int x = (int)(Math.random()*1500);
            System.out.println(x);
            for(int k=0; k<x; k++)
                counter.increment();
            payload.put(IdGeneratorUtils.createId(), counter);
        }
        
        Snapshot s = new Snapshot("ThisIsJustATestType", IdGeneratorUtils.createId(), payload);
        
        try(ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream( new FileOutputStream(TMPFILETEST)))) {
            
            output.writeObject(s);
            output.flush();
        } catch (IOException ex) {
            Logger.getLogger(SnapshotSerializationTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        assertTrue(f.exists());
        assertTrue(f.delete());
    }
    
}
