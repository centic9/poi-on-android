package org.apache.poi.poifs.crypt;

import org.apache.poi.poifs.crypt.dsig.SignatureInfo;

import static junit.framework.Assert.assertNotNull;

public class TestSignatureInfo {
    public void testConstruct()  {
        // simple test which just verifies that we can load the class and all dependend classes
        SignatureInfo info = new SignatureInfo();
        assertNotNull(info);
    }
}
