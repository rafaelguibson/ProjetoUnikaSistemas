package frontend.Masks;


import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

public class MaskBehavior extends Behavior {
    private String mask;

    public MaskBehavior(String mask) {
        this.mask = mask;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(OnDomReadyHeaderItem.forScript(
                "$('#" + component.getMarkupId() + "').mask('" + mask + "');"
        ));
        response.render(JavaScriptHeaderItem.forUrl("mascaras.js"));
    }

    @Override
    public void bind(Component component) {
        component.setOutputMarkupId(true);
    }
}