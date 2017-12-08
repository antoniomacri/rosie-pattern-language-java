#include "com_github_antoniomacri_TestObject.h"
#include <example.h>

JNIEXPORT jdouble JNICALL Java_com_github_antoniomacri_TestObject_getY
  (JNIEnv *env, jobject obj)
{
	jclass cls = env->GetObjectClass(obj);
	jfieldID fid = env->GetFieldID(cls, "x", "D");
	if (fid)
		return sinsquare(env->GetDoubleField(obj, fid));
	else
		return 0.0;
}