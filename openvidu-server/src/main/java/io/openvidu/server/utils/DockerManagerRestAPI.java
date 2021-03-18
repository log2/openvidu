package io.openvidu.server.utils;

import io.openvidu.server.utils.dockermanager.model.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface DockerManagerRestAPI {

    @POST("/containers/{mediaNodeId}")
    Call<String> runContainer(@Path("mediaNodeId") String mediaNodeId,
                              @Body RunContainerRequest runContainerRequest);

    @DELETE("/containers/{mediaNodeId}/container/{containerId}")
    Call<BasicResponse> removeContainer(@Path("mediaNodeId") String mediaNodeId,
                         @Path("containerId") String containerId);

    @DELETE("/containers/{mediaNodeId}/container/{containerId}/forced")
    Call<BasicResponse>  removeContainerForced(@Path("mediaNodeId") String mediaNodeId,
                               @Path("containerId") String containerId);

    @POST("/containers/{mediaNodeId}/container/{containerId}/runAndWait")
    Call<BasicResponse>  runCommandInContainerSync(@Path("mediaNodeId") String mediaNodeId,
                                   @Path("containerId") String containerId,
                                   @Body RunCommandRequestSync runCommandRequestSync);

    @POST("/containers/{mediaNodeId}/container/{containerId}/run")
    Call<BasicResponse>  runCommandInContainerAsync(@Path("mediaNodeId") String mediaNodeId,
                                    @Path("containerId") String containerId,
                                    @Body RunCommandRequestAsync runCommandRequestAsync);

    @POST("/containers/{mediaNodeId}/container/{containerId}/join")
    Call<BasicResponse>  waitForContainerStopped(@Path("mediaNodeId") String mediaNodeId,
                                 @Path("containerId") String containerId,
                                 @Body WaitForStopped waitForStopped) ;


    @POST("/containers/cleanup")
    Call<BasicResponse>  cleanStrandedContainers(@Body CleanStrandedContainersRequest cleanStrandedContainersRequest);

    @GET("/containers/check")
    Call<BasicResponse>  checkEnabled();

    @POST("/images/ensure")
    Call<BasicResponse>  ensureImageAvailable(@Body EnsureImageAvailableRequest ensureImageAvailableRequest);

    @POST("/images/check")
    Call<ImageAvailableResponse> checkImageAvailable(@Body CheckImageAvailableRequest checkImageAvailableRequest);
}
