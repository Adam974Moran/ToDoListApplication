#include <jni.h>

extern "C" JNIEXPORT jint JNICALL
Java_com_example_todolistandnotifications_ToDoObject_checkDeadlineNative(JNIEnv* env, jobject obj, jintArray currentTime, jintArray taskTime) {
    jint *current_time = env->GetIntArrayElements(currentTime, 0);
    jint *task_time = env->GetIntArrayElements(taskTime, 0);

    jint result = -1;
    for (int i = 0; i < 5; i++) {
        if (task_time[i] < current_time[i]) {
            result = 1;
            break;
        }
    }

    env->ReleaseIntArrayElements(currentTime, current_time, 0);
    env->ReleaseIntArrayElements(taskTime, task_time, 0);

    return result;
}
