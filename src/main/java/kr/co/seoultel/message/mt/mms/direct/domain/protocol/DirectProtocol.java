package kr.co.seoultel.message.mt.mms.direct.domain.protocol;

import java.util.List;

public class DirectProtocol {


    public static final String SKT = "SKT";
    public static final String KTF = "KTF";
    public static final String LGT = "LGT";
    public static final List<String> DIRECT_GROUP = List.of(SKT, KTF, LGT);



    public static final String UPLUS = "UPLUS";
    public static final String SMTNT = "SMTNT";
    public static final String LGHV = "LGHV";
    public static final String HIST = "HIST";
    public static final String UNION = "UNION";

    public final static String SYSTEM = "SYSTEM";

    public static final String MMS_PREFIX = "MMS";
    public static final String REDIS_IMAGE_PREFIX = "FileExpireData";


    public final static String EUC_KR = "euc-kr";



    /* MCMP 플랫폼에서 지원하는 이미지 확장자명 */
    public static final String JPG_EXTENSION = ".jpg";
    public static final String JPEG_EXTENSION = ".jpeg";

    /* MCMP 플랫폼에서 지원하는 이미지 최대 크기 */
    public static final int STANDARD_IMAGE_MAX_SIZE = 1024 * 100; // 100KB

    /* 시간 단위 */
    public static final long SECOND = 1000L;
    public static final long MINUTE = SECOND * 60L;
    public static final long HOUR = MINUTE* 60L;
    public static final long DAY = HOUR * 24L;
    public static final long WEEK = DAY * 7L;

    public static final long MMS_EXPIRE_TIME = DAY * 3L + HOUR;


    /*
     * =============================================================================
     *
     *                CONSTANTS USE REPORT WHEN MESSAGE VALIDATION FAIL
     *
     * =============================================================================
     */
    public final static String UMS_MSG_ID_VALIDATION_FAILED_MNO_RESULT = "62021";
    public final static String PHONE_NUMBER_VALIDATION_FAILED_MNO_RESULT = "62022";
    public final static String ORIGIN_CODE_VALIDATION_FAILED_MNO_RESULT = "62023";

    public final static String MESSAGE_IMAGE_IS_EMPTY_MNO_RESULT = "62024";
    public final static String MASSAGE_IS_EXPIRED_MNO_RESULT = "62027";
    public final static String TRANSMISSIVE_TIME_OVER_MNO_RESULT = "62028";

    public final static String FAILED_TO_CREATE_SOAP_MNO_RESULT = "62030";
    public final static String SOCKET_TIMEOUT_SUBMIT_FAIL_MNO_RESULT = "62031";


    /*
     * =============================================================================
     *
     *                   CONSTANTS USE REPORT WHEN FILE-SERVER EXCEPTION
     *
     * =============================================================================
     */
    public final static String IMAGE_CNT_OVER = "IMAGE_CNT_OVER";
    public final static String IMAGE_CNT_OVER_MNO_RESULT = "62041";
    public final static String IMAGE_IS_EXPIRED = "IMAGE_IS_EXPIRED";
    public final static String IMAGE_IS_EXPIRED_MNO_RESULT = "62042";

    public final static String IMAGE_NOT_FOUND = "NOT_ADDED_CLIENTS_IMAGE";
    public final static String IMAGE_NOT_FOUND_MNO_RESULT = "62043";

    public final static String IMAGE_SIZE_OVER = "IMG_SIZE_OVER";
    public final static String IMAGE_SIZE_OVER_MNO_RESULT = "62044";

    public final static String UNKNOWN_FILE_SERVER_ERROR = "MMS_SND_UNKNOWN_FILE_ERR";
    public final static String UNKNOWN_FILE_SERVER_ERROR_MNO_RESULT = "62049";




    public final static String SOAP_ERROR = "SOAP_ERROR";
    public final static String FORMAT_ERROR = "FORMAT_ERROR";
    public final static String UNKNOWN_ERROR = "UNKNOWN_ERROR";
    public final static String SOCKET_TIMEOUT_ERROR = "SOCKET_TIMEOUT_ERROR";





    public static final String SOAP_ACTION = "\"\"";

    //    public static long MMS_REPORT_WAIT_MAXIMUM_TIME = 0L; // -> 72 Hour : 3일
    public static long MMS_REPORT_WAIT_MAXIMUM_TIME = 72L; // -> 72 Hour : 3일


    public static String EXPIRED_REPORT_SITUATION = "MESSAGE EXPIRED";

    public static String SUBMIT_ACK_REPORT_SITUATION = "SUBMIT-ACK REPORT";
    public static String REPORT_SITUATION = "REPORT";
    public static String FILE_SERVER_UNKNOWN_EXCEPTION_SITUATION = "SEND REQUEST FILE SERVER";
    public static String SEND_MESSAGE_UNKNOWN_EXCEPTION = "SEND MESSAGE";

//    public static String EXPIRED_REPORT_SITUATION = "MESSAGE EXPIRED";


    //SKT
    public static final String SKT_REQUEST_URL = "/mmsr41/mm7";
    public static final String SKT_TRANSACTION_ID_URL = "http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-5-MM7-1-0";
    public static final String SKT_ENDPOINT_URI = "/mmsr41/mm7";
    public static final String SKT_CONTENT_ID = "<start_MM7_SOAP>";

    //KT
    public static final String KTF_REQUEST_URL = "/";
    public static final String KTF_TRANSACTION_ID_URL = "http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-5-MM7-1-2";
    public static final String KTF_REPORT_TRANSACTION_ID_URL = "http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-5-MM7-1-0";


    public static final String KTF_ENDPOINT_URI = "/";
    public static final String KTF_CONTENT_ID = "</tnn-200102/mm7-submit>";


    //LG
    public static final String LGT_REQUEST_URL = "/";
    public static final String LGT_TRANSACTION_ID_URL = "http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-5-MM7-1-2";  // TODO : submitReq(이모티콘) 체크
    public static final String LGT_ENDPOINT_URI = "/";
    public static final String LGT_CONTENT_ID = "</tnn-200102/mm7-submit>";



    public final static String tmpFileName = "arreo_jpeg.jpeg";
    public final static String tmpXtName = "arreo_xt.xt";
    public final static String tmpSmiName = "arreo_smil.smi";
}
