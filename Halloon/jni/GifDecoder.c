/*
 * Gif image decoder
 *
 * author:airbus
 */

#include <jni.h>
#include <android/log.h>

typedef unsigned char byte;

bool checkIsGif(byte * data){
    if(data == "G" && data + 1 == "I" && data + 2 == "F"){
    	return 1;
    }else{
    	return 0;
    }
}

JNIEXPORT void JNICALL Java_com_halloon_android_util_GifDecoder_gifInit(JNIEnv * env, jobject obj){

}
