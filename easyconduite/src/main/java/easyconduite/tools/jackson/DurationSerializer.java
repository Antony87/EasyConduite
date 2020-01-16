package easyconduite.tools.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import javafx.util.Duration;

import java.io.IOException;

public class DurationSerializer extends StdSerializer<Duration> {

    public DurationSerializer() {
        this(null);
    }

    public DurationSerializer(Class<Duration> t) {
        super(t);
    }

    @Override
    public void serialize(Duration duration, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("ms", duration.toMillis());
        jsonGenerator.writeEndObject();
    }
}
