package io.openvidu.server.utils;

import io.openvidu.server.config.OpenviduConfig;

public class DockerManagerFactory {
    public static DockerManager create(OpenviduConfig openviduConfig) {
        final String openViduRecordingDockerHelperUrl = openviduConfig.getOpenViduRecordingDockerHelperUrl();
        if (openViduRecordingDockerHelperUrl == null)
            return new LocalDockerManager(false);
        return new ExternalizedDockerManager(false, openViduRecordingDockerHelperUrl);
    }

    public static DockerManager createAndInitialize(OpenviduConfig openviduConfig) {
        DockerManager dockerManager = create(openviduConfig);
        dockerManager.init();
        return dockerManager;
    }
}
