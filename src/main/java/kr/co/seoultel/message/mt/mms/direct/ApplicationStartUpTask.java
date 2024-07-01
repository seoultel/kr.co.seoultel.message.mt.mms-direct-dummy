package kr.co.seoultel.message.mt.mms.direct;



import kr.co.seoultel.message.mt.mms.direct.modules.ktf.KtfReportProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartUpTask implements ApplicationListener<ApplicationReadyEvent> {

    protected final KtfReportProcessor ktfReportProcessor;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Application.startApplicatiaon();

        ktfReportProcessor.start();
    }
}