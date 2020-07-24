package com.icatch.usbcam.engine.streaming;

import android.view.Surface;
import android.view.SurfaceHolder;

import com.icatch.usbcam.common.mode.PreviewLaunchMode;
import com.icatch.usbcam.common.type.Tristate;
import com.icatch.usbcam.sdkapi.PanoramaPreviewPlayback;
import com.icatch.usbcam.sdkapi.StreamProvider;
import com.icatchtek.baseutil.log.AppLog;
import com.icatchtek.pancam.customer.ICatchPancamConfig;
import com.icatchtek.pancam.customer.exception.IchGLSurfaceNotSetException;
import com.icatchtek.pancam.customer.stream.ICatchIStreamProvider;
import com.icatchtek.pancam.customer.surface.ICatchSurfaceContext;
import com.icatchtek.pancam.customer.type.ICatchGLPanoramaType;
import com.icatchtek.pancam.customer.type.ICatchGLSurfaceType;
import com.icatchtek.reliant.customer.type.ICatchCodec;
import com.icatchtek.reliant.customer.type.ICatchStreamParam;
import com.icatchtek.reliant.customer.type.ICatchVideoFormat;

/**
 * Created by b.jiang on 2017/9/21.
 */

public class CameraStreaming {
    private final String TAG = CameraStreaming.class.getSimpleName();
    private PanoramaPreviewPlayback previewPlayback;
    private ICatchIStreamProvider iCatchIStreamProvider;
    private StreamProvider streamProvider;
    private Surface surface;
    private SurfaceHolder holder;
    private boolean isStreaming = false;
    private H264DecoderThread h264DecoderThread;
    private MjpgDecoderThread mjpgDecoderThread;
    private ICatchVideoFormat videoFormat;
    private int frmW = 0;
    private int frmH = 0;
    private int viewWidth = 0;
    private int viewHeigth = 0;
    private int previewCodec;
    private ICatchSurfaceContext iCatchSurfaceContext;
    private RenderType renderType;
    public CameraStreaming(PanoramaPreviewPlayback previewPlayback) {
        this.previewPlayback = previewPlayback;
    }

    public boolean initSurface(ICatchSurfaceContext surfaceContext,SurfaceHolder holder, int width, int heigth, RenderType renderType) {
        this.surface = holder.getSurface();
        this.holder = holder;
        AppLog.d(TAG, "initSurface renderType:" + renderType + " holder:" + holder + " surface:" + surface + " surfaceContext:" + surfaceContext);
        this.renderType = renderType;
        boolean ret = false;
        if (renderType == RenderType.APP_RENDER) {
            if(previewPlayback != null) {
                iCatchIStreamProvider = previewPlayback.disableRender();
                if(iCatchIStreamProvider != null){
                    this.streamProvider = new StreamProvider(iCatchIStreamProvider);
                    ret = true;
                }
            }
            AppLog.i(TAG, "SurfaceViewWidth=" + width + " SurfaceViewHeight=" + heigth);
            if (width <= 0 || heigth <= 0) {
                width = 1080;
                heigth = 1920;
            }
            viewWidth = width;
            viewHeigth = heigth;
        } else if (renderType == RenderType.COMMON_RENDER) {
            if(previewPlayback != null) {
                iCatchSurfaceContext = surfaceContext;
                ret = previewPlayback.enableCommonRender(iCatchSurfaceContext);
//                previewPlayback.init(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
//                previewPlayback.setSurface(ICatchGLSurfaceType.ICH_GL_SURFACE_TYPE_SPHERE, iCatchSurfaceContext);
            }
        }
        AppLog.d(TAG, "initSurface ret: " + ret);
        return ret;
    }

    public int start(ICatchStreamParam param, boolean enableAudio) {
        AppLog.d(TAG, "startStreaming, enableAudio: " + enableAudio + " renderType:" + renderType);
        if (surface == null) {
            AppLog.e(TAG, "surface is not set");
            return -1;
        }
        if (isStreaming) {
            AppLog.d(TAG, "apv streaming already started");
            return -1;
        }
        if(previewPlayback == null){
            AppLog.e(TAG, "previewPlayback is null");
            return -1;
        }
        Tristate ret = previewPlayback.start(param, enableAudio);
        AppLog.d(TAG, "sdk start streamProvider ret =" + ret);
        if (ret != Tristate.NORMAL) {
            return -1;
        }
        AppLog.d(TAG, "sdk start streamProvider OK");
        // init decoder
        isStreaming = true;
        if (renderType == RenderType.APP_RENDER && streamProvider != null) {
            try {
                videoFormat = streamProvider.getVideoFormat();
                if (videoFormat != null) {
                    frmW = videoFormat.getVideoW();
                    frmH = videoFormat.getVideoH();
                }
                startDecoderThread(PreviewLaunchMode.RT_PREVIEW_MODE, videoFormat);
            } catch (Exception e) {
                AppLog.e(TAG, "get video format err: " + e.getMessage());
                return -1;
            }
        }
        return 0;
    }

