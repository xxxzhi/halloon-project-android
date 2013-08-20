/**
 * a simple test for create Java Object in C
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

//a simple bitmap covert test, not working..
JNIEXPORT void JNICALL Java_com_halloon_android_util_GifDecoder_reverseBitmap(JNIEnv * env, jobject obj, jobject sourceBitmap, jobject destBitmap){

	    AndroidBitmapInfo sourceInfo;
		void*             sourcePixels;
		AndroidBitmapInfo destInfo;
		void*             destPixels;
		int               ret;
		int               y;
		int               x;

		LOGI("convertToGray");
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


		    uint8_t c = line[x].red * 0.3 + line[x].green * 0.59 + line[x].blue * 0.11;

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

	LOGI("convertToGray");
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

JNIEXPORT void JNICALL Java_com_halloon_android_util_GifDecoder_convolutionFilter(JNIEnv * env, jobject obj, jobject sourceBitmap, jobject destBitmap, jfloatArray matrix, jint matrixX, jint matrixY, jfloat divisor, jfloat bias){

	AndroidBitmapInfo sourceInfo;
	void*             sourcePixels;
	AndroidBitmapInfo destInfo;
	void*             destPixels;
	int               ret;
	int               y;
	int               x;

	int              mx;
	int              my;

	jsize len = (*env)->GetArrayLength(env, matrix);
	if(len != 20){
		LOGE("matrix length error! must be 20");
		return;
	}

	LOGI("convertToGray");
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

	int divX = (matrixX - 1) / 2;
	int divY = (matrixY - 1) / 2;

	sourcePixels = (char *) sourcePixels + (sourceInfo.stride * divY);
	destPixels = (char *) destPixels + (destInfo.stride * divY);

	for(y = divY; y < sourceInfo.height - divY; y++){
		argb * line = (argb *) sourcePixels;
		argb * destLine = (argb *) destPixels;
		for(x = divX; x <sourceInfo.width - divX; x++){
			int16_t   dRed = 0;
			int16_t dGreen = 0;
			int16_t  dBlue = 0;
			int16_t dAlpha = 0;

			for(my = 0; my < matrixY; my++){
				for(mx = 0; mx < matrixX; mx++){
					dRed += matrix[mx * my]
				}
			}

		}

		sourcePixels = (char *) sourcePixels + sourceInfo.stride;
		destPixels = (char *) destPixels + destInfo.stride;
	}

	LOGI("unlocking pixels");
	AndroidBitmap_unlockPixels(env, sourceBitmap);
	AndroidBitmap_unlockPixels(env, destBitmap);
}
