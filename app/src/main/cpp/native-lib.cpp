#include <jni.h>
#include <string>

//#include "test.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_arprog_inc_ar_arprog_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    //testFunc();

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
