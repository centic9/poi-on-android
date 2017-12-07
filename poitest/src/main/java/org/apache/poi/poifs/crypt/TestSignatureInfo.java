package org.apache.poi.poifs.crypt;

import org.apache.poi.poifs.crypt.dsig.SignatureInfo;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class TestSignatureInfo {
    @Test
    public void testConstruct() throws Exception {
        // simple test which just verifies that we can load the class and all dependend classes
        SignatureInfo info = new SignatureInfo();
        assertNotNull(info);
    }
}
