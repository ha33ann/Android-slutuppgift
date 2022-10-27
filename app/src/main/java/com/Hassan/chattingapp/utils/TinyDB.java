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




    public String getSavedImagePath() {
        return lastImagePath;
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



    //under här skapar jag en metod som heter putImageWithfullPath
    //som kommer att användas för att spara en bild med full path
    public boolean putImageWithFullPath(String fullPath, Bitmap theBitmap) {
        return !(fullPath == null || theBitmap == null) && saveBitmap(fullPath, theBitmap);
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



    //här skapar jag en metod som heter getint
    public int getInt(String key) {
        return preferences.getInt(key, 0);
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


    public long getLong(String key) {
        return preferences.getLong(key, 0);
    }


    public float getFloat(String key) {
        return preferences.getFloat(key, 0);
    }


    public double getDouble(String key) {
        String number = getString(key);

        try {
            return Double.parseDouble(number);

        } catch (NumberFormatException e) {
            return 0;
        }
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


    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
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




    //här skapar jag en metod som heter putint
    public void putInt(String key, int value) {
        checkForNullKey(key);
        preferences.edit().putInt(key, value).apply();
    }


    //här skapar jag en metod som heter putlistint
    public void putListInt(String key, ArrayList<Integer> intList) {
        checkForNullKey(key);
        Integer[] myIntList = intList.toArray(new Integer[intList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myIntList)).apply();
    }


    //här skapar jag en metod som heter putlong
    public void putLong(String key, long value) {
        checkForNullKey(key);
        preferences.edit().putLong(key, value).apply();
    }


    //här skapar jag en metod som heter putlistlong
    public void putListLong(String key, ArrayList<Long> longList) {
        checkForNullKey(key);
        Long[] myLongList = longList.toArray(new Long[longList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myLongList)).apply();
    }


    //här skapar jag en metod som heter putfloat
    public void putFloat(String key, float value) {
        checkForNullKey(key);
        preferences.edit().putFloat(key, value).apply();
    }


    //här skapar jag en metod som heter putdouble
    public void putDouble(String key, double value) {
        checkForNullKey(key);
        putString(key, String.valueOf(value));
    }


    //här skapar jag en metod som heter putlistdouble
    public void putListDouble(String key, ArrayList<Double> doubleList) {
        checkForNullKey(key);
        Double[] myDoubleList = doubleList.toArray(new Double[doubleList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myDoubleList)).apply();
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


    //här skapar jag en metod som heter putboolean
    public void putBoolean(String key, boolean value) {
        checkForNullKey(key);
        preferences.edit().putBoolean(key, value).apply();
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



//    }


    //här skapar jag en metod som heter remove
    public void remove(String key) {
        preferences.edit().remove(key).apply();
    }


    //här skapar jag en metod som heter deleteImage
    public boolean deleteImage(String path) {
        return new File(path).delete();
    }



    //här skapar jag en metod som heter clear
    public void clear() {
        preferences.edit().clear().apply();
    }


    //här skapar jag en metod som heter nap som returnerar preferences
    public Map<String, ?> getAll() {
        return preferences.getAll();
    }



    //här skapar jag en metod som heter registerOnSharedPreferenceChangeListener
    public void registerOnSharedPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {

        preferences.registerOnSharedPreferenceChangeListener(listener);
    }


    //här skapar jag en metod som heter unregisterOnSharedPreferenceChangeListener
    public void unregisterOnSharedPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {

        preferences.unregisterOnSharedPreferenceChangeListener(listener);
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


    //här skapar jag en metod som heter objectExists
    public boolean objectExists(String key){
        String gottenString = getString(key);
        if(gottenString.isEmpty()){
            return false;
        }else {
            return true;
        }

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
