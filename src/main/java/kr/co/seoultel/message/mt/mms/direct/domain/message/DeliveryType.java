package kr.co.seoultel.message.mt.mms.direct.domain.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeliveryType {
    SUBMIT(1, "SUBMIT"), SUBMIT_ACK(2, "SUBMIT_ACK"),
    REPORT(3, "REPORT"), REPORT_ACK(4, "REPORT_ACK"),
    FALLBACK_SUBMIT(6, "FALLBACK_SUBMIT"), FALLBACK_SUBMIT_ACK(7, "FALLBACK_SUBMIT_ACK"),
    FALLBACK_REPORT(8, "FALLBACK_REPORT"), FALLBACK_REPORT_ACK(9, "FALLBACK_REPORT_ACK");


    private final int type;
    private final String typeEng;

    public static String getDeliveryTypeEng(DeliveryType deliveryType) {
        return deliveryType.getTypeEng();
    }
}
