import 'package:jitsi_meet_wrapper_platform_interface/jitsi_meeting_listener.dart';
import 'package:jitsi_meet_wrapper_platform_interface/method_channel_jitsi_meet_wrapper.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'jitsi_meeting_options.dart';
import 'jitsi_meeting_response.dart';

export 'feature_flag.dart';
export 'jitsi_meeting_listener.dart';
export 'jitsi_meeting_options.dart';
export 'jitsi_meeting_response.dart';

abstract class JitsiMeetWrapperPlatformInterface extends PlatformInterface {
  JitsiMeetWrapperPlatformInterface() : super(token: _token);

  static final Object _token = Object();

  static JitsiMeetWrapperPlatformInterface _instance = MethodChannelJitsiMeetWrapper();

  /// The default instance of [JitsiMeetWrapperPlatformInterface] to use.
  ///
  /// Defaults to [MethodChannelJitsiMeetWrapper].
  static JitsiMeetWrapperPlatformInterface get instance => _instance;

  static set instance(JitsiMeetWrapperPlatformInterface instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  /// Joins a meeting based on the [JitsiMeetingOptions] passed in.
  Future<JitsiMeetingResponse> joinMeeting({
    required JitsiMeetingOptions options,
    JitsiMeetingListener? listener,
  }) async {
    throw UnimplementedError('joinMeeting has not been implemented.');
  }

  Future<JitsiMeetingResponse> setMuted({required bool muted}) async {
    throw UnimplementedError('setMuted has not been implemented.');
  }

  Future<JitsiMeetingResponse> closeMeeting() async {
    throw UnimplementedError('closeMeeting has not been implemented.');
  }

  Future<JitsiMeetingResponse> enterPictureInPictureMode() async {
    throw UnimplementedError('enterPictureInPictureMode has not been implemented.');
  }
}
