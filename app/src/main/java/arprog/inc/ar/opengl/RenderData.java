package arprog.inc.ar.opengl;

import java.io.InputStream;
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
        prepareModel("cow.obj", R.raw.cow, 1.5f);
        prepareModel("owl.obj", R.raw.owl, 1.5f);
        prepareModel("ussr.obj", R.raw.hammer_and_sickle, 1.5f);

        prepareModel("current", R.raw.hammer_and_sickle, 1.5f);
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

    public static void prepareModel(String name, InputStream is) {
        preparedModels.put(name, ModelLoader.asObj(is, 1, true));
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
