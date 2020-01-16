package easyconduite.tools.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class ApacheLogSerializer extends StdSerializer<Level> {

    public ApacheLogSerializer() {
        this(null);
    }

    protected ApacheLogSerializer(Class<Level> t) {
        super(t);
    }

    @Override
    public void serialize(Level o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("level",o.name());
        jsonGenerator.writeEndObject();
    }
}
