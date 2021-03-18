package io.openvidu.server.utils;

public interface SafeAutocloseable extends AutoCloseable {
    void close();
}
