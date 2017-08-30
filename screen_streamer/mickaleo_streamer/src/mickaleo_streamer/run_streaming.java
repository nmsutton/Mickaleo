package mickaleo_streamer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.indexer.Indexer;
import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;

public class run_streaming {

	Mat mat_to_send;
	int WIDTH=1024;
	int HEIGHT=786;
	//IplImage ipl_to_send;
	
	public void set_frame(Mat image) {
		//mat_to_send = mat;
		mat_to_send = image;
	}
	
	public Mat get_frame() {
		return mat_to_send;
	}

	public void run_streaming() {
		/*
		 * new Thread() { public void run() { // grab screen
		 * 
		 * int x = 0, y = 0, w = 1024, h = 768; // specify the region of screen to grab
		 * FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(":0.0+" + x + "," + y);
		 * AndroidFrameConverter converterToBitmap = new AndroidFrameConverter();
		 * OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
		 * Frame grabber_frame; Mat mat;
		 * 
		 * grabber.setFormat("x11grab"); grabber.setImageWidth(w);
		 * grabber.setImageHeight(h); try { grabber.start(); } catch
		 * (org.bytedeco.javacv.FrameGrabber.Exception e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); }
		 * 
		 * CanvasFrame frame = new CanvasFrame("Screen Capture");
		 * 
		 * while (frame.isVisible()) { try { grabber_frame = grabber.grab(); mat =
		 * converterToMat.convert(grabber_frame); // mat.
		 * frame.showImage(grabber.grab()); // System.out.println(grabber.grab()); }
		 * catch (org.bytedeco.javacv.FrameGrabber.Exception e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 * 
		 * } frame.dispose(); try { grabber.stop(); } catch
		 * (org.bytedeco.javacv.FrameGrabber.Exception e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); } } }.start();
		 */

		/* transfer data through network */
		new Thread() {
			public void run() {
				ServerSocket serverSocket = null;
				Socket socket = null;
				DataInputStream dataInputStream = null;
				BufferedReader is = null;
				String accel_x_str, accel_y_str, accel_z_str;
				String magnet_x_str, magnet_y_str, magnet_z_str = null;
				float accel_x, accel_y, accel_z;
				float magnet_x, magnet_y, magnet_z = 0;
				float mouse_x, mouse_y;
				float x_tracking_scaling = 0.0F;
				float x_tracking_offset = 3F, y_tracking_offset = -4F;
				float mouse_x_scaling = 0.025F, mouse_y_scaling = 0.025F;
				float mouse_x_offset = 932F, mouse_y_offset = 533F;
				Boolean logging_active = false;
				try {

					serverSocket = new ServerSocket(8888);
					System.out.println("Listening :8888");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				while (true) {
					try {
						socket = serverSocket.accept();

						dataInputStream = new DataInputStream(socket.getInputStream());
						is = new BufferedReader(new InputStreamReader(dataInputStream));
						if (logging_active) {
							System.out.println("ip: " + socket.getInetAddress());
						}

						// System.out.println("message: " + dataInputStream.readUTF());
						accel_x_str = is.readLine();
						// System.out.println(accel_x_str);
						accel_x = Float.valueOf(accel_x_str.split("\\.")[0].replaceAll("[^0-9-]", "") + "."
								+ accel_x_str.split("\\.")[1].replaceAll("[^0-9]", ""));// is.readLine();//Float.valueOf(is.readLine().replaceAll("[^0-9.]",
																						// ""));//.replace('!',
																						// '+').replace(')',
																						// '+').replace('/',
																						// '+').replace('(', '+'));
						accel_y_str = is.readLine();
						accel_y = Float.valueOf(accel_y_str.split("\\.")[0].replaceAll("[^0-9]-", "") + "."
								+ accel_y_str.split("\\.")[1].replaceAll("[^0-9]", ""));// accel_z =
																						// Float.valueOf(is.readLine().replace('!',
																						// '+'));
						accel_z_str = is.readLine();
						// System.out.println(accel_z_str);
						accel_z = Float.valueOf(accel_z_str.split("\\.")[0].replaceAll("[^0-9]-", "") + "."
								+ accel_z_str.split("\\.")[1].replaceAll("[^0-9]", ""));
						magnet_x_str = is.readLine();
						magnet_x = Float.valueOf(magnet_x_str.split("\\.")[0].replaceAll("[^0-9]-", "") + "."
								+ magnet_x_str.split("\\.")[1].replaceAll("[^0-9]", ""));
						magnet_y_str = is.readLine();
						magnet_y = Float.valueOf(magnet_y_str.split("\\.")[0].replaceAll("[^0-9]-", "") + "."
								+ magnet_y_str.split("\\.")[1].replaceAll("[^0-9]", ""));
						magnet_z_str = is.readLine();
						// if (magnet_z_str != null) {} else {
						if (magnet_z_str != null && magnet_z_str.split("\\.")[0] != (null)
								&& magnet_z_str.split("\\.")[1] != (null)) {
							magnet_z = Float.valueOf(magnet_z_str.split("\\.")[0].replaceAll("[^0-9]-", "") + "."
									+ magnet_z_str.split("\\.")[1].replaceAll("[^0-9]", ""));
						}
						// magnet_x = Float.valueOf(is.readLine().replace('!', '+'));
						// magnet_y = Float.valueOf(is.readLine().replace('!', '+'));
						// magnet_z = Float.valueOf(is.readLine().replace('!', '+'));
						x_tracking_scaling = (14F / 45F) * .75F;
						mouse_x = mouse_x_offset + (1080F
								* (mouse_x_scaling * (1 - ((x_tracking_offset + magnet_y + (x_tracking_scaling * 45F))
										/ (x_tracking_scaling * 90F)))));
						mouse_y = mouse_y_offset
								+ (1920F * (mouse_y_scaling * (y_tracking_offset + accel_z + 9.81F) / 19.62F));

						if (mouse_x > 1919) {
							mouse_x = 1919;
						}
						if (mouse_x < 1) {
							mouse_x = 1;
						}
						if (mouse_y > 1079) {
							mouse_y = 1079;

						}
						if (mouse_y < 1) {
							mouse_y = 1;
						}
						if (logging_active) {
							System.out.println(accel_x + "\t" + accel_y + "\t" + accel_z + "\t" + magnet_x + "\t"
									+ magnet_y + "\t" + magnet_z + "\tx:\t" + mouse_x + "\ty:\t" + mouse_y);
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						if (socket != null) {
							try {
								socket.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						if (dataInputStream != null) {
							try {
								dataInputStream.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						try {
							socket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}.start();

		new Thread() {
			public void run() {
				ServerSocket serverSocket_out = null;
				Socket socket_out = null;
				DataOutputStream dataOutputStream = null;
				BufferedWriter dataOutputStream2 = null;
				ObjectOutputStream objectOutputStream = null;
				int transfer_segments;

				try {

					serverSocket_out = new ServerSocket(8887);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				while (true) {
					try {
						socket_out = serverSocket_out.accept();

						dataOutputStream = new DataOutputStream(socket_out.getOutputStream());
						//dataOutputStream.writeUTF("Hello!\n");
						
						// export all pixel data here
						transfer_segments = (WIDTH*HEIGHT)/50000;
						for (int i = 0; i < transfer_segments; i++) {
							dataOutputStream.writeUTF(get_frame().createIndexer().toString());
						}
						dataOutputStream.flush();
						dataOutputStream.close();
						
						/*objectOutputStream = new ObjectOutputStream(socket_out.getOutputStream());
						objectOutputStream.writeObject(get_frame());
						objectOutputStream.flush();
						objectOutputStream.close();	*/					
						
						/*
						 * dataOutputStream2 = new BufferedWriter(new
						 * OutputStreamWriter(socket.getOutputStream()));
						 * dataOutputStream2.write("Hello2"); dataOutputStream2.newLine();
						 * dataOutputStream2.flush(); dataOutputStream2.close();
						 */

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						if (socket_out != null) {
							try {
								socket_out.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						if (dataOutputStream != null) {
							try {
								dataOutputStream.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					try {
						socket_out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
		}.start();

		/* grab screen */

		int x = 0, y = 0; // specify the region of screen to grab
		FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(":0.0+" + x + "," + y);
		AndroidFrameConverter converterToBitmap = new AndroidFrameConverter();
		OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
		Frame grabber_frame;
		Mat mat;
		//IplImage image = IplImage.create(WIDTH, HEIGHT, 8, 4);

		grabber.setFormat("x11grab");
		grabber.setImageWidth(WIDTH);
		grabber.setImageHeight(HEIGHT);
		try {
			grabber.start();
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		CanvasFrame frame = new CanvasFrame("Screen Capture");
		while (frame.isVisible()) {
			try {
				grabber_frame = grabber.grab();
				mat = converterToMat.convert(grabber_frame);
				//image = new IplImage(mat);
				//bitmap = converterToBitmap.convert(frame);
				set_frame(mat);
				// mat.
				frame.showImage(grabber.grab());
				//System.out.println(mat.createIndexer().toString());
				/*System.out.println("hello "+mat.createIndexer().toString().substring(0, 500));
				System.out.print("\n");				*/
				Indexer frame_arr = mat.createIndexer();
				//String frame_arr_str = frame_arr.toString();
				//List<String> frame_pixels = Arrays.asList(frame_arr_str.split(","));
				//ArrayList frame_pixels2 = (ArrayList) (Arrays.asList(frame_arr_str.split(","))).split(",");
				//ArrayList<List<String>> frame_total_array = (ArrayList<List<String>>) frame_pixels;
				//Arrays.
				//ArrayList<ArrayList<Integer>> frame_total_array = (ArrayList<Integer>) frame_pixels;
				// ArrayList<int[]> myList = new ArrayList<int[]>((int[]) Arrays.asList(frame_arr_str.split(",")));
				//System.out.print(frame_arr.index(1));
				//System.out.print("\n");
				Mat m = mat;
				int count = (int) m.total() * m.channels();
				// depending on Mat data type choose byte, short, int, float or double
				byte[] buff = new byte[count];
				
				//m.get(0, 0, buff);

				// on the other side
				Mat z = new Mat(/* the same size and type */);
				z.put(0, 0, buff);
			} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		frame.dispose();
		try {
			grabber.stop();
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
