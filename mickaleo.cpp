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

cairo_surface_t *cairo_create_x11_surface0(int x, int y)
{
    Display *dsp;
    Drawable da;
    int screen;
    cairo_surface_t *sfc;

    if ((dsp = XOpenDisplay(NULL)) == NULL)
        exit(1);
    screen = DefaultScreen(dsp);
    da = XCreateSimpleWindow(dsp, DefaultRootWindow(dsp),
        0, 0, x, y, 0, 0, 0);
    XSelectInput(dsp, da, ButtonPressMask | KeyPressMask);
    XMapWindow(dsp, da);

    sfc = cairo_xlib_surface_create(dsp, da,
        DefaultVisual(dsp, screen), x, y);
    cairo_xlib_surface_set_size(sfc, x, y);


    cairo_surface_t *surface;
    cairo_t *cr;// = cairo_create(surf);
    int scr;
    Display *display = XOpenDisplay(NULL);
    Window root = DefaultRootWindow(display);
    scr = DefaultScreen(display);
    //root = DefaultRootWindow(display);
    /* get the root surface on given displaylay */
    surface = cairo_xlib_surface_create(display, root, DefaultVisual(display, scr),
                                                    DisplayWidth(display, scr), 
                                                    DisplayHeight(display, scr));
    cairo_surface_t *image2;
    image2 = cairo_image_surface_create_from_png ("test.png");
    //w = cairo_image_surface_get_width (image2);
    //h = cairo_image_surface_get_height (image2);
    cairo_set_source_surface (cr, image2, 300, 300);
    cairo_paint (cr);

    cairo_xlib_surface_set_size(image2,300,300);
    /* right now, the tool only outputs PNG images */
    //cairo_surface_write_to_png( surface, "test.png" );
    
    /* free the memory*/
    cairo_surface_destroy(image2);

    return sfc;
}

int main()
{
   unsigned long pixel;
   unsigned char blue;
   unsigned char green;
   unsigned char red;
   XImage *image;

   cairo_create_x11_surface0(400, 400);

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
   /*CImgDisplay main_disp(VR_display,"VR Display");


   //CImgDisplay main_disp(left_eye_display,"Desktop Screenshot");
   //CImgDisplay main_disp(desktop,"Desktop Screenshot");

   while (!main_disp.is_closed() ) {
      
      //sleep
      //std::this_thread::sleep_for (std::chrono::milliseconds(16));
      //std::this_thread::sleep_for (std::chrono::milliseconds(4));

      //image->Clear();
      XDestroyImage(image);
      image = XGetImage(display,root, 0,0 , width,height,AllPlanes, ZPixmap);

      //red_mask = image->red_mask;
      //green_mask = image->green_mask;
      //blue_mask = image->blue_mask;

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
      //VR_display.display(main_disp);
   }*/



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

    cairo_surface_t *surface;
    int scr4;
    scr4 = DefaultScreen(display);
    //root = DefaultRootWindow(disp);
    /* get the root surface on given display */
    surface = cairo_xlib_surface_create(display, root, DefaultVisual(display, scr4),
                                                    DisplayWidth(display, scr4), 
                                                    DisplayHeight(display, scr4));
    /* right now, the tool only outputs PNG images */
    cairo_surface_write_to_png( surface, "test5.png" );
    /* free the memory*/
    //cairo_surface_destroy(surface);

    //cairo_surface_t *surface;
    //cairo_t *cr2;// = cairo_create(surf);
    //int scr;

    /* The only checkpoint only concerns about the number of parameters, see "Usage" */
    /*if( argc != 3) {
      fprintf(stderr, "Wrong number of parameters given \n");
      fprintf(stderr, "Usage: ./ahenk_import <display> <output file> \n");
      return 1;
    }*/
    /* try to connect to display, exit if it's NULL */
    /*disp = XOpenDisplay( argv[1] );
    if( disp == NULL ){
      fprintf(stderr, "Given display cannot be found, exiting: %s\n" , argv[1]);
      return 1;      
    }*/
    
    /*int scr;
    scr = DefaultScreen(display);
    surface2 = cairo_xlib_surface_create(display, root, DefaultVisual(display, scr),
                                                    DisplayWidth(display, scr), 
                                                    DisplayHeight(display, scr));*/
    //cairo_set_source_surface (cr2, surface, 0, 0);
    //cairo_paint (cr2);
    // right now, the tool only outputs PNG images //
    //cairo_surface_write_to_png( surface, "test3.png" );
    // free the memory//
    //cairo_surface_destroy(surface);
    int s = DefaultScreen(display);
    XSelectInput (display, root, SubstructureNotifyMask);
    width = DisplayWidth(display, s);
    height = DisplayHeight(display, s);
    /*cairo_surface_t *surf = cairo_xlib_surface_create(display, root,
                                  DefaultVisual(display, s),
                                  width, height);*/
    cairo_surface_t *surf = cairo_xlib_surface_create(display, root, DefaultVisual(display, s), 300, 300);
    cairo_t *cr = cairo_create(surf);
    //XSelectInput(display, root, ExposureMask);
    //cairo_paint(cr);
    //int              w, h;
    //cairo_surface_t *image2;
    while(1) {
      cairo_set_source_surface (cr, surf, 0, 0);
      cairo_paint(cr);
    }

    /*XEvent ev;
    while (1) {
    XNextEvent(display, &ev);
        if (ev.type == Expose) {
          //image2 = cairo_image_surface_create_from_png ("test.png");
          //w = cairo_image_surface_get_width (image2);
          //h = cairo_image_surface_get_height (image2);
          //cairo_set_source_surface (cr, image2, 300, 300);
          //cairo_surface_write_to_png( image2, "test2.png" );
         cairo_set_source_rgb(cr, 0, 0, 0);
        //draw some text
        cairo_select_font_face(cr, "serif", CAIRO_FONT_SLANT_NORMAL, CAIRO_FONT_WEIGHT_BOLD);
        cairo_set_font_size(cr, 32.0);
        cairo_set_source_rgb(cr, 0, 0, 1.0);
        cairo_move_to(cr, 10.0, 25.0);         
         cairo_show_text(cr, "usage: ./p1 <string>");
                 cairo_surface_flush(surface);
        XFlush(display);
         //cairo_set_source_surface (cr, surface, 0, 0);
            cairo_paint(cr);
        }
    }*/

    cairo_destroy(cr);
    cairo_surface_destroy(surf);
    cairo_surface_destroy(surface);
    //cairo_surface_destroy(image2);
    XCloseDisplay(display);

   return 0;
}
