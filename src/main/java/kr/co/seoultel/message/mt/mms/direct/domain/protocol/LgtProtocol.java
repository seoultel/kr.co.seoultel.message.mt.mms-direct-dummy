package kr.co.seoultel.message.mt.mms.direct.domain.protocol;

public class LgtProtocol {

    public static final String SUBMIT_REQ = "SubmitReq";
    public static final String SUBMIT_RES = "SubmitRsp";
    public static final String DELIVERY_REPORT_REQ = "DeliveryReportReq";
    public static final String DELIVERY_REPORT_RES = "DeliveryReportRsp";
    public static final String READ_REPLY_REQ = "ReadReplyReq";
    public static final String READ_REPLY_RES = "ReadReplyRsp";
    public static final String RS_ERROR_RES = "RSErrorRsp";
    public static final String VASP_ERROR_RES = "VaspErrorRsp";


    public static final String SUCCESS = "1000";    // 메시지가 성공적으로 처리 되었음
    public static final String PARTIAL_SUCCESS = "1100";  // 메시지가 부분적으로 실행 되었으나 일부는 처리되지 못했음.
    public static final String CLIENT_ERROR = "2000";   // Client 가 잘못된 응답을 보냄
    public static final String OPERATION_RESTRICTED = "2001";   // 허용되지 않은 command 실행에 의해 메시지가 거부됨
    public static final String ADDRESS_ERROR = "2002"; // 메시지에 있는 주소가 잘못된 형식이거나 유효하지 않음. 메시지 수신자가 다수일 경우 적어도 한 개의 주소가 잘못되어도 응답을 줌
    public static final String ADDRESS_NOT_FOUND = "2003";  // 메시지에 있는 주소를 MMS Relay/Server 가 찾을 수 없음. 이 코드는 메시지가 전송될 주소를 찾을 수 없을 때 리턴 됨
    public static final String MULTI_MEDIA_CONTENT_REFUSED = "2004"; // 1) SOAP 메시지에 포함된 MIME content의 요소나 크기, 타입이 불분명하거나 2) 이미지 크기가 허용된 크기보다 초과하여 전송하는 경우에 리턴
    public static final String MESSAGE_ID_NOT_FOUND = "2005"; // MMS Relay/Server가 이전에 전송된 메시지에 대한 message ID를 찾을 수 없거나 VASP로 부터 받은 응답에서 message ID를 찾을 수 없음.
    public static final String LINKED_ID_NOT_FOUND = "2006";  // MMS Relay/Server가 메시지에 있는 LinkedID 를 찾을 수 없음.
    public static final String MESSAGE_FORMAT_CORRUPT = "2007"; // 메시지가 규격에 맞지 않거나 부적당함
    public static final String SERVER_ERROR = "3000"; // 서버에서 올바른 요청에 대한 처리를 실패함
    public static final String NOT_POSSIBLE = "3001"; // 메시지 처리가 불가능함. 이 코드는 메시지 가 더 이상 유효하지 않거나 취소된 것에 대한 결과임. 메시지가 이미 처리 되었거나 더 이상 유효하지 않아서 MMS Relay/Serve가 처리할 수 없음.
    public static final String MESSAGE_REJECTED = "3002";  // 서버에서 메시지를 받아들일 수 없음
    public static final String MULTIPLE_ADDRESSED_NOT_SUPPORTED = "3003"; // MMS Relay/Server가 multiple recipients를 지원하지 않음
    public static final String GENERAL_SERVICE_ERROR = "4000"; // 요구된 서비스가 실행될 수 없음
    public static final String IMPROPER_IDENTIFICATION = "4001"; // 메시지의 Identification header가 client를 확인할 수 없음 (VASP나 MMS Relay/Server)
    public static final String UNSUPPORTED_VERSION = "4002"; // 메시지에 있는 MM7 version 이 지원되지 않는 version임
    public static final String UNSUPPORTED_OPERATION = "4003"; // 메시지 헤더에 있는 Message Type 이 서버에서 지원되지 않음
    public static final String VALIDATION_ERROR = "4004"; // 필수적인 field가 빠졌거나 message-format 이 맞지 않아 XML로 된 SOAP 메시지를 parsing할 수 없음
    public static final String SERVICE_ERROR = "4005"; // 서버(MMS Relay/Server 나 VASP)에서 메시지 처리에 실패하여 재전송 할 수 없음.
    public static final String SERVICE_UNAVAILBALE = "4006"; // 서비스가 일시적으로 되지 않음. 서버에서 응답이 없음
    public static final String SERVICE_DENIED = "4007"; // Client에게 요청된 작업에 대한 허가가 나있지 않음
    public static final String SYSTEM_ERROR = "4030"; // 서버 과부하로 인해 일시적으로 메시지 수신불가, 해당 VASID로 문자 전송을 중지하고 1분 이후 재전송 해야 함
    public static final String KISACODE_ERROR = "4032"; // 최초 발신 사업자 코드가 규격에 맞지 않아 문자 전송 불가능
    public static final String SUBS_REJECT = "6014"; // 수신자가 착신거절 신청자임
    public static final String INVALID_AUTH_PASSWORD = "6024"; // CP 보안 인증 실패
    public static final String EXPIRED_PASSWORD = "6025"; // CP 인증 Password 유효 기간 만료
    public static final String MMS_DISABLE_SUBS = "6072"; // MMS 비가용 단말
    public static final String SUBS_IS_PORTED = "6102"; // PORT-OUT
    public static final String TRAFFIC_IS_OVER = "7103"; // 1:1 메시지 전송 시 허용된 트래픽을 초과 하여 전송 하는 경우
}
