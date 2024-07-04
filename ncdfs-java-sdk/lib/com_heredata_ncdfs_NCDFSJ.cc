#include <sys/time.h>
#include <unistd.h>
#include <sys/types.h>
#include <stdint.h>
#include <sys/syscall.h>
#include "com_heredata_ncdfs_NCDFSJ.h"

//#define DEBUG
#ifdef __cplusplus
#define JNIEnv_FUNCTION_ADAPTER(fun, env, ...) env->fun(__VA_ARGS__);
#else
#define JNIEnv_FUNCTION_ADAPTER(fun, env, args...) (*env)->fun(env, ##args);
#endif
#include "ncdfs.h"
#include "ncdfs_exp.h"
#ifdef DEBUG
#define PRINTF_DEBUG(format, args...) printf_debug("%s %s(%d): " format, __FILE__, __FUNCTION__, __LINE__, ##args);
#else
#define PRINTF_DEBUG(format, args...) ;
#endif
enum EXE_RESULT
{
    EXE_SUCCESS = 0,
    EXE_ERROR
};

//获取当前时间，含微秒
void getLocationTime(char *strTime, int strLen)
{
    time_t nowtime;
    struct tm *timeinfo;
    time(&nowtime);
    timeinfo = localtime(&nowtime);

    struct timeval tv;
    gettimeofday(&tv, NULL);
    /*clock_gettime(CLOCK_REALTIME, &time_start);*/

    snprintf(strTime, strLen - 1, "%04d-%02d-%02d %2d:%02d:%02d.%06d", 1900 + timeinfo->tm_year, timeinfo->tm_mon + 1, timeinfo->tm_mday, timeinfo->tm_hour, timeinfo->tm_min, timeinfo->tm_sec, (int)tv.tv_usec);
}
//带自定义格式输出
void printf_debug(const char *pFormat, ...)
{
    const int desc_max = 4096;
    char szDescription[desc_max + 1];
    va_list args;
    char strTime[64] = {0};
    memset(strTime, 0x0, sizeof(strTime));
    getLocationTime(strTime, sizeof(strTime));
    va_start(args, pFormat);
    vsnprintf(szDescription, desc_max, pFormat, args);
    va_end(args);
    fprintf(stderr, "time:\"%s\",pid:%d,tid:%d,%s\n", strTime, getpid(), (pid_t)(syscall(__NR_gettid)), szDescription);
}
//将java类指定的int类型的类变量的值进行修改
int x2long(JNIEnv *env, jobject obj, const char *vchar, jlong value)
{
    jclass clazz = JNIEnv_FUNCTION_ADAPTER(GetObjectClass, env, obj);
    jfieldID id_v = JNIEnv_FUNCTION_ADAPTER(GetFieldID, env, clazz, vchar, "J");
    if (NULL == id_v)
    {
        JNIEnv_FUNCTION_ADAPTER(DeleteLocalRef, env, clazz);
        return -1;
    }
    JNIEnv_FUNCTION_ADAPTER(SetLongField, env, obj, id_v, value);
    JNIEnv_FUNCTION_ADAPTER(DeleteLocalRef, env, clazz);
    return 0;
}

//将java类指定的int类型的类变量的值进行修改
int x2int(JNIEnv *env, jobject obj, const char *vchar, int value)
{
    jclass clazz = JNIEnv_FUNCTION_ADAPTER(GetObjectClass, env, obj);
    jfieldID id_v = JNIEnv_FUNCTION_ADAPTER(GetFieldID, env, clazz, vchar, "I");
    if (NULL == id_v)
    {
        JNIEnv_FUNCTION_ADAPTER(DeleteLocalRef, env, clazz);
        return -1;
    }
    JNIEnv_FUNCTION_ADAPTER(SetIntField, env, obj, id_v, value);
    JNIEnv_FUNCTION_ADAPTER(DeleteLocalRef, env, clazz);
    return 0;
}

//修改指定类中的变量内容
int x2string(JNIEnv *env, jobject obj, const char *vchar, const char *value)
{
    jfieldID fid;
    jstring jstr;
    const char *str;
    jclass cls = JNIEnv_FUNCTION_ADAPTER(GetObjectClass, env, obj);
    fid = JNIEnv_FUNCTION_ADAPTER(GetFieldID, env, cls, vchar, "Ljava/lang/String;");
    if (fid == NULL)
    {
        JNIEnv_FUNCTION_ADAPTER(DeleteLocalRef, env, cls);
        return -1;
    }
    jstr = JNIEnv_FUNCTION_ADAPTER(NewStringUTF, env, value);
    if (jstr == NULL)
    {
        JNIEnv_FUNCTION_ADAPTER(DeleteLocalRef, env, cls);
        return -1;
    }
    JNIEnv_FUNCTION_ADAPTER(SetObjectField, env, obj, fid, jstr);
    JNIEnv_FUNCTION_ADAPTER(DeleteLocalRef, env, cls);
    return 0;
}
/*
jstring strToJstring(JNIEnv* env, const char* pStr)
{
    int        strLen    = strlen(pStr);
    jclass     jstrObj   = (*env)->FindClass(env, "java/lang/String");
    jmethodID  methodId  = (*env)->GetMethodID(env, jstrObj, "<init>", "([BLjava/lang/String;)V");
    jbyteArray byteArray = (*env)->NewByteArray(env, strLen);
    jstring    encode    = (*env)->NewStringUTF(env, "utf-8");

    (*env)->SetByteArrayRegion(env, byteArray, 0, strLen, (jbyte*)pStr);

    return (jstring)(*env)->NewObject(env, jstrObj, methodId, byteArray, encode);
}
*/
/*!
 * @brief 返回首地址，共享JbyteArray生命周期，使用完后需手动调用ReleaseByteArrayElements
 */
