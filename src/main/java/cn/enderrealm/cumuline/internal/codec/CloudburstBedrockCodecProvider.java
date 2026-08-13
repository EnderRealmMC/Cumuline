package cn.enderrealm.cumuline.internal.codec;

import cn.enderrealm.bedrock.mappings.BedrockProtocolMappings;
import cn.enderrealm.bedrock.mappings.BedrockVersion;
import cn.enderrealm.cumuline.exception.CumulineException;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves Cloudburst Bedrock codecs from Bedrock client versions.
 */
public final class CloudburstBedrockCodecProvider implements BedrockCodecProvider {
    private final Map<String, BedrockCodec> codecs = new ConcurrentHashMap<>();

    /**
     * Resolves the mapped codec for a client version.
     *
     * @param clientVersion client version or registered alias
     * @return matching Cloudburst codec
     * @throws CumulineException if the version or codec is unavailable
     */
    @Override
    public BedrockCodec getCodec(String clientVersion) {
        BedrockVersion version = BedrockProtocolMappings.findByClientVersion(clientVersion)
                .orElseThrow(() -> new CumulineException("Unsupported Bedrock client version: " + clientVersion));
        return codecs.computeIfAbsent(version.codecId(), this::loadCodec);
    }

    /**
     * Loads a codec class using the runtime package name so Shadow relocation remains supported.
     *
     * @param codecId codec identifier, such as {@code v1001}
     * @return loaded codec
     */
    private BedrockCodec loadCodec(String codecId) {
        String codecPackage = BedrockCodec.class.getPackageName();
        String className = codecPackage + "." + codecId + ".Bedrock_" + codecId;
        Thread currentThread = Thread.currentThread();
        ClassLoader previousClassLoader = currentThread.getContextClassLoader();
        ClassLoader cumulineClassLoader = CloudburstBedrockCodecProvider.class.getClassLoader();
        try {
            // Paper server threads may use a class loader that cannot see the plugin's ServiceLoader resources.
            currentThread.setContextClassLoader(cumulineClassLoader);
            Class<?> codecClass = Class.forName(className, true, CloudburstBedrockCodecProvider.class.getClassLoader());
            Field field = codecClass.getField("CODEC");
            if (!Modifier.isStatic(field.getModifiers())) {
                throw new CumulineException("Cloudburst codec field is not static: " + className + ".CODEC");
            }
            Object value = field.get(null);
            if (!(value instanceof BedrockCodec codec)) {
                throw new CumulineException("Cloudburst codec field has an unexpected type: " + className + ".CODEC");
            }
            return codec;
        } catch (CumulineException exception) {
            throw exception;
        } catch (ReflectiveOperationException | LinkageError exception) {
            throw new CumulineException("Cloudburst codec is unavailable for " + codecId + ": " + className, exception);
        } finally {
            currentThread.setContextClassLoader(previousClassLoader);
        }
    }
}
