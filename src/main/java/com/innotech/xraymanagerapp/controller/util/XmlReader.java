/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.util;

import com.innotech.xraymanagerapp.model.DicomTags;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Alexander Escobar
 */
public class XmlReader {

    private static HashMap<String, DicomTags> dicomFileTags = new HashMap<>();

    public XmlReader() {
        dicomFileTags = new HashMap<>();
    }

    public static String getXmlAsSTring(File fXmlFile) {
        String xmlString = "";
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer;

            transformer = tf.newTransformer();

            StringWriter writer = new StringWriter();

            //transform document to string
            transformer.transform(new DOMSource(convertXMLFileToXMLDocument(fXmlFile.getAbsolutePath())), new StreamResult(writer));

            xmlString = writer.getBuffer().toString();
            System.out.println(xmlString);                      //Print to console or logs

        } catch (TransformerException ex) {
            Logger.getLogger(XmlReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return xmlString;
    }

    private static Document convertXMLFileToXMLDocument(String filePath) {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();

            //Parse the content to Document object
            Document xmlDocument = builder.parse(new File(filePath));

            return xmlDocument;
        } catch (IOException | ParserConfigurationException | SAXException e) {
        }
        return null;
    }

    public HashMap<String, DicomTags> createTagList(File fXmlFile) throws ParserConfigurationException, SAXException, IOException {

//            File fXmlFile = new File("D:\\ImageLoaderWSImages\\dicomXml\\test3.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        return createTagList(doc);
    }

    public HashMap<String, DicomTags> createTagList(String xmlString) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
        return createTagList(doc);
    }

    public HashMap<String, DicomTags> createTagList(Document doc) {
        doc.getDocumentElement().normalize();
        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        NodeList nList = doc.getElementsByTagName("DicomAttribute");
        System.out.println("----------------------------");
        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);

//                System.out.println("\nCurrent Element :" + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                DicomTags tag = new DicomTags(eElement.getAttribute("tag"), eElement.getAttribute("keyword"));
                tag.setTagVr(eElement.getAttribute("vr"));
//                        System.out.println("keyword : " + eElement.getAttribute("keyword"));
//                        System.out.println("tag : " + eElement.getAttribute("tag"));
//                        System.out.println("vr : " + eElement.getAttribute("vr"));
                Node lastChild = eElement.getLastChild();
                if (lastChild != null) {
                    if (lastChild.getNodeName().equals("PersonName")) {
                        System.out.println("patient name tag....");
                        String patientName = getLastNodeValue(lastChild);
                        tag.setTagValue(patientName);
                    }
                }
                
                int size = eElement.getElementsByTagName("Value").getLength();
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        try {
    //                            System.out.println(eElement.getAttribute("keyword") + " - " + i + " : " + eElement.getElementsByTagName("Value").item(i).getTextContent());                                
                            if(Objects.equals(eElement.getAttribute("keyword"), "ImagerPixelSpacing") || Objects.equals(eElement.getAttribute("keyword"), "PixelSpacing")){
                                if(Objects.nonNull(tag.getTagValue())){
                                    tag.setTagValue(tag.getTagValue()+"\\"+eElement.getElementsByTagName("Value").item(i).getTextContent());
                                }else{
                                    tag.setTagValue(eElement.getElementsByTagName("Value").item(i).getTextContent());
                                }
                            }else{
                                tag.setTagValue(eElement.getElementsByTagName("Value").item(i).getTextContent());
                            
                            }
                            
                        } catch (NullPointerException e) {
                            System.out.println("Exception: "+e.getMessage());
                        }
                    }
//                        break;
                }
//                if (eElement.getElementsByTagName("PersonName").getLength() > 0) {
//                    for (int i = 0; i < eElement.getElementsByTagName("PersonName").getLength(); i++) {
//                        try {
////                            System.out.println("Node Name: "+eElement.getNodeValue() + "tagName: "+eElement.getTextContent().trim());
////                            System.out.println("Value Personname " + i + " : " + eElement.getElementsByTagName("PersonName").item(i).getTextContent());
//                            tag.setTagValue(eElement.getElementsByTagName("PersonName").item(i).getTextContent().trim());
//                        } catch (NullPointerException e) {
//                        }
//                        break;
//                    }
//                }
                if (eElement.getElementsByTagName("FamilyName").getLength() > 0) {
                    for (int i = 0; i < eElement.getElementsByTagName("FamilyName").getLength(); i++) {
                        try {
                            System.out.println("Node Name: "+eElement.getNodeValue() + " FamilyName: "+eElement.getTextContent().trim());
                            System.out.println("Value FamilyName " + i + " : " + eElement.getElementsByTagName("FamilyName").item(i).getTextContent());
                            tag.setTagValue(eElement.getElementsByTagName("FamilyName").item(i).getTextContent().trim());
                        } catch (NullPointerException e) {
                        }
                        break;
                    }
                }
                if (eElement.getElementsByTagName("GivenName").getLength() > 0) {
                    for (int i = 0; i < eElement.getElementsByTagName("GivenName").getLength(); i++) {
                        try {
                            System.out.println("Node Name: "+eElement.getNodeValue() + " GivenName: "+eElement.getTextContent().trim());
                            System.out.println("Value GivenName " + i + " : " + eElement.getElementsByTagName("GivenName").item(i).getTextContent());
                                tag.setTagValue(eElement.getElementsByTagName("GivenName").item(i).getTextContent().trim()+ ", " + tag.getTagValue() );
                        } catch (NullPointerException e) {
                        }
                        break;
                    }
                }
                
                try {
                    dicomFileTags.put(tag.getTagId(), tag);
                } catch (NullPointerException e) {
                    Logger.getLogger(XmlReader.class.getName()).log(Level.SEVERE, null, e);
                }
            }

        }
        return dicomFileTags;
    }

    String petAndOwnerName;

    public static String getLastNodeValue(Node root) {
        if (root == null) {
            return null;
        }
        if (root.getNodeValue() != null) {
            return root.getNodeValue();
        }
        if (root.getChildNodes() == null) {
            return null;
        }

        return getLastNodeValue(root.getChildNodes().item(0));
    }

    public static void main(String argv[]) {
        try {
//            File fXmlFile = new File("D:\\ImageLoaderWSImages\\dicomXml\\test3.xml");
//            File fXmlFile = new File("D:\\ImageLoaderWSImages\\dicomXml\\avon.xml");
            File fXmlFile = new File("D:\\DicomSent\\ddd.xml");

            XmlReader xmlReader = new XmlReader();

            // first create the tags map from the xml file
            HashMap<String, DicomTags> tagsMap = xmlReader.createTagList(fXmlFile);
            tagsMap.entrySet().forEach((m) -> {
                System.out.println(m.getKey() + " " + m.getValue().getTagDescription() + " = " + m.getValue().getTagValue());
            });
            //XmlReader.getXmlAsSTring(fXmlFile);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(XmlReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
