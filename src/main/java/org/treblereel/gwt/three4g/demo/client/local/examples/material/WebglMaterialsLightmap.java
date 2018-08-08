package org.treblereel.gwt.three4g.demo.client.local.examples.material;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.core.client.GWT;
import elemental2.dom.HTMLScriptElement;
import jsinterop.base.Js;
import org.treblereel.gwt.three4g.THREE;
import org.treblereel.gwt.three4g.cameras.PerspectiveCamera;
import org.treblereel.gwt.three4g.core.Geometry;
import org.treblereel.gwt.three4g.core.JsObject;
import org.treblereel.gwt.three4g.demo.client.local.AppSetup;
import org.treblereel.gwt.three4g.demo.client.local.Attachable;
import org.treblereel.gwt.three4g.demo.client.local.utils.StatsProducer;
import org.treblereel.gwt.three4g.examples.controls.OrbitControls;
import org.treblereel.gwt.three4g.geometries.SphereBufferGeometry;
import org.treblereel.gwt.three4g.lights.DirectionalLight;
import org.treblereel.gwt.three4g.loaders.JSONLoader;
import org.treblereel.gwt.three4g.loaders.OnLoadCallbackPair;
import org.treblereel.gwt.three4g.materials.Material;
import org.treblereel.gwt.three4g.materials.MeshPhongMaterial;
import org.treblereel.gwt.three4g.materials.ShaderMaterial;
import org.treblereel.gwt.three4g.materials.parameters.ShaderMaterialParameters;
import org.treblereel.gwt.three4g.materials.parameters.Uniforms;
import org.treblereel.gwt.three4g.math.Color;
import org.treblereel.gwt.three4g.objects.Mesh;
import org.treblereel.gwt.three4g.renderers.WebGLRenderer;
import org.treblereel.gwt.three4g.renderers.parameters.WebGLRendererParameters;
import org.treblereel.gwt.three4g.scenes.Scene;
import org.treblereel.gwt.three4g.utils.JSON;

import static elemental2.dom.DomGlobal.document;

/**
 * @author Dmitrii Tikhomirov <chani@me.com>
 * Created by treblereel on 8/1/18.
 */
public class WebglMaterialsLightmap extends Attachable {

    public static final String name = "materials / lightmap";

    private String fragmentShader = "\tuniform vec3 topColor;\n" +
            "\t\t\tuniform vec3 bottomColor;\n" +
            "\t\t\tuniform float offset;\n" +
            "\t\t\tuniform float exponent;\n" +
            "\t\t\tvarying vec3 vWorldPosition;\n" +
            "\t\t\tvoid main() {\n" +
            "\t\t\t\tfloat h = normalize( vWorldPosition + offset ).y;\n" +
            "\t\t\t\tgl_FragColor = vec4( mix( bottomColor, topColor, max( pow( max( h, 0.0 ), exponent ), 0.0 ) ), 1.0 );\n" +
            "\t\t\t}";


    private String vertexShader = "\tvarying vec3 vWorldPosition;\n" +
            "\t\t\tvoid main() {\n" +
            "\t\t\t\tvec4 worldPosition = modelMatrix * vec4( position, 1.0 );\n" +
            "\t\t\t\tvWorldPosition = worldPosition.xyz;\n" +
            "\t\t\t\tgl_Position = projectionMatrix * modelViewMatrix * vec4( position, 1.0 );\n" +
            "\t\t\t}";


    public WebglMaterialsLightmap() {

        camera = new PerspectiveCamera(40, aspect, 1, 10000);
        camera.position.set(700, 200, -500);
        // SCENE
        scene = new Scene();
        // CONTROLS
        orbitControls = new OrbitControls(camera, root);
        orbitControls.maxPolarAngle = (float) (0.9f * Math.PI / 2);
        orbitControls.enableZoom = false;
        // LIGHTS
        DirectionalLight light = new DirectionalLight(0xaabbff, 0.3f);
        light.position.x = 300;
        light.position.y = 250;
        light.position.z = -500;
        scene.add(light);
        // SKYDOME
        HTMLScriptElement fragmentShaderElm = (HTMLScriptElement) document.createElement("script");
        fragmentShaderElm.type = "x-shader/x-fragment";
        fragmentShaderElm.textContent = fragmentShader;
        root.appendChild(fragmentShaderElm);

        HTMLScriptElement vertexShaderElm = (HTMLScriptElement) document.createElement("script");
        vertexShaderElm.type = "x-shader/x-vertex";
        vertexShaderElm.textContent = vertexShader;
        root.appendChild(vertexShaderElm);

        Uniforms uniforms = new Uniforms();
        setUniforms(uniforms);

        Color color = Js.uncheckedCast(uniforms.get("topColor"));
        color.copy(light.color);

        SphereBufferGeometry skyGeo = new SphereBufferGeometry(4000, 32, 15);

        ShaderMaterialParameters parameters = new ShaderMaterialParameters();
        parameters.uniforms = uniforms;
        parameters.vertexShader = vertexShaderElm.textContent;
        parameters.fragmentShader = fragmentShaderElm.textContent;
        parameters.side = THREE.BackSide;

        ShaderMaterial skyMat = new ShaderMaterial(parameters);

        Mesh sky = new Mesh(skyGeo, skyMat);
        scene.add(sky);
        JSONLoader loader = new JSONLoader();
        loader.load("models/json/lightmap/lightmap.json", (OnLoadCallbackPair<Geometry, Material[]>) (geometry, materials) -> {
            for (int i = 0; i < materials.length; i++) {
                MeshPhongMaterial meshPhongMaterial = materials[i].cast();
                meshPhongMaterial.lightMapIntensity = 0.75f;
            }
            Mesh mesh = new Mesh(geometry, materials);
            mesh.scale.multiplyScalar(100);
            scene.add(mesh);
        });

        WebGLRendererParameters webGLRendererParameters = new WebGLRendererParameters();
        webGLRendererParameters.antialias = true;
        renderer = new WebGLRenderer(webGLRendererParameters);
        setupWebGLRenderer(renderer);
        container.appendChild(renderer.domElement);
    }

    //shitty code, sometimes js better whan java :(
    private void setUniforms(Uniforms uniforms) {
        JsObject topColor = new JsObject();
        topColor.setProperty("type", "c");
        topColor.setProperty("value", new Color(0x0077ff));

        JsObject bottomColor = new JsObject();
        bottomColor.setProperty("type", "c");
        bottomColor.setProperty("value", new Color(0xffffff));

        JsObject offset = new JsObject();
        offset.setProperty("type", "f");
        offset.setProperty("value", 400);

        JsObject exponent = new JsObject();
        exponent.setProperty("type", "f");
        exponent.setProperty("value", 0.6);

        JsObject temp = Js.uncheckedCast(uniforms);
        temp.setProperty("topColor", topColor);
        temp.setProperty("bottomColor", bottomColor);
        temp.setProperty("offset", offset);
        temp.setProperty("exponent", exponent);
    }

    @Override
    protected void doAttachScene() {
        root.appendChild(container);
        renderer.setSize(getWidth(), getHeight());
        animate();
    }

    @Override
    protected void doAttachInfo() {
        AppSetup.infoDiv.show().hide();
    }

    private void animate() {
        AnimationScheduler.get().requestAnimationFrame(timestamp -> {
            if (root.parentNode != null) {
                StatsProducer.getStats().update();
                render();
                animate();
            }
        });
    }

    private void render() {
        renderer.render(scene, camera);
    }

}


