package com.cnp.sdk;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
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

    //TODO: fix headers

    public File getDocumentRequest(String documentId, Properties configuration, String urlSuffix) {
        File document = null;
        boolean httpKeepAlive = Boolean.valueOf(configuration.getProperty("httpKeepAlive", "true"));
        RequestConfig requestConfig = generateRequestConfig(configuration);

        String requestUrl = configuration.getProperty("url") + urlSuffix;
        HttpGet get = new HttpGet(requestUrl);
        String username = configuration.getProperty("username");
        String password = configuration.getProperty("password");
        String authCode = new String(Base64.encodeBase64((username + ":" + password).getBytes()));
        get.setHeader("Authorization", "Basic " + authCode);
        if(!httpKeepAlive) {
            get.setHeader("Connection", "close");
        }

        get.setConfig(requestConfig);

        HttpEntity entity = null;
        try {
            HttpResponse response = httpClient.execute(get);
            entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new ChargebackException(response.getStatusLine().getStatusCode() + ":" +
                        response.getStatusLine().getReasonPhrase());
            }
            document = new File(documentId);
            entity.writeTo(new FileOutputStream(document));
        } catch (IOException e) {
            throw new ChargebackException("Exception connection to Vantiv", e);
        } finally {
            if (entity != null) {
                EntityUtils.consumeQuietly(entity);
            }
            get.abort();
        }
        return document;
    }

    public String postDocumentRequest(File file, String urlSuffix, Properties configuration) {
        String xmlResponse;
        boolean httpKeepAlive = Boolean.valueOf(configuration.getProperty("httpKeepAlive", "false"));
        RequestConfig requestConfig = generateRequestConfig(configuration);
        String requestUrl = configuration.getProperty("url") + urlSuffix;
        System.out.println(requestUrl);
        HttpPost post = new HttpPost(requestUrl);
        String username = configuration.getProperty("username");
        String password = configuration.getProperty("password");
        String authCode = new String(Base64.encodeBase64((username + ":" + password).getBytes()));
        post.setHeader("Authorization", "Basic " + authCode);
        post.setHeader("Content-Type", "image/png");
//        post.setHeader("Content-Length", new String(file.getTotalSpace()));
        if(!httpKeepAlive) {
            post.setHeader("Connection", "close");
        }

        post.setConfig(requestConfig);
        try {
            post.setEntity(new FileEntity(file));
            HttpResponse response = httpClient.execute(post);
            xmlResponse = validateDocumentResponse(response);
            printToConsole(null, xmlResponse, configuration);
        } catch (IOException e) {
            throw new ChargebackException("Exception connection to Litle", e);
        } finally {
            post.abort();
        }
        return xmlResponse;
    }

    public String putDocumentRequest(File file, String urlSuffix, Properties configuration) {
        String xmlResponse;
        boolean httpKeepAlive = Boolean.valueOf(configuration.getProperty("httpKeepAlive", "false"));
        RequestConfig requestConfig = generateRequestConfig(configuration);
        String requestUrl = configuration.getProperty("url") + urlSuffix;
        HttpPut put = new HttpPut(requestUrl);
        String username = configuration.getProperty("username");
        String password = configuration.getProperty("password");
        String authCode = new String(Base64.encodeBase64((username + ":" + password).getBytes()));
        put.setHeader("Authorization", "Basic " + authCode);
        put.setHeader("Content-Type", "image");
//        post.setHeader("Content-Length", "application/com.vantivcnp.services-v2+xml");
        if(!httpKeepAlive) {
            put.setHeader("Connection", "close");
        }

        put.setConfig(requestConfig);
        try {
            put.setEntity(new FileEntity(file));
            HttpResponse response = httpClient.execute(put);
            xmlResponse = validateDocumentResponse(response);
            printToConsole(null, xmlResponse, configuration);
        } catch (IOException e) {
            throw new ChargebackException("Exception connection to Vantiv", e);
        } finally {
            put.abort();
        }
        return xmlResponse;
    }

    public String deleteDocumentRequest(Properties configuration, String urlSuffix) {
        String xmlResponse;

        boolean httpKeepAlive = Boolean.valueOf(configuration.getProperty("httpKeepAlive", "false"));
        RequestConfig requestConfig = generateRequestConfig(configuration);

        String requestUrl = configuration.getProperty("url") + urlSuffix;
        HttpDelete delete = new HttpDelete(requestUrl);
        String username = configuration.getProperty("username");
        String password = configuration.getProperty("password");
        String authCode = new String(Base64.encodeBase64((username + ":" + password).getBytes()));
        delete.setHeader("Authorization", "Basic " + authCode);
        delete.setHeader("Content-Type", "image");
        if(!httpKeepAlive) {
            delete.setHeader("Connection", "close");
        }

        delete.setConfig(requestConfig);

        try {
            HttpResponse response = httpClient.execute(delete);
            xmlResponse = validateDocumentResponse(response);
            printToConsole(null, xmlResponse, configuration);
        } catch (IOException e) {
            throw new ChargebackException("Exception connection to Vantiv", e);
        } finally {
            delete.abort();
        }
        return xmlResponse;
    }

    private RequestConfig generateRequestConfig(Properties configuration){
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
            throw new ChargebackException("Exception connection to Vantiv", e);
        }
        finally {
            if (entity != null) {
                EntityUtils.consumeQuietly(entity);
            }
        }
        return xmlResponse;
    }

    private String validateDocumentResponse(HttpResponse response){
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
            throw new ChargebackException("Exception connection to Vantiv", e);
        }
        finally {
            if (entity != null) {
                EntityUtils.consumeQuietly(entity);
            }
        }
        return xmlResponse;
    }

    private void printToConsole(String xmlRequest, String xmlResponse, Properties configuration){
        boolean printxml = "true".equalsIgnoreCase(configuration.getProperty("printxml"));
        boolean neuterXml = "true".equalsIgnoreCase(configuration.getProperty("neuterXml"));
        if (printxml && xmlRequest!=null) {
            if (neuterXml) {
                xmlRequest = neuterXml(xmlRequest);
            }
            System.out.println("Request XML: " + xmlRequest);
        }

        if (printxml) {
            if (neuterXml) {
                xmlResponse = neuterXml(xmlResponse);
            }
            System.out.println("Response XML: " + xmlResponse);
        }
    }

    public String getRequest(Properties configuration, String urlSuffix) {
        String xmlResponse;

        boolean httpKeepAlive = Boolean.valueOf(configuration.getProperty("httpKeepAlive", "false"));
        RequestConfig requestConfig = generateRequestConfig(configuration);

        String requestUrl = configuration.getProperty("url") + urlSuffix;
        HttpGet get = new HttpGet(requestUrl);
        String username = configuration.getProperty("username");
        String password = configuration.getProperty("password");
        String authCode = new String(Base64.encodeBase64((username + ":" + password).getBytes()));
        get.setHeader("Authorization", "Basic " + authCode);
        get.setHeader("Content-Type", "application/com.vantivcnp.services-v2+xml");
        get.setHeader("Accept", "application/com.vantivcnp.services-v2+xml");
        if(!httpKeepAlive) {
            get.setHeader("Connection", "close");
        }

        get.setConfig(requestConfig);

        try {
            HttpResponse response = httpClient.execute(get);
            xmlResponse = validateResponse(response);
            printToConsole(null, xmlResponse, configuration);
        } catch (IOException e) {
            throw new ChargebackException("Exception connection to Vantiv", e);
        } finally {
            get.abort();
        }
        return xmlResponse;
    }


    public String putRequest(Properties configuration, String urlSuffix, String xmlRequest) {
        String xmlResponse;

        boolean httpKeepAlive = Boolean.valueOf(configuration.getProperty("httpKeepAlive", "false"));
        RequestConfig requestConfig = generateRequestConfig(configuration);

        String requestUrl = configuration.getProperty("url") + urlSuffix;
        HttpPut put = new HttpPut(requestUrl);
        String username = configuration.getProperty("username");
        String password = configuration.getProperty("password");
        String authCode = new String(Base64.encodeBase64((username + ":" + password).getBytes()));
        put.setHeader("Authorization", "Basic " + authCode);
        put.setHeader("Content-Type", "application/com.vantivcnp.services-v2+xml");
        put.setHeader("Accept", "application/com.vantivcnp.services-v2+xml");
        if(!httpKeepAlive) {
            put.setHeader("Connection", "close");
        }

        put.setConfig(requestConfig);
        try {
            put.setEntity(new StringEntity(xmlRequest,"UTF-8"));
            HttpResponse response = httpClient.execute(put);
            xmlResponse = validateResponse(response);
            printToConsole(xmlRequest, xmlResponse, configuration);
        } catch (IOException e) {
            throw new ChargebackException("Exception connection to Vantiv", e);
        } finally {
            put.abort();
        }
        return xmlResponse;
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