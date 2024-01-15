package wicket.classes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.CssPackageResource;
import wicket.entities.Endereco;
import wicket.entities.Monitorador;
import wicket.http.MonitoradorHttpClient;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CadastroPF extends Panel implements Serializable {
    private static final long serialVersionUID = 1L;
    private String selectedEstadoCivil;
    private String selectedStatus;
    List<Endereco> listaDeEnderecos = new ArrayList<>();
    WebMarkupContainer formAddress = new WebMarkupContainer("formAddress");
    WebMarkupContainer tableAddress = new WebMarkupContainer("tableAddress");
    Endereco endereco = new Endereco();

    public CadastroPF(String id) {
        super(id);
        setOutputMarkupId(true);

        //Formulário que contem os dados de cadastro monitorador, o botão de adicionar endereço e a tableAddress
        Form<Void> form = new Form<>("form");
        add(form);

        //FormNew
        formAddress.setOutputMarkupPlaceholderTag(true);
        formAddress.setVisible(false);
        tableAddress.setOutputMarkupPlaceholderTag(true);
        form.add(formAddress, tableAddress);

        Monitorador monitorador = new Monitorador();
        form.setDefaultModel(new CompoundPropertyModel<>(monitorador));

        TextField<String> nome = new TextField<String>("nome");
        TextField<String> cpf = new TextField<String>("cpf");
        TextField<String> telefone = new TextField<String>("telefone");
        TextField<String> email = new TextField<String>("email");
        TextField<String> rg = new TextField<String>("rg");
        DateTextField dataNascimento = new DateTextField("dataNascimento", "yyyy-MM-dd");
        List<String> estadosCivis = Arrays.asList("Solteiro(a)", "Casado(a)", "Divorciado(a)", "Viuvo(a)");
        DropDownChoice<String> estadoCivilDropDown = new DropDownChoice<>("estadoCivil",
                new PropertyModel<>(this, "selectedEstadoCivil"), estadosCivis);

        estadoCivilDropDown.setRequired(true); // torna o campo obrigatório
        // Define as opções do dropdown
        List<String> statusOptions = Arrays.asList("Ativado", "Desativado");

        // Cria um modelo para armazenar o valor selecionado
        PropertyModel<String> listaStatus = new PropertyModel<>(this, "selectedStatus");

        // Cria o dropdown e o adiciona à página
        DropDownChoice<String> statusDropDown = new DropDownChoice<>("status", listaStatus, statusOptions);

        // Crie um componente ListView para exibir a tabela

        Endereco endereco1 = new Endereco();
        endereco1.setCep("12345-678");
        endereco1.setLogradouro("Rua A");
        endereco1.setNumero("123");
        endereco1.setBairro("Bairro 1");
        endereco1.setCidade("Cidade 1");
        endereco1.setUf("Estado 1");
        listaDeEnderecos.add(endereco1);

        Endereco endereco2 = new Endereco();
        endereco2.setCep("98765-432");
        endereco2.setLogradouro("Rua B");
        endereco2.setNumero("456");
        endereco2.setBairro("Bairro 2");
        endereco2.setCidade("Cidade 2");
        endereco2.setUf("Estado 2");
        listaDeEnderecos.add(endereco2);

        ListView<Endereco> enderecoList = new ListView<Endereco>("enderecoList", listaDeEnderecos) {
            @Override
            protected void populateItem(ListItem<Endereco> item) {
                Endereco endereco = item.getModelObject();

                // Adicione as colunas à linha da tabela
                item.add(new Label("cep", Model.of(endereco.getCep())));
                item.add(new Label("logradouro", Model.of(endereco.getLogradouro())));
                item.add(new Label("numero", Model.of(endereco.getNumero())));
                item.add(new Label("bairro", Model.of(endereco.getBairro())));
                item.add(new Label("cidade", Model.of(endereco.getCidade())));
                item.add(new Label("estado", Model.of(endereco.getUf())));

            }
        };
        tableAddress.add(enderecoList);

        //Botão que chama o evento Ajax para exibir o formulario de cadastrar endereço
        AjaxLink<Void> addAddress = new AjaxLink<>("addAddress") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                formAddress.setVisible(!formAddress.isVisible());
                target.add(formAddress);
            }
        };
        addAddress.add(new AjaxFormSubmitBehavior(form,"click") {});
        form.add(nome,cpf,telefone,email,rg,dataNascimento,estadoCivilDropDown, statusDropDown, addAddress);

        formAddress.setDefaultModel(new CompoundPropertyModel<>(endereco));

        TextField<String> cep = new TextField<String>("cep");
        TextField<String> logradouro = new TextField<String>("logradouro");
        TextField<String> numero = new TextField<String>("numero");
        TextField<String> bairro = new TextField<String>("bairro");
        TextField<String> cidade = new TextField<String>("cidade");
        TextField<String> uf = new TextField<String>("uf");

        formAddress.add(cep,logradouro, numero, bairro, cidade, uf);


        //Botão de salvar endereço adicionando ele a lista e atualizando a tableAddress
        AjaxLink<Void> saveAddress = new AjaxLink<Void>("saveAddress") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                endereco.setCep(cep.getValue());
                endereco.setNumero(numero.getValue());
                endereco.setLogradouro(logradouro.getValue());
                endereco.setCidade(cidade.getValue());
                endereco.setBairro(bairro.getValue());
                endereco.setUf(uf.getValue());
                listaDeEnderecos.add(endereco);
                formAddress.setVisible(false);;
                target.add(formAddress, tableAddress);
            }
        };
        saveAddress.add(new AjaxFormSubmitBehavior(form,"click") {});
        formAddress.add(saveAddress);

        //Botão que fecha o modal
        AjaxLink<Void> cancelButton = new AjaxLink<Void>("cancelButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                // Fecha a janela modal (assumindo que você tem uma referência para ela)
                ModalWindow.closeCurrent(target);

                // Supondo que a tableAddress esteja no escopo correto e precisa ser atualizada
                target.add(tableAddress);
            }

        };
        form.add(cancelButton);

    }


}

