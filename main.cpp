/*
Author: Nate Sutton
Copyright: 2017

This is code which captures the current display image to
reformat it in left and right eye image streams.  A
buffered image capture is planned to be streamed.

References:
Implemented code based on
https://stackoverflow.com/questions/8249669/how-do-take-a-screenshot-correctly-with-xlib
http://www.dreamincode.net/forums/topic/166837-linux-writing-our-first-x-windows-application/
https://stackoverflow.com/questions/16887897/overlaying-images-with-cimg
*/

#include <X11/Xlib.h>
#include <X11/X.h>

#include <cstdio>
#include "CImg.h"

// for sleeping
#include <thread>         // std::this_thread::sleep_for
#include <chrono>         // std::chrono::seconds

#include <cairo.h>
#include <cairo-xlib.h>

using namespace cimg_library;

int main()
{
   unsigned long pixel;
   unsigned char blue;
   unsigned char green;
   unsigned char red;
   XImage *image;

   Display *display = XOpenDisplay(NULL);
   Window root = DefaultRootWindow(display);

   XWindowAttributes gwa;

   XGetWindowAttributes(display, root, &gwa);
   /*int width = gwa.width;
   int height = gwa.height;*/
   int width = 1920;
   int height = 1080;

   image = XGetImage(display,root, 0,0 , width,height,AllPlanes, ZPixmap);

   unsigned char *array = new unsigned char[width * height * 3];
   char *array2 = new char[width * height * 3];

   unsigned long red_mask = image->red_mask;
   unsigned long green_mask = image->green_mask;
   unsigned long blue_mask = image->blue_mask;

   CImg<unsigned char> pic(array,width,height,1,3);
   CImg<char> pic2(array,width,height,1,3);

   for (int x = 0; x < width; x++) 
   {
      for (int y = 0; y < height ; y++)
      {
         pixel = XGetPixel(image,x,y);

         blue = pixel & blue_mask;
         green = (pixel & green_mask) >> 8;
         red = (pixel & red_mask) >> 16;

         pic(x,y,0) = red;
         pic(x,y,1) = green;
         pic(x,y,2) = blue;
      }
   }

   //pic.save_png("blah.png");

   printf("%ld %ld %ld\n",red_mask>> 16, green_mask>>8, blue_mask);
   printf("%d\n",pic.height());

   //const char* array2;
   //CImg<unsigned char> left_eye_display(500,500);//(pic);
   //CImg<unsigned char> right_eye_display(500,500);//(pic);
   CImg<unsigned char> left_eye_display(pic);
   CImg<unsigned char> right_eye_display(pic);
   left_eye_display = left_eye_display.get_crop((pic.width()*.5)*.29,0,0,0,pic.width()*.79,pic.height(),0,1000);
   right_eye_display = right_eye_display.get_crop((pic.width()*.5)*.41+1,0,0,0,pic.width()*.91,pic.height(),0,1000);
   int resized_screenshot_width = pic.width()*.4;
   int resized_screenshot_height = pic.height()*.8;
   left_eye_display = left_eye_display.resize(resized_screenshot_width,resized_screenshot_height,1,1);
   right_eye_display = right_eye_display.resize(resized_screenshot_width,resized_screenshot_height,1,1);
   CImgList<unsigned char> VR_display(left_eye_display, right_eye_display);
   CImgDisplay main_disp(VR_display,"VR Display");


   //CImgDisplay main_disp(left_eye_display,"Desktop Screenshot");
   //CImgDisplay main_disp(desktop,"Desktop Screenshot");

   while (!main_disp.is_closed() ) {
      
      //sleep
      //std::this_thread::sleep_for (std::chrono::milliseconds(16));
      //std::this_thread::sleep_for (std::chrono::milliseconds(4));

      //image->Clear();
      XDestroyImage(image);
      image = XGetImage(display,root, 0,0 , width,height,AllPlanes, ZPixmap);

      /*red_mask = image->red_mask;
      green_mask = image->green_mask;
      blue_mask = image->blue_mask;*/

      for (int x = 0; x < width; x++) 
      {
         for (int y = 0; y < height ; y++)
         {
            if (XGetPixel(image,x,y) != pixel) {
               pixel = XGetPixel(image,x,y);
            }
               if (red != (pixel & red_mask) >> 16) {
               red = (pixel & red_mask) >> 16;
               }
                  if (pic(x,y,0) != red) {
                  pic(x,y,0) = red;
                  }
               //}
               
               
               if (green != (pixel & green_mask) >> 8) {
               green = (pixel & green_mask) >> 8;
               }
                  if (pic(x,y,1) != green) {
                  pic(x,y,1) = green;
                  }
               //}
                        
               if (blue != pixel & blue_mask) {
               blue = pixel & blue_mask;
               }
                  if (pic(x,y,2) != blue) {
                  pic(x,y,2) = blue;
                  }
               //} 
         }
      }

      left_eye_display.clear();
      right_eye_display.clear();
      left_eye_display.assign(pic);
      right_eye_display.assign(pic);
      left_eye_display = left_eye_display.get_crop((pic.width()*.5)*.29,0,0,0,pic.width()*.79,pic.height(),0,4);
      right_eye_display = right_eye_display.get_crop((pic.width()*.5)*.41+1,0,0,0,pic.width()*.91,pic.height(),0,4);
      left_eye_display = left_eye_display.resize(resized_screenshot_width,resized_screenshot_height,1,4);
      right_eye_display = right_eye_display.resize(resized_screenshot_width,resized_screenshot_height,1,4);
      VR_display.clear();
      VR_display.assign(left_eye_display, right_eye_display);
      VR_display.display(main_disp);
   }

   /*
   other window section
   */

    Display                 *display2;
    Visual                  *visual;
    int                     depth;
    int                     text_x;
    int                     text_y;
    XSetWindowAttributes    frame_attributes;
    Window                  frame_window;
    XFontStruct             *fontinfo;
    XGCValues               gr_values;
    GC                      graphical_context;
    XEvent               event;
    char                    hello_string[] = "Hello World";
    int                     hello_string_length = strlen(hello_string);

    display2 = XOpenDisplay(NULL);
    visual = DefaultVisual(display2, 0);
    depth  = DefaultDepth(display2, 0);

    frame_attributes.background_pixel = XWhitePixel(display2, 0);

    /* create the application window */
    frame_window = XCreateWindow(display2, XRootWindow(display2, 0),
                                 0, 0, 400, 400, 5, depth,
                                 InputOutput, visual, CWBackPixel,
                                 &frame_attributes);
    XStoreName(display2, frame_window, "Hello World Example");

    XSelectInput(display2, frame_window, ExposureMask | StructureNotifyMask);
    fontinfo = XLoadQueryFont(display2, "10x20");
    gr_values.font = fontinfo->fid;
    gr_values.foreground = XBlackPixel(display2, 0);
    graphical_context = XCreateGC(display2, frame_window,
                                  GCFont+GCForeground, &gr_values);
    XMapWindow(display2, frame_window);

    /*while ( 1 ) {
        XNextEvent(display2, (XEvent *)&event);
        switch ( event.type ) {
            case Expose:
            {
                XWindowAttributes window_attributes;
                int font_direction, font_ascent, font_descent;
                XCharStruct text_structure;
                XTextExtents(fontinfo, hello_string, hello_string_length,
                             &font_direction, &font_ascent, &font_descent,
                             &text_structure);
                XGetWindowAttributes(display2, frame_window, &window_attributes);
                text_x = (window_attributes.width - text_structure.width)/2;
                text_y = (window_attributes.height -
                          (text_structure.ascent+text_structure.descent))/2;
                //XDrawString(display2, frame_window, graphical_context,
                //            text_x, text_y, hello_string, hello_string_length);
                //XDrawImage(display2, frame_window, graphical_context,
                //            text_x, text_y, left_eye_display, hello_string_length);
                //left_eye_display.draw_image(display2);

                //CImgDisplay main_disp(pic2, array2.c_str() );
                //CImgDisplay main_disp(pic,"Desktop Screenshot");
                //CImgDisplay main_disp(desktop,"Desktop Screenshot");


                break;
            }
            default:
                break;
        }
    }*/

   return 0;
}
