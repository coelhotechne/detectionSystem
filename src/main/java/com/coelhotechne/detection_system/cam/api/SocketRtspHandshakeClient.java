package com.coelhotechne.detection_system.cam.api;

import com.coelhotechne.detection_system.cam.api.dto.RtspCredentials;
import com.coelhotechne.detection_system.cam.api.dto.RtspResponse;
import com.coelhotechne.detection_system.cam.domain.connectioncam.CamConnectionProfile;
import com.coelhotechne.detection_system.cam.domain.connectioncam.CamConnectionTestProbe;
import com.coelhotechne.detection_system.cam.domain.connectioncam.enums.CamProtocol;
import com.coelhotechne.detection_system.cam.domain.homologation.CamConnectionTestOutcome;
import com.coelhotechne.detection_system.cam.infrastructure.CamCredentialResolver;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SocketRtspHandshakeClient implements CamConnectionTestProbe {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private static final String USER_AGENT = "CoelhoTechne-CamHomologation/1.0";
    private static final int DEFAULT_RTSP_PORT = 554;

    private final CamCredentialResolver credentialResolver;

    public SocketRtspHandshakeClient(CamCredentialResolver credentialResolver) {
        this.credentialResolver = credentialResolver;
    }

    @Override
    public CamConnectionTestOutcome test(CamConnectionProfile connection) {
        if (connection.getProtocol() != CamProtocol.RTSP && connection.getProtocol() != CamProtocol.NATIVE) {
            return CamConnectionTestOutcome.failure(
                    "SocketRtspHandshakeClient is only for RTSP/NATIVE, request is: " + connection.getProtocol());
        }
        String streamUri = connection.getStreamUri();
        if (streamUri == null) {
            return CamConnectionTestOutcome.failure("Uri not configured");
        }

        URI uri;
        try {
            uri = new URI(streamUri);
        } catch (URISyntaxException e) {
            return CamConnectionTestOutcome.failure("Uri invalid: " + e.getMessage());
        }
        String host = uri.getHost();
        if (host == null) {
            return CamConnectionTestOutcome.failure("Uri with not host: " + streamUri);
        }
        int port = uri.getPort() == -1 ? DEFAULT_RTSP_PORT : uri.getPort();

        RtspCredentials credentials =
                connection.getCredentialRef()
                        == null ? null : credentialResolver.resolve(connection.getCredentialRef());

        try {
            RtspResponse first = describe(host, port, streamUri, 1, null);
            if (first.statusCode() == 200) {
                return CamConnectionTestOutcome.success(SdpMetadataExtractor.extractCodec(first.body()));
            }
            if (first.statusCode() != 401) {
                return CamConnectionTestOutcome.failure("RTSP DESCRIBE status return: " + first.statusCode());
            }

            String wwwAuthenticate = first.header("WWW-Authenticate");
            if (wwwAuthenticate == null) {
                return CamConnectionTestOutcome.failure("Cam return: 401 with not header WWW-Authenticate");
            }
            if (credentials == null) {
                return CamConnectionTestOutcome.failure(
                        "Cam requires authentication, and no credentials were resolved.\n (credentialRef="
                                + connection.getCredentialRef() + ")");
            }

            String authorization = buildAuthorizationHeader(wwwAuthenticate, credentials, streamUri, "DESCRIBE");
            // muitos servidores RTSP fecham a conexão após responder 401 — nova conexão pro retry
            RtspResponse second = describe(host, port, streamUri, 2, authorization);
            if (second.statusCode() == 200) {
                return CamConnectionTestOutcome.success(SdpMetadataExtractor.extractCodec(second.body()));
            }
            if (second.statusCode() == 401) {
                return CamConnectionTestOutcome.failure("Credentials rejected by the camera (401 após autenticação)");
            }
            return CamConnectionTestOutcome.failure("RTSP DESCRIBE authenticated status return  " + second.statusCode());

        } catch (SocketTimeoutException e) {
            return CamConnectionTestOutcome.failure("Timeout connected" + host + ":" + port);
        } catch (IOException e) {
            return CamConnectionTestOutcome.failure("Connection field with " + host + ":" + port + ": " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
                return CamConnectionTestOutcome.failure("Calculation failed Digest (MD5 unavailable): " + e.getMessage());
        }
    }

    private RtspResponse describe(String host, int port, String streamUri, int cseq, String authorizationHeader)
            throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), (int) TIMEOUT.toMillis());
            socket.setSoTimeout((int) TIMEOUT.toMillis());

            StringBuilder request = new StringBuilder();
            request.append("DESCRIBE ").append(streamUri).append(" RTSP/1.0\r\n");
            request.append("CSeq: ").append(cseq).append("\r\n");
            request.append("Accept: application/sdp\r\n");
            request.append("User-Agent: ").append(USER_AGENT).append("\r\n");
            if (authorizationHeader != null) {
                request.append("Authorization: ").append(authorizationHeader).append("\r\n");
            }
            request.append("\r\n");

            OutputStream out = socket.getOutputStream();
            out.write(request.toString().getBytes(StandardCharsets.UTF_8));
            out.flush();

            return parseResponse(socket.getInputStream());
        }
    }

    private RtspResponse parseResponse(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String statusLine = reader.readLine();
        if (statusLine == null) {
            throw new EOFException("Connection closed before response (status line null)");
        }
        // ex.: "RTSP/1.0 200 OK"
        String[] parts = statusLine.split(" ", 3);
        int statusCode = parts.length >= 2 ? Integer.parseInt(parts[1]) : -1;

        Map<String, String> headers = new LinkedHashMap<>();
        String line;
        int contentLength = 0;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int sep = line.indexOf(':');
            if (sep <= 0) {
                continue;
            }
            String name = line.substring(0, sep).trim();
            String value = line.substring(sep + 1).trim();
            headers.put(name.toLowerCase(Locale.ROOT), value);
            if (name.equalsIgnoreCase("Content-Length")) {
                contentLength = Integer.parseInt(value);
            }
        }

        String body = null;
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            int read = 0;
            while (read < contentLength) {
                int n = reader.read(buffer, read, contentLength - read);
                if (n == -1) {
                    break;
                }
                read += n;
            }
            body = new String(buffer, 0, read);
        }

        return new RtspResponse(statusCode, headers, body);
    }

    private String buildAuthorizationHeader(String wwwAuthenticate, RtspCredentials credentials, String uri, String method)
            throws NoSuchAlgorithmException {
        if (wwwAuthenticate.toLowerCase(Locale.ROOT).startsWith("digest")) {
            return buildDigestHeader(wwwAuthenticate, credentials, uri, method);
        }
        return buildBasicHeader(credentials);
    }

    private String buildBasicHeader(RtspCredentials credentials) {
        String raw = credentials.username() + ":" + credentials.password();
        return "Basic " + Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    // RFC 2617 — Digest sem qop (compatível com a maioria das câmeras IP baratas, que ainda
    // implementam o esquema antigo do RFC 2069/2617 sem "qop=auth"). Câmeras que exigem
    // qop=auth (com nc/cnonce) vão precisar de extensão deste método — não testado.
    private String buildDigestHeader(String wwwAuthenticate, RtspCredentials credentials, String uri, String method)
            throws NoSuchAlgorithmException {
        String realm = extractDirective(wwwAuthenticate, "realm");
        String nonce = extractDirective(wwwAuthenticate, "nonce");

        String ha1 = md5(credentials.username() + ":" + realm + ":" + credentials.password());
        String ha2 = md5(method + ":" + uri);
        String response = md5(ha1 + ":" + nonce + ":" + ha2);

        return "Digest username=\"" + credentials.username() + "\", realm=\"" + realm + "\", nonce=\"" + nonce
                + "\", uri=\"" + uri + "\", response=\"" + response + "\"";
    }

    private String extractDirective(String header, String directive) {
        Pattern pattern = Pattern.compile(directive + "=\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(header);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String md5(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
