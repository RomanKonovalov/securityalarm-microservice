package com.romif.securityalarm.service.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Created by Roman_Konovalov on 3/7/2017.
 */
@Data
@JsonSerialize(using = DeviceConfigDTOSerializer.class)
public class DeviceConfigDTO {

    private String sendLocationPath;
    private String pauseAlarmPath;
    private String resumeAlarmPath;
    private String phone;

    @Override
    public String toString() {
        return StringUtils.join(new String[]{sendLocationPath, pauseAlarmPath, resumeAlarmPath, phone, ""},';');
    }
}

class DeviceConfigDTOSerializer extends JsonSerializer<DeviceConfigDTO> {

    @Override
    public void serialize(DeviceConfigDTO deviceConfigDTO, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeRaw(StringUtils.join(
            new String[]{
                deviceConfigDTO.getSendLocationPath(),
                deviceConfigDTO.getPauseAlarmPath(),
                deviceConfigDTO.getResumeAlarmPath(),
                deviceConfigDTO.getPhone(),
                ""},
            ';'));
    }
}
