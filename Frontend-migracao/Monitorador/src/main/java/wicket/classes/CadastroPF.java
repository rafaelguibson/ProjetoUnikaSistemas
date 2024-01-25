package wicket.classes;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import wicket.configs.MaskBehavior;
import wicket.entities.Endereco;
import wicket.entities.Monitorador;
import wicket.enums.Estado;
import wicket.enums.Status;
import wicket.enums.TipoPessoa;
import wicket.http.MonitoradorHttpClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.closeCurrent;
import static wicket.entities.Endereco.buscarCep;

public class CadastroPF  extends Panel implements Serializable {
    private static final long serialVersionUID = 1L;

    FeedbackPanel fp;
    List<Endereco> listaDeEnderecos = new ArrayList<>();
    WebMarkupContainer formAddress = new WebMarkupContainer("formAddress");
    WebMarkupContainer tableAddress = new WebMarkupContainer("tableAddress");
    Endereco endereco = new Endereco();

    public CadastroPF(String id) {
        super(id);

        MonitoradorHttpClient monitoradorHttpClient = new MonitoradorHttpClient("http://localhost:8080/api/monitoradores");

        setOutputMarkupId(true);
        fp = new FeedbackPanel("feedbackPanel");
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

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
//        cpf.add(new MaskBehavior("000.000.000-00"));
        TextField<String> telefone = new TextField<String>("telefone");
        TextField<String> email = new TextField<String>("email");
        TextField<String> rg = new TextField<String>("rg");
        DateTextField dataNascimento = new DateTextField("dataNascimento", "yyyy-MM-dd");
        List<String> estadosCivis = Arrays.asList("Solteiro(a)", "Casado(a)", "Divorciado(a)", "Viuvo(a)");
        DropDownChoice<String> estadoCivilDropDown = new DropDownChoice<>("estadoCivil",
                new PropertyModel<>(this, "selectedEstadoCivil"), estadosCivis);

        //estadoCivilDropDown.setRequired(true); // torna o campo obrigatório
        // Define as opções do dropdown
        List<String> statusOptions = Arrays.asList("Ativado", "Desativado");

        // Cria um modelo para armazenar o valor selecionado
        PropertyModel<String> listaStatus = new PropertyModel<>(this, "selectedStatus");

        // Cria o dropdown e o adiciona à página
        DropDownChoice<String> statusDropDown = new DropDownChoice<>("status", listaStatus, statusOptions);



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
                item.add(new Label("estado", Model.of(endereco.getEstado())));

            }
        };
        tableAddress.add(enderecoList);

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
        cep.setOutputMarkupId(true);
        TextField<String> logradouro = new TextField<String>("logradouro");
        logradouro.setOutputMarkupId(true);
        TextField<String> numero = new TextField<String>("numero");
        numero.setOutputMarkupId(true);
        TextField<String> bairro = new TextField<String>("bairro");
        bairro.setOutputMarkupId(true);
        TextField<String> cidade = new TextField<String>("cidade");
        cidade.setOutputMarkupId(true);
        TextField<String> estado = new TextField<String>("estado");
        estado.setOutputMarkupId(true);

        formAddress.add(cep,logradouro, numero, bairro, cidade, estado);

        AjaxLink<Void> saveMonitorador = new AjaxLink<Void>("saveMonitorador") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Monitorador monitoradorSalvar = new Monitorador();
                monitoradorSalvar.setTipoPessoa(TipoPessoa.PF);
                monitoradorSalvar.setNome(nome.getValue());
                monitoradorSalvar.setCpf(cpf.getValue());
                monitoradorSalvar.setTelefone(telefone.getValue());
                monitoradorSalvar.setEmail(email.getValue());
                monitoradorSalvar.setRg(rg.getValue());
                monitoradorSalvar.setDataNascimento(dataNascimento.getModelObject());
                monitoradorSalvar.setStatus(Status.ATIVO);
                monitoradorSalvar.setEnderecos(listaDeEnderecos);
                try {
                    monitoradorHttpClient.salvar(monitoradorSalvar);
                    info("Cadastro realizado com sucesso.");
                } catch (RuntimeException e) {
                    error(e.getMessage()); // Exibe a mensagem de erro
                }
                target.add(fp); // Atualiza o FeedbackPanel na página
            }
        };
        saveMonitorador.add(new AjaxFormSubmitBehavior(form,"click") {});
        form.add(saveMonitorador);
        //Botão para pesquisar cep via correios
        AjaxLink<Void> searchCEP  = new AjaxLink<Void>("searchCEP") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                String cepValue = cep.getValue();
                if (cepValue == null || cepValue.trim().isEmpty()) {
                    error("Favor digitar um número de CEP.");
                    target.add(fp);
                } else {
                    Endereco endereco = null;
                    try {
                        endereco = buscarCep(cepValue);
                        // Defina os valores dos campos com base no resultado da pesquisa
                        logradouro.setModelObject(endereco.getLogradouro());
                        bairro.setModelObject(endereco.getBairro());
                        cidade.setModelObject(endereco.getCidade());
                        estado.setModelObject(endereco.getEstado().toString());
                    } catch (IllegalArgumentException e) {
                        error(e.getMessage());
                        cep.setModelObject("");
                        target.add(formAddress);
                    } catch (Endereco.CepNaoEncontradoException e) {
                        error("CEP não encontrado.");
                    } catch (Exception e) {
                        error("Erro ao buscar CEP: " + e.getMessage()); // Captura outras exceções
                    }
                    target.add(fp); // Atualiza o FeedbackPanel na página
                    target.add(logradouro, bairro, numero, cidade, estado); // Atualiza os campos no formulário

                }
                }

        };
        searchCEP.add(new AjaxFormSubmitBehavior(form,"click") {});
        formAddress.add(searchCEP);

        //Botão de salvar endereço adicionando ele a lista e atualizando a tableAddress
        AjaxLink<Void> saveAddress = new AjaxLink<Void>("saveAddress") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                endereco.setCep(cep.getValue());
                endereco.setNumero(numero.getValue());
                endereco.setLogradouro(logradouro.getValue());
                endereco.setCidade(cidade.getValue());
                endereco.setBairro(bairro.getValue());
                endereco.setEstado(Estado.valueOf(estado.getValue()));
                listaDeEnderecos.add(endereco);
                formAddress.setVisible(false);;
                target.add(formAddress, tableAddress);
            }
        };
        saveAddress.add(new AjaxFormSubmitBehavior(form,"click") {});
        formAddress.add(saveAddress);

        AjaxLink<Void> cancelAddress = new AjaxLink<Void>("cancelAddress") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                formAddress.setVisible(false);
                target.add(formAddress, tableAddress);
            }
        };
        cancelAddress.add(new AjaxFormSubmitBehavior(form,"click") {});
        formAddress.add(cancelAddress);

        //Botão que fecha o modal
        AjaxLink<Void> cancelButton = new AjaxLink<Void>("cancelButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                // Fecha a janela modal
                closeCurrent(target);
                //Atualiza a tabla de
                target.add(tableAddress);
            }

        };
        form.add(cancelButton);

    }
    private void showInfo(AjaxRequestTarget target, String msg) {
        info(msg);
        target.add(fp);
    }

}
