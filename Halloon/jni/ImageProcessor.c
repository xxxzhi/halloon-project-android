/**
 * a simple test for create Java Object in C
 *
 * added image process methods
 *
 * by airbus(caifangmao8@gmail.com)
 */
#include <jni.h>
#include <string.h>

#include <android/bitmap.h>
#include <android/log.h>

#define LOG_TAG "halloon-jni"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

typedef struct{
	uint8_t alpha;
	uint8_t red;
	uint8_t green;
	uint8_t blue;
}argb;

//create Java Object in C & return it
JNIEXPORT jobject JNICALL Java_com_halloon_android_util_GifDecoder_init(JNIEnv * env, jobject obj){
	jclass jTweetBeanClass = (*env)->FindClass(env, "com/halloon/android/bean/TweetBean");

	jmethodID jTweetBeanConstructorID = (*env)->GetMethodID(env, jTweetBeanClass, "<init>", "()V");
	jmethodID jTweetBeanSetNameID = (*env)->GetMethodID(env, jTweetBeanClass, "setName", "(Ljava/lang/String;)V");

	jobject jTweetBeanObject = (*env)->NewObject(env, jTweetBeanClass, jTweetBeanConstructorID);
	jstring jstr = NULL;
	jstr = (*env)->NewStringUTF(env, "name_from_c");
	(*env)->CallVoidMethod(env, jTweetBeanObject, jTweetBeanSetNameID, jstr);

	return jTweetBeanObject;
}

//use Java Interface in C
JNIEXPORT void JNICALL Java_com_halloon_android_util_GifDecoder_callInTime(JNIEnv * env, jobject obj, jint time, jobject delayListener){
	int i = 0;

	jclass jDelayListenerClass = (*env)->FindClass(env, "com/halloon/android/listener/DelayListener");
    jmethodID jDelayListenerOnDelayID = (*env)->GetMethodID(env, jDelayListenerClass, "onDelay", "(I)V");

	while(i < 10){
		sleep(time);

		(*env)->CallVoidMethod(env, delayListener, jDelayListenerOnDelayID, i);

	    i++;
	}
}

//a simple bitmap covert test
JNIEXPORT void JNICALL Java_com_halloon_android_util_GifDecoder_reverseBitmap(JNIEnv * env, jobject obj, jobject sourceBitmap, jobject destBitmap){

	    AndroidBitmapInfo sourceInfo;
		void*             sourcePixels;
		AndroidBitmapInfo destInfo;
		void*             destPixels;
		int               ret;
		int               y;
		int               x;

		LOGI("reverseBitmap");
		if((ret = AndroidBitmap_getInfo(env, sourceBitmap, &sourceInfo)) < 0){
			LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
			return;
		}

		if((ret = AndroidBitmap_getInfo(env, destBitmap, &destInfo)) < 0){
			LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
			return;
		}

		LOGI("color image :: width is %d; height is %d; stride is %d; format is %d; flags is %d",
				sourceInfo.width, sourceInfo.height, sourceInfo.stride, sourceInfo.format, sourceInfo.flags);
		if(sourceInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888){
			LOGE("Bitmap format is not RGBA_8888 !");
			return;
		}

		LOGI("gray image :: width is %d; height is %d; stride is %d; format is %d; flags is %d",
				destInfo.width, destInfo.height, destInfo.stride, destInfo.format, destInfo.flags);
		if(destInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888){
			LOGE("Bitmap format is not RGBA_8888");
			return;
		}

		if((ret = AndroidBitmap_lockPixels(env, sourceBitmap, &sourcePixels)) < 0){
			LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
		}

		if((ret = AndroidBitmap_lockPixels(env, destBitmap, &destPixels)) < 0){
			LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
		}

		for(y = 0; y < sourceInfo.height; y++){
			argb * line = (argb *) sourcePixels;
			argb * destLine = (argb *) destPixels;
			for(x = 0; x <sourceInfo.width; x++){

			    destLine[x].alpha = 255 - line[x].alpha;
				destLine[x].red = 255 - line[x].red;
				destLine[x].green = 255 - line[x].green;
				destLine[x].blue = 255 - line[x].blue;
			}

			sourcePixels = (char *) sourcePixels + sourceInfo.stride;
			destPixels = (char *) destPixels + destInfo.stride;
		}

		LOGI("unlocking pixels");
		AndroidBitmap_unlockPixels(env, sourceBitmap);
		AndroidBitmap_unlockPixels(env, destBitmap);
}

