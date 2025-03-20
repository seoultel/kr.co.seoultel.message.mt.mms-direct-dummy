package kr.co.seoultel.message.mt.mms.direct.domain.message.direct.lgt;

import kr.co.seoultel.message.mt.mms.direct.common.exception.MMSException;
import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.SoapMessage;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
public abstract class LgtSoapMessage extends SoapMessage {

    public LgtSoapMessage() throws MMSException {
        super();
    }
}

