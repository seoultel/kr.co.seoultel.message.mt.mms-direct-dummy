package kr.co.seoultel.message.mt.mms.direct.controller;

import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.SoapMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ReportSoapMessageWrapper<T extends SoapMessage> {

    private final SoapMessage soapMessage;
    private final String reportUrl;

    public ReportSoapMessageWrapper(T soapMessage, String reportUrl) {
        this.soapMessage = soapMessage;
        this.reportUrl = reportUrl;
    }
}
