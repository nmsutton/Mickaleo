/*
Author: Nate Sutton, 2017

References:
https://stackoverflow.com/questions/24988164/c-fast-screenshots-in-linux-for-use-with-opencv
https://stackoverflow.com/questions/13133055/opencv-displaying-2-images-adjacently-in-the-same-window
*/

// g++ xshm2.c -o xshm2 -lX11 -lXext `$cv`-Ofast -mfpmath=both -march=native -m64 -funroll-loops -mavx2 && ./xshm2

#include <X11/Xlib.h>
#include <X11/Xutil.h>

#include <X11/extensions/XShm.h>
#include <sys/ipc.h>
#include <sys/shm.h>

#include <opencv2/opencv.hpp>  // This includes most headers!
//#include <highgui.h>

#include <time.h>
#define FPS(start) (CLOCKS_PER_SEC / (clock()-start))

#include <stdio.h>
#include <string.h>    //strlen
#include <string>
#include <cstring>
#include <sys/socket.h>
#include <arpa/inet.h> //inet_addr
#include <unistd.h>    //write

using namespace cv;

// Using one monitor DOESN'T improve performance! Querying a smaller subset of the screen DOES
const uint WIDTH  = 1920>>0;
const uint HEIGHT = 1080>>0;

#define MAXRCVLEN 500
#define PORTNUM 2300

