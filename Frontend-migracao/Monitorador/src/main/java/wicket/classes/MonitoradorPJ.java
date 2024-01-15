package wicket.classes;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.theme.DefaultTheme;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import wicket.entities.Monitorador;
import wicket.http.MonitoradorHttpClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MonitoradorPJ extends BasePage implements Serializable {
    MonitoradorHttpClient monitoradorHttpClient = new MonitoradorHttpClient("http://localhost:8080/api/monitoradores");
    List<Monitorador> mntList;
    private ModalWindow modal  = new ModalWindow("modal");
    public MonitoradorPJ(final PageParameters parameters) {
        super(parameters);

        mntList = monitoradorHttpClient.listarTodos();


        Form<Void> formSearch = new Form<>("formSearch");
        formSearch.setOutputMarkupId(true);
        add(formSearch);

        WebMarkupContainer sectionFilters = new WebMarkupContainer("sectionFilters");
        sectionFilters.setOutputMarkupId(true);
        sectionFilters.setVisible(false);
        formSearch.add(sectionFilters);

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
        add(modal);

        //Verificação de carregamento do section filter na tela de busca
        add(new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                if ("true".equals(parameters.get("showFilter").toString(""))) {
                    sectionFilters.setVisible(true);
                    target.add(sectionFilters);
                    target.add(formSearch);
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



        /* declaração do feedback panel para notificações */
        FeedbackPanel feedbackPanel = new FeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupPlaceholderTag(true);
        feedbackPanel.setVisible(false);
        add(feedbackPanel);

        // Filtrar a lista para listar apenas os monitoradores com tipoPessoa igual a "PJ"
        //TODO - ajustar lista para o backend chamando a lista já filtrada
        List<Monitorador> mntListPJ = new ArrayList<>();
        for (Monitorador monitorador : mntList) {
            if ("PJ".equals(monitorador.getTipoPessoa())) {
                mntListPJ.add(monitorador);
            }
        }
        ListView<Monitorador> monitoradorList = new ListView<Monitorador>("monitoradorList", mntListPJ) {
            @Override
            protected void populateItem(ListItem<Monitorador> item) {
                Monitorador monitorador = item.getModelObject();



                // Coluna do chebox para selecionar os monitoradores para deletar

                item.add(new org.apache.wicket.markup.html.basic.Label("id", new PropertyModel<String>(item.getModel(), "id")));
                String tipoPessoa = monitorador.getTipoPessoa().equals("PF") ? "Física" : "Jurídica";
                item.add(new Label("tipoPessoa", tipoPessoa));
                item.add(new org.apache.wicket.markup.html.basic.Label("razaoSocial", new PropertyModel<String>(item.getModel(), "razaoSocial")));
                item.add(new org.apache.wicket.markup.html.basic.Label("cnpj", new PropertyModel<String>(item.getModel(), "cnpj")));
                item.add(new org.apache.wicket.markup.html.basic.Label("telefone", new PropertyModel<String>(item.getModel(), "telefone")));
                item.add(new org.apache.wicket.markup.html.basic.Label("email", new PropertyModel<String>(item.getModel(), "email")));
                item.add(new org.apache.wicket.markup.html.basic.Label("inscricaoEstadual", new PropertyModel<String>(item.getModel(), "inscricaoEstadual")));
                String status = monitorador.getAtivo() ? "Ativo" : "Inativo";
                item.add(new Label("ativo", status));
            }
        };
        monitoradorList.setReuseItems(true);
        formSearch.add(monitoradorList);

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
    }
}
