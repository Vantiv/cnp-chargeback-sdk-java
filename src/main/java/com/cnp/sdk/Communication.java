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
    private final String CONTENT_TYPE_VALUE = "application/com.vantivcnp.services-v2+xml";
    private final String ACCEPT_HEADER = "Accept";
    private final String CONNECTION_EXCEPTION_MESSAGE = "Error connecting to Vantiv";
    private final String XML_ENCODING = "UTF-8";

    public Communication() {
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

    public File httpGetDocumentRequest(String filepath, String requestUrl, Properties config) {
        File document;
        HttpGet get = new HttpGet(requestUrl);
        prepareHttpRequest(get, config);

        printToConsole("\nGET request to url: \n", requestUrl, config);

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

    ////////////////////////////////////////////////////////////////////
    //                    Return response objects:                    //
    ////////////////////////////////////////////////////////////////////

    public ChargebackDocumentUploadResponse getDocumentListRequest(String requestUrl, Properties config) {
        String response = httpGetDocumentListRequest(requestUrl, config);
        return XMLConverter.generateDocumentResponse(response);
    }

    public ChargebackDocumentUploadResponse postDocumentRequest(File file, String requestUrl, Properties config) {
        String response = httpPostDocumentRequest(file, requestUrl, config);
        return XMLConverter.generateDocumentResponse(response);
    }
    public ChargebackDocumentUploadResponse putDocumentRequest(File file, String requestUrl, Properties config) {
        String response = httpPutDocumentRequest(file, requestUrl, config);
        return XMLConverter.generateDocumentResponse(response);
    }

    public ChargebackDocumentUploadResponse deleteDocumentRequest(String requestUrl, Properties config) {
        String response = httpDeleteDocumentRequest(requestUrl, config);
        return XMLConverter.generateDocumentResponse(response);
    }

    public ChargebackRetrievalResponse getRetrievalRequest(String requestUrl, Properties config) {
        String response = httpGetRetrievalRequest(requestUrl, config);
        return XMLConverter.generateRetrievalResponse(response);
    }

    public ChargebackUpdateResponse putUpdateRequest(String xmlRequest, String requestUrl, Properties config) {
        String response = httpPutUpdateRequest(xmlRequest, requestUrl, config);
        return XMLConverter.generateUpdateResponse(response);
    }

    ////////////////////////////////////////////////////////////////////
    //                    Return xml string response:                 //
    ////////////////////////////////////////////////////////////////////

    public String httpGetDocumentListRequest(String requestUrl, Properties config) {
        HttpGet get = new HttpGet(requestUrl);
        printToConsole("\nGET request to url: \n", requestUrl, config);
        return sendHttpRequestToCnp(get, config);
    }

    public String httpPostDocumentRequest(File file, String requestUrl, Properties config) {
        HttpPost post = new HttpPost(requestUrl);
        post.setHeader(CONTENT_TYPE_HEADER, getFileContentType(file));
        post.setEntity(new FileEntity(file));
        printToConsole("\nPOST request to url: \n", requestUrl, config);
        printToConsole("\nEntity: \n", file.getName(), config);
        return sendHttpRequestToCnp(post, config);
    }

    public String httpPutDocumentRequest(File file, String requestUrl, Properties config) {
        HttpPut put = new HttpPut(requestUrl);
        put.setHeader(CONTENT_TYPE_HEADER, getFileContentType(file));
        put.setEntity(new FileEntity(file));
        printToConsole("\nPUT request to url: \n", requestUrl, config);
        printToConsole("\nEntity: \n", file.getName(), config);
        return sendHttpRequestToCnp(put, config);
    }

    public String httpDeleteDocumentRequest(String requestUrl, Properties config) {
        HttpDelete delete = new HttpDelete(requestUrl);
        printToConsole("\nDELETE request to url: \n", requestUrl, config);
        return sendHttpRequestToCnp(delete, config);
    }

    public String httpGetRetrievalRequest(String requestUrl, Properties config) {
        HttpGet get = new HttpGet(requestUrl);
        get.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE);
        get.setHeader(ACCEPT_HEADER, CONTENT_TYPE_VALUE);
        printToConsole("\nGET request to url: \n", requestUrl, config);
        return sendHttpRequestToCnp(get, config);
    }

    public String httpPutUpdateRequest(String xmlRequest, String requestUrl, Properties config) {
        HttpPut put = new HttpPut(requestUrl);
        put.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE);
        put.setHeader(ACCEPT_HEADER, CONTENT_TYPE_VALUE);
        put.setEntity(new StringEntity(xmlRequest, XML_ENCODING));
        printToConsole("\nPUT request to url: \n", requestUrl, config);
        printToConsole("\nRequest XML: \n", xmlRequest, config);
        return sendHttpRequestToCnp(put, config);
    }

    ////////////////////////////////////////////////////////////////////

    /**
     *  Method to send given HttpRequest to server, after preparing it. Returns response from server
     */
    public String sendHttpRequestToCnp(HttpRequestBase baseRequest, Properties config){
        HttpResponse response = execHttpRequest(baseRequest, config);
        String xmlResponse = validateResponse(response);
        printToConsole("\nResponse XML: \n", xmlResponse, config);
        return xmlResponse;
    }

    /**
     *  Method to execute HttpRequest: given http request is sent, and receieved response is returned after validation
     */
    public HttpResponse execHttpRequest(HttpRequestBase baseRequest, Properties config){
        prepareHttpRequest(baseRequest, config);
        try {
            return httpClient.execute(baseRequest);
        } catch (IOException e) {
            throw new ChargebackException(CONNECTION_EXCEPTION_MESSAGE, e);
        } finally {
            baseRequest.abort();
        }
    }

    /**
     *  Method to prepare HttpRequest: set default headers, configs to given http request
     */
    public void prepareHttpRequest(HttpRequestBase baseRequest, Properties config){
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
            if (response.getStatusLine().getStatusCode() != 200) {
                System.out.println(EntityUtils.toString(entity,XML_ENCODING));
                throw new ChargebackException(response.getStatusLine().getStatusCode() + ":" +
                        response.getStatusLine().getReasonPhrase());
            }
            xmlResponse = EntityUtils.toString(entity,XML_ENCODING);
        }
        catch (IOException e) {
            throw new ChargebackException("There was an exception while fetching the response xml.", e);
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
        try{
            entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() != 200) {
                System.out.println(EntityUtils.toString(entity, XML_ENCODING));
                throw new ChargebackException(response.getStatusLine().getStatusCode() + ":" +
                        response.getStatusLine().getReasonPhrase());
            }

            if(!"image/tiff".equals(entity.getContentType().getValue())){
                String xmlResponse = EntityUtils.toString(entity, XML_ENCODING);
                System.out.println(xmlResponse);
                ChargebackDocumentUploadResponse responseObj = XMLConverter.generateDocumentResponse(xmlResponse);
                throw new ChargebackException(responseObj.getResponseCode() + ":" + responseObj.getResponseMessage());
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
            throw new ChargebackException("There was an exception while fetching the requested document.", e);
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
    private void printToConsole(String prefixMessage, String xml, Properties config){
        boolean printxml = "true".equalsIgnoreCase(config.getProperty("printxml"));
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
}