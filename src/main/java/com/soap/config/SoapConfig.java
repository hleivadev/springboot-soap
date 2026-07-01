package com.soap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.soap.client.SoapClient;

@Configuration
public class SoapConfig {

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
        soapClient.setDefaultUri("http://www.dneonline.com/calculator.asmx");
        soapClient.setMarshaller(marshaller);
        soapClient.setUnmarshaller(marshaller);
        return soapClient;

    }

}
