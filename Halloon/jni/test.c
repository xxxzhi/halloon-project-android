/**
 * a simple test for create Java Object in C
 *
 * by airbus(caifangmao8@gmail.com)
 */
#include <jni.h>
#include <string.h>

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

JNIEXPORT void JNICALL Java_com_halloon_android_util_GifDecoder_callInTime(JNIEnv * env, jobject obj, jint time, jobject delayListener){
	int i = 0;

	jclass jDelayListenerClass = (*env)->FindClass(env, "com/halloon/android/listener/DelayListener");
    jmethodID jDelayListenerOnDelayID = (*env)->GetMethodID(env, jDelayListenerClass, "onDelay", "()V");

	while(i < 10){
		sleep(time);

		(*env)->CallVoidMethod(env, delayListener, jDelayListenerOnDelayID);

	    i++;
	}
}
