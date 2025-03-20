package kr.co.seoultel.message.mt.mms.direct.domain.message.direct.ktf;

import jakarta.xml.soap.*;
import kr.co.seoultel.message.mt.mms.direct.common.exception.ErrorCode;
import kr.co.seoultel.message.mt.mms.direct.common.exception.MMSException;
import kr.co.seoultel.message.mt.mms.direct.common.util.DateUtil;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.Objects;

@Slf4j
@Getter
@Setter
@ToString
public class KtfSubmitReqMessage extends KtfSoapMessage {

    protected String tid;
    protected String vaspId;
    protected String vasId;
    protected String cpid;

    protected final String senderAddress = "01025971376";
    protected String callback;
    protected String receiver;

    protected String timeStamp;
    protected String subject;

    protected String resellerCode;


    public KtfSubmitReqMessage() throws MMSException {
        this.timeStamp = DateUtil.getDate(0, "dd-MM-yyyy HH:mm:ss");
    }

    @Builder
    public KtfSubmitReqMessage(String vaspId, String vasId, String cpid, String tid, String callback, String receiver, String subject, String resellerCode) throws MMSException {
        this.vaspId = vaspId;
        this.vasId = vasId;
        this.cpid = cpid;

        this.tid = tid;
        this.callback = callback;
        this.receiver = receiver;
        this.timeStamp = DateUtil.getDate(0, "dd-MM-yyyy HH:mm:ss");
        this.subject = subject;
        this.resellerCode = resellerCode;
    }

    @Override
    public SOAPMessage toSOAPMessage() throws Exception {
        /* Create SOAP message */
        SOAPMessage soapMessage = messageFactory.createMessage();
        soapMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
        soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "euc-kr");

        /* SOAP Part */
        SOAPPart soapPart = soapMessage.getSOAPPart();
        soapPart.setContentId(DirectProtocol.KTF_CONTENT_ID);
        soapPart.setMimeHeader("Content-Transfer-Encoding", "8bit");

        /* SOAP Envelope */
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.setPrefix("env");


        /* SOAP Header */
        SOAPHeader soapHeader = envelope.getHeader();
        soapHeader.setPrefix("env");
        soapHeader.addHeaderElement(new QName(DirectProtocol.KTF_TRANSACTION_ID_URL, "TransactionID", "mm7"))
                    .addTextNode(this.tid)
                    .setAttribute("env:mustUnderstand", "1");

        /* SOAP Body */
        SOAPBody soapBody = envelope.getBody();
        soapBody.setPrefix("env");

        SOAPBodyElement submitReq = soapBody.addBodyElement(new QName(DirectProtocol.KTF_TRANSACTION_ID_URL, "SubmitReq", "mm7"));
        submitReq.addChildElement("ServiceType").addTextNode(serviceType);
        submitReq.addChildElement("MM7Version").addTextNode(mm7Version);

        SOAPElement senderIdentification = submitReq.addChildElement("SenderIdentification");
        senderIdentification.addChildElement("VASPID").addTextNode(vaspId);
        senderIdentification.addChildElement("VASID").addTextNode(vasId);
        senderIdentification.addChildElement("CPID").addTextNode(cpid);
        senderIdentification.addChildElement("SenderAddress").addTextNode(senderAddress);   //고정값 (과금 번호)
        senderIdentification.addChildElement("CallBack").addTextNode(callback);

        submitReq.addChildElement("Recipients")
                .addChildElement("To")
                .addChildElement("Number")
                .addTextNode(receiver);

        submitReq.addChildElement("MessageClass").addTextNode(messageClass);
        submitReq.addChildElement("TimeStamp").addTextNode(timeStamp);
        submitReq.addChildElement("Subject").addTextNode(subject);
        submitReq.addChildElement("DeliveryReport").addTextNode(deliveryReport);
        submitReq.addChildElement("ReadReply").addTextNode(readReply);
        submitReq.addChildElement("ResellerCode").addTextNode(resellerCode);

        return soapMessage;
    }


    @Override
    public void fromSOAPMessage(SOAPMessage soapMessage) throws MMSException {
        try {
            SOAPHeader soapHeader = soapMessage.getSOAPHeader();
            // Directly access the TransactionID element by its QName
            SOAPElement transactionIdElement = (SOAPElement) soapHeader.getChildElements(new QName(DirectProtocol.KTF_TRANSACTION_ID_URL, "TransactionID", "mm7")).next();
            this.tid = transactionIdElement != null ? transactionIdElement.getValue() : null;

            SOAPBody soapBody = soapMessage.getSOAPBody();
            Document document = soapBody.extractContentAsDocument();

            // Get mm7:SubmitReq element
            Element submitReqElement = (Element) document.getElementsByTagName("mm7:SubmitReq").item(0);

            // Extract values from mm7:SubmitReq element
            this.vaspId = getElementValue(submitReqElement, "VASPID").orElse("");
            this.vasId = getElementValue(submitReqElement, "VASID").orElse("");
            this.cpid = getElementValue(submitReqElement, "CPID").orElse("");
            this.callback = getElementValue(submitReqElement, "CallBack").orElse("");
            this.receiver = getElementValue(submitReqElement, "Number").orElse("");
            this.timeStamp = getElementValue(submitReqElement, "TimeStamp").orElse("");
            this.subject = getElementValue(submitReqElement, "Subject").orElse("");
        } catch (Exception e) {
            throw new MMSException(null, ErrorCode.SUBMIT_REQ_SOAP_CREATE_ERROR, null);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        KtfSubmitReqMessage that = (KtfSubmitReqMessage) object;
        return Objects.equals(vaspId, that.vaspId) && Objects.equals(vasId, that.vasId) && Objects.equals(cpid, that.cpid) && Objects.equals(tid, that.tid) && Objects.equals(senderAddress, that.senderAddress) && Objects.equals(callback, that.callback) && Objects.equals(receiver, that.receiver) && Objects.equals(timeStamp, that.timeStamp) && Objects.equals(subject, that.subject) && Objects.equals(resellerCode, that.resellerCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vaspId, vasId, cpid, tid, senderAddress, callback, receiver, timeStamp, subject, resellerCode);
    }

    @Override
    public String toString() {
        return "KtfSubmitReqMessage{" +
                "vaspId='" + vaspId + '\'' +
                ", vasId='" + vasId + '\'' +
                ", cpid='" + cpid + '\'' +
                ", tid='" + tid + '\'' +
                ", senderAddress='" + senderAddress + '\'' +
                ", callback='" + callback + '\'' +
                ", receiver='" + receiver + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", subject='" + subject + '\'' +
                ", resellerCode='" + resellerCode + '\'' +
                '}';
    }
}