    private void startDecoderThread(int previewLaunchMode, ICatchVideoFormat videoFormat) {
        AppLog.i(TAG, "start startDecoderThread");
        if (videoFormat == null ) {
            AppLog.e(TAG, "videoFormat is null");
            return;
        }
        if(streamProvider == null){
            AppLog.e(TAG, "streamProvider is null");
            return;
        }
        boolean enableAudio = streamProvider.containsAudioStream();
        previewCodec = videoFormat.getCodec();
        AppLog.i(TAG, "start startDecoderThread previewCodec=" + previewCodec + " enableAudio=" + enableAudio);
        switch (previewCodec) {
            case ICatchCodec.ICH_CODEC_RGBA_8888:
                mjpgDecoderThread = new MjpgDecoderThread(streamProvider, holder, previewLaunchMode, viewWidth, viewHeigth);
                mjpgDecoderThread.start(enableAudio, true);
                setSurfaceViewArea();
                break;
            case ICatchCodec.ICH_CODEC_H264:
                h264DecoderThread = new H264DecoderThread(streamProvider, surface, previewLaunchMode);
                h264DecoderThread.start(enableAudio, true);
                setSurfaceViewArea();
                break;
            default:
                return;
        }
    }

    public void setDrawingArea(int width, int height) {
        if (renderType == RenderType.COMMON_RENDER) {
            if (iCatchSurfaceContext != null) {
                AppLog.d(TAG, "start setDrawingArea width=" + width + " height=" + height);
                try {
                    iCatchSurfaceContext.setViewPort(0, 0, width, height);
                } catch (IchGLSurfaceNotSetException e) {
                    e.printStackTrace();
                }
                AppLog.d(TAG, "end setDrawingArea");
            }
        }
    }

    public boolean stop(int stopPvMode) {
        AppLog.d(TAG, "stopStreaming isStreaming = " + isStreaming);
        AppLog.i(TAG, "stopMPreview preview");
        if(!isStreaming){
            return true;
        }
        if (renderType == RenderType.APP_RENDER) {
            if (mjpgDecoderThread != null) {
                mjpgDecoderThread.stop();
                AppLog.i(TAG, "start stopMPreview mjpgDecoderThread.isAlive() =" + mjpgDecoderThread.isAlive());
            }
            if (h264DecoderThread != null) {
                h264DecoderThread.stop();
                AppLog.i(TAG, "start stopMPreview h264DecoderThread.isAlive() =" + h264DecoderThread.isAlive());
            }
        }
        // stopPreview pv streaming
        boolean ret = previewPlayback.stop(stopPvMode);
        isStreaming = false;
        AppLog.i(TAG, "end preview ret:" + ret);
        return ret;
    }


    public void destroyPreview() {
        if (previewPlayback == null) {
            return;
        }
        if (renderType == RenderType.COMMON_RENDER) {
            if (iCatchSurfaceContext != null) {
                previewPlayback.removeSurface(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE, iCatchSurfaceContext);
            }
            previewPlayback.release();
        }
        isStreaming = false;
    }

    public boolean isStreaming() {
        AppLog.d(TAG, "get getStream: " + isStreaming);
        return isStreaming;
    }

    public void setSurfaceViewArea() {
        AppLog.e(TAG, "setSurfaceViewArea viewWidth=" + viewWidth + " viewHeigth=" + viewHeigth + " frmW=" + frmW + " " +
                "frmH=" + frmH + " previewCodec=" + previewCodec);
        if (viewWidth <= 0 || viewHeigth <= 0) {
            return;
        }
        if (frmH <= 0 || frmW <= 0) {
            AppLog.e(TAG, "setSurfaceViewArea frmW or frmH <= 0!!!");
            holder.setFixedSize(viewWidth, viewWidth * 9 / 16);
            return;
        }
        if (previewCodec == ICatchCodec.ICH_CODEC_RGBA_8888) {
            if (mjpgDecoderThread != null) {
                mjpgDecoderThread.redrawBitmap(holder, viewWidth, viewHeigth);
            }
        } else if (previewCodec == ICatchCodec.ICH_CODEC_H264) {
            if (viewWidth * frmH / frmW <= viewHeigth) {
                holder.setFixedSize(viewWidth, viewWidth * frmH / frmW);
            } else {
                holder.setFixedSize(viewHeigth * frmW / frmH, viewHeigth);
            }
        }
        AppLog.d(TAG, "end setSurfaceViewArea");
    }
}
