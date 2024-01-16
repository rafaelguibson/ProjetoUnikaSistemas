package wicket.classes;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import wicket.entities.Monitorador;
import wicket.http.MonitoradorHttpClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomePage extends BasePage implements Serializable {
    private static final long serialVersionUID = 1L;
    MonitoradorHttpClient monitoradorHttpClient = new MonitoradorHttpClient("http://localhost:8080/api/monitoradores");
    List<Monitorador> mntList;
    FeedbackPanel fp;

    public HomePage(final PageParameters parameters) {
        super(parameters);
        Monitorador monitorador = new Monitorador();

        /* declaração do feedback panel para notificações */


        fp = new FeedbackPanel("feedbackPanel");
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        WebMarkupContainer sectionForm = new WebMarkupContainer("sectionForm");
        sectionForm.setOutputMarkupId(true);
        add(sectionForm);

        Form<Void> form = new Form<>("form");
        sectionForm.add(form);

        sectionForm.setOutputMarkupId(true);
        form.setDefaultModel(new CompoundPropertyModel<>(monitorador));
        /* Lista que é preenchida com os monitoradores do banco de dados */
        mntList = monitoradorHttpClient.listarTodos();

        /* listview que preenche a table com os dados dos monitoradores*/
        ListView<Monitorador> monitoradorList = new ListView<Monitorador>("monitoradorList", mntList) {
            @Override
            protected void populateItem(ListItem<Monitorador> item) {
                Monitorador monitorador = item.getModelObject();

                // Coluna do chebox para selecionar os monitoradores para deletar
                item.add(new CheckBox("selected", new PropertyModel<>(item.getModel(), "selected")));

                item.add(new Label("id", new PropertyModel<String>(item.getModel(), "id")));

                String tipoPessoa = monitorador.getTipoPessoa().equals("PF") ? "Física" : "Jurídica";
                item.add(new Label("tipoPessoa", tipoPessoa));

                // Combina Nome e Razão Social
                String nomeOuRazaoSocial = monitorador.getTipoPessoa().equals("PF") ? monitorador.getNome() : monitorador.getRazaoSocial();
                item.add(new Label("nomeOuRazaoSocial", nomeOuRazaoSocial));

                // Combina CPF e CNPJ
                String cpfOuCnpj = monitorador.getTipoPessoa().equals("PF") ? monitorador.getCpf() : monitorador.getCnpj();
                item.add(new Label("cpfOuCnpj", cpfOuCnpj));

                item.add(new Label("telefone", new PropertyModel<String>(item.getModel(), "telefone")));
                item.add(new Label("email", new PropertyModel<String>(item.getModel(), "email")));
                // Combina RG e Inscrição Estadual
                String rgOuInscricaoEstadual = monitorador.getTipoPessoa().equals("PF") ? monitorador.getRg() : monitorador.getInscricaoEstadual();
                item.add(new Label("rgOuInscricaoEstadual", rgOuInscricaoEstadual));

                item.add(new Label("dataNascimento", new PropertyModel<String>(item.getModel(), "dataNascimento")));

                String status = monitorador.getAtivo() ? "Ativo" : "Inativo";
                item.add(new Label("ativo", status));
            }
        };
        monitoradorList.setReuseItems(true);
        form.add(monitoradorList);


        /* Método que controla o selecionar/deselecionar do checkbox de excluisão */
//        AjaxLink<Void> checkBox = new AjaxLink<>("checkBox", new PropertyModel<>(new CompoundPropertyModel<>(monitorador), "selected")) {
//            @Override
//            public void onClick(AjaxRequestTarget target) {
//                for (Monitorador monitorador : mntList) {
//                    if(!monitorador.isSelected()) {
//                        monitorador.setSelected(true);
//                        target.add(sectionForm);
//                    } else {
//                        monitorador.setSelected(false);
//                        target.add(sectionForm);
//                    }
//
//                }
//
//            }
//        };
//        sectionForm.add(checkBox);

        //Excluir monitorador
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
                    showInfo(target, "Foram removidos ("+monitoradorRemove.size()+") monitoradores...");
                }
                target.add(sectionForm);
            }
        };
        btnRemove.add(new AjaxFormSubmitBehavior(form, "click") {});
        add(btnRemove);
    } //Fora do construtor
    private void showInfo(AjaxRequestTarget target, String msg) {
        info(msg);
        target.add(fp);
    }
}
