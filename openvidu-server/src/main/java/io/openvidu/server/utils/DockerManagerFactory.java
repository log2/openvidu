package io.openvidu.server.utils;

import io.openvidu.server.config.OpenviduConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerManagerFactory {
    private static final Logger log = LoggerFactory.getLogger(DockerManagerFactory.class);

    public static DockerManager create(OpenviduConfig openviduConfig) {
        final String openViduRecordingDockerHelperUrl = openviduConfig.getOpenViduRecordingDockerHelperUrl();
        if (openViduRecordingDockerHelperUrl == null) {
            log.debug("Using local Docker Manager for recording");
            return new LocalDockerManager(false);
        }
        log.debug("Using externalized Docker Manager (URL: {}) for recording", openViduRecordingDockerHelperUrl);
        return new ExternalizedDockerManager(false, openViduRecordingDockerHelperUrl);
    }

    public static DockerManager createAndInitialize(OpenviduConfig openviduConfig) {
        DockerManager dockerManager = create(openviduConfig);
        log.debug("Initializing recording's Docker Manager");
        dockerManager.init();
        log.debug("Initialized recording's Docker Manager");
        return dockerManager;
    }
}
