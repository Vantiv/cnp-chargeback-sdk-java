package com.cnp.sdk;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.activation.MimetypesFileTypeMap;
import javax.net.ssl.SSLContext;

import com.cnp.sdk.generate.ChargebackDocumentUploadResponse;
import com.cnp.sdk.generate.ChargebackRetrievalResponse;
import com.cnp.sdk.generate.ChargebackUpdateResponse;
import com.cnp.sdk.generate.ErrorResponse;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.commons.codec.binary.Base64;

public class Communication {

    private static final String[] SUPPORTED_PROTOCOLS = new String[] {"TLSv1.2", "TLSv1.1"};
    private static final String NEUTER_STR = "NEUTERED";
    private CloseableHttpClient httpClient;
    private final int KEEP_ALIVE_DURATION = 8000;
    private final String CONTENT_TYPE_HEADER = "Content-Type";
    private final String CNP_CONTENT_TYPE = "application/com.vantivcnp.services-v2+xml";
    private final String ACCEPT_HEADER = "Accept";
    private final String CONNECTION_EXCEPTION_MESSAGE = "Error connecting to Vantiv";
    private final String XML_ENCODING = "UTF-8";
    private Properties config;

    public Communication(Properties config) {
        setupCommunication();
        this.config = config;
    }

    public Communication() {
        setupCommunication();
        this.config = (new Configuration()).getProperties();
    }

