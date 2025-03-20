package kr.co.seoultel.message.mt.mms.direct.domain.message.direct.skt;

import kr.co.seoultel.message.mt.mms.direct.common.exception.MMSException;
import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.SoapMessage;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
public abstract class SktSoapMessage extends SoapMessage {


    protected final String mm7Version = "5.3.0";
    protected final String messageClass = "Personal";
    protected final String deliveryReport = "True";
    protected final String readReply = "False";
    protected final String priority = "Normal";
    protected final String distributionIndicator = "False";

    public SktSoapMessage() throws MMSException {
        super();
    }
}

