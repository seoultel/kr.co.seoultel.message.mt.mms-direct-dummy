package kr.co.seoultel.message.mt.mms.direct.common.util;


import kr.co.seoultel.message.mt.mms.direct.domain.protocol.SktProtocol;

public class SktUtil {

    public static String getRandomStatusCodeBySubmitAck() {
        int random = RandomUtil.getRandomNumberInRange(0, 100);

        String statusCode;
        if (random <= 95) {
            return SktProtocol.SUCCESS; // 성공
        } else {
            String[] statusCodes = new String[]{
                // SktProtocol.ADDRESSS_ERROR,  // CID 및 발송IP 인증 실패
                SktProtocol.ORIG_ADDRESS_ERROR,  // 최초발신사업자 코드 인증 실패
                SktProtocol.MULTIMEDIA_CONTENT_REFUSED, // 지원하지 않는 미디어 타입이거나 MIME 형식 오류
                SktProtocol.MESSAGE_ID_NOT_FOUND, // Message ID가 없음
                SktProtocol.MESSAGE_FORMAT_CORRUPT, // 메시지 포맷 혹은 값 오류
                SktProtocol.CONTENT_ERROR, // 올바른 컨텐츠가 아님
                SktProtocol.CLIENT_ERROR, // 일시적인 단말 문제 예> 단말 SMS 전송 실패"
                SktProtocol.CLIENT_UNAVAILABLE, // 미지원 단말
                SktProtocol.CONTENT_SIZE_ERROR,  // 컨텐츠 크기가 커서 처리할 수 없음
                SktProtocol.SERVICE_ERROR, // 일반적인 서비스 에러
                SktProtocol.SERVICE_UNAVAILABLE, // 사용자가 많아 일시적인 서비스 불가 (Timeout)
                // SktProtocol.SERVICE_DENIED, // 서비스를 요청한 client가 permission이 없는 경우
                // SktProtocol.EXCEED_MAX_TRANS, // CID에 등록 된 전송량 초과로 실패 처리 (전송되지 않음)
                SktProtocol.SERVICE_CHECK // 서비스 점검중인 경우
            };

            return RandomUtil.getRandomStr(statusCodes);
        }
    }

    public static String getRandomStatusCodeByReport() {
        int random = RandomUtil.getRandomNumberInRange(0, 100);

        String statusCode;
        if (random >= 85) {
            return SktProtocol.SUCCESS;
        } else {
            String[] statusCodes = new String[]{
                    // SktProtocol.ADDRESSS_ERROR,  // CID 및 발송IP 인증 실패
                    SktProtocol.ORIG_ADDRESS_ERROR,  // 최초발신사업자 코드 인증 실패
                    SktProtocol.MULTIMEDIA_CONTENT_REFUSED, // 지원하지 않는 미디어 타입이거나 MIME 형식 오류
                    SktProtocol.MESSAGE_ID_NOT_FOUND, // Message ID가 없음
                    SktProtocol.MESSAGE_FORMAT_CORRUPT, // 메시지 포맷 혹은 값 오류
                    SktProtocol.CONTENT_ERROR, // 올바른 컨텐츠가 아님
                    SktProtocol.CLIENT_ERROR, // 일시적인 단말 문제 예> 단말 SMS 전송 실패"
                    SktProtocol.CLIENT_UNAVAILABLE, // 미지원 단말
                    SktProtocol.CONTENT_SIZE_ERROR,  // 컨텐츠 크기가 커서 처리할 수 없음
                    SktProtocol.SERVICE_ERROR, // 일반적인 서비스 에러
                    SktProtocol.SERVICE_UNAVAILABLE, // 사용자가 많아 일시적인 서비스 불가 (Timeout)
                    // SktProtocol.SERVICE_DENIED, // 서비스를 요청한 client가 permission이 없는 경우
                    // SktProtocol.EXCEED_MAX_TRANS, // CID에 등록 된 전송량 초과로 실패 처리 (전송되지 않음)
                    SktProtocol.SERVICE_CHECK // 서비스 점검중인 경우
            };

            return RandomUtil.getRandomStr(statusCodes);
        }
    }

    public static String getStatusCodeKor(String statusCode) {
        switch (statusCode) {
            case SktProtocol.SUCCESS:
                return "SUCCESS";
            case SktProtocol.ADDRESSS_ERROR:
                return "ADDRESSS_ERROR";
            case SktProtocol.ORIG_ADDRESS_ERROR:
                return "ORIG_ADDRESS_ERROR";
            case SktProtocol.MULTIMEDIA_CONTENT_REFUSED:
                return "MULTIMEDIA_CONTENT_REFUSED";
            case SktProtocol.MESSAGE_ID_NOT_FOUND:
                return "MESSAGE_ID_NOT_FOUND";
            case SktProtocol.MESSAGE_FORMAT_CORRUPT:
                return "MESSAGE_FORMAT_CORRUPT";
            case SktProtocol.CONTENT_ERROR:
                return "CONTENT_ERROR";
            case SktProtocol.CLIENT_ERROR:
                return "CLIENT_ERROR";
            case SktProtocol.CLIENT_UNAVAILABLE:
                return "CLIENT_UNAVAILABLE";
            case SktProtocol.CONTENT_SIZE_ERROR:
                return "CONTENT_SIZE_ERROR";
            case SktProtocol.SERVICE_ERROR:
                return "SERVICE_ERROR";
            case SktProtocol.SERVICE_UNAVAILABLE:
                return "SERVICE_UNAVAILABLE";
            case SktProtocol.SERVICE_DENIED:
                return "SERVICE_DENIED";
            case SktProtocol.EXCEED_MAX_TRANS:
                return "EXCEED_MAX_TRANS";

            case SktProtocol.SERVICE_CHECK:
            default:
                return "SERVICE_CHECK";
        }
    }
}
