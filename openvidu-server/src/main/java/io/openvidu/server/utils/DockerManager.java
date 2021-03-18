package io.openvidu.server.utils;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;
import io.openvidu.client.OpenViduException;

import javax.ws.rs.ProcessingException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DockerManager extends SafeAutocloseable {
    DockerManager init();

    void checkDockerEnabled() throws OpenViduException;

    String runContainer(String mediaNodeId, String image, String containerName, String user,
                        List<Volume> volumes, List<Bind> binds, String networkMode, List<String> envs, List<String> command,
                        Long shmSize, boolean privileged, Map<String, String> labels) throws Exception;

    void removeContainer(String mediaNodeId, String containerId, boolean force);

    void runCommandInContainerSync(String mediaNodeId, String containerId, String command, int secondsOfWait)
            throws IOException;

    void runCommandInContainerAsync(String mediaNodeId, String containerId, String command) throws IOException;

    void waitForContainerStopped(String mediaNodeId, String containerId, int secondsOfWait) throws Exception;

    boolean dockerImageExistsLocally(String image) throws ProcessingException;

    void downloadDockerImage(String image, int secondsOfWait);

    void cleanStrandedContainers(String imageName);

    void close();
}
