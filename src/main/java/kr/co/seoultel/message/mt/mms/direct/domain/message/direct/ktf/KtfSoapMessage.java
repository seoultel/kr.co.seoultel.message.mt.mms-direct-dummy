package kr.co.seoultel.message.mt.mms.direct.domain.message.direct.ktf;

import kr.co.seoultel.message.mt.mms.direct.common.exception.MMSException;
import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.SoapMessage;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public abstract class KtfSoapMessage extends SoapMessage {

    protected final String serviceType = "MMSMT1";
    protected final String mm7Version = "1.0";


    protected final String messageClass = "GENERAL";
    protected final String deliveryReport = "TRUE";
    protected final String readReply = "FALSE";

    public KtfSoapMessage() throws MMSException {
        super();
    }
}
