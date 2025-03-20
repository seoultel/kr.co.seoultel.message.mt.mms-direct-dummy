package kr.co.seoultel.message.mt.mms.direct.domain.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeliveryState {
    READY(0, "READY"), SENDING(1, "SENDING"), SUBMIT(2, "SUBMIT"), SUCCESS(3, "SUBMIT_ACK"),
    FAILED(4, "FAILED"), RETRY(5, "RETRY"),
    FALLBACK_READY(6, "FALLBACK_RETRY"), FALLBACK_SENDING(7, "FALLBACK_SENDING"), FALLBACK_SUBMIT(8, "FALLBACK_SUBMIT"),
    FALLBACK_SUCCESS(9, "FALLBACK_SUCCESS"), FALLBACK_FAILED(10, "FALLBACK_FAILED"), FALLBACK_RETRY(11, "FALLBACK_RETRY");

    private final int state;
    private final String stateEng;

    public static String getDeliveryStateEng(DeliveryState deliveryState) {
        return deliveryState.getStateEng();
    }
}
