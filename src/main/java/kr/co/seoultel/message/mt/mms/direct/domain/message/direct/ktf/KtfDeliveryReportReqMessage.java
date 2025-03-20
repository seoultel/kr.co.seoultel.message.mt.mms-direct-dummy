package kr.co.seoultel.message.mt.mms.direct.domain.message.direct.ktf;

import jakarta.xml.soap.*;
import kr.co.seoultel.message.mt.mms.direct.common.exception.ErrorCode;
import kr.co.seoultel.message.mt.mms.direct.common.exception.MMSException;
import kr.co.seoultel.message.mt.mms.direct.common.util.DateUtil;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.KtfProtocol;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.Objects;

@Slf4j
@Getter
@Setter
public class KtfDeliveryReportReqMessage extends KtfSoapMessage {

    protected String tid;
    protected String messageId;
    protected String receiver;
    protected String callback;
    protected String timeStamp;
    protected String mmStatus;

    public KtfDeliveryReportReqMessage() throws MMSException {
        super();
    }

    @Builder
    public KtfDeliveryReportReqMessage(String tid, String messageId, String receiver, String callback, String mmStatus) throws MMSException {
        this.tid = tid;
        this.messageId = messageId;
        this.receiver = receiver;
        this.callback = callback;
        this.mmStatus = mmStatus;
    }

    public boolean isSuccess() {
        return mmStatus.equals(KtfProtocol.KTF_REPORT_SUCCESS) || mmStatus.equals(KtfProtocol.KTF_REPORT_SUCCESS_ANSIM);
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
        soapHeader.addHeaderElement(new QName(DirectProtocol.KTF_REPORT_TRANSACTION_ID_URL, "TransactionID", "mm7"))
                    .addTextNode(tid)
                    .setAttribute("env:mustUnderstand", "1");

        /* SOAP Body */
        SOAPBody soapBody = envelope.getBody();
        soapBody.setPrefix("env");

        /*
         * TODO : KTF-TRANSACTION_ID_URL 수정 필요할지도 모름
         *        NOW : "http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-5-MM7-1-2"
         *            -> TO BE ? http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-5-MM7-1-0
         */
        SOAPBodyElement deliveryReportReq = soapBody.addBodyElement(new QName(DirectProtocol.KTF_REPORT_TRANSACTION_ID_URL, KtfProtocol.DELIVERY_REPORT_REQ, "mm7"));
        deliveryReportReq.addChildElement("MM7Version").addTextNode(mm7Version);
        deliveryReportReq.addChildElement("MessageID").addTextNode(messageId);

        SOAPElement recipient = deliveryReportReq.addChildElement("Recipient");
        recipient.addChildElement("Number").addTextNode(receiver);

        SOAPElement sender = deliveryReportReq.addChildElement("Sender");
        sender.addChildElement("Number").addTextNode(callback);

        deliveryReportReq.addChildElement("TimeStamp").addTextNode(DateUtil.getDate("dd-MM-yyyy HH:mm:ss"));
        deliveryReportReq.addChildElement("MMStatus").addTextNode(mmStatus);

        return soapMessage;
    }


    @Override
    public void fromSOAPMessage(SOAPMessage soapMessage) throws MMSException {
        try {
            SOAPHeader soapHeader = soapMessage.getSOAPHeader();

            SOAPElement transactionIdElement = (SOAPElement) soapHeader.getChildElements(new QName(DirectProtocol.KTF_REPORT_TRANSACTION_ID_URL, "TransactionID", "mm7")).next();
            this.tid = transactionIdElement != null ? transactionIdElement.getValue() : null;

            SOAPBody soapBody = soapMessage.getSOAPBody();
            Document document = soapBody.extractContentAsDocument();

            // Get mm7:DeliveryReportReq element
            String localpart = document.getDocumentElement().getTagName();
            Element deliveryReportReqElement = (Element) document.getElementsByTagName(localpart).item(0);

            if (localpart.contains(KtfProtocol.DELIVERY_REPORT_REQ)) {
                // Extract values from mm7:DeliveryReportReq element
                this.messageId = getElementValue(deliveryReportReqElement, "MessageID").orElse("");

                Element recipientElement = (Element) document.getElementsByTagName("Recipient").item(0);
                Element recipientNumberElement = (Element) recipientElement.getElementsByTagName("Number").item(0);
                this.receiver = recipientNumberElement.getTextContent().trim();

                Element senderElement = (Element) document.getElementsByTagName("Sender").item(0);
                Element senderNumberElement = (Element) senderElement.getElementsByTagName("Number").item(0);
                this.callback = senderNumberElement.getTextContent().trim();

                this.timeStamp = getElementValue(deliveryReportReqElement, "TimeStamp").orElse("");
                this.mmStatus = getElementValue(deliveryReportReqElement, "MMStatus").orElse("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MMSException(null, ErrorCode.DELIVERY_REQ_SOAP_CREATE_ERROR, null);
        }
    }



    public boolean isTpsOver() {
        return this.mmStatus.equals(KtfProtocol.KTF_REPORT_HUB_OVER_INTRAFFIC);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        KtfDeliveryReportReqMessage that = (KtfDeliveryReportReqMessage) object;
        return Objects.equals(tid, that.tid) && Objects.equals(messageId, that.messageId) && Objects.equals(receiver, that.receiver) && Objects.equals(callback, that.callback) && Objects.equals(timeStamp, that.timeStamp) && Objects.equals(mmStatus, that.mmStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tid, messageId, receiver, callback, timeStamp, mmStatus);
    }

    @Override
    public String toString() {
        return "KtfDeliveryReportReqMessage{" +
                "tid='" + tid + '\'' +
                ", messageId='" + messageId + '\'' +
                ", receiver='" + receiver + '\'' +
                ", callback='" + callback + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", mmStatus='" + mmStatus + '\'' +
                '}';
    }
}

