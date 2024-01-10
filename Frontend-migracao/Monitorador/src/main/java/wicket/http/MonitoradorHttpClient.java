package wicket.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import wicket.entities.Monitorador;

import java.io.IOException;
import java.io.Serializable;
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

            return objectMapper.readValue(responseString, new TypeReference<List<Monitorador>>() {});
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

    public Monitorador alterar(Long id, Monitorador novoMonitorador) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPatch request = new HttpPatch(baseUrl + "/" + id);

            String requestBody = objectMapper.writeValueAsString(novoMonitorador);
            request.setEntity(new StringEntity(requestBody));

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

            // Converte o objeto Monitorador para JSON
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

    public void deleteAllMonitoradores(List<Monitorador> monitoradores) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(baseUrl + "/deleteAll");
            request.setHeader("Content-Type", "application/json; charset=UTF-8");

            // Converte a lista de Monitorador para JSON
            String requestBody = objectMapper.writeValueAsString(monitoradores);
            request.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // adicionar lógica adicional aqui se necessário,
                // por exemplo, para processar a resposta do servidor
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

