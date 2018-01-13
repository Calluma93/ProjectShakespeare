import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Umar on 07/10/2016.
 */
public class XMLParser {
    static int titleCount = 0;
    static String title, subtitle, sceneDesc = null;
    static Set<String> persona, speaker, stageDir, line;

    private XMLParser(){

    }

    public static void parse(File file) throws ParserConfigurationException, IOException, SAXException {
        titleCount = 0;
        title = null;
        subtitle = null;
        persona = new HashSet<>();
        sceneDesc = null;
        speaker = new HashSet<>();
        stageDir = new HashSet<>();
        line = new HashSet<>();

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(file);
        doParse(document.getDocumentElement());
    }

    private static void doParse(Node node) {
        switch (node.getNodeName()) {
            case ("TITLE"):
                if (titleCount == 0) {
                    titleCount++;
                    title = node.getTextContent();
                }
                break;

            case ("PERSONA"):
                persona.add(node.getTextContent());
                break;

            case ("SCNDESCR"):
                sceneDesc = node.getTextContent();
                break;

            case ("SPEAKER"):
                speaker.add(node.getTextContent());
                break;

            case ("STAGEDIR"):
                stageDir.add(node.getTextContent());
                break;

            case ("PLAYSUBT"):
                subtitle = node.getTextContent();
                break;
            case ("LINE"):
                line.add(node.getTextContent());
                break;

            default:
                break;
        }


        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                //calls this method for all the children which is Element
                doParse(currentNode);
            }
        }
    }


}
