package kr.co.seoultel.message.mt.mms.direct.domain.message.direct;

import jakarta.xml.soap.SOAPMessage;
import kr.co.seoultel.message.mt.mms.direct.common.exception.MMSException;

public interface ConvertableToSOAPMessage {

    SOAPMessage toSOAPMessage() throws Exception;
    void fromSOAPMessage(SOAPMessage soapMessage) throws MMSException;
}
