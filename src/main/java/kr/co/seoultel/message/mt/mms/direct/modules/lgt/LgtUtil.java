package kr.co.seoultel.message.mt.mms.direct.modules.lgt;

import kr.co.seoultel.message.mt.mms.core.common.protocol.LgtProtocol;
import kr.co.seoultel.mms.server.core.util.TestUtil;

public class LgtUtil {

    public static String getRandomStatusCodeBySubmitAck() {
        int random = TestUtil.getRandomNumberInRange(0, 100);

        String statusCode;
        if (random >= 85) {
            return TestUtil.getRandomStr(LgtProtocol.SUCCESS, LgtProtocol.PARTIAL_SUCCESS);
        } else {
            String[] statusCodes = new String[]{
                    LgtProtocol.CLIENT_ERROR,
                    LgtProtocol.OPERATION_RESTRICTED,
                    LgtProtocol.ADDRESS_ERROR,
                    LgtProtocol.ADDRESS_NOT_FOUND,
                    LgtProtocol.MULTI_MEDIA_CONTENT_REFUSED,
                    LgtProtocol.MESSAGE_ID_NOT_FOUND,
                    LgtProtocol.LINKED_ID_NOT_FOUND,
                    LgtProtocol.MESSAGE_FORMAT_CORRUPT,
                    LgtProtocol.SERVER_ERROR,
                    LgtProtocol.NOT_POSSIBLE,
                    LgtProtocol.MESSAGE_REJECTED,
                    LgtProtocol.MULTIPLE_ADDRESSED_NOT_SUPPORTED,
                    LgtProtocol.GENERAL_SERVICE_ERROR,
                    LgtProtocol.IMPROPER_IDENTIFICATION,
                    LgtProtocol.UNSUPPORTED_VERSION,
                    LgtProtocol.UNSUPPORTED_OPERATION,
                    LgtProtocol.VALIDATION_ERROR,
                    LgtProtocol.SERVICE_ERROR,
                    LgtProtocol.SERVICE_UNAVAILBALE,
                    LgtProtocol.SERVICE_DENIED,
                    LgtProtocol.SYSTEM_ERROR,
                    LgtProtocol.KISACODE_ERROR,
                    LgtProtocol.SUBS_REJECT,
                    LgtProtocol.INVALID_AUTH_PASSWORD,
                    LgtProtocol.EXPIRED_PASSWORD,
                    LgtProtocol.MMS_DISABLE_SUBS,
                    // LgtProtocol.TRAFFIC_IS_OVER
            };

            return TestUtil.getRandomStr(statusCodes);
        }
    }

    public static String getRandomStatusCodeByReport() {
        int random = TestUtil.getRandomNumberInRange(0, 100);

        String statusCode;
        if (random >= 85) {
            return TestUtil.getRandomStr(LgtProtocol.SUCCESS, LgtProtocol.PARTIAL_SUCCESS);
        } else {
            String[] statusCodes = new String[]{
                    LgtProtocol.CLIENT_ERROR,
                    LgtProtocol.OPERATION_RESTRICTED,
                    LgtProtocol.ADDRESS_ERROR,
                    LgtProtocol.ADDRESS_NOT_FOUND,
                    LgtProtocol.MULTI_MEDIA_CONTENT_REFUSED,
                    LgtProtocol.MESSAGE_ID_NOT_FOUND,
                    LgtProtocol.LINKED_ID_NOT_FOUND,
                    LgtProtocol.MESSAGE_FORMAT_CORRUPT,
                    LgtProtocol.SERVER_ERROR,
                    LgtProtocol.NOT_POSSIBLE,
                    LgtProtocol.MESSAGE_REJECTED,
                    LgtProtocol.MULTIPLE_ADDRESSED_NOT_SUPPORTED,
                    LgtProtocol.GENERAL_SERVICE_ERROR,
                    LgtProtocol.IMPROPER_IDENTIFICATION,
                    LgtProtocol.UNSUPPORTED_VERSION,
                    LgtProtocol.UNSUPPORTED_OPERATION,
                    LgtProtocol.VALIDATION_ERROR,
                    LgtProtocol.SERVICE_ERROR,
                    LgtProtocol.SERVICE_UNAVAILBALE,
                    LgtProtocol.SERVICE_DENIED,
                    LgtProtocol.SYSTEM_ERROR,
                    LgtProtocol.KISACODE_ERROR,
                    LgtProtocol.SUBS_REJECT,
                    LgtProtocol.INVALID_AUTH_PASSWORD,
                    LgtProtocol.EXPIRED_PASSWORD,
                    LgtProtocol.MMS_DISABLE_SUBS,
                    // LgtProtocol.TRAFFIC_IS_OVER
            };

            return TestUtil.getRandomStr(statusCodes);
        }
    }

    public static String getLgtResultMessageKor(String statusCode) {
        switch (statusCode) {
            case "1000":
                return LgtProtocol.SUCCESS;
            case "1100":
                return LgtProtocol.PARTIAL_SUCCESS;
            case "2000":
                return LgtProtocol.CLIENT_ERROR;
            case "2001":
                return LgtProtocol.OPERATION_RESTRICTED;
            case "2002":
                return LgtProtocol.ADDRESS_ERROR;
            case "2003":
                return LgtProtocol.ADDRESS_NOT_FOUND;
            case "2004":
                return LgtProtocol.MULTI_MEDIA_CONTENT_REFUSED;
            case "2005":
                return LgtProtocol.MESSAGE_ID_NOT_FOUND;
            case "2006":
                return LgtProtocol.LINKED_ID_NOT_FOUND;
            case "2007":
                return LgtProtocol.MESSAGE_FORMAT_CORRUPT;
            case "3001":
                return LgtProtocol.NOT_POSSIBLE;
            case "3002":
                return LgtProtocol.MESSAGE_REJECTED;
            case "3003":
                return LgtProtocol.MULTIPLE_ADDRESSED_NOT_SUPPORTED;
            case "4000":
                return LgtProtocol.GENERAL_SERVICE_ERROR;
            case "4001":
                return LgtProtocol.IMPROPER_IDENTIFICATION;
            case "4002":
                return LgtProtocol.UNSUPPORTED_VERSION;
            case "4003":
                return LgtProtocol.UNSUPPORTED_OPERATION;
            case "4004":
                return LgtProtocol.VALIDATION_ERROR;
            case "4005":
                return LgtProtocol.SERVICE_ERROR;
            case "4006":
                return LgtProtocol.SERVICE_UNAVAILBALE;
            case "4007":
                return LgtProtocol.SERVICE_DENIED;
            case "4030":
                return LgtProtocol.SYSTEM_ERROR;
            case "4032":
                return LgtProtocol.KISACODE_ERROR;
            case "6014":
                return LgtProtocol.SUBS_REJECT;
            case "6024":
                return LgtProtocol.INVALID_AUTH_PASSWORD;
            case "6025":
                return LgtProtocol.EXPIRED_PASSWORD;
            case "6072":
                return LgtProtocol.MMS_DISABLE_SUBS;
            case "7103":
                return LgtProtocol.TRAFFIC_IS_OVER;

            default:
            case "3000":
                return LgtProtocol.SERVER_ERROR;
        }
    }


}
