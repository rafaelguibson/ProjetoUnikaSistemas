package wicket.classes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import wicket.entities.Endereco;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CadastroPJ  extends Panel implements Serializable {
    private static final long serialVersionUID = 1L;

    public CadastroPJ(String id) {
        super(id);


        Form<Void> form = new Form<>("form");
        add(form);
        TextField<String> cep = new TextField<>("cep", Model.of(""));
        TextField<String> logradouro = new TextField<>("logradouro", Model.of(""));
        TextField<String> numero = new TextField<>("numero", Model.of(""));
        TextField<String> bairro = new TextField<>("bairro", Model.of(""));
        TextField<String> cidade = new TextField<>("cidade", Model.of(""));
        TextField<String> estado = new TextField<>("estado", Model.of(""));
        cep.setOutputMarkupId(true);
        logradouro.setOutputMarkupId(true);
        numero.setOutputMarkupId(true);
        bairro.setOutputMarkupId(true);
        cidade.setOutputMarkupId(true);
        estado.setOutputMarkupId(true);

        form.add(cep, logradouro, numero, bairro, cidade, estado);


        AjaxButton pesquisarCepBtn = new AjaxButton("pesquisarCepBtn") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                String cepValue = cep.getModelObject();
                Endereco endereco = buscarCep(cepValue); // Implemente esta função
                System.out.println(endereco.getBairro().toString());
                logradouro.setModelObject(endereco.getLogradouro());
                bairro.setModelObject(endereco.getBairro());
                cidade.setModelObject(endereco.getLocalidade());
                estado.setModelObject(endereco.getEstado());

                // Atualiza os campos no formulário
                target.add(logradouro, bairro, cidade, estado);
            }
        };

        form.add(pesquisarCepBtn);



    }

    private Endereco buscarCep(String cep) {
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
