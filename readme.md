# HTTP Connection pool

This module contains the library code for Closeable HTTP Connection pool.
This library will create CloseableHttpClient (with custom properties) for every client defined in config file. 

# Usage
- Add library dependency in code and exclude duplicate dependencies.
  
  ````
          <dependency>
              <groupId>com.http.connection</groupId>
              <artifactId>core</artifactId>
              <version>VERSION</version>
          </dependency>
  
- Initialize class variable, connection pool by calling init method from application root(main).  
  ````  
  Properties/configs can be stored in .properties, .yaml or .xml files. Here considering .yaml as config file.
  
  File confFile = new File("$PATH/client.yaml");
          Map<String, ConnectionConfig> configMap = new ObjectMapper(new YAMLFactory()).readValue(confFile, new TypeReference<Map<String,
                  ConnectionConfig>>() {
          });
  
  HttpConnectionManager.init(configMap);        
- Sample clients.yaml  
    ````
    clientName1:                      #client name
        host: "xx.xx.xx.xx"
        port: $PORT
        connectionPoolSize: 1000      #Connection pool size for this client
        connectTimeout: 100           #This denotes the time elapsed before the connection established
        connectionRequestTimeout: 100 #Timeout in milliseconds used when requesting a connection from the connection manager.
        socketTimeout : 5000          #Time the timeout to receive data (socket timeout).
    clientName2:
        host: "yy.yy.yy.yy"
        port: $PORT
        connectionPoolSize: 500
        connectTimeout: 100
        connectionRequestTimeout: 100
        socketTimeout : 5000       
- Destroy connection pool: Call destroy method from root context while shutting down application to destroy connection pool and prevent resources leak.
   ````
   HttpConnectionManager.destroy();
- Making http call by using this library
    ````
    private <T> T getResult(TypeReference<T> responseType) {
            JSONHttpRequest jsonHttpRequest = new JSONHttpRequest();
            jsonHttpRequest.setUri(customUrlWithoutHostAndPort);
            jsonHttpRequest.setHeaders(headers);
            jsonHttpRequest.setHttpMethod(JSONHttpRequest.HttpMethod.POST); #method type
            //in case of PUT and POST method
            jsonHttpRequest.setEntity(requestBodyObject);
            
            JSONHttpResponse jsonHttpResponse;
            try {
                jsonHttpResponse = HttpRequestExecutor.getInstance().execute("clientNameSpecifiedInYamlFile", jsonHttpRequest);
            } catch (HttpConnectionException e) {
                throw new CustomServiceException(CustomServiceCodes.INTERNAL_SERVER_ERROR, e);
            }
    
            if (!jsonHttpResponse.isSuccess()) {
                throw new CustomServiceException(CustomServiceCodes.INVALID_ARGUMENTS);
            }
    
            try {
                return jsonHttpResponse.getEntity(responseType);
            } catch (IOException e) {
                throw new CustomServiceException(CustomServiceCodes.INTERNAL_SERVER_ERROR,e);
            }
      }
- Sample usage:
    ````
    public class Example {
        private static final String CLIENT1 = "client1";
    
        public static void main(String[] args) throws HttpConnectionException {
            Map<String, ConnectionConfig> poolsConfigMap = new HashMap<>();
            ConnectionConfig config = new ConnectionConfig();
            config.setHost("localhost");
            config.setPort(8080);
            config.setConnectionPoolSize(100);
            config.setConnectionRequestTimeout(100);
            config.setConnectTimeout(100);
            config.setSocketTimeout(1000);
            poolsConfigMap.put(CLIENT1, config);
    
            HttpConnectionManager.init(poolsConfigMap);
    
    
            //create appropriate jsonHttpRequest and pass
            JSONHttpRequest jsonHttpRequest = new JSONHttpRequest();
            HttpRequestExecutor.getInstance().execute(CLIENT1, jsonHttpRequest);
    
            //release connection for set of clients.
            HttpConnectionManager.destroy(poolsConfigMap.keySet());
        }
    }