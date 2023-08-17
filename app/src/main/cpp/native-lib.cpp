#include <jni.h>
#include <string>

#include "json/json.h"

using namespace std;
using namespace Json;

extern "C" jstring
Java_zs_android_synthesis_MainActivity_stringFromJNI(JNIEnv *env, jobject /* this */) {
    Value json;
    json["test"] = "hello";
    json["value"] = "world";
    json["ret"] = 10;
    StreamWriterBuilder fastWriter;
    string jsonStr=writeString(fastWriter, json);
    return env->NewStringUTF(jsonStr.c_str());
}