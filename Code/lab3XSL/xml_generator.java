public class xml_generator{
    private static Document getDOMParsedDocument(final String fileName)
    {
        Document document = null;
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            //If want to make namespace aware.
            //factory.setNamespaceAware(true);
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
