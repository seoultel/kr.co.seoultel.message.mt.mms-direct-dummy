package kr.co.seoultel.message.mt.mms.direct.modules.ktf;

import kr.co.seoultel.message.mt.mms.core.messages.direct.ktf.report.KtfSubmitReqMessage;
import kr.co.seoultel.message.mt.mms.direct.config.DirectServerConfig;
import kr.co.seoultel.message.mt.mms.direct.entity.KtfSoapMessage;
import kr.co.seoultel.message.mt.mms.direct.entity.SoapMessage;
import kr.co.seoultel.mms.server.core.config.ServerConfig;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(KtfCondition.class)
public class KtfServerConfig extends ServerConfig<KtfSubmitReqMessage> {
}
