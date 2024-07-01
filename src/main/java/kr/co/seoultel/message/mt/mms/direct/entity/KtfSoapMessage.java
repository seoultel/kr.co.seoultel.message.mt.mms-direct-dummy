package kr.co.seoultel.message.mt.mms.direct.entity;

import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import kr.co.seoultel.message.mt.mms.core.messages.Message;
import lombok.Builder;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Slf4j
@ToString
public class KtfSoapMessage extends SoapMessage {

    protected String serviceType;
    protected String mm7Version;
    protected String vaspId;
    protected String vasId;
    protected String cpid;
    protected String senderAddress;
    protected String callback;
    protected String receiver;
    protected String timeStamp;
    protected String subject;

    public KtfSoapMessage() {}

    @Builder
    protected KtfSoapMessage(String serviceType, String mm7Version, String vaspId, String vasId, String cpid, String senderAddress, String callback, String receiver, String timeStamp, String subject) {
        this.serviceType = serviceType;
        this.mm7Version = mm7Version;
        
        this.vaspId = vaspId;
        this.vasId = vasId;
        this.cpid = cpid;
        this.senderAddress = senderAddress;
        this.callback = callback;

        this.receiver = receiver;

        this.timeStamp = timeStamp;
        this.subject = subject;
    }
    
    @Override
    public void fromSOAPMessage(SOAPMessage soapMessage) throws SOAPException {
        SOAPBody soapBody = soapMessage.getSOAPBody();
        Document document = soapBody.extractContentAsDocument();

        // Get mm7:SubmitReq element
        Element submitReqElement = (Element) document.getElementsByTagName("mm7:SubmitReq").item(0);

        // Extract values from mm7:SubmitReq element
        this.serviceType = getElementValue(submitReqElement, "ServiceType");
        this.mm7Version = getElementValue(submitReqElement, "MM7Version");
        this.vaspId = getElementValue(submitReqElement, "VASPID");
        this.vasId = getElementValue(submitReqElement, "VASID");
        this.cpid = getElementValue(submitReqElement, "CPID");
        this.senderAddress = getElementValue(submitReqElement, "SenderAddress");
        this.callback = getElementValue(submitReqElement, "CallBack");
        this.receiver = getElementValue(submitReqElement, "Number");
        this.timeStamp = getElementValue(submitReqElement, "TimeStamp");
        this.subject = getElementValue(submitReqElement, "Subject");
    }
}