char *JbyteArray2Chars(JNIEnv *env, jbyteArray barr, uint64_t *len)
{
    jsize alen = JNIEnv_FUNCTION_ADAPTER(GetArrayLength, env, barr); //获取长度
    if (len != NULL)
    {
        *len = alen;
    }
    jbyte *ba = JNIEnv_FUNCTION_ADAPTER(GetByteArrayElements, env, barr, JNI_FALSE); // jbyteArray转为jbyte*
    if (ba == NULL)
    {
        return NULL;
    }

    return (char *)ba;
}

/*!
 * @param pncdfs type_参数==EXE_SUCCESS，可以为NULL
 */
void SetExeResultInfo(enum EXE_RESULT type_, JNIEnv *env, jobject obj, NCDFS *pncdfs)
{
    if (type_ == EXE_SUCCESS)
    {
        x2int(env, obj, "errno", 0);
        x2string(env, obj, "errinfo", "Success");
    }
    else if (type_ == EXE_ERROR && pncdfs != NULL)
    {
        x2int(env, obj, "errno", adapter_errno2(pncdfs));
        x2string(env, obj, "errinfo", adapter_errstr2(pncdfs));
    }
}

JNIEXPORT jboolean JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1init(JNIEnv *env, jobject obj)
{
    PRINTF_DEBUG("start");
    NCDFS *pncdfs;
    char szError[256];
    NCDFS strSDFS;
    memset(&strSDFS, 0, sizeof(NCDFS));
    pncdfs = (NCDFS *)malloc(sizeof(NCDFS));
    memset(pncdfs, 0, sizeof(NCDFS));
    if (0 != adapter_init(pncdfs, szError))
    {
        PRINTF_DEBUG("szError [%s]",szError);
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno2(pncdfs), adapter_errstr2(pncdfs));
        SetExeResultInfo(EXE_ERROR, env, obj, pncdfs);
        adapter_destory(pncdfs);
        return JNI_FALSE;
    }
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    x2long(env, obj, "pncdfs", (jlong)pncdfs);
    PRINTF_DEBUG("end");
    return JNI_TRUE;
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_login
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1login(JNIEnv *env, jobject obj, jstring jusername, jstring jpassword, jstring jerrstr, jlong jpncdfs)
{ // TODO: 目前不使用
    return JNI_FALSE;
}
/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_stat
 * Signature: (Ljava/lang/String;Ljava/lang/String;J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1stat(JNIEnv *env, jobject obj, jstring jpathname, jobject jstat, jlong jpncdfs)
{
    PRINTF_DEBUG("start");
    NCDFS *pncdfs = (NCDFS *)jpncdfs;
    struct sstat stStat;
    memset(&stStat, 0, sizeof(struct sstat));
    const char *filename = JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, jpathname, NULL);
    if (0 != adapter_stat(filename, &stStat, pncdfs))
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno2(pncdfs), adapter_errstr2(pncdfs));
        SetExeResultInfo(EXE_ERROR, env, obj, pncdfs);
        JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jpathname, filename);
        return JNI_FALSE;
    }
    else
    {
        if (0 == x2int(env, jstat, "stMode", stStat.st_mode) &&
            0 == x2int(env, jstat, "ftMode", stStat.ft_mode) &&
            0 == x2int(env, jstat, "stDev", stStat.st_dev) &&
            0 == x2int(env, jstat, "stUid", stStat.st_uid) &&
            0 == x2int(env, jstat, "stGid", stStat.st_gid) &&
            0 == x2int(env, jstat, "stPerm", stStat.st_perm) &&
            0 == x2int(env, jstat, "stLink", stStat.st_link) &&
            0 == x2long(env, jstat, "stSize", stStat.st_size) &&
            0 == x2long(env, jstat, "stCount", stStat.st_count) &&
            0 == x2long(env, jstat, "stLine", stStat.st_line) &&
            0 == x2long(env, jstat, "dId", stStat.d_id) &&
            0 == x2long(env, jstat, "lcTime", stStat.lctime) &&
            0 == x2long(env, jstat, "laTime", stStat.latime) &&
            0 == x2long(env, jstat, "lmTime", stStat.lmtime) &&
            0 == x2string(env, jstat, "szUnam", stStat.sz_unam) &&
            0 == x2string(env, jstat, "szGnam", stStat.sz_gnam))
        {
            SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
        }
        else
        {
            PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno2(pncdfs), adapter_errstr2(pncdfs));
            SetExeResultInfo(EXE_ERROR, env, obj, pncdfs);
        }
    }
    PRINTF_DEBUG("end");
    JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jpathname, filename);
    return JNI_TRUE;
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_access
 * Signature: (Ljava/lang/String;Ljava/lang/String;J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1access(JNIEnv *env, jobject obj, jstring jpathname, jstring jmode, jlong jpncdfs)
{
    PRINTF_DEBUG("begin");
    NCDFS *pncdfs = (NCDFS *)jpncdfs;
    const char *filename = JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, jpathname, NULL);
    PRINTF_DEBUG("filename[%s]", filename);

    if (0 != adapter_access(filename, 00, pncdfs))
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno2(pncdfs), adapter_errstr2(pncdfs));
        SetExeResultInfo(EXE_ERROR, env, obj, pncdfs);
        JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jpathname, filename);
        return JNI_FALSE;
    }
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jpathname, filename);
    PRINTF_DEBUG("end");
    return JNI_TRUE;
}

