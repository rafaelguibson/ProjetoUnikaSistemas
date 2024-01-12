package wicket.classes;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;

public class BasePage extends WebPage {

    public BasePage() {
        final ModalWindow modal = new ModalWindow("modal");
        modal.setInitialHeight(600);
        modal.setInitialWidth(950);
        modal.setTitle("");
        add(modal);

        AjaxLink<Void> cadastrarPF = new AjaxLink<Void>("cadastrarPF") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                CadastroPF cadastroPF = new CadastroPF(modal.getContentId());
                modal.setContent(cadastroPF);
                modal.show(target);
            }
        };
        add(cadastrarPF);

        AjaxLink<Void> cadastrarPJ = new AjaxLink<Void>("cadastrarPJ") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                CadastroPJ cadastroPJ = new CadastroPJ(modal.getContentId());
                modal.setContent(cadastroPJ);
                modal.show(target);
            }
        };
        add(cadastrarPJ);

    }
}
