package com.coelhotechne.detection_system.connection.domain;

import com.coelhotechne.detection_system.connection.domain.enums.ApplicationProtocol;
import com.coelhotechne.detection_system.connection.exceptions.ConnectionNotFoundException;
import com.coelhotechne.detection_system.connection.exceptions.enums.ConnectionError;
import com.coelhotechne.detection_system.connection.infrastructure.ConnectionResolver;
import org.springframework.stereotype.Component;

@Component
public class RtspConnectionResolver implements ConnectionResolver {
    @Override
    public boolean supports(ApplicationProtocol protocol) {
        return false;
    }

    @Override
    public Connection resolve(ConnectionParams params) {
        return null;
    }

    /*private final RtspSessionFactory sessionFactory;

    public RtspConnectionResolver(RtspSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public boolean supports(ApplicationProtocol protocol) {
        return protocol == ApplicationProtocol.RTSP;
    }

    @Override
    public Connection resolve(ConnectionParams params) {
        if (params.streamUri() == null) {
            throw new ConnectionNotFoundException(ConnectionError.PROTOCOL_UNSUPPORTED,
                    "streamUri é obrigatório para RTSP");
        }
        try {
            RtspSessionHandle session = sessionFactory.open(params.streamUri(), params.credentialRef());
            return params.linkType().isWireless()
                    ? new WirelessConnection(ApplicationProtocol.RTSP, params.linkType(), params.signalStrengthDbm(), session)
                    : new WiredConnection(ApplicationProtocol.RTSP, params.linkType(), session);
        } catch (RtspHandshakeException e) {
            throw new ConnectionNotFoundException(ConnectionError.HANDSHAKE_FAILED,
                    "Handshake RTSP falhou: " + e.getMessage());
        } catch (TimeoutException e) {
            throw new ConnectionNotFoundException(ConnectionError.TIMEOUT,
                    "Timeout ao conectar RTSP em " + params.streamUri());
        }
    } */
}