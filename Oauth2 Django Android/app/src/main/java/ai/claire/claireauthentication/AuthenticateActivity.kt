package ai.claire.claireauthentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.getAs
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import org.json.JSONObject

class AuthenticateActivity : AppCompatActivity(), View.OnClickListener {
    private val authCal = 69
    private val authGmail = 70
    private val authDrive = 71
    private val authGmailReadonly = 72
    private val authDriveReadonly = 73


    private lateinit var mContext: Context
    private val tag = "AuthenticateActivity"

    override fun onClick(p0: View?) {
        val id = p0?.id
        Log.d(tag, "OnClick called")
        when(id) {
            R.id.btn_auth_calendar -> { authenticate("https://www.googleapis.com/auth/calendar", authCal) }
            R.id.btn_auth_gmail_readonly -> { authenticate("https://www.googleapis.com/auth/gmail.readonly", authGmailReadonly) }
            R.id.btn_auth_gmail -> { authenticate("https://mail.google.com/", authGmail) }
            R.id.btn_auth_drive_readonly -> { authenticate("https://www.googleapis.com/auth/drive.readonly", authDriveReadonly) }
            R.id.btn_auth_drive -> { authenticate("https://www.googleapis.com/auth/drive", authDrive) }
        }
    }

    private fun authenticate(s: String, id: Int) {
        val serverClientId = getString(R.string.server_client_id)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Scope(s))
                .requestServerAuthCode(serverClientId)
                .requestEmail()
                .build()

        val googleSignInClient = GoogleSignIn.getClient(mContext, gso)
        val authIntent = googleSignInClient?.signInIntent
        startActivityForResult(authIntent, id)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            authCal -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    sendIdTokenToServer("http://172.104.161.215:3001/Authenticate/google_calendar/", account.serverAuthCode!!, account.email!!)
                } catch (e: ApiException) {
                    Log.d(tag, e.message)
                    e.printStackTrace()
                }
            }
            authGmail -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    sendIdTokenToServer("http://172.104.161.215:3001/Authenticate/gmail/", account.serverAuthCode!!, account.email!!)
                } catch (e: ApiException) {
                    Log.d(tag, e.message)
                    e.printStackTrace()
                }
            }
            authDrive -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    sendIdTokenToServer("http://172.104.161.215:3001/Authenticate/drive/", account.serverAuthCode!!, account.email!!)
                } catch (e: ApiException) {
                    Log.d(tag, e.message)
                    e.printStackTrace()
                }
            }
            authDriveReadonly -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    sendIdTokenToServer("http://172.104.161.215:3001/Authenticate/drive_readonly/", account.serverAuthCode!!, account.email!!)
                } catch (e: ApiException) {
                    Log.d(tag, e.message)
                    e.printStackTrace()
                }
            }
            authGmailReadonly -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    sendIdTokenToServer("http://172.104.161.215:3001/Authenticate/gmail_readonly/", account.serverAuthCode!!, account.email!!)
                } catch (e: ApiException) {
                    Log.d(tag, e.message)
                    e.printStackTrace()
                }
            }
        }
    }

    private fun sendIdTokenToServer(url: String, authToken: String, email: String) {
        val p = listOf(Pair("id_token", authToken), Pair("email", email))


        url.httpGet(p).responseString { _, _, result ->
            when (result) {
                is Result.Failure -> {
                    val error = result.getAs<String>()
                    Log.d(tag, "Http error: $error and error is $error")
                }
                is Result.Success -> {
                    val data = JSONObject(result.getAs<String>())
                    if (data["status"] == "error") {
                        Toast.makeText(mContext, "Error authenticating user: $data[\"message\"]", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(mContext, "Success authenticating user!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticate)
        mContext = applicationContext
        findViewById<Button>(R.id.btn_auth_drive).setOnClickListener(this)
        findViewById<Button>(R.id.btn_auth_gmail).setOnClickListener(this)
        findViewById<Button>(R.id.btn_auth_gmail_readonly).setOnClickListener(this)
        findViewById<Button>(R.id.btn_auth_drive_readonly).setOnClickListener(this)
        findViewById<Button>(R.id.btn_auth_calendar).setOnClickListener(this)
    }
}
