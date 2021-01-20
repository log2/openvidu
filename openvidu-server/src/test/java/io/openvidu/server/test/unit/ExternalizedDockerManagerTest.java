package io.openvidu.server.test.unit;

import io.openvidu.server.utils.DockerManagerRestAPI;
import org.junit.jupiter.api.Test;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ExternalizedDockerManagerTest {
    @Test
    void apiCanBeInstantiated() throws IOException {
        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(JacksonConverterFactory.create()).baseUrl("http://localhost:8080").build();
        DockerManagerRestAPI service = retrofit.create(DockerManagerRestAPI.class);
    }
}
