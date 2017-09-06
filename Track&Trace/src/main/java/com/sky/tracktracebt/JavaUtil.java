package com.sky.tracktracebt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JavaUtil {

    public static BluetoothSocket connect(BluetoothDevice bTDevice) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = bTDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
        return (BluetoothSocket) method.invoke(bTDevice, 1);
    }
}
