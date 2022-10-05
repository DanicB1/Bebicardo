package danB.bebicardo

import android.telephony.SmsManager
import android.widget.Spinner
import androidx.core.view.get

class TextMessage {
    private val requestMessages: HashMap<String, String> = HashMap()

    private val smsManager: SmsManager = SmsManager.getDefault()
    private val chatinousNumber: String = "5149126298"

    constructor(requests: Array<String>, answers: Array<String>) {
        initializeRequestMessages(requests, answers)
    }

    fun requestAttention(requestType:String) {
        smsManager.sendTextMessage(chatinousNumber,
                                   null,
                                    getMessageString(requestType),
                                   null,
                                   null)
    }

    private fun getMessageString(requestType:String): String? {
        return requestMessages[requestType]
    }

    private fun initializeRequestMessages(requests: Array<String>, answers: Array<String>) {
        for (i in requests.indices) {
            requestMessages[requests[i]] = answers[i]
        }
    }
}