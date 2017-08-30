# Mickaleo

This project (code name: Mickaleo, pronunciation: mick-ah-leo) is designed to add at least basic VR to any program on Linux with any VR headset including phone and tablet headset kits. Eventually it could be a cross platform program to add VR to any program with any headset.

## Software Sections and Status:

1.  Streamer:
<br>www.splashtop.com/personal
<br>www.splashtop.com/wiredxdisplay
<br>The device currently used for testing VR streaming is a Durovis Dive 7 headset with Nexus 7 2cnd Gen tablet. Any remote desktop software is compatible but I recommend Splashtop. Also, run this over your local area network, not streaming over the internet for performance gains and avoiding excess internet data usage. Splashtop wireless is free for personal use and specifically designed for high performance video game level quality streaming. It works but should be used for personal use, if you want to use it professionally, please support them. Splashtop wired connection (XDisplay) software can mirror a display at 1080p 60fps. 

2.  VR Formatter:
<br>/cpp_screen_streamer/ 
<br>Currently this C++ code provides realtime mirror of one Linux monitor display to another monitor in a VR format. It is in alpha stage development, it has limitations such as needing to exit it from the taskmanager, no shortcut to close at the moment. Usage: Open a window to stream or stream a full monitor display of the current OS session. Run the executable, which only works at 1080p unless a code modification is done. A window opens duplicating the display for the left and right eye with a divider between them. Currently offsets specific to the Durovis Dive are configured but those can be altered in the code, a GUI interface can be developed for that in the future. This is designed to work well with a dual monitor system where one screen can be converted into a VR display and the other a normal display. Future subgoal: have profiles able to be saved for headsets. Create an online repository where people can share profiles and descriptions of them.
<br>/sublime_build/ - Example sublime build settings.

3.  Mouse Interactivity:
<br>/desktop_client_mouse_controller/ - pc client
<br>/mobile_app_position_tracker/ - android server
<br>Currently this runs in Linux as a client that receives mouse control commands. The Android app is a server that transmits movement data to control the mouse. Future potential work: create mouse control on the client in only a window instead of a whole display if wanted. The sensitivity of the movements can be set in the client code but a GUI can be made for that later. The mouse pointer is recentered on screen to accommodate game control. In the app pressing the button for enabling sensor transmission causes activation of mouse control and the disable button stops it. The Android app was built using Android Studio.

4.  Other Work in Progress Folders:
<br>/screen_streamer/ - pc server
<br>Work toward a Java based screen streaming server for more of an all-in-one solution. Currently not working but contains development code. This was worked on to avoid having to use splashtop or something separately when running the mouse control, and being able to stream directly to a headset instead of mirroring one display on another. This project is build using Eclipse Java edition.
<br>/MickaleoApp2/ - android client
<br>Work toward a Java based screen streaming app for receiving content from the server.
<br>/alpha_release/ - software for development testing
<br>These are versions of the programs that should work, further testing is needed before declaring them ready for official alpha release. The java client runs successfully in the Eclipse IDE but when exported as a runnable jar a message about 'missing main class' occurs and it is being looked into how to resolve that. The other programs appear to run successfully.

5.  Optional future work: Create gamepad or VR accessory interactivity with the software running so that mouse and keyboard, etc. do not need to be used.

## Dependencies:
For X11 development in the cpp_screen_streamer software:
<br>xorg-dev
<br>libx11-dev

