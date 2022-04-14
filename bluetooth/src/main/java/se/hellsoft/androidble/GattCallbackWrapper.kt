package se.hellsoft.androidble

import android.bluetooth.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import timber.log.Timber

@Suppress("TooManyFunctions")
class WatchGattCallback :
    BluetoothGattCallback() {
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _events = MutableSharedFlow<GattEvent>(extraBufferCapacity = 50)
    val events: SharedFlow<GattEvent> = _events
    // We keep a separate flow for these to avoid overflowing other events
    private val _characteristicChangedEvents = MutableSharedFlow<CharacteristicChanged>(extraBufferCapacity = 50)
    val characteristicChangedEvents: SharedFlow<CharacteristicChanged> = _characteristicChangedEvents

    private fun <T> MutableSharedFlow<T>.emitEvent(event: T) {
        scope.launch { emit(event) }
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        if (characteristic != null) {
            _events.emitEvent(CharacteristicWritten(characteristic, status))
        }
    }

    override fun onPhyUpdate(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
        _events.emitEvent(PhyUpdate(txPhy, rxPhy, status))
    }

    override fun onPhyRead(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
        _events.emitEvent(PhyRead(txPhy, rxPhy, status))
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        Timber.d("onConnectionStateChange $status $newState ${stateText[newState]} ")
        val result = _events.emitEvent(ConnectionStateChanged(status, newState))
        Timber.d("Emitted connection state changed: $result")
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        _events.emitEvent(ServicesDiscovered(status))
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        if (characteristic != null) {
            _events.emitEvent(CharacteristicRead(characteristic, status))
        }
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?
    ) {
        if (characteristic != null) {
            _characteristicChangedEvents.emitEvent(CharacteristicChanged(characteristic))
        }
    }

    override fun onDescriptorRead(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
        if (descriptor != null) {
            _events.emitEvent(DescriptorRead(descriptor, status))
        }
    }

    override fun onDescriptorWrite(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
        if (descriptor != null) {
            _events.emitEvent(DescriptorWritten(descriptor, status))
        }
    }

    override fun onReliableWriteCompleted(gatt: BluetoothGatt?, status: Int) {
        _events.emitEvent(ReliableWriteCompleted(status))
    }

    override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
        _events.emitEvent(ReadRemoteRssi(rssi, status))
    }

    override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
        _events.emitEvent(MtuChanged(mtu, status))
    }
}

internal val statusText = mapOf(
    BluetoothGatt.GATT_FAILURE to "GATT Failure",
    BluetoothGatt.GATT_SUCCESS to "GATT Success",
)

internal val stateText = mapOf(
    BluetoothProfile.STATE_CONNECTED to "Connected",
    BluetoothProfile.STATE_CONNECTING to "Connecting",
    BluetoothProfile.STATE_DISCONNECTED to "Disconnected",
    BluetoothProfile.STATE_DISCONNECTING to "Disconnecting",
)
