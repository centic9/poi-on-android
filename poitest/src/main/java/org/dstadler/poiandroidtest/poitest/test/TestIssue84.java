package org.dstadler.poiandroidtest.poitest.test;

import org.apache.poi.java.awt.Color;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

import java.io.IOException;
import java.io.OutputStream;

public class TestIssue84 {
    public static void saveExcelFile(OutputStream outputStream) throws IOException {
        try (XMLSlideShow ppt = new XMLSlideShow()) {
            XSLFSlide slide = ppt.createSlide();
            XSLFTextBox shape = slide.createTextBox();
            XSLFTextParagraph p = shape.addNewTextParagraph();
            XSLFTextRun r1 = p.addNewTextRun();
            r1.setText("The");
            r1.setFontColor(Color.blue);
            r1.setFontSize(24.0);

            ppt.write(outputStream);
        }
    }
}
