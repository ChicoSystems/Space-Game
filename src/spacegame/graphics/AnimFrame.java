package spacegame.graphics;

import java.awt.Image;

public class AnimFrame {

    public Image image;
    public long endTime;

    public AnimFrame(Image image, long endTime) {
        this.image = image;
        this.endTime = endTime;
    }
}