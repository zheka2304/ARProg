package arprog.inc.ar.opengl;

import java.util.HashMap;

import arprog.inc.ar.arprog.R;

/**
 * prepares all render data for further access
 *
 */
public class RenderData {
    public static TextureHelper.Texture FLAT_WHITE;

    private static HashMap<String, Model> preparedModels = new HashMap<String, Model>();
    private static HashMap<String, ShaderHelper.Shader> preparedShaders = new HashMap<String, ShaderHelper.Shader>();
    private static HashMap<String, TextureHelper.Texture> preparedTextures = new HashMap<String, TextureHelper.Texture>();

    public static void prepareAllModels() {
        // load models
        prepareModel("cow.obj", R.raw.cow, .1f);
        prepareModel("owl.obj", R.raw.owl, .1f);
    }

    public static void prepareAllAssets() {
        FLAT_WHITE = TextureHelper.loadTexture(R.drawable.flat_white);

        // load textures
        prepareTexture("ar_icon_contrast", R.drawable.ar_icon_contrast);
        prepareTexture("model_texture_owl", R.drawable.model_texture_owl);

        // load shaders
        prepareShader("default", R.raw.default_vertex, R.raw.default_fragment);
        prepareShader("model", R.raw.model_vertex, R.raw.model_fragment);
        prepareShader("projector", R.raw.projector_vertex, R.raw.projector_fragment);
        prepareShader("hologram", R.raw.hologram_vertex, R.raw.hologram_fragment);
    }

    public static void prepareModel(String name, int res, float scale) {
        preparedModels.put(name, ModelLoader.loadObj(res, scale));
    }

    public static Model getModel(String name) {
        return preparedModels.get(name);
    }

    public static void prepareTexture(String name, int res) {
        preparedTextures.put(name, TextureHelper.loadTexture(res));
    }

    public static TextureHelper.Texture getTexture(String name) {
        return preparedTextures.get(name);
    }

    public static void prepareShader(String name, int res1, int res2) {
        preparedShaders.put(name, ShaderHelper.createShader(res1, res2));
    }

    public static void prepareShader(String name, int res1, int res2, ShaderHelper.IAttributeNames attrNames) {
        preparedShaders.put(name, ShaderHelper.createShader(res1, res2, attrNames));
    }

    public static ShaderHelper.Shader getShader(String name) {
        return preparedShaders.get(name);
    }
}
