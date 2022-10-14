00_README.txt

Two-Dimensional Fourier Image Reconstruction (Inverse FT) Demo using Matlab

Authors: Kota S. Sasaki and Izumi Ohzawa
Graduate School of Frontier Biosciences, Osaka University
kota@fbs.osaka-u.ac.jp, ohzawa@fbs.osaka-u.ac.jp

License: BSD license, http://creativecommons.org/licenses/BSD/

Modification Date: 2009-05-01

Modification Date: 2016-08-19
     IO - added 2 more images.
          Near Line 184Å@removed "~isnan(s.hCurrentImage) &" for fix for newer Matlab.
		  (Since R2014b, image handle type became object from double?)

------------------------------------------


IFFTDemo.m:

This Matlab script demonstrates the well-known fact that any arbitrary image
is a superposition of sine waves. This may be hard to believe for lay people
without a good demo.

Just run IFFTDemo.m. It first computes the Fourier transform of an image
using 2-dimensional FFT. The original image and its amplitude spectrum
are displayed in the top half of the window.

The bottom half has three image areas. Click anywhere within the bottom-center
image area.  This is a standard Fourier domain with the DC component located
at the center. It is used as a "picker" for selecting a Fourier component to
add to the current reconstructed image, which is displayed at bottom-left.
Each time the mouse pointer touches a new pixel in the picker area, the
corresponding Fourier component is added. The most recent Fourier component
added is diplayed in the image area at bottom right. In this last component
display, the contrast of the grating is normalized to 1 and does not
represent the actual contrast of the sine wave added. Otherwise,
most sine wave components are not visible, because most Fourier components
of typical images have very low amplitude, i.e., contrast.

It is not necessary to click for each component. You may hold down the mouse
button and continuously scribble on the picker area.

Button at top right of the window resets the IFT results and associated
display areas.

You may easily customize the script. The default Einstein image may be
replaced by editing the top section of the script.


Window explanation (See screen capture image in: results/IFFT2D-window.png):

Top left: Original image (gray scaled if color)
Top middle: Amplitude spectrum (logarithmically scaled amplitude)
Bottom left: Reconstructed (synthesized) image from mouse-selected Fourier components
Bottom middle: "Picker" for Fourier components to add to the image at bottom left.
				Image here shows all added components.
Bottom right: The last sine wave added.


GetLuminanceImage.m:

Reads in an image (may be color) and converts it into a gray scale image
for computation.

Myff2.m:

Custimized version of 2D FFT for better handling.


