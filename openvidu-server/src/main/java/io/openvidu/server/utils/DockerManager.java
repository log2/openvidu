package io.openvidu.server.utils;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;
import io.openvidu.client.OpenViduException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DockerManager extends SafeAutocloseable {
    void init();

    void checkDockerEnabled() throws OpenViduException;

    String runContainer(String container, String containerName, String user, List<Volume> volumes,
                        List<Bind> binds, String networkMode, List<String> envs, List<String> command, Long shmSize,
                        boolean privileged, Map<String, String> labels) throws Exception;

    void removeDockerContainer(String containerId, boolean force);

    void cleanStrandedContainers(String imageName);

    void runCommandInContainerSync(String containerId, String command, int secondsOfWait) throws InterruptedException, IOException;

    void runCommandInContainerAsync(String containerId, String command) throws IOException;

    boolean dockerImageExistsLocally(String image);

    void downloadDockerImage(String image, int secondsOfWait);

    void waitForContainerStopped(String containerId, int secondsOfWait) throws Exception;
}
