package org.runaway;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

public class ResourceDownloader {

    private final static String VERSIONS_LIST = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
    private final static String ASSETS_URL = "http://resources.download.minecraft.net/";
    private final Gson gson = new Gson();

    public ResourceDownloader() {

    }

    /**
     *
     * @param locale Name of resource. Ex.: ru_RU, en_CA, etc.
     * @param destination Destination where to store file.
     */
    public void downloadResource(String locale, File destination) throws IOException {
        VersionManifest vm = this.downloadObject(new URL(ResourceDownloader.VERSIONS_LIST), VersionManifest.class);
        ClientVersion client = this.downloadObject(new URL(vm.getLatestRelease().getUrl()), ClientVersion.class);
        AssetIndex ai = this.downloadObject(new URL(client.getAssetUrl()), AssetIndex.class);
        String hash = ai.getLocaleHash(locale);
        Prison.getInstance().getLogger().log(Level.INFO, "Downloading {0}.lang (hash: {1})", new Object[]{locale, hash});
        FileUtils.copyURLToFile(new URL(ResourceDownloader.ASSETS_URL + this.createPathFromHash(hash)), destination);
    }

    private <T> T downloadObject(URL url, Class<T> object) throws IOException {
        try (InputStream inputStream = url.openConnection().getInputStream();
             InputStreamReader r = new InputStreamReader(inputStream);
             JsonReader jr = new JsonReader(r)) {
            return this.gson.fromJson(jr, object);
        }
    }

    /**
     * From Mojang, with love.
     */
    private String createPathFromHash(String hash) {
        return hash.substring(0, 2) + "/" + hash;
    }

    /*
        Gson serialization
     */
    class VersionManifest {

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        private LinkedTreeMap<String, String> latest;
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        private ArrayList<RemoteClient> versions;

        RemoteClient getLatestRelease() {
            String release = this.latest.get("release");
            for (RemoteClient c : this.versions) {
                if (c.getId().equals(release)) {
                    return c;
                }
            }

            throw new IllegalArgumentException(release + " does not exists. There something is definitely wrong.");
        }
    }

    class RemoteClient {

        private String id, url;

        public String getId() {
            return id;
        }

        String getUrl() {
            return url;
        }
    }

    class ClientVersion {

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        private LinkedTreeMap<String, String> assetIndex;

        String getAssetUrl() {
            return this.assetIndex.get("url");
        }
    }

    class AssetIndex {

        private final static String PATH = "minecraft/lang/%s.json";
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        private LinkedTreeMap<String, LinkedTreeMap<String, String>> objects;

        String getLocaleHash(String locale) {
            this.objects.keySet().forEach(key -> {
                Bukkit.getConsoleSender().sendMessage("Key: " + key);
            });
            LinkedTreeMap<String, String> asset
                    = this.objects.get(String.format(PATH, locale.toLowerCase()));
            if (asset == null) {
                throw new IllegalArgumentException("Locale " + locale + " does not exists!");
            }
            return asset.get("hash");
        }
    }
}
