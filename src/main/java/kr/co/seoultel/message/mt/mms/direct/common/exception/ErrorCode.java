package kr.co.seoultel.message.mt.mms.direct.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    /**
     *  FORMAT EXCEPTION
     */
    UMS_MSG_ID_FORMAT_ERROR("62021","UMS_MSG_ID_FORMAT_ERROR"),
    PHN_NBR_FORMAT_ERROR("62022", ""),
    ORIGIN_CODE_FORMAT_ERROR("62023", ""),
    FILE_COUNT_OVER_ERROR("62041", "FILE_COUNT_OVER"), /** 포맷 예외로 바뀌었음. */

    /**
     * SOAP EXCEPTION
     */
    MESSAGE_FACTORY_CREATE_ERROR("62080", "FAIL_TO_CREATE_MSG_FACTORY"),
    SUBMIT_REQ_SOAP_CREATE_ERROR("62081", "SBM_REQ_SOAP_ERR"),
    SUBMIT_RES_SOAP_CREATE_ERROR("62082", "SBM_RES_SOAP_ERR"),
    DELIVERY_REQ_SOAP_CREATE_ERROR("62083", "DLV_REQ_SOAP_ERR"),
    DELIVERY_RES_SOAP_ERROR("62084", "DLV_RES_SOAP_ERR"),


    /** FILE SERVER */
    FILE_EXPIRED_ERROR("62042", "FILE_IS_EXPIRED"),
    FILE_NOT_FOUND("62043", "FILE_NOT_FOUND"),
    FILE_SIZE_OVER_ERROR("62044", "FILE_SIZE_OVER"),
    FILE_SERVER_UNKNOWN_ERROR("62049", "MMS_SND_UNKNOWN_ERR"),


    /** INTERNAL SERVER ERROR */
    TPS_OVER_ERROR("", ""), // 내부적으로 처리하는 예외 코드.
    TIME_OVER_ERROR("62028", "TRANSMISSIVE_TIME_OVER"),
    DST_SOCKET_TIMEOUT_ERROR("62031", "SOCKET_TIMEOUT_ERROR"),


    /** DESTINATION ERROR */
    DST_BAD_REQUEST("62060", "DST_BAD_REQUEST"),
    DST_CLIENT_ERROR("62061", "DST_CLIENT_ERROR"),
    DST_SERVER_ERROR("62062", "DST_SERVER_ERROR"),
    DST_NETWORK_ERROR("62063", "DST_NETWORK_ERROR"),
    DST_UNKNOWN_ERROR("62064", "DST_UNKNOWN_ERROR"),;

    private final String resultCode;
    private final String resultMessage;

    ErrorCode(String resultCode, String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }
}
