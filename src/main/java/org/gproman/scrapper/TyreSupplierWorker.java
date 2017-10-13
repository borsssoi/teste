package org.gproman.scrapper;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.gproman.model.season.TyreSupplier;
import org.gproman.model.season.TyreSupplierAttrs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlHeading2;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class TyreSupplierWorker
        implements
        Callable<TyreSupplierWorkerResult> {

    private static final Logger    logger = LoggerFactory.getLogger(TyreSupplierWorker.class);
    private final Future<HtmlPage>  page;

    public TyreSupplierWorker(Future<HtmlPage> supPage) {
        this.page = supPage;
    }

    @Override
    public TyreSupplierWorkerResult call() {
        try {
            return parsePage(page.get());
        } catch (Exception e) {
            logger.error("Error downloading tyre supplier page.", e);
        }
        return null;
    }

    public TyreSupplierWorkerResult parsePage(HtmlPage page) {
        logger.info("Parsing Tyre Supplier page.");
        TyreSupplierWorkerResult result = new TyreSupplierWorkerResult();
        
        for( TyreSupplier supplier : TyreSupplier.values() ) {
            HtmlDivision div = page.getFirstByXPath("//h2[contains(text(),'"+supplier.toString()+"')]/ancestor::div[1]");
            if( div != null ) {
                TyreSupplierAttrs attrs = new TyreSupplierAttrs();
                attrs.setSupplier( supplier );
                HtmlTable table = div.getFirstByXPath(".//table");
                if( table != null ) {
                    for( HtmlTableRow row : table.getRows() ) {
                        if( "Dry Performance:".equalsIgnoreCase( row.getCell(0).getTextContent().trim() ) ) {
                            String value = row.getCell(1).getAttribute("title");
                            attrs.setDry( value != null ? new Integer(value) : Integer.valueOf(0));
                        } else if( "Wet performance:".equalsIgnoreCase( row.getCell(0).getTextContent().trim() ) )  {
                            String value = row.getCell(1).getAttribute("title");
                            attrs.setWet( value != null ? new Integer(value) : Integer.valueOf(0));
                        } else if( "Peak temperature:".equalsIgnoreCase( row.getCell(0).getTextContent().trim() ) )  {
                            String value = row.getCell(1).getTextContent().trim();
                            attrs.setPeak( value != null ? new Integer(value.substring(0, value.indexOf('°'))) : Integer.valueOf(0));
                        } else if( "Durability:".equalsIgnoreCase( row.getCell(0).getTextContent().trim() ) )  {
                            String value = row.getCell(1).getAttribute("title");
                            attrs.setDurability( value != null ? new Integer(value) : Integer.valueOf(0));
                        } else if( "Warmup distance:".equalsIgnoreCase( row.getCell(0).getTextContent().trim() ) )  {
                            String value = row.getCell(1).getAttribute("title");
                            attrs.setWarmup( value != null ? new Integer(value) : Integer.valueOf(0));
                        }
                    }
                }
                HtmlParagraph cost = div.getFirstByXPath(".//p[contains(text(), 'Cost per race:')]");
                String coststr = cost != null ? cost.asText().trim() : null;
                attrs.setCost( coststr != null ? new Integer( coststr.substring( coststr.indexOf('$')+1 ).replaceAll("\\.", "") ) : Integer.valueOf(0) );
                result.suppliers.add(attrs);
            }
        }
        HtmlHeading2 h2 = page.getFirstByXPath("//b[contains(text(),'Contract active')]/ancestor::div[1]/h2");
        result.signed = h2 != null ? TyreSupplier.determineTyre(h2.getTextContent().trim()) : null;

        logger.info("Tyre supplier successfully parsed. Contract " + (result.signed != null ? "with "+result.signed : "not signed yet."));
        return result;
    }

}