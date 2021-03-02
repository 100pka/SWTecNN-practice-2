package com.example.sharedpreferenceswithcontentprovider

import android.content.*
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class SharedPreferencesProvider : ContentProvider() {
    companion object {
        const val AUTHORITY = "com.example.sharedpreferenceswithcontentprovider"
        const val MATCH_DATA = 1
        const val PREFERENCES_FILE = "preferences"
        val CONTENT_URI: Uri = Uri.parse(
            "content://" +
                    AUTHORITY + "/" + PREFERENCES_FILE
        )
    }

    private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        sUriMatcher.addURI(AUTHORITY, PREFERENCES_FILE, MATCH_DATA)
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val uriType = sUriMatcher.match(uri)
        var cursor: MatrixCursor? = null
        when (uriType) {
            MATCH_DATA -> {
                val key = uri.pathSegments[0]
                cursor = MatrixCursor(arrayOf(key))
                val sharedPreferences =
                    context?.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)

                sharedPreferences?.let {
                    if (!it.contains(key)) {
                        return cursor
                    }
                    val rowBuilder = cursor.newRow()
                    rowBuilder.add(it.getString(key, "0"))
                }
            }
            else -> throw IllegalArgumentException("Unknown Uri")
        }
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + ".item"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException()
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException()
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return when (sUriMatcher.match(uri)) {
            MATCH_DATA -> {
                var count = 0
                val text = values?.getAsString(PREFERENCES_FILE)
                text?.let {
                    val sharedPref = context?.getSharedPreferences(
                        PREFERENCES_FILE,
                        AppCompatActivity.MODE_PRIVATE
                    )
                    with(sharedPref?.edit()) {
                        this?.putString(SHARED_KEY, text)
                        this?.apply()
                    }
                    count++
                }
                context?.contentResolver?.notifyChange(uri, null)
                count
            }
            else -> throw UnsupportedOperationException()
        }
    }
}