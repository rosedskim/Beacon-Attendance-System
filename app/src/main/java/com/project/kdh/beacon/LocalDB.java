package com.project.kdh.beacon;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class LocalDB {
    private static String userId; // 로그인한 유저 아이디
    private static int userAuth; // 로그인한 유저 권한
    private static ArrayList<Lecture> lectures = new ArrayList<Lecture>(); // 로그인한 유저의 강의 리스트

    //==================================================================================================

    // Firebase
    private static FirebaseAuth mAuth;
    private static FirebaseDatabase mDatabase;
    private static DatabaseReference mRef;

    //==================================================================================================

    // 교수 --> 학생 출석 기록 확인 위한, 학생 id 담는 컨테이너
    private static ArrayList<String> studentIds;

    public static ArrayList<String> getStudentIds() {
        return studentIds;
    }

    public static void setStudentIds(ArrayList<String> studentIds) {
        LocalDB.studentIds = studentIds;
    }
    //==================================================================================================

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        LocalDB.userId = userId;

        if(LocalDB.userId .charAt(0) == '0'){
            LocalDB.userAuth = 0; // 교수&조교 권한
        }else{
            LocalDB.userAuth = 1; // 학생 권한
        }
    }

    public static int getUserAuth() {
        return userAuth;
    }

    public static void setUserAuth(int userAuth) {
        LocalDB.userAuth = userAuth;
    }

    public static ArrayList<Lecture> getLectures() {
        return lectures;
    }

    public static void setLectures(ArrayList<Lecture> lectures) {
        LocalDB.lectures = lectures;
    }

    public static FirebaseAuth getmAuth() {
        return mAuth;
    }

    public static void setmAuth(FirebaseAuth mAuth) {
        LocalDB.mAuth = mAuth;
    }

    public static FirebaseDatabase getmDatabase() {
        return mDatabase;
    }

    public static void setmDatabase(FirebaseDatabase mDatabase) {
        LocalDB.mDatabase = mDatabase;
    }

    public static DatabaseReference getmRef() {
        return mRef;
    }

    public static void setmRef(DatabaseReference mRef) {
        LocalDB.mRef = mRef;
    }

    public static void defaultSetting(){

        LocalDB.userId = null;
        LocalDB.userAuth = -1;
        LocalDB.lectures = new ArrayList<Lecture>();

        LocalDB.mAuth = null;
        LocalDB.mDatabase = null;
        LocalDB.mRef = null;

    }
}