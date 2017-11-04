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
}