/*!
 * @brief 不支持mkdir -R 选项，请确保父级目录存在
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_mkdir
 * Signature: (Ljava/lang/String;Ljava/lang/String;J)Z
 * @param jmode要求10进制的字符串表示
 */
JNIEXPORT jboolean JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1mkdir(JNIEnv *env, jobject obj, jstring jpath, jstring jmode, jlong jpncdfs)
{
    PRINTF_DEBUG("begin");
    NCDFS *pncdfs = (NCDFS *)jpncdfs;
    const char *path = JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, jpath, NULL);
    const char *smode = JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, jmode, NULL);
    int imode = atoi(smode);
    PRINTF_DEBUG("filename[%s]", path);

    if (0 != adapter_mkdir(path, imode, pncdfs))
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno2(pncdfs), adapter_errstr2(pncdfs));
        SetExeResultInfo(EXE_ERROR, env, obj, pncdfs);
        JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jpath, path);
        JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jmode, smode);
        return JNI_FALSE;
    }
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jpath, path);
    JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jmode, smode);
    PRINTF_DEBUG("end");
    return JNI_TRUE;
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    open_mkdir
 * Signature: (Ljava/lang/String;J)J
 */
JNIEXPORT jlong JNICALL Java_com_heredata_ncdfs_NCDFSJ_open_1dir(JNIEnv *env, jobject obj, jstring jpath, jlong jpncdfs)
{
    PRINTF_DEBUG("begin");
    NCDFS *pncdfs = (NCDFS *)jpncdfs;
    const char *path = JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, jpath, NULL);
    PRINTF_DEBUG("filename[%s]", path);
    SDIR *dirptr = NULL;
    if (NULL == (dirptr = adapter_opendir(path, pncdfs)))
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno2(pncdfs), adapter_errstr2(pncdfs));
        SetExeResultInfo(EXE_ERROR, env, obj, pncdfs);
        JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jpath, path);
        return (jlong)NULL;
    }
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jpath, path);
    PRINTF_DEBUG("end");
    return (jlong)dirptr;
}

/*TODO:readdir 估计不使用，后续增加listdir，返回一定数量的目录下文件或目录
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    read_dir
 * Signature: (Ljava/lang/String;J)J
 */
JNIEXPORT jlong JNICALL Java_com_heredata_ncdfs_NCDFSJ_read_1dir(JNIEnv *env, jobject obj, jlong jpdir, jlong jpncdfs)
{
    PRINTF_DEBUG("begin");
    PRINTF_DEBUG("end");
    return (jlong)0;
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    close_dir
 * Signature: (Ljava/lang/String;J)J
 */
JNIEXPORT jlong JNICALL Java_com_heredata_ncdfs_NCDFSJ_close_1dir(JNIEnv *env, jobject obj, jlong jpdir, jlong jpncdfs)
{
    PRINTF_DEBUG("begin");
    NCDFS *pncdfs = (NCDFS *)jpncdfs;
    SDIR *dirptr = (SDIR *)jpdir;
    if (adapter_closedir(dirptr, pncdfs) != 0)
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno2(pncdfs), adapter_errstr2(pncdfs));
        SetExeResultInfo(EXE_ERROR, env, obj, pncdfs);
        return JNI_FALSE;
    }
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    PRINTF_DEBUG("end");
    return JNI_TRUE;
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    rewind_dir
 * Signature: (Ljava/lang/String;J)J
 */
JNIEXPORT jlong JNICALL Java_com_heredata_ncdfs_NCDFSJ_rewind_1dir(JNIEnv *env, jobject obj, jlong jpdir, jlong jpncdfs)
{
    PRINTF_DEBUG("begin");
    NCDFS *pncdfs = (NCDFS *)jpncdfs;
    SDIR *dirptr = (SDIR *)jpdir;
    adapter_rewinddir(dirptr, pncdfs);
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    PRINTF_DEBUG("end");
    return JNI_TRUE;
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_rename
 * Signature: (Ljava/lang/String;Ljava/lang/String;J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1rename(JNIEnv *env, jobject obj, jstring joldname, jstring jnewname, jlong jpncdfs)
{
    PRINTF_DEBUG("satrt");
    NCDFS *pncdfs = (NCDFS *)jpncdfs;
    const char *oldname = JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, joldname, 0);
    const char *newname = JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, jnewname, 0);
    PRINTF_DEBUG("oldname[%s] newname[%s]", oldname, newname);

    if (0 != adapter_rename(oldname, newname, pncdfs))
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno2(pncdfs), adapter_errstr2(pncdfs));
        SetExeResultInfo(EXE_ERROR, env, obj, pncdfs);
        JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, joldname, oldname);
        JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jnewname, newname);
        return JNI_FALSE;
    }
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, joldname, oldname);
    JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jnewname, newname);
    PRINTF_DEBUG("end");
    return JNI_TRUE;
}
/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_remove
 * Signature: (Ljava/lang/String;J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1remove(JNIEnv *env, jobject obj, jstring jpath, jlong jpncdfs)
{
    PRINTF_DEBUG("start");
    NCDFS *pncdfs = (NCDFS *)jpncdfs;
    const char *filename = JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, jpath, 0);
    PRINTF_DEBUG("filename[%s]", filename);
    if (0 != adapter_remove(filename, pncdfs))
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno2(pncdfs), adapter_errstr2(pncdfs));
        SetExeResultInfo(EXE_ERROR, env, obj, pncdfs);
        JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jpath, filename);
        return JNI_FALSE;
    }
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jpath, filename);
    PRINTF_DEBUG("end");
    return JNI_TRUE;
}

