package com.friendsteam_cafehak.app;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;


public class DB_Helper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TaskDSS.db";

    public DB_Helper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE  courses (userid TEXT ,course1 TEXT, course2 TEXT, course3 TEXT, course4 TEXT,course5 TEXT,fname TEXT,lname TEXT ,email TEXT)";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    private boolean ISUID(String userid) {
        ArrayList<String> list = SelectCourses(userid);
        return list != null;
    }

    public boolean insertname(String fname, String lname, String email, String userID) {
        if (ISUID(userID)) {
            SQLiteDatabase db = this.getWritableDatabase();
            SQLiteStatement sqLiteStatement = db.compileStatement("UPDATE  courses SET fname=?, lname=?, email=? WHERE userid=?");
            sqLiteStatement.bindString(1, fname);
            sqLiteStatement.bindString(2, lname);
            sqLiteStatement.bindString(3, email);
            sqLiteStatement.bindString(4, userID);
            sqLiteStatement.executeUpdateDelete();
            db.close();
            return true;
        }
        return false;
    }

    public boolean insertCourse(ArrayList<String> courses, String userId) {
        try {
            if (!ISUID(userId)) {
                SQLiteDatabase db = this.getWritableDatabase();
                SQLiteStatement sqLiteStatement = db.compileStatement(
                        "INSERT INTO courses (userid, course1, course2, course3, course4, course5) VALUES (?,?,?,?,?,?)"
                );
                sqLiteStatement.bindString(1, userId);
                int counter = 2;
                for (String name : courses) {
                    sqLiteStatement.bindString(counter, name);
                    counter++;
                    if (counter > 6)
                        break;
                }
                sqLiteStatement.executeInsert();
                db.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String[] getnames(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String table = "courses";
        String[] columnsToReturn = {"fname", "lname"};
        String selection = "email =?";
        String[] selectionArgs = {email}; // matched to "?" in selection
        Cursor dbCursor = db.query(table, columnsToReturn, selection, selectionArgs, null, null, null);
        if (dbCursor.moveToFirst()) {
            {
                columnsToReturn[0] = dbCursor.getString(0);
                columnsToReturn[1] = dbCursor.getString(1);
            }
        }
        db.close();
        return columnsToReturn;

    }

    public boolean updateCouese(String name, String userid, int position) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            SQLiteStatement sqLiteStatement = db.compileStatement("UPDATE courses SET course" + position + "=? WHERE userid=?");
            sqLiteStatement.bindString(1, name);
            sqLiteStatement.bindString(2, userid);
            sqLiteStatement.executeUpdateDelete();
            db.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ArrayList<String> SelectCourses(String userid) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM courses WHERE userid='" + userid + "'";
            Cursor cursor = db.rawQuery(query, null);
            ArrayList<String> course = new ArrayList<>(5);
            if (cursor.moveToLast()) {
                do {
                    course.add(cursor.getString(1));
                    course.add(cursor.getString(2));
                    course.add(cursor.getString(3));
                    course.add(cursor.getString(4));
                    course.add(cursor.getString(5));
                } while (cursor.moveToNext());
                db.close();
                return course;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
