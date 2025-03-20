package kr.co.seoultel.message.mt.mms.direct.domain.protocol;

public class SktProtocol {

    public static final String SUBMIT_REQ = "SubmitReq";
    public static final String SUBMIT_RES = "SubmitRsp";
    public static final String DELIVERY_REPORT_REQ = "DeliverReportReq";
    public static final String DELIVERY_REPORT_RES = "DeliveryReportRsp";
    public static final String READ_REPLY_REQ = "ReadReplyReq";
    public static final String READ_REPLY_RES = "ReadReplyRsp";
    public static final String RS_ERROR_RES = "RSErrorRsp";
    public static final String VASP_ERROR_RES = "VaspErrorRsp";


















    public final static String SUCCESS = "1000"; // 성공
    public final static String ADDRESSS_ERROR = "2002"; // CID 및 발송IP 인증 실패
    public final static String ORIG_ADDRESS_ERROR = "2003"; // 최초발신사업자 코드 인증 실패
    public final static String MULTIMEDIA_CONTENT_REFUSED = "2004"; // 지원하지 않는 미디어 타입이거나 MIME 형식 오류
    public final static String MESSAGE_ID_NOT_FOUND = "2005"; // Message ID가 없음
    public final static String MESSAGE_FORMAT_CORRUPT = "2007"; // 메시지 포맷 혹은 값 오류
    public final static String CONTENT_ERROR = "2101"; // 올바른 컨텐츠가 아님
    public final static String CLIENT_ERROR = "2102"; // 일시적인 단말 문제
    public final static String CLIENT_UNAVAILABLE = "2103"; // 미지원 단말
    public final static String CONTENT_SIZE_ERROR = "2104"; // 컨텐츠 크기가 커서 처리할 수 없음
    public final static String SERVICE_ERROR = "4005"; // 일반적인 서비스 에러
    public final static String SERVICE_UNAVAILABLE = "4006"; // 사용자가 많아 일시적인 서비스 불가 (Timeout)
    public final static String SERVICE_DENIED = "4007"; // 서비스를 요청한 client가 permission이 없는 경우
    public final static String EXCEED_MAX_TRANS = "4008"; // CID에 등록 된 전송량 초과로 실패 처리 (전송되지 않음)
    public final static String SERVICE_CHECK = "4101"; // 서비스 점검중인 경우

}
