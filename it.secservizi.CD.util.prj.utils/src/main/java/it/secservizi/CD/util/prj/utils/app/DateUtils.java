package it.secservizi.CD.util.prj.utils.app;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {
	private static final Logger log = LoggerFactory.getLogger(DateUtils.class);

	
	public static final String DATE_FORMAT_SORTABLE = "yyyyMMdd-HHmmss.SSS";
	public static final String DATE_FORMAT_SIMPLE = "yyyyMMdd-HHmmss";
	
	/**
	 * provvisorio prima della corretta versione in java 8
	 * @param date
	 * @return
	 */
	public static Timestamp getStartOfDay(Timestamp timestamp) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(timestamp);
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    return new Timestamp(calendar.getTime().getTime());
	}
	
	
	/**
	 * Un timestamp formattato sortabile
	 * @param timestamp
	 * @return
	 */
	public static String getDateSortableFormatText(Timestamp timestamp) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_SORTABLE);
		
		return simpleDateFormat.format(new Date(timestamp.getTime()));
	}
	
	/**
	 * adesso in formato sortabile yyyyMMdd-HHmmss.SSS
	 * 
	 * @return
	 */
	
	public static String getDateSortableFormatNow() {
		
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		
		return DateUtils.getDateSortableFormatText(new Timestamp(date.getTime()));		
	}
	
	/**
	 * Un timestamp formattato sortabile
	 * @param timestamp
	 * @return
	 */
	public static String getDateSimpleFormatText(Timestamp timestamp) {
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_SIMPLE);
		
		return simpleDateFormat.format(new Date(timestamp.getTime()));
	}


	/**
	 * adesso in formato simple yyyyMMdd-HHmmss
	 * 
	 * @return
	 */
	
	public static String getDateSimpleFormatNow() {
		
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		
		return DateUtils.getDateSimpleFormatText(new Timestamp(date.getTime()));		
	}
	
	
	public static Timestamp getNow() {
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		
		return new Timestamp(date.getTime());
	}
	
	/**
	 * 
	 * riceve stringa in formato "yyyyMMdd-HHmmss.SSS" e la trasforma in timestamp
	 * 
	 * @param dateText
	 * @return timestamp
	 * @throws ParseException 
	 */
	public static Timestamp getTimestamp(String dateText) throws ParseException {
		
		String pattern = DATE_FORMAT_SORTABLE;
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date date = null;
		try {
			date = simpleDateFormat.parse(dateText);
		} catch (ParseException e) {
			log.error("impossibile convertire =>" + dateText + "<= in pattern =>" + pattern + "<=");
			throw e;
		}
		return new Timestamp(date.getTime());
		
	}
	
	/**
	 * aggiunge (o toglie se negativo) giorni ad un certo timestamp
	 * @param timestamp
	 * @param days
	 * @return
	 */
	public static Timestamp addDays(Timestamp timestamp, int days) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timestamp);
		calendar.add(Calendar.DAY_OF_WEEK, days);


		return new Timestamp(calendar.getTime().getTime());
	}

	// solo per sample
	public static void main (String[] args) {

		//ALTERNATIVA Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Timestamp timestamp = DateUtils.getNow();
		
		log.trace("now = " + timestamp);
		log.trace("now formatted = " + DateUtils.getDateSortableFormatText(timestamp));
		
		log.trace("Timestamp to string = " + timestamp);
		
		String sortableTimestamp = DateUtils.getDateSortableFormatText(timestamp);
		log.trace("Dopo conversione a sortable = " + sortableTimestamp);
		
		int days = 10;
		log.trace("Aggiungo " + days + " giorni : " + DateUtils.addDays(timestamp, days));
		
		days = -10;
		log.trace("Aggiungo " + days + " giorni : " + DateUtils.addDays(timestamp, days));
		

		log.trace("Inizio giornata = " + DateUtils.getStartOfDay(timestamp));
	}
}
