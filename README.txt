________________________________________________________________________________
DynaMiT

Permission to use and distribute this software is granted under the BSD 2-Clause
License (see http://opensource.org/licenses/BSD-2-Clause). 

Copyright (c) 2013, G. Becq, Gipsa-lab, UMR 5216, CNRS; E. Denarier, Grenoble 
 Institute of Neurosciences, Inserm U 836. 
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this 
list of conditions and the following disclaimer in the documentation and/or 
other materials provided with the distribution.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
________________________________________________________________________________
________________________________________________________________________________
The README.txt is composed as follows : 
I. Introduction
II. Installation
III. Plugins Description
________________________________________________________________________________
________________________________________________________________________________
I. Introduction

This package has been realized with ImageJ 1.48a, Java 1.6.0_20 (64-bit). 
It contains a set of plugins used for the studies of the Dynamic of 
MicroTubules (DynaMiT).
Images used for these studies are image stacks containing slices corresponding 
to successive images observed during experiments. 
Each slice contains an instantanteous observation of microtubules. 
Microtubules are evolving through the slices. 
The selection of one microtubule of interest is done with a segmented line also 
called a polyline.
The evolution of the length of the microtubule on this line is plotted versus 
time to produce a kymograph, an image with length in x coordinates and time in 
y coordinates. 
The analysis of the left and right parts of the kymograph produces a set of 
measures associated to the dynamics of the microtubule during the experiment.  
Three events, polymerization, depolymerization and pause, are analyzed. 
They corresponds respectively to: increase of the length of a microtubule end;  
reduction of the length of the microtubule end; no increase or reduction of the 
length of a microtubule end. 
Transitions of the events are also analyzed: rescue, when the microtubule 
restarts a polymerization event; catastrophe, when the microtubule starts a 
depolymerization event. 
Each microtubule has two ends that do not present the same evolution. 
One is growing faster than the other and is qualified as the plus end marked 
(+). 
The slower is the minus end and it is marked (-). 
________________________________________________________________________________
________________________________________________________________________________
II. Installation

For the installation of the package: 
1. Unzip the "DynaMiT.zip" file. A "DynaMiT/" folder is created. 
2. Copy the uncompressed "DynaMiT/" folder into the plugin folder of your ImageJ 
installation. 
3. Launch ImageJ. The different plugins are accessible through menu: 
 "Plugins" > "DynaMiT" 
________________________________________________________________________________
________________________________________________________________________________
III. Plugins Description

There are 5 plugins accessible through the "plugin" menu: 
1. "DynaMiT Setup"
2. "Microtubule Image Processing"
3. "Microtubule Selection"
4. "Kymograph Processing"
5. "Kymograph Stack Processing"

................................................................................
1. "DynaMiT Setup" : 

This plugin proposes a window to enter the parameters of the experiments: 
"Data path", "Result path", "Bandwith around microtubule", "Pixel size", 
"Frame interval", "Processing method", "Threshold pause". 

"Data path" is a shortcut for accessing the microtubule data. 
"Result path" is the root directory for the results. 
A new directory will be created for each new image in this directory.    
"Bandwith around microtubule" indicates width of the line in pixels for the 
selection of microtubule during "Microtubule Selection". 
"Pixel size", "Frame interval" and "Threshold pause" are used in 
"Kymograph Processing". 
They are respectivaley: the width of one pixel in micrometers; 
the time interval between two images in seconds; 
and the threshold in micrometer per seconds used to discriminate Pause events. 
"Processing method" is a popup that proposes two methods to enhance the 
detection of microtubules: "Median - Gaussian" and "FFT". 
These methods use band filtering. 
................................................................................
................................................................................
2. "Microtubule Image Processing"

This plugin opens a window that enables the user to select the different files 
that will be processed according to the selected "processing method". 
For each file a folder with the name of the image, for example ".\img1\", is 
created in "Result path". 
Each folder contains: the processed image, postnamed with the method of the 
processing used, that will be used during the microtubule selection; an image 
of the projection on all slices, postnamed with "z_projection".  
A text file "log.txt" is also created to report the user operation and 
parameters.  
All images are saved in a "tiff" format. 
For example ".\img1\" contains : 
"log.txt", "img1_FFT.tiff", "img1_Z_projection.tiff".  

................................................................................
................................................................................
3. "Microtubule Selection"

This plugin proposed you to open a "Z_projected" image and select the different 
microtubules of interests or regions of interests (roi) with a polyline. 
Selection of the roi are put into the roi manager with the "Add (t)" button or 
with the "t" shortcut. 
The end of the selection is realized with the non blocking window by pushing 
"OK". 
The roi manager is saved as "RoiSet.zip". 
This file will be automatically reloaded for future selections or modifications
when relaunching the "Microtubule Selection" plugin. 
Three files are created: "*kymoBW.tiff", "*kymoGray.tiff", "*Microtubule.tiff".
They contain respectively: a stack of all the binarized kymographs in black and
white; a stack of all the filtered kymographs in greyscale; a stack that combine
all the microtubules in an horizontal alignment presentation. 
................................................................................
................................................................................
4. "Kymograph Processing"

This plugin must be used with an opened image containing a single kymograph. 
A window proposes to "select the left polyline". 
Click on the image to generate a segmented line or a polyline. 
A double click ends the polyline. 
Click on "OK" in the "selection of the left polyline" window to continue. 
A window proposes to "select the right polyline" and is controlled by the same 
process as for the left polyline. 
A click on "OK" opens two windows "slope" and "analyze".
The window "slope" contains from top to bottom: 
1st row, the name of the slice used for the selection; 
Coordinate values of the segmented lines (X_BEG, X_END, Y_BEG, Y_END) and the 
slope of the corresponding line (SLOPE) calculated with the following formula
(X_END - X_BEG) / (Y_END - Y_BEG).
Values are given in pixels. 
Data corresponding to the (+) ends and the (-) ends parts are separated by 
lines containing 
"***** (+) end *****" or "***** (-) end *****"

Example

X_BEG, X_END, Y_BEG, Y_END, SLOPE
1, MAX_Reslice of ROI_0051-0453
***** (+) end *****
51.00, 53.00, 1.00, 15.00, 0.14
53.00, 44.00, 15.00, 13.00, -9.00
44.00, 68.00, 13.00, 61.00, 0.50
***** (-) end *****
41.00, 8.00, 3.00, 348.00, 0.10
8.00, 15.00, 348.00, 348.00, -7.00
15.00, 14.00, 348.00, 357.00, 0.11

The window "analyze" contains one line containing the results of the analysis of
the kymograph.
Parameters of the study are taken into account with resolution in micrometers 
and time between frames in seconds. 
These parameters are noted in the "log.txt" file. 
The line contains 22 rows separated by a comma that correspond to : 
1. I_MIT, index of the slice or microtubule. 
It is one for an image with one slice.  
2. NAME, name of the slice or image. 
3. SPEED_POLY_(+), mean speed of polymerization segments for the (+) end, in 
micrometers per seconds. 
4. SPEED_DEPOLY_(+), ... depolymerization ...
5. SPEED_PAUSE_(+), ... pause ...
6. TIME_POLY_(+), duration of polymerization segments, in seconds.  
7. TIME_DEPOLY_(+), ... depolymerization ... 
8. TIME_PAUSE_(+), ... pause ...
9. TIME_TOTAL_(+), total duration, sum of time poly, depoly and pause.  
10. N_CATA_(+), number of catastrophes, in units.  
11. N_RESCUE_(+), ... rescues. 
12. N_PAUSE_(+), ... pauses. 
13. SPEED_POLY_(-), same as before for (-) end. 
14. SPEED_DEPOLY_(-), ... 
15. SPEED_PAUSE_(-), ...
16. TIME_POLY_(-), ...
17. TIME_DEPOLY_(-), ...
18. TIME_PAUSE_(-), ...
19. TIME_TOTAL_(-), ...
20. N_CATA_(-), ...
21. N_RESCUE_(-), ...
22. N_PAUSE_(-), ...

For each negative or null difference between y values in a segment, 
or saying it in another way, if Y_END - Y_BEG is null or negative, the value is 
replaced by 1 pixel. 
This avoids instantaneous growth or reduction of the microtubule and corrects 
wrong selections. 
Speeds are computed with a ponderation of the duration of segments using, by 
example for polymerization: 
$$ speed_poly = \frac{\sum_i d_{poly, i}}{\sum_i t_{poly, i}} $$ 
with $d_{poly, i}$ and $t_{poly, i}$ respectively distance and duration of 
polymerization segment i.
This is equivalent to compute : 
$$ speed_poly = Fs / resolution * sum_{i} n_{poly, i} / N_{poly} * 
slope_{poly, i} $$
with $n_{poly, i}$ the number of pixels for segment number i and 
$ N_poly = \sum_i n_{poly, i} $ the sum of polymerization pixels
and slope computed on pixels. 
$Fs = 1 / "Frame interval"$
$resolution = 1 / "Pixel size"$

Pause events are detected with the rule: 
$$ -th_pause < speed < th_pause $$

All event with duration null and speed null are not considered and considered 
as null event containing 0 everywhere.  
An "OK" with no selection or a "Cancel" click generates a null event.
Null events are not counted. 

The (+) and (-) ends are automatically detected by assigning the (+) end to the
left or right part with the higher polymerization speed. 

By default, parameters are set to "fs=1.0 resolution=1.0 thpause=0.01" for 
this plugin. Results are given in pixels. 
................................................................................
................................................................................
5. "Kymograph Stack Processing"

This plugin must be used with an opened image containing a stack of kymographs. 
This kind of image stack is produced while using "Microtubule Image Selection". 
The plugin applies "Kymograph Processing" to all slices and proposes to select 
the left and right part for each kymograph.
At the end of the selection, two files are saved on the result path "slope.csv" 
and "stat.csv" in a comma separated values format.
In the "slope.csv" file, each "slope" result are saved one after the other. 
In the "stat.csv" file, each line contains the "analyze" result of one 
kymograph. 
Results are in micrometers and seconds for this plugin. 
................................................................................
________________________________________________________________________________
