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
JNIEXPORT void JNICALL Java_com_halloon_android_util_GifDecoder_reverseBitmap(JNIEnv * env, jobject obj, jobject sourceBitmap){

	AndroidBitmapInfo info;
	void* pixels;

	AndroidBitmap_getInfo(env, sourceBitmap, &info);
	const int width = info.width;
	const int height = info.height;

	AndroidBitmap_lockPixels(env, sourceBitmap, &pixels);

	int i;
	int j;
	for(i = 0; i < height; i++){
		uint8_t * grayLine = (uint8_t *) pixels;
		int v;
		for(j = 0; j < width; j++){
			v = (int) grayLine[i];

			v +=100;
			if(v>= 255){
				grayLine[i] = 255;
			}else if(v <= 0){
				grayLine[i] = 0;
			}else{
				grayLine[i] = (uint8_t) v;
			}
		}

		pixels = (char *) pixels + info.stride;
	}

	AndroidBitmap_unlockPixels(env, sourceBitmap);
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
	if(infoGray.format != ANDROID_BITMAP_FORMAT_A_8){
		LOGE("Bitmap format is not A_8");
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
		uint8_t * grayLine = (uint8_t *) pixelsGray;
		for(x = 0; x <infoColor.width; x++){
			grayLine[x] = 0.3 * line[x].red + 0.59 * line[x].green + 0.11 * line[x].blue;
		}

		pixelsColor = (char *) pixelsColor + infoColor.stride;
		pixelsGray = (char *) pixelsGray + infoGray.stride;
	}

	LOGI("unlocking pixels");
	AndroidBitmap_unlockPixels(env, bitmapColor);
	AndroidBitmap_unlockPixels(env, bitmapGray);
}
