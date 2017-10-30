package com.nocomment.sphevres;

import android.graphics.Color;
import android.os.AsyncTask;

import org.gearvrf.GVRCameraRig;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMain;
import org.gearvrf.scene_objects.GVRModelSceneObject;

abstract class Main extends GVRMain {

    private AmbisonicPlayer player;
    private GVRCameraRig cameraRig;

    Main(AmbisonicPlayer player) {
        super();
        this.player = player;
    }

    @Override
    public void onInit(GVRContext gvrContext) throws Throwable {
        cameraRig = gvrContext.getMainScene().getMainCameraRig();

        cameraRig.getLeftCamera().setBackgroundColor(Color.BLUE);
        cameraRig.getRightCamera().setBackgroundColor(Color.BLUE);
    }

    abstract public void sceneLoaded(GVRModelSceneObject result, GVRContext gvrContext);

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

    public void loadModel(GVRContext gvrContext, String name) {
        new LoadingTask(gvrContext, name).execute();
    }

    private class LoadingTask extends AsyncTask<String, Void, GVRModelSceneObject> {

        private GVRContext gvrContext;
        private String modelName;

        LoadingTask(GVRContext gvrContext, String modelName) {
            this.gvrContext = gvrContext;
            this.modelName = modelName;
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
            Main.this.sceneLoaded(result, gvrContext); // $$$$ faudrait unsubscribe aussi
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
