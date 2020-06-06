package marsrover.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Image {
    @JsonProperty("img_src")
    private String source;

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}