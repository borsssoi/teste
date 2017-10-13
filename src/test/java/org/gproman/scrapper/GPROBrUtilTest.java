package org.gproman.scrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.gproman.model.UserConfiguration;
import org.gproman.model.UserConfiguration.ProxyType;
import org.gproman.model.UserCredentials;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class GPROBrUtilTest {

    private UserConfiguration conf;

    @Before
    public void setup() {
        conf = mock( UserConfiguration.class );
        when( conf.getProxyType() ).thenReturn( ProxyType.NO_PROXY );
    }

    @Test
    public void testParsePage() throws FailingHttpStatusCodeException, IOException {
        HtmlPage page = loadPage( "gprobr_login_moderador.html" );
        
        HtmlAnchor link = page.getFirstByXPath( "//div[contains( text(), 'Welcome' )]/strong/a" );
        
        assertEquals( "http://gprobrasil.com/profile/4143931/", link.getHrefAttribute() );
    }

    @Test
    public void testParsePage2() throws FailingHttpStatusCodeException, IOException {
        HtmlPage page = loadPage( "gprobr_login_member.html" );
        
        GPROBrUtil gprobr = new GPROBrUtil( new UserCredentials( "", "", "Adonis Santos", "" , UserCredentials.UserRole.STANDARD), conf );
        boolean ok = gprobr.validateLogin( page );
        
        assertTrue( ok );
    }

    @Test
    public void testParsePage3() throws FailingHttpStatusCodeException, IOException {
        HtmlPage page = loadPage( "gprobr_profile2.html" );
        
        GPROBrUtil gprobr = new GPROBrUtil( new UserCredentials( "", "", "Adonis Santos", "" , UserCredentials.UserRole.STANDARD ), conf );
        String group = gprobr.extractGroup( page );
        
        assertEquals( "Membros", group );
    }

    @Test
    public void testParsePage4() throws FailingHttpStatusCodeException, IOException {
        HtmlPage page = loadPage( "gprobr_login_member.html" );
        
        HtmlAnchor link = page.getFirstByXPath( "//div[contains( text(), 'Welcome' )]/strong/a" );
        
        assertEquals( "http://gprobrasil.com/profile/4121256/", link.getHrefAttribute() );
    }

    private HtmlPage loadPage( String pageStr ) throws IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        URL url = getClass().getResource( pageStr );
        assertNotNull( url );
        
        HtmlPage page = client.getPage( url );
        assertNotNull( page );
        return page;
    }
}
