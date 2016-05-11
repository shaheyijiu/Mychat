package domain;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;
import java.util.Map;

import database.DatabaseAdapter;
import database.DatabaseHelper;

/**
 * Created by Administrator on 2016/1/14.
 * 数据库中读取和保存联系人
 */
public class UserDao {
    public static final String TABLE_NAME = "users";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_NICK = "nick";
    public static final String COLUMN_NAME_SEX = "sex";
    public static final String COLUMN_NAME_AVATAR = "avatar";
    public static final String COLUMN_NAME_SIGN = "sign";
    public static final String COLUMN_NAME_TEL = "tel";
    public static final String COLUMN_NAME_FXID = "fxid";
    public static final String COLUMN_NAME_REGION = "region";
    public static final String COLUMN_NAME_BEIZHU = "beizhu";
    public static final String COLUMN_NAME_IS_STRANGER = "is_stranger";

    private DatabaseHelper databaseHelper;
    private DatabaseAdapter databaseAdapter;

    public UserDao(Context context){
        databaseHelper = DatabaseHelper.getInstance(context);
        databaseAdapter = new DatabaseAdapter();
    }

    public void saveInviteMessage(InviteMessage msg){

    }


    public void saveContact(User user){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        databaseAdapter.insertUser(user,db);
    }
    public Map<String,User> getContactList(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        return databaseAdapter.queryContactList(db);
    }

    public void saveContactList(List<User> contactList){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        for (int i = 0;i < contactList.size();i++){
            User user = contactList.get(i);
            databaseAdapter.insertUser(user,db);
        }
    }

    public void clearContactList(){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL("DELETE FROM users");
    }
}
