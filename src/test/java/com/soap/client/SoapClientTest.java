package com.soap.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.ws.test.client.RequestMatchers.payload;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;
import static org.springframework.ws.test.client.ResponseCreators.withServerOrReceiverFault;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.xml.transform.StringSource;

import com.soap.config.SoapConfig;
import com.soap.exception.CalculatorServiceException;

/**
 * Unit tests for {@link SoapClient}, built as a plain POJO (no Spring
 * context) so they run fast and never touch the real network. Spring-WS's
 * {@link MockWebServiceServer} intercepts calls made through the client's
 * WebServiceTemplate and lets us assert on the outgoing payload while
 * scripting the response, exactly like MockMvc does for REST controllers.
 */
class SoapClientTest {

    private SoapClient soapClient;
    private MockWebServiceServer mockServer;

    @BeforeEach
    void setUp() throws Exception {
        SoapConfig soapConfig = new SoapConfig();

        Jaxb2Marshaller marshaller = soapConfig.marshaller();
        marshaller.afterPropertiesSet(); // builds the JAXBContext

        soapClient = soapConfig.getSoapClient(marshaller);
        mockServer = MockWebServiceServer.createServer(soapClient);
    }

    @Test
    void add_returnsSumFromService() throws Exception {
        mockServer.expect(payload(new StringSource(
                        "<Add xmlns=\"http://tempuri.org/\"><intA>2</intA><intB>2</intB></Add>")))
                .andRespond(withPayload(new StringSource(
                        "<AddResponse xmlns=\"http://tempuri.org/\"><AddResult>4</AddResult></AddResponse>")));

        int result = soapClient.add(2, 2);

        assertThat(result).isEqualTo(4);
        mockServer.verify();
    }

    @Test
    void subtract_returnsDifferenceFromService() throws Exception {
        mockServer.expect(payload(new StringSource(
                        "<Subtract xmlns=\"http://tempuri.org/\"><intA>10</intA><intB>4</intB></Subtract>")))
                .andRespond(withPayload(new StringSource(
                        "<SubtractResponse xmlns=\"http://tempuri.org/\"><SubtractResult>6</SubtractResult></SubtractResponse>")));

        int result = soapClient.subtract(10, 4);

        assertThat(result).isEqualTo(6);
        mockServer.verify();
    }

    @Test
    void multiply_returnsProductFromService() throws Exception {
        mockServer.expect(payload(new StringSource(
                        "<Multiply xmlns=\"http://tempuri.org/\"><intA>6</intA><intB>7</intB></Multiply>")))
                .andRespond(withPayload(new StringSource(
                        "<MultiplyResponse xmlns=\"http://tempuri.org/\"><MultiplyResult>42</MultiplyResult></MultiplyResponse>")));

        int result = soapClient.multiply(6, 7);

        assertThat(result).isEqualTo(42);
        mockServer.verify();
    }

    @Test
    void divide_returnsQuotientFromService() throws Exception {
        mockServer.expect(payload(new StringSource(
                        "<Divide xmlns=\"http://tempuri.org/\"><intA>20</intA><intB>5</intB></Divide>")))
                .andRespond(withPayload(new StringSource(
                        "<DivideResponse xmlns=\"http://tempuri.org/\"><DivideResult>4</DivideResult></DivideResponse>")));

        int result = soapClient.divide(20, 5);

        assertThat(result).isEqualTo(4);
        mockServer.verify();
    }

    @Test
    void divide_byZero_failsFastWithoutCallingService() {
        // call the service anyway, mockServer.verify() below would fail.
        assertThatThrownBy(() -> soapClient.divide(10, 0))
                .isInstanceOf(CalculatorServiceException.class)
                .hasMessageContaining("dividir por cero");

        mockServer.verify();
    }

    @Test
    void add_whenServiceReturnsSoapFault_throwsCalculatorServiceException() throws Exception {
        mockServer.expect(payload(new StringSource(
                        "<Add xmlns=\"http://tempuri.org/\"><intA>2</intA><intB>2</intB></Add>")))
                .andRespond(withServerOrReceiverFault("Simulated server error", Locale.ENGLISH));

        assertThatThrownBy(() -> soapClient.add(2, 2))
                .isInstanceOf(CalculatorServiceException.class)
                .hasMessageContaining("Add");

        mockServer.verify();
    }
}
