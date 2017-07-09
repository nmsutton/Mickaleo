package mickaleo_streamer;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacv.*;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class mickaleo_streamer {


	public static void main(String[] args) throws Exception {

		run_streaming run_straming = new run_streaming();
		run_straming.run_streaming();

	}

	/*
	 * public void smooth(String filename) { IplImage image = cvLoadImage(filename);
	 * if (image != null) { cvSmooth(image, image); cvSaveImage(filename, image);
	 * cvReleaseImage(image); } }
	 */

}