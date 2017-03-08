
package com.romif.securityalarm.gateway.domain.sms;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "num_parts",
    "sender",
    "content"
})
public class MessageResponse_ {

    @JsonProperty("num_parts")
    private Integer numParts;
    @JsonProperty("sender")
    private String sender;
    @JsonProperty("content")
    private String content;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("num_parts")
    public Integer getNumParts() {
        return numParts;
    }

    @JsonProperty("num_parts")
    public void setNumParts(Integer numParts) {
        this.numParts = numParts;
    }

    @JsonProperty("sender")
    public String getSender() {
        return sender;
    }

    @JsonProperty("sender")
    public void setSender(String sender) {
        this.sender = sender;
    }

    @JsonProperty("content")
    public String getContent() {
        return content;
    }

    @JsonProperty("content")
    public void setContent(String content) {
        this.content = content;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
