package org.apache.poi.poifs.crypt;

import org.apache.poi.poifs.crypt.dsig.SignatureInfo;

public class TestSignatureInfo {
    public void testConstruct() {
        // simple test which just verifies that we can load the class and all dependent classes
        SignatureInfo info = new SignatureInfo();
        if(info.toString() == null) {
            throw new IllegalStateException("ToString is null");
        }
    }
}
