package simplforum.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.data.jpa.domain.AbstractPersistable;

/** Serializes persistable objects to JSON by their ID. This causes the serialized object
 * to show up as an number in the JSON string; further requests are needed to retrieve
 * any of its other properties.
 */
public class PersistableIdSerializer extends JsonSerializer<AbstractPersistable<Long>> {

    /** Serializes a persistable object. Used internally by the JSON serialization
     * process.
     * @param persistable The object which is being serialized.
     * @param generator Generator for JSON serialization data.
     * @param provider A provider for serializers.
     * @throws IOException If an IO exception occurs when serializing.
     */
    @Override
    public void serialize(AbstractPersistable<Long> persistable, JsonGenerator generator,
            SerializerProvider provider) throws IOException {
        generator.writeNumber(persistable.getId());
    }

}