//this is a convert to gray function i copy from someone's blog, it works fine...
JNIEXPORT void JNICALL Java_com_halloon_android_util_GifDecoder_convertToGray(JNIEnv * env, jobject obj, jobject bitmapColor, jobject bitmapGray){

	AndroidBitmapInfo infoColor;
	void*             pixelsColor;
	AndroidBitmapInfo infoGray;
	void*             pixelsGray;
	int               ret;
	int               y;
	int               x;

	LOGI("convertToGray");
	if((ret = AndroidBitmap_getInfo(env, bitmapColor, &infoColor)) < 0){
		LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		return;
	}

	if((ret = AndroidBitmap_getInfo(env, bitmapGray, &infoGray)) < 0){
		LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		return;
	}

	LOGI("color image :: width is %d; height is %d; stride is %d; format is %d; flags is %d",
	      infoColor.width, infoColor.height, infoColor.stride, infoColor.format, infoColor.flags);
	if(infoColor.format != ANDROID_BITMAP_FORMAT_RGBA_8888){
		LOGE("Bitmap format is not RGBA_8888 !");
		return;
	}

	LOGI("gray image :: width is %d; height is %d; stride is %d; format is %d; flags is %d",
		      infoGray.width, infoGray.height, infoGray.stride, infoGray.format, infoGray.flags);
	if(infoGray.format != ANDROID_BITMAP_FORMAT_RGBA_8888){
		LOGE("Bitmap format is not RGBA_8888");
		return;
	}

	if((ret = AndroidBitmap_lockPixels(env, bitmapColor, &pixelsColor)) < 0){
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
	}

	if((ret = AndroidBitmap_lockPixels(env, bitmapGray, &pixelsGray)) < 0){
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
	}

	for(y = 0; y < infoColor.height; y++){
		argb * line = (argb *) pixelsColor;
		argb * destLine = (argb *) pixelsGray;
		for(x = 0; x <infoColor.width; x++){


			uint8_t c = (77 * line[x].red + 151 * line[x].green + 28 * line[x].blue) >> 8;

		    destLine[x].alpha = c;
			destLine[x].red = c;
			destLine[x].green = c;
			destLine[x].blue = c;
		}

		pixelsColor = (char *) pixelsColor + infoColor.stride;
		pixelsGray = (char *) pixelsGray + infoGray.stride;
	}

	LOGI("unlocking pixels");
	AndroidBitmap_unlockPixels(env, bitmapColor);
	AndroidBitmap_unlockPixels(env, bitmapGray);
}

