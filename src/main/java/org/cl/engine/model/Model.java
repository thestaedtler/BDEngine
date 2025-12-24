package org.cl.engine.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Location;
import org.cl.engine.adapter.types.BlockComponentAdapter;
import org.cl.engine.adapter.types.CollectionAdapter;
import org.cl.engine.adapter.types.ItemComponentAdapter;
import org.cl.engine.adapter.types.Matrix4fAdapter;
import org.cl.engine.adapter.types.TextComponentAdapter;
import org.cl.engine.collection.types.BlockDisplaySchematicComponent;
import org.cl.engine.collection.types.CollectionSchematicComponent;
import org.cl.engine.collection.types.ItemDisplaySchematicComponent;
import org.cl.engine.collection.types.TextDisplaySchematicComponent;
import org.cl.engine.model.types.Group;
import org.joml.Matrix4f;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

/**
 * Represents a model spawned from a {@link Schematic}. Note that in order for
 * all related functionality to work well, it is highly advised that group names/ids are
 * made and kept unique.
 * @param group the parent (project) group
 *
 * @author cahrypt
 */
public record Model(Group group) {
    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .serializeNulls()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
            .registerTypeAdapter(CollectionSchematicComponent.class, new CollectionAdapter())
            .registerTypeAdapter(BlockDisplaySchematicComponent.class, new BlockComponentAdapter())
            .registerTypeAdapter(ItemDisplaySchematicComponent.class, new ItemComponentAdapter())
            .registerTypeAdapter(TextDisplaySchematicComponent.class, new TextComponentAdapter())
            .registerTypeAdapter(Matrix4f.class, new Matrix4fAdapter())
            .create();

    public record Schematic(CollectionSchematicComponent collection) {
        private static String getUglyString(ByteArrayInputStream inputStream) throws IOException {
            GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int read;

            while ((read = gzipInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            String uglyJson = outputStream.toString();

            gzipInputStream.close();
            outputStream.close();

            uglyJson = uglyJson.substring(1, uglyJson.length() - 1);
            return uglyJson;
        }

        /**
         * Deserializes a file in bdengine format into a {@link Schematic}
         * @param file the file to deserialize
         * @return the schematic
         * @throws IOException if the file could not be deserialized
         */
        public static Schematic fromEngineFormat(File file) throws IOException {
            return fromEngineFormat(file.toURI().toURL().openStream());
        }

        /**
         * Deserializes an {@link InputStream} into a {@link Schematic}
         * @param stream the input stream to deserialize
         * @return the schematic
         * @throws IOException if the stream could not be deserialized
         */
        public static Schematic fromEngineFormat(InputStream stream) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String base64EncodedLine = reader.readLine();
            reader.close();

            byte[] compressedData = Base64.getDecoder().decode(base64EncodedLine);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedData);
            String uglyJson = getUglyString(inputStream);

            return new Schematic(GSON.fromJson(uglyJson, CollectionSchematicComponent.class));
        }

        /**
         * Spawns the model at the given location. Note that pitch and yaw specified by the
         * location will induce rotation with respect to the world coordinate frame.
         * @param location the location
         * @return the physical model
         */
        public Model build(Location location) {
            return new Model((Group) collection.build(location, new Matrix4f()));
        }
    }
}
