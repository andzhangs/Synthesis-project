#include <jni.h>
#include <string>

#include "json/json.h"

using namespace std;
using namespace Json;

/**
 * jsoncpp工具
 * /usr/local/Cellar/jsoncpp/1.9.5/include/json
 */

extern "C" jstring
Java_com_dongnao_module_jnijson_MainActivity_stringFromJNI(JNIEnv *env, jobject /* this */) {
    Value json;
    json["test"] = "hello";
    json["value"] = "world";
    json["ret"] = 10;

    //方式一：
    string jsonStr=json.toStyledString();
    //方式二：
//    StreamWriterBuilder fastWriter;
//    string jsonStr=writeString(fastWriter, json);
    return env->NewStringUTF(jsonStr.c_str());
}