JNIEXPORT void JNICALL Java_com_halloon_android_util_GifDecoder_reverseAndGray(JNIEnv * env, jobject obj, jobject bitmapColor, jobject bitmapGray){

	AndroidBitmapInfo infoColor;
	void*             pixelsColor;
	AndroidBitmapInfo infoGray;
	void*             pixelsGray;
	int               ret;
	int               y;
	int               x;

	LOGI("convertToGray");
	if((ret = AndroidBitmap_getInfo(env, bitmapColor, &infoColor)) < 0){
		LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		return;
	}

	if((ret = AndroidBitmap_getInfo(env, bitmapGray, &infoGray)) < 0){
		LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		return;
	}

	LOGI("color image :: width is %d; height is %d; stride is %d; format is %d; flags is %d",
	      infoColor.width, infoColor.height, infoColor.stride, infoColor.format, infoColor.flags);
	if(infoColor.format != ANDROID_BITMAP_FORMAT_RGBA_8888){
		LOGE("Bitmap format is not RGBA_8888 !");
		return;
	}

	LOGI("gray image :: width is %d; height is %d; stride is %d; format is %d; flags is %d",
		      infoGray.width, infoGray.height, infoGray.stride, infoGray.format, infoGray.flags);
	if(infoGray.format != ANDROID_BITMAP_FORMAT_RGBA_8888){
		LOGE("Bitmap format is not RGBA_8888");
		return;
	}

	if((ret = AndroidBitmap_lockPixels(env, bitmapColor, &pixelsColor)) < 0){
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
	}

	if((ret = AndroidBitmap_lockPixels(env, bitmapGray, &pixelsGray)) < 0){
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
	}

	for(y = 0; y < infoColor.height; y++){
		argb * line = (argb *) pixelsColor;
		argb * destLine = (argb *) pixelsGray;
		for(x = 0; x <infoColor.width; x++){


		    //uint8_t c = line[x].red * 0.3 + line[x].green * 0.59 + line[x].blue * 0.11;

			//bitwise operation instead of float operation make the calculation faster.
			//uint8_t c = (line[x].red shr 2) + (line[x].red shr 4) + (line[x].green shr 1) + (line[x].green shr 4) + (line[x].blue shr 3);
            uint8_t c = (77 * line[x].red + 151 * line[x].green + 28 * line[x].blue) >> 8;

		    c = 255 - c;

		    destLine[x].alpha = c;
			destLine[x].red = c;
			destLine[x].green = c;
			destLine[x].blue = c;
		}

		pixelsColor = (char *) pixelsColor + infoColor.stride;
		pixelsGray = (char *) pixelsGray + infoGray.stride;
	}

	LOGI("unlocking pixels");
	AndroidBitmap_unlockPixels(env, bitmapColor);
	AndroidBitmap_unlockPixels(env, bitmapGray);
}

