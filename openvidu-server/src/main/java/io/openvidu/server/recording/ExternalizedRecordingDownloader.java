/*
 * (C) Copyright 2017-2022 OpenVidu (https://openvidu.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.openvidu.server.recording;

import io.openvidu.server.config.OpenviduConfig;
import io.openvidu.server.utils.CustomFileManager;
import io.openvidu.server.utils.RecordingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

public class ExternalizedRecordingDownloader implements RecordingDownloader {

    private static final Logger log = LoggerFactory.getLogger(ExternalizedRecordingDownloader.class);

    private OpenviduConfig openviduConfig;
    private CustomFileManager fileManager;

    public ExternalizedRecordingDownloader(OpenviduConfig openviduConfig, CustomFileManager fileManager) {
        this.openviduConfig = openviduConfig;
        this.fileManager = fileManager;
    }

    @Override
    public void downloadRecording(Recording recording, Collection<RecorderEndpointWrapper> wrappers, Runnable callback) throws IOException {
        final String VIDEO_FILE = this.openviduConfig.getOpenViduRecordingPath() + recording.getId() + "/"
                + recording.getName() + RecordingUtils.getExtensionFromRecording(recording);
        try {
            this.fileManager.waitForFileToExistAndNotEmpty(recording.getRecordingProperties().mediaNode(), VIDEO_FILE);
            log.info("File {} exists and is not empty", VIDEO_FILE);
            callback.run();
        } catch (Exception e) {
            log.error("Recorder container failed generating video file (is empty) for session {}",
                    recording.getSessionId());
            throw new IOException(e);
        }
    }

    @Override
    public void cancelDownload(String recordingId) {
    }
}
