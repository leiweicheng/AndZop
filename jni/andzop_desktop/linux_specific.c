static void render_a_frame(int _width, int _height, float _roiSh, float _roiSw, float _roiEh, float _roiEw) {
    decode_a_frame(_width, _height, _roiSh, _roiSw, _roiEh, _roiEw);
    if (gVideoPicture.data.linesize[0] != 0) {
        dump_frame_to_file(gVideoPacketNum);
    }
    avpicture_free(&gVideoPicture.data);
}

void *decode_video(void *arg) {
    int l_i;
    int lRoiStW = 200, lRoiStH = 200, lRoiEdW = 600, lRoiEdH = 600; 
    for (l_i = 0; l_i < NUM_OF_FRAMES_TO_DECODE; ++l_i) {
        /*if (l_i == 10) {
            gZoomLevelUpdate = 1;
            lRoiStW = 0;
            lRoiStH = 0;
            lRoiEdW = 150;
            lRoiEdH = 700;
	} 
        if (l_i == 50) {
	    gZoomLevelUpdate = -1;
            lRoiStW = 400;
            lRoiStH = 400;
            lRoiEdW = 800;
            lRoiEdH = 800;
        }
	if (l_i == 80) {
            gZoomLevelUpdate = 3;
            lRoiStW = 40;
            lRoiStH = 50;
            lRoiEdW = 100;
            lRoiEdH = 400;
        } */
#if defined(SELECTIVE_DECODING) || defined(NORM_DECODE_DEBUG)
        render_a_frame(800, 480, lRoiStH, lRoiStW, lRoiEdH, lRoiEdW);	//decode frame
#else
        render_a_frame(800, 480, 0, 0, 100, 250);	//decode frame
#endif
    }
}

int main(int argc, char **argv) {
    int l_i;
    /*number of input parameter is less than 1*/
    if (argc < 3) {
        LOGE(1, "usage: ./ffplay [debug] <videoFilename0> <videoFilename1> ");
        return 0;
    }    
    l_i = atoi(argv[1]);
    gNumOfVideoFiles = argc-2;
    gVideoFileNameList = &argv[2];
    andzop_init(l_i);
#ifdef DECODE_VIDEO_THREAD
    if (pthread_create(&gVideoDecodeThread, NULL, decode_video, NULL)) {
        LOGE(1, "Error: failed to createa native thread for decoding video");
    } else {
        LOGI(10, "decoding thread created");
    }
#endif
#ifdef BG_DUMP_THREAD
    for (l_i = 0; l_i < gNumOfVideoFiles; ++l_i) {
        LOGI(10, "join a dep dump thread");
        pthread_join(gDepDumpThreadList[l_i], NULL);
    }
#endif
#ifdef DECODE_VIDEO_THREAD
    LOGI(10, "join decoding thread");
    pthread_join(gVideoDecodeThread, NULL);
#endif
    LOGI(10, "decoding thread finished");
    andzop_finish();
    return 0;
}
