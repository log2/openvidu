/*
 * (C) Copyright 2017-2020 OpenVidu (https://openvidu.io)
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

package io.openvidu.server.utils;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;
import io.openvidu.client.OpenViduException;
import io.openvidu.server.utils.dockermanager.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.List;
import java.util.Map;

final class ExternalizedDockerManager implements DockerManager {
    private static final Logger log = LoggerFactory.getLogger(ExternalizedDockerManager.class);
    public static final String DEFAULT_MEDIA_NODE_ID = "default";
    private DockerManagerRestAPI service;

    ExternalizedDockerManager(boolean init, String openViduRecordingDockerHelperUrl) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(openViduRecordingDockerHelperUrl).build();
        service = retrofit.create(DockerManagerRestAPI.class);
    }

    @Override
    public void init() {

    }

    public void checkDockerEnabled() throws OpenViduException {
        service.checkEnabled();
    }

    @Override
    public boolean dockerImageExistsLocally(String image) {
        return service.checkImageAvailable(new CheckImageAvailableRequest().image(image));
    }

    @Override
    public void downloadDockerImage(String image, int secondsOfWait) {
        service.ensureImageAvailable(new EnsureImageAvailableRequest().image(image).secondsOfWait(secondsOfWait));
    }

    @Override
    public String runContainer( String image, String containerName, String user,
                               List<Volume> volumes, List<Bind> binds, String networkMode, List<String> envs, List<String> command,
                               Long shmSize, boolean privileged, Map<String, String> labels) throws Exception {
        Call<String> call = service.runContainer(DEFAULT_MEDIA_NODE_ID, new RunContainerRequest()
                .image(image)
                .containerName(containerName)
                .user(user)
                .networkMode(networkMode)
                .privileged(privileged));
        Response<String> response = call.execute();
        return response.body();
    }

    @Override
    public void removeDockerContainer(String containerId, boolean force) {
        if (force)
            service.removeContainerForced(DEFAULT_MEDIA_NODE_ID, containerId);
        else service.removeContainer(DEFAULT_MEDIA_NODE_ID, containerId);
    }

    @Override
    public void runCommandInContainerSync( String containerId, String command, int secondsOfWait)
            throws IOException {
        service.runCommandInContainerSync(DEFAULT_MEDIA_NODE_ID, containerId, new RunCommandRequestSync().command(command).secondsOfWait(secondsOfWait));
    }

    @Override
    public void runCommandInContainerAsync( String containerId, String command) throws IOException {
        service.runCommandInContainerAsync(DEFAULT_MEDIA_NODE_ID, containerId, new RunCommandRequestAsync().command(command));
    }

    @Override
    public void waitForContainerStopped(String containerId, int secondsOfWait) throws Exception {
        service.waitForContainerStopped(DEFAULT_MEDIA_NODE_ID, containerId, new WaitForStopped().secondsOfWait(secondsOfWait));
    }

    @Override
    public void cleanStrandedContainers(String imageName) {
        service.cleanStrandedContainers(new CleanStrandedContainersRequest().image(imageName));
    }

    public void close() {
        // Do nothing
    }
}