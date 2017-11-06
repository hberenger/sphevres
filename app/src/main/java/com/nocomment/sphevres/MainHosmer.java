package com.nocomment.sphevres;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.animation.GVRAnimation;
import org.gearvrf.animation.GVRRepeatMode;
import org.gearvrf.animation.GVRRotationByAxisWithPivotAnimation;
import org.gearvrf.scene_objects.GVRModelSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

final class MainHosmer extends Main {

    static int kPHARE_TAG = 1;

    private List<GVRAnimation> mAnimations = new ArrayList<GVRAnimation>();
    private int planetLoadCounter = 8;
    private GVRScene scene;

    MainHosmer(AmbisonicPlayer player) {
        super(player);
    }

    @Override
    public void onInit(GVRContext gvrContext) throws Throwable {
        super.onInit(gvrContext);
        scene = gvrContext.getMainScene();

        scene.getMainCameraRig().getTransform().setPosition(-1.164f, -0.765f, 1.716f);

        loadModel(gvrContext, "hosmer/hosmerhb.3ds", kPHARE_TAG);
        loadModel(gvrContext, "hosmer/1-mercure.3ds", 2);
        loadModel(gvrContext, "hosmer/2-venus.3ds", 3);
        loadModel(gvrContext, "hosmer/3-terre.3ds", 4);
        loadModel(gvrContext, "hosmer/4-mars.3ds", 5);
        loadModel(gvrContext, "hosmer/5-jupiter.3ds", 6);
        loadModel(gvrContext, "hosmer/6-saturne.3ds", 7);
        loadModel(gvrContext, "hosmer/7-uranus.3ds", 8);
        loadModel(gvrContext, "hosmer/8-neptune.3ds", 9);

        GVRSceneObject environment = buildEnvironment(gvrContext);
        scene.addSceneObject(environment);
    }

    private void startAnimations(GVRContext gvrContext) {
        for (GVRAnimation animation : mAnimations) {
            animation.start(gvrContext.getAnimationEngine());
        }
        mAnimations = null;
    }


    @Override
    public void sceneLoaded(GVRModelSceneObject model, GVRContext gvrContext, int tag) {
        GVRSceneObject result = model;

        if (tag != kPHARE_TAG) {
            //float periods[] = { 88, 225, 365, 687, 4335, 10758, 30687, 60225 };
            float periods[] =   { 5,   13,  20,  40,  100,   200,   600,  1200 };
            float period = periods[tag - 2];
            GVRSceneObject planetPositionObject = new GVRSceneObject(gvrContext);

            GVRSceneObject planetRotationObject = new GVRSceneObject(gvrContext);
            planetPositionObject.addChildObject(planetRotationObject);

            rotate(planetRotationObject, period, false, -2.f * 0.5825f, 1.f, -0.0f);

            planetRotationObject.addChildObject(result);
            result = planetPositionObject;
            planetLoadCounter--;
        }

        float scale = 0.5f;
        result.getTransform().setScale(scale, scale, scale);
        // x vers la droite, y vers le haut, z derrière
        result.getTransform().setPosition(0.5825f, -10.f, +0.0f);

        scene.addSceneObject(result);

        if (planetLoadCounter == 0 && mAnimations != null) {
            startAnimations(gvrContext);
        }
    }

    private void rotate(GVRSceneObject object, float duration, boolean inverse, float px, float py, float pz) {
        GVRAnimation animation = new GVRRotationByAxisWithPivotAnimation(
                object, duration, inverse ? -360.f : 360.0f,
                0.f, 1.0f, 0.f,
                px, py, pz);
        animation.setRepeatMode(GVRRepeatMode.REPEATED).setRepeatCount(-1);
        mAnimations.add(animation);
    }

    private GVRSceneObject buildEnvironment(GVRContext context) {
        Future<GVRTexture> tex = context.getAssetLoader().loadFutureCubemapTexture(new GVRAndroidResource(context, R.raw.cubemapgood));
        GVRMaterial material = new GVRMaterial(context, GVRMaterial.GVRShaderType.Cubemap.ID);
        material.setMainTexture(tex);
        GVRSphereSceneObject environment = new GVRSphereSceneObject(context, 18, 36, false, material, 4, 4);
        environment.getTransform().setScale(120.0f, 120.0f, 120.0f);

        GVRDirectLight sunLight = new GVRDirectLight(context);
        sunLight.setAmbientIntensity(0.4f, 0.4f, 0.4f, 1.0f);
        sunLight.setDiffuseIntensity(0.6f, 0.6f, 0.6f, 1.0f);
        sunLight.setDefaultOrientation(new Quaternionf(0.f, 0.f, -1.f));
        sunLight.setCastShadow(false);

        environment.attachComponent(sunLight);

        return environment;
    }

    @Override
    public float getWalkStep() {
        return 0.5f;
    }
}
