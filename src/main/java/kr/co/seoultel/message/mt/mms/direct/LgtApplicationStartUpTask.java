package kr.co.seoultel.message.mt.mms.direct;



import kr.co.seoultel.message.mt.mms.direct.modules.ktf.KtfReportProcessor;
import kr.co.seoultel.message.mt.mms.direct.modules.lgt.LgtCondition;
import kr.co.seoultel.message.mt.mms.direct.modules.lgt.LgtReportProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Conditional(LgtCondition.class)
public class LgtApplicationStartUpTask implements ApplicationListener<ApplicationReadyEvent> {

    private final LgtReportProcessor lgtReportProcessor;
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Application.startApplicatiaon();

        lgtReportProcessor.start();
    }
}