package kr.co.seoultel.message.mt.mms.direct.common.exception;

import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.mt.mms.direct.domain.message.DeliveryType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class MMSException extends Exception {

    protected Throwable throwable;

    protected final String name = "SYSTEM";
    protected final DeliveryType deliveryType;
    protected final ErrorCode errorCode;
    protected final MessageDelivery messageDelivery;

    public MMSException(DeliveryType deliveryType, ErrorCode errorCode, MessageDelivery messageDelivery) {
        this.deliveryType = deliveryType;
        this.errorCode = errorCode;
        this.messageDelivery = messageDelivery;
    }

    public MMSException(DeliveryType deliveryType, ErrorCode errorCode, MessageDelivery messageDelivery, Throwable throwable) {
        this.deliveryType = deliveryType;
        this.errorCode = errorCode;
        this.messageDelivery = messageDelivery;
        this.throwable = throwable;
    }

}
