package pollingapp.by.mohammadnihalls3451385.mypolls

import android.content.Context

object AppData {

    private const val POLLING_APP_PREFS_FILE = "PollingAppPreferences"


    fun setLoggedIn(context: Context, status: Boolean) {
        val sharedPref = context.getSharedPreferences(POLLING_APP_PREFS_FILE, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("POLL_USER_LOGGED_IN", status).apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(POLLING_APP_PREFS_FILE, Context.MODE_PRIVATE)
        return sharedPref.getBoolean("POLL_USER_LOGGED_IN", false)
    }

    fun setUserName(context: Context, name: String) {
        val sharedPref = context.getSharedPreferences(POLLING_APP_PREFS_FILE, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("POLL_USER_FULL_NAME", name).apply()
    }

    fun getUserFullName(context: Context): String? {
        val sharedPref = context.getSharedPreferences(POLLING_APP_PREFS_FILE, Context.MODE_PRIVATE)
        return sharedPref.getString("POLL_USER_FULL_NAME", null)
    }

    fun setUserGender(context: Context, gender: String) {
        val sharedPref = context.getSharedPreferences(POLLING_APP_PREFS_FILE, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("POLL_USER_GENDER", gender).apply()
    }

    fun getUserGender(context: Context): String? {
        val sharedPref = context.getSharedPreferences(POLLING_APP_PREFS_FILE, Context.MODE_PRIVATE)
        return sharedPref.getString("POLL_USER_GENDER", null)
    }


    fun setUserEmail(context: Context, email: String) {
        val sharedPref = context.getSharedPreferences(POLLING_APP_PREFS_FILE, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("POLL_USER_EMAIL", email).apply()
    }

    fun getUserEmail(context: Context): String? {
        val sharedPref = context.getSharedPreferences(POLLING_APP_PREFS_FILE, Context.MODE_PRIVATE)
        return sharedPref.getString("POLL_USER_EMAIL", null)
    }

    fun getFirebaseCompatibleUserId(context: Context): String? {
        return getUserEmail(context)?.replace(".", ",")
    }

    fun clearUserSession(context: Context) {
        val sharedPref = context.getSharedPreferences(POLLING_APP_PREFS_FILE, Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
    }
}