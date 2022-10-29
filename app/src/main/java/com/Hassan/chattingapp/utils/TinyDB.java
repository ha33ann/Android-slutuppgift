package com.Hassan.chattingapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class TinyDB {
    //under här skapar jag en ny fil som heter tinydb
    //denna fil kommer att användas för att spara data
    private Context context;
    private SharedPreferences preferences;
    private String DEFAULT_APP_IMAGEDATA_DIRECTORY;
    private String lastImagePath = "";

    //här skapar jag en konstruktor som tar emot en context
    public TinyDB(Context appContext) {
        preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        context = appContext;
    }



    //här skapar jag en metod som heter putListString
    //denna metod kommer att användas för att spara en lista med strängar
    public Bitmap getImage(String path) {
        //här skapar jag en ny bitmap som heter image
        Bitmap bitmapFromPath = null;
        try {
            bitmapFromPath = BitmapFactory.decodeFile(path);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return bitmapFromPath;
    }
    



    //under här skapar jag en metod som heter putimage
    public String putImage(String theFolder, String theImageName, Bitmap theBitmap) {
        //om thefolder är tom så kommer den att skapa en ny folder som heter tinydb
        //annars kommer den att använda den folder som användaren har skrivit in
        if (theFolder == null || theImageName == null || theBitmap == null)
            return null;

        this.DEFAULT_APP_IMAGEDATA_DIRECTORY = theFolder;
        String mFullPath = setupFullPath(theImageName);

        //om mfullpath är tom så kommer den att returnera null
        //annars kommer den att returnera mfullpath
        if (!mFullPath.equals("")) {
            lastImagePath = mFullPath;
            saveBitmap(mFullPath, theBitmap);
        }

        return mFullPath;
    }



    //här skapar jag en metod som heter setupfullpath
    private String setupFullPath(String imageName) {
        File mFolder = new File(context.getExternalFilesDir(null), DEFAULT_APP_IMAGEDATA_DIRECTORY);

        //om mfolder inte existerar så kommer den att skapa en ny folder
        if (isExternalStorageReadable() && isExternalStorageWritable() && !mFolder.exists()) {
            if (!mFolder.mkdirs()) {
                Log.e("ERROR", "Failed to setup folder");
                return "";
            }
        }

        return mFolder.getPath() + '/' + imageName;
    }


    //här skapar jag en metod som heter savebitmap
    private boolean saveBitmap(String fullPath, Bitmap bitmap) {
        //om fullpath är tom så kommer den att returnera false
        if (fullPath == null || bitmap == null)
            return false;

        boolean fileCreated = false;
        boolean bitmapCompressed = false;
        boolean streamClosed = false;

        //här skapar jag en ny file som heter imagefile
        File imageFile = new File(fullPath);

        //om imagefile inte existerar så kommer den att skapa en ny file
        //annars kommer den att returnera false
        if (imageFile.exists())
            if (!imageFile.delete())
                return false;

        //try catch som kommer att användas för att spara bilden
        try {
            fileCreated = imageFile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //om fileoutputstream är tom så kommer den att returnera false
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imageFile);
            bitmapCompressed = bitmap.compress(CompressFormat.PNG, 100, out);

        } catch (Exception e) {
            e.printStackTrace();
            bitmapCompressed = false;

        }
        //try catch som kommer att användas för att stänga streamen
        finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                    streamClosed = true;

                } catch (IOException e) {
                    e.printStackTrace();
                    streamClosed = false;
                }
            }
        }

        return (fileCreated && bitmapCompressed && streamClosed);
    }



    //här under skapar jag en arraylist som returnerar key
    public ArrayList<Integer> getListInt(String key) {
        String[] myList = TextUtils.split(preferences.getString(key, ""), "‚‗‚");
        ArrayList<String> arrayToList = new ArrayList<String>(Arrays.asList(myList));
        ArrayList<Integer> newList = new ArrayList<Integer>();

        for (String item : arrayToList)
            newList.add(Integer.parseInt(item));

        return newList;
    }



    //här skapar jag en arraylist som returnerar key
    public ArrayList<Double> getListDouble(String key) {
        String[] myList = TextUtils.split(preferences.getString(key, ""), "‚‗‚");
        ArrayList<String> arrayToList = new ArrayList<String>(Arrays.asList(myList));
        ArrayList<Double> newList = new ArrayList<Double>();

        for (String item : arrayToList)
            newList.add(Double.parseDouble(item));

        return newList;
    }


    //här skapar jag en arraylist som returnerar key
    public ArrayList<Long> getListLong(String key) {
        String[] myList = TextUtils.split(preferences.getString(key, ""), "‚‗‚");
        ArrayList<String> arrayToList = new ArrayList<String>(Arrays.asList(myList));
        ArrayList<Long> newList = new ArrayList<Long>();

        for (String item : arrayToList)
            newList.add(Long.parseLong(item));

        return newList;
    }


    public String getString(String key) {
        return preferences.getString(key, "");
    }


    public ArrayList<String> getListString(String key) {
        return new ArrayList<String>(Arrays.asList(TextUtils.split(preferences.getString(key, ""), "‚‗‚")));
    }



    //här skapar jag en arraylist som returnerar key
    public ArrayList<Boolean> getListBoolean(String key) {
        ArrayList<String> myList = getListString(key);
        ArrayList<Boolean> newList = new ArrayList<Boolean>();

        for (String item : myList) {
            if (item.equals("true")) {
                newList.add(true);
            } else {
                newList.add(false);
            }
        }

        return newList;
    }


    //här skapar jag en metod som heter putstring
    public void putString(String key, String value) {
        checkForNullKey(key); checkForNullValue(value);
        preferences.edit().putString(key, value).apply();
    }


    //här skapar jag en metod som heter putliststring
    public void putListString(String key, ArrayList<String> stringList) {
        checkForNullKey(key);
        String[] myStringList = stringList.toArray(new String[stringList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply();
    }




    //här skapar jag en metod som heter putlistboolean
    public void putListBoolean(String key, ArrayList<Boolean> boolList) {
        checkForNullKey(key);
        ArrayList<String> newList = new ArrayList<String>();

        //här skapar jag en for loop som går igenom boolList
        for (Boolean item : boolList) {
            if (item) {
                newList.add("true");
            } else {
                newList.add("false");
            }
        }

        putListString(key, newList);
    }







    //här skapar jag en metod som heter nap som returnerar preferences
    public Map<String, ?> getAll() {
        return preferences.getAll();
    }




    //här skapar jag en metod som heter isexternalstoragewritable
    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }


    //här skapar jag en metod som heter isexternalstoragereadable
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }




    //här skapar jag en metod som heter checkForNullKey
    //som kollar om key är null
    private void checkForNullKey(String key){
        if (key == null){
            throw new NullPointerException();
        }
    }

    //här skapar jag en metod som heter checkForNullValue
    //som kollar om value är null
    private void checkForNullValue(String value){
        if (value == null){
            throw new NullPointerException();
        }
    }
}