    private void setupCommunication(){
        try {

            String protocol = getBestProtocol(SSLContext.getDefault().getSupportedSSLParameters().getProtocols());
            if (protocol == null) {
                throw new IllegalStateException("No supported TLS protocols available");
            }
            SSLContext ctx = SSLContexts.custom().useProtocol(protocol).build();
            ConnectionSocketFactory plainSocketFactory = new PlainConnectionSocketFactory();
            LayeredConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(ctx);

            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", plainSocketFactory)
                    .register("https", sslSocketFactory)
                    .build();

            BasicHttpClientConnectionManager manager = new BasicHttpClientConnectionManager(registry);

            HttpRequestRetryHandler requestRetryHandler = new DefaultHttpRequestRetryHandler(0, true);
            // Vantiv will a close an idle connection, so we define our Keep-alive strategy to be below that threshold
            ConnectionKeepAliveStrategy keepAliveStrategy = new ConnectionKeepAliveStrategy() {
                public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                    return KEEP_ALIVE_DURATION;
                }
            };
            httpClient = HttpClients.custom().setConnectionManager(manager)
                    .setRetryHandler(requestRetryHandler)
                    .setKeepAliveStrategy(keepAliveStrategy)
                    .build();

        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static String getBestProtocol(final String[] availableProtocols) {
        String bestProtocol = null;
        if (availableProtocols == null || availableProtocols.length == 0) {
            return bestProtocol;
        }
        List<String> availableProtocolsList = Arrays.asList(availableProtocols);
        for (String supportedProtocol: SUPPORTED_PROTOCOLS) {
            if (availableProtocolsList.contains(supportedProtocol)) {
                bestProtocol = supportedProtocol;
                break;
            }
        }
        return bestProtocol;
    }

    ////////////////////////////////////////////////////////////////////
    //                Chargeback service end points:                  //
    ////////////////////////////////////////////////////////////////////

    public File httpGetDocumentRequest(String filepath, String requestUrl) {
        File document;
        HttpGet get = new HttpGet(requestUrl);
        prepareHttpRequest(get);

        printToConsole("\nGET request to url: \n", requestUrl);

        try {
            HttpResponse response = httpClient.execute(get);
            validateDocumentResponse(response, filepath);
            document = new File(filepath);
        } catch (IOException e) {
            throw new ChargebackException(CONNECTION_EXCEPTION_MESSAGE, e);
        } finally {
            get.abort();
        }
        return document;
    }

    public ChargebackDocumentUploadResponse httpGetDocumentListRequest(String requestUrl) {
        HttpGet get = new HttpGet(requestUrl);
        printToConsole("\nGET request to url: \n", requestUrl);
        String response = sendHttpRequestToCnp(get);
        return XMLConverter.generateDocumentResponse(response);
    }

    public ChargebackDocumentUploadResponse httpPostDocumentRequest(File file, String requestUrl) {
        HttpPost post = new HttpPost(requestUrl);
        post.setHeader(CONTENT_TYPE_HEADER, getFileContentType(file));
        post.setEntity(new FileEntity(file));
        printToConsole("\nPOST request to url: \n", requestUrl);
        printToConsole("\nEntity: \n", file.getName());
        String response = sendHttpRequestToCnp(post);
        return XMLConverter.generateDocumentResponse(response);

    }

    public ChargebackDocumentUploadResponse httpPutDocumentRequest(File file, String requestUrl) {
        HttpPut put = new HttpPut(requestUrl);
        put.setHeader(CONTENT_TYPE_HEADER, getFileContentType(file));
        put.setEntity(new FileEntity(file));
        printToConsole("\nPUT request to url: \n", requestUrl);
        printToConsole("\nEntity: \n", file.getName());
        String response = sendHttpRequestToCnp(put);
        return XMLConverter.generateDocumentResponse(response);
    }

    public ChargebackDocumentUploadResponse httpDeleteDocumentRequest(String requestUrl) {
        HttpDelete delete = new HttpDelete(requestUrl);
        printToConsole("\nDELETE request to url: \n", requestUrl);
        String response = sendHttpRequestToCnp(delete);
        return XMLConverter.generateDocumentResponse(response);
    }

    public ChargebackRetrievalResponse httpGetRetrievalRequest(String requestUrl) {
        HttpGet get = new HttpGet(requestUrl);
        get.setHeader(CONTENT_TYPE_HEADER, CNP_CONTENT_TYPE);
        get.setHeader(ACCEPT_HEADER, CNP_CONTENT_TYPE);
        printToConsole("\nGET request to url: \n", requestUrl);
        String response = sendHttpRequestToCnp(get);
        return XMLConverter.generateRetrievalResponse(response);
    }

    public ChargebackUpdateResponse httpPutUpdateRequest(String xmlRequest, String requestUrl) {
        HttpPut put = new HttpPut(requestUrl);
        put.setHeader(CONTENT_TYPE_HEADER, CNP_CONTENT_TYPE);
        put.setHeader(ACCEPT_HEADER, CNP_CONTENT_TYPE);
        put.setEntity(new StringEntity(xmlRequest, XML_ENCODING));
        printToConsole("\nPUT request to url: \n", requestUrl);
        printToConsole("\nRequest XML: \n", xmlRequest);
        String response = sendHttpRequestToCnp(put);
        return XMLConverter.generateUpdateResponse(response);
    }

    ////////////////////////////////////////////////////////////////////

    /**
     *  Method to send given HttpRequest to server, after preparing it. Returns response from server
     */
    private String sendHttpRequestToCnp(HttpRequestBase baseRequest){
        String xmlResponse = execHttpRequest(baseRequest);
        printToConsole("\nResponse XML: \n", xmlResponse);
        return xmlResponse;
    }

    /**
     *  Method to execute HttpRequest: given http request is sent, and receieved response is returned after validation
     */
    private String execHttpRequest(HttpRequestBase baseRequest){
        prepareHttpRequest(baseRequest);
        try {
            HttpResponse response = httpClient.execute(baseRequest);
            return validateResponse(response);
        } catch (IOException e) {
            throw new ChargebackException(CONNECTION_EXCEPTION_MESSAGE, e);
        } finally {
            System.out.println("Headers");
            for(Header header : baseRequest.getAllHeaders()) {
                System.out.println(header.getName() +" : "+header.getValue());
            }
            baseRequest.abort();
        }
    }

    /**
     *  Method to prepare HttpRequest: set default headers, configs to given http request
     */
    private void prepareHttpRequest(HttpRequestBase baseRequest){
        String proxyHost = config.getProperty("proxyHost");
        String proxyPort = config.getProperty("proxyPort");
        int httpTimeout = Integer.valueOf(config.getProperty("timeout", "6000"));
        String username = config.getProperty("username");
        String password = config.getProperty("password");
        boolean httpKeepAlive = Boolean.valueOf(config.getProperty("httpKeepAlive", "false"));

        RequestConfig requestConfig = generateHttpRequestConfig(proxyHost, proxyPort, httpTimeout);
        String authCode = generateAuthcode(username, password);
        baseRequest.setHeader("Authorization", authCode);
        if(!httpKeepAlive) {
            baseRequest.setHeader("Connection", "close");
        }

        baseRequest.setConfig(requestConfig);
    }

    /**
     *  Method to generate and return the base64 encoded code for authentication
     */
    private String generateAuthcode(String username, String password){
        return "Basic " + new String(Base64.encodeBase64((username + ":" + password).getBytes()));
    }

    /**
     *  Method to return the content-type for input file
     */
    private String getFileContentType(File file){
        return new MimetypesFileTypeMap().getContentType(file);
    }

    /**
     *  Method to return the content-length for input file in bytes
     */
    private String getFileLength(File file){
        return String.valueOf(file.length());
    }

    /**
     *  Method to generate and return the RequestConfig object for HttpRequests
     */
    private RequestConfig generateHttpRequestConfig(String proxyHost, String proxyPort, int httpTimeout){
        HttpHost proxy;
        RequestConfig requestConfig;
        if (proxyHost != null && proxyHost.length() > 0 && proxyPort != null) {
            proxy = new HttpHost(proxyHost, Integer.valueOf(proxyPort));
            requestConfig = RequestConfig.copy(RequestConfig.DEFAULT)
                    .setProxy(proxy)
                    .setSocketTimeout(httpTimeout)
                    .setConnectTimeout(httpTimeout)
                    .build();
        } else {
            requestConfig = RequestConfig.copy(RequestConfig.DEFAULT)
                    .setSocketTimeout(httpTimeout)
                    .setConnectTimeout(httpTimeout)
                    .build();
        }
        return requestConfig;
    }

    /**
     *  method to Validate response: check that the Http response code is a Success response
     *                               and return the resonse if valid
     */
    private String validateResponse(HttpResponse response){
        HttpEntity entity = null;
        String xmlResponse;
        try{
            entity = response.getEntity();
            String contentType = entity.getContentType().getValue();
            int statusCode = response.getStatusLine().getStatusCode();
            xmlResponse = EntityUtils.toString(entity, XML_ENCODING);

            if (statusCode != 200) {
                printToConsole("\nErrorResponse: ", xmlResponse);
                if(contentType.contains(CNP_CONTENT_TYPE)){
                    ErrorResponse errorResponse = XMLConverter.generateErrorResponse(xmlResponse);
                    throw new ChargebackWebException(getErrorMessage(errorResponse), String.valueOf(statusCode), errorResponse.getErrors().getErrors());
                }
                throw new ChargebackWebException(xmlResponse, String.valueOf(statusCode));
            }
        }
        catch (IOException e) {
            throw new ChargebackWebException("There was an exception while fetching the response xml.", e);
        }
        finally {
            if (entity != null) {
                EntityUtils.consumeQuietly(entity);
            }
        }
        return xmlResponse;
    }

    /**
     *  Method to Validate document response: check that the Http response code is a Success response
     *                                        and the document returned is of the right type
     */
    private void validateDocumentResponse(HttpResponse response, String filepath){
        HttpEntity entity = null;
        String xmlResponse;
        try{
            entity = response.getEntity();
            String contentType = entity.getContentType().getValue();
            int statusCode = response.getStatusLine().getStatusCode();

            if(contentType.contains(CNP_CONTENT_TYPE)) {
                xmlResponse = EntityUtils.toString(entity, XML_ENCODING);
                printToConsole("\nErrorResponse: ", xmlResponse);
                if (statusCode != 200) {
                    ErrorResponse errorResponse = XMLConverter.generateErrorResponse(xmlResponse);
                    throw new ChargebackWebException(getErrorMessage(errorResponse), String.valueOf(statusCode), errorResponse.getErrors().getErrors());
                }
                else{
                    ChargebackDocumentUploadResponse errorResponse = XMLConverter.generateDocumentResponse(xmlResponse);
                    throw new ChargebackDocumentException(errorResponse.getResponseMessage(), errorResponse.getResponseCode());
                }
            }
            else if(statusCode != 200){
                xmlResponse = EntityUtils.toString(entity, XML_ENCODING);
                printToConsole("\nErrorResponse: ", xmlResponse);
                throw new ChargebackWebException(xmlResponse, String.valueOf(statusCode));
            }

            InputStream is = entity.getContent();
            FileOutputStream fos = new FileOutputStream(new File(filepath));
            int inByte;
            while((inByte = is.read()) != -1)
                fos.write(inByte);
            is.close();
            fos.close();
        }
        catch (IOException e) {
            throw new ChargebackDocumentException("There was an exception while fetching the requested document.", e);
        }
        finally {
            if (entity != null) {
                EntityUtils.consumeQuietly(entity);
            }
        }
    }

    /**
     *  Method to print xml to console: a prefixMessage is appended to given xml before printing to console
     */
    private void printToConsole(String prefixMessage, String xml){
        boolean printxml = "true".equalsIgnoreCase(config.getProperty("printXml"));
        boolean neuterXml = "true".equalsIgnoreCase(config.getProperty("neuterXml"));
        if (printxml) {
            if (neuterXml) {
                xml = neuterXml(xml);
            }
            System.out.println(prefixMessage + xml);
        }
    }

    /**
     *  Method to neuter out sensitive information from xml
     */
    public String neuterXml(String xml) {
        if (xml == null) {
            return xml;
        }
        xml = xml.replaceAll("<accNum>.*</accNum>", "<accNum>" + NEUTER_STR + "</accNum>");
        xml = xml.replaceAll("<user>.*</user>", "<user>" + NEUTER_STR + "</user>");
        xml = xml.replaceAll("<password>.*</password>", "<password>" + NEUTER_STR + "</password>");
        xml = xml.replaceAll("<track>.*</track>", "<track>" + NEUTER_STR + "</track>");
        xml = xml.replaceAll("<number>.*</number>", "<number>" + NEUTER_STR + "</number>");
        return xml;
    }

    private String getErrorMessage(ErrorResponse errorResponse){
        StringBuilder errorMessage = new StringBuilder();
        String prefix = "";

        for (String error : errorResponse.getErrors().getErrors()) {
            errorMessage.append(prefix).append(error);
            prefix = "\n";
        }

        return errorMessage.toString();
    }
}