package kr.co.seoultel.message.mt.mms.direct.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Slf4j
@Configuration
public class ServerDataVaultConfig extends kr.co.seoultel.mms.server.core.config.ServerDataVaultConfig {

    @Value("${file.paths.message-history}")
    public void setMessageHistoriesFilePath(String messageHistoriesFilePath) {
        MESSAGE_HISTORIES_FILE_PATH = messageHistoriesFilePath;
    }

    @Value("${file.paths.report}")
    public void setReportFilePath(String reportFilePath) {
        REPORT_FILE_PATH = reportFilePath;
    }

    @Value("${file.paths.submit-ack}")
    public void setSubmitAckFilePath(String submitAckFilePath) {
        SUBMIT_ACK_FILE_PATH = submitAckFilePath;
    }

    @Override
    @PostConstruct
    public void check() {
        Objects.requireNonNull(MESSAGE_HISTORIES_FILE_PATH);
        Objects.requireNonNull(SUBMIT_ACK_FILE_PATH);
        Objects.requireNonNull(REPORT_FILE_PATH);

        log.info("[SERVER-DATA-VAULT-CONFIG] {}", this);
    }
}
