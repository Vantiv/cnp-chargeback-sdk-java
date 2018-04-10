package com.cnp.sdk;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.activation.MimetypesFileTypeMap;
import javax.net.ssl.SSLContext;

import com.cnp.sdk.generate.ChargebackDocumentUploadResponse;
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
    private final String AUTHORIZATION_HEADER = "Authorization";
    private final String CONTENT_TYPE_HEADER = "Content-Type";
    private final String ACCEPT_HEADER = "Accept";
    private final String CONNECTION_HEADER = "Connection";
    private final String CONNECTION_CLOSE = "close";
    private final String CONTENT_TYPE_CNP = "application/com.vantivcnp.services-v2+xml";
    private final String CONNECTION_EXCEPTION_MESSAGE = "Error connecting to Vantiv";

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

    public File getDocumentRequest(String filepath, Properties configuration, String urlSuffix) {
        File document;

        String requestUrl = configuration.getProperty("url") + urlSuffix;
        HttpGet get = new HttpGet(requestUrl);

        prepareHttpRequest(get, configuration);

        HttpEntity entity = null;
        try {
            HttpResponse response = httpClient.execute(get);
            validateDocumentResponse(response, filepath);
            document = new File(filepath);
        } catch (IOException e) {
            throw new ChargebackException(CONNECTION_EXCEPTION_MESSAGE, e);
        } finally {
            if (entity != null) {
                EntityUtils.consumeQuietly(entity);
            }
            get.abort();
        }
        return document;
    }

    public String postDocumentRequest(File file, String urlSuffix, Properties configuration) {
        String requestUrl = configuration.getProperty("url") + urlSuffix;
        HttpPost post = new HttpPost(requestUrl);
        post.setHeader(CONTENT_TYPE_HEADER, getFileContentType(file));
        post.setEntity(new FileEntity(file));
        return requestToCnp(post, configuration);
    }

    public String putDocumentRequest(File file, String urlSuffix, Properties configuration) {
        String requestUrl = configuration.getProperty("url") + urlSuffix;
        HttpPut put = new HttpPut(requestUrl);
        put.setHeader(CONTENT_TYPE_HEADER, getFileContentType(file));
        return requestToCnp(put, configuration);
    }

    public String deleteDocumentRequest(Properties configuration, String urlSuffix) {
        String requestUrl = configuration.getProperty("url") + urlSuffix;
        HttpDelete delete = new HttpDelete(requestUrl);
        return requestToCnp(delete, configuration);
    }

    public String getRequest(Properties configuration, String urlSuffix) {
        String requestUrl = configuration.getProperty("url") + urlSuffix;
        HttpGet get = new HttpGet(requestUrl);
        get.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_CNP);
        get.setHeader(ACCEPT_HEADER, CONTENT_TYPE_CNP);
        return requestToCnp(get, configuration);
    }

    public String putRequest(Properties configuration, String urlSuffix, String xmlRequest) {
        String requestUrl = configuration.getProperty("url") + urlSuffix;
        HttpPut put = new HttpPut(requestUrl);
        put.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_CNP);
        put.setHeader(ACCEPT_HEADER, CONTENT_TYPE_CNP);
        put.setEntity(new StringEntity(xmlRequest,"UTF-8"));
        return requestToCnp(put, configuration);
    }

    /* Sends given HttpRequest to server, after preparing it */
    public String requestToCnp(HttpRequestBase baseRequest, Properties configuration){
        String xmlResponse;
        prepareHttpRequest(baseRequest, configuration);

        try {
            xmlResponse = execHttpRequest(baseRequest, configuration);
        } catch (Exception e) {
            throw e;
        }
        return xmlResponse;
    }

    /* Prepare HttpRequest: set default headers, configs */
    public void prepareHttpRequest(HttpRequestBase baseRequest, Properties configuration){
        boolean httpKeepAlive = Boolean.valueOf(configuration.getProperty("httpKeepAlive", "false"));
        RequestConfig requestConfig = generateHttpRequestConfig(configuration);

        String authCode = generateAuthcode(configuration);
        baseRequest.setHeader(AUTHORIZATION_HEADER, authCode);
        if(!httpKeepAlive) {
            baseRequest.setHeader(CONNECTION_HEADER, CONNECTION_CLOSE);
        }

        baseRequest.setConfig(requestConfig);
    }

    /* Executes HttpRequest, receieves response and validates it */
    public String execHttpRequest(HttpRequestBase baseRequest, Properties configuration){
        String xmlResponse;
        try {
            HttpResponse response = httpClient.execute(baseRequest);
            xmlResponse = validateResponse(response);
            printToConsole(xmlResponse, configuration);
        } catch (IOException e) {
            throw new ChargebackException(CONNECTION_EXCEPTION_MESSAGE, e);
        } finally {
            baseRequest.abort();
        }
        return xmlResponse;
    }

    /* Generate base64 encoded code for authentication */
    private String generateAuthcode(Properties configuration){
        String username = configuration.getProperty("username");
        String password = configuration.getProperty("password");

        return "Basic " + new String(Base64.encodeBase64((username + ":" + password).getBytes()));
    }

    /* Get content-type for input file */
    private String getFileContentType(File file){
        return new MimetypesFileTypeMap().getContentType(file);
    }

    private String getFileLength(File file){
        return String.valueOf(file.length());
    }

    /* Method to generate RequestConfig object for HttpRequests */
    private RequestConfig generateHttpRequestConfig(Properties configuration){
        String proxyHost = configuration.getProperty("proxyHost");
        String proxyPort = configuration.getProperty("proxyPort");
        int httpTimeout = Integer.valueOf(configuration.getProperty("timeout", "6000"));
        HttpHost proxy;
        RequestConfig requestConfig;
        if (proxyHost != null && proxyHost.length() > 0 && proxyPort != null && proxyHost.length() > 0) {
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

    /* Method to check Http response code is a Success response */
    private String validateResponse(HttpResponse response){
        HttpEntity entity = null;
        String xmlResponse;
        try{
            entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() != 200) {
                System.out.println(EntityUtils.toString(entity,"UTF-8"));
                throw new ChargebackException(response.getStatusLine().getStatusCode() + ":" +
                        response.getStatusLine().getReasonPhrase());
            }
            xmlResponse = EntityUtils.toString(entity,"UTF-8");
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

    private void validateDocumentResponse(HttpResponse response, String filepath){
        HttpEntity entity = null;
        try{
            entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() != 200) {
                System.out.println(EntityUtils.toString(entity,"UTF-8"));
                throw new ChargebackException(response.getStatusLine().getStatusCode() + ":" +
                        response.getStatusLine().getReasonPhrase());
            }

            if(!"image/tiff".equals(entity.getContentType().getValue())){
                String xmlResponse = EntityUtils.toString(entity,"UTF-8");
                System.out.println(xmlResponse);
                ChargebackDocumentUploadResponse responseObj = XMLConverter.generateDocumentResponse(xmlResponse);
                throw new ChargebackException(responseObj.getResponseCode()+":"+responseObj.getResponseMessage());
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

    /* Method to print request & response to console */
    private void printToConsole(String xmlRequest, String xmlResponse, Properties configuration){
        boolean printxml = "true".equalsIgnoreCase(configuration.getProperty("printxml"));
        boolean neuterXml = "true".equalsIgnoreCase(configuration.getProperty("neuterXml"));
        if (printxml) {
            if (neuterXml) {
                xmlRequest = neuterXml(xmlRequest);
                xmlResponse = neuterXml(xmlResponse);
            }
            System.out.println("Request XML: " + xmlRequest);
            System.out.println("Response XML: " + xmlResponse);
        }
    }

    private void printToConsole(String xmlResponse, Properties configuration){
        boolean printxml = "true".equalsIgnoreCase(configuration.getProperty("printxml"));
        boolean neuterXml = "true".equalsIgnoreCase(configuration.getProperty("neuterXml"));
        if (printxml) {
            if (neuterXml) {
                xmlResponse = neuterXml(xmlResponse);
            }
            System.out.println("Response XML: " + xmlResponse);
        }
    }

    /* Method to neuter out sensitive information from xml */
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