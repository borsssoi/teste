package org.gproman.util;

import org.gproman.model.UserCredentials;
import org.junit.Assert;
import org.junit.Test;

public class CredentialsManagerTest {

    @Test
    public void test() {
        UserCredentials credentials = new UserCredentials("some login", "some password", "GproBR user", "GproBR password", UserCredentials.UserRole.STANDARD);
        try {
            UserCredentials newCredentials = CredentialsManager.unmarshallCredentials( CredentialsManager.marshallCredentials( credentials ) );
            Assert.assertEquals( credentials, newCredentials );
        } catch ( Exception e ) {
            e.printStackTrace();
            Assert.fail("Should not have raised exception.");
        }
    }

}
