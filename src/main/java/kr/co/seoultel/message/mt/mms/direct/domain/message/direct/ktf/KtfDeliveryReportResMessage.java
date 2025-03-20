package kr.co.seoultel.message.mt.mms.direct.domain.message.direct.ktf;

import jakarta.xml.soap.*;
import kr.co.seoultel.message.mt.mms.direct.common.exception.ErrorCode;
import kr.co.seoultel.message.mt.mms.direct.common.exception.MMSException;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.KtfProtocol;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

@Getter
@Setter
public class KtfDeliveryReportResMessage extends KtfSoapMessage {

    protected String tid;
    protected String messageId;
    protected String statusCode;
    protected String statusText;

    @Builder
    public KtfDeliveryReportResMessage(String tid, String messageId, String statusCode, String statusText) throws MMSException {
        this.tid = tid;
        this.messageId = messageId;
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    @Override
    public SOAPMessage toSOAPMessage() throws MMSException {
        try {
            // Create SOAP message
            SOAPMessage soapMessage = messageFactory.createMessage();
            soapMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
            soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "euc-kr");

            SOAPPart soapPart = soapMessage.getSOAPPart();

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

            SOAPBodyElement deliveryReportRsp = soapBody.addBodyElement(new QName(DirectProtocol.KTF_REPORT_TRANSACTION_ID_URL, KtfProtocol.DELIVERY_REPORT_RES, "mm7"));

            SOAPElement status = deliveryReportRsp.addChildElement("Status");
            status.addChildElement("StatusCode").addTextNode(statusCode);
            status.addChildElement("StatusText").addTextNode(statusText);

            deliveryReportRsp.addChildElement("MM7Version").addTextNode(mm7Version);

            return soapMessage;
        } catch (Exception e) {
            throw new MMSException(null, ErrorCode.DELIVERY_RES_SOAP_ERROR, null);
        }
    }

    @Override
    public void fromSOAPMessage(SOAPMessage soapMessage) throws MMSException {
        try {
            SOAPHeader soapHeader = soapMessage.getSOAPHeader();
            SOAPElement transactionIdElement = (SOAPElement) soapHeader.getChildElements(new QName(DirectProtocol.KTF_REPORT_TRANSACTION_ID_URL, "TransactionID", "mm7")).next();
            this.tid = transactionIdElement != null ? transactionIdElement.getValue() : null;

            SOAPBody soapBody = soapMessage.getSOAPBody();
            Document document = soapBody.extractContentAsDocument();

            // Get mm7:DeliveryReportRes element
            Element deliveryReportRspElement = (Element) document.getElementsByTagName(KtfProtocol.DELIVERY_REPORT_RES).item(0);


                // Extract values from mm7:SubmitReq element
            this.statusCode = getElementValue(deliveryReportRspElement, "StatusCode").orElse("");
            this.statusText = getElementValue(deliveryReportRspElement, "StatusText").orElse("");
            this.messageId = getElementValue(deliveryReportRspElement, "MessageID").orElse("");
        } catch (Exception e) {
            throw new MMSException(null, ErrorCode.DELIVERY_RES_SOAP_ERROR, null);
        }
    }
}