/*!
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_open
 * Signature: (Ljava/lang/String;Ljava/lang/String;J)J
 * @return 返回0表示打开文件失败
 */
JNIEXPORT jlong JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1open(JNIEnv *env, jobject obj, jstring jfilename, jstring jmode, jlong jpncdfs)
{
    PRINTF_DEBUG("start");
    NCDFS *pncdfs = (NCDFS *)jpncdfs;
    const char *filename = JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, jfilename, 0);
    const char *fmode = JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, jmode, 0);
    PRINTF_DEBUG("filename[%s] fmode[%s]", filename, fmode);
    SFILE *file_ptr = adapter_open(filename, fmode, pncdfs);
    if (NULL == file_ptr)
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno2(pncdfs), adapter_errstr2(pncdfs));
        SetExeResultInfo(EXE_ERROR, env, obj, pncdfs);
        JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jfilename, filename);
        JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jmode, fmode);
        return 0;
    }
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jfilename, filename);
    JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jmode, fmode);
    PRINTF_DEBUG("end");
    return (jlong)file_ptr;
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_write
 * Signature: (Ljava/lang/String;JJJ)J
 */
JNIEXPORT jlong JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1write__Ljava_lang_String_2JJJ(JNIEnv *env, jobject obj, jstring jbuf, jlong jsize, jlong jn, jlong jfp)
{
    // PRINTF_DEBUG("begin");
    SFILE *file_ptr = (SFILE *)jfp;
    JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, jbuf, NULL);
    const char *buf = JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, jbuf, NULL);
    size_t s_size = (size_t)jsize;
    size_t s_count = (size_t)jn;
    // PRINTF_DEBUG("size[%lu] n[%lu] buf[%s]", s_size, s_count, buf);
    long len = adapter_write(buf, s_size, s_count, file_ptr);

    if (0 > len)
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno(file_ptr), adapter_errstr(file_ptr));
        x2int(env, obj, "errno", adapter_errno(file_ptr));
        x2string(env, obj, "errinfo", adapter_errstr(file_ptr));
        JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jbuf, buf);
        return -1;
    }
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jbuf, buf);
    // PRINTF_DEBUG("end");
    return len;
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_write
 * Signature: ([BJJJ)J
 */
JNIEXPORT jlong JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1write___3BJJJ(JNIEnv *env, jobject obj, jbyteArray jarrbuf, jlong jsize, jlong jn, jlong jfp)
{
    // PRINTF_DEBUG("begin");
    SFILE *file_ptr = (SFILE *)jfp;
    uint64_t jarrlen = 0;
    char *buf = JbyteArray2Chars(env, jarrbuf, &jarrlen);
    size_t s_size = (size_t)jsize;
    size_t s_count = (size_t)jn;
    // PRINTF_DEBUG("jarrlen[%lu] size[%lu] n[%lu] buf[%s]", s_size, s_count, jarrlen, buf);
    if (jarrlen < s_size * s_count)
    {
        JNIEnv_FUNCTION_ADAPTER(ReleaseByteArrayElements, env, jarrbuf, (jbyte *)buf, 0); //释放掉
        return -1;
    }
    long len = adapter_write(buf, s_size, s_count, file_ptr);

    if (0 > len)
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno(file_ptr), adapter_errstr(file_ptr));
        x2int(env, obj, "errno", adapter_errno(file_ptr));
        x2string(env, obj, "errinfo", adapter_errstr(file_ptr));
        JNIEnv_FUNCTION_ADAPTER(ReleaseByteArrayElements, env, jarrbuf, (jbyte *)buf, 0); //释放掉
        return -1;
    }
    JNIEnv_FUNCTION_ADAPTER(ReleaseByteArrayElements, env, jarrbuf, (jbyte *)buf, 0); //释放掉
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    // PRINTF_DEBUG("end");
    return len;
}

