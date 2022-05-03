package se.hellsoft.blewithcoroutines

import android.Manifest.permission.BLUETOOTH
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Context
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import se.hellsoft.androidble.GattDevice
import java.util.*

class SimpleBleRepository {
    private val gattDevice = GattDevice()

    // TODO Use something real :)
    private val someCharId = UUID.randomUUID()
    private val someServiceId = UUID.randomUUID()

    /**
     * Connects and discover services on the device.
     * Will return when discover services is completed, or connection fails.
     * Other BLE operations will be queued until this completes
     */
    @RequiresPermission(anyOf = [BLUETOOTH, BLUETOOTH_CONNECT])
    suspend fun connect(context: Context, bluetoothDevice: BluetoothDevice): Boolean {
        val stateChange = gattDevice.connect(context, bluetoothDevice)
        return if (stateChange.newState == BluetoothGatt.STATE_CONNECTED) {
            gattDevice.discoverServices()
        } else false
    }

    /**
     * Write a string to the characteristic.
     * Will return when the string has been written to the characteristic, or there is a failure.
     * Other BLE operations will be queued until this completes
     */
    @RequiresPermission(anyOf = [BLUETOOTH, BLUETOOTH_CONNECT])
    suspend fun writeText(data: String): Boolean {
        val characteristic = gattDevice.getService(someServiceId).getCharacteristic(someCharId)
        characteristic.setValue(data)
        return gattDevice.writeCharacteristic(characteristic).status == BluetoothGatt.GATT_SUCCESS
    }

    /**
     * Write read string from the characteristic.
     * Will return when the string hass been read from the characteristic, or there is a failure.
     * Other BLE operations will be queued until this completes
     */
    @RequiresPermission(anyOf = [BLUETOOTH, BLUETOOTH_CONNECT])
    suspend fun readText(): String {
        val characteristic = gattDevice.getService(someServiceId).getCharacteristic(someCharId)
        val result = gattDevice.readCharacteristic(characteristic)
        if (result.status == BluetoothGatt.GATT_SUCCESS) {
            return result.characteristic.getStringValue(0)
        } else {
            throw IllegalStateException("Read failed!")
        }
    }

    val events: Flow<String>
        get() {
            return gattDevice
                .characteristicChangedEvents
                .filter { it.characteristic.uuid == someCharId }
                .map { it.characteristic.getStringValue(0) }
        }
}
