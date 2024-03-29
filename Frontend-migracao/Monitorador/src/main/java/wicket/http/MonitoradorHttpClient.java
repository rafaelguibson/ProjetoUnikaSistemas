package wicket.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import wicket.entities.Monitorador;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MonitoradorHttpClient implements Serializable {
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    public MonitoradorHttpClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<Monitorador> listarTodos() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpGet request = new HttpGet(baseUrl);
            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);

            httpClient.close();
            response.close();

            return objectMapper.readValue(responseString, new TypeReference<List<Monitorador>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public Monitorador listarPorId(Long id) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(baseUrl + "/" + id);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity);

                return objectMapper.readValue(responseString, Monitorador.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Monitorador alterar(Monitorador novoMonitorador) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPatch request = new HttpPatch(baseUrl + "/" + novoMonitorador.getId());

            // Configure o cabeçalho Content-Type para JSON
            request.setHeader("Content-Type", "application/json; charset=UTF-8");

            String requestBody = objectMapper.writeValueAsString(novoMonitorador);
            request.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity);

                return objectMapper.readValue(responseString, Monitorador.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void excluir(Long id) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDelete request = new HttpDelete(baseUrl + "/" + id);
            httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Monitorador salvar(Monitorador novoMonitorador) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(baseUrl);
            request.setHeader("Content-Type", "application/json; charset=UTF-8");

            String requestBody = objectMapper.writeValueAsString(novoMonitorador);
            request.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity);

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    return objectMapper.readValue(responseString, Monitorador.class);

                } else {
                    throw new RuntimeException(responseString); // Lança uma exceção com a mensagem de erro
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteAllMonitoradores(List<Monitorador> monitoradores) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(baseUrl + "/deleteAll");
            request.setHeader("Content-Type", "application/json; charset=UTF-8");

            // Converte a lista de Monitorador para JSON
            String requestBody = objectMapper.writeValueAsString(monitoradores);
            request.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(request)) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Monitorador> filtrar(Monitorador filtro) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(baseUrl + "/filtrar");

            request.setHeader("Content-Type", "application/json; charset=UTF-8");

            String requestBody = objectMapper.writeValueAsString(filtro);
            request.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity);

                return objectMapper.readValue(responseString, new TypeReference<List<Monitorador>>() {
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Monitorador> listarPF() {
        return listarPorTipo("pf");
    }

    public List<Monitorador> listarPJ() {
        return listarPorTipo("pj");
    }

    private List<Monitorador> listarPorTipo(String tipo) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(baseUrl + "/" + tipo);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity);

                return objectMapper.readValue(responseString, new TypeReference<List<Monitorador>>() {});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Monitorador> uploadFile(File file) throws IOException, InterruptedException {
        // Converte o arquivo em array de bytes
        byte[] data = readFileToByteArray(file);

        // Cria o request com o array de bytes
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/upload"))
                .POST(HttpRequest.BodyPublishers.ofByteArray(data))
                .build();

        // Envia o request e recebe a resposta
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Converte a resposta JSON em uma lista de Monitorador
        // Assumindo que a resposta é um JSON e você possui um método para desserializar
        return parseJsonToList(response.body());
    }

    private byte[] readFileToByteArray(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fileInputStream.read(data);
            return data;
        }
    }

    private List<Monitorador> parseJsonToList(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, new TypeReference<List<Monitorador>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveAll(List<Monitorador> monitoradorList) throws RuntimeException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(baseUrl + "/saveAll");
            request.setHeader("Content-Type", "application/json; charset=UTF-8");

            // Converte a lista de Monitorador para JSON
            String requestBody = objectMapper.writeValueAsString(monitoradorList);
            request.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
            CloseableHttpResponse response = httpClient.execute(request);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                // Lança uma exceção com a mensagem do corpo da resposta do backend
                throw new RuntimeException(responseBody);
            }
        } catch (IOException e) {
            // Trate exceções de E/S, como falha na comunicação com o servidor
            throw new RuntimeException("Erro durante a chamada do endpoint.", e);
        }
    }

}

