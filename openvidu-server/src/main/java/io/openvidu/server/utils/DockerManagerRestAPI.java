package io.openvidu.server.utils;

import io.openvidu.server.utils.dockermanager.model.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface DockerManagerRestAPI {

    @POST("/containers/{mediaNodeId}")
    Call<String> runContainer(@Path("mediaNodeId") String mediaNodeId,
                              @Body RunContainerRequest runContainerRequest);

    @DELETE("/containers/{mediaNodeId}/container/{containerId}")
    Call<OKResponse> removeContainer(@Path("mediaNodeId") String mediaNodeId,
                         @Path("containerId") String containerId);

    @DELETE("/containers/{mediaNodeId}/container/{containerId}/forced")
    Call<OKResponse>  removeContainerForced(@Path("mediaNodeId") String mediaNodeId,
                               @Path("containerId") String containerId);

    @POST("/containers/{mediaNodeId}/container/{containerId}/runAndWait")
    Call<OKResponse>  runCommandInContainerSync(@Path("mediaNodeId") String mediaNodeId,
                                   @Path("containerId") String containerId,
                                   @Body RunCommandRequestSync runCommandRequestSync);

    @POST("/containers/{mediaNodeId}/container/{containerId}/run")
    Call<OKResponse>  runCommandInContainerAsync(@Path("mediaNodeId") String mediaNodeId,
                                    @Path("containerId") String containerId,
                                    @Body RunCommandRequestAsync runCommandRequestAsync);

    @POST("/containers/{mediaNodeId}/container/{containerId}/join")
    Call<OKResponse>  waitForContainerStopped(@Path("mediaNodeId") String mediaNodeId,
                                 @Path("containerId") String containerId,
                                 @Body WaitForStopped waitForStopped) ;


    @POST("/containers/cleanup")
    Call<OKResponse>  cleanStrandedContainers(@Body CleanStrandedContainersRequest cleanStrandedContainersRequest);

    @GET("/containers/check")
    Call<OKResponse>  checkEnabled();

    @POST("/images/ensure")
    Call<OKResponse>  ensureImageAvailable(@Body EnsureImageAvailableRequest ensureImageAvailableRequest);

    @POST("/images/check")
    Call<ImageAvailableResponse> checkImageAvailable(@Body CheckImageAvailableRequest checkImageAvailableRequest);
}
