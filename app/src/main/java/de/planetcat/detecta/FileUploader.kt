package de.planetcat.detecta

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File

class FileUploader(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        // Do the work here--in this case, upload the images.
        uploadImages()
        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

    private fun uploadImages() {
        try {
            var uploadImageURI = "/storage/emulated/0/Pictures/15062020155500.jpg";
            if (!TextUtils.isEmpty(uploadImageURI)) {
                val file = File(uploadImageURI)

                var transferUtility = AWSUtil.getTransferUtility(applicationContext)
                var observer: TransferObserver = transferUtility.upload(
                    AWSConstants.BUCKET_NAME_STAGING, AWSConstants.FOLDER_USERS + file.name,
                    file, CannedAccessControlList.PublicRead
                )
                observer.setTransferListener(mUploadListener)
            }
        } catch (exeption: Exception) {
            exeption.printStackTrace()
        }

    }

    var mUploadListener = object : TransferListener {
        override fun onError(id: Int, e: Exception) {
            Log.e("ImageUpload", "Error during upload: " + id, e)
        }

        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
            Log.d(
                "ImageUpload", String.format(
                    "onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent
                )
            )
        }
        override fun onStateChanged(id: Int, newState: TransferState) {
            if (newState == TransferState.COMPLETED) {
                val imageURL: String =
                    AWSConstants.S3_URL + AWSConstants.S3_URL + "/" + AWSConstants.FOLDER_USERS + "imageFilename"
                Log.e("URL", "Upload: " + imageURL)
            }
        }
    }
}