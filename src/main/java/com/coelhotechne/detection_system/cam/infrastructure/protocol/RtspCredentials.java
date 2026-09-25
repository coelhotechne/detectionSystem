package com.coelhotechne.detection_system.cam.infrastructure.protocol;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public final class RtspCredentials implements AutoCloseable{

    private final String username;
    private  final char [] password;
    public RtspCredentials(String username,char[]password) {
        this.username=Objects.requireNonNull(username, "username");
        this.password=Objects.requireNonNull(password, "password").clone();
    }
    public String username(){
        return username;
    }

    public byte[] passwordUtf8(){
        ByteBuffer encoded = StandardCharsets.UTF_8.encode(CharBuffer.wrap(password));
        byte[] bytes= Arrays.copyOfRange(encoded.array(),encoded.position(),encoded.limit());
        Arrays.fill(encoded.array(),(byte)0);
        return bytes;
    }
    @Override
    public void close(){
        Arrays.fill(password,'\0');
    }
    @Override
    public String toString() {
        return "RtspCredentials[username=" + username + ", password= *Nice Try!* ]";
    }
}

