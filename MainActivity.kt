package com.progfx

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.rikka.shizuku.Shizuku
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val REQUEST_SHIZUKU = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            Toast.makeText(this, "Requires Android 12+ (SDK 31+)", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setContent {
            ProGfxApp(onApply = { resolution, fps, quality, api, shadows, aa ->
                applyWithShizuku(resolution, fps, quality, api, shadows, aa)
            })
        }
    }

    private fun requestShizukuPermission() {
        try {
            Shizuku.requestPermission(REQUEST_SHIZUKU)
        } catch (e: Exception) {
            Toast.makeText(this, "Shizuku request failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showInstallShizuku() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api"))
        startActivity(intent)
    }

    private fun applyWithShizuku(resolution: String, fps: Int, quality: String, api: String, shadows: Boolean, aa: Boolean) {
        if (!Shizuku.pingBinder()) {
            Toast.makeText(this, "Shizuku not running. Open Shizuku app and start it.", Toast.LENGTH_LONG).show()
            showInstallShizuku()
            return
        }

        if (Shizuku.checkSelfPermission() != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestShizukuPermission()
            return
        }

        val commands = listOf(
            "settings put global gfx_resolution $resolution",
            "settings put global gfx_fps $fps",
            "settings put global gfx_quality $quality",
            "settings put global gfx_rendering_api $api",
            "settings put global gfx_shadows ${if (shadows) 1 else 0}",
            "settings put global gfx_antialiasing ${if (aa) 1 else 0}"
        )

        Thread {
            try {
                for (cmd in commands) {
                    val fullCmd = arrayOf("sh", "-c", cmd)
                    val process = Runtime.getRuntime().exec(fullCmd)
                    val exit = process.waitFor()
                }
                runOnUiThread {
                    Toast.makeText(this, "Commands executed (placeholder). See SHIZUKU_INSTRUCTIONS.md for proper exec usage.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Failed to execute: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }
}

@Composable
fun ProGfxApp(onApply: (String, Int, String, String, Boolean, Boolean) -> Unit = { _,_,_,_,_,_ -> }) {
    var resolution by remember { mutableStateOf("1280x720") }
    var fps by remember { mutableStateOf(60) }
    var graphicsQuality by remember { mutableStateOf("Smooth") }
    var renderingApi by remember { mutableStateOf("OpenGL") }
    var shadows by remember { mutableStateOf(true) }
    var aa by remember { mutableStateOf(true) }
    var log by remember { mutableStateOf("Ready") }

    MaterialTheme {
        Scaffold(topBar = {
            TopAppBar(title = { Text("Pro GFX Shizuku (PUBG)") })
        }) { padding ->
            Column(modifier = Modifier
                .padding(padding)
                .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {

                Text("Resolution")
                DropdownField(options = listOf("480x270","640x360","854x480","960x540","1280x720","1920x1080","2160x1440"),
                    selected = resolution, onSelect = { resolution = it })

                Text("FPS")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(30,40,60,90,120).forEach { v ->
                        FilterChip(selected = (fps==v), onClick = { fps = v }) {
                            Text("$v fps")
                        }
                    }
                }

                Text("Graphics Quality")
                DropdownField(options = listOf("Smooth","Balanced","HD","HDR","Ultra"),
                    selected = graphicsQuality, onSelect = { graphicsQuality = it })

                Text("Rendering API")
                DropdownField(options = listOf("OpenGL","Vulkan"), selected = renderingApi, onSelect = { renderingApi = it })

                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Shadows")
                    Switch(checked = shadows, onCheckedChange = { shadows = it })
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Anti-Aliasing")
                    Switch(checked = aa, onCheckedChange = { aa = it })
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = {
                        onApply(resolution, fps, graphicsQuality, renderingApi, shadows, aa)
                    }) {
                        Text("Apply Settings")
                    }
                    Button(onClick = {
                        // Reset defaults
                        resolution = "1280x720"
                        fps = 60
                        graphicsQuality = "Smooth"
                        renderingApi = "OpenGL"
                        shadows = true
                        aa = true
                        log = "Reset to defaults"
                    }) {
                        Text("Reset to Default")
                    }
                }

                Text("Log:")
                Text(log, modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp))
            }
        }
    }
}

@Composable
fun DropdownField(options: List<String>, selected: String, onSelect: (String)->Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { expanded = true }) {
            Text(selected)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(text = { Text(opt) }, onClick = { onSelect(opt); expanded = false })
            }
        }
    }
}
