package colourwhirl.manpreet.com.colourwhirl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HighScoreDb {

    public static String ID = "score_id";
    public static String HIGH_SCORE= " High_Score";

    public static String TABLE_NAME= "highScoreTable";
    public static String CREATE_TABLE_HIGHSCORE_TABLE= "create table " + TABLE_NAME + "("+ ID + " integer not null primary key, "
            +HIGH_SCORE+" integer not null);";

    public static final String DATABASE_NAME = "HIGH_SCORE_DB";
    public static final int DATABASE_VERSION = 1;

    private final Context context;
    private DatabaseEditor DBeditor;
    private SQLiteDatabase db;

    public HighScoreDb(Context ctx){
        this.context = ctx;
        DBeditor = new DatabaseEditor(context);
    }

    private static class DatabaseEditor extends SQLiteOpenHelper{

        public DatabaseEditor(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase sdb){
            try{
                sdb.execSQL(CREATE_TABLE_HIGHSCORE_TABLE);
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

        }
    }

    public HighScoreDb open() throws SQLException{
        db = DBeditor.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON;");
        return this;
    }

    public void close(){
        DBeditor.close();
    }

    public void updateHighScore(int score){
        Cursor record = getScore(1);
        int highScore = 0;

        if(record.moveToFirst()) {
            highScore = record.getInt(1);
            if(score > highScore){
                updateScore(1, score);
            }
        }
        else{
            insertNewHighScore(1,score);
        }
    }
    public int getHighScore(){
        Cursor record = getScore(1);

        if(record.moveToFirst()) {
            return record.getInt(1);
        }
        else{
            return 0;
        }
    }

    public long insertNewHighScore(int Id,int newScore){
        ContentValues highScoreInfo= new ContentValues();
        highScoreInfo.put(ID, Id);
        highScoreInfo.put(HIGH_SCORE, newScore);
        return db.insert(TABLE_NAME, null, highScoreInfo);
    }

    public boolean updateScore(long Id, int newScore){
        ContentValues newInfo = new ContentValues();
        newInfo.put(HIGH_SCORE, newScore);
        return db.update(TABLE_NAME, newInfo, ID + " = " + Id, null) > 0;
    }

    public Cursor getScore(long Id)throws SQLException{
        Cursor myCursor = db.query(true, TABLE_NAME, new String[]{ID, HIGH_SCORE}, ID+" = " +Id, null, null, null, null, null );
        if(myCursor != null){
            myCursor.moveToFirst();
        }
        return myCursor;
    }

}