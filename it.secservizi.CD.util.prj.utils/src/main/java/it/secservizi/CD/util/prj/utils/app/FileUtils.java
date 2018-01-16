package it.secservizi.CD.util.prj.utils.app;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
  * Source : java tutorial (Character Streams)
  * <br>
  * Per encoding vedere http://docs.oracle.com/javase/1.5.0/docs/guide/intl/encoding.doc.html
  * <br>
  * Solo per 1.7+ vale StandardCharsets
 * <p>
 * INPUT read - FileInputStream (byte) <- InputStreamReader ( , encoding) (char)
 * <- BufferedReader
 * <br>
 * diverso da FileReader che usa sempre il default encoding...
 * <p>
 * OUTPUT write - FileOutputStream (byte) <- OutputStreamWriter ( , encoding)
 * (char) <- BufferedWriter corrisponde esattamente a PrintWriter (file,
 * encoding)
 * <p>
 * ATTENZIONE: Methods in this class never throw I/O exceptions, although some
 * of its constructors may. The client may inquire as to whether any errors have
 * occurred by invoking checkError()
 * @author Stefano_Antoniazzi
 *
 */
public class FileUtils {
	private static final Logger log = LoggerFactory.getLogger(FileUtils.class);
	
	public static final String WIN_ENCODING = "windows-1252";
	public static final String UTF8_ENCODING = "UTF-8";

	public static String getUserDir() {
		return System.getProperty("user.dir");
	}
	
	public static String getFileSeparator() {
		return System.getProperty("file.separator");
	}
	
	public static String getLineSeparator() {
		return System.getProperty("line.separator");
	}
	
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
	 * Combina path aggiungendo un pezzo all'altro senza usare esplicitamente i separatori
	 * 
	 * @param pathName1
	 * @param pathName2
	 * @return
	 */
	public static String combinePath(String...pathNames) {
		
		File file = null;
		for (String pathName : pathNames) {
			if (file == null) {
				file = new File(pathName);
			} else {
				file = new File(file, pathName);
			}
		}
		
		return file.getPath();	
	}
	
	
	/**
	 * Verifica se file esiste ed e' proprio file (non directory)
	 * 
	 * @param filePathName
	 * @return
	 * @throws ExceptionUtils
	 */
	public static boolean checkExistFile(String filePathName) throws ExceptionUtils {
		
		File file = new File(filePathName);
		boolean fileExist = false;
		try {
			fileExist = file.exists();
		} catch (Exception e) {
			log.error("impossibile stabilire se file esiste =>" + filePathName + "<=",e);
			throw new ExceptionUtils("impossibile stabilire se file esiste =>" + filePathName + "<=",e);
		}
		
		boolean fileIsFile = false;
		if (fileExist) {	
			try {
				fileIsFile = file.isFile();
			} catch (Exception e) {
				log.error("impossibile stabilire se file o directory  =>" + filePathName + "<=",e);
				throw new ExceptionUtils("impossibile stabilire se file o directory =>" + filePathName + "<=",e);
			}
		}
		
		return fileExist && fileIsFile;
			
	}
	
