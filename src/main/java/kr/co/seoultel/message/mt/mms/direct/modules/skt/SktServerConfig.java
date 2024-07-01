package kr.co.seoultel.message.mt.mms.direct.modules.skt;

import kr.co.seoultel.message.mt.mms.core.messages.direct.skt.SktSoapMessage;
import kr.co.seoultel.message.mt.mms.direct.entity.KtfSoapMessage;
import kr.co.seoultel.message.mt.mms.direct.modules.ktf.KtfCondition;
import kr.co.seoultel.mms.server.core.config.ServerConfig;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(SktCondition.class)
public class SktServerConfig extends ServerConfig<SktSoapMessage> {
}
