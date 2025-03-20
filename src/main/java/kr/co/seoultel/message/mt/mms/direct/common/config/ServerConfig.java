package kr.co.seoultel.message.mt.mms.direct.common.config;

import kr.co.seoultel.message.mt.mms.direct.domain.CpidInfo;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol.EUC_KR;

@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties("direct")
public class ServerConfig {

    private List<CpidInfo> cpids;

    @Bean
    public Map<String, CpidInfo> cpidInfoMap() {
        log.info("cpidInfos : {}", cpids);
        return cpids.stream().map((cpid) -> {
            return Map.entry(cpid.getCpid(), cpid);
        }).collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
        ));
    }
}
