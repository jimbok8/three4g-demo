package org.treblereel.gwt.three4g.demo.client.local.mvc.presenter;

import com.google.gwt.core.client.GWT;
import elemental2.dom.HTMLDivElement;
import org.treblereel.gwt.three4g.demo.client.local.Attachable;
import org.treblereel.gwt.three4g.demo.client.local.examples.loaders.WebglLoaderJsonBlender;

/**
 * @author Dmitrii Tikhomirov <chani@me.com>
 * Created by treblereel on 6/8/18.
 */
public class WebglLoaderJsonBlenderPresenter implements Presenter {
    @Override
    public void dispatch(HTMLDivElement container) {
        container.appendChild(((Attachable) GWT.create(WebglLoaderJsonBlender.class)).asWidget());
    }
}