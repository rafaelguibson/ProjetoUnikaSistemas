package wicket.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Endereco implements Serializable {
    private Long id;
    private Long monitoradorId;
    private String logradouro;
    private String numero;
    private String cep;
    private String bairro;
    private String complemento;

    @JsonProperty("localidade")
    private String cidade;

    @JsonProperty("uf")
    private String estado;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endereco endereco = (Endereco) o;
        return Objects.equals(id, endereco.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Endereco{" +
                "id=" + id +
                ", monitoradorId=" + monitoradorId +
                ", logradouro='" + logradouro + '\'' +
                ", numero='" + numero + '\'' +
                ", cep='" + cep + '\'' +
                ", bairro='" + bairro + '\'' +
                ", cidade='" + cidade + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
    public static Endereco buscarCep(String cep) {
        // Remove caracteres não numéricos
        cep = cep.replaceAll("\\D", "");

        // Verifica se o CEP possui 8 dígitos
        if (cep.matches("\\d{8}")) {
            // URL da API do ViaCEP
            String url = "https://viacep.com.br/ws/" + cep + "/json/";

            try {
                // Realiza a requisição e processa a resposta
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Converte a resposta para um objeto Endereco
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(response.body(), Endereco.class);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null; // Retorna null em caso de erro ou CEP inválido
    }
}
