package org.treblereel.gwt.three4g.demo.client.local.examples.loaders;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.core.client.GWT;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import org.treblereel.gwt.three4g.cameras.PerspectiveCamera;
import org.treblereel.gwt.three4g.core.JsObject;
import org.treblereel.gwt.three4g.core.Object3D;
import org.treblereel.gwt.three4g.demo.client.api.TrackballControls;
import org.treblereel.gwt.three4g.demo.client.local.AppSetup;
import org.treblereel.gwt.three4g.demo.client.local.Attachable;
import org.treblereel.gwt.three4g.demo.client.local.resources.JavascriptTextResource;
import org.treblereel.gwt.three4g.demo.client.local.utils.JSON;
import org.treblereel.gwt.three4g.demo.client.local.utils.StatsProducer;
import org.treblereel.gwt.three4g.examples.loaders.OBJLoader2;
import org.treblereel.gwt.three4g.helpers.GridHelper;
import org.treblereel.gwt.three4g.lights.AmbientLight;
import org.treblereel.gwt.three4g.lights.DirectionalLight;
import org.treblereel.gwt.three4g.loaders.OnLoadCallback;
import org.treblereel.gwt.three4g.materials.Material;
import org.treblereel.gwt.three4g.math.Color;
import org.treblereel.gwt.three4g.math.Vector2;
import org.treblereel.gwt.three4g.math.Vector3;
import org.treblereel.gwt.three4g.renderers.WebGLRenderer;
import org.treblereel.gwt.three4g.renderers.parameters.WebGLRendererParameters;
import org.treblereel.gwt.three4g.scenes.Scene;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel on 6/10/18.
 */
public class WebglLoaderObj2 extends Attachable {

    public static final String name = "loader / obj2";
    private Vector2 mouse = new Vector2();
    private HTMLDivElement panel = (HTMLDivElement) DomGlobal.document.getElementById("panel");
    private Vector3 cameraTarget = new Vector3(0, 0, 0);

    public WebglLoaderObj2() {

        loadJavaScript(JavascriptTextResource.IMPL.getTrackballControls().getText());

        initGL();
        initContent();

        container.appendChild(renderer.domElement);

    }

    private void initGL() {

        WebGLRendererParameters rendererParameters = new WebGLRendererParameters();
        rendererParameters.antialias = true;

        renderer = new WebGLRenderer(rendererParameters);
        renderer.setSize(window.innerWidth, window.innerHeight);
        renderer.gammaInput = true;
        renderer.autoClear = true;
        renderer.setClearColor(new Color(0x050505));

        container.appendChild(renderer.domElement);

        scene = new Scene();
        camera = new PerspectiveCamera(45, aspect, 0.1f, 10000);

        this.resetCamera();

        trackballControls = new TrackballControls(camera);
        AmbientLight ambientLight = new AmbientLight(0x404040);
        DirectionalLight directionalLight1 = new DirectionalLight(0xC0C090);
        DirectionalLight directionalLight2 = new DirectionalLight(0xC0C090);
        directionalLight1.position.set(-100, -50, 100);
        directionalLight2.position.set(100, 50, -100);
        this.scene.add(directionalLight1);
        this.scene.add(directionalLight2);
        this.scene.add(ambientLight);
        GridHelper helper = new GridHelper(1200, 60, new Color(0xFF4444), new Color(0x404040));
        this.scene.add(helper);
    }

    private void initContent() {

        String modelName = "female02";

        OBJLoader2 objLoader = new OBJLoader2();
        OnLoadCallback callbackOnLoad = new OnLoadCallback<JsObject>() {
            @Override
            public void onLoad(JsObject event) {
                JsObject map = event.getProperty("detail");
                Object3D loaderRootNode = map.getProperty("loaderRootNode");
                scene.add(loaderRootNode);
            }
        };

        OnLoadCallback onLoadMtl = new OnLoadCallback<JsObject>() {

            @Override
            public void onLoad(JsObject object) {
                Material[] materials = object.cast();
                objLoader.setModelName(modelName);
                objLoader.setMaterials(materials);
                objLoader.setLogging(true, true);
                objLoader.load("models/obj/female02/female02.obj", callbackOnLoad, null, null, null, false);
            }
        };
        objLoader.loadMtl("models/obj/female02/female02.mtl", null, onLoadMtl);
    }

    private void resetCamera() {
        camera.position.copy(new Vector3(0.0f, 175.0f, 500.0f));
        this.cameraTarget.copy(new Vector3(0, 0, 0));
        this.updateCamera();
    }

    private void updateCamera() {
        camera.aspect = aspect;
        camera.lookAt(this.cameraTarget);
        camera.updateProjectionMatrix();
    }

    @Override
    protected void doAttachScene() {
        root.appendChild(container);
        renderer.setSize(getWidth(), getHeight());
        animate();
    }

    @Override
    protected void doAttachInfo() {
        AppSetup.infoDiv.show().setHrefToInfo("http://threejs.org").setTextContentToInfo("three.js").setInnetHtml(" - OBJLoader2 direct loader test");


    }

    private void animate() {
        AnimationScheduler.get().requestAnimationFrame(timestamp -> {
            if (root.parentNode != null) {
                StatsProducer.getStats().update();
                trackballControls.update();
                renderer.render(scene, camera);
                animate();

            }
        });
    }

}