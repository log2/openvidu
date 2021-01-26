package io.openvidu.server.utils;

import io.openvidu.server.utils.dockermanager.model.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface DockerManagerRestAPI {

    @POST("/containers/{mediaNodeId}")
    Call<String> runContainer(@Path("mediaNodeId") String mediaNodeId,
                              @Body RunContainerRequest runContainerRequest);

    @DELETE("/containers/{mediaNodeId}/container/{containerId}")
    void removeContainer(@Path("mediaNodeId") String mediaNodeId,
                         @Path("containerId") String containerId);

    @DELETE("/containers/{mediaNodeId}/container/{containerId}/forced")
    void removeContainerForced(@Path("mediaNodeId") String mediaNodeId,
                               @Path("containerId") String containerId);

    @POST("/containers/{mediaNodeId}/container/{containerId}/runAndWait")
    void runCommandInContainerSync(@Path("mediaNodeId") String mediaNodeId,
                                   @Path("containerId") String containerId,
                                   @Body RunCommandRequestSync runCommandRequestSync);

    @POST("/containers/{mediaNodeId}/container/{containerId}/run")
    void runCommandInContainerAsync(@Path("mediaNodeId") String mediaNodeId,
                                    @Path("containerId") String containerId,
                                    @Body RunCommandRequestAsync runCommandRequestAsync);

    @POST("/containers/{mediaNodeId}/container/{containerId}/join")
    void waitForContainerStopped(@Path("mediaNodeId") String mediaNodeId,
                                 @Path("containerId") String containerId,
                                 @Body WaitForStopped waitForStopped) throws Exception;


    @POST("/containers/cleanup")
    void cleanStrandedContainers(@Body CleanStrandedContainersRequest cleanStrandedContainersRequest);

    @GET("/containers/check")
    void checkEnabled();

    @POST("/images/ensure")
    void ensureImageAvailable(@Body EnsureImageAvailableRequest ensureImageAvailableRequest);

    @POST("/images/check")
    boolean checkImageAvailable(@Body CheckImageAvailableRequest checkImageAvailableRequest);
}
