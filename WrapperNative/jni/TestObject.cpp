#include "uk_co_humboldt_MavenJNIExample_TestObject.h"
#include <example.h>

JNIEXPORT jdouble JNICALL Java_uk_co_humboldt_MavenJNIExample_TestObject_getY
  (JNIEnv *env, jobject obj)
{
	jclass cls = env->GetObjectClass(obj);
	jfieldID fid = env->GetFieldID(cls, "x", "D");
	if (fid)
		return sinsquare(env->GetDoubleField(obj, fid));
	else
		return 0.0;
}