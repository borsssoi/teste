package org.gproman.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gproman.db.DataService;
import org.gproman.model.driver.Driver;
import org.gproman.model.driver.DriverAttributes;
import org.gproman.model.race.Race;
import org.gproman.model.track.Track;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class DriverPanel extends UIPluginBase {

    private static final long   serialVersionUID = 210232127277861273L;

    private final MessageFormat salFormatter     = new MessageFormat( "{0,number,currency}" );
    private final MessageFormat avgFormatter     = new MessageFormat( "{0,number,#.##}" );

    private JLabel              driverName       = new JLabel();
    private JLabel              nationality      = new JLabel();
    private JLabel              salary           = new JLabel();
    private JLabel              contract         = new JLabel();
    private JLabel              trophies         = new JLabel();
    private JLabel              gps              = new JLabel();
    private JLabel              wins             = new JLabel();
    private JLabel              podiums          = new JLabel();
    private JLabel              points           = new JLabel();
    private JLabel              poles            = new JLabel();
    private JLabel              fastestLaps      = new JLabel();
    private JLabel              avgPoints        = new JLabel();
    private JLabel[]            favoriteTracks   = new JLabel[4];

    private JLabel              age              = new JLabel();
    private JLabel              weight           = new JLabel();
    private JLabel              reputation       = new JLabel();
    private JLabel              motivation       = new JLabel();
    private JLabel              charisma         = new JLabel();
    private JLabel              stamina          = new JLabel();
    private JLabel              techInsight      = new JLabel();
    private JLabel              experience       = new JLabel();
    private JLabel              aggressiveness   = new JLabel();
    private JLabel              talent           = new JLabel();
    private JLabel              concentration    = new JLabel();
    private JLabel              overall          = new JLabel();

    private JLabel              page             = new JLabel();
    private JLabel              pweight          = new JLabel();
    private JLabel              preputation      = new JLabel();
    private JLabel              pmotivation      = new JLabel();
    private JLabel              pcharisma        = new JLabel();
    private JLabel              pstamina         = new JLabel();
    private JLabel              ptechInsight     = new JLabel();
    private JLabel              pexperience      = new JLabel();
    private JLabel              paggressiveness  = new JLabel();
    private JLabel              ptalent          = new JLabel();
    private JLabel              pconcentration   = new JLabel();
    private JLabel              poverall         = new JLabel();

    private JLabel              zsWings          = new JLabel();
    private JLabel              zsEngine         = new JLabel();
    private JLabel              zsBrakes         = new JLabel();
    private JLabel              zsGear           = new JLabel();
    private JLabel              zsSuspension     = new JLabel();

    public DriverPanel(GPROManFrame gproManFrame,
                       DataService dataService) {
        super( gproManFrame,
               dataService );
        setLayout( new BoxLayout( this, BoxLayout.LINE_AXIS ) );

        FormLayout layout = new FormLayout( "right:max(40dlu;p), 4dlu, 80dlu, 7dlu ", // 1st major column
        "" );

        // add rows dynamically
        DefaultFormBuilder builder = new DefaultFormBuilder( layout );
        builder.border( Borders.DIALOG );
        builder.appendSeparator( "Piloto: " );

        JLabel lbl = builder.append( "Nome: ", driverName );
        Font bold = lbl.getFont().deriveFont( Font.BOLD );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Nacionalidade: ", nationality );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Salário: ", salary );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Contrato: ", contract );
        lbl.setFont( bold );
        builder.nextLine();

        builder.appendSeparator( "Carreira: " );

        lbl = builder.append( "Troféus: ", trophies );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "# de GPs: ", gps );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Vitórias: ", wins );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Pódios: ", podiums );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Pontos: ", points );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Poles: ", poles );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Voltas rápidas: ", fastestLaps );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Pontos/corrida: ", avgPoints );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Pistas favoritas: " );
        lbl.setFont( bold );
        favoriteTracks[0] = new JLabel();
        builder.append( favoriteTracks[0] );
        builder.nextLine();
        for ( int i = 1; i < favoriteTracks.length; i++ ) {
            favoriteTracks[i] = new JLabel();
            builder.append( "" );
            builder.append( favoriteTracks[i] );
        }

        JPanel panel = builder.getPanel();
        panel.setMaximumSize( new Dimension( panel.getPreferredSize().width, panel.getMaximumSize().height ) );
        
        add( panel );

        // Building the second column
        layout = new FormLayout( "right:max(40dlu;p), 4dlu, 20dlu, 4dlu, 70dlu, 7dlu ", "" );

        builder = new DefaultFormBuilder( layout );
        builder.border( Borders.DIALOG );
        builder.appendSeparator( "Atributos: " );

        lbl = builder.append( "Total: ", overall, poverall );
        lbl.setFont( bold );
        Font boldItalic = poverall.getFont().deriveFont( Font.BOLD | Font.ITALIC );
        poverall.setFont( boldItalic );
        builder.nextLine();

        lbl = builder.append( "Concentração: ", concentration, pconcentration );
        lbl.setFont( bold );
        pconcentration.setFont( boldItalic );
        builder.nextLine();

        lbl = builder.append( "Talento: ", talent, ptalent );
        lbl.setFont( bold );
        ptalent.setFont( boldItalic );
        builder.nextLine();

        lbl = builder.append( "Agressividade: ", aggressiveness, paggressiveness );
        lbl.setFont( bold );
        paggressiveness.setFont( boldItalic );
        builder.nextLine();

        lbl = builder.append( "Experiência: ", experience, pexperience );
        lbl.setFont( bold );
        pexperience.setFont( boldItalic );
        builder.nextLine();

        lbl = builder.append( "Conhecimento Técnico: ", techInsight, ptechInsight );
        lbl.setFont( bold );
        ptechInsight.setFont( boldItalic );
        builder.nextLine();

        lbl = builder.append( "Resistência: ", stamina, pstamina );
        lbl.setFont( bold );
        pstamina.setFont( boldItalic );
        builder.nextLine();

        lbl = builder.append( "Carisma: ", charisma, pcharisma );
        lbl.setFont( bold );
        pcharisma.setFont( boldItalic );
        builder.nextLine();

        lbl = builder.append( "Motivação: ", motivation, pmotivation );
        lbl.setFont( bold );
        pmotivation.setFont( boldItalic );
        builder.nextLine();

        lbl = builder.append( "Reputação: ", reputation, preputation );
        lbl.setFont( bold );
        preputation.setFont( boldItalic );
        builder.nextLine();

        lbl = builder.append( "Peso: ", weight, pweight );
        lbl.setFont( bold );
        pweight.setFont( boldItalic );
        builder.nextLine();

        lbl = builder.append( "Idade: ", age, page );
        lbl.setFont( bold );
        page.setFont( boldItalic );
        builder.nextLine();

        builder.appendSeparator( "Zona de Satisfação: " );

        lbl = builder.append( "Asas: ", zsWings );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Motor: ", zsEngine );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Freio: ", zsBrakes );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Câmbio: ", zsGear );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Suspensão: ", zsSuspension );
        lbl.setFont( bold );
        builder.nextLine();

        builder.append( "" );
        builder.nextLine();

        lbl = new JLabel( "AVISO: o valor da ZS apresentado aqui é baseado" );
        Font italic = lbl.getFont().deriveFont( Font.ITALIC );
        lbl.setFont( italic );
        builder.append( lbl, 5 );
        builder.nextLine();
        lbl = new JLabel( "somente nos atributos do piloto. Para encontrar" );
        lbl.setFont( italic );
        builder.append( lbl, 5 );
        builder.nextLine();
        lbl = new JLabel( "a ZS efetiva, é necessário subtrair a ZS do" );
        lbl.setFont( italic );
        builder.append( lbl, 5 );
        builder.nextLine();
        lbl = new JLabel( "Diretor Técnico do valor acima." );
        lbl.setFont( italic );
        builder.append( lbl, 5 );
        builder.nextLine();
        
        panel = builder.getPanel();
        panel.setMaximumSize( new Dimension( panel.getPreferredSize().width, panel.getMaximumSize().height ) );

        add( panel );

    }

    public void updateDriver(Driver driver,
                             Driver previousDriver) {
        driverName.setText( driver.getName() );
        nationality.setText( driver.getNationality() );
        salary.setText( salFormatter.format( new Object[]{driver.getSalary()} ) );
        contract.setText( driver.getContract() + (driver.getContract() == 1 ? " corrida" : " corridas") );
        trophies.setText( String.valueOf( driver.getTrophies() ) );
        gps.setText( String.valueOf( driver.getGps() ) );
        wins.setText( String.valueOf( driver.getWins() ) );
        podiums.setText( String.valueOf( driver.getPodiums() ) );
        points.setText( String.valueOf( driver.getPoints() ) );
        poles.setText( String.valueOf( driver.getPoles() ) );
        fastestLaps.setText( String.valueOf( driver.getFastestLaps() ) );
        avgPoints.setText( avgFormatter.format( new Object[]{((double) driver.getPoints()) / driver.getGps()} ) );

        List<Track> tracks = driver.getFavoriteTracks();
        for ( int i = 0; i < favoriteTracks.length; i++ ) {
            // displays up to 4 favorite tracks
            if ( i < tracks.size() ) {
                favoriteTracks[i].setText( tracks.get( i ).getName() );
            } else {
                favoriteTracks[i].setText( "" );
            }
        }

        DriverAttributes attr = driver.getAttributes();
        overall.setText( String.valueOf( attr.getOverall() ) );
        concentration.setText( String.valueOf( attr.getConcentration() ) );
        talent.setText( String.valueOf( attr.getTalent() ) );
        aggressiveness.setText( String.valueOf( attr.getAggressiveness() ) );
        experience.setText( String.valueOf( attr.getExperience() ) );
        techInsight.setText( String.valueOf( attr.getTechInsight() ) );
        stamina.setText( String.valueOf( attr.getStamina() ) );
        charisma.setText( String.valueOf( attr.getCharisma() ) );
        motivation.setText( String.valueOf( attr.getMotivation() ) );
        reputation.setText( String.valueOf( attr.getReputation() ) );
        weight.setText( String.valueOf( attr.getWeight() ) );
        age.setText( String.valueOf( attr.getAge() ) );

        if ( previousDriver != null && previousDriver.getName().equals( driver.getName() ) ) {
            DriverAttributes pattr = previousDriver.getAttributes();
            setDiff( poverall, attr.getOverall(), pattr.getOverall() );
            setDiff( pconcentration, attr.getConcentration(), pattr.getConcentration() );
            setDiff( ptalent, attr.getTalent(), pattr.getTalent() );
            setDiff( paggressiveness, attr.getAggressiveness(), pattr.getAggressiveness() );
            setDiff( pexperience, attr.getExperience(), pattr.getExperience() );
            setDiff( ptechInsight, attr.getTechInsight(), pattr.getTechInsight() );
            setDiff( pstamina, attr.getStamina(), pattr.getStamina() );
            setDiff( pcharisma, attr.getCharisma(), pattr.getCharisma() );
            setDiff( pmotivation, attr.getMotivation(), pattr.getMotivation() );
            setDiff( preputation, attr.getReputation(), pattr.getReputation() );
            setDiff( pweight, attr.getWeight(), pattr.getWeight() );
            setDiff( page, attr.getAge(), pattr.getAge() );
        }

        zsWings.setText( String.valueOf( driver.getSatisfactionZone() ) );
        zsEngine.setText( String.valueOf( driver.getSatisfactionZone() ) );
        zsBrakes.setText( String.valueOf( driver.getSatisfactionZone() ) );
        zsGear.setText( String.valueOf( driver.getSatisfactionZone() ) );
        zsSuspension.setText( String.valueOf( driver.getSatisfactionZone() ) );
    }

    private static Color DARK_GREEN = new Color( 0, 128, 0 );
    private static Color DARK_RED = new Color( 210, 0, 0 );
    private void setDiff(JLabel lbl,
                         int val,
                         int pval) {
        int diff = val - pval;
        if ( diff == 0 ) {
            lbl.setText( "" );
        } else if ( diff > 0 ) {
            lbl.setText( String.format( "(%+d)", diff ) );
            lbl.setForeground( DARK_GREEN );
        } else {
            lbl.setText( String.format( "(%+d)", diff ) );
            lbl.setForeground( DARK_RED );
        }
    }

    @Override
    public void update() {
        if ( isDirty() ) {
            Race nextRace = db.getNextRace();
            if ( nextRace != null ) {
                Driver driver = nextRace.getDriverStart();
                Race previousRace = getPreviousRace( nextRace );
                Driver previousDriver = previousRace != null ? previousRace.getDriverStart() : null;
                if ( driver != null ) {
                    updateDriver( driver, previousDriver );
                }
            }
            setDirty( false );
        }
    }

    private Race getPreviousRace(Race nextRace) {
        Race previousRace = null;
        if ( nextRace.getNumber() == 1 ) {
            previousRace = db.getRace( nextRace.getSeasonNumber() - 1, 17 );
        } else {
            previousRace = db.getRace( nextRace.getSeasonNumber(), nextRace.getNumber() - 1 );
        }
        return previousRace;
    }

    @Override
    public String getTitle() {
        return "Piloto ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon( "/icons/helmet_32.png" );
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon( "/icons/helmet_16.png" );
    }

    @Override
    public String getDescription() {
        return "Informações sobre o piloto";
    }

    @Override
    public Category getCategory() {
        return Category.TEAM;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_P;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
