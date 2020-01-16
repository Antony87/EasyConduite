package easyconduite.tools.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import javafx.util.Duration;

import java.io.IOException;

public class DurationDeserializer extends StdDeserializer<Duration> {

    public DurationDeserializer(){
        this(null);
    }

    public DurationDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Duration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        //jsonParser.getNumberValue();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
                //int id = (Integer) ((IntNode) node.get("id")).numberValue();
        double ms = node.get("ms").asDouble();
        return new Duration(ms);
    }
}
