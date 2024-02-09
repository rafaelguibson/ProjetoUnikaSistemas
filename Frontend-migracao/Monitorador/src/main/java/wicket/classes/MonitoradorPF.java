package wicket.classes;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.theme.DefaultTheme;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import wicket.entities.Monitorador;
import wicket.enums.Status;
import wicket.enums.TipoPessoa;
import wicket.http.MonitoradorHttpClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MonitoradorPF extends BasePage implements Serializable {
    MonitoradorHttpClient monitoradorHttpClient = new MonitoradorHttpClient("http://localhost:8080/api/monitoradores");
    Monitorador monitorador = new Monitorador();
    List<Monitorador> mntList;
    private ModalWindow modal = new ModalWindow("modal");
    FeedbackPanel fp;
    static int itemsPerPage = 5;
    final Class<? extends Page> currentPageClass = this.getPage().getClass();

    public MonitoradorPF(final PageParameters parameters) {
        super(parameters);

        /* declaração do feedback panel para notificações */
        fp = new FeedbackPanel("feedbackPanel");
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);


        WebMarkupContainer sectionForm = new WebMarkupContainer("sectionForm");
        sectionForm.setOutputMarkupId(true);
        add(sectionForm);



        Form<Monitorador> form = new Form<>("form");
        //form.setDefaultModel(new CompoundPropertyModel<>(monitorador));
        form.setOutputMarkupId(true);
        sectionForm.add(form);

        Form<Monitorador> sectionFilters = new Form<>("sectionFilters");
        sectionFilters.setDefaultModel(new CompoundPropertyModel<>(monitorador));
        sectionFilters.setOutputMarkupId(true);
        sectionFilters.setVisible(false);
        form.add(sectionFilters);

        //Definições da janela modal de cadastrar pessoa Física
        modal.setInitialHeight(600);
        modal.setInitialWidth(1150);
        modal.setResizable(false);
        modal.add(AttributeAppender.append("class", "custom-1"));
        modal.setCssClassName("style");
        modal.setTitle("");
        modal.showUnloadConfirmation(false);
        modal.add(new DefaultTheme());

        //Método que valida se o modal deve ser chamado no carregamento da página
        //quando usuário clica no botão de cadastrar
        modal.add(new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                if ("true".equals(parameters.get("openModal").toString(""))) {
                    CadastroPF cadastroPF = new CadastroPF(modal.getContentId());
                    modal.setTitle("Cadastro Pessoa Física");
                    modal.setContent(cadastroPF);
                    modal.show(target);
                }
            }

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                if ("true".equals(parameters.get("openModal").toString("")) && !modal.isShown()) {
                    response.render(OnDomReadyHeaderItem.forScript(getCallbackScript()));
                }
            }
        });
        modal.setOutputMarkupId(true);
        modal.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            @Override
            public void onClose(AjaxRequestTarget target) {
                mntList.clear();
                mntList.addAll(monitoradorHttpClient.listarTodos());
                target.add(form);
                PageParameters pageParameters = new PageParameters();
                pageParameters.add("message", "Operação realizada com sucesso");

                setResponsePage(currentPageClass, pageParameters);

            }
        });
        add(modal);
        AjaxLink<Void> btnRemove = getComponents();
        btnRemove.add(new AjaxFormSubmitBehavior(form, "click") {});
        form.add(btnRemove);

        //Verificação de carregamento do section filter na tela de busca
        add(new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                if ("true".equals(parameters.get("showFilter").toString(""))) {
                    sectionFilters.setVisible(true);
                    btnRemove.setVisible(false);
                    target.add(sectionFilters);
                    target.add(form);
                }
            }

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                if ("true".equals(parameters.get("showFilter").toString("")) && !sectionFilters.isVisible()) {
                    response.render(OnDomReadyHeaderItem.forScript(getCallbackScript()));
                }
            }
        });

        mntList = monitoradorHttpClient.listarPF();

        PageableListView<Monitorador> monitoradorPageableList = getMonitoradorPageableList(mntList);
        form.add(monitoradorPageableList);

        form.add(new PagingNavigator("pagingNavigator", monitoradorPageableList));


        AjaxLink<Void> checkBox = new AjaxLink<>("checkBox", new PropertyModel<>(new CompoundPropertyModel<>(monitorador), "selected")) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                for (Monitorador monitorador : mntList) {
                    if(!monitorador.isSelected()) {
                        monitorador.setSelected(true);
                        target.add(sectionForm);
                    } else {
                        monitorador.setSelected(false);
                        target.add(sectionForm);
                    }

                }

            }
        };
        form.add(checkBox);




        // Campos do Formulário de busca
        TextField<String> nome = new TextField<>("nome");
        nome.setOutputMarkupId(true);
        TextField<String> cpf = new TextField<>("cpf");
        cpf.setOutputMarkupId(true);
        TextField<String> rg = new TextField<>("rg");
        rg.setOutputMarkupId(true);
        DateTextField dataNascimento = new DateTextField("dataNascimento", "yyyy-MM-dd");
        dataNascimento.setOutputMarkupId(true);
        DateTextField dataInicial = new DateTextField("dataInicial", "yyyy-MM-dd");
        dataInicial.setOutputMarkupId(true);
        DateTextField dataFinal = new DateTextField("dataFinal","yyyy-MM-dd");
        dataFinal.setOutputMarkupId(true);

        List<Status> listaDeStatus = Arrays.asList(Status.values());
        DropDownChoice<Status> status = new DropDownChoice<>("status", new Model<Status>(), listaDeStatus, new ChoiceRenderer<>("status", "status"));
        status.setOutputMarkupId(true);

        // Adicione os campos de filtro ao formFilter
        sectionFilters.add(nome, cpf, rg, dataNascimento, status, dataInicial, dataFinal);

        AjaxLink<Void> btnSearch = new AjaxLink<Void>("btnSearch") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Monitorador monitoradorBuscar = new Monitorador();
                monitoradorBuscar.setTipoPessoa(TipoPessoa.PF);
                monitoradorBuscar.setNome(nome.getValue());
                monitoradorBuscar.setCpf(cpf.getValue());
                monitoradorBuscar.setRg(rg.getValue());
                monitoradorBuscar.setDataNascimento(dataNascimento.getModelObject());
                monitoradorBuscar.setDataInicial(dataInicial.getModelObject());
                monitoradorBuscar.setDataFinal(dataFinal.getModelObject());
                monitoradorBuscar.setStatus(status.getModelObject());
                mntList.clear();
                mntList.addAll(monitoradorHttpClient.filtrar(monitoradorBuscar));
                target.add(sectionForm);
            }
        };
        btnSearch.add(new AjaxFormSubmitBehavior(form, "click") {});
        btnSearch.setOutputMarkupId(true);
        sectionFilters.add(btnSearch);

        String message = parameters.get("message").toString("");
        if (!message.isEmpty()) {
            info(message);
        }


    }

    private AjaxLink<Void> getComponents() {
        return new AjaxLink<Void>("btnRemove") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                List<Monitorador> monitoradorRemove = mntList.stream().
                        filter(Monitorador::isSelected).collect(Collectors.toList());

                monitoradorHttpClient.deleteAllMonitoradores(monitoradorRemove);

                mntList.clear();
                mntList.addAll(monitoradorHttpClient.listarTodos());

                if(monitoradorRemove.isEmpty()) {
                    showInfo(target, "Selecione algum registro para remover...");
                } else {
                    PageParameters pageParameters = new PageParameters();
                    pageParameters.add("message", "Foram removidos (" +monitoradorRemove.size() +") monitoradores...");
                    setResponsePage(currentPageClass, pageParameters);

                }

            }
        };
    }

    public PageableListView<Monitorador> getMonitoradorPageableList(List<Monitorador> mntListPF) {
            return new PageableListView<Monitorador>("monitoradorList", mntListPF, itemsPerPage) {
                @Override
                protected void populateItem(ListItem<Monitorador> item) {
                    Monitorador monitorador = item.getModelObject();

                // Coluna do chebox para selecionar os monitoradores para deletar
                item.add(new CheckBox("selected", new PropertyModel<>(item.getModel(), "selected")));
                item.add(new Label("id", new PropertyModel<String>(item.getModel(), "id")));
//                String tipoPessoa = monitorador.getTipoPessoa().equals(TipoPessoa.PF) ? "Física" : "Jurídica";
                item.add(new Label("tipoPessoa", monitorador.getTipoPessoa().getTipoPessoa()));
                item.add(new Label("nome", new PropertyModel<String>(item.getModel(), "nome")));
                item.add(new Label("cpf", new PropertyModel<String>(item.getModel(), "cpf")));
                item.add(new Label("telefone", new PropertyModel<String>(item.getModel(), "telefone")));
                item.add(new Label("email", new PropertyModel<String>(item.getModel(), "email")));
                item.add(new Label("rg", new PropertyModel<String>(item.getModel(), "rg")));
                item.add(new Label("dataNascimento", new PropertyModel<String>(item.getModel(), "dataNascimento")));
                item.add(new Label("ativo", monitorador.getStatus().getStatus()));
            }
        };
    }

    private void showInfo(AjaxRequestTarget target, String msg) {
        info(msg);
        target.add(fp);
    }
}
