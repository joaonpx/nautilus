package br.com.fiap.nautilus

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import br.com.fiap.nautilus.ui.theme.NautilusTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class MainActivity : ComponentActivity() {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null
    private val isReceivingData = mutableStateOf("")

    private lateinit var enableBluetoothLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>

    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Bluetooth habilitado", Toast.LENGTH_SHORT).show()
                openBluetoothSettings()
            } else {
                Toast.makeText(this, "Bluetooth não foi habilitado", Toast.LENGTH_SHORT).show()
            }
        }

        requestPermissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                enableBluetooth()
            } else {
                Toast.makeText(this, "Permissões necessárias não concedidas", Toast.LENGTH_SHORT).show()
            }
        }

        checkPermissions()
        setContent {
            NautilusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SubmarineControl(
                        onUp = { sendCommand("UP") },
                        onDown = { sendCommand("DOWN") },
                        onRight = { sendCommand("RIGHT") },
                        onLeft = { sendCommand("LEFT") },
                        onForward = { sendCommand("FORWARD") },
                        onBackward = { sendCommand("BACKWARD") },
                        receivedData = isReceivingData.value
                    )
                }
            }
        }
    }

    private fun checkPermissions() {
        val permissionsToRequest = locationPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            enableBluetooth()
        }
    }

    private fun enableBluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth não é suportado neste dispositivo", Toast.LENGTH_SHORT).show()
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
        } else {
            openBluetoothSettings()
        }
    }

    private fun openBluetoothSettings() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Permissão Bluetooth não concedida", Toast.LENGTH_SHORT).show()
            return
        }

        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        enableBluetoothLauncher.launch(discoverableIntent)
    }

    private fun connectToSelectedDevice(device: BluetoothDevice) {
        val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permissão Bluetooth não concedida", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
            bluetoothSocket?.connect()
            Toast.makeText(this, "Conectado a ${device.name}", Toast.LENGTH_SHORT).show()
            startReceivingData()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erro ao conectar ao dispositivo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startReceivingData() {
        bluetoothSocket?.let { socket ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val inputStream: InputStream = socket.inputStream
                    val buffer = ByteArray(1024)
                    var bytes: Int

                    while (true) {
                        bytes = inputStream.read(buffer)
                        val incomingMessage = String(buffer, 0, bytes)
                        withContext(Dispatchers.Main) {
                            isReceivingData.value = incomingMessage
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Erro ao receber dados", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun sendCommand(command: String) {
        sendData(command)
    }

    private fun sendData(data: String) {
        val outputStream: OutputStream? = bluetoothSocket?.outputStream
        outputStream?.write(data.toByteArray())
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothSocket?.close()
    }
}
