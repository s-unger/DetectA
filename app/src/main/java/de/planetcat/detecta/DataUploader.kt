package de.planetcat.detecta

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class DataUploader(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        // Do the work here--in this case, upload the images.
        val data = inputData.getString("data")
        if (data != null) {
            val uploadStatus = uploadDataset(data)
            if (uploadStatus == UploadStatus.FAILED) {
                return Result.retry()
            } else if (uploadStatus == UploadStatus.FORBIDDEN) {
                val sharedPreferences = this.applicationContext.getSharedPreferences("preferences", Activity.MODE_PRIVATE)
                val oldtoken = sharedPreferences.getString("token", "")
                with (sharedPreferences.edit()) {
                    putString("token", "")
                    apply()
                }
                uploadDataset("TOKEN CHANGE DUE TO FORBIDDEN. OLD TOKEN WAS $oldtoken.")
                return Result.retry()
            }
        }
        return Result.success()
    }

    enum class UploadStatus {
        SUCCESS, FAILED, FORBIDDEN
    }

    private fun uploadDataset(data: String): UploadStatus {
            val dataEndpoint = URL("https://researchinternational.net/detecta/postData.php")
            val dataConnection: HttpsURLConnection =
                dataEndpoint.openConnection() as HttpsURLConnection
            dataConnection.doOutput = true
            dataConnection.requestMethod = "POST"
            dataConnection.setRequestProperty("User-Agent", getToken(this.applicationContext))
            dataConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            val postData = "data=$data"
            dataConnection.setRequestProperty("Content-Length", postData.length.toString())
            DataOutputStream(dataConnection.getOutputStream()).use { it.writeBytes(postData) }
            if (dataConnection.getResponseCode() == 200) {
                val responseBody: InputStream = dataConnection.inputStream
                val responseBodyReader = InputStreamReader(responseBody, "UTF-8")
                val response = responseBodyReader.readText()
                responseBodyReader.close()
                Log.w("DetectAService", "Response: $response")
                return UploadStatus.SUCCESS
            } else if (dataConnection.getResponseCode() == 403) {
                return UploadStatus.FORBIDDEN
            } else {
                val responseCode = dataConnection.getResponseCode()
                Log.w("DetectAService", "Response Code: $responseCode")
                return UploadStatus.FAILED
            }

        }

    class InternetException(message:String): Exception(message)

    fun getToken(context: Context):String {
        val sharedPreferences = context.getSharedPreferences("preferences", Activity.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")
        if (token == "") {
            val tokenEndpoint = URL("https://researchinternational.net/detecta/getToken.php")
            val tokenConnection: HttpsURLConnection = tokenEndpoint.openConnection() as HttpsURLConnection
            if (tokenConnection.getResponseCode() == 200) {
                val responseBody: InputStream = tokenConnection.inputStream
                val responseBodyReader = InputStreamReader(responseBody, "UTF-8")
                val newtoken = responseBodyReader.readText()
                responseBodyReader.close()
                with (sharedPreferences.edit()) {
                    putString("token", newtoken)
                    apply()
                }
                Log.w("DetectAService", "New Token Requested: $newtoken")
                return newtoken
            } else {
                throw InternetException("getToken failed.")
            }
        } else {
            Log.w("DetectAService", "Old Token Reused: $token")
            return token.toString()
        }
    }
}