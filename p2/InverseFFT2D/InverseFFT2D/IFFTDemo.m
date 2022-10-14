function IFFTDemo(filename)

if ~exist('filename', 'var') % if filename was not provided
   filename = 'images/Albert_Einstein_Nobel_s.png'; % Einstein
%    filename = 'images/lena_std128x128.png'; % Lena
%     filename = 'images/TaiyounoTou128x128.png'; % default TaiyounoTou
%     filename = 'images/barb.png'; % default image filename
end

show_log_amp = true; % flag to show log SF spectrum instead of SF spectrum
min_amp_to_show = 10 ^ -10; % small positive value to replace 0 for log SF spectrum

L = GetLuminanceImage(filename);

% calculate the number of points for FFT (power of 2)
FFT_pts = 2 .^ ceil(log2(size(L)));

[FFTedL fx fy mfx mfy] = Myff2(L, FFT_pts(1), FFT_pts(2));

mask = repmat(0, size(FFTedL)); % mask for spectrum



fig=figure(1);
set(fig,'Units','Normalized','Position',[.1 .1 .8 .8]);

clf reset;

% callback functions for figure
set(gcf, 'WindowButtonMotionFcn', @myWindowButtonMotionFcn);
set(gcf, 'WindowButtonUpFcn', @myWindowButtonUpFcn);

uicontrol('Style', 'pushbutton', 'String', 'reset', ...
    'Units', 'normalized', 'Position', [.75 .8 .15 .05], ...
    'Callback', 's = get(gcf, ''UserData''); IFFTDemo(s.filename);');

colormap gray;

% luminance image
subplot(2, 3, 1);
imagesc(L);
% colorbar;
axis square;
set(gca, 'TickDir', 'out');
title('original image');
xlabel('x');
ylabel('y');

amp = abs(FFTedL);
if show_log_amp
    amp(find(amp < min_amp_to_show)) = min_amp_to_show; % avoid taking log 0 
    amp = log10(amp);
end

% spectral amplitude
subplot(2, 3, 2);
hAmpImage = imagesc(fx, fy, amp);
axis xy;
axis square;
set(gca, 'TickDir', 'out');
title('amplitude spectrum');
xlabel('fx (cyc/pix)');
ylabel('fy (cyc/pix)');

% callback function for axes which have amplitude spectrum image
set(hAmpImage, 'ButtonDownFcn', @myButtonDownFcn);


s.hAmpImage = hAmpImage;
s.hCurrentImage = NaN;
s.filename = filename;
s.L = L;
s.mask = mask;
s.FFTedL = FFTedL;
s.amp = amp;
s.fx = fx;
s.fy = fy;
s.mouseDragging = false;
s.oldxiyi = [NaN NaN];

set(gcf, 'UserData', s);

UpdateIFFT(s);


function myButtonDownFcn(src, eventdata)

[h hFigure] = gcbo;
s = get(hFigure, 'UserData');
s.hCurrentImage = h;
s.mouseDragging = true;
set(hFigure, 'UserData', s);

s = UpdateMask(s);
s = UpdateIFFT(s);
set(hFigure, 'UserData', s);


function myWindowButtonMotionFcn(src, eventdata)

[h hFigure] = gcbo;
s = get(hFigure, 'UserData');
if s.mouseDragging
    s = UpdateMask(s);
    s = UpdateIFFT(s);
    set(hFigure, 'UserData', s);
end


function myWindowButtonUpFcn(src, eventdata)

[h hFigure] = gcbo;
s = get(hFigure, 'UserData');
s.hCurrentImage = NaN;
s.mouseDragging = false;
s.oldxiyi = [NaN NaN];
set(hFigure, 'UserData', s);


function s = UpdateMask(s)

pt = get(get(s.hCurrentImage, 'Parent'), 'CurrentPoint');
xy = pt(1, [1 2]);

% if the point is out of the axes, invalidate mouse dragging.
if xy(1) < s.fx(1) | xy(1) > s.fx(end) | xy(2) > s.fy(1) | xy(2) < s.fy(end)
    s.hCurrentImage = NaN;
    s.mouseDragging = false;
    s.oldxiyi = [NaN NaN];
    return;
end

[minc xi] = min(abs(s.fx - xy(1)));
[minc yi] = min(abs(s.fy - xy(2)));

if isnan(s.oldxiyi(1))
    xxi = xi;
    yyi = yi;
% connect old and current points
elseif xi == s.oldxiyi(1)
    yyi = min([yi s.oldxiyi(2)]): max([yi s.oldxiyi(2)]);
    xxi = xi * repmat(1, size(yyi));
else
    slope = (yi - s.oldxiyi(2)) / (xi - s.oldxiyi(1));
    if slope < 1
        xxi = min([xi s.oldxiyi(1)]): max([xi s.oldxiyi(1)]);
        yyi = round(slope * (xxi - xi) + yi);
    else
        yyi = min([yi s.oldxiyi(2)]): max([yi s.oldxiyi(2)]);
        xxi = round(1 / slope * (yyi - yi) + xi);
    end
end


for ii = 1: length(xxi)
    for k1=-1:1
        for k2=-1:1
        s.mask(yyi(ii)+k1, xxi(ii)+k2) = 1;
        s.mask(end+2-yyi(ii)-k1,end+2-xxi(ii)-k2) = 1;
        end
    end
end

% 
% for ii = 1: length(xxi)
%     s.mask(yyi(ii), xxi(ii)) = 1;
% end

s.oldxiyi = [xi yi];


function s = UpdateIFFT(s)

A = real(ifft2(ifftshift(s.mask .* s.FFTedL)));
A = A(1: size(s.L, 1), 1: size(s.L, 2));

% IFFTed luminance image
subplot(2, 3, 4);
imagesc(A);
% colorbar;
axis square;
set(gca, 'TickDir', 'out');
title('IFFTed image');
xlabel('x');
ylabel('y');

% unmasked spectral amplitude
subplot(2, 3, 5);
h = imagesc(s.fx, s.fy, s.mask .* s.amp);
axis xy;
axis square;
set(gca, 'TickDir', 'out');
title('unmasked amplitude spectrum');
xlabel('fx (cyc/pix)');
ylabel('fy (cyc/pix)');

% callback function for a newly created unmasked spectral amplitude image
set(h, 'ButtonDownFcn', @myButtonDownFcn);

% the handle of the image must be remembered if necessary
% if ~isnan(s.hCurrentImage) & s.hCurrentImage ~= s.hAmpImage
if s.hCurrentImage ~= s.hAmpImage
    s.hCurrentImage = h;
end


A = repmat(0, size(s.mask));
if ~isnan(s.oldxiyi(1))
    A(s.oldxiyi(2), s.oldxiyi(1)) = 1;
    A = real(ifft2(ifftshift(A .* s.FFTedL)));
    A = A(1: size(s.L, 1), 1: size(s.L, 2));
end

% most recent grating added
subplot(2, 3, 6);
imagesc(A);
% colorbar;
axis square;
set(gca, 'TickDir', 'out');
title('most recent grating added');
xlabel('x');
ylabel('y');
