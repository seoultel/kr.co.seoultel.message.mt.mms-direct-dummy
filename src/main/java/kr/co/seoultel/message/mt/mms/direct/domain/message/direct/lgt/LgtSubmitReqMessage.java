package kr.co.seoultel.message.mt.mms.direct.domain.message.direct.lgt;

import jakarta.xml.soap.*;
import kr.co.seoultel.message.mt.mms.direct.common.exception.ErrorCode;
import kr.co.seoultel.message.mt.mms.direct.common.exception.MMSException;
import kr.co.seoultel.message.mt.mms.direct.common.util.DateUtil;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.LgtProtocol;
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
public class LgtSubmitReqMessage extends LgtSoapMessage {

    protected String tid;
    protected String vaspId;
    protected String vasId;
    protected String callback;
    protected String receiver;

    protected String timeStamp;
    protected String subject;
    protected String originCode;

    public LgtSubmitReqMessage() throws MMSException {
        super();
    }

    @Builder
    public LgtSubmitReqMessage(String tid, String vaspId, String vasId, String callback, String receiver, String subject, String originCode) throws MMSException {
        this.tid = tid;
        this.vaspId = vaspId;
        this.vasId = vasId;
        this.callback = callback;
        this.receiver = receiver;
        this.subject = subject;
        this.originCode = originCode;

        this.timeStamp = DateUtil.getDate(0, "dd-MM-yyyy HH:mm:ss");
    }

    @Override
    public SOAPMessage toSOAPMessage() throws Exception {
        SOAPMessage soapMessage = messageFactory.createMessage();
        soapMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
        soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "euc-kr");

        /* SOAP Part */
        SOAPPart soapPart = soapMessage.getSOAPPart();
        soapPart.setContentId(DirectProtocol.LGT_CONTENT_ID);
        soapPart.setMimeHeader("Content-Transfer-Encoding", "8bit");

        /* SOAP Envelope */
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.setPrefix("env");

        /* SOAP Header */
        SOAPHeader soapHeader = envelope.getHeader();
        soapHeader.setPrefix("env");
        soapHeader.addHeaderElement(new QName(DirectProtocol.LGT_TRANSACTION_ID_URL, "TransactionID", "mm7"))
                .addTextNode(this.tid)
                .setAttribute("env:mustUnderstand", "1");

        /* SOAP Body */
        SOAPBody soapBody = envelope.getBody();
        soapBody.setPrefix("env");

        SOAPBodyElement submitReq = soapBody.addBodyElement(new QName(DirectProtocol.KTF_TRANSACTION_ID_URL, LgtProtocol.SUBMIT_REQ, "mm7"));
        submitReq.addChildElement("MM7Version").addTextNode("5.3.0");

        SOAPElement senderIdentification = submitReq.addChildElement("SenderIdentification");
        senderIdentification.addChildElement("VASPID").addTextNode(vaspId);
        senderIdentification.addChildElement("VASID").addTextNode(vasId); // LGU+에서 발급한 문자열, CID 개념
        senderIdentification.addChildElement("SenderAddress").addTextNode("01083025800"); // 송신자 정보(과금 번호)
        senderIdentification.addChildElement("CallBack").addTextNode(callback);

        submitReq.addChildElement("Recipients")
                 .addChildElement("To")
                 .addChildElement("Number")
                 .addTextNode(receiver);

        submitReq.addChildElement("TimeStamp").addTextNode(timeStamp);

        /*
         * -> 현재 미처리 필드
         * submitReq.addChildElement("ServiceCode").addTextNode("0000");
         * submitReq.addChildElement("MessageClass").addTextNode("137");
         * submitReq.addChildElement("EarliestDeliveryTime").addTextNode(DateUtil.getDate(0, "dd-MM-yyyy HH:mm:ss")); // 현재 미처리
         * submitReq.addChildElement("DeliveryReport").addTextNode("TRUE");
         * submitReq.addChildElement("ReadReply").addTextNode("FALSE");
         * submitReq.addChildElement("ChargedParty").addTextNode("Sender");
         * submitReq.addChildElement("DistributionIndicator").addTextNode("0000"); // Content 가 메시지에 포함된 용도와 파일 이름을 포함
         */

        submitReq.addChildElement("KisaOrigCode").addTextNode(originCode);
        submitReq.addChildElement("Subject").addTextNode(subject);

        return soapMessage;
    }

    @Override
    public void fromSOAPMessage(SOAPMessage soapMessage) throws MMSException {
        try {
            SOAPHeader soapHeader = soapMessage.getSOAPHeader();

            SOAPElement transactionIdElement = (SOAPElement) soapHeader.getChildElements(new QName(DirectProtocol.KTF_TRANSACTION_ID_URL, "TransactionID", "mm7")).next();
            this.tid = transactionIdElement != null ? transactionIdElement.getValue() : null;

            SOAPBody soapBody = soapMessage.getSOAPBody();
            Document document = soapBody.extractContentAsDocument();

            // Get mm7:SubmitReq element
            Element submitReqElement = (Element) document.getElementsByTagName("mm7:SubmitReq").item(0);

            // Extract values from mm7:SubmitReq element
            this.vaspId = getElementValue(submitReqElement, "VASPID").orElse("");
            this.vasId = getElementValue(submitReqElement, "VASID").orElse("");
            this.callback = getElementValue(submitReqElement, "CallBack").orElse("");
            this.receiver = getElementValue(submitReqElement, "Number").orElse("");
            this.originCode = getElementValue(submitReqElement, "KisaOrigCode").orElse("");
            this.subject = getElementValue(submitReqElement, "Subject").orElse("");
        } catch (Exception e) {
            throw new MMSException(null, ErrorCode.SUBMIT_REQ_SOAP_CREATE_ERROR, null);
        }
    }
}
