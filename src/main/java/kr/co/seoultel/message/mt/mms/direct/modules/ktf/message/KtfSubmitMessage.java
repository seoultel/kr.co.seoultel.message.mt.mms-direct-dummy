package kr.co.seoultel.message.mt.mms.direct.modules.ktf.message;

import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.ktf.KtfResMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.ktf.KtfSoapMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.ktf.KtfSubmitReqMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class KtfSubmitMessage extends KtfSoapMessage {
    protected KtfSubmitReqMessage ktfSubmitReqMessage;
    protected KtfResMessage ktfResMessage;

    public KtfSubmitMessage(KtfSubmitReqMessage ktfSubmitReqMessage, KtfResMessage ktfResMessage) throws SOAPException {
        super();

        this.ktfSubmitReqMessage = ktfSubmitReqMessage;
        this.ktfResMessage = ktfResMessage;
    }


    public String getCallback() {
        return ktfSubmitReqMessage.getCallback();
    }
    public String getReceiver() {
        return ktfSubmitReqMessage.getReceiver();
    }
    public String getTimeStamp() {
        return ktfSubmitReqMessage.getTimeStamp();
    }
    public String getSubject() {
        return ktfSubmitReqMessage.getSubject();
    }
    public String getResellerCode() {
        return ktfSubmitReqMessage.getResellerCode();
    }


    public String getTid() {
        return ktfResMessage.getTid();
    }
    public String getStatusCode() {
        return ktfResMessage.getStatusCode();
    }
    public String getStatusText() {
        return ktfResMessage.getStatusText();
    }
    public String getMessageId() {
        return ktfResMessage.getMessageId();
    }

    @Override
    @Deprecated
    public String convertSOAPMessageToString() throws SOAPException, IOException {
        return null;
    }

    @Override
    @Deprecated
    public SOAPMessage toSOAPMessage() throws SOAPException {
        return null;
    }

    @Override
    @Deprecated
    public void fromSOAPMessage(SOAPMessage soapMessage) throws SOAPException {

    }

    @Override
    public String toString() {
        return "KtfSubmitMessage{" +
                "ktfSubmitReqMessage=" + ktfSubmitReqMessage +
                ", ktfSubmitResMessage=" + ktfResMessage +
                '}';
    }
}
