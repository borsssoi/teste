package org.gproman.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.gproman.model.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CredentialsManager {
    private static final Logger logger           = LoggerFactory.getLogger( CredentialsManager.class );
    private static final String CREDENTIALS_FILE = System.getProperty( "user.home" )+"/"+".gmt.credentials.bin";

    private static final byte[] s                = new byte[]{25, 32, 87, 110, 87, 34, 76, 29};

    public static byte[] marshallCredentials(UserCredentials credentials) throws InvalidKeyException,
                                                                     InvalidKeySpecException,
                                                                     NoSuchAlgorithmException,
                                                                     NoSuchPaddingException,
                                                                     IllegalBlockSizeException,
                                                                     IOException {

        SecretKey secretKey = SecretKeyFactory.getInstance( "DES" ).generateSecret( new DESKeySpec( s ) );

        Cipher encrypter = Cipher.getInstance( "DES/ECB/PKCS5Padding" );
        encrypter.init( Cipher.ENCRYPT_MODE, secretKey );

        // Seal it, storing it in a SealedObject
        SealedObject sealed = new SealedObject( credentials, encrypter );

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( buffer );
        oos.writeObject( sealed );
        oos.close();

        return buffer.toByteArray();
    }

    public static UserCredentials unmarshallCredentials(byte[] buffer) throws InvalidKeyException,
                                                                InvalidKeySpecException,
                                                                NoSuchAlgorithmException,
                                                                NoSuchPaddingException,
                                                                IOException,
                                                                ClassNotFoundException,
                                                                IllegalBlockSizeException,
                                                                BadPaddingException {
        SecretKey secretKey = SecretKeyFactory.getInstance( "DES" ).generateSecret( new DESKeySpec( s ) );

        Cipher decrypter = Cipher.getInstance( "DES/ECB/PKCS5Padding" );
        decrypter.init( Cipher.DECRYPT_MODE, secretKey );

        SealedObject sealed = (SealedObject) new ObjectInputStream( new ByteArrayInputStream( buffer ) ).readObject();

        UserCredentials credentials = (UserCredentials) sealed.getObject( decrypter );

        return credentials;
    }

    public static UserCredentials loadCredentials() {
        UserCredentials credentials = null;
        File file = new File( CREDENTIALS_FILE );
        if ( file.exists() ) {
            try {
                byte[] buffer = new byte[(int) file.length()];
                FileInputStream fis = new FileInputStream( file );
                fis.read( buffer );
                fis.close();
                credentials = CredentialsManager.unmarshallCredentials( buffer );
            } catch ( Exception e ) {
                logger.error( "Error trying to read user credentials from file " + CREDENTIALS_FILE, e );
            }
        }
        return credentials;
    }
    
    public static void saveCredentials(UserCredentials credentials) {
        try {
            FileOutputStream fos = new FileOutputStream( CREDENTIALS_FILE );
            fos.write( CredentialsManager.marshallCredentials( credentials ) );
            fos.close();
        } catch ( Exception e ) {
            logger.error( "Error saving credentials to file. Credentials will not be saved.", e );
        }
    }

    

}