/*TODO:暂不实现，返回jstring可能不能包含所有的请求数据
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_read
 * Signature: (JJJ)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1read__JJJ(JNIEnv *env, jobject obj, jlong jsize, jlong jn, jlong jfp)
{
    // PRINTF_DEBUG("begin");
    // SFILE *file_ptr = (SFILE *)jfp;
    // size_t s_size = (size_t)jsize;
    // size_t s_count = (size_t)jn;
    // size_t r_count=0;
    // jstring jbuf=NULL;
    // char *s_buf = NULL;
    // s_buf = (char *)malloc(sizeof(char) * s_size * s_count);
    // if(s_buf==NULL)
    // {
    //             x2int(env, obj, "errno", adapter_errno(file_ptr));
    //     x2string(env, obj, "errinfo", adapter_errstr(file_ptr));
    //     PRINTF_DEBUG("malloc szie[%d] memory failed",sizeof(char) * s_size * s_count);
    //     return jbuf;
    // }
    // memset(s_buf, 0x0, sizeof(char) * s_size * s_count);
    // if ((r_count=adapter_read(s_buf, s_size, s_count, file_ptr))<0)
    // {
    //     PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno(file_ptr), adapter_errstr(file_ptr));
    //     x2int(env, obj, "errno", adapter_errno(file_ptr));
    //     x2string(env, obj, "errinfo", adapter_errstr(file_ptr));
    //     return NULL;
    // }
    // PRINTF_DEBUG("read buf=[%s]",s_buf);
    // {
    //     int strLen = r_count*s_size;
    //     JNIEnv_FUNCTION_ADAPTER(FindClass,env, "java/lang/String");
    //     jclass jstrObj = JNIEnv_FUNCTION_ADAPTER(FindClass,env, "java/lang/String");
    //     jmethodID methodId = JNIEnv_FUNCTION_ADAPTER(GetMethodID,env, jstrObj, "<init>", "([BLjava/lang/String;)V");
    //     jbyteArray byteArray = (*env)->NewByteArray(env, strLen);
    //     jstring encode = (*env)->NewStringUTF(env, "utf-8");

    //     (*env)->SetByteArrayRegion(env, byteArray, 0, strLen, (jbyte *)s_buf);

    //     jbuf = (*env)->NewObject(env, jstrObj, methodId, byteArray, encode);
    //     (*env)->DeleteLocalRef(env, jstrObj); // 20170419
    // }
    // printf_debug(__FILE__, __LINE__, "[Java_com_sitech_heredata_ncdfs_NCDFSJ_read] ...........2222");
    // if (NULL != s_buf)
    // {
    //     printf_debug(__FILE__, __LINE__, "[Java_com_sitech_heredata_ncdfs_NCDFSJ_read] ...........eee");
    //     free(s_buf);
    //     s_buf = NULL;
    // }

    // printf_debug(__FILE__, __LINE__, "[Java_com_sitech_heredata_ncdfs_NCDFSJ_read] ...........3333");
    // // BUG，这里fp需要修改为空，0,因为当前存储的是上次文件的内存地址，关闭了需要置空

    // printf_debug(__FILE__, __LINE__, "[Java_com_sitech_heredata_ncdfs_NCDFSJ_read] s_buf[%s]", s_buf);
    // x2int(env, obj, "errno", 0);
    // x2string(env, obj, "errinfo", "Success");
    // printf_debug(__FILE__, __LINE__, "[Java_com_sitech_heredata_ncdfs_NCDFSJ_read] end");
    // return jbuf;
    return NULL;
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_read
 * Signature: ([BJJJ)J
 */
JNIEXPORT jlong JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1read___3BJJJ(JNIEnv *env, jobject obj, jbyteArray jarrbuf, jlong jsize, jlong jcount, jlong jfp)
{
    // PRINTF_DEBUG("begin");
    SFILE *file_ptr = (SFILE *)jfp;
    size_t s_size = (size_t)jsize;
    size_t s_count = (size_t)jcount;
    size_t rsize = 0;
    uint64_t jarrlen = 0;
    char *s_buf = JbyteArray2Chars(env, jarrbuf, &jarrlen);
    if (jarrlen < s_size * s_count)
    {
        PRINTF_DEBUG("jarrlen [%lu] 小于 read 容量[%lu]", jarrlen, s_size * s_count);
        JNIEnv_FUNCTION_ADAPTER(ReleaseByteArrayElements, env, jarrbuf, (jbyte *)s_buf, 0);
        return -1;
    }
    rsize = adapter_read(s_buf, s_size, s_count, file_ptr);
    // PRINTF_DEBUG("rsize[%d] s_size[%d] s_count[%d] sbuf[%s]", rsize, s_size, s_count, s_buf);
    if (rsize < 0)
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno(file_ptr), adapter_errstr(file_ptr));
        x2int(env, obj, "errno", adapter_errno(file_ptr));
        x2string(env, obj, "errinfo", adapter_errstr(file_ptr));
        JNIEnv_FUNCTION_ADAPTER(ReleaseByteArrayElements, env, jarrbuf, (jbyte *)s_buf, 0);
        return rsize;
    }
    // 提交
    JNIEnv_FUNCTION_ADAPTER(SetByteArrayRegion, env, jarrbuf, 0, rsize, (const jbyte *)s_buf);
    // 释放
    JNIEnv_FUNCTION_ADAPTER(ReleaseByteArrayElements, env, jarrbuf, (jbyte *)s_buf, 0);
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    // PRINTF_DEBUG("end");
    return (jlong)rsize;
}

/*TODO:暂不实现,可在java层处理
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_readline
 * Signature: ([BJ)J
 */
