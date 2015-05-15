package ee.sk.hwcrypto.demo.util;

import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by kalver on 15.05.2015.
 */
@Service
public class SOAPMessageParser {

    //TODO make parsing more reasonable

    public String[] parseMobileAuthResponse(SOAPMessage message) throws SOAPException {
        NodeList nodeList = message.getSOAPBody().getChildNodes();
        String sessionCode = "";
        String status = "";
        String userIDCode = "";
        String userGivenName = "";
        String userSurname = "";
        String userCountry = "";
        String userCN = "";
        String challengeID = "";
        for(int i=0;i<nodeList.getLength();i++){
            Node n = nodeList.item(i);
            if(n.hasChildNodes()){
                NodeList children = n.getChildNodes();
                for(int j=0;j<children.getLength();j++){
                    Node child = children.item(j);
                    if(child.getNodeName().equalsIgnoreCase("Sesscode")){
                        sessionCode=child.getTextContent();
                    }else if(child.getNodeName().equalsIgnoreCase("Status")){
                        status=child.getTextContent();
                    }else if(child.getNodeName().equalsIgnoreCase("UserIDCode")){
                        userIDCode=child.getTextContent();
                    }else if(child.getNodeName().equalsIgnoreCase("UserGivenname")){
                        userGivenName=child.getTextContent();
                    }else if(child.getNodeName().equalsIgnoreCase("UserSurname")){
                        userSurname=child.getTextContent();
                    }else if(child.getNodeName().equalsIgnoreCase("UserCountry")){
                        userCountry=child.getTextContent();
                    }else if(child.getNodeName().equalsIgnoreCase("UserCN")){
                        userCN=child.getTextContent();
                    }else if(child.getNodeName().equalsIgnoreCase("ChallengeID")){
                        challengeID=child.getTextContent();
                    }
                }
            }
        }
        return new String[]{sessionCode,status,userIDCode,userGivenName,userSurname,userCountry,userCN,challengeID};
    }

    public String[] parseStartSessionResponse(SOAPMessage startSessionResponse) throws SOAPException{
        NodeList s2 = startSessionResponse.getSOAPBody().getChildNodes();
        String status = "";
        String sessCode = "";
        for(int i=0;i<s2.getLength();i++){
            Node n = s2.item(i);
            if(n.hasChildNodes()){
                NodeList children = n.getChildNodes();
                for(int j=0;j<children.getLength();j++){
                    Node child = children.item(j);
                    if(child.getNodeName().equalsIgnoreCase("Status")){
                        status=child.getTextContent();
                    }else if(child.getNodeName().equalsIgnoreCase("Sesscode")){
                        sessCode=child.getTextContent();
                    }
                }
            }
        }
        return new String[]{status,sessCode};
    }

    public String[] parseMobileSignResponse(SOAPMessage startSessionResponse) throws SOAPException{
        NodeList s2 = startSessionResponse.getSOAPBody().getChildNodes();
        String status = "";
        String statusCode = "";
        String challengeID = "";
        for(int i=0;i<s2.getLength();i++){
            Node n = s2.item(i);
            if(n.hasChildNodes()){
                NodeList children = n.getChildNodes();
                for(int j=0;j<children.getLength();j++){
                    Node child = children.item(j);
                    if(child.getNodeName().equalsIgnoreCase("Status")){
                        status=child.getTextContent();
                    }else if(child.getNodeName().equalsIgnoreCase("StatusCode")){
                        statusCode=child.getTextContent();
                    }else if(child.getNodeName().equalsIgnoreCase("ChallengeID")){
                        challengeID=child.getTextContent();
                    }
                }
            }
        }
        return new String[]{status,statusCode,challengeID};
    }

    public String parseGetStatusInfo(SOAPMessage startSessionResponse) throws SOAPException{
        NodeList s2 = startSessionResponse.getSOAPBody().getChildNodes();
        String status = "";
        for(int i=0;i<s2.getLength();i++){
            Node n = s2.item(i);
            if(n.hasChildNodes()){
                NodeList children = n.getChildNodes();
                for(int j=0;j<children.getLength();j++){
                    Node child = children.item(j);
                    if(child.getNodeName().equalsIgnoreCase("StatusCode")){
                        status=child.getTextContent();
                    }
                }
            }
        }
        return status;
    }

    public String[] parseGetSignedDoc(SOAPMessage startSessionResponse) throws SOAPException{
        NodeList s2 = startSessionResponse.getSOAPBody().getChildNodes();
        String status = "";
        String data = "";
        for(int i=0;i<s2.getLength();i++){
            Node n = s2.item(i);
            if(n.hasChildNodes()){
                NodeList children = n.getChildNodes();
                for(int j=0;j<children.getLength();j++){
                    Node child = children.item(j);
                    if(child.getNodeName().equalsIgnoreCase("Status")){
                        status=child.getTextContent();
                    }else if(child.getNodeName().equalsIgnoreCase("SignedDocData")){
                        data=child.getTextContent();
                    }
                }
            }
        }
        return new String[]{status,data};
    }

    public void printSOAPResponse(SOAPMessage soapResponse) {
        try{
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            Source sourceContent = soapResponse.getSOAPPart().getContent();
            System.out.print("\nResponse SOAP Message = ");
            StreamResult result = new StreamResult(System.out);
            transformer.transform(sourceContent, result);
        }catch (Exception e){
            System.out.print("\nPrinting exception " + e);
        }
    }

}
