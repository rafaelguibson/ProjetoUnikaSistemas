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
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
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

public class MonitoradorPJ extends BasePage implements Serializable {
    MonitoradorHttpClient monitoradorHttpClient = new MonitoradorHttpClient("http://localhost:8080/api/monitoradores");
    Monitorador monitorador = new Monitorador();
    List<Monitorador> mntList;
    FeedbackPanel fp;
    private ModalWindow modal  = new ModalWindow("modal");
    final Class<? extends Page> currentPageClass = this.getPage().getClass();
    public MonitoradorPJ(final PageParameters parameters) {
        super(parameters);

        mntList = monitoradorHttpClient.listarTodos();

        /* declaração do feedback panel para notificações */
        fp = new FeedbackPanel("feedbackPanel");
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        WebMarkupContainer sectionForm = new WebMarkupContainer("sectionForm");
        sectionForm.setOutputMarkupId(true);
        add(sectionForm);

        Form<Void> form = new Form<>("form");
        form.setOutputMarkupId(true);
        sectionForm.add(form);

        WebMarkupContainer sectionFilters = new WebMarkupContainer("sectionFilters");
        sectionFilters.setOutputMarkupId(true);
        sectionFilters.setVisible(false);
        form.add(sectionFilters);

        //Definições da janela modal de cadastrar pessoa Física
        modal.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
        modal.setInitialHeight(600);
        modal.setInitialWidth(1150);
        modal.setResizable(false);
//      modal.setMarkupId("idDaModalWindow ");
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
                    CadastroPJ cadastroPJ = new CadastroPJ(modal.getContentId());
                    modal.setTitle("Cadastro Pessoa Jurídica");
                    modal.setContent(cadastroPJ);
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
        modal.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            @Override
            public void onClose(AjaxRequestTarget target) {
                mntList.clear();
                mntList.addAll(monitoradorHttpClient.listarTodos());
                target.add(form);
                setResponsePage(currentPageClass);
            }
        });
        add(modal);

        //Verificação de carregamento do section filter na tela de busca
        add(new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                if ("true".equals(parameters.get("showFilter").toString(""))) {
                    sectionFilters.setVisible(true);
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

        // Filtrar a lista para listar apenas os monitoradores com tipoPessoa igual a "PJ"
        //TODO - ajustar lista para o backend chamando a lista já filtrada
        List<Monitorador> mntListPJ = monitoradorHttpClient.listarPJ();
        ListView<Monitorador> monitoradorList = new ListView<Monitorador>("monitoradorList", mntListPJ) {
            @Override
            protected void populateItem(ListItem<Monitorador> item) {
                Monitorador monitorador = item.getModelObject();



                // Coluna do chebox para selecionar os monitoradores para deletar
                item.add(new CheckBox("selected", new PropertyModel<>(item.getModel(), "selected")));
                item.add(new org.apache.wicket.markup.html.basic.Label("id", new PropertyModel<String>(item.getModel(), "id")));
                String tipoPessoa = monitorador.getTipoPessoa().equals(TipoPessoa.PF) ? "Física" : "Jurídica";
                item.add(new Label("tipoPessoa", tipoPessoa));
                item.add(new org.apache.wicket.markup.html.basic.Label("razaoSocial", new PropertyModel<String>(item.getModel(), "razaoSocial")));
                item.add(new org.apache.wicket.markup.html.basic.Label("cnpj", new PropertyModel<String>(item.getModel(), "cnpj")));
                item.add(new org.apache.wicket.markup.html.basic.Label("telefone", new PropertyModel<String>(item.getModel(), "telefone")));
                item.add(new org.apache.wicket.markup.html.basic.Label("email", new PropertyModel<String>(item.getModel(), "email")));
                item.add(new org.apache.wicket.markup.html.basic.Label("inscricaoEstadual", new PropertyModel<String>(item.getModel(), "inscricaoEstadual")));
                String status = monitorador.getStatus() == Status.ATIVO ? "Ativo" : "Inativo";
                item.add(new Label("ativo", status));
            }
        };
        monitoradorList.setReuseItems(true);
        form.add(monitoradorList);

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

        AjaxLink<Void> btnRemove = new AjaxLink<Void>("btnRemove") {
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
                    //Adiciona uma mensagem de feedback para ser exibida ao remover e atualizar tabela
                    PageParameters pageParameters = new PageParameters();
                    pageParameters.add("message", "Foram removidos (" +monitoradorRemove.size() +") monitoradores...");
                    setResponsePage(currentPageClass, pageParameters);
                }

            }
        };
        btnRemove.add(new AjaxFormSubmitBehavior(form, "click") {});
        add(btnRemove);

        // Campos do Formulário de busca

        TextField<String> razaoSocialFilter = new TextField<String>("razaoSocialFilter");
        razaoSocialFilter.setOutputMarkupId(true);
        TextField<String> cnpjFilter = new TextField<String>("cnpjFilter");
        cnpjFilter.setOutputMarkupId(true);
        TextField<String> inscricaoEstadual = new TextField<String>("inscricaoEstadual");
        inscricaoEstadual.setOutputMarkupId(true);
        List<String> listaDeStatus = Arrays.asList("Ativado", "Desativado");
        DropDownChoice<String> statusFilter = new DropDownChoice<>("statusFilter", Model.ofList(listaDeStatus));
        statusFilter.setOutputMarkupId(true);
        sectionFilters.add(razaoSocialFilter,cnpjFilter,inscricaoEstadual,statusFilter);

        String message = parameters.get("message").toString("");
        if (!message.isEmpty()) {
            info(message);
        }
    }
    private void showInfo(AjaxRequestTarget target, String msg) {
        info(msg);
        target.add(fp);
    }
}
