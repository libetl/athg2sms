package org.toilelibre.libe.athg2sms.androidstuff;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.BaseColumns;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.provider.DocumentFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class FileRetriever {

    
    private static final String EXTERNAL_MEDIA = "external"; 
    
    public static String getFile (Context activity, String filename) throws FileNotFoundException {
        if (filename == null) {
            return null;
            
        }
        try {
            return readFileFromFilesContentUri (activity, filename);
        } catch (FileNotFoundException e4) {
            try {
                return readFileFromUri (activity, filename);
            } catch (FileNotFoundException e2) {
                try {
                    return tryToOpenWithMediaStoreReader (activity, filename);
                } catch (FileNotFoundException e3) {
                    try {
                        return tryToOpenWithContentProvider (activity, filename);
                    } catch (FileNotFoundException e1) {
                        try {
                            return tryToOpenWithAsASimpleFile (filename);
                        } catch (FileNotFoundException e) {
                            try {
                                return tryToOpenWithAsAPicture (activity, filename);
                            } catch (FileNotFoundException e5) {
                                try {
                                    return readTextFromUri (activity, filename);
                                } catch (FileNotFoundException e6) {
                                    try {
                                        return readTextFromDocumentFile (activity, filename);
                                    } catch (FileNotFoundException e7) {
                                        try {
                                            return readTextFromDocumentContract (activity, filename);
                                        } catch (Exception e8) {
                                            throw new FileNotFoundException (filename);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uriS The Uri to query.
     * @throws FileNotFoundException 
     */
    @SuppressLint ("NewApi")
    public static String readTextFromDocumentContract(final Context context, final String uriS) throws FileNotFoundException {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        final Uri uri = uriS.startsWith ("content:/") ? Uri.parse (uriS) : Uri.parse ("content:/" + uriS);

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @throws FileNotFoundException 
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
            String[] selectionArgs) throws FileNotFoundException {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        throw new FileNotFoundException (uri.toString ());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
    private static String readTextFromDocumentFile (Context activity, String filename) throws FileNotFoundException {
        try {
            DocumentFile docFile = DocumentFile.fromTreeUri (activity, Uri.parse (filename));
            return readTextFromUri (activity, docFile == null ? "" : docFile.getUri ().toString ());
        } catch (IllegalArgumentException iae) {
            DocumentFile docFile = DocumentFile.fromSingleUri (activity, Uri.parse (filename));
            return readTextFromUri (activity, docFile == null ? "" : docFile.getUri ().toString ());
        }
    }
    
    private static String readTextFromUri (Context activity, String filename) throws FileNotFoundException {
        if ("".equals (filename)) {
            throw new FileNotFoundException (filename);
        }
        InputStream inputStream = activity.getContentResolver ().openInputStream (Uri.parse (filename));
        BufferedReader reader = new BufferedReader (new InputStreamReader (inputStream));
        StringBuilder stringBuilder = new StringBuilder ();
        String line;
        try {
            while ( (line = reader.readLine ()) != null) {
                stringBuilder.append (line);
            }
        } catch (IOException e) {
            throw new FileNotFoundException (filename);
        }
        return stringBuilder.toString ();
    }
    
    private static String tryToOpenWithAsAPicture (Context activity, String filename) throws FileNotFoundException {
        String realPath = null;
        if (filename.indexOf (':') == -1) {
            throw new FileNotFoundException (filename);
        }
        final String id = filename.split (":") [1];
        
        final String [] column = { MediaColumns.DATA };
        
        final String where = BaseColumns._ID + "=?";
        final Cursor cursor = activity.getContentResolver ().query (MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, where, new String [] { id }, null);
        if (cursor == null) {
            throw new FileNotFoundException (filename);
        }
        if (cursor.moveToFirst ()) {
            final int columnIndex = cursor.getColumnIndex (column [0]);
            realPath = cursor.getString (columnIndex);
        }
        cursor.close ();
        if (realPath == null) {
            throw new FileNotFoundException (filename);
        }
        return tryToOpenWithAsASimpleFile (realPath);
    }
    
    @SuppressLint ("NewApi")
    private static String readFileFromFilesContentUri (Context activity, String filename) throws FileNotFoundException {
        if (filename.indexOf ('/') == -1) {
            throw new FileNotFoundException (filename);
        }
        String [] path = filename.split ("/");
        if (path.length < 3) {
            throw new FileNotFoundException (filename);
        }
        return tryToOpenWithContentProvider (activity, Files.getContentUri (EXTERNAL_MEDIA, parseLong (path [2])).toString ());
    }
    
    @SuppressLint ("NewApi")
    private static Uri getCompleteUriPath (String fileName) throws FileNotFoundException {
        if (fileName.indexOf ('/') == -1) {
            throw new FileNotFoundException (fileName);
        }
        String [] path = fileName.split ("/");
        if (path.length < 3) {
            throw new FileNotFoundException (fileName);
        }
        return MediaStore.Files.getContentUri (path [1], parseLong (path [2]));
    }
    
    private static String tryToOpenWithMediaStoreReader (Context activity, String filename) throws FileNotFoundException {
        return tryToOpenWithContentProvider (activity, getCompleteUriPath (filename).toString ());
    }
    
    private static String tryToOpenWithAsASimpleFile (String filename) throws FileNotFoundException {
        Scanner scan = new Scanner (new File (filename));
        
        scan.useDelimiter ("\\A");
        final String content = scan.next ();
        scan.close ();
        return content;
    }
    
    private static String tryToOpenWithContentProvider (Context activity, String filename) throws FileNotFoundException {
        ParcelFileDescriptor descriptor = null;
        Uri finalUri = Uri.parse ( (filename.startsWith ("content:/") ? "" : "content:/") + filename);
        descriptor = activity.getContentResolver ().openFileDescriptor (finalUri, "r");
        
        FileInputStream fis = new FileInputStream (descriptor.getFileDescriptor ());
        
        final Scanner scan = new Scanner (fis);
        scan.useDelimiter ("\\A");
        final String content = scan.next ();
        scan.close ();
        return content;
        
    }
    
    private static long parseLong (String string) throws FileNotFoundException {
        try {
            return Long.parseLong (string);
        } catch (NumberFormatException e) {
            throw new FileNotFoundException (string);
        }
    }
    
    @SuppressLint ("InlinedApi")
    public static String readFileFromUri (Context activity, String fileName) throws FileNotFoundException {
        if (android.os.Build.VERSION.SDK_INT < 11) {
            throw new FileNotFoundException (fileName);
        }
        
        Cursor cursor = null;
        try {
            String [] proj = { MediaStore.Files.FileColumns.DATA };
            cursor = activity.getContentResolver ().query (getCompleteUriPath (fileName), proj, null, null, null);
            if (cursor == null) {
                throw new FileNotFoundException (fileName);
            }
            int column_index = cursor.getColumnIndexOrThrow (proj [0]);
            cursor.moveToFirst ();
            return cursor.getString (column_index);
        } finally {
            if (cursor != null) {
                cursor.close ();
            } else {
                throw new FileNotFoundException (fileName);
            }
            
        }
    }
}
