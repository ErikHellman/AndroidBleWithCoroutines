package se.hellsoft.androidble

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor

sealed class GattEvent

data class PhyUpdate(val txPhy: Int, val rxPhy: Int, val status: Int) : GattEvent()

data class PhyRead(val txPhy: Int, val rxPhy: Int, val status: Int) : GattEvent()

data class ConnectionStateChanged(val status: Int, val newState: Int) : GattEvent()

data class CharacteristicRead(val characteristic: BluetoothGattCharacteristic, val status: Int) :
    GattEvent()

data class CharacteristicWritten(val characteristic: BluetoothGattCharacteristic, val status: Int) :
    GattEvent()

data class CharacteristicChanged(val characteristic: BluetoothGattCharacteristic) : GattEvent()

data class ServicesDiscovered(val status: Int) : GattEvent()

data class DescriptorRead(val descriptor: BluetoothGattDescriptor, val status: Int) : GattEvent()

data class DescriptorWritten(val descriptor: BluetoothGattDescriptor, val status: Int) : GattEvent()

data class ReliableWriteCompleted(val status: Int) : GattEvent()

data class ReadRemoteRssi(val rssi: Int, val status: Int) : GattEvent()

data class MtuChanged(val mtu: Int, val status: Int) : GattEvent()
