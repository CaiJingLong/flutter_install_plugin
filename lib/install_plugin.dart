import 'dart:async';

import 'package:flutter/services.dart';

class InstallPlugin {
  static const MethodChannel _channel =
      const MethodChannel('top.kikt/install_plugin');

  static Future<void> install(String filePath) async {
    await _channel.invokeMethod('install', {'path': filePath});
  }
}
