package frontend;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.basic.Label;

public class BasePage extends WebPage {

    public BasePage() {
        // Adiciona links para navegação
        add(new BookmarkablePageLink<>("inicioLink", InicioPage.class));
        add(new BookmarkablePageLink<>("cadastrarLink", CadastrarPage.class));
        add(new BookmarkablePageLink<>("pesquisarLink", PesquisarPage.class));
        add(new BookmarkablePageLink<>("contatoLink", ContatoPage.class));
        add(new BookmarkablePageLink<>("sobreLink", SobrePage.class));

    }
}
