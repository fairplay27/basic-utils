package it.secservizi.CD.util.prj.utils.app;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * esempi con idee da stackoverflow per tradurre stringhe in hex e viceversa
 * 
 * @author Stefano_Antoniazzi
 *
 */
public class HexConvertUtils {
	private static final Logger log = LoggerFactory.getLogger(HexConvertUtils.class);
	

	/**
	 * Encoding di default esempio Cp1252
	 * <br/>
	 * NOTA: "ISO-8859-1" non ha il trattino lungo (tipo word)
	 * 
	 * @return
	 */
	public static String getFileEncoding() {
		return System.getProperty("file.encoding");
	}
	
	/**
	 * Esempio: da "a accentata = �" a "00000000006120616363656e74617461203d20e0"
	 * 
	 * @param input
	 * @param encoding esempio "ISO-8859-1"
	 * @return
	 * @throws ExceptionUtils
	 */
	public static String format40InHex(
			String input, 
			String encoding) 
					throws ExceptionUtils {
		
		String returnString = null;
		try {
			returnString = String.format(
					"%040x",
					new BigInteger(input.getBytes(encoding)));
		} catch (UnsupportedEncodingException e) {
			log.error("impossibile convertire in hex",e);
			throw new ExceptionUtils("impossibile convertire in hex",e);
		}
				
		return returnString;
	}
	
	

	/**
	 * Esempio: da "6120616363656e74617461203d20e0" a "a accentata = à"
	 * 
	 * @param lexicalXSDHexBinary
	 * @param encoding esempio "ISO-8859-1"
	 * @return
	 * @throws ExceptionUtils
	 */
	public static String convertFromHex(
			String lexicalXSDHexBinary, 
			String encoding) 
			throws ExceptionUtils {
		
		byte[] bytes = null;
		try {
			bytes = DatatypeConverter.parseHexBinary(lexicalXSDHexBinary);
		} catch (Exception e) {
			log.error("impossibile tradurre in stringa =>" + lexicalXSDHexBinary +"<=",e);
			throw new ExceptionUtils("impossibile tradurre in stringa =>" + lexicalXSDHexBinary +"<=",e);
		}
		
		String returnString = null;
		
		try {
			returnString = new String(bytes, encoding);
		} catch (UnsupportedEncodingException e) {
			log.error("impossibile codificare la stringa in encoding =>" + encoding +"<=",e);
			throw new ExceptionUtils("impossibile codificare la stringa in encoding =>" + encoding +"<=",e);
		}
		
		return returnString;	
	}
	
	/**
	 * Esempio: da "a accentata = à" a "6120616363656E74617461203D20E0"
	 * 
	 * 
	 * @param input
	 * @return
	 * @throws ExceptionUtils 
	 */
	public static String convertToHex(
			String input, 
			String encoding) 
					throws ExceptionUtils {
		
		byte[] bytes = null;
		try {
			bytes = input.getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			log.error("impossibile convertire in byte la stringa=>" +input + "<= secondo encoding =>" + encoding +"<=",e);
			throw new ExceptionUtils("impossibile convertire in byte la stringa=>" +input + "<= secondo encoding =>" + encoding +"<=",e);

		}
		
		String output = null;
		try {
			output = DatatypeConverter.printHexBinary(bytes);
		} catch (Exception e) {
			log.error("impossibile convertire in stringa hex la stringa input=>" +input + "<=",e);
			throw new ExceptionUtils("impossibile convertire in byte la stringa=>" +input + "<= secondo encoding =>" + encoding +"<=",e);
		}
		
		return output;
	}
	
	
	public static void main(String[] args) throws ExceptionUtils {
		log.info("START sample SampleHexConvert0");
		
		String encoding = getFileEncoding();
		log.info("default file encoding =>" + encoding + "<=");
		
		String sampleHexFormatString = "6120616363656e74617461203d20e0";
		String sampleNormalFormatString = "a accentata = à";
		
		log.info("Stringa formattata in hex =>" + format40InHex(sampleNormalFormatString,encoding) + "<=");
		log.info("Stringa convertita da hex =>" + convertFromHex(sampleHexFormatString, encoding) + "<=");
		log.info("Stringa convertita in hex =>" + convertToHex(sampleNormalFormatString, encoding) + "<=");
		
		log.info("END sample SampleHexConvert0");
	}
	

	
	
}

