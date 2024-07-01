package kr.co.seoultel.message.mt.mms.direct.modules.lgt;

import kr.co.seoultel.message.mt.mms.core.messages.direct.lgt.LgtSoapMessage;
import kr.co.seoultel.message.mt.mms.direct.entity.KtfSoapMessage;
import kr.co.seoultel.message.mt.mms.direct.modules.ktf.KtfCondition;
import kr.co.seoultel.mms.server.core.config.ServerConfig;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(LgtCondition.class)
public class LgtServerConfig extends ServerConfig<LgtSoapMessage> {
}
