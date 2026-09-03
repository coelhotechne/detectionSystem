package com.coelhotechne.detection_system.connection.domain;

import com.coelhotechne.detection_system.connection.domain.enums.ApplicationProtocol;
import com.coelhotechne.detection_system.connection.exceptions.ConnectionNotFoundException;
import com.coelhotechne.detection_system.connection.exceptions.enums.ConnectionError;
import com.coelhotechne.detection_system.connection.infrastructure.ConnectionResolver;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;

@Component
public class MqttConnectionResolver implements ConnectionResolver {
    @Override
    public boolean supports(ApplicationProtocol protocol) {
        return false;
    }

    @Override
    public Connection resolve(ConnectionParams params) {
        return null;
    }

   /* private final MqttClientFactory clientFactory;

    public MqttConnectionResolver(MqttClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public boolean supports(ApplicationProtocol protocol) {
        return protocol == ApplicationProtocol.MQTT;
    }

    @Override
    public Connection resolve(ConnectionParams params) {
        if (params.host() == null || params.port() == null) {
            throw new ConnectionNotFoundException(ConnectionError.UNSUPPORTED_PROTOCOL,
                    "host/port são obrigatórios para MQTT");
        }
        try {
            MqttClientHandle client = clientFactory.connect(params.host(), params.port(), params.credentialRef());
            return build(params, client);
        } catch (MqttException e) {
            throw new ConnectionNotFoundException(ConnectionError.HOST_UNREACHABLE,
                    "Falha ao conectar via MQTT: " + e.getMessage());
        }
    }

    private Connection build(ConnectionParams params, MqttClientHandle client) {
        return params.linkType().isWireless()
                ? new WirelessConnection(ApplicationProtocol.MQTT, params.linkType(), params.signalStrengthDbm(), client)
                : new WiredConnection(ApplicationProtocol.MQTT, params.linkType(), client);
    } */
}
