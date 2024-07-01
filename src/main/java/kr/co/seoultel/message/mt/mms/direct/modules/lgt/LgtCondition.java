package kr.co.seoultel.message.mt.mms.direct.modules.lgt;


import kr.co.seoultel.message.mt.mms.core.common.constant.Constants;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class LgtCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String telecom = context.getEnvironment().getProperty("direct.telecom");
        return telecom.toUpperCase().equals(Constants.LGT);
    }
}