// -------------------------------------------------------
int main(){
    int socket_desc;
    struct sockaddr_in server;
    std::string message_string;
    char *message;

    /*********
     
    //Create socket
    socket_desc = socket(AF_INET , SOCK_STREAM , 0);
    if (socket_desc == -1)
    {
        printf("Could not create socket");
    }
         
    //server.sin_addr.s_addr = inet_addr("10.0.0.139");
    server.sin_addr.s_addr = htonl(INADDR_LOOPBACK); // set destination IP number - localhost, 127.0.0.1
    server.sin_family = AF_INET;
    server.sin_port = htons( 8888 );
 
    //Connect to remote server
    if (connect(socket_desc , (struct sockaddr *)&server , sizeof(server)) < 0)*******/

   char buffer[MAXRCVLEN + 1]; /* +1 so we can add null terminator */
   int len, mysocket;
   struct sockaddr_in dest; 
 
   mysocket = socket(AF_INET, SOCK_STREAM, 0);
  
   memset(&dest, 0, sizeof(dest));                /* zero the struct */
   dest.sin_family = AF_INET;
   dest.sin_addr.s_addr = htonl(INADDR_LOOPBACK); /* set destination IP number - localhost, 127.0.0.1*/ 
   dest.sin_port = htons(PORTNUM);                /* set destination port number */
 
 connect(mysocket, (struct sockaddr *)&dest, sizeof(struct sockaddr_in));
  
   len = recv(mysocket, buffer, MAXRCVLEN, 0);
 
   /* We have to null terminate the received data ourselves */
   buffer[len] = '\0';
 
   printf("Received %s (%d bytes).\n", buffer, len);
 
   close(mysocket);
   //if (connect(mysocket, (struct sockaddr *)&dest, sizeof(struct sockaddr_in)))
    {
        puts("connect error");
        return 1;
    }
     
    puts("Connected\n");
     
    //Send some data
    message_string = "GET / HTTP/1.1\r\n\r\n";
    message = new char[message_string.size() + 1];
    memcpy(message, message_string.c_str(), message_string.size() + 1);
    if( send(socket_desc , message , strlen(message) , 0) < 0)
    {
        puts("Send failed");
        return 1;
    }
    puts("Data Sent\n");

    Display* display = XOpenDisplay(NULL);
    Window root = DefaultRootWindow(display);  // Macro to return the root window! It's a simple uint32
    XWindowAttributes window_attributes;
    XGetWindowAttributes(display, root, &window_attributes);
    Screen* screen = window_attributes.screen;
    XShmSegmentInfo shminfo;
    XImage* ximg = XShmCreateImage(display, DefaultVisualOfScreen(screen), DefaultDepthOfScreen(screen), ZPixmap, NULL, &shminfo, WIDTH, HEIGHT);

    shminfo.shmid = shmget(IPC_PRIVATE, ximg->bytes_per_line * ximg->height, IPC_CREAT|0777);
    shminfo.shmaddr = ximg->data = (char*)shmat(shminfo.shmid, 0, 0);
    shminfo.readOnly = False;
    if(shminfo.shmid < 0)
        puts("Fatal shminfo error!");;
    Status s1 = XShmAttach(display, &shminfo);
    printf("XShmAttach() %s\n", s1 ? "success!" : "failure!");

    cv::Mat img;
    cv::Mat screenstream_resized;
    cv::Mat left_eye;
    cv::Mat right_eye;
    cv::Size left_eye_size = left_eye.size();
    cv::Size right_eye_size = right_eye.size();    
    left_eye_size.height = HEIGHT;
    left_eye_size.width = WIDTH*.45;
    right_eye_size.height = HEIGHT;
    right_eye_size.width = WIDTH*.45;
    //cv::Mat vr_display(left_eye_size.height, left_eye_size.width + right_eye_size.width, CV_8UC4);
    cv::Mat vr_display(HEIGHT, WIDTH, CV_8UC4);
    double scaling_factor = 1.0;//.9; // resize
    int left_eye_width_offset = (WIDTH*.16); // shift
    int left_eye_height_offset = 0;//(HEIGHT*.1);
    int right_eye_width_offset = (WIDTH*.34);
    int right_eye_height_offset = 0;//(HEIGHT*.1);
    int width_border_offset = (WIDTH*0.05); // shift
    int height_border_offset = 0;//(HEIGHT*0.06); 
    cv::Rect left_eye_region(left_eye_width_offset, left_eye_height_offset, left_eye_size.width,left_eye_size.height); //crop
    cv::Rect right_eye_region(right_eye_width_offset, right_eye_height_offset, right_eye_size.width,right_eye_size.height);
    //vr_display.height = left_eye_size.height;
    //vr_display.width = left_eye_size.width + right_eye_size.width;

    /*for(int i; ; i++){
        double start = clock();

        XShmGetImage(display, root, ximg, 0, 0, 0x00ffffff);
        img = cv::Mat(HEIGHT, WIDTH, CV_8UC4, ximg->data);
        cv::imshow("img", img);

        //if(!(i & 0b111111))
        //    printf("fps %4.f  spf %.4f\n", FPS(start), 1 / FPS(start));
        //break;
    }*/

    /**************

    while(1) {
    XShmGetImage(display, root, ximg, 0, 0, 0x00ffffff);
    img = cv::Mat(HEIGHT, WIDTH, CV_8UC4, ximg->data);
    //r = 100.0 / image.shape[1]
    //dim = (100, int(image.shape[0] * r))
    cv::resize(img, screenstream_resized, cv::Size(WIDTH*scaling_factor, HEIGHT*scaling_factor), 0, 0, cv::INTER_AREA);
    //cv::resize(img, left_eye, cv::Size(700, 500), 0, 0, cv::INTER_AREA);
    //cv::resize(img, right_eye, cv::Size(700, 500), 0, 0, cv::INTER_AREA);
    //cv::resize(img, left_eye, cv::Size(WIDTH*scaling_factor, HEIGHT*scaling_factor), 0, 0, cv::INTER_AREA);
    //cv::resize(img, right_eye, cv::Size(WIDTH*scaling_factor, HEIGHT*scaling_factor), 0, 0, cv::INTER_AREA);
    //left_eye = cv::cvCreateImage();
    //Mat im3(sz1.height, sz1.width+sz2.width, CV_8UC3);
    //cv::Mat left(vr_display, cv::Rect(0, 0, left_eye_size.width, left_eye_size.height));
    //left_eye.copyTo(left);
    //cv::Mat right(vr_display, cv::Rect(left_eye_size.width, 0, right_eye_size.width, right_eye_size.height));
    //right_eye.copyTo(right);
    left_eye = screenstream_resized(left_eye_region);
    right_eye = screenstream_resized(right_eye_region);
    left_eye.copyTo(vr_display(cv::Rect(width_border_offset, height_border_offset, left_eye_size.width, left_eye_size.height)));
    right_eye.copyTo(vr_display(cv::Rect(width_border_offset+left_eye_size.width, height_border_offset, right_eye_size.width, right_eye_size.height)));
    //resize(vr_display, vr_display, Size(1920, 1080));
    
    cvNamedWindow("VR Display", WINDOW_NORMAL);
    cvSetWindowProperty("VR Display", CV_WND_PROP_FULLSCREEN, CV_WINDOW_FULLSCREEN);
    cv::imshow("VR Display", vr_display);
    //cvShowImage("VR Display", vr_display);
    cv::waitKey(1);
    }
    ***********************/    

    XShmDetach(display, &shminfo);
    XDestroyImage(ximg);
    shmdt(shminfo.shmaddr);
    XCloseDisplay(display);
    puts("Exit success!");

}