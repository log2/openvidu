package io.openvidu.server.utils;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;
import io.openvidu.client.OpenViduException;

import javax.ws.rs.ProcessingException;
import java.util.List;
import java.util.Map;

public interface DockerManager {
    void downloadDockerImage(String image, int secondsOfWait) throws Exception;

    boolean dockerImageExistsLocally(String image) throws ProcessingException;

    void checkDockerEnabled() throws OpenViduException;

    String runContainer(String container, String containerName, String user, List<Volume> volumes,
                        List<Bind> binds, String networkMode, List<String> envs, List<String> command, Long shmSize,
                        boolean privileged, Map<String, String> labels) throws Exception;

    void removeDockerContainer(String containerId, boolean force);

    void cleanStrandedContainers(String imageName);

    void runCommandInContainer(String containerId, String command) throws InterruptedException;

    void runCommandInContainerSync(String containerId, String command, int secondsOfWait)
                    throws InterruptedException;

    void waitForContainerStopped(String containerId, int secondsOfWait) throws Exception;

    void close();
}
