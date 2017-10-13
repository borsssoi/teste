package org.gproman.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gproman.model.UserConfiguration;
import org.gproman.model.UserCredentials;
import org.gproman.model.car.CarWearData;
import org.gproman.model.driver.DriverWearWeight;
import org.gproman.model.track.Downforce;
import org.gproman.model.track.FuelConsumption;
import org.gproman.model.track.Overtaking;
import org.gproman.model.track.SuspensionRigidity;
import org.gproman.model.track.Track;
import org.gproman.model.track.TrackWearFactors;
import org.gproman.model.track.TyreWear;
import org.gproman.model.track.WearCoefs;
import org.gproman.scrapper.GPROUtil;
import org.gproman.scrapper.PageLoader;
import org.gproman.scrapper.TrackInfoWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.thoughtworks.xstream.XStream;

public class CarWearSpreadsheetLoader {

    private static final String TRACK_URL_SUFFIX   = "/gb/TrackDetails.asp?id=";
    private static final int    TRACK_COUNT        = 60;

    private static final String SOURCE_SPREADSHEET = "resources/Car_wear_prediction_v3.xlsx";
    private static final String ADD_SPREADSHEET    = "resources/TrackAdditionalData.csv";
    private static final String XML_FILENAME       = "src/main/resources/refdata.bin";
    private final static Logger logger             = LoggerFactory.getLogger(CarWearSpreadsheetLoader.class);

    private static final byte[] s                  = new byte[]{25, 32, 87, 110, 87, 34, 76, 29};

    public void serializeToXML(CarWearData cws, OutputStream out, boolean encrypt) throws InvalidKeyException,
            InvalidKeySpecException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            IOException {
        if (encrypt) {
            SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(s));
            Cipher encrypter = Cipher.getInstance("DES/ECB/PKCS5Padding");
            encrypter.init(Cipher.ENCRYPT_MODE, secretKey);

            out = new CipherOutputStream(out, encrypter);
        }

        XStream xstream = getXStream();
        xstream.toXML(cws, out);

