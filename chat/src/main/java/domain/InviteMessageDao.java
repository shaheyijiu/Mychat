package domain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import database.DatabaseAdapter;
import database.DatabaseHelper;

public class InviteMessageDao {
    public static final String TABLE_NAME = "new_friends_msgs";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_FROM = "username";
    public static final String COLUMN_NAME_GROUP_ID = "groupid";
    public static final String COLUMN_NAME_GROUP_Name = "groupname";

    public static final String COLUMN_NAME_TIME = "time";
    public static final String COLUMN_NAME_REASON = "reason";
    public static final String COLUMN_NAME_STATUS = "status";
    public static final String COLUMN_NAME_ISINVITEFROMME = "isInviteFromMe";

    private DatabaseHelper databaseHelper;
    private DatabaseAdapter databaseAdapter;

    public InviteMessageDao(Context context){
        databaseHelper = DatabaseHelper.getInstance(context);
        databaseAdapter = new DatabaseAdapter();
    }

    /**
     *
     * @param msg
     * @return 返回这条messaged在db中的id
     */
    public synchronized Integer saveMessage(InviteMessage msg){
        int id = -1;
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        databaseAdapter.saveMessage(msg,db);
        Cursor cursor = db.rawQuery("select last_insert_rowid() from " + TABLE_NAME,null);
        if(cursor.moveToFirst()){
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    /**
     * 更新message
     * @param msgId
     * @param values
     */
    public void updateMessage(int msgId,ContentValues values){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        if(db.isOpen()){
            db.update(TABLE_NAME, values, COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(msgId)});
        }
    }

    /**
     * @return 获得朋友申请消息
     */
    public List<InviteMessage> getMessageList(){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        return databaseAdapter.queryMessage(db);
    }

    /**
     * 删除消息
     */
    public void deleteAllMessage(){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        if(db.isOpen()){
            db.execSQL("DELETE FROM "+TABLE_NAME);
        }
        if(db!=null){
            db.close();
        }
    }

}
