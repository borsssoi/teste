package org.gproman.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.gproman.db.DataService;
import org.gproman.scrapper.GPROBrUtil;
import org.gproman.util.BareBonesBrowserLaunch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class MessagePanel extends UIPluginBase {

    private static final long   serialVersionUID = 210232127277861273L;

    private static final Logger logger           = LoggerFactory.getLogger(MessagePanel.class);

    private static final String URL              = "http://s3.zetaboards.com/Grand_Prix_RO/topic/7655029/1";

    private final JLabel        message;
    private Calendar            lastUpdate;

    public MessagePanel(GPROManFrame frame,
            DataService db) {
        super(frame, db);
        setLayout(new BorderLayout());

        message = new JLabel();
        String text = "<html><center><b><font size='6' color='blue'>Bem Vindo ao GMT</font></b></center><br/><br/>\n"
                + "<font size='4'>O <b>GMT</b> e o <b>GPRO Brasil</b> são ferramentas gratuitas e <br/>"
                + "mantidas por voluntários. Ao utilizar o GMT, você concorda<br/>"
                + "em compartilhar os dados de suas corridas com a comunidade<br/>"
                + "brasileira do GPRO Brasil.<br/><br/>"
                + "Obrigado e divirta-se!</font></html>";
        message.setText(text);
        message.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel gproBr = new JLabel();
        gproBr.setIcon(UIUtils.createImageIcon("/icons/gprobrBanner.jpg"));
        gproBr.setSize(750, 200);

        JLabel forum = new JLabel();
        forum.setIcon(UIUtils.createImageIcon("/icons/forumBackground.png"));

        JButton open = new JButton("Abrir o GPRO Brasil");
        open.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                BareBonesBrowserLaunch.openURL(GPROBrUtil.GPROBrasil_URL);
            }
        });
        open.setIcon(UIUtils.createImageIcon("/icons/internet_32.png"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(open);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));

        FormLayout layout = new FormLayout("270dlu ",
                "240dlu, 30dlu");

        // add rows dynamically
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);
        builder.append(message);
        builder.nextLine();
        builder.append(buttonPanel);

        add(gproBr, BorderLayout.NORTH);
        add(forum, BorderLayout.WEST);
        add(builder.build(), BorderLayout.CENTER);
    }

    @Override
    public void update() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        if (lastUpdate == null || lastUpdate.before(yesterday)) {
            new SwingWorker<String, Void>() {

                @Override
                protected String doInBackground() throws Exception {
                    logger.info("Retrieving message from GPRO Brasil.");
                    try {
                        GPROBrUtil gproBr = gproManFrame.getGPROBr();
                        HtmlPage page = gproBr.getPage(URL);
                        HtmlTableDataCell td = page.getFirstByXPath("//td[@class='c_post']");
                        if( td != null ) {
                            String content = td.asXml().trim();
                            Pattern p = Pattern.compile("<td.*?>(.*)<div class=\"editby\">.+</td>", Pattern.MULTILINE | Pattern.DOTALL);
                            Matcher m = p.matcher(content);
                            if (m.matches()) {
                                logger.info("Message retrieved.");
                                return m.group(1);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Error retrieving message.", e);
                        return null;
                    }
                    logger.info("Failed to match message.");
                    return null;
                }

                protected void done() {
                    try {
                        String result = get();
                        if (result != null) {
                            message.setText("<html>" + result + "</html>");
                            lastUpdate = Calendar.getInstance();
                        }
                    } catch (Exception e) {
                        logger.error("Error retrieving message from GPROBrasil", e);
                    }
                };
            }.execute();
        }
    }
    
    @Override
    public String getTitle() {
        return "Mensagem ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon("/icons/message_32.png");
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon("/icons/message_16.png");
    }

    @Override
    public String getDescription() {
        return "Mensagens do GMT";
    }

    @Override
    public Category getCategory() {
        return Category.SEASON;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_E;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
    
    @Override
    public boolean isEnabledAtStart() {
        // message plugin is always enabled at start
        return true;
    }
}
