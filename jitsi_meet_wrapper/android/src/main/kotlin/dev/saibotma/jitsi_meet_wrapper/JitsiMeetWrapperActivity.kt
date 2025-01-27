package dev.saibotma.jitsi_meet_wrapper

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.jitsi.meet.sdk.BroadcastEvent
import org.jitsi.meet.sdk.BroadcastIntentHelper
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions

class JitsiMeetWrapperActivity : JitsiMeetActivity() {
    private val eventStreamHandler = JitsiMeetWrapperEventStreamHandler.instance
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            this@JitsiMeetWrapperActivity.onBroadcastReceived(intent)
        }
    }

    companion object {
        val ACTION_ENTER_PICTURE_IN_PICTURE_MODE = "org.jitsi.meet.ENTER_PICTURE_IN_PICTURE_MODE";

        fun launch(context: Context, options: JitsiMeetConferenceOptions?) {
            val intent = Intent(context, JitsiMeetWrapperActivity::class.java)
            intent.action = "org.jitsi.meet.CONFERENCE"
            intent.putExtra("JitsiMeetConferenceOptions", options)
            if (context !is Activity) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    var onStopCalled: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerForBroadcastMessages()
        eventStreamHandler.onOpened()
    }

    override fun onStop() {
        super.onStop()
        onStopCalled = true;
    }

    override fun onResume() {
        super.onResume()
        onStopCalled = false
    }

    private fun registerForBroadcastMessages() {
        val intentFilter = IntentFilter()
        for (eventType in BroadcastEvent.Type.values()) {
            intentFilter.addAction(eventType.action)
        }
        intentFilter.addAction(ACTION_ENTER_PICTURE_IN_PICTURE_MODE);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.broadcastReceiver, intentFilter)
    }

    private fun onBroadcastReceived(intent: Intent?) {
        if (intent != null) {
            if (intent.action == ACTION_ENTER_PICTURE_IN_PICTURE_MODE) {
                enterPictureInPictureMeeting()
            } else {
                val event = BroadcastEvent(intent)
                val data = event.data
                when (event.type!!) {
                    BroadcastEvent.Type.CONFERENCE_JOINED -> eventStreamHandler.onConferenceJoined(data)
                    BroadcastEvent.Type.CONFERENCE_TERMINATED -> eventStreamHandler.onConferenceTerminated(data)
                    BroadcastEvent.Type.CONFERENCE_WILL_JOIN -> eventStreamHandler.onConferenceWillJoin(data)
                    BroadcastEvent.Type.AUDIO_MUTED_CHANGED -> eventStreamHandler.onAudioMutedChanged(data)
                    BroadcastEvent.Type.PARTICIPANT_JOINED -> eventStreamHandler.onParticipantJoined(data)
                    BroadcastEvent.Type.PARTICIPANT_LEFT -> eventStreamHandler.onParticipantLeft(data)
                    BroadcastEvent.Type.ENDPOINT_TEXT_MESSAGE_RECEIVED -> eventStreamHandler.onEndpointTextMessageReceived(data)
                    BroadcastEvent.Type.SCREEN_SHARE_TOGGLED -> eventStreamHandler.onScreenShareToggled(data)
                    BroadcastEvent.Type.PARTICIPANTS_INFO_RETRIEVED -> eventStreamHandler.onParticipantsInfoRetrieved(data)
                    BroadcastEvent.Type.CHAT_MESSAGE_RECEIVED -> eventStreamHandler.onChatMessageReceived(data)
                    BroadcastEvent.Type.CHAT_TOGGLED -> eventStreamHandler.onChatToggled(data)
                    BroadcastEvent.Type.VIDEO_MUTED_CHANGED -> eventStreamHandler.onVideoMutedChanged(data)
                    BroadcastEvent.Type.READY_TO_CLOSE -> {}
                }
            }
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        if (!isInPictureInPictureMode && onStopCalled) {
            // Picture-in-Picture mode has been closed, we can (should !) end the call
            val hangupIntent: Intent = BroadcastIntentHelper.buildHangUpIntent()
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(hangupIntent)
        }
    }

    private fun enterPictureInPictureMeeting() {
        jitsiView?.enterPictureInPicture()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.broadcastReceiver)
        eventStreamHandler.onClosed()
    }
}
