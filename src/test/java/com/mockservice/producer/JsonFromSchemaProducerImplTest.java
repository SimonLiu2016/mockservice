package com.mockservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockservice.util.IOUtils;
import com.mockservice.util.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
public class JsonFromSchemaProducerImplTest {

    @Mock
    private RandomUtils randomUtils;

    private JsonFromSchemaProducer producer() {
        return new JsonFromSchemaProducerImpl(new ValueProducerImpl(randomUtils), randomUtils);
    }

    @Test
    public void generate() throws IOException {
        String jsonSchema = IOUtils.asString("json_schema.json");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonSchemaMap = mapper.readValue(jsonSchema, Map.class);
        String json = producer().jsonFromSchema(jsonSchemaMap);
        System.out.println(json);

        assertDoesNotThrow(() -> mapper.readTree(json));
    }
}
