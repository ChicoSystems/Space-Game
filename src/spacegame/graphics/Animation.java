package spacegame.graphics;

import java.awt.Image;
import java.util.ArrayList;

/**
    The Animation class manages a series of images (frames) and
    the amount of time to display each frame.
*/
public class Animation {

    private ArrayList frames;
    private int currFrameIndex;
    private long animTime;
    private long totalDuration;


    /**
        Creates a new, empty Animation.
    */
    public Animation() {
        this(new ArrayList(), 0);
    }


    private Animation(ArrayList frames, long totalDuration) {
        this.frames = frames;
        this.totalDuration = totalDuration;
        start();
    }


    /**
        Creates a duplicate of this animation. The list of frames
        are shared between the two Animations, but each Animation
        can be animated independently.
    */
    public Object clone() {
        return new Animation(cloneFrames(frames), totalDuration);
    }

    public ArrayList<AnimFrame> cloneFrames(ArrayList<AnimFrame>frame){
    	ArrayList<AnimFrame>newFrames = (ArrayList<AnimFrame>) frame.clone();
    	return newFrames;
    }

    /**
        Adds an image to the animation with the specified
        duration (time to display the image).
    */
    public synchronized void addFrame(Image image,
        long duration)
    {
        totalDuration += duration;
        frames.add(new AnimFrame(image, totalDuration));
    }


    /**
        Starts this animation over from the beginning.
    */
    public synchronized void start() {
        animTime = 0;
        currFrameIndex = 0;
    }
    
    public int getNumFrames(){
    	return frames.size();
    }
    
    public void setFrames(ArrayList f){
    	frames = f;
    }
    
    public ArrayList getFrames(){
    	return frames;
    }


    /**
        Updates this animation's current image (frame), if
        neccesary.
    */
    public synchronized void update(long elapsedTime) {
        if (frames.size() > 1) {
            animTime += elapsedTime;

            if (animTime >= totalDuration) {
                animTime = animTime % totalDuration;
                currFrameIndex = 0;
            }

            while (animTime > getFrame(currFrameIndex).endTime) {
                currFrameIndex++;
            }
        }
    }


    /**
        Gets this Animation's current image. Returns null if this
        animation has no images.
    */
    public synchronized Image getImage() {
        if (frames.size() == 0) {
            return null;
        }
        else {
            return getFrame(currFrameIndex).image;
        }
    }
    
    /**
	    Gets this Animation's current image. Returns null if this
	    animation has no images.
	*/
	public synchronized void setImage(Image i) {
	    if (frames.size() == 0) {
	        
	    }
	    else {
	    	
	        getFrame(currFrameIndex).image = i;
	    }
	}


    private AnimFrame getFrame(int i) {
        return (AnimFrame)frames.get(i);
    }



}
