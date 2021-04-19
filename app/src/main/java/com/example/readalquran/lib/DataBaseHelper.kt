package com.example.readalquran.lib

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.readalquran.models.Ayat
import java.io.*
import java.sql.SQLException


class DataBaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private var myDataBase: SQLiteDatabase? = null
    val database: SQLiteDatabase
        get() = myDataBase!!

    private val myContext: Context

    companion object {
        private const val DATABASE_NAME = "data.sqlite"
        const val DATABASE_PATH = "/data/data/com.example.readalquran/databases/"
        const val DATABASE_VERSION = 1
    }

    init {
        myContext = context
    }

    //Create a empty database on the system
    @Throws(IOException::class)
    fun createDatabase() {
        val dbExist = checkDataBase()
        if (dbExist) {
            Log.v("DB Exists", "db exists")
            // By calling this method here onUpgrade will be called on a
            // writeable database, but only if the version number has been
            // bumped
            //onUpgrade(myDataBase, DATABASE_VERSION_old, DATABASE_VERSION);
        }
        val dbExist1 = checkDataBase()
        if (!dbExist1) {
            this.readableDatabase
            try {
                close()
                copyDataBase()
            } catch (e: IOException) {
                throw Error("Error copying database")
            }
        }
    }

    //Check database already exist or not
    private fun checkDataBase(): Boolean {
        var checkDB = false
        try {
            val myPath = DATABASE_PATH + DATABASE_NAME
            val dbfile = File(myPath)
            checkDB = dbfile.exists()
        } catch (e: SQLiteException) {
        }
        return checkDB
    }

    //Copies your database from your local assets-folder to the just created empty database in the system folder
    @Throws(IOException::class)
    private fun copyDataBase() {
        val mInput: InputStream = myContext.getAssets().open(DATABASE_NAME)
        val outFileName = DATABASE_PATH + DATABASE_NAME
        val mOutput: OutputStream = FileOutputStream(outFileName)
        val mBuffer = ByteArray(2024)
        var mLength: Int
        while (mInput.read(mBuffer).also { mLength = it } > 0) {
            mOutput.write(mBuffer, 0, mLength)
        }
        mOutput.flush()
        mOutput.close()
        mInput.close()
    }

    //delete database
    fun db_delete() {
        val file = File(DATABASE_PATH + DATABASE_NAME)
        if (file.exists()) {
            file.delete()
            println("delete database file.")
        }
    }

    //Open database
    @Throws(SQLException::class)
    fun openDatabase() {
        val myPath = DATABASE_PATH + DATABASE_NAME
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE)
    }

    @Synchronized
    @Throws(SQLException::class)
    fun closeDataBase() {
        if (myDataBase != null) myDataBase!!.close()
        super.close()
    }

    override fun onCreate(db: SQLiteDatabase) {
        // TODO Auto-generated method stub
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (newVersion > oldVersion) {
            Log.v("Database Upgrade", "Database version higher than old.")
            db_delete()
        }
    }

    fun getBismillah(): Ayat? {
        var ayat: Ayat? = null
        val cursor: Cursor = this.readableDatabase.rawQuery(
            "SELECT * FROM quran_text WHERE sura LIKE 1 AND aya LIKE 1 LIMIT 1",
            null
        )
        if (cursor.moveToFirst()) {
            do {
                ayat = Ayat(
                    cursor.getString(cursor.getColumnIndex("index")).toInt(),
                    cursor.getString(cursor.getColumnIndex("sura")).toInt(),
                    cursor.getString(cursor.getColumnIndex("aya")).toInt(),
                    cursor.getString(cursor.getColumnIndex("text"))
                )
            } while (cursor.moveToNext());
            cursor.close();
        }
        closeDataBase()
        return ayat
    }
}