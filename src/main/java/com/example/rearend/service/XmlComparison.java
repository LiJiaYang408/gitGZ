package com.example.rearend.service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XmlComparison {

    public static void main(String[] args) {
        File file1 = new File("FT100045627_AY_FY-TEST.ddem");
        File file2 = new File("FT100045627AY_FY-TEST(1).ddem");

        try {
            compareXmlFiles(file1, file2);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void compareXmlFiles(File file1, File file2) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document doc1 = builder.parse(file1);
        Document doc2 = builder.parse(file2);

        NodeList specimens1 = doc1.getElementsByTagName("SPECIMEN");
        NodeList specimens2 = doc2.getElementsByTagName("SPECIMEN");

        for (int i = 0; i < specimens1.getLength(); i++) {
            Element specimen1 = (Element) specimens1.item(i);
            Element specimen2 = (Element) specimens2.item(i);

            compareSpecimens(specimen1, specimen2);
        }
    }

    public static void compareSpecimens(Element specimen1, Element specimen2) {
        NodeList locusList1 = specimen1.getElementsByTagName("LOCUS");
        NodeList locusList2 = specimen2.getElementsByTagName("LOCUS");

        Map<String, Element> locusMap1 = new HashMap<>();
        Map<String, Element> locusMap2 = new HashMap<>();

        for (int i = 0; i < locusList1.getLength(); i++) {
            Element locus1 = (Element) locusList1.item(i);
            String locusName1 = locus1.getElementsByTagName("LOCUSNAME").item(0).getTextContent();
            locusMap1.put(locusName1, locus1);
        }

        for (int i = 0; i < locusList2.getLength(); i++) {
            Element locus2 = (Element) locusList2.item(i);
            String locusName2 = locus2.getElementsByTagName("LOCUSNAME").item(0).getTextContent();
            locusMap2.put(locusName2, locus2);
        }

        for (String locusName : locusMap1.keySet()) {
            Element locus1 = locusMap1.get(locusName);
            Element locus2 = locusMap2.get(locusName);

            if (locus2 != null &&!compareLocus(locus1, locus2)) {
                System.out.println("不一致的 LOCUSNAME: " + locusName);
            }
        }
    }

    public static boolean compareLocus(Element locus1, Element locus2) {
        NodeList childNodes1 = locus1.getChildNodes();
        NodeList childNodes2 = locus2.getChildNodes();

        for (int i = 0; i < childNodes1.getLength(); i++) {
            Node node1 = childNodes1.item(i);
            if (node1.getNodeType() == Node.ELEMENT_NODE) {
                if ("READINGDATETIME".equals(node1.getNodeName())) {
                    continue;
                }

                Node node2 = findNodeByName(childNodes2, node1.getNodeName());
                if (node2 == null) {
                    return false;
                }

                if (node1.getNodeType() == Node.ELEMENT_NODE && node2.getNodeType() == Node.ELEMENT_NODE) {
                    Element element1 = (Element) node1;
                    Element element2 = (Element) node2;

                    if (element1.hasChildNodes() && element2.hasChildNodes()) {
                        if (!compareLocus(element1, element2)) {
                            return false;
                        }
                    } else if (!element1.getTextContent().equals(element2.getTextContent())) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static Node findNodeByName(NodeList nodeList, String nodeName) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(nodeName)) {
                return node;
            }
        }
        return null;
    }
}