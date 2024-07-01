package kr.co.seoultel.message.mt.mms.direct.config;

import kr.co.seoultel.message.mt.mms.direct.entity.SoapMessage;
import kr.co.seoultel.mms.server.core.config.ServerConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Getter
@Setter
@Configuration
@RequiredArgsConstructor
public class DirectServerConfig extends ServerConfig<SoapMessage> {

    @Value("${direct.endpoint.ip}")
    private String ip;

    @Value("${direct.endpoint.port}")
    private int port;
}