JNIEXPORT jlong JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1readline(JNIEnv *env, jobject obj, jbyteArray jarrbuf, jlong jfp)
{
    // printf_debug(__FILE__,__LINE__,"[%s] begin",__FUNCTION__);
    // SFILE * file_ptr = (SFILE *)fp;
    // printf_debug(__FILE__,__LINE__,"[%s] ...........",__FUNCTION__);

    // size_t rtn=0;

    // jbyte* p = (*env)->GetByteArrayElements(env, jbArray, NULL);
    // //jsize len = (*env)->GetArrayLength(env, jbArray);
    // int i;

    // char * s_buf=NULL;

    // s_buf=(char*)malloc(sizeof(char)*1024);
    // //char s_buf[1024] = "";
    // printf_debug(__FILE__,__LINE__,"[%s] ...........",__FUNCTION__);
    // memset(s_buf,0x0,sizeof(char)*1024);
    // printf_debug(__FILE__,__LINE__,"[%s] ...........",__FUNCTION__);
    // //char *sgets (char *buffer, int size, SFILE *stream);
    // if(NULL == adapter_gets(s_buf,1024,file_ptr))
    // {
    // 	x2int(env, obj, "errno", adapter_errno(file_ptr));
    // 	x2string(env, obj, "errinfo", adapter_errstr(file_ptr));
    // 	return -1;
    // }
    // printf_debug(__FILE__,__LINE__,"[%s] ...........sbuf[%s]",__FUNCTION__, s_buf);
    // //			fprintf(stderr,"zjg............    getchar\n");
    // //			getchar();
    // int local_len = strlen(s_buf);

    // if(local_len >= 1024) local_len= 1024; // BUG 下一次获取不到当前行1024后面的东西？

    // rtn=local_len;

    // printf_debug(__FILE__,__LINE__,"[%s] ...........rtn:%d",__FUNCTION__,rtn);

    // for (i = 0; i < local_len; i++) //      <-------------- 逻辑问题
    // 	//for (i = 0; i < size; i++)
    // {
    // 	printf_debug(__FILE__,__LINE__,"[%s] ...........s_buf[%d][%c]",__FUNCTION__,i,s_buf[i]);
    // 	p[i] = s_buf[i];
    // }

    // (*env)->ReleaseByteArrayElements(env, jbArray, p, 0);

    // if(NULL != s_buf)
    // {
    // 	free(s_buf);
    // 	s_buf=NULL;
    // }

    // return (jlong)rtn;
    return 0;
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_puts
 * Signature: ([BJ)J
 */
JNIEXPORT jlong JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1puts(JNIEnv *env, jobject obj, jbyteArray jarrbuf, jlong jfp)
{
    PRINTF_DEBUG("begin");
    SFILE *file_ptr = (SFILE *)jfp;
    uint64_t jarrlen = 0;
    char *s_buf = JbyteArray2Chars(env, jarrbuf, &jarrlen);
    if (s_buf == NULL || jarrlen == 0)
    {
        PRINTF_DEBUG("buf is [%p] jarrlen [%lu]", s_buf, jarrlen);
        JNIEnv_FUNCTION_ADAPTER(ReleaseByteArrayElements, env, jarrbuf, (jbyte *)s_buf, 0);
        return -1;
    }
    if (adapter_puts(s_buf, file_ptr) != 0)
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno(file_ptr), adapter_errstr(file_ptr));
        x2int(env, obj, "errno", adapter_errno(file_ptr));
        x2string(env, obj, "errinfo", adapter_errstr(file_ptr));
        JNIEnv_FUNCTION_ADAPTER(ReleaseByteArrayElements, env, jarrbuf, (jbyte *)s_buf, 0);
        return -1;
    }
    JNIEnv_FUNCTION_ADAPTER(ReleaseByteArrayElements, env, jarrbuf, (jbyte *)s_buf, 0);
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    PRINTF_DEBUG("end");
    return 0;
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_close
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1close(JNIEnv *env, jobject obj, jlong jfp)
{
    PRINTF_DEBUG("begin");
    SFILE *file_ptr = (SFILE *)jfp;
    if (adapter_close(file_ptr) != 0)
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno(file_ptr), adapter_errstr(file_ptr));
        x2int(env, obj, "errno", adapter_errno(file_ptr));
        x2string(env, obj, "errinfo", adapter_errstr(file_ptr));
        file_ptr = NULL;
        return -1;
    }
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    PRINTF_DEBUG("end");
    return 0;
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_flush
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1flush(JNIEnv *env, jobject obj, jlong jfp)
{
    PRINTF_DEBUG("begin");
    SFILE *file_ptr = (SFILE *)jfp;
    if (adapter_flush(file_ptr) != 0)
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno(file_ptr), adapter_errstr(file_ptr));
        x2int(env, obj, "errno", adapter_errno(file_ptr));
        x2string(env, obj, "errinfo", adapter_errstr(file_ptr));
        return -1;
    }
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    PRINTF_DEBUG("end");
    return 0;
}

/*!
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_eof
 * Signature: (J)I
 * @return
 * - 返回1 到达文件尾
 * - 返回0 没有到达文件尾
 * - 返回-1 处理错误
 */
JNIEXPORT jint JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1eof(JNIEnv *env, jobject obj, jlong jfp)
{
    PRINTF_DEBUG("begin");
    SFILE *file_ptr = (SFILE *)jfp;
    int ret = adapter_eof(file_ptr);
    if (ret < 0)
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno(file_ptr), adapter_errstr(file_ptr));
        x2int(env, obj, "errno", adapter_errno(file_ptr));
        x2string(env, obj, "errinfo", adapter_errstr(file_ptr));
        return -1;
    }
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    PRINTF_DEBUG("end");
    return ret;
}

/*!
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_seek
 * Signature: (JJJ)I
 * @param fromwhere
 * - 文件头 0（SEEK_SET）
 * - 当前位置1（SEEK_CUR）
 * - 文件尾2（SEEK_END）
 * @param joffset 偏移量
 */
