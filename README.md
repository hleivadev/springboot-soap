# 📦 Spring Boot SOAP Client — Calculator API

Cliente SOAP construido con **Spring Boot 3.4.5**, **Java 17** y **Maven**, que consume el [Calculator Web Service](http://www.dneonline.com/calculator.asmx?WSDL) (un servicio SOAP público de operaciones matemáticas básicas, ampliamente usado como referencia para practicar integración SOAP).

El foco del proyecto no es el servicio en sí (es deliberadamente simple), sino mostrar cómo estructurar un cliente SOAP en Spring de forma productiva: manejo de errores propio, logging de las peticiones/respuestas, y tests que no dependen de que el servicio real esté disponible.

---

## 🚀 Funcionalidad

- Consume las 4 operaciones del servicio: **Add, Subtract, Multiply, Divide**.
- Código de los tipos SOAP (`Add`, `AddResponse`, etc.) generado automáticamente desde el WSDL en tiempo de build, vía `jaxws-maven-plugin` (no se versiona en el repo — se regenera en cada `mvn compile`).
- **Manejo de errores propio**: cualquier falla (fault del servicio, problema de conectividad, o división por cero) se traduce a una única excepción de negocio (`CalculatorServiceException`) en vez de dejar filtrar las excepciones internas de Spring-WS.
- **Interceptor de logging**: registra el XML crudo de cada request/response SOAP a nivel `DEBUG`, útil para depurar integraciones sin necesitar un sniffer de red.
- **Tests con `MockWebServiceServer`**: usan el framework de testing propio de Spring-WS para simular el servicio, así los tests corren rápido y no dependen de internet ni de que `dneonline.com` esté disponible.

---

## 📋 Tecnologías usadas

- Java 17
- Spring Boot 3.4.5 (Spring Web Services)
- Maven
- JAXB (Jakarta XML Binding 3.0.1)
- JUnit 5 + AssertJ + `spring-ws-test` (testing)

---

## 🛠️ Cómo levantar el proyecto

```bash
git clone https://github.com/hleivadev/springbootsoap.git
cd springbootsoap
./mvnw spring-boot:run
```

Al levantar, un `CommandLineRunner` llama a las 4 operaciones contra el servicio real y deja el resultado en el log:

```
2 + 2 = 4
10 - 4 = 6
6 x 7 = 42
20 / 5 = 4
```

## ✅ Cómo correr los tests

```bash
./mvnw test
```

Los tests no llaman al servicio real: `MockWebServiceServer` intercepta las llamadas salientes del `WebServiceTemplate`, permite verificar el payload enviado y scriptear la respuesta — el mismo enfoque que `MockMvc` para controllers REST.

---

## 🗂️ Estructura

```
src/main/java/com/soap
├── client/        SoapClient — construye cada request y delega el envío
├── config/        SoapConfig — configura el marshaller, el unmarshaller y el interceptor
├── exception/      CalculatorServiceException — única excepción expuesta a los llamadores
├── interceptor/    SoapLoggingInterceptor — logging de requests/responses SOAP
└── springbootsoap/ SpringbootsoapApplication — clase principal + demo
```
