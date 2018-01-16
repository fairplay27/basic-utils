package it.secservizi.CD.util.prj.utils.app;

import java.io.File;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlUtils {
	private static final Logger log = LoggerFactory.getLogger(FileUtils.class);
	
	//ha solo classi statiche quindi no costruttori pubblici
	private XmlUtils() {}
	
	/**
	 * 
	 * Parse XML Error Handler to be registered to documentBuilder
	 *
	 */
	private static class XmlUtilsErrorHandler implements ErrorHandler {
		private static final Logger log = LoggerFactory.getLogger(XmlUtilsErrorHandler.class);

	    /**
	     * Estrae informazioni da errore
	     * @param saxParseException
	     * @return
	     */
		private String getParseExceptionInfo(SAXParseException saxParseException) {
	        String systemId = (saxParseException.getSystemId() == null) ? "null" : saxParseException.getSystemId();

	        String returnMessage = "URI (systemId) =>" + systemId 
	        			+ "<= Line Problem =>" + saxParseException.getLineNumber()
	        			+ "<= Message =>" + saxParseException.getMessage() 
	        			+ "<=";
	        return returnMessage;
	    }

	    public void warning(SAXParseException saxParseException) throws SAXException {
	        log.warn("Warning: " + getParseExceptionInfo(saxParseException));
	    }
	        
	    public void error(SAXParseException saxParseException) throws SAXException {
	        String message = "Error: " + getParseExceptionInfo(saxParseException);
	        throw new SAXException(message);
	    }

	    public void fatalError(SAXParseException saxParseException) throws SAXException {
	        String message = "Fatal Error: " + getParseExceptionInfo(saxParseException);
	        throw new SAXException(message);
	    }
		
	}
	
	/**
	 * Xml document da stringa (costruita preventivamente)
	 * @param xmlDocumentContent
	 * @return
	 * @throws ExceptionUtils
	 */
	public static Document constructDocument(final String xmlDocumentContent) throws ExceptionUtils {
		
		log.trace("START costruzione xml document da stringa...");
		Document returnDocument = null;

		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			documentBuilderFactory.setIgnoringComments(true);
			documentBuilderFactory.setIgnoringElementContentWhitespace(true);
			documentBuilderFactory.setCoalescing(true); // putCDATAIntoText
			documentBuilderFactory.setExpandEntityReferences(true);
			// documentBuilder.setValidating(dtdValidate || xsdValidate);

			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			documentBuilder.setErrorHandler(new XmlUtilsErrorHandler());

			InputSource inputSource = new InputSource(new StringReader(xmlDocumentContent));
			returnDocument = documentBuilder.parse(inputSource);
		} catch (Exception e) {
			String logError = "Problema xml document ricevuto come stringa";
			log.error(logError, e);
			throw new ExceptionUtils(logError, e);
		}

		log.trace("END costruzione xml document da stringa...OK");
		return returnDocument;
	}
	
	/**
	 * Xml document da file
	 * @param file
	 * @return
	 * @throws ExceptionUtils
	 */
	public static Document constructDocument(File file) throws ExceptionUtils {
		
		log.trace("START costruzione xml document da file..." + file.getAbsolutePath());
		Document returnDocument = null;
		
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			documentBuilderFactory.setIgnoringComments(true);
			documentBuilderFactory.setIgnoringElementContentWhitespace(true);
			documentBuilderFactory.setCoalescing(true); // putCDATAIntoText
			documentBuilderFactory.setExpandEntityReferences(true);
			// documentBuilder.setValidating(dtdValidate || xsdValidate);
			
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			documentBuilder.setErrorHandler(new XmlUtilsErrorHandler());
			
			returnDocument = documentBuilder.parse(file);
		} catch (Exception e) {
			String logError = "Problema xml document ricevuto come file=>" + file.getAbsolutePath() + "<=" ;
			log.error(logError, e);
			throw new ExceptionUtils(logError, e);
		}
		
		log.trace("END costruzione xml document OK da file..." + file.getAbsolutePath());
		return returnDocument;
	}

	/**
	 * restituisce i nodi che rispettano xpath
	 * @param document
	 * @param xpathExpressionName
	 * @return
	 * @throws ExceptionUtils 
	 * @throws XPathExpressionException
	 */
	public static NodeList getNodes(Document document, String xpathExpressionName) throws ExceptionUtils {
		
		NodeList returnNodeList = null;
		
		try {
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			XPathExpression xpathExpression = xpath.compile(xpathExpressionName);

			returnNodeList = (NodeList) xpathExpression.evaluate(document, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			String logError = "Problemi durante ricerca xpath usando: " + xpathExpressionName;
			log.error(logError,e);
			throw new ExceptionUtils(logError,e);
		}
		
		return  returnNodeList;
	}
	
	/**
	 * restituisce elemento con local name e namespace URI desiderato
	 * @param nodes
	 * @param localName
	 * @param namespaceURI
	 * @return
	 */
	public static Element getElement(NodeList nodes, String localName, String namespaceURI) {
		int len = nodes.getLength();
		for (int i = 0; i < len; i++) {
			Node node = nodes.item(i);
			if(node instanceof Element && localName.equals(node.getLocalName()) && namespaceURI.equals(node.getNamespaceURI())) {
				return (Element) node;
			}
		}
		return null;
	}

	/**
	 * Restituisce solo i nodi del nome desiderato
	 * @param nodes
	 * @param name
	 * @return
	 */
	public static Element getElement(NodeList nodes, String name) {
		int len = nodes.getLength();
		for (int i = 0; i < len; i++) {
			Node node = nodes.item(i);
			if(node instanceof Element && node.getNodeName().equals(name)) {
				return (Element) node;
			}
		}
		return null;
	}
	

	
}
