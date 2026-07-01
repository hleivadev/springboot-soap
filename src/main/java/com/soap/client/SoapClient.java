package com.soap.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import com.soap.exception.CalculatorServiceException;
import com.soap.wsdl.Add;
import com.soap.wsdl.AddResponse;
import com.soap.wsdl.Divide;
import com.soap.wsdl.DivideResponse;
import com.soap.wsdl.Multiply;
import com.soap.wsdl.MultiplyResponse;
import com.soap.wsdl.Subtract;
import com.soap.wsdl.SubtractResponse;

/**
 * Client for the public dneonline Calculator SOAP service. Each public
 * method builds the request, delegates to {@link #sendAndReceive}, and
 * converts any transport/service failure into a {@link CalculatorServiceException}
 * so callers never have to depend on Spring-WS's internal exception types.
 */
public class SoapClient extends WebServiceGatewaySupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(SoapClient.class);

    private static final String NAMESPACE_URI = "http://tempuri.org/";

    public int add(int a, int b) {
        Add request = new Add();
        request.setIntA(a);
        request.setIntB(b);
        AddResponse response = (AddResponse) sendAndReceive(request, "Add");
        return response.getAddResult();
    }

    public int subtract(int a, int b) {
        Subtract request = new Subtract();
        request.setIntA(a);
        request.setIntB(b);
        SubtractResponse response = (SubtractResponse) sendAndReceive(request, "Subtract");
        return response.getSubtractResult();
    }

    public int multiply(int a, int b) {
        Multiply request = new Multiply();
        request.setIntA(a);
        request.setIntB(b);
        MultiplyResponse response = (MultiplyResponse) sendAndReceive(request, "Multiply");
        return response.getMultiplyResult();
    }

    public int divide(int a, int b) {
        if (b == 0) {
            // Fail fast on the client side. No need to spend a network round trip
            // just to find out the server would have rejected it too.
            throw new CalculatorServiceException(
                    "No es posible dividir por cero (validado antes de llamar al servicio).");
        }
        Divide request = new Divide();
        request.setIntA(a);
        request.setIntB(b);
        DivideResponse response = (DivideResponse) sendAndReceive(request, "Divide");
        return response.getDivideResult();
    }

    private Object sendAndReceive(Object request, String operation) {
        try {
            SoapActionCallback soapActionCallback = new SoapActionCallback(NAMESPACE_URI + operation);
            // Uses the default URI configured on this gateway (see SoapConfig),
            // instead of hardcoding the endpoint again here.
            return getWebServiceTemplate().marshalSendAndReceive(request, soapActionCallback);
        } catch (SoapFaultClientException ex) {
            LOGGER.error("El servicio Calculator devolvi\u00f3 un SOAP Fault en '{}': {}", operation, ex.getMessage());
            throw new CalculatorServiceException(
                    "El servicio de calculadora rechaz\u00f3 la operaci\u00f3n '" + operation + "': " + ex.getMessage(), ex);
        } catch (WebServiceIOException ex) {
            LOGGER.error("No fue posible conectar con el servicio Calculator (operaci\u00f3n '{}')", operation, ex);
            throw new CalculatorServiceException(
                    "No fue posible conectar con el servicio de calculadora (operaci\u00f3n '" + operation + "').", ex);
        }
    }
}
