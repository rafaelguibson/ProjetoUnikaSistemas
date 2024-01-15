package wicket.classes;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class BasePage extends WebPage {

    public BasePage(PageParameters parameters) {
        super(parameters);

        add(new BookmarkablePageLink<>("homePage", HomePage.class));
        add(new BookmarkablePageLink<>("monitoradorPF", MonitoradorPF.class));
        add(new BookmarkablePageLink<>("cadastrarPF", MonitoradorPF.class, new PageParameters().add("openModal", "true")));
        add(new BookmarkablePageLink<>("monitoradorPJ", MonitoradorPJ.class));
        add(new BookmarkablePageLink<>("cadastrarPJ", MonitoradorPJ.class, new PageParameters().add("openModal", "true")));

    }
}
