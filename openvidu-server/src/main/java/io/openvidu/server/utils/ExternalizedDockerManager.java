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
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

final class ExternalizedDockerManager implements DockerManager {
    private static final Logger log = LoggerFactory.getLogger(ExternalizedDockerManager.class);
    public static final String DEFAULT_MEDIA_NODE_ID = "default";
    private final DockerManagerRestAPI service;

    ExternalizedDockerManager(boolean init, String openViduRecordingDockerHelperUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(openViduRecordingDockerHelperUrl)
                .client(
                        new OkHttpClient.Builder()
                                .connectTimeout(10, TimeUnit.SECONDS)
                                .readTimeout(2, TimeUnit.MINUTES)
                                .writeTimeout(2, TimeUnit.MINUTES)
                                .callTimeout(5, TimeUnit.MINUTES)
                                .build()
                )
                .build();
        service = retrofit.create(DockerManagerRestAPI.class);
    }

    @Override
    public DockerManager init() {
        return this;
    }

    @Override
    public void checkDockerEnabled() throws OpenViduException {
        executeAndCheck("checkEnabled", service::checkEnabled);
    }

    @Override
    public boolean dockerImageExistsLocally(String image) {
        return execute(() -> service.checkImageAvailable(new CheckImageAvailableRequest().image(image))).body().getAvailable();
    }

    @Override
    public void downloadDockerImage(String image, int secondsOfWait) {
        executeAndCheck("ensureImageAvailable", () -> service.ensureImageAvailable(new EnsureImageAvailableRequest().image(image).secondsOfWait(secondsOfWait)));
    }

    @Override
    public String runContainer(String mediaNodeId, String image, String containerName, String user,
                               List<Volume> volumes, List<Bind> binds, String networkMode, List<String> envs, List<String> command,
                               Long shmSize, boolean privileged, Map<String, String> labels, boolean enableGPU) throws Exception {
        Call<String> call = service.runContainer(getMediaNodeId(mediaNodeId), new RunContainerRequest()
                .image(image)
                .containerName(containerName)
                .user(user)
                .volumes(translate(volumes, dockerVolume -> new io.openvidu.server.utils.dockermanager.model.Volume
                        ().path(dockerVolume.getPath())
                ))
                .binds(translate(binds, dockerBind -> new io.openvidu.server.utils.dockermanager.model.Bind
                                ().path(dockerBind.getPath())
                                .accessMode(AccessMode.fromValue(dockerBind.getAccessMode().name()))
                                .propagationMode(PropagationMode.fromValue(dockerBind.getPropagationMode().name()))
                                .selContext(SELContext.fromValue(dockerBind.getSecMode().name())) // FIXME mapping issue for different case SELContext modes
                                .volume(new io.openvidu.server.utils.dockermanager.model.Volume().path(dockerBind.getVolume().getPath()))
                                .noCopy(dockerBind.getNoCopy())
                        //
                ))
                .networkMode(networkMode)
                .envs(envs)
                .shmSize(shmSize)
                .privileged(privileged));
        Response<String> response = call.execute();
        return response.body();
    }

    private static <A, B> List<B> translate(Collection<A> input, Function<? super A, ? extends B> typeTransformer) {
        return input.stream().map(typeTransformer).collect(toList());
    }

    private <T> Response<T> execute(Supplier<Call<T>> callSupplier) {
        try {
            return callSupplier.get().execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeAndCheck(String name, Supplier<Call<BasicResponse>> callSupplier) {
        BasicResponse basicResponse = execute(callSupplier).body();
        if (!basicResponse.getSuccess()) {
            throw new OpenViduException(OpenViduException.Code.GENERIC_ERROR_CODE, "Failed call " + name + ", due to: \"" + basicResponse.getMessage() + "");
        }
    }

    @Override
    public void removeContainer(String mediaNodeId, String containerId, boolean force) {
        final String mediaNode = getMediaNodeId(mediaNodeId);
        if (force) executeAndCheck("removeContainerForced", () ->
                service.removeContainerForced(mediaNode, containerId));
        else executeAndCheck("removeContainer", () -> service.removeContainer(mediaNode, containerId));
    }

    @Override
    public void runCommandInContainerSync(String mediaNodeId, String containerId, String command, int secondsOfWait) {
        executeAndCheck("runCommandInContainerSync", () -> service.runCommandInContainerSync(getMediaNodeId(mediaNodeId), containerId, new RunCommandRequestSync().command(command).secondsOfWait(secondsOfWait)));
    }

    @Override
    public void runCommandInContainerAsync(String mediaNodeId, String containerId, String command) {
        executeAndCheck("runCommandInContainerAsync", () -> service.runCommandInContainerAsync(getMediaNodeId(mediaNodeId), containerId, new RunCommandRequestAsync().command(command)));
    }

    @Override
    public void waitForContainerStopped(String mediaNodeId, String containerId, int secondsOfWait) {
        executeAndCheck("waitForContainerStopped", () -> service.waitForContainerStopped(getMediaNodeId(mediaNodeId), containerId, new WaitForStopped().secondsOfWait(secondsOfWait)));
    }

    @Override
    public void cleanStrandedContainers(String imageName) {
        executeAndCheck("cleanStrandedContainers", () -> service.cleanStrandedContainers(new CleanStrandedContainersRequest().image(imageName)));
    }

    @Override
    public void close() {
        // Do nothing
    }

    private String getMediaNodeId(String mediaNodeId) {
        return StringUtils.hasText(mediaNodeId) ? mediaNodeId : DEFAULT_MEDIA_NODE_ID;
    }
}