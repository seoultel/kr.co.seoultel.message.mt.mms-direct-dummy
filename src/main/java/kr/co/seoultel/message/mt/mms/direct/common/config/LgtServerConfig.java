package kr.co.seoultel.message.mt.mms.direct.common.config;

import com.google.gson.reflect.TypeToken;
import kr.co.seoultel.message.mt.mms.direct.common.condition.LgtCondition;
import kr.co.seoultel.message.mt.mms.direct.controller.ReportSoapMessageWrapper;
import kr.co.seoultel.message.mt.mms.direct.controller.ktf.KtfReporter;
import kr.co.seoultel.message.mt.mms.direct.controller.lgt.LgtReporter;
import kr.co.seoultel.message.mt.mms.direct.domain.CpidInfo;
import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.ktf.KtfDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.lgt.LgtDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol;
import kr.co.seoultel.message.mt.mms.direct.infrastructure.cache.storage.QueueStorage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol.EUC_KR;

@Slf4j
@Getter
@Setter
@Configuration
@Conditional(LgtCondition.class)
public class LgtServerConfig {
//
//    @Bean
//    public RestTemplate restTemplate(
//        RestTemplateBuilder builder
//    ) {
//        return builder
//            .defaultHeader(HttpHeaders.CONTENT_TYPE, "text/xml; charset=\"euc-kr\"")
//            .defaultHeader(HttpHeaders.ACCEPT_CHARSET, EUC_KR)
//            .defaultHeader("SOAPAction", DirectProtocol.SOAP_ACTION)
//            .build();
//    }

    @Bean
    public QueueStorage<ReportSoapMessageWrapper<LgtDeliveryReportReqMessage>> reportQueue(
            @Value("${storage.report.file-path}") String filePath
    ) throws Exception {
        QueueStorage<ReportSoapMessageWrapper<LgtDeliveryReportReqMessage>> queueStorage = new QueueStorage<ReportSoapMessageWrapper<LgtDeliveryReportReqMessage>>(filePath);
        Type type = new TypeToken<Collection<ReportSoapMessageWrapper<LgtDeliveryReportReqMessage>>>(){}.getType();
        queueStorage.createFileAnd().readFileAsCollection(type).ifPresent(queueStorage::addAll);

        return queueStorage;
    }

    @Bean
    public LgtReporter lgtReporter(
        QueueStorage<ReportSoapMessageWrapper<LgtDeliveryReportReqMessage>> reportQueue
    ) {
        return new LgtReporter(reportQueue);
    }

}
