package marsrover.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageList {
    @JsonProperty("photos")
    private Image[] images;

    public Image[] getImages() {
        return this.images;
    }

    public void setImages(Image[] images) {
        this.images = images;
    }
}