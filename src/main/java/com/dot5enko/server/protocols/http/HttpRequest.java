package com.dot5enko.server.protocols.http;

import com.dot5enko.server.Request;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.impl.entity.EntityDeserializer;
import org.apache.http.impl.entity.LaxContentLengthStrategy;
import org.apache.http.impl.io.AbstractSessionInputBuffer;
import org.apache.http.impl.io.HttpRequestParser;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicLineParser;
import org.apache.http.params.BasicHttpParams;

public class HttpRequest extends Request {

    private Map<String, String> parameters = new HashMap<String, String>();
    private String uri;
    private String method;

    public HttpRequest(String request) throws Exception {
        super(request);
        try {

            org.apache.http.HttpRequest req = create(request);

            method = req.getRequestLine().getMethod();
            uri = req.getRequestLine().getUri();

            if (req instanceof BasicHttpEntityEnclosingRequest) {
                List<NameValuePair> pairs = URLEncodedUtils.parse(((BasicHttpEntityEnclosingRequest) req).getEntity());
                for (NameValuePair pair : pairs) {
                    parameters.put(pair.getName(), pair.getValue());
                }
            } else {
                String uri = req.getRequestLine().getUri();
                if (uri.contains("?")) {
                    String queryString = uri.split("\\?")[1];
                    String[] query = queryString.split("&");
                    for (String it : query) {

                        String value = null;
                        String[] kVal = it.split("=");
                        if (kVal.length > 1) {
                            value = kVal[1];
                        }

                        parameters.put(kVal[0].trim(), value);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(HttpRequest.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("Request is malformed");
        }
    }

    private org.apache.http.HttpRequest create(final String requestAsString) {
        try {
            SessionInputBuffer inputBuffer = new AbstractSessionInputBuffer() {
                {
                    init(new ByteArrayInputStream(requestAsString.getBytes()), 10, new BasicHttpParams());
                }

                @Override
                public boolean isDataAvailable(int timeout) throws IOException {
                    throw new RuntimeException("have to override but probably not even called");
                }
            };
            HttpMessageParser parser = new HttpRequestParser(inputBuffer, new BasicLineParser(new ProtocolVersion("HTTP", 1, 1)), new DefaultHttpRequestFactory(), new BasicHttpParams());
            HttpMessage message = parser.parse();
            if (message instanceof BasicHttpEntityEnclosingRequest) {
                BasicHttpEntityEnclosingRequest request = (BasicHttpEntityEnclosingRequest) message;
                EntityDeserializer entityDeserializer = new EntityDeserializer(new LaxContentLengthStrategy());
                HttpEntity entity = entityDeserializer.deserialize(inputBuffer, message);
                request.setEntity(entity);
            }
            return (org.apache.http.HttpRequest) message;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (HttpException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public String getUri() {
        return this.uri;
    }

    public String getMethod() {
        return this.method;
    }

}