	/**
	 * Verifica se directory esiste ed e' proprio directory (non file)
	 * 
	 * @param folderPathName
	 * @return
	 * @throws ExceptionUtils
	 */
	public static boolean checkExistFolder(String folderPathName) throws ExceptionUtils {
		
		File file = new File(folderPathName);
		boolean fileExist = false;
		try {
			fileExist = file.exists();
		} catch (Exception e) {
			log.error("impossibile stabilire se file esiste =>" + folderPathName + "<=",e);
			throw new ExceptionUtils("impossibile stabilire se file esiste =>" + folderPathName + "<=",e);
		}
		
		boolean fileIsDirectory = false;
		if (fileExist) {	
			try {
				fileIsDirectory = file.isDirectory();
			} catch (Exception e) {
				log.error("impossibile stabilire se file o directory  =>" + folderPathName + "<=",e);
				throw new ExceptionUtils("impossibile stabilire se file o directory =>" + folderPathName + "<=",e);
			}
		}
		
		return fileExist && fileIsDirectory;
	}
	
	
	/**
	 * 
	 * Lettura di righe di testo (delimitatore LF o CR o CR LF) con
	 * BufferedReader ( InputStreamReader ( FileInputStream
	 * <br> Nota che se ultima riga e' solo CR LF non la legge
	 * <p>
	 * NOTA: sul motivo per cui vengono chiusi tutti gli stream: <br>
	 * <a href="https://stackoverflow.com/questions/1388602/do-i-need-to-close-both-filereader-and-bufferedreader"> link </a>
	 * As others have pointed out, you only need to close the outer wrapper.
	 * BufferedReader reader = new BufferedReader(new FileReader(fileName));
	 * <b>There is a very slim chance that this could leak a file handle if the
	 * BufferedReader constructor threw an exception</b> (e.g.
	 * OutOfMemoryError). If your app is in this state, how careful your clean
	 * up needs to be might depend on how critical it is that you don't deprive
	 * the OS of resources it might want to allocate to other programs
	 * 
	 * @param fileName
	 * @param charsetName
	 *            vedere classe java.nio.charset.Charset per valori possibili
	 * @return
	 * @throws ExceptionUtils
	 */
	public static List<String> readTextFileLines(
			String fileName,
			String charsetName) 
					throws ExceptionUtils {
		
		log.trace("richiesta lettura (read) di =>" + fileName + "<= cone encoding =>" + charsetName + "<=");
		
		List<String> returnRecords = new ArrayList<String>();
		
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {
		
			fileInputStream = new FileInputStream(fileName);
			inputStreamReader = new InputStreamReader(fileInputStream, charsetName);
			bufferedReader = new BufferedReader(inputStreamReader);
			
			//ATTENZIONE: se fine linea e' un qualsiasi carattere LN CR o la somma dei due � impossibile capire se il file originale aveva un fine linea sull'ultima riga
			//NON restituisce i caratteri di fine riga
			for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {				
				returnRecords.add(line);
			}
		} catch (FileNotFoundException e) {
			log.error("NON esiste, impossibile leggere file=>" + fileName + "<=",e);
			throw new ExceptionUtils("NON esiste, impossibile leggere file=>" + fileName + "<=",e);
		} catch (UnsupportedEncodingException e) {
			log.error("impossibile usare  charsetName=>" + charsetName + "<=",e);
			throw new ExceptionUtils("impossibile usare  charsetName=>" + charsetName + "<=",e);
		} catch (IOException e) {
			log.error("errore durante la lettura di =>" + fileName + "<=",e);
			throw new ExceptionUtils("errore durante la lettura di =>" + fileName + "<=",e);
		} finally {
			
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					log.error("errore durante close fileInputStream",e);
				}				
			}
			
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					log.error("errore durante close inputStreamReader",e);
				}				
			}
			
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					log.error("errore durante close bufferedReader",e);
				}				
			}
			
		}

		log.trace("OK lettura (read) di =>" + fileName + "<= cone encoding =>" + charsetName + "<= record: " + returnRecords.size());

		return returnRecords;	
	}
	
	
	/**
	 * Lettura di righe di testo (delimitatore LF o CR o CR LF) con  Scanner ( FileInputStream
	 * 
	 * @param fileName
	 * @param charsetName
	 * @return
	 * @throws ExceptionUtils
	 */
	public static List<String> scanTextFileLines(
			String fileName,
			String charsetName) 
					throws ExceptionUtils {

		log.trace("richiesta lettura (scan) di =>" + fileName + "<= con encoding =>" + charsetName + "<=");
		
		List<String> returnRecords = new ArrayList<String>();
		
		FileInputStream fileInputStream = null;
		Scanner scanner = null;
		try {
			
			fileInputStream = new FileInputStream(fileName);
			scanner = new Scanner(fileInputStream, charsetName);
			while (scanner.hasNextLine()) {
				//...any line terminator ... quindi quali ? CR LF ?
				// non ritorna nessun fine riga
				String line = scanner.nextLine();
				returnRecords.add(line);	
			}
	
		} catch (FileNotFoundException e) {
			log.error("NON esiste, impossibile leggere file=>" + fileName + "<=",e);
			throw new ExceptionUtils("NON esiste, impossibile leggere file=>" + fileName + "<=",e);
		} catch (Exception e) {
			log.error("errore durante lettura file=>" + fileName + "<=",e);
			throw new ExceptionUtils("errore durante lettura file=>" + fileName + "<=",e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	
		log.trace("richiesta lettura (scan) di =>" + fileName + "<= con encoding =>" + charsetName + "<= record: " + returnRecords.size());
		return returnRecords;
	}

	/**
	 * Scrittura di una lista di record aggiungendo sempre newline alla fine di ogni record escluso ultimo (resta come originale)
	 * 
	 * @param fileName
	 * @param encoding vedere classe java.nio.charset.Charset per possibili encoding
	 * @param records
	 * @throws ExceptionUtils
	 */
	public static void writeTextFileLines(
			String fileName,
			String encoding,
			List<String> records) throws ExceptionUtils {
		
		log.trace("richiesta scrittura su =>" + fileName + "<= di righe n. :" + records.size() );
		
		FileOutputStream fileOutputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		BufferedWriter bufferedWriter = null;
		
		if (checkExistFolder(fileName)) {
			log.error("impossibile scrivere file, esiste gia' una drectory di nome =>" + fileName + "<=");
			throw new ExceptionUtils("impossibile scrivere file, esiste gia' una directory di nome =>" + fileName + "<=");
		}
		
		// crea eventuali directory mancanti al file
		File file = new File(fileName);
		File parent = file.getParentFile();

		
		try {
			if (!parent.exists()) {
				if (!parent.mkdirs()) {
					log.error("impossibile creare directory =>" + parent + "<=");
					throw new ExceptionUtils("impossibile creare directory =>" + parent + "<=");
				} else {
					log.trace("creata directory mancante =>" + parent.getPath() + "<=");
				}
			} else {
				log.trace("trovata esistente =>" + parent.getPath() + "<=");
			}
		} catch (ExceptionUtils e) {
			throw e;
		} catch (Exception e) {
			log.error("impossibile costruire path per  =>" + parent + "<=",e);
			throw new ExceptionUtils("impossibile costruire path per  =>" + parent + "<=",e);
		}
		
		
		try {
			
			fileOutputStream = new FileOutputStream(fileName);
			outputStreamWriter = new OutputStreamWriter(fileOutputStream, encoding);
			bufferedWriter = new BufferedWriter(outputStreamWriter);
			
			/*
			 * Non sapendo se il record finale originale ha newline o meno per
			 * lasciarlo intatto si aggiungono esplicitamente newline solo per
			 * separare i record
			 */
			boolean first = true;
			for (String record : records) {
				
				if (first) {
					first= false;
				} else {
					bufferedWriter.newLine();
				}
				bufferedWriter.write(record);	
			}
			
			
		} catch (FileNotFoundException e) {
			log.info("file per output non esisteva =>" + fileName + "<=");
		} catch (IOException e) {
			log.error("problemi in scrittura su =>" + fileName + "<=");
			throw new ExceptionUtils("problemi in scrittura su =>" + fileName + "<=");
		} finally {
			
			if (bufferedWriter != null) {
				try {
					bufferedWriter.close();
				} catch (IOException e) {
					log.error("problemi durante la close bufferedWriter del file =>" + fileName + "<=",e);
				}
			}
			if (outputStreamWriter != null) {
				try {
					outputStreamWriter.close();
				} catch (IOException e) {
					log.error("problemi durante la close outputStreamWriter del file =>" + fileName + "<=",e);
				}
			}
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					log.error("problemi durante la close fileOutputStream del file =>" + fileName + "<=",e);
				}
			}
		}
		
		log.trace("OK scrittura su =>" + fileName + "<= di righe n. :" + records.size() );
	}
	
	/**
	 * Scrittura di una lista di record aggiungendo sempre newline alla fine di ogni record escluso ultimo (resta come originale) 
	 * 
	 * @param fileName
	 * @param encoding
	 * @param records
	 * @throws ExceptionUtils
	 */
	public static void printTextFile(
			String fileName,
			String encoding,
			List<String> records) throws ExceptionUtils {
		
		log.trace("richiesta scrittura su =>" + fileName + "<= per righe n. :" + records.size() );
		
		PrintWriter printWriter = null;
		try {
			
			printWriter = new PrintWriter(fileName, encoding);
			
			/*
			 * Non sapendo se il record finale originale ha newline o meno per
			 * lasciarlo intatto si aggiungono esplicitamente newline solo per
			 * separare i record
			 */
			boolean first = true;
			for (String line : records) {
				
				if (first) {
					first= false;
				} else {
					printWriter.println();
				}
				printWriter.print(line);
			}
			
		} catch (FileNotFoundException e) {
			log.info("file in output non esisteva =>" + fileName + "<=",e);
			throw new ExceptionUtils("file in output non esisteva =>" + fileName + "<=",e);
		} catch (UnsupportedEncodingException e) {
			log.error("encoding non supportato =>" + encoding + "<=",e);
			throw new ExceptionUtils("encoding non supportato =>" + encoding + "<=",e);
		} catch (Exception e) {
			log.info("errore durante scrittura =>" + fileName + "<=",e);
			throw new ExceptionUtils("errore durante scrittura =>" + fileName + "<=",e);
		} finally {
			if (printWriter != null) {
				printWriter.close();
				if (printWriter.checkError()) {
					log.info("errore durante chiusura printWriter per file  =>" + fileName + "<=");
					throw new ExceptionUtils("errore durante chiusura printWriter per file  =>" + fileName + "<=");
				}

			}
		}
		
		log.trace("OK scrittura su =>" + fileName + "<= per righe n. :" + records.size() );
	}
	
	/**
	 * Solo come sample di utilizzo
	 * @param args
	 * @throws ExceptionUtils
	 * @throws IOException 
	 */
	public static void main(String[] args) throws ExceptionUtils, IOException {
		
		log.info("START sample read text file =================================================");

		// struttura per leggere record e passarli in scrittura
		List<String> readRecordsCRLFOk = null;
		List<String> scanRecordsCRLFOk = null;
		
		String defaultEncoding = getFileEncoding();
		
		String basePathName = getUserDir();
		String workingInputSampledataFolder 	= "sampledata";
		String workingInputFolder 				= "SampleFileUtils01";
		String workingInputFile 				= "inputLastCRLFOk.txt";

		
		String workingInputFolderPathName = combinePath(basePathName, workingInputSampledataFolder, workingInputFolder);
		String workingInputFilePathName = combinePath(workingInputFolderPathName, workingInputFile);
		
		
		
		log.info("encoding =>" + defaultEncoding + "<=");
		log.info("basePathName =>" + basePathName + "<=");
		log.info("workingInputFolderPathName =>" + workingInputFolderPathName + "<=");
		
		if (FileUtils.checkExistFolder(workingInputFolderPathName)) {
			log.info("SI  esiste ed � FOLDER workingInputFolderPathName =>" + workingInputFolderPathName + "<=");
		} else {
			log.info("NON esiste o NON � FOLDEr workingInputFolderPathName =>" + workingInputFolderPathName + "<=");
		}
		
		if (FileUtils.checkExistFile(workingInputFilePathName)) {
			log.info("SI  esiste ed � FILE workingInputFilePathName =>" + workingInputFilePathName + "<=");
		
			// legge con encoding di default del sistema...potrebbe non andare bene...
			readRecordsCRLFOk = FileUtils.readTextFileLines(workingInputFilePathName, defaultEncoding);
			
			log.info("POST readTextFileLines di  =>" + workingInputFilePathName + "<=");
			for (String line : readRecordsCRLFOk) {
				log.info(" Letta Riga =>" + line + "<=");
				log.info(" in hex  =>" + HexConvertUtils.convertToHex(line, defaultEncoding) + "<=");
				
			}
			
			
			scanRecordsCRLFOk = FileUtils.scanTextFileLines(workingInputFilePathName, defaultEncoding);
			
			log.info("POST scanTextFileLines di =>" + workingInputFilePathName + "<=");
			for (String line : scanRecordsCRLFOk) {
				log.info(" Letta Riga =>" + line + "<=");
				log.info(" in hex  =>" + HexConvertUtils.convertToHex(line, defaultEncoding) + "<=");
				
			}
			
			
		} else {
			log.info("NON esiste o NON � FILE workingInputFilePathName =>" + workingInputFilePathName + "<=");
		}
		
		
		List<String> readRecordsCRLFKo = null;
		List<String> scanRecordsCRLFKo = null;
		
		basePathName = getUserDir();
		workingInputSampledataFolder 	= "sampledata";
		workingInputFolder 				= "SampleFileUtils01";
		workingInputFile 				= "inputLastCRLFKo.txt";
		
		workingInputFolderPathName = combinePath(basePathName, workingInputSampledataFolder, workingInputFolder);
		workingInputFilePathName = combinePath(workingInputFolderPathName, workingInputFile);
		
		
		
		log.info("encoding =>" + defaultEncoding + "<=");
		log.info("basePathName =>" + basePathName + "<=");
		log.info("workingInputFolderPathName =>" + workingInputFolderPathName + "<=");
		
		if (FileUtils.checkExistFolder(workingInputFolderPathName)) {
			log.info("SI  esiste ed � FOLDER workingInputFolderPathName =>" + workingInputFolderPathName + "<=");
		} else {
			log.info("NON esiste o NON � FOLDEr workingInputFolderPathName =>" + workingInputFolderPathName + "<=");
		}
		
		if (FileUtils.checkExistFile(workingInputFilePathName)) {
			log.info("SI  esiste ed � FILE workingInputFilePathName =>" + workingInputFilePathName + "<=");
			
			// legge con encoding di default del sistema...potrebbe non andare bene...
			readRecordsCRLFKo = FileUtils.readTextFileLines(workingInputFilePathName, defaultEncoding);
			
			log.info("POST readTextFileLines di  =>" + workingInputFilePathName + "<=");
			for (String line : readRecordsCRLFKo) {
				log.info(" Letta Riga =>" + line + "<=");
				log.info(" in hex  =>" + HexConvertUtils.convertToHex(line, defaultEncoding) + "<=");
				
			}
			
			
			scanRecordsCRLFKo = FileUtils.scanTextFileLines(workingInputFilePathName, defaultEncoding);
			
			log.info("POST scanTextFileLines di =>" + workingInputFilePathName + "<=");
			for (String line : scanRecordsCRLFKo) {
				log.info(" Letta Riga =>" + line + "<=");
				log.info(" in hex  =>" + HexConvertUtils.convertToHex(line, defaultEncoding) + "<=");
				
			}
			
			
		} else {
			log.info("NON esiste o NON � FILE workingInputFilePathName =>" + workingInputFilePathName + "<=");
		}
		
		log.info("END sample read text file =================================================");

		log.info("START sample write text file =================================================");
		
		String outFolder1Name 	= "sampledata";
		String outFolder2Name 	= "SampleFileUtils01";
		String outFileName		= "outputRead1Write.txt";
		String outPath = FileUtils.combinePath(basePathName, outFolder1Name, outFolder2Name, outFileName);
		FileUtils.writeTextFileLines(outPath, defaultEncoding, readRecordsCRLFOk);
		
		outFolder1Name 	= "sampledata";
		outFolder2Name 	= "SampleFileUtils01";
		outFileName		= "outputRead1Print.txt";
		outPath = FileUtils.combinePath(basePathName, outFolder1Name, outFolder2Name, outFileName);
		FileUtils.printTextFile(outPath, defaultEncoding, readRecordsCRLFOk);
		
		outFolder1Name 	= "sampledata";
		outFolder2Name 	= "SampleFileUtils01";
		outFileName		= "outputScan1Write.txt";
		outPath = FileUtils.combinePath(basePathName, outFolder1Name, outFolder2Name, outFileName);
		FileUtils.writeTextFileLines(outPath, defaultEncoding, scanRecordsCRLFOk);
		
		outFolder1Name 	= "sampledata";
		outFolder2Name 	= "SampleFileUtils01";
		outFileName		= "outputScan1Print.txt";
		outPath = FileUtils.combinePath(basePathName, outFolder1Name, outFolder2Name, outFileName);
		FileUtils.printTextFile(outPath, defaultEncoding, scanRecordsCRLFOk);

		
		log.info("END sample write text file =================================================");
		
		log.info("START specific write text file =================================================");

		
		
		List<String> linesToWrite = new ArrayList<String>();
		linesToWrite.add("Prima riga   � � � � �");
		linesToWrite.add("Seconda riga � � � � �");
		
		
		String workingOutput1SampledataFolder 	= "sampledata";
		String workingOutput1Folder 			= "SampleFileUtils01";
		String workingOutput1File 				= "outputSpec1.txt";
		String workingOutput1FilePathName = FileUtils.combinePath(basePathName, workingOutput1SampledataFolder, workingOutput1Folder, workingOutput1File);
		

		
		FileUtils.writeTextFileLines(workingOutput1FilePathName, defaultEncoding, linesToWrite);

		String workingOutput2SampledataFolder 	= "sampledata";
		String workingOutput2Folder 			= "SampleFileUtils01";
		String workingOutput2File 				= "outputSpec2.txt";
		
		String workingOutput2FilePathName = FileUtils.combinePath(basePathName, workingOutput2SampledataFolder, workingOutput2Folder, workingOutput2File);
		
		
		FileUtils.printTextFile(workingOutput2FilePathName, defaultEncoding, linesToWrite);
		
		log.info("END specific write text file =================================================");
		
		// Nota bene sono indistinguibili perch� scanner restituisce la riga senza i line feed
		
		String multilinetext = null;
		Scanner sc = null;
		multilinetext = "Line1\n" + 
						"Line2\n" + 
						"\n" + 
						"Line4";

		log.trace("StringBuilder Hex =>"
				+ HexConvertUtils
						.convertToHex(multilinetext, defaultEncoding) + "<=");

		sc = new Scanner(multilinetext);
		while (sc.hasNextLine()) {
			System.out.println("[" + sc.nextLine() + "]");
		}			
		sc.close();
		
		multilinetext = "Line1\n" + 
				"Line2\n" + 
				"\n" + 
				"Line4\n";
		
		log.trace("StringBuilder Hex =>"
				+ HexConvertUtils
				.convertToHex(multilinetext, defaultEncoding) + "<=");
		
		sc = new Scanner(multilinetext);
		while (sc.hasNextLine()) {
			System.out.println("[" + sc.nextLine() + "]");
		}			
		sc.close();
		
		log.info("START UTF-8 =================================================");
		
		basePathName = getUserDir();
		workingInputSampledataFolder 	= "sampledata";
		workingInputFolder 				= "SampleFileUtils01";
		workingInputFile 				= "input-sample-alfa.txt";
		
		workingInputFolderPathName = combinePath(basePathName, workingInputSampledataFolder, workingInputFolder);
		workingInputFilePathName = combinePath(workingInputFolderPathName, workingInputFile);
		
		String UTFEncoding = UTF8_ENCODING;
		log.info("encoding =>" + UTFEncoding + "<=");
		log.info("basePathName =>" + basePathName + "<=");
		log.info("workingInputFolderPathName =>" + workingInputFolderPathName + "<=");
		
		if (FileUtils.checkExistFolder(workingInputFolderPathName)) {
			log.info("SI  esiste ed � FOLDER workingInputFolderPathName =>" + workingInputFolderPathName + "<=");
		} else {
			log.info("NON esiste o NON � FOLDEr workingInputFolderPathName =>" + workingInputFolderPathName + "<=");
		}
		
		if (FileUtils.checkExistFile(workingInputFilePathName)) {
			log.info("SI  esiste ed � FILE workingInputFilePathName =>" + workingInputFilePathName + "<=");
			
			// legge con encoding di default del sistema...potrebbe non andare bene...
			readRecordsCRLFKo = FileUtils.readTextFileLines(workingInputFilePathName, UTFEncoding);
			
			log.info("POST readTextFileLines di  =>" + workingInputFilePathName + "<=");
			for (String line : readRecordsCRLFKo) {
				log.info(" Letta Riga =>" + line + "<=");
				log.info(" in hex  =>" + HexConvertUtils.convertToHex(line, UTFEncoding) + "<=");
				
			}
			
			
			scanRecordsCRLFKo = FileUtils.scanTextFileLines(workingInputFilePathName, UTFEncoding);
			
			log.info("POST scanTextFileLines di =>" + workingInputFilePathName + "<=");
			for (String line : scanRecordsCRLFKo) {
				log.info(" Letta Riga =>" + line + "<=");
				log.info(" in hex  =>" + HexConvertUtils.convertToHex(line, UTFEncoding) + "<=");
				
			}
			//alla console non si vede...
			log.info("Test alfa :" + "\u03B1");
			
			
		} else {
			log.info("NON esiste o NON e' FILE workingInputFilePathName =>" + workingInputFilePathName + "<=");
		}
		
		
		
		outFolder1Name 	= "sampledata";
		outFolder2Name 	= "SampleFileUtils01";
		outFileName		= "output-sample-alfa.txt";
		outPath = FileUtils.combinePath(basePathName, outFolder1Name, outFolder2Name, outFileName);
		FileUtils.writeTextFileLines(outPath, UTFEncoding, scanRecordsCRLFKo);
		log.info("END UTF-8 =================================================");

		
	}

}
