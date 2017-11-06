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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

final class MainMontreal extends Main {

    private List<GVRAnimation> mAnimations = new ArrayList<GVRAnimation>();
    private GVRScene scene;

    MainMontreal(AmbisonicPlayer player) {
        super(player);
    }

    @Override
    public void onInit(GVRContext gvrContext) throws Throwable {
        super.onInit(gvrContext);
        scene = gvrContext.getMainScene();
        
        loadModel(gvrContext, "biosphere3-3ds/biosphere3.3ds");

        GVRSceneObject solarSystem = buildSolarSystem(gvrContext, scene);
        scene.addSceneObject(solarSystem);

        GVRSceneObject environment = buildEnvironment(gvrContext);
        scene.addSceneObject(environment);

        for (GVRAnimation animation : mAnimations) {
            animation.start(gvrContext.getAnimationEngine());
        }
        mAnimations = null;
    }

    @Override
    public void sceneLoaded(GVRModelSceneObject result, GVRContext gvrContext, int tag) {

        float scale = 100.f;
        result.getTransform().setScale(scale, scale, scale);

        // x vers la droite, y vers le haut, z derrière
        result.getTransform().setPosition(0.f, 0.f, 0.f);

        scene.addSceneObject(result);
    }

    private GVRSceneObject buildSolarSystem(GVRContext gvrContext, GVRScene scene) throws IOException {
        GVRSceneObject globalPositionObject = new GVRSceneObject(gvrContext);
        globalPositionObject.getTransform().setPosition(25.f, 0.f, 0.f);

        GVRSceneObject globalRotationObject = new GVRSceneObject(gvrContext);
        globalPositionObject.addChildObject(globalRotationObject);
        rotate(globalRotationObject, 30.f);

        // mercure
        float radius = 60.f;
        GVRSceneObject mercure = addPlanet(gvrContext, scene, radius, 0.0f, "mercure/sphere_mercury.obj", 5.f);
        globalRotationObject.addChildObject(mercure);
        rotate(mercure.getChildByIndex(0), 10.f);

        // venus
        GVRSceneObject venus = addPlanet(gvrContext, scene, -radius / 2.f, radius * (float)Math.sqrt(3.0) / 2.f, "venus/sphere_venus.obj", 7.5f);
        globalRotationObject.addChildObject(venus);
        rotate(venus.getChildByIndex(0), 5.f, true);

        // mars
        GVRSceneObject mars = addPlanet(gvrContext, scene, -radius / 2.f, -radius * (float)Math.sqrt(3.0) / 2.f, "mars/sphere_mars.obj", 10.0f);
        globalRotationObject.addChildObject(mars);
        rotate(mars.getChildByIndex(0), 5.f);
        return globalPositionObject;
    }

    private GVRSceneObject addPlanet(GVRContext gvrContext, GVRScene scene, float x, float z, String filePath, float k) throws IOException {
        GVRSceneObject planetRevolutionObject = new GVRSceneObject(gvrContext);
        planetRevolutionObject.getTransform().setPosition(x, 5.0f, z);

        GVRSceneObject planetRotationObject = new GVRSceneObject(gvrContext);
        planetRevolutionObject.addChildObject(planetRotationObject);

        GVRSceneObject meshObject = gvrContext.getAssetLoader().loadModel(filePath, scene);
        meshObject.getTransform().setScale(k, k, k);
        planetRotationObject.addChildObject(meshObject);

        return planetRevolutionObject;
    }

    private void rotate(GVRSceneObject object, float duration, boolean inverse) {
        GVRAnimation animation = new GVRRotationByAxisWithPivotAnimation(
                object, duration, inverse ? -360.f : 360.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f);
        animation.setRepeatMode(GVRRepeatMode.REPEATED).setRepeatCount(-1);
        mAnimations.add(animation);
    }

    private void rotate(GVRSceneObject object, float duration) {
        rotate(object, duration, false);
    }

    private GVRSceneObject buildEnvironment(GVRContext context) {
        GVRTexture tex = context.getAssetLoader().loadCubemapTexture(new GVRAndroidResource(context, R.raw.lycksele3));
        GVRMaterial material = new GVRMaterial(context, GVRMaterial.GVRShaderType.Cubemap.ID);
        material.setMainTexture(tex);
        GVRSphereSceneObject environment = new GVRSphereSceneObject(context, 18, 36, false, material, 4, 4);
        environment.getTransform().setScale(120.0f, 120.0f, 120.0f);

        GVRDirectLight sunLight = new GVRDirectLight(context);
        sunLight.setAmbientIntensity(0.4f, 0.4f, 0.4f, 1.0f);
        sunLight.setDiffuseIntensity(0.6f, 0.6f, 0.6f, 1.0f);
        sunLight.setDefaultOrientation(new Quaternionf(1.f, 0.f, 0.f));
        sunLight.setCastShadow(false);

        environment.attachComponent(sunLight);

        return environment;
    }
}
