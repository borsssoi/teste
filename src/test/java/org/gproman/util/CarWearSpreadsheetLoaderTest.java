package org.gproman.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;

import org.gproman.model.car.CarWearData;
import org.junit.Ignore;
import org.junit.Test;

public class CarWearSpreadsheetLoaderTest {

    @Test @Ignore("Just a manual test to check the output")
    public void testReadSourceSpreadsheet() throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
        CarWearSpreadsheetLoader loader = new CarWearSpreadsheetLoader();
        CarWearData data = loader.loadSourceSpreadsheet( new FileInputStream( "resources/Car_wear_prediction_v3.xlsx" ), 
                                                         new FileInputStream( "resources/TrackAdditionalData.xls" ), 
                                                         false );
        loader.serializeToXML( data, System.out, false );
    }

}
