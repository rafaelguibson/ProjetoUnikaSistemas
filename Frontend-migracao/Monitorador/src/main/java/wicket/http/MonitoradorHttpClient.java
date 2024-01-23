package wicket.http;

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
import java.text.SimpleDateFormat;
import java.util.Date;
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
                    throw new RuntimeException("Erro ao cadastrar Monitordor"); // Lança uma exceção com a mensagem de erro
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


}

