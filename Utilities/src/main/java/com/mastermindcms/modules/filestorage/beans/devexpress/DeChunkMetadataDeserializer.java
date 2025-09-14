package com.mastermindcms.modules.filestorage.beans.devexpress;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class DeChunkMetadataDeserializer extends JsonDeserializer<DeChunkMetadata> {
    @Override
    public DeChunkMetadata deserialize(
            JsonParser jsonParser, 
            DeserializationContext deserializationContext
    ) throws IOException {
        return new ObjectMapper().readValue(jsonParser.getValueAsString(), DeChunkMetadata.class);
    }
}
