package mickaleo_streamer;

//import static org.bytedeco.javacpp.opencv_core.*;
//import static org.bytedeco.javacpp.opencv_imgproc.*;
//import static org.bytedeco.javacpp.avutil.*;

//import org.bytedeco.javacv.CanvasFrame;
//import org.bytedeco.javacv.FFmpegFrameGrabber;
//import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.*;

//import static org.bytedeco.javacpp.opencv_imgcodecs.*;
//import static org.bytedeco.javacv.*;

public class mickaleo_streamer {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("hello world");
		
        int x = 0, y = 0, w = 1024, h = 768; // specify the region of screen to grab
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(":0.0+" + x + "," + y);
        grabber.setFormat("x11grab");
        grabber.setImageWidth(w);
        grabber.setImageHeight(h);
        grabber.start();

        CanvasFrame frame = new CanvasFrame("Screen Capture");
        while (frame.isVisible()) {
            frame.showImage(grabber.grab());
			
        }
        frame.dispose();
        grabber.stop();
	}
	
    /*public void smooth(String filename) { 
        IplImage image = cvLoadImage(filename);
        if (image != null) {
            cvSmooth(image, image);
            cvSaveImage(filename, image);
            cvReleaseImage(image);
        }
    }*/

}