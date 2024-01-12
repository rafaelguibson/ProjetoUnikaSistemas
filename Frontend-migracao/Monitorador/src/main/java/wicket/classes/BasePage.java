package wicket.classes;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.theme.DefaultTheme;
import org.apache.wicket.markup.html.WebPage;

public class BasePage extends WebPage {

    public BasePage() {
        final ModalWindow modal = new ModalWindow("modal");
        modal.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
        modal.setInitialHeight(600);
        modal.setInitialWidth(950);
        modal.setResizable(false);
        modal.setMarkupId("idDaModalWindow ");
        modal.add(AttributeAppender.append("class", "custom-1"));
        modal.setCssClassName("style");
        modal.setTitle("");
        modal.showUnloadConfirmation(false);
        modal.add(new DefaultTheme());
        add(modal);

        AjaxLink<Void> cadastrarPF = new AjaxLink<Void>("cadastrarPF") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                CadastroPF cadastroPF = new CadastroPF(modal.getContentId());
                System.out.println(modal.getCssClassName());
                modal.setContent(cadastroPF);
                modal.show(target);
            }
        };
        add(cadastrarPF);

        AjaxLink<Void> cadastrarPJ = new AjaxLink<Void>("cadastrarPJ") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                CadastroPJ cadastroPJ = new CadastroPJ(modal.getContentId());
                modal.setTitle("Cadastro Pessoa Física");
                modal.setContent(cadastroPJ);
                modal.show(target);
            }
        };
        add(cadastrarPJ);

    }
}
