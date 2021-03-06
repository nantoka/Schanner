#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include "de_haidozo_sudoku_SudokuBoardRecognition.h"

using namespace std;
using namespace cv;

JNIEXPORT jstring JNICALL Java_de_haidozo_sudoku_SudokuBoardRecognition_extract
(JNIEnv *env, jobject obj, jlong imgAddr) {
	Mat& mGr  = *(Mat*)imgAddr;
	if(mGr.data) {
		return env->NewStringUTF("Img transfered :D");
	}
	return env->NewStringUTF("Noooot transfered");
}