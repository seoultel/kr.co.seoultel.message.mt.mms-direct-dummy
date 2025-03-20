package kr.co.seoultel.message.mt.mms.direct.domain.message.direct.skt;

import jakarta.xml.soap.*;
import kr.co.seoultel.message.mt.mms.direct.common.exception.ErrorCode;
import kr.co.seoultel.message.mt.mms.direct.common.exception.MMSException;
import kr.co.seoultel.message.mt.mms.direct.common.util.DateUtil;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.SktProtocol;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

@Slf4j
@Getter
@ToString
public class SktDeliveryReportReqMessage extends SktSoapMessage {

    protected String tid;
    protected String messageId;
    protected String receiver;
    protected String senderAddress;
    protected String statusCode;
    protected String statusText;


    public SktDeliveryReportReqMessage() throws MMSException {

    }

    @Builder
    public SktDeliveryReportReqMessage(String tid, String messageId, String receiver, String senderAddress, String statusCode, String statusText) throws MMSException {
        this.tid = tid;
        this.messageId = messageId;

        /* Receiver, Callback */
        this.receiver = receiver;
        this.senderAddress = senderAddress;

        /* statusCode, statusText */
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    @Override
    public SOAPMessage toSOAPMessage() throws Exception {
        // Create SOAP message
        SOAPMessage soapMessage = messageFactory.createMessage();
        soapMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
        soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "euc-kr");

        /* SOAP Part */
        SOAPPart soapPart = soapMessage.getSOAPPart();

        /* SOAP Envelope */
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.setPrefix("env");


        /* SOAP Header */
        SOAPHeader soapHeader = envelope.getHeader();
        soapHeader.setPrefix("env");
        soapHeader.addHeaderElement(new QName(DirectProtocol.SKT_TRANSACTION_ID_URL, "TransactionID", "mm7"))
                .addTextNode(tid)
                .setAttribute("env:mustUnderstand", "1");

        /* SOAP Body */
        SOAPBody soapBody = envelope.getBody();
        soapBody.setPrefix("env");

        SOAPBodyElement deliveryReportReq = soapBody.addBodyElement(new QName(DirectProtocol.SKT_TRANSACTION_ID_URL, SktProtocol.DELIVERY_REPORT_REQ, "mm7"));
        deliveryReportReq.addChildElement("MM7Version").addTextNode("5.3.0");
        deliveryReportReq.addChildElement("MessageID").addTextNode(messageId);

        SOAPElement recipient = deliveryReportReq.addChildElement("Recipient").addChildElement("Number").addTextNode(receiver);
        SOAPElement sender = deliveryReportReq.addChildElement("Sender").addTextNode(senderAddress);

        deliveryReportReq.addChildElement("CalledNet").addTextNode(DateUtil.getDate(0));

        SOAPElement status = deliveryReportReq.addChildElement("Status");
        status.addChildElement("StatusCode").addTextNode(statusCode);
        status.addChildElement("StatusText").addTextNode(statusText);

        return soapMessage;
    }

    @Override
    public void fromSOAPMessage(SOAPMessage soapMessage) throws MMSException {
        try {
            SOAPHeader soapHeader = soapMessage.getSOAPHeader();

            SOAPElement transactionIdElement = (SOAPElement) soapHeader.getChildElements(new QName(DirectProtocol.SKT_TRANSACTION_ID_URL, "TransactionID", "mm7")).next();
            this.tid = transactionIdElement != null ? transactionIdElement.getValue() : null;

            SOAPBody soapBody = soapMessage.getSOAPBody();
            Document document = soapBody.extractContentAsDocument();

            // Get mm7:DeliveryReportReq element
            String localpart = document.getDocumentElement().getTagName();
            Element DeliveryReportReqElement = (Element) document.getElementsByTagName(localpart).item(0);

            // Extract values from mm7:DeliveryReportReq element
            this.messageId = getElementValue(DeliveryReportReqElement, "MessageID").orElse("");
            this.senderAddress = getElementValue(DeliveryReportReqElement, "Sender").orElse("");
            this.receiver = getElementValue(DeliveryReportReqElement, "Number").orElse("");
            this.statusCode = getElementValue(DeliveryReportReqElement, "StatusCode").orElse("");
            this.statusText = getElementValue(DeliveryReportReqElement, "StatusText").orElse("");
        } catch (Exception e) {
            e.printStackTrace();
            throw new MMSException(null, ErrorCode.DELIVERY_REQ_SOAP_CREATE_ERROR, null);
        }
    }

    public boolean isTpsOver() {
        return (statusCode.equals("4006") |
                statusCode.equals("4008"));
    }

    public boolean isSuccess() {
        return statusCode.equals(SktProtocol.SUCCESS);
    }

}
