package org.treblereel.gwt.three4g.demo.client.local.mvc.presenter;

import com.google.gwt.core.client.GWT;
import elemental2.dom.HTMLDivElement;
import org.treblereel.gwt.three4g.demo.client.local.Attachable;
import org.treblereel.gwt.three4g.demo.client.local.examples.animation.WebGlAnimationKeyframesJson;

/**
 * @author Dmitrii Tikhomirov <chani@me.com>
 * Created by treblereel on 5/31/18.
 */
public class WebGlAnimationKeyframesJsonPresenter implements Presenter{

    @Override
    public void dispatch(HTMLDivElement container) {
        container.appendChild(((Attachable)GWT.create(WebGlAnimationKeyframesJson.class)).asWidget());
    }
}