JNIEXPORT jint JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1seek(JNIEnv *env, jobject obj, jlong joffset, jlong jfromWhere, jlong jfp)
{
    PRINTF_DEBUG("begin");
    SFILE *file_ptr = (SFILE *)jfp;
    long offset = (long)joffset;
    long fromwhere = (long)jfromWhere;
    if (adapter_seek(file_ptr, offset, fromwhere) != 0)
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno(file_ptr), adapter_errstr(file_ptr));
        x2int(env, obj, "errno", adapter_errno(file_ptr));
        x2string(env, obj, "errinfo", adapter_errstr(file_ptr));
        return (jint)-1;
    }
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    PRINTF_DEBUG("end");
    return (jint)0;
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_tell
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1tell(JNIEnv *env, jobject obj, jlong jfp)
{
    PRINTF_DEBUG("begin");
    SFILE *file_ptr = (SFILE *)jfp;
    long pos = adapter_tell(file_ptr);
    if (pos < 0)
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno(file_ptr), adapter_errstr(file_ptr));
        x2int(env, obj, "errno", adapter_errno(file_ptr));
        x2string(env, obj, "errinfo", adapter_errstr(file_ptr));
        return (jlong)-1;
    }
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    PRINTF_DEBUG("end");
    return (jlong)pos;
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_rewind
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1rewind(JNIEnv *env, jobject obj, jlong jfp)
{
    PRINTF_DEBUG("begin");
    SFILE *file_ptr = (SFILE *)jfp;
    if (adapter_tell(file_ptr) != 0)
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno(file_ptr), adapter_errstr(file_ptr));
        x2int(env, obj, "errno", adapter_errno(file_ptr));
        x2string(env, obj, "errinfo", adapter_errstr(file_ptr));
        return (jint)-1;
    }
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    PRINTF_DEBUG("end");
    return (jint)0;
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_link
 * Signature: (Ljava/lang/String;Ljava/lang/String;J)I
 */
JNIEXPORT jint JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1link(JNIEnv *env, jobject obj, jstring joldpath, jstring jnewpath, jlong jpncdfs)
{
    PRINTF_DEBUG("begin");
    NCDFS *pncdfs = (NCDFS *)jpncdfs;
    const char *oldpath = JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, joldpath, NULL);
    const char *newpath = JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, jnewpath, NULL);
    if (adapter_link(oldpath, newpath, pncdfs) != 0)
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno2(jpncdfs), adapter_errstr2(jpncdfs));
        SetExeResultInfo(EXE_ERROR, env, obj, pncdfs);
        JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, joldpath, oldpath);
        JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jnewpath, newpath);
        return (jint)-1;
    }
    JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, joldpath, oldpath);
    JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jnewpath, newpath);
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    PRINTF_DEBUG("end");
    return (jint)0;
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_unlink
 * Signature: (Ljava/lang/String;J)I
 */
JNIEXPORT jint JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1unlink(JNIEnv *env, jobject obj, jstring jpath, jlong jpncdfs)
{
    PRINTF_DEBUG("begin");
    NCDFS *pncdfs = (NCDFS *)jpncdfs;
    const char *path = JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, jpath, NULL);
    if (adapter_unlink(path, pncdfs) != 0)
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno2(jpncdfs), adapter_errstr2(jpncdfs));
        SetExeResultInfo(EXE_ERROR, env, obj, pncdfs);
        JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jpath, path);
        return (jint)-1;
    }
    JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jpath, path);
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    PRINTF_DEBUG("end");
    return (jint)0;
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_chown
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;J)I
 */
JNIEXPORT jint JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1chown(JNIEnv *env, jobject obj, jstring jfilename, jstring jowner, jstring jgroup, jlong jpncdfs)
{
    PRINTF_DEBUG("begin");
    NCDFS *pncdfs = (NCDFS *)jpncdfs;
    const char *filename = JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, jfilename, NULL);
    const char *owner = JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, jowner, NULL);
    const char *group = JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, jgroup, NULL);
    if (adapter_chown(filename, owner, group, pncdfs) != 0)
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno2(jpncdfs), adapter_errstr2(jpncdfs));
        SetExeResultInfo(EXE_ERROR, env, obj, pncdfs);
        JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jfilename, filename);
        JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jowner, owner);
        JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jgroup, group);
        return (jint)-1;
    }
    JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jfilename, filename);
    JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jowner, owner);
    JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jgroup, group);
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    PRINTF_DEBUG("end");
    return (jint)0;
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_chmod
 * Signature: (Ljava/lang/String;JJ)I
 */
JNIEXPORT jint JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1chmod(JNIEnv *env, jobject obj, jstring jfilename, jlong jmode, jlong jpncdfs)
{
    PRINTF_DEBUG("begin");
    NCDFS *pncdfs = (NCDFS *)jpncdfs;
    const char *filename = JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, jfilename, NULL);
    mode_t mode = (mode_t)jmode;
    if (adapter_chmod(filename, mode, pncdfs) != 0)
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno2(jpncdfs), adapter_errstr2(jpncdfs));
        SetExeResultInfo(EXE_ERROR, env, obj, pncdfs);
        JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jfilename, filename);
        return (jint)-1;
    }
    JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jfilename, filename);
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    PRINTF_DEBUG("end");
    return (jint)0;
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_destory
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1destory(JNIEnv *env, jobject obj, jlong jpncdfs)
{
    PRINTF_DEBUG("begin");
    NCDFS *pncdfs = (NCDFS *)jpncdfs;
    if (NULL != pncdfs)
    {
        adapter_destory(pncdfs);
    }
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    PRINTF_DEBUG("end");
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_ver
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1ver(JNIEnv *env, jobject obj)
{
    PRINTF_DEBUG("begin");
    char S_version[128];
    char S_date[128];
    memset(S_version, 0x0, sizeof(S_version));
    memset(S_date, 0x0, sizeof(S_date));
    adapter_ver(S_version, S_date);
    x2string(env, obj, "sver_num", S_version);
    x2string(env, obj, "sver_date", S_date);
    PRINTF_DEBUG("end");
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    adapter_setdir
 * Signature: (Ljava/lang/String;IJ)Z
 */
JNIEXPORT jboolean JNICALL Java_com_heredata_ncdfs_NCDFSJ_adapter_1setdir(JNIEnv *env, jobject obj, jstring jdir, jint jtype, jlong jpncdfs)
{
    PRINTF_DEBUG("begin");
    NCDFS *pncdfs = (NCDFS *)jpncdfs;
    const char *filename = JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, jdir, NULL);
    int type = (int)jtype;
    PRINTF_DEBUG("filename[%s] type[%d]", filename, type);
    if (0 != adapter_setdir(filename, (NCDFS_NODE_TYPE)type, pncdfs))
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno2(jpncdfs), adapter_errstr2(jpncdfs));
        SetExeResultInfo(EXE_ERROR, env, obj, pncdfs);
        JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jdir, filename);
        return JNI_FALSE;
    }
    JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jdir, filename);
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    PRINTF_DEBUG("end");
    return JNI_TRUE;
}

