package database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domain.InviteMessage;
import domain.InviteMessageDao;
import domain.User;
import domain.UserDao;

/**
 * Created by Administrator on 2015/12/15.
 */
public class DatabaseAdapter {
    public static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
            + UserDao.TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_NICK +" TEXT, "
            + UserDao.COLUMN_NAME_AVATAR +" TEXT, "
            + UserDao.COLUMN_NAME_BEIZHU +" TEXT, "
            + UserDao.COLUMN_NAME_FXID +" TEXT, "
            + UserDao.COLUMN_NAME_REGION +" TEXT, "
            + UserDao.COLUMN_NAME_SEX +" TEXT, "
            + UserDao.COLUMN_NAME_SIGN +" TEXT, "
            + UserDao.COLUMN_NAME_TEL +" TEXT, "
            + UserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";

    public static final String INIVTE_MESSAGE_TABLE_CREATE = "CREATE TABLE "
            + InviteMessageDao.TABLE_NAME + " ("
            + InviteMessageDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + InviteMessageDao.COLUMN_NAME_FROM + " TEXT, "
            + InviteMessageDao.COLUMN_NAME_GROUP_ID + " TEXT, "
            + InviteMessageDao.COLUMN_NAME_GROUP_Name + " TEXT, "
            + InviteMessageDao.COLUMN_NAME_REASON + " TEXT, "
            + InviteMessageDao.COLUMN_NAME_STATUS + " INTEGER, "
            + InviteMessageDao.COLUMN_NAME_ISINVITEFROMME + " INTEGER, "
            + InviteMessageDao.COLUMN_NAME_TIME + " TEXT); ";
    static final int VERSION = 1;

    /**
     * 保存消息
     * @param msg
     * @param db
     */
    public void saveMessage(InviteMessage msg,SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put(InviteMessageDao.COLUMN_NAME_FROM, msg.getFrom());
        values.put(InviteMessageDao.COLUMN_NAME_GROUP_ID, msg.getGroupId());
        values.put(InviteMessageDao.COLUMN_NAME_GROUP_Name, msg.getGroupName());
        values.put(InviteMessageDao.COLUMN_NAME_REASON, msg.getReason());
        values.put(InviteMessageDao.COLUMN_NAME_TIME, msg.getTime());
        values.put(InviteMessageDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
        db.insert(InviteMessageDao.TABLE_NAME, null, values);
    }

    /**
     *
     */
    public List<InviteMessage> queryMessage(SQLiteDatabase db){
        Cursor cursor = db.rawQuery("select * from " + InviteMessageDao.TABLE_NAME + " desc",null);
        List<InviteMessage> msgs = new ArrayList<>();
        while(cursor.moveToNext()){
            InviteMessage msg = new InviteMessage();
            int id = cursor.getInt(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_ID));
            String from = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_FROM));
            String groupid = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_GROUP_ID));
            String groupname = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_GROUP_Name));
            String reason = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_REASON));
            long time = cursor.getLong(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_TIME));
            int status = cursor.getInt(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_STATUS));

            msg.setId(id);
            msg.setFrom(from);
            msg.setGroupId(groupid);
            msg.setGroupName(groupname);
            msg.setReason(reason);
            msg.setTime(time);
            if(status == InviteMessage.InviteMesageStatus.BEINVITEED.ordinal())
                msg.setStatus(InviteMessage.InviteMesageStatus.BEINVITEED);
            else if(status == InviteMessage.InviteMesageStatus.BEAGREED.ordinal())
                msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);
            else if(status == InviteMessage.InviteMesageStatus.BEREFUSED.ordinal())
                msg.setStatus(InviteMessage.InviteMesageStatus.BEREFUSED);
            else if(status == InviteMessage.InviteMesageStatus.AGREED.ordinal())
                msg.setStatus(InviteMessage.InviteMesageStatus.AGREED);
            else if(status == InviteMessage.InviteMesageStatus.REFUSED.ordinal())
                msg.setStatus(InviteMessage.InviteMesageStatus.REFUSED);
            else if(status == InviteMessage.InviteMesageStatus.BEAPPLYED.ordinal()){
                msg.setStatus(InviteMessage.InviteMesageStatus.BEAPPLYED);
            }
            msgs.add(msg);
        }
        return msgs;
    }


    /**
     * 联系人表添加user
     * @param user
     * @param db
     */
    public void insertUser(User user,SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put(UserDao.COLUMN_NAME_ID,user.getUserName());
        if (user.getUsernick() != null){
            values.put(UserDao.COLUMN_NAME_NICK,user.getUsernick());
        }
        if (user.getHeadImage() != null){
            values.put(UserDao.COLUMN_NAME_AVATAR,user.getHeadImage());
        }
        if (user.getBeizhu() != null){
            values.put(UserDao.COLUMN_NAME_BEIZHU ,user.getBeizhu());
        }
        if (user.getFxid() != null){
            values.put(UserDao.COLUMN_NAME_FXID,user.getFxid());
        }
        if (user.getSex() != null){
            values.put(UserDao.COLUMN_NAME_SEX,user.getSex());
        }
        if (user.getTel() != null){
            values.put(UserDao.COLUMN_NAME_TEL,user.getTel());
        }
        db.insert(UserDao.TABLE_NAME,null,values);
    }

    /**
     * 遍历联系人列表
     */
    public Map<String,User> queryContactList(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME /* + " desc" */, null);
        Map<String,User> users = new HashMap<>();
        while (cursor.moveToNext()){
            String username = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_ID));
            String nick = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_NICK));
            String avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));
            String tel = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_TEL));
            String sign = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_SIGN));
            String sex = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_SEX));
            String region = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_REGION));
            String beizhu = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_BEIZHU));
            String fxid = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_FXID));
            User user = new User();
            user.setUserName(username);
            user.setUsernick(nick);
            user.setHeadImage(avatar);
            user.setTel(tel);
            user.setSign(sign);
            user.setSex(sex);
            user.setRegion(region);
            user.setBeizhu(beizhu);
            user.setFxid(fxid);
            users.put(username,user);
        }
        cursor.close();
        return users;//说明已注册
    }

    public boolean isExist(String number,SQLiteDatabase db) {
        Cursor cursor=db.query("LOGIN", null, " PHONENUMBER=?", new String[]{number}, null, null, null);
        if (cursor.getCount() >= 1) // UserName Not Exist
        {
            cursor.close();
            return true;
        } else {
            return false;
        }
    }

    public void deleteEntry(SQLiteDatabase db,String numeber){
        String where="PHONENUMBER=?";
        int numberOFEntriesDeleted = db.delete(UserDao.TABLE_NAME, where, new String[]{numeber}) ;
    }

    public void  updateEntry(String number,String password,SQLiteDatabase db)
    {
        // Define the updated row content.
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.
        updatedValues.put("PHONENUMBER",number);
        updatedValues.put("PASSWORD",password);

        String where="PHONENUMBER = ?";
        db.update(UserDao.TABLE_NAME, updatedValues, where, new String[]{number});
    }
}
