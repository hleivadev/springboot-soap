package com.soap.interceptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;

/**
 * Logs the raw XML of every SOAP request, response, and fault sent through
 * the client. Useful for debugging integration issues without attaching a
 * network sniffer. Logged at DEBUG level on purpose, since raw payloads are
 * verbose.
 */
public class SoapLoggingInterceptor implements ClientInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SoapLoggingInterceptor.class);

    @Override
    public boolean handleRequest(MessageContext messageContext) {
        log("Request", messageContext.getRequest());
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) {
        log("Response", messageContext.getResponse());
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext) {
        log("Fault", messageContext.getResponse());
        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception ex) {
        if (ex != null) {
            LOGGER.error("SOAP call finished with an exception", ex);
        }
    }

    private void log(String label, WebServiceMessage message) {
        if (message == null) {
            return;
        }
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            message.writeTo(out);
            LOGGER.debug("SOAP {}:\n{}", label, out.toString(StandardCharsets.UTF_8));
        } catch (IOException ex) {
            LOGGER.warn("Could not log SOAP {}", label, ex);
        }
    }
}
