package com.nocomment.sphevres;

import android.graphics.Color;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRScene;
import org.gearvrf.scene_objects.GVRModelSceneObject;

final class Main extends GVRMain {
    @Override
    public void onInit(GVRContext gvrContext) throws Throwable {
        GVRScene scene = gvrContext.getMainScene();

        scene.getMainCameraRig().getLeftCamera().setBackgroundColor(Color.BLUE);
        scene.getMainCameraRig().getRightCamera().setBackgroundColor(Color.BLUE);

        try {
            GVRModelSceneObject geosphere = gvrContext.getAssetLoader().loadModel("biosphere3-3ds/biosphere3.3ds");
            geosphere.getTransform().setPosition(0.f, 0.f, 0.f);
            scene.addSceneObject(geosphere);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SplashMode getSplashMode() {
        return SplashMode.NONE;
    }

    @Override
    public void onStep() {
    }
}
