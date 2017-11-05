package com.nocomment.sphevres;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRScene;
import org.gearvrf.scene_objects.GVRModelSceneObject;

abstract class Main extends GVRMain {

    private AmbisonicPlayer player;
    private GVRCameraRig cameraRig;
    private GVRScene mMainScene;

    Main(AmbisonicPlayer player) {
        super();
        this.player = player;
    }

    @Override
    public void onInit(GVRContext gvrContext) throws Throwable {
        cameraRig = gvrContext.getMainScene().getMainCameraRig();

        cameraRig.getLeftCamera().setBackgroundColor(Color.BLUE);
        cameraRig.getRightCamera().setBackgroundColor(Color.BLUE);

        mMainScene = gvrContext.getMainScene();
    }

    abstract public void sceneLoaded(GVRModelSceneObject result, GVRContext gvrContext, int tag);

    @Override
    public SplashMode getSplashMode() {
        return SplashMode.NONE;
    }

    @Override
    public void onStep() {
        if (player != null) {
            player.applyTransform(cameraRig.getHeadTransform());
        }
    }

    public void loadModel(GVRContext gvrContext, String name, int tag) {
        new LoadingTask(gvrContext, name, tag).execute();
    }

    public void loadModel(GVRContext gvrContext, String name) {
        loadModel(gvrContext, name, 0);
    }

    private class LoadingTask extends AsyncTask<String, Void, GVRModelSceneObject> {

        private GVRContext gvrContext;
        private String modelName;
        private int tag;

        LoadingTask(GVRContext gvrContext, String modelName, int tag) {
            this.gvrContext = gvrContext;
            this.modelName = modelName;
            this.tag = tag;
        }

        @Override
        protected GVRModelSceneObject doInBackground(String... params) {
            GVRModelSceneObject geosphere = null;
            try {
                geosphere = gvrContext.getAssetLoader().loadModel(modelName);
            } catch (Exception e) {
                // $$$$ TODO retry
                e.printStackTrace();
            }
            return geosphere;
        }

        @Override
        protected void onPostExecute(GVRModelSceneObject result) {
            Main.this.sceneLoaded(result, gvrContext, tag); // $$$$ faudrait unsubscribe aussi
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    @Override
    public boolean onBackPress() {
        float[] dir = mMainScene.getMainCameraRig().getLookAt();
        for (int i = 0; i < 3; ++i) {
            dir[i] *= getWalkStep();
        }
        float x = mMainScene.getMainCameraRig().getTransform().getPositionX();
        float y = mMainScene.getMainCameraRig().getTransform().getPositionY();
        float z = mMainScene.getMainCameraRig().getTransform().getPositionZ();

        x += dir[0];
        y += dir[1];
        z += dir[2];

        Log.d("Main", String.format("Going to : (%.3f, %.3f, %.3f", x, y, z));

        mMainScene.getMainCameraRig().getTransform().setPosition(x, y, z);
        return true;
    }

    public float getWalkStep() {
        return 10.f;
    }
}
