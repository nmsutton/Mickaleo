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
*/

#include <X11/Xlib.h>
#include <X11/X.h>

#include <cstdio>
#include "CImg.h"

using namespace cimg_library;

int main()
{
   Display *display = XOpenDisplay(NULL);
   Window root = DefaultRootWindow(display);

   XWindowAttributes gwa;

   XGetWindowAttributes(display, root, &gwa);
   int width = gwa.width;
   int height = gwa.height;


   XImage *image = XGetImage(display,root, 0,0 , width,height,AllPlanes, ZPixmap);

   unsigned char *array = new unsigned char[width * height * 3];
   char *array2 = new char[width * height * 3];

   unsigned long red_mask = image->red_mask;
   unsigned long green_mask = image->green_mask;
   unsigned long blue_mask = image->blue_mask;

   CImg<unsigned char> pic(array,width,height,1,3);
   CImg<char> pic2(array,width,height,1,3);

   for (int x = 0; x < width; x++)
      for (int y = 0; y < height ; y++)
      {
         unsigned long pixel = XGetPixel(image,x,y);

         unsigned char blue = pixel & blue_mask;
         unsigned char green = (pixel & green_mask) >> 8;
         unsigned char red = (pixel & red_mask) >> 16;

         array[(x + width * y) * 3] = red;
         array[(x + width * y) * 3+1] = green;
         array[(x + width * y) * 3+2] = blue;

         pic(x,y,0) = red;
         pic(x,y,1) = green;
         pic(x,y,2) = blue;

         /*long pixel2 = XGetPixel(image,x,y);

         char blue2 = pixel2 & blue_mask;
         char green2 = (pixel2 & green_mask) >> 8;
         char red2 = (pixel2 & red_mask) >> 16;

         array2[(x + width * y) * 3] = red2;
         array2[(x + width * y) * 3+1] = green2;
         array2[(x + width * y) * 3+2] = blue2;         

         pic2(x,y,0) = red2;
         pic2(x,y,1) = green2;
         pic2(x,y,2) = blue2;  */
      }

   //CImg<unsigned char> pic(array,width,height,1,3);
   pic.save_png("blah.png");

   printf("%ld %ld %ld\n",red_mask>> 16, green_mask>>8, blue_mask);
   printf("%d\n",pic.height());

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

    //const char* array2;
    CImg<unsigned char> left_eye_display(pic);
    CImg<unsigned char> right_eye_display(pic);
    left_eye_display = left_eye_display.get_crop(0,0,0,1,pic.width()*.5,pic.height(),0,1);
    right_eye_display = right_eye_display.get_crop((pic.width()*.5)+1,0,0,1,pic.width(),pic.height(),0,1);
    int resized_screenshot_width = pic.width()*.4;
    int resized_screenshot_height = pic.height()*.8;
    left_eye_display = left_eye_display.resize(resized_screenshot_width,resized_screenshot_height,1,1);
    right_eye_display = right_eye_display.resize(resized_screenshot_width,resized_screenshot_height,1,1);
    CImgList<unsigned char> VR_display(left_eye_display, right_eye_display);
    CImgDisplay main_disp(VR_display,"VR Display");
    //CImgDisplay main_disp(left_eye_display,"Desktop Screenshot");
    //CImgDisplay main_disp(desktop,"Desktop Screenshot");

    while ( 1 ) {
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
    }

   return 0;
}