//Color Matrix function with the Color Matrix Algorithm I found in AS3 reference website, it can be use for color bitmap convert into gray bitmap, or reverse bitmap.
//it works fine except the red channel & the alpha channel are always mix up,
//my guess is that it's cause by the different RGBA format in C & java,still don't know what kind of format is in C and java,looking for a answer.
JNIEXPORT void JNICALL Java_com_halloon_android_util_GifDecoder_colorMatrix(JNIEnv * env, jobject obj, jobject sourceBitmap, jobject destBitmap, jfloatArray matrix){

	AndroidBitmapInfo sourceInfo;
	void*             sourcePixels;
	AndroidBitmapInfo destInfo;
	void*             destPixels;
	int               ret;
	int               y;
	int               x;

	jsize len = (*env)->GetArrayLength(env, matrix);
	if(len != 20){
		LOGE("matrix length error! must be 20");
		return;
	}

	LOGI("convertStart");
	if((ret = AndroidBitmap_getInfo(env, sourceBitmap, &sourceInfo)) < 0){
		LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		return;
	}

	if((ret = AndroidBitmap_getInfo(env, destBitmap, &destInfo)) < 0){
		LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		return;
	}

	LOGI("color image :: width is %d; height is %d; stride is %d; format is %d; flags is %d",
			sourceInfo.width, sourceInfo.height, sourceInfo.stride, sourceInfo.format, sourceInfo.flags);
	if(sourceInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888){
		LOGE("Bitmap format is not RGBA_8888 !");
		return;
	}

	LOGI("gray image :: width is %d; height is %d; stride is %d; format is %d; flags is %d",
			destInfo.width, destInfo.height, destInfo.stride, destInfo.format, destInfo.flags);
	if(destInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888){
		LOGE("Bitmap format is not RGBA_8888");
		return;
	}

	if((ret = AndroidBitmap_lockPixels(env, sourceBitmap, &sourcePixels)) < 0){
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
	}

	if((ret = AndroidBitmap_lockPixels(env, destBitmap, &destPixels)) < 0){
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
	}

	jfloat *matrixC = (*env)->GetFloatArrayElements(env, matrix, 0);

	for(y = 0; y < sourceInfo.height; y++){
		argb * line = (argb *) sourcePixels;
		argb * destLine = (argb *) destPixels;
		for(x = 0; x <sourceInfo.width; x++){

			int16_t dRed   =  (matrixC[0] * line[x].red) +  (matrixC[1] * line[x].green) +  (matrixC[2] * line[x].blue) +  (matrixC[3] * line[x].alpha) + matrixC[4];
			int16_t dGreen =  (matrixC[5] * line[x].red) +  (matrixC[6] * line[x].green) +  (matrixC[7] * line[x].blue) +  (matrixC[8] * line[x].alpha) + matrixC[9];
			int16_t dBlue  = (matrixC[10] * line[x].red) + (matrixC[11] * line[x].green) + (matrixC[12] * line[x].blue) + (matrixC[13] * line[x].alpha) + matrixC[14];
			int16_t dAlpha = (matrixC[15] * line[x].red) + (matrixC[16] * line[x].green) + (matrixC[17] * line[x].blue) + (matrixC[18] * line[x].alpha) + matrixC[19];

			if(dRed > 255) dRed = 255;
			if(dRed < 0) dRed = 0;
			if(dGreen > 255) dGreen = 255;
			if(dGreen < 0) dGreen = 0;
			if(dBlue > 255) dBlue = 255;
			if(dBlue < 0) dBlue = 0;
			if(dAlpha > 255) dAlpha = 255;
			if(dAlpha < 0) dAlpha = 0;

			destLine[x].red = dRed;
			destLine[x].green = dGreen;
			destLine[x].blue = dBlue;
			destLine[x].alpha = dAlpha;
		}

		sourcePixels = (char *) sourcePixels + sourceInfo.stride;
		destPixels = (char *) destPixels + destInfo.stride;
	}

	LOGI("unlocking pixels");
	AndroidBitmap_unlockPixels(env, sourceBitmap);
	AndroidBitmap_unlockPixels(env, destBitmap);
}