        out.close();
        logger.info(cws.getTracks().size() + " tracks successfully serialized to file.");
    }

    public CarWearData deserializeFromXML(InputStream in) throws InvalidKeyException,
            InvalidKeySpecException,
            NoSuchAlgorithmException,
            NoSuchPaddingException {
        SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(s));

        Cipher decrypter = Cipher.getInstance("DES/ECB/PKCS5Padding");
        decrypter.init(Cipher.DECRYPT_MODE, secretKey);

        CipherInputStream cin = new CipherInputStream(in, decrypter);

        XStream xstream = getXStream();
        return (CarWearData) xstream.fromXML(cin);
    }

    private XStream getXStream() {
        XStream xstream = new XStream();
        xstream.alias("carWearData", CarWearData.class);
        xstream.alias("track", Track.class);
        return xstream;
    }

    public CarWearData loadSourceSpreadsheet(InputStream input, InputStream addDataInput, boolean online) {
        logger.info("Loading track data from original datasource.");
        try {
            CarWearData cwd = new CarWearData();
            XSSFWorkbook workbook = new XSSFWorkbook(input);
            Map<Integer, CSVRow> rows = readRows(addDataInput);

            List<Track> tracks = null;
            if (online) {
                tracks = loadTrackListFromWebsite();
            } else {
                tracks = loadTrackListFromSpreadsheet(workbook);
            }
            cwd.setTracks(tracks);

            loadWearPrediction(workbook, tracks, cwd);
            loadTrackAdditionalData(tracks, rows);

            return cwd;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private void loadWearPrediction(XSSFWorkbook workbook,
            List<Track> tracks,
            CarWearData cwd) {
        XSSFSheet wearPred = workbook.getSheet("Car-wear-prediction");

        cwd.setDriverattributsWearWeight(loadDriverAttrWearWeight(cwd, wearPred));

        cwd.setWearPolinomialCoef(loadWearPolinomialCoefs(wearPred));

        for (int r = 5;; r++) {
            XSSFRow row = wearPred.getRow(r);
            int id = getInt(row, 0, false);
            if (id == 0) {
                break;
            }
            TrackWearFactors wear = new TrackWearFactors();

            for (Track track : tracks) {
                if (track.getId() == id) {
                    track.setWearFactors(wear);
                    break;
                }
            }

            for (int c = 0; c < wear.getFactors().length; c++) {
                wear.getFactors()[c] = getBigDecimal(row, 9 + c);
            }
        }
    }

    private void loadTrackAdditionalData(List<Track> tracks, Map<Integer, CSVRow> rows) {
        for (Track track : tracks) {
            CSVRow row = rows.get(track.getId());
            track.setCompoundCoef(row.compoundCoef);
            track.setFuelCoef(row.fuelCoef);
            track.setSetupWings(row.setupWings);
            track.setSetupEngine(row.setupEngine);
            track.setSetupBrakes(row.setupBrakes);
            track.setSetupGear(row.setupGear);
            track.setSetupSuspension(row.setupSuspension);
            track.setWingSplit(row.wingSplit);
            track.setWingNormal(row.wingNormal);
            track.setFCon(row.fCon);
            track.setFAgr(row.fAgr);
            track.setFExp(row.fExp);
            track.setFTeI(row.fTeI);
            track.setFEng(row.fEng);
            track.setFEle(row.fEle);
            track.setFHum(row.fHum);
            track.setFFue(row.fFue);
            logger.info("Track data enriched = " + track);
        }
    }

    private Map<Integer, CSVRow> readRows(InputStream addData) throws IOException {
        Map<Integer, CSVRow> rows = new HashMap<Integer, CSVRow>();
        BufferedReader bis = new BufferedReader(new InputStreamReader(addData));
        bis.readLine(); // title
        String line = null;
        while ((line = bis.readLine()) != null) {
            String[] split = line.replaceAll(",", ", ").split(",");
            CSVRow row = new CSVRow();
            row.id = Integer.parseInt(split[0].trim());
            row.name = split[1].trim();
            row.compoundCoef = getBigDecimal(split[2].trim());
            row.fuelCoef = getBigDecimal(split[3].trim());
            row.setupWings = getInteger(split[4].trim());
            row.setupEngine = getInteger(split[5].trim());
            row.setupBrakes = getInteger(split[6].trim());
            row.setupGear = getInteger(split[7].trim());
            row.setupSuspension = getInteger(split[8].trim());
            row.wingSplit = getInteger(split[9].trim());
            row.wingNormal = "Y".equalsIgnoreCase(split[10].trim());
            row.fCon = getInteger(split[11].trim());
            row.fAgr = getInteger(split[12].trim());
            row.fExp = getInteger(split[13].trim());
            row.fTeI = getInteger(split[14].trim());
            row.fEng = getInteger(split[15].trim());
            row.fEle = getInteger(split[16].trim());
            row.fHum = getDouble(split[17].trim());
            row.fFue = getDouble(split[18].trim());
            rows.put(row.id, row);
        }
        return rows;
    }

    private boolean getBoolean(String si) {
        if (si != null && !si.isEmpty()) {
            return "Y".equalsIgnoreCase(si);
        }
        return false;
    }

    private Integer getInteger(String si) {
        if (si != null && !si.isEmpty()) {
            return new Integer(si);
        }
        return null;
    }

    private Double getDouble(String si) {
        if (si != null && !si.isEmpty()) {
            return new Double(si);
        }
        return null;
    }

    private BigDecimal getBigDecimal(String bd) {
        if (bd != null && !bd.isEmpty()) {
            return new BigDecimal(bd);
        }
        return null;
    }

    private static class CSVRow {
        public Integer    id;
        public String     name;
        public BigDecimal compoundCoef;
        public BigDecimal fuelCoef;
        public Integer    setupWings;
        public Integer    setupEngine;
        public Integer    setupBrakes;
        public Integer    setupGear;
        public Integer    setupSuspension;
        public Integer    wingSplit;
        public boolean    wingNormal;
        public Integer    fCon;
        public Integer    fAgr;
        public Integer    fExp;
        public Integer    fTeI;
        public Integer    fEng;
        public Integer    fEle;
        public Double     fHum;
        public Double     fFue;
    }

    private DriverWearWeight loadDriverAttrWearWeight(CarWearData cwd,
            XSSFSheet wearPred) {
        XSSFRow wr = wearPred.getRow(4);
        DriverWearWeight dww = new DriverWearWeight();
        dww.setConcentration(BigDecimal.valueOf(getDouble(wr, 2)));
        dww.setTalent(BigDecimal.valueOf(getDouble(wr, 3)));
        dww.setAggressiveness(BigDecimal.valueOf(getDouble(wr, 4)));
        dww.setExperience(BigDecimal.valueOf(getDouble(wr, 5)));
        dww.setStamina(BigDecimal.valueOf(getDouble(wr, 6)));
        return dww;
    }

    private WearCoefs loadWearPolinomialCoefs(XSSFSheet wearPred) {
        XSSFRow cr = wearPred.getRow(6);
        WearCoefs pc = new WearCoefs();
        for (int c = 0; c < pc.getCoefs().length; c++) {
            pc.getCoefs()[c] = getBigDecimal(cr, 32 + c);
        }
        return pc;
    }

    private List<Track> loadTrackListFromSpreadsheet(XSSFWorkbook workbook) {
        XSSFSheet trackList = workbook.getSheet("Track_list");
        List<Track> tracks = new ArrayList<Track>();
        for (int r = 2;; r++) {
            XSSFRow row = trackList.getRow(r);
            int id = getInt(row, 1, false);
            if (id == 0) {
                break;
            }

            Track track = new Track();
            track.setId(id);
            track.setName(getString(row, 2));
            track.setDownforce(getDownforce(row, 3));
            track.setOvertaking(getOvertaking(row, 4));
            track.setSuspension(getSuspension(row, 5));
            track.setFuelConsumption(getFuelConsumption(row, 6));
            track.setTyreWear(getTyreWear(row, 7));

            track.setLapDistance(getDouble(row, 8));
            track.setLaps(getInt(row, 9, true));
            track.setDistance(getDouble(row, 10));
            track.setPower(getInt(row, 11, true));
            track.setHandling(getInt(row, 12, true));
            track.setAcceleration(getInt(row, 13, true));
            track.setAvgSpeed(getDouble(row, 14));
            track.setCorners(getInt(row, 15, true));
            track.setTimeInOut((int) (getDouble(row, 16) * 1000));
            tracks.add(track);
        }
        return tracks;
    }

    private List<Track> loadTrackListFromWebsite() throws IOException {
        UserCredentials credentials = CredentialsManager.loadCredentials();
        UserConfiguration configuration = ConfigurationManager.loadConfiguration();
        logger.info("Credentials loaded for user: " + credentials.getGproUser());
        GPROUtil util = new GPROUtil(credentials, configuration);
        List<Track> tracks = new ArrayList<Track>();
        util.login();
        HtmlPage[] pages = new HtmlPage[TRACK_COUNT];
        for (int i = 1; i <= TRACK_COUNT; i++) {
            try {
                logger.info("Retrieving track " + TRACK_URL_SUFFIX + i);
                PageLoader loader = new PageLoader(util.getWebConnection().clone(), util.getWebConnection().getConf().getGproUrl() + TRACK_URL_SUFFIX + i);
                pages[i - 1] = loader.call();
            } catch (Exception e) {
                logger.error("Error fetching track id=" + i, e);
            }
        }
        for (int i = 1; i <= TRACK_COUNT; i++) {
            try {
                logger.info("Parsing track " + TRACK_URL_SUFFIX + i);
                TrackInfoWorker tiw = new TrackInfoWorker(pages[i - 1]);
                Track track = tiw.call();
                tracks.add(track);
                logger.info("Track " + track.getName() + " loaded.");
            } catch (Exception e) {
                logger.error("Error parsing track id=" + i, e);
            }
        }
        util.logout();
        return tracks;
    }

    private Downforce getDownforce(XSSFRow row,
            int col) {
        Downforce val = Downforce.fromString(getString(row, col));
        if (val == null) {
            logger.warn("Unable to parse downforce value from column " + col + " and row " + row);
        }
        return val;
    }

    private Overtaking getOvertaking(XSSFRow row,
            int col) {
        Overtaking val = Overtaking.fromString(getString(row, col));
        if (val == null) {
            logger.warn("Unable to parse overtaking value from column " + col + " and row " + row);
        }
        return val;
    }

    private SuspensionRigidity getSuspension(XSSFRow row,
            int col) {
        SuspensionRigidity val = SuspensionRigidity.fromString(getString(row, col));
        if (val == null) {
            logger.warn("Unable to parse suspension rigidity value from column " + col + " and row " + row);
        }
        return val;
    }

    private FuelConsumption getFuelConsumption(XSSFRow row,
            int col) {
        FuelConsumption val = FuelConsumption.fromString(getString(row, col));
        if (val == null) {
            logger.warn("Unable to parse fuel consumption value from column " + col + " and row " + row);
        }
        return val;
    }

    private TyreWear getTyreWear(XSSFRow row,
            int col) {
        TyreWear val = TyreWear.fromString(getString(row, col));
        if (val == null) {
            logger.warn("Unable to parse tyre wear value from column " + col + " and row " + row);
        }
        return val;
    }

    private BigDecimal getBigDecimal(Row row,
            int col) {
        try {
            if (row.getCell(col) != null && row.getCell(col).getCellType() != Cell.CELL_TYPE_BLANK) {
                if (row.getCell(col).getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                    return new BigDecimal(row.getCell(col).getNumericCellValue());
                } else {
                    return new BigDecimal(row.getCell(col).getStringCellValue());
                }
            } else {
                logger.error("Non-existent cell for column " + col + " on row " + row);
            }
        } catch (Exception e) {
            logger.warn("Unable to parse big decimal value '" + row.getCell(col).getStringCellValue() + "' from column " + col + " and row " + row);
        }
        return BigDecimal.ZERO;
    }

    private Integer getIntOrNull(Row row,
            int col) {
        try {
            if (row.getCell(col) != null && row.getCell(col).getCellType() != Cell.CELL_TYPE_BLANK) {
                if (row.getCell(col).getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                    return new Integer((int) row.getCell(col).getNumericCellValue());
                } else {
                    return new Integer(row.getCell(col).getStringCellValue());
                }
            }
        } catch (Exception e) {
            logger.warn("Unable to parse Integer value '" + row.getCell(col).getStringCellValue() + "' from column " + col + " and row " + row);
        }
        return null;
    }

    private boolean getBoolean(Row row,
            int col) {
        try {
            if (row.getCell(col) != null && row.getCell(col).getCellType() != Cell.CELL_TYPE_BLANK) {
                if (row.getCell(col).getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
                    return row.getCell(col).getBooleanCellValue();
                } else {
                    return "Y".equalsIgnoreCase(row.getCell(col).getStringCellValue());
                }
            }
        } catch (Exception e) {
            logger.warn("Unable to parse boolean value '" + row.getCell(col).getStringCellValue() + "' from column " + col + " and row " + row);
        }
        return false;
    }

    private String getString(XSSFRow row,
            int col) {
        try {
            return row.getCell(col).getStringCellValue();
        } catch (Exception e) {
            logger.warn("Unable to parse string value from column " + col + " and row " + row);
            return null;
        }
    }

    private double getDouble(XSSFRow row,
            int col) {
        try {
            return row.getCell(col).getNumericCellValue();
        } catch (Exception e) {
            logger.warn("Unable to parse double value from column " + col + " and row " + row);
            return 0;
        }
    }

    private int getInt(Row row,
            int col,
            boolean reportError) {
        try {
            return (int) row.getCell(col).getNumericCellValue();
        } catch (Exception e) {
            if (reportError) {
                logger.warn("Unable to parse int value from column " + col + " and row " + row);
            }
            return 0;
        }
    }

    public static void main(String[] args) {
        CarWearSpreadsheetLoader loader = new CarWearSpreadsheetLoader();
        FileInputStream sourceWearInput = null;
        FileInputStream addDataInput = null;
        FileOutputStream out = null;
        try {
            logger.info("Importing car wear data from original spreadsheet '" + SOURCE_SPREADSHEET + "'");
            sourceWearInput = new FileInputStream(SOURCE_SPREADSHEET);
            addDataInput = new FileInputStream(ADD_SPREADSHEET);

            CarWearData data = loader.loadSourceSpreadsheet(sourceWearInput, addDataInput, true);
            logger.info("Serializing imported data to XML file '" + XML_FILENAME + "'");
            out = new FileOutputStream(XML_FILENAME);
            loader.serializeToXML(data, out, true);
            logger.info("XML file successfully created.");
        } catch (Exception e) {
            logger.error("Error creating data file.", e);
        } finally {
            if (sourceWearInput != null) {
                try {
                    sourceWearInput.close();
                } catch (IOException e) {
                }
            }
            if (addDataInput != null) {
                try {
                    addDataInput.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
