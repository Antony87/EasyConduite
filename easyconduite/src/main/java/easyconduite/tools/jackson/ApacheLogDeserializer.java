package easyconduite.tools.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class ApacheLogDeserializer extends StdDeserializer<Level> {

    public ApacheLogDeserializer() {
        this(null);
    }

    protected ApacheLogDeserializer(Class<Level> t) {
        super(t);
    }

    @Override
    public Level deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        final JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        final String sLevel = node.get("level").asText();
        Level level = Level.getLevel(sLevel);
        return level;
    }
}
