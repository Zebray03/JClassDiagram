package diagram.config;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashSet;
import java.util.Set;

public class XMLAnalyzerConfigParser implements AnalyzerConfigParser {
    @Override
    public Set<String> parse(String configPath) {
        try {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(configPath);
            NodeList nodes = doc.getElementsByTagName("analyzer");
            Set<String> analyzers = new HashSet<>();
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                analyzers.add(element.getTextContent().trim());
            }
            return analyzers;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing config file", e);
        }
    }
}
