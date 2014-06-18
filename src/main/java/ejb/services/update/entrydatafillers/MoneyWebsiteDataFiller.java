/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.services.update.entrydatafillers;


import ejb.services.update.entrydatafillers.interfaces.StockDataFiller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import jpa.*;

/**
 *
 * @author dawid
 */
@Dependent
@MoneyFiller
public class MoneyWebsiteDataFiller implements StockDataFiller {

    private BufferedReader reader;
    private static final String[] URL_PARTS = {"http://www.money.pl/gielda/archiwum/", ",strona,", ".html"};
    private int site = 1;
    private PricesHolder shareData;
    private static final String SHARE = "spolki";
    private static final String INDEX = "indeksy";
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private String targetValorType;
    private Date beginDate;

    public MoneyWebsiteDataFiller() {
    }

    private static boolean isToday(Date date) {
        Calendar thatDay = Calendar.getInstance();
        thatDay.setTime(date);
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());

        return thatDay.get(Calendar.YEAR) == today.get(Calendar.YEAR) && thatDay.get(Calendar.MONTH) == today.get(Calendar.MONTH) && thatDay.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);

    }

    private boolean isUpdateNeeded() {
        boolean isUpdateNeeded = !isToday(beginDate);
        if (!isUpdateNeeded) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "data update not needed for " + this.shareData.getName() + " (" + this.shareData.getShortName() + ")");
        }
        return isUpdateNeeded;
    }

    private String combineUrlString() {

        return URL_PARTS[0] + this.targetValorType + "/" + formatter.format(this.beginDate) + "," + formatter.format(new Date()) + "," + this.shareData.getShortName() + URL_PARTS[1] + this.site + URL_PARTS[2];
    }

    private void fillSingleSiteShareData() throws IOException {
        this.openConnection();
        if (!this.findFirstDataRow()) {
            throw new NoSuchElementException("Data rows in: " + this.combineUrlString() + " are in unexpected format or not present at all");
        }
        String[] rowData;
        while ((rowData = this.getNextDayData()) != null) {
            try {
                DailyPrices prices = paseRowDataToObject(rowData);
                if (prices.getDate().after(this.beginDate)) {
                    this.shareData.addDailyData(prices);
                }
            } catch (ParseException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "error parsing rowData: " + ex.getMessage());
            }
        }
        this.closeConnection();
    }

    @Override
    public void fillDataInIndex(StockIndex index) throws IOException {
        fillShareData(index);
        for (Share s : index.getShares()) {
            fillShareData(s);
        }
    }

    private void adjustCorrectTargetValorType() {
        if (this.shareData instanceof Share) {
            this.targetValorType = SHARE;
        } else if (this.shareData instanceof StockIndex) {
            this.targetValorType = INDEX;
        }
    }

    private void readShareDate() {
        Date time = this.shareData.getLastPriceData();
        if (time == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.YEAR, -100);
            time = cal.getTime();
        }
        this.beginDate = time;
    }

    @Override
    public void fillShareData(PricesHolder shareData) throws IOException {
        this.shareData = shareData;
        this.readShareDate();
        if (!this.isUpdateNeeded()) {
            this.shareData = null;
            return;
        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "filling data of : " + shareData.getName() + " (" + shareData.getShortName() + ")");
        this.adjustCorrectTargetValorType();
        this.site = 1;
        this.setNumberOfSubsites();
        do {
            this.fillSingleSiteShareData();
            this.previousSite();
        } while (this.hasPrevious());
        this.beginDate=null;
        this.shareData = null;
    }

    private void openConnection() throws IOException {
        String urlFormat = this.combineUrlString();
        URL moneySite = new URL(urlFormat);
        this.reader = new BufferedReader(
                new InputStreamReader(moneySite.openStream()));
    }

    private void setNumberOfSubsites() throws IOException {
        int number = 1;
        this.openConnection();
        Pattern p = Pattern.compile(".*<a\\s+href=.*>(\\d{1,})</a>.*");
        Matcher m;
        String line = null;
        while ((line = this.reader.readLine()) != null && !line.matches("<div class=\"pager\">.*"));
        while ((line = this.reader.readLine()) != null && !line.matches("</div>$")) {
            m = p.matcher(line);
            if (m.matches()) {
                number = Integer.parseInt(m.group(1));
            }
        }
        this.site = number;
        this.closeConnection();
    }

    private boolean hasPrevious() {
        return this.site > 0;
    }

    private void previousSite() {
        this.site--;
    }

    private void closeConnection() {
        if (this.reader != null) {
            try {
                this.reader.close();
            } catch (IOException ex) {
                Logger.getLogger(MoneyWebsiteDataFiller.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                this.reader = null;
            }
        }
    }

    private boolean findFirstDataRow() throws IOException {
        String line;
        boolean founded = false;
        while ((line = reader.readLine()) != null) {
            if (!line.contains("<div class=\"corner box\">")) {
                continue;
            } else {
                while ((line = reader.readLine()) != null) {
                    if (line.contains("</thead>")) {
                        founded = true;
                        break;
                    }
                }
                break;
            }
        }
        return founded;
    }

    private static DailyPrices paseRowDataToObject(String[] arrayData) throws ParseException {
        DailyPrices data = new DailyPrices();
        data.setDate(formatter.parse(arrayData[0]));
        data.setOpen(parseToFloat(arrayData[1]));
        data.setMinimal(parseToFloat(arrayData[2]));
        data.setMaximal(parseToFloat(arrayData[3]));
        data.setClose(parseToFloat(arrayData[4]));
        data.setChange(parseToFloat(arrayData[5]));
        data.setSales((long) (parseToFloat(arrayData[6])*1000000));
        return data;
    }

    private static float parseToFloat(String num) {
        if (num == null) {
            return 0.0F;
        }
        return Float.parseFloat(num.replace(',', '.'));
    }

    private String[] getNextDayData() throws IOException {
        String[] data = new String[7];
        String line;
        Pattern p = Pattern.compile(".*>([+-]*\\d{1,},\\d{2}|\\d{4}-\\d{2}-\\d{2})<.*");
        line = reader.readLine();
        if (!line.contains("<tr >")) {
            return null;
        }
        for (int i = 0; i < 7 && !line.contains("</tr>"); i++) {
            line = reader.readLine();
            Matcher m = p.matcher(line);
            if (!m.matches()) {
                i--;
                continue;
            }
            data[i] = m.group(1);

        }
        while (!(line = reader.readLine()).contains("</tr>"));
        return data;
    }

}
