package com.nocomment.sphevres;

import android.graphics.Color;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRPerspectiveCamera;
import org.gearvrf.GVRPhongShader;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.animation.GVRAnimation;
import org.gearvrf.animation.GVRPositionAnimation;
import org.gearvrf.animation.GVRRepeatMode;
import org.gearvrf.scene_objects.GVRModelSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

final class MainFriede extends Main {

    private GVRScene scene;
    private GVRSceneObject mLightObject;

    private List<GVRAnimation> mAnimations = new ArrayList<GVRAnimation>();

    MainFriede(AmbisonicPlayer player) {
        super(player);
    }

    @Override
    public void onInit(GVRContext gvrContext) throws Throwable {
        super.onInit(gvrContext);

        scene = gvrContext.getMainScene();

        GVRPerspectiveCamera right = (GVRPerspectiveCamera)scene.getMainCameraRig().getRightCamera();
        GVRPerspectiveCamera left = (GVRPerspectiveCamera)scene.getMainCameraRig().getLeftCamera();
        left.setFovY(110);
        right.setFovY(110);
        left.setBackgroundColor(Color.BLACK);
        right.setBackgroundColor(Color.BLACK);

        scene.getMainCameraRig().getTransform().setPosition(0.f, 500.f, 0.f);

        scene.getMainCameraRig().setFarClippingDistance(10000.f);

        loadModel(gvrContext, "friede-3ds.3ds");
        //loadModel(gvrContext, "friede-v2-3ds.3ds");

        GVRSceneObject spherex = addCoordinate(gvrContext, 1.0f, 0.0f, 0.0f);
        scene.addSceneObject(spherex);
        GVRSceneObject spherey = addCoordinate(gvrContext, 0.0f, 1.0f, 0.0f);
        scene.addSceneObject(spherey);
        GVRSceneObject spherez = addCoordinate(gvrContext, 0.0f, 0.0f, 1.0f);
        scene.addSceneObject(spherez);

        mLightObject = buildLight(gvrContext);
        scene.addSceneObject(mLightObject);

        //translate(scene, 700.f, /*duration */ 10.f);

        for (GVRAnimation animation : mAnimations) {
            animation.start(gvrContext.getAnimationEngine());
        }
        mAnimations = null;
    }

    @Override
    public void sceneLoaded(GVRModelSceneObject result, GVRContext gvrContext, int tag) {

        ArrayList<GVRRenderData> renderDatas = result.getAllComponents(GVRRenderData.getComponentType());
        for (GVRRenderData renderData : renderDatas) {
            //renderData.enableLight();
        }

        GVRSceneObject resultPositionObject = new GVRSceneObject(gvrContext);
        resultPositionObject.getTransform().setPosition(0.f, -10.f, 10.f);

        GVRSceneObject resultScaleObject = new GVRSceneObject(gvrContext);
        float scale = 10.f;
        resultScaleObject.getTransform().setScale(scale, scale, scale);

        resultPositionObject.addChildObject(resultScaleObject);
        resultScaleObject.addChildObject(result);

        scene.addSceneObject(resultPositionObject);
    }

    private GVRSceneObject addCoordinate(GVRContext gvrContext, float x, float y, float z) throws IOException {
        GVRSceneObject sphereObject = new GVRSphereSceneObject(gvrContext);
        float r = 10.f;
        sphereObject.getTransform().setPosition(x * r, y * r, z * r);


        GVRMaterial flatMaterial;
        flatMaterial = new GVRMaterial(gvrContext);
        flatMaterial.setColor((x != 0.f) ? 1.f : 0.f, (y != 0.f) ? 1.f : 0.f, (z != 0.f) ? 1.f : 0.f);
        flatMaterial.setDiffuseColor((x != 0.f) ? 1.f : 0.f, (y != 0.f) ? 1.f : 0.f, (z != 0.f) ? 1.f : 0.f, 0.8f);

        sphereObject.getRenderData().setShaderTemplate(GVRPhongShader.class);
        sphereObject.getRenderData().setMaterial(flatMaterial);

        return sphereObject;
    }

    private void translate(GVRScene scene, float distance, float duration) {
        GVRAnimation animation = new GVRPositionAnimation(scene.getMainCameraRig().getTransform(), duration, distance, 0.f, 0.f);
        animation.setRepeatMode(GVRRepeatMode.REPEATED).setRepeatCount(-1);
//        animation.setOnFinish(new GVROnRepeat() {
//            @Override
//            public boolean iteration(GVRAnimation animation, int count) {
//                count = count % 6;
//                Log.d("coucou", "count = " + count);
//                if (count == 0) {
//                    lightObject.getTransform().setRotationByAxis(90, 1, 0, 0);
//                } else if (count == 1) {
//                    lightObject.getTransform().setRotationByAxis(90, 0, 1, 0);
//                } else if (count == 2) {
//                    lightObject.getTransform().setRotationByAxis(90, 0, 0, 1);
//                } else if (count == 3) {
//                    lightObject.getTransform().setRotationByAxis(-90, 1, 0, 0); // par le haut
//                } else if (count == 4) {
//                    lightObject.getTransform().setRotationByAxis(-90, 0, 1, 0);
//                } else if (count == 5) {
//                    lightObject.getTransform().setRotationByAxis(-90, 0, 0, 1); // par le bas
//                }
//                return true;
//            }
//
//            @Override
//            public void finished(GVRAnimation animation) {}
//        });
        mAnimations.add(animation);
    }

    private GVRSceneObject buildLight(GVRContext context) {
        GVRSceneObject lightObject = new GVRSphereSceneObject(context);

        GVRDirectLight sunLight = new GVRDirectLight(context);
        sunLight.setAmbientIntensity(0.4f, 0.4f, 0.4f, 1.0f);
        sunLight.setDiffuseIntensity(0.6f, 0.6f, 0.6f, 1.0f);
        sunLight.setCastShadow(false);

        lightObject.attachComponent(sunLight);

        lightObject.getTransform().setRotationByAxis(-65, 1, 0, 0); // par le haut

        return lightObject;
    }

    @Override
    public boolean onBackPress() {
        float[] dir = scene.getMainCameraRig().getLookAt();
        for (int i = 0; i < 3; ++i) {
            dir[i] *= 50.f;
        }
        float x = scene.getMainCameraRig().getTransform().getPositionX();
        float y = scene.getMainCameraRig().getTransform().getPositionY();
        float z = scene.getMainCameraRig().getTransform().getPositionZ();
        scene.getMainCameraRig().getTransform().setPosition(x + dir[0], y + dir[1], z + dir[2]);
        return true;
    }


}
