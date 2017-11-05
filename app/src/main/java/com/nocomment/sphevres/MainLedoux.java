package com.nocomment.sphevres;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRPhongShader;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRSpotLight;
import org.gearvrf.GVRTexture;
import org.gearvrf.animation.GVRAnimation;
import org.gearvrf.scene_objects.GVRModelSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

final class MainLedoux extends Main {

    private List<GVRAnimation> mAnimations = new ArrayList<GVRAnimation>();
    private GVRScene scene;

    MainLedoux(AmbisonicPlayer player) {
        super(player);
    }

    @Override
    public void onInit(GVRContext gvrContext) throws Throwable {
        super.onInit(gvrContext);
        scene = gvrContext.getMainScene();

//        GVRPerspectiveCamera p = null;
//        p = (GVRPerspectiveCamera)(scene.getMainCameraRig().getLeftCamera());
//        p.setFovY(90);
//        p = (GVRPerspectiveCamera)(scene.getMainCameraRig().getRightCamera());
//        p.setFovY(90);

        //loadModel(gvrContext, "ledoux-3ds.3ds");
        //loadModel(gvrContext, "ledoux-lwo.lwo"); finit par marcher
        /// loadModel(gvrContext, "ledoux-ply.ply"); blue
        //loadModel(gvrContext, "ledoux-stl.stl"); // blue
        //loadModel(gvrContext, "ledoux-v3-blanc.x");
        //loadModel(gvrContext, "ledoux-v3-x.x");
        //loadModel(gvrContext, "ledoux-v3-3ds.3ds");
        loadModel(gvrContext, "ledoux-v3-from3ds.dae");

        GVRSceneObject environment = buildEnvironment(gvrContext);
        scene.addSceneObject(environment);

        GVRSceneObject spotLight = createLight(gvrContext, 1.f, 0.f, 0.f);
        scene.addSceneObject(spotLight);

        for (GVRAnimation animation : mAnimations) {
            animation.start(gvrContext.getAnimationEngine());
        }
        mAnimations = null;
    }

    private GVRSceneObject createLight(GVRContext context, float r, float g, float b)
    {
        GVRSceneObject lightNode = new GVRSceneObject(context);
        GVRSpotLight light = new GVRSpotLight(context);

        lightNode.attachLight(light);
        lightNode.getTransform().setPosition(0, 0.f, 0.f);
        light.setAmbientIntensity(0.3f * r, 0.3f * g, 0.3f * b, 1);
        light.setDiffuseIntensity(r, g, b, 1);
        light.setSpecularIntensity(r, g, b, 1);
        light.setAttenuation(0.1f, 0.02f, 0.0f);
        light.setInnerConeAngle(8);
        light.setOuterConeAngle(12);
        //light.setCastShadow(true);
        lightNode.getTransform().setRotationByAxis(-15.f, 1.f, 0.f, 0.f);

        return lightNode;
    }

    @Override
    public void sceneLoaded(GVRModelSceneObject result, GVRContext gvrContext, int tag) {

        float scale = 300f; // 30.f for ledoux v2.x
        result.getTransform().setScale(scale, scale, scale);

        // x vers la droite, y vers le haut, z derrière
        result.getTransform().setPosition(0.f, -1650.f, 0.f);

        ArrayList<GVRRenderData> renderDatas = result
                .getAllComponents(GVRRenderData.getComponentType());

        GVRMaterial material = new GVRMaterial(gvrContext);

        GVRTexture calepinage = gvrContext.getAssetLoader().loadTexture(new GVRAndroidResource(gvrContext, R.raw.calepinage));
        material.setTexture("diffuseTexture", calepinage);

        //material.setVec4("diffuse_color", 0.8f, 0.8f, 0.8f, 1.0f);
        material.setVec4("ambient_color", 0.3f, 0.3f, 0.3f, 1.0f);
        material.setVec4("specular_color", 1.0f, 1.0f, 1.0f, 1.0f);
        //material.setVec4("emissive_color", 0.5f, 0.0f, 0.5f, 1.0f);
        material.setFloat("specular_exponent", 10.0f);


        for (GVRRenderData renderData : renderDatas) {
            renderData.setMaterial(material);
            renderData.setShaderTemplate(GVRPhongShader.class);
            renderData.enableLight();
        }

        scene.addSceneObject(result);
    }

    private GVRSceneObject buildEnvironment(GVRContext context) {
        Future<GVRTexture> tex = context.getAssetLoader().loadFutureCubemapTexture(new GVRAndroidResource(context, R.raw.lycksele3));
        GVRMaterial material = new GVRMaterial(context, GVRMaterial.GVRShaderType.Cubemap.ID);
        material.setMainTexture(tex);
        GVRSphereSceneObject environment = new GVRSphereSceneObject(context, 18, 36, false, material, 4, 4);
        environment.getTransform().setScale(3200.0f, 3200.0f, 3200.0f);

        GVRDirectLight sunLight = new GVRDirectLight(context);
        sunLight.setAmbientIntensity(1.0f, 0.8f, 0.8f, 1.0f);
        sunLight.setDiffuseIntensity(0.8f, 1.0f, 0.8f, 1.0f);
        sunLight.setSpecularIntensity(0.8f, 0.8f, 1.0f, 1.0f);

        // Ledoux
        sunLight.setDefaultOrientation(new Quaternionf(-1.f, 0.f, 0.f));
        sunLight.setPosition(0.f, 1000.f, 0.f);
        sunLight.setCastShadow(false);

        environment.attachComponent(sunLight);

        return environment;
    }

    @Override
    public float getWalkStep() {
        return 50.f;
    }
}
