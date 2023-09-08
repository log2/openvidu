package io.openvidu.server.utils;

import io.openvidu.java.client.RecordingProperties;
import io.openvidu.server.core.Session;
import io.openvidu.server.recording.Recording;

import static io.openvidu.java.client.Recording.OutputMode.INDIVIDUAL;
import static io.openvidu.server.recording.service.RecordingService.*;

public final class RecordingUtils {

	public final static RecordingProperties RECORDING_PROPERTIES_WITH_MEDIA_NODE(Session session) {
		RecordingProperties recordingProperties = session.getSessionProperties().defaultRecordingProperties();
		if (RecordingProperties.IS_COMPOSED(recordingProperties.outputMode())
				&& recordingProperties.mediaNode() == null) {
			recordingProperties = new RecordingProperties.Builder(recordingProperties)
					.mediaNode(session.getMediaNodeId()).build();
		}
		return recordingProperties;
	}

	public static String getExtensionFromRecording(Recording recording) {
		if (INDIVIDUAL.equals(recording.getOutputMode())) {
			return INDIVIDUAL_RECORDING_COMPRESSED_EXTENSION;
		} else if (recording.hasVideo()) {
			return COMPOSED_RECORDING_EXTENSION;
		} else {
			return COMPOSED_RECORDING_AUDIO_ONLY_EXTENSION;
		}
	}

}
