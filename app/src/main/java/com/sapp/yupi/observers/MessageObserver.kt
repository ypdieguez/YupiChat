package com.sapp.yupi.observers

import android.database.ContentObserver
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.sapp.yupi.workers.IncomingMsgWorker
import com.sapp.yupi.workers.KEY_BODY
import com.sapp.yupi.workers.KEY_DATE
import com.sapp.yupi.workers.KEY_MSG_ID

open class MessageObserver : ContentObserver(null) {

    protected fun insertMsg(id:Long, date:Long, body: String) {
        // Do the work
        val data = Data.Builder()
                .putLong(KEY_MSG_ID, id)
                .putLong(KEY_DATE, date)
                .putString(KEY_BODY, body)
                .build()

        val worker = OneTimeWorkRequest.Builder(IncomingMsgWorker::class.java)
                .setInputData(data)
                .build()

        WorkManager.getInstance().enqueue(worker)
    }
}