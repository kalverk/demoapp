package ee.sk.hwcrypto.demo.model;

import org.springframework.stereotype.Service;

/**
 * Created by kalver on 12.05.2015.
 */
public class MobileAuthenticate {

    String id;
    String countryCode;
    String phoneNo;
    String language;
    String serviceName;
    String messageToDisplay;
    String SPChallenge;
    String messagingMode;
    String asyncConfiguration;
    String returnCertData;
    String returnRevocationData;

    public MobileAuthenticate(String id, String countryCode, String phoneNo, String language, String serviceName, String messageToDisplay, String SPChallenge, String messagingMode, String asyncConfiguration, String returnCertData, String returnRevocationData) {
        this.id = id;
        this.countryCode = countryCode;
        this.phoneNo = phoneNo;
        this.language = language;
        this.serviceName = serviceName;
        this.messageToDisplay = messageToDisplay;
        this.SPChallenge = SPChallenge;
        this.messagingMode = messagingMode;
        this.asyncConfiguration = asyncConfiguration;
        this.returnCertData = returnCertData;
        this.returnRevocationData = returnRevocationData;
    }

    public MobileAuthenticate(String id, String phoneNo, String language, String serviceName, String messagingMode) {
        this.id = id;
        this.countryCode = "";
        this.phoneNo = phoneNo;
        this.language = language;
        this.serviceName = serviceName;
        this.messageToDisplay = "";
        this.SPChallenge = "";
        this.messagingMode = messagingMode;
        this.asyncConfiguration = "";
        this.returnCertData = "";
        this.returnRevocationData = "";
    }

    public String query(){
        return "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tns=\"http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:d=\"http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl\" xmlns:mss=\"http://www.sk.ee:8098/MSSP_GW/MSSP_GW.wsdl\" xmlns:SOAP=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:MIME=\"http://schemas.xmlsoap.org/wsdl/mime/\" xmlns:DIME=\"http://schemas.xmlsoap.org/ws/2002/04/dime/wsdl/\" xmlns:WSDL=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                "<SOAP-ENV:Body>" +
                "<mns:MobileAuthenticate xmlns:mns=\"http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                "<IDCode xsi:type=\"xsd:string\">"+id+"</IDCode>" +
                "<CountryCode xsi:type=\"xsd:string\">"+countryCode+"</CountryCode>" +
                "<PhoneNo xsi:type=\"xsd:string\">"+phoneNo+"</PhoneNo>" +
                "<Language xsi:type=\"xsd:string\">"+language+"</Language>" +
                "<ServiceName xsi:type=\"xsd:string\">"+serviceName+"</ServiceName>" +
                "<MessageToDisplay xsi:type=\"xsd:string\">"+messageToDisplay+"</MessageToDisplay>" +
                "<SPChallenge xsi:type=\"xsd:string\">"+SPChallenge+"</SPChallenge>" +
                "<MessagingMode xsi:type=\"xsd:string\">"+messagingMode+"</MessagingMode>" +
                "<AsyncConfiguration xsi:type=\"xsd:int\">"+asyncConfiguration+"</AsyncConfiguration>" +
                "<ReturnCertData xsi:type=\"xsd:boolean\">"+returnCertData+"</ReturnCertData>" +
                "<ReturnRevocationData xsi:type=\"xsd:boolean\">"+returnRevocationData+"</ReturnRevocationData>" +
                "</mns:MobileAuthenticate></SOAP-ENV:Body></SOAP-ENV:Envelope>";
    }

}
