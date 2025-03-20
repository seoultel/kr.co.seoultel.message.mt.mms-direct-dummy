package kr.co.seoultel.message.mt.mms.direct.common.condition;


import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class KtfCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String telecom = context.getEnvironment().getProperty("direct.telecom");
        return telecom.toUpperCase().equalsIgnoreCase("KTF");
    }
}