/*! @brief TODO:由stat接口获取，底层不封装
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    SS_ISREG
 * Signature: (Ljava/lang/String;J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_heredata_ncdfs_NCDFSJ_SS_1ISREG(JNIEnv *env, jobject obj, jstring jstr, jlong jpncdfs)
{
    return JNI_FALSE;
}

/*! @brief TODO:由stat接口获取，底层不封装
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    SS_ISDIR
 * Signature: (Ljava/lang/String;J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_heredata_ncdfs_NCDFSJ_SS_1ISDIR(JNIEnv *env, jobject obj, jstring jstr, jlong jpncdfs)
{
    return JNI_FALSE;
}

/*! @brief TODO:由stat接口获取，底层不封装
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    getlen
 * Signature: (Ljava/lang/String;J)J
 */
JNIEXPORT jlong JNICALL Java_com_heredata_ncdfs_NCDFSJ_getlen(JNIEnv *env, jobject obj, jstring jstr, jlong jpncdfs)
{
    return JNI_FALSE;
}

/*! @brief TODO:由stat接口获取，底层不封装
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    getlastModified
 * Signature: (Ljava/lang/String;J)J
 */
JNIEXPORT jlong JNICALL Java_com_heredata_ncdfs_NCDFSJ_getlastModified(JNIEnv *env, jobject obj, jstring jstr, jlong jpncdfs)
{
    return JNI_FALSE;
}

/*
 * Class:     com_heredata_ncdfs_NCDFSJ
 * Method:    getlist
 * Signature: (Ljava/lang/String;J)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_com_heredata_ncdfs_NCDFSJ_getlist(JNIEnv *env, jobject obj, jstring jdir, jlong jpncdfs)
{
    PRINTF_DEBUG("begin");
    NCDFS *pncdfs = (NCDFS *)jpncdfs;
    const char *filename = JNIEnv_FUNCTION_ADAPTER(GetStringUTFChars, env, jdir, NULL);
    PRINTF_DEBUG("filename[%s]", filename);
    SDIR *dirptr = NULL;
    sdirent *entry;
    if (NULL == (dirptr = adapter_opendir(filename, pncdfs)))
    {
        PRINTF_DEBUG("err errno[%d] errinfo[%s]", adapter_errno2(pncdfs), adapter_errstr2(pncdfs));
        SetExeResultInfo(EXE_ERROR, env, obj, pncdfs);
        JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jdir, filename);
        return NULL;
    }
    int i = 0;
    long dircount = 0;
    while (NULL != (entry = adapter_readdir(dirptr, pncdfs)))
        dircount++;

    PRINTF_DEBUG("dircount[%d]", dircount);
    char **rtn;
    rtn = (char **)malloc((1 + dircount) * 8);
    for (i = 0; i < dircount; i++)
    {
        rtn[i] = (char *)malloc(512);
        memset(rtn[i], 0, 512);
    }

    adapter_rewinddir(dirptr, pncdfs);
    i = 0;
    while (NULL != (entry = adapter_readdir(dirptr, pncdfs)))
    {
        PRINTF_DEBUG("name[%s] i:%d", entry->d_name, i);
        snprintf(rtn[i], 1023, "%s", entry->d_name);
        PRINTF_DEBUG("rtrnname[%s]", rtn[i]);
        i++;
        if (i >= dircount)
            break;
    }
    adapter_closedir(dirptr, pncdfs);
    jobjectArray args = 0;
    PRINTF_DEBUG("i[%d]", i);
    // i是数组大小
    jclass objClass = JNIEnv_FUNCTION_ADAPTER(FindClass, env, "java/lang/String");
    args = JNIEnv_FUNCTION_ADAPTER(NewObjectArray, env, i, objClass, 0);
    int num = 0;
    for (num = 0; num < i; num++)
    {
        PRINTF_DEBUG("num[%d] rtrnname[%s]", num, rtn[i]);
        jstring jstr = JNIEnv_FUNCTION_ADAPTER(NewStringUTF, env, rtn[num]);
        JNIEnv_FUNCTION_ADAPTER(SetObjectArrayElement, env, args, num, jstr);
        if (NULL != rtn[num])
        {
            free(rtn[num]);
            rtn[i] = NULL;
        }
    }
    if (NULL != rtn)
    {
        free(rtn);
    }
    JNIEnv_FUNCTION_ADAPTER(ReleaseStringUTFChars, env, jdir, filename);
    SetExeResultInfo(EXE_SUCCESS, env, obj, NULL);
    PRINTF_DEBUG("end");
    return args;
}