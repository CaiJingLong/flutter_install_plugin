package com.example.install_plugin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.File
import java.io.FileNotFoundException

/** InstallPlugin */
public class InstallPlugin() : FlutterPlugin, MethodCallHandler {
  
  lateinit var context: Context
  
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel: MethodChannel
  
  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    this.context = flutterPluginBinding.applicationContext
    channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "top.kikt/install_plugin")
    channel.setMethodCallHandler(this);
  }
  
  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "install_plugin")
      val installPlugin = InstallPlugin()
      installPlugin.context = registrar.context().applicationContext
      channel.setMethodCallHandler(installPlugin)
    }
  }
  
  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "install") {
      val path = call.argument<String>("path")!!
      installApk(path)
      result.success(true)
    } else {
      result.notImplemented()
    }
  }
  
  private fun installApk(filePath: String?) {
    if (filePath == null) throw NullPointerException("fillPath is null!")
    val file = File(filePath)
    if (!file.exists()) throw FileNotFoundException("$filePath is not exist! or check permission")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      install24(file)
    } else {
      installBelow24(file)
    }
  }
  
  private fun install24(file: File) {
    val appId = context.packageName
    
    val intent = Intent(Intent.ACTION_VIEW)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    val uri: Uri = FileProvider.getUriForFile(context, "$appId.install.provider", file)
    intent.setDataAndType(uri, "application/vnd.android.package-archive")
    context.startActivity(intent)
  }
  
  
  private fun installBelow24(file: File?) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val uri = Uri.fromFile(file)
    intent.setDataAndType(uri, "application/vnd.android.package-archive")
    context.startActivity(intent)
  }
  
  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