//here's another algorithm from AS3 reference website, convolution filter, it's very useful in edge detection or something like that.
//it works eventually...tooks me three days.....
JNIEXPORT void JNICALL Java_com_halloon_android_util_GifDecoder_convolutionFilter(JNIEnv * env, jobject obj, jobject sourceBitmap, jobject destBitmap, jfloatArray matrix, jfloat divisor, jfloat bias){

	AndroidBitmapInfo sourceInfo;
	void*             sourcePixels;
	AndroidBitmapInfo destInfo;
	void*             destPixels;
	int               ret;
	int               y;
	int               x;

	int               mx;
	int               my;

	jsize len = (*env)->GetArrayLength(env, matrix);
	if(len != 9){
		LOGE("matrix length error! must be 9");
		return;
	}

	if((ret = AndroidBitmap_getInfo(env, sourceBitmap, &sourceInfo)) < 0){
		LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		return;
	}

	if((ret = AndroidBitmap_getInfo(env, destBitmap, &destInfo)) < 0){
		LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		return;
	}

	LOGI("color image :: width is %d; height is %d; stride is %d; format is %d; flags is %d",
			sourceInfo.width, sourceInfo.height, sourceInfo.stride, sourceInfo.format, sourceInfo.flags);
	if(sourceInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888){
		LOGE("Bitmap format is not RGBA_8888 !");
		return;
	}

	LOGI("gray image :: width is %d; height is %d; stride is %d; format is %d; flags is %d",
			destInfo.width, destInfo.height, destInfo.stride, destInfo.format, destInfo.flags);
	if(destInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888){
		LOGE("Bitmap format is not RGBA_8888");
		return;
	}

	if((ret = AndroidBitmap_lockPixels(env, sourceBitmap, &sourcePixels)) < 0){
		LOGE("AndroidBItmap_lockPixels() failed ! error=%d", ret);
	}

	if((ret = AndroidBitmap_lockPixels(env, destBitmap, &destPixels)) < 0){
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
	}

	int width = sourceInfo.width;
	int height = sourceInfo.height;

	jfloat *matrixC = (*env)->GetFloatArrayElements(env, matrix, 0);

	//the algorithm is
	//dst (x, y) = ((src (x-1, y-1) * a0 + src(x, y-1) * a1.... src(x, y+1) * a7 + src (x+1,y+1) * a8) / divisor) + bias
	//for every channel in each pixel
	for(y = 0; y < height; y++){
		argb * sourLine = (argb *) sourcePixels;
		argb * destLine = (argb *) destPixels;

		for(x = 0; x < width; x++){

			if(y > 0 && y < height - 1 && x > 0 && x < width - 1){

				argb * preLine = (argb *) ((char *) sourcePixels - sourceInfo.stride);
				argb * nxtLine = (argb *) ((char *) sourcePixels + sourceInfo.stride);


				int16_t dAlpha = 0;
				int16_t dRed = 0;
				int16_t dGreen = 0;
				int16_t dBlue = 0;


				for(my = 0; my < 3; my++){
					for(mx = 0; mx < 3; mx++){

						switch(my){
						case 0:
							dAlpha += matrixC[my * 3 + mx] * preLine[x + (mx - 1)].alpha;
							dRed   += matrixC[my * 3 + mx] * preLine[x + (mx - 1)].red;
							dGreen += matrixC[my * 3 + mx] * preLine[x + (mx - 1)].green;
							dBlue  += matrixC[my * 3 + mx] * preLine[x + (mx - 1)].blue;
							break;
						case 1:
							dAlpha += matrixC[my * 3 + mx] * sourLine[x + (mx - 1)].alpha;
						    dRed   += matrixC[my * 3 + mx] * sourLine[x + (mx - 1)].red;
							dGreen += matrixC[my * 3 + mx] * sourLine[x + (mx - 1)].green;
							dBlue  += matrixC[my * 3 + mx] * sourLine[x + (mx - 1)].blue;
							break;
						case 2:
							dAlpha += matrixC[my * 3 + mx] * nxtLine[x + (mx - 1)].alpha;
						    dRed   += matrixC[my * 3 + mx] * nxtLine[x + (mx - 1)].red;
							dGreen += matrixC[my * 3 + mx] * nxtLine[x + (mx - 1)].green;
							dBlue  += matrixC[my * 3 + mx] * nxtLine[x + (mx - 1)].blue;
							break;
						}
					}
				}

				dRed   = (dRed / divisor) + bias;
				dGreen = (dGreen / divisor) + bias;
				dBlue  = (dBlue / divisor) + bias;
				dAlpha = (dAlpha / divisor) + bias;

				if(dRed > 255) dRed = 255;
				if(dRed < 0) dRed = 0;
				if(dGreen > 255) dGreen = 255;
				if(dGreen < 0) dGreen = 0;
				if(dBlue > 255) dBlue = 255;
				if(dBlue < 0) dBlue = 0;
				if(dAlpha > 255) dAlpha = 255;
			    if(dAlpha < 0) dAlpha = 0;

				destLine[x].red = dRed;
				destLine[x].green = dGreen;
				destLine[x].blue = dBlue;
				destLine[x].alpha = dAlpha;

		    }

		}

		sourcePixels = (char *) sourcePixels + sourceInfo.stride;
		destPixels = (char *) destPixels + destInfo.stride;
	}

	LOGI("unlocking pixels");
	AndroidBitmap_unlockPixels(env, destBitmap);
}
