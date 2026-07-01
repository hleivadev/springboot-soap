package com.soap.config;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;

import com.soap.client.SoapClient;
import com.soap.interceptor.SoapLoggingInterceptor;

@Configuration
public class SoapConfig {

    private static final String CALCULATOR_URI = "http://www.dneonline.com/calculator.asmx";

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(
                com.soap.wsdl.Add.class,
                com.soap.wsdl.AddResponse.class,
                com.soap.wsdl.Subtract.class,
                com.soap.wsdl.SubtractResponse.class,
                com.soap.wsdl.Multiply.class,
                com.soap.wsdl.MultiplyResponse.class,
                com.soap.wsdl.Divide.class,
                com.soap.wsdl.DivideResponse.class);
        return marshaller;
    }

    @Bean
    public SoapClient getSoapClient(Jaxb2Marshaller marshaller) {

        SoapClient soapClient = new SoapClient();
        soapClient.setDefaultUri(CALCULATOR_URI);
        soapClient.setMarshaller(marshaller);
        soapClient.setUnmarshaller(marshaller);
        soapClient.getWebServiceTemplate()
                .setInterceptors(new ClientInterceptor[] { new SoapLoggingInterceptor() });
        // dneonline.com's IIS server resets the connection when it sees the
        // default "Java/x.y" User-Agent, so it must be overridden here.
        soapClient.getWebServiceTemplate().setMessageSender(new HttpUrlConnectionMessageSender() {
            @Override
            protected void prepareConnection(HttpURLConnection connection) throws IOException {
                super.prepareConnection(connection);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            }
        });
        return soapClient;

    }

}
