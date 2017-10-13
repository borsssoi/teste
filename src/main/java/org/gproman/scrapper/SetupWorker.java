package org.gproman.scrapper;

import java.util.List;
import java.util.concurrent.Callable;

import org.gproman.scrapper.SetupWorker.SetupData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

public class SetupWorker
        implements
        Callable<SetupData> {
    private static final Logger logger = LoggerFactory.getLogger( SetupWorker.class );

    private final HtmlPage      setupPage;

    public SetupWorker(HtmlPage q1Page) {
        this.setupPage = q1Page;
    }

    @Override
    public SetupData call() {
        return parsePage( setupPage );
    }

    public SetupData parsePage(HtmlPage q1Page) {
        logger.info( "Parsing race setup and strategy..." );
        SetupData data = new SetupData();

        HtmlTable table = q1Page.getFirstByXPath( "//th[contains(text(),'Car setup')]/ancestor::table" );
        if( table != null ) {
            data.fwing = new Integer( ((HtmlInput)table.getElementById( "FWing" )).getValueAttribute().trim() );
            data.rwing = new Integer( ((HtmlInput)table.getElementById( "RWing" )).getValueAttribute().trim() );
            data.engine = new Integer( ((HtmlInput)table.getElementById( "Engine" )).getValueAttribute().trim() );
            data.brakes = new Integer( ((HtmlInput)table.getElementById( "Brakes" )).getValueAttribute().trim() );
            data.gear = new Integer( ((HtmlInput)table.getElementById( "Gear" )).getValueAttribute().trim() );
            data.suspension = new Integer( ((HtmlInput)table.getElementById( "Suspension" )).getValueAttribute().trim() );
        } else {
            logger.error( "Unable to find setup data." );
        }
        table = q1Page.getFirstByXPath( "//th[contains(text(),'Fuel strategy')]/ancestor::table" );
        if( table != null ) {
            data.startingFuel = new Integer( ((HtmlInput)table.getElementsByAttribute( "input", "name", "FuelStart" ).get( 0 )).getValueAttribute().trim() );
            String strat = ((HtmlInput)table.getElementsByAttribute( "input", "name", "FuelStop1" ).get( 0 )).getValueAttribute().trim()+"/";
            strat += ((HtmlInput)table.getElementsByAttribute( "input", "name", "FuelStop2" ).get( 0 )).getValueAttribute().trim()+"/";
            strat += ((HtmlInput)table.getElementsByAttribute( "input", "name", "FuelStop3" ).get( 0 )).getValueAttribute().trim()+"/";
            strat += ((HtmlInput)table.getElementsByAttribute( "input", "name", "FuelStop4" ).get( 0 )).getValueAttribute().trim()+"/";
            strat += ((HtmlInput)table.getElementsByAttribute( "input", "name", "FuelStop5" ).get( 0 )).getValueAttribute().trim();
            data.fuelStrategy = strat;
        } else {
            logger.error( "Unable to find fuel strategy data." );
        }
        table = q1Page.getFirstByXPath( "//th[contains(text(),'Tyres strategy')]/ancestor::table" );
        if( table != null ) {
            data.tyreAtStart = ((HtmlSelect)table.getElementsByAttribute( "select", "name", "StartTyres" ).get( 0 )).getSelectedOptions().get( 0 ).asText().trim();
            data.tyreWhenWet = ((HtmlSelect)table.getElementsByAttribute( "select", "name", "RainTyres" ).get( 0 )).getSelectedOptions().get( 0 ).asText().trim();
            data.tyreWhenDry = ((HtmlSelect)table.getElementsByAttribute( "select", "name", "DryTyres" ).get( 0 )).getSelectedOptions().get( 0 ).asText().trim();
            data.waitWhenWet = new Integer( ((HtmlInput)table.getElementsByAttribute( "input", "name", "LapsWaitPitRain" ).get( 0 )).getValueAttribute().trim() );
            data.waitWhenDry = new Integer( ((HtmlInput)table.getElementsByAttribute( "input", "name", "LapsWaitPitDry" ).get( 0 )).getValueAttribute().trim() );
        } else {
            logger.error( "Unable to find fuel strategy data." );
        }
        table = q1Page.getFirstByXPath( "//th[contains(text(),'Driver strategy')]/ancestor::table" );
        if( table != null ) {
            data.overtake = new Integer( ((HtmlInput)table.getElementsByAttribute( "input", "name", "RiskOver" ).get( 0 )).getValueAttribute().trim() );
            data.defend = new Integer( ((HtmlInput)table.getElementsByAttribute( "input", "name", "RiskDefend" ).get( 0 )).getValueAttribute().trim() );
            data.clear = new Integer( ((HtmlInput)table.getElementsByAttribute( "input", "name", "DriverRisk" ).get( 0 )).getValueAttribute().trim() );
            List<HtmlElement> list = table.getElementsByAttribute( "input", "name", "RiskWet" );
            data.clearWet = list.isEmpty() ? data.clear : new Integer( ((HtmlInput)list.get(0)).getValueAttribute().trim() ) ;
            data.malfunc = new Integer( ((HtmlInput)table.getElementsByAttribute( "input", "name", "DriverRiskProb" ).get( 0 )).getValueAttribute().trim() );
            data.startRisk = ((HtmlSelect)table.getElementsByAttribute( "select", "name", "StartRisk" ).get( 0 )).getSelectedOptions().get( 0 ).asText().trim();
        } else {
            logger.error( "Unable to find strategy data for risks." );
        }
        return data;
    }

    public static class SetupData {
        public Integer fwing;
        public Integer rwing;
        public Integer engine;
        public Integer brakes;
        public Integer gear;
        public Integer suspension;
        public Integer startingFuel;
        public String fuelStrategy;
        public Integer overtake;
        public Integer defend;
        public Integer clear;
        public Integer clearWet;
        public Integer malfunc;
        public String  startRisk;
        public String tyreAtStart;
        public String tyreWhenWet;
        public String tyreWhenDry;
        public Integer waitWhenWet;
        public Integer waitWhenDry;
        @Override
        public String toString() {
            return "SetupData [fwing=" + fwing + ", rwing=" + rwing + ", engine=" + engine + ", brakes=" + brakes + ", gear=" + gear + ", suspension=" + suspension + ", startingFuel=" + startingFuel + ", fuelStrategy=" + fuelStrategy + ", overtake=" + overtake + ", defend=" + defend + ", clear=" + clear + ", malfunc=" + malfunc + ", startRisk=" + startRisk + ", tyreAtStart=" + tyreAtStart + ", tyreWhenWet=" + tyreWhenWet + ", tyreWhenDry=" + tyreWhenDry + ", waitWhenWet=" + waitWhenWet + ", waitWhenDry=" + waitWhenDry + "]";
        }
    }

}