
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.DOMBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class XMLGen {
    // source: https://howtodoinjava.com/xml/jdom2-read-parse-xml-examples/
    public static Document getDOMParsedDocument(final String fileName)
    {
        Document document = null;
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            org.w3c.dom.Document w3cDocument = documentBuilder.parse(fileName);
            document = new DOMBuilder().build(w3cDocument);
        }
        catch (IOException | SAXException | ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        return document;
    }



}
