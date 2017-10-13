package org.gproman.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gproman.db.DataService;
import org.gproman.model.race.Race;
import org.gproman.model.staff.TDAttributes;
import org.gproman.model.staff.TechDirector;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class TechDirectorPanel extends UIPluginBase {

    private static final long   serialVersionUID = 210232127277861273L;

    private final MessageFormat salFormatter     = new MessageFormat( "{0,number,currency}" );

    private JLabel              tdName           = new JLabel();
    private JLabel              nationality      = new JLabel();
    private JLabel              salary           = new JLabel();
    private JLabel              contract         = new JLabel();
    private JLabel              trophies         = new JLabel();
    private JLabel              gps              = new JLabel();
    private JLabel              wins             = new JLabel();
    private JLabel              pointsBonus      = new JLabel();
    private JLabel              podiumBonus      = new JLabel();
    private JLabel              winBonus         = new JLabel();
    private JLabel              trophyBonus      = new JLabel();

    private JLabel              overall          = new JLabel();
    private JLabel              leadership       = new JLabel();
    private JLabel              rdMech           = new JLabel();
    private JLabel              rdElect          = new JLabel();
    private JLabel              rdAero           = new JLabel();
    private JLabel              experience       = new JLabel();
    private JLabel              pitCoord         = new JLabel();
    private JLabel              motivation       = new JLabel();
    private JLabel              age              = new JLabel();

    private JLabel              zsWings          = new JLabel();
    private JLabel              zsEngine         = new JLabel();
    private JLabel              zsBrakes         = new JLabel();
    private JLabel              zsGear           = new JLabel();
    private JLabel              zsSuspension     = new JLabel();

    public TechDirectorPanel(GPROManFrame gproManFrame,
                             DataService dataService) {
        super( gproManFrame,
               dataService );
        setLayout( new BoxLayout( this, BoxLayout.LINE_AXIS ) );

        FormLayout layout = new FormLayout( "right:max(40dlu;p), 4dlu, 80dlu, 7dlu ", // 1st major column
        "" );

        // add rows dynamically
        DefaultFormBuilder builder = new DefaultFormBuilder( layout );
        builder.border( Borders.DIALOG );
        builder.appendSeparator( "Diretor Técnico: " );

        JLabel lbl = builder.append( "Nome: ", tdName );
        Font bold = lbl.getFont().deriveFont( Font.BOLD );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Nacionalidade: ", nationality );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Contrato: ", contract );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Salário: ", salary );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Bônus por pontos: ", pointsBonus );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Bônus por pódios: ", podiumBonus );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Bônus por vitórias: ", winBonus );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Bônus por troféu: ", trophyBonus );
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

        JPanel panel = builder.getPanel();
        panel.setMaximumSize( new Dimension( panel.getPreferredSize().width, panel.getMaximumSize().height ) );

        add( panel );

        // Building the second column
        layout = new FormLayout( "right:max(40dlu;p), 4dlu, 20dlu, 4dlu, 70dlu, 7dlu ", "" );

        builder = new DefaultFormBuilder( layout );
        builder.border( Borders.DIALOG );
        builder.appendSeparator( "Atributos: " );
        
        lbl = builder.append( "Total: ", overall );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Liderança: ", leadership );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "P&D Mecânico: ", rdMech );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "P&D Eletrônico: ", rdElect );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "P&D Aerodinâmico: ", rdAero );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Experiência: ", experience );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Coordenação de pit: ", pitCoord );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Motivação: ", motivation );
        lbl.setFont( bold );
        builder.nextLine();

        lbl = builder.append( "Idade: ", age );
        lbl.setFont( bold );
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

        lbl = new JLabel( "AVISO: o valor da ZS do diretor técnico é" );
        Font italic = lbl.getFont().deriveFont( Font.ITALIC );
        lbl.setFont( italic );
        builder.append( lbl, 5 );
        builder.nextLine();
        lbl = new JLabel( "subtraído da ZS do piloto para calcular a" );
        lbl.setFont( italic );
        builder.append( lbl, 5 );
        builder.nextLine();
        lbl = new JLabel( "ZS efetiva para os treinos." );
        lbl.setFont( italic );
        builder.append( lbl, 5 );
        builder.nextLine();

        panel = builder.getPanel();
        panel.setMaximumSize( new Dimension( panel.getPreferredSize().width, panel.getMaximumSize().height ) );

        add( panel );

    }

    public void updateTechDirector(TechDirector td) {
        if( td != null ) {
            tdName.setText( td.getName() );
            nationality.setText( td.getNationality() );
            salary.setText( salFormatter.format( new Object[]{td.getSalary()} ) );
            contract.setText( td.getContract() + (td.getContract() == 1 ? " corrida" : " corridas") );
            trophies.setText( String.valueOf( td.getTrophies() ) );
            gps.setText( String.valueOf( td.getGps() ) );
            wins.setText( String.valueOf( td.getWins() ) );
            pointsBonus.setText( salFormatter.format( new Object[]{ td.getPointsBonus() } ) );
            podiumBonus.setText( salFormatter.format( new Object[]{ td.getPodiumBonus() } ) );
            winBonus.setText( salFormatter.format( new Object[]{ td.getWinBonus() } ) );
            trophyBonus.setText( salFormatter.format( new Object[]{ td.getTrophyBonus() } ) );

            TDAttributes attr = td.getAttributes();
            overall.setText( String.valueOf( attr.getOverall() ) );
            leadership.setText( String.valueOf( attr.getLeadership() ) );
            rdMech.setText( String.valueOf( attr.getRdMech() ) );
            rdElect.setText( String.valueOf( attr.getRdElect() ) );
            rdAero.setText( String.valueOf( attr.getRdAero() ) );
            experience.setText( String.valueOf( attr.getExperience() ) );
            pitCoord.setText( String.valueOf( attr.getPitCoord() ) );
            motivation.setText( String.valueOf( attr.getMotivation() ) );
            age.setText( String.valueOf( attr.getAge() ) );

            zsWings.setText( String.valueOf( td.getWingsSZ() ) );
            zsEngine.setText( String.valueOf( td.getEngineSZ() ) );
            zsBrakes.setText( String.valueOf( td.getBrakesSZ() ) );
            zsGear.setText( String.valueOf( td.getGearboxSZ() ) );
            zsSuspension.setText( String.valueOf( td.getSuspensionSZ() ) );
        } else {
            tdName.setText( "" );
            nationality.setText( "" );
            salary.setText( "" );
            contract.setText( "" );
            trophies.setText("");
            gps.setText( "" );
            wins.setText( "" );
            pointsBonus.setText( "" );
            podiumBonus.setText( "" );
            winBonus.setText( "" );
            trophyBonus.setText( "" );

            overall.setText( "" );
            leadership.setText( "" );
            rdMech.setText( "" );
            rdElect.setText( "" );
            rdAero.setText( "" );
            experience.setText( "" );
            pitCoord.setText( "" );
            motivation.setText( "" );
            age.setText( "" );

            zsWings.setText( "" );
            zsEngine.setText( "" );
            zsBrakes.setText( "" );
            zsGear.setText( "" );
            zsSuspension.setText( "" );
        }
    }

    @Override
    public void update() {
        if ( isDirty() ) {
            Race nextRace = db.getNextRace();
            if ( nextRace != null ) {
                TechDirector td = nextRace.getTDStart();
                updateTechDirector( td );
            }
            setDirty( false );
        }
    }

    @Override
    public String getTitle() {
        return "Diretor Téc. ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon( "/icons/td_32.png" );
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon( "/icons/td_16.png" );
    }

    @Override
    public String getDescription() {
        return "Informações sobre o Diretor Técnico";
    }

    @Override
    public Category getCategory() {
        return Category.TEAM;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_D;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
