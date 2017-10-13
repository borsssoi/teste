package org.gproman.scrapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.gproman.db.DataService;
import org.gproman.ui.GPROManFrame;

/**
 * A class to aggregate the data scrapping methods
 * for the website
 *   
 */
public class GPROScrapper {

    private static final int    NTHREDS   = 5;
    private final DataService   db;
    private final GPROManFrame  frame;

    public GPROScrapper(GPROManFrame frame,
                        DataService db) {
        this.frame = frame;
        this.db = db;
    }

    public void fetchGPROData() {
        ExecutorService executor = Executors.newFixedThreadPool( NTHREDS );

        DataWorker worker = new DataWorker( frame, 
                                            db, 
                                            executor,
                                            frame.getGPRO(),
                                            frame.getGPROBr() );

        executor.execute( worker );
    }

}
