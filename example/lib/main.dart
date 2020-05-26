import 'dart:io';

import 'package:flutter/material.dart';
import 'package:install_plugin/install_plugin.dart';
import 'package:path_provider/path_provider.dart';
import 'package:http/http.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: RaisedButton(
            onPressed: () async {
              final dir = (await getExternalStorageDirectories(
                  type: StorageDirectory.downloads))[0];
              final f = File(
                  '${dir.path}/${DateTime.now().millisecondsSinceEpoch}.apk');

              String url =
                  'https://cdn.jsdelivr.net/gh/sxw365/apk-provider@1.0.1/%E9%A3%9F%E8%BF%85%E7%94%9F%E9%B2%9C-1.0.1.apk';

              final response = await get(url);

              f.writeAsBytesSync(response.bodyBytes);

              InstallPlugin.install(f.absolute.path);
            },
            child: Text('install'),
          ),
        ),
      ),
    );
  }
}
