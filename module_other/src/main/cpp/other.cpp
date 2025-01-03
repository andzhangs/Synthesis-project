#include <jni.h>

// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("other");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("other")
//      }
//    }

extern "C"
JNIEXPORT jstring JNICALL
Java_com_module_other_MainActivity_getString(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF("Hello world");
}