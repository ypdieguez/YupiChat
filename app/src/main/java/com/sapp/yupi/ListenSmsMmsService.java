package com.sapp.yupi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ListenSmsMmsService extends Service {

    private ContentResolver contentResolver;


    String substr;
    int k;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.v("Debug", " service creatd.........");
    }

    public void registerObserver() {

        contentResolver = getContentResolver();
        contentResolver.registerContentObserver(
                Uri.parse("content://mms-sms/conversations/"),
                true, new SMSObserver(new Handler()));
        Log.v("Debug", " in registerObserver method.........");
    }

    //start the service and register observer for lifetime
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("Debug", "Service has been started..");
        Toast.makeText(getApplicationContext(),
                "Service has been started.. ",
                Toast.LENGTH_SHORT).show();
        registerObserver();

        return START_STICKY;
    }

    class SMSObserver extends ContentObserver{

        public SMSObserver(Handler handler) {
            super(handler);
        }

        //will be called when database get change
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.v("Debug", "Observer has been started..");

            /*first of all we need to decide message is Text or MMS type.*/
            final String[] projection = new String[]{
                    "_id", "ct_t"};
            Uri mainUri = Uri.parse(
                    "content://mms-sms/conversations/");
            Cursor mainCursor = contentResolver.
                    query(mainUri, projection,
                            null, null, null);
            mainCursor.moveToFirst();

            String msgContentType = mainCursor.getString(mainCursor.
                    getColumnIndex("ct_t"));
            if ("application/vnd.wap.multipart.related".
                    equals(msgContentType)) {
                // it's MMS
                Log.v("Debug", "it's MMS");

                //now we need to decide MMS message is sent or received
                Uri mUri = Uri.parse("content://mms");
                Cursor mCursor = contentResolver.query(mUri, null, null,
                        null, null);
                mCursor.moveToNext();
                int type = mCursor.getInt(mCursor.getColumnIndex("type"));

                if(type==1){
                    //it's received MMS
                    Log.v("Debug", "it's received MMS");
                    getReceivedMMSinfo();
                }
                else if(type==2)
                {
                    //it's sent MMS
                    Log.v("Debug", "it's Sent MMS");
                    getSentMMSinfo();
                }

            }
            else{
                // it's SMS
                Log.v("Debug", "it's SMS");

                //now we need to decide SMS message is sent or received
                Uri mUri = Uri.parse("content://sms");
                Cursor mCursor = contentResolver.query(mUri, null, null,
                        null, null);
                mCursor.moveToNext();
                int type = mCursor.getInt(mCursor.getColumnIndex("type"));

                if(type==1){
                    //it's received SMS
                    Log.v("Debug", "it's received SMS");
                    getReceivedSMSinfo();
                }
                else if(type==2)
                {
                    //it's sent SMS
                    Log.v("Debug", "it's sent SMS");
                    getSentSMSinfo();
                }
            }//message content type block closed


        }//on changed closed


        /*now Methods start to getting details for sent-received SMS*/



        //method to get details about received SMS..........
        private void getReceivedSMSinfo() {
            Uri uri = Uri.parse("content://sms/inbox");
            String str = "";
            Cursor cursor = contentResolver.query(uri, null,
                    null,null, null);
            cursor.moveToNext();

            // 1 = Received, etc.
            int type = cursor.getInt(cursor.
                    getColumnIndex("type"));
            String msg_id= cursor.getString(cursor.
                    getColumnIndex("_id"));
            String phone = cursor.getString(cursor.
                    getColumnIndex("address"));
            String dateVal = cursor.getString(cursor.
                    getColumnIndex("date"));
            String body = cursor.getString(cursor.
                    getColumnIndex("body"));
            Date date = new Date(Long.valueOf(dateVal));

            str = "Received SMS: \n phone is: " + phone;
            str +="\n SMS type is: "+type;
            str +="\n SMS time stamp is:"+date;
            str +="\n SMS body is: "+body;
            str +="\n id is : "+msg_id;


            Log.v("Debug","Received SMS phone is: "+phone);
            Log.v("Debug","SMS type is: "+type);
            Log.v("Debug","SMS time stamp is:"+date);
            Log.v("Debug","SMS body is: "+body);
            Log.v("Debug","SMS id is: "+msg_id);

            Toast.makeText(getBaseContext(), str,
                    Toast.LENGTH_SHORT).show();
            Log.v("Debug", "RDC : So we got all informaion " +
                    "about SMS Received Message :) ");

        }

        //method to get details about Sent SMS...........
        private void getSentSMSinfo() {
            Uri uri = Uri.parse("content://sms/sent");
            String str = "";
            Cursor cursor = contentResolver.query(uri, null,
                    null, null, null);
            cursor.moveToNext();

            // 2 = sent, etc.
            int type = cursor.getInt(cursor.
                    getColumnIndex("type"));
            String msg_id= cursor.getString(cursor.
                    getColumnIndex("_id"));
            String phone = cursor.getString(cursor.
                    getColumnIndex("address"));
            String dateVal = cursor.getString(cursor.
                    getColumnIndex("date"));
            String body = cursor.getString(cursor.
                    getColumnIndex("body"));
            Date date = new Date(Long.valueOf(dateVal));

            str = "Sent SMS: \n phone is: " + phone;
            str +="\n SMS type is: "+type;
            str +="\n SMS time stamp is:"+date;
            str +="\n SMS body is: "+body;
            str +="\n id is : "+msg_id;


            Log.v("Debug","sent SMS phone is: "+phone);
            Log.v("Debug","SMS type is: "+type);
            Log.v("Debug","SMS time stamp is:"+date);
            Log.v("Debug","SMS body is: "+body);
            Log.v("Debug","SMS id is: "+msg_id);

            Toast.makeText(getBaseContext(), str,
                    Toast.LENGTH_SHORT).show();
            Log.v("Debug", "RDC : So we got all informaion " +
                    "about Sent SMS Message :) ");
        }


        /*now Methods start to getting details for sent-received MMS.*/

        // 1. method to get details about Received (inbox)  MMS...
        private void getReceivedMMSinfo() {
            Uri uri = Uri.parse("content://mms/inbox");
            String str = "";
            Cursor cursor = getContentResolver().query(uri, null,null,
                    null, null);
            cursor.moveToNext();

            String mms_id= cursor.getString(cursor.
                    getColumnIndex("_id"));
            String phone = cursor.getString(cursor.
                    getColumnIndex("address"));
            String dateVal = cursor.getString(cursor.
                    getColumnIndex("date"));
            Date date = new Date(Long.valueOf(dateVal));

            // 2 = sent, etc.
            int mtype = cursor.getInt(cursor.
                    getColumnIndex("type"));
            String body="";

            Bitmap bitmap;

            String type = cursor.getString(cursor.
                    getColumnIndex("ct"));
            if ("text/plain".equals(type)){
                String data = cursor.getString(cursor.
                        getColumnIndex("body"));
                if(data != null){
                    body = getReceivedMmsText(mms_id);
                }
                else {
                    body = cursor.getString(cursor.
                            getColumnIndex("text"));
                    //body text is stored here
                }
            }
            else if("image/jpeg".equals(type) ||
                    "image/bmp".equals(type) ||
                    "image/gif".equals(type) ||
                    "image/jpg".equals(type) ||
                    "image/png".equals(type)){
                bitmap = getReceivedMmsImage(mms_id);
                //image is stored here
                //now we are storing on SDcard
                storeMmsImageOnSDcard(bitmap);
            }

            str = "Sent MMS: \n phone is: " + phone;
            str +="\n MMS type is: "+mtype;
            str +="\n MMS time stamp is:"+date;
            str +="\n MMS body is: "+body;
            str +="\n id is : "+mms_id;


            Log.v("Debug","sent MMS phone is: "+phone);
            Log.v("Debug","MMS type is: "+mtype);
            Log.v("Debug","MMS time stamp is:"+date);
            Log.v("Debug","MMS body is: "+body);
            Log.v("Debug","MMS id is: "+mms_id);

            Toast.makeText(getBaseContext(), str,
                    Toast.LENGTH_SHORT).show();
            Log.v("Debug", "RDC : So we got all informaion " +
                    "about Received MMS Message :) ");
        }




        //method to get Text body from Received MMS.........
        private String getReceivedMmsText(String id) {
            Uri partURI = Uri.parse("content://mms/inbox" + id);
            InputStream is = null;
            StringBuilder sb = new StringBuilder();
            try {
                is = getContentResolver().openInputStream(partURI);
                if (is != null) {
                    InputStreamReader isr = new InputStreamReader(is,
                            "UTF-8");
                    BufferedReader reader = new BufferedReader(isr);
                    String temp = reader.readLine();
                    while (temp != null) {
                        sb.append(temp);
                        temp = reader.readLine();
                    }
                }
            } catch (IOException e) {}
            finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {}
                }
            }
            return sb.toString();
        }

        //method to get image from Received MMS..............
        private Bitmap getReceivedMmsImage(String id) {


            Uri partURI = Uri.parse("content://mms/inbox" + id);
            InputStream is = null;
            Bitmap bitmap = null;
            try {
                is = getContentResolver().
                        openInputStream(partURI);
                bitmap = BitmapFactory.decodeStream(is);
            } catch (IOException e) {}
            finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {}
                }
            }
            return bitmap;

        }

        //Storing image on SD Card
        private void storeMmsImageOnSDcard(Bitmap bitmap) {
            try {

                substr = "A " +k +".PNG";
                String extStorageDirectory = Environment.
                        getExternalStorageDirectory().toString();
                File file = new File(extStorageDirectory, substr);
                OutputStream outStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG,
                        100,outStream);
                outStream.flush();
                outStream.close();

                Toast.makeText(getApplicationContext(), "Image Saved",
                        Toast.LENGTH_LONG).show();
                Log.v("Debug", "Image seved sucessfully");
            }
            catch (FileNotFoundException e) {

                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        e.toString(),
                        Toast.LENGTH_LONG).show();
            } catch (IOException e) {

                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        e.toString(),
                        Toast.LENGTH_LONG).show();
            }
            k++;
        }



        /* .......methods to get details about Sent MMS.... */
        private void getSentMMSinfo() {


            Uri uri = Uri.parse("content://mms/sent");
            String str = "";
            Cursor cursor = getContentResolver().query(uri,
                    null,null,
                    null, null);
            cursor.moveToNext();

            String mms_id= cursor.getString(cursor.
                    getColumnIndex("_id"));
            String phone = cursor.getString(cursor.
                    getColumnIndex("address"));
            String dateVal = cursor.getString(cursor.
                    getColumnIndex("date"));
            Date date = new Date(Long.valueOf(dateVal));
            // 2 = sent, etc.
            int mtype = cursor.getInt(cursor.
                    getColumnIndex("type"));
            String body="";

            Bitmap bitmap;

            String type = cursor.getString(cursor.
                    getColumnIndex("ct"));
            if ("text/plain".equals(type)){
                String data = cursor.getString(cursor.
                        getColumnIndex("body"));
                if(data != null){
                    body = getSentMmsText(mms_id);
                }
                else {
                    body = cursor.getString(cursor.
                            getColumnIndex("text"));
                    //body text is stored here
                }
            }
            else if("image/jpeg".equals(type) ||
                    "image/bmp".equals(type) ||
                    "image/gif".equals(type) ||
                    "image/jpg".equals(type) ||
                    "image/png".equals(type)){
                bitmap = getSentMmsImage(mms_id);
                //image is stored here
                //now we are storing on SDcard
                storeMmsImageOnSDcard(bitmap);
            }

            str = "Sent MMS: \n phone is: " + phone;
            str +="\n MMS type is: "+mtype;
            str +="\n MMS time stamp is:"+date;
            str +="\n MMS body is: "+body;
            str +="\n id is : "+mms_id;


            Log.v("Debug","sent MMS phone is: "+phone);
            Log.v("Debug","MMS type is: "+mtype);
            Log.v("Debug","MMS time stamp is:"+date);
            Log.v("Debug","MMS body is: "+body);
            Log.v("Debug","MMS id is: "+mms_id);

            Toast.makeText(getBaseContext(), str,
                    Toast.LENGTH_SHORT).show();
            Log.v("Debug", "RDC : So we got all informaion " +
                    "about Sent MMS Message :) ");

        }


        //method to get Text body from Sent MMS............
        private String getSentMmsText(String id) {

            Uri partURI = Uri.parse("content://mms/sent" + id);
            InputStream is = null;
            StringBuilder sb = new StringBuilder();
            try {
                is = getContentResolver().openInputStream(partURI);
                if (is != null) {
                    InputStreamReader isr = new InputStreamReader(is,
                            "UTF-8");
                    BufferedReader reader = new BufferedReader(isr);
                    String temp = reader.readLine();
                    while (temp != null) {
                        sb.append(temp);
                        temp = reader.readLine();
                    }
                }
            } catch (IOException e) {}
            finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {}
                }
            }
            return sb.toString();

        }

        //method to get image from sent MMS............
        private Bitmap getSentMmsImage(String id) {

            Uri partURI = Uri.parse("content://mms/sent" + id);
            InputStream is = null;
            Bitmap bitmap = null;
            try {
                is = getContentResolver().
                        openInputStream(partURI);
                bitmap = BitmapFactory.decodeStream(is);
            } catch (IOException e) {}
            finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {}
                }
            }
            return bitmap;

        }

    }//smsObserver class closed
}
