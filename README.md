# BluetoothChat

基于Android Classic Bluetooth的蓝牙聊天软件，目前仅支持一对一实时通信、文件传输、好友添加、好友分组、好友在线状态更新等功能，其中消息发送支持文本、表情等方式。

项目地址：[https://github.com/xiaoyaoyou1212/BluetoothChat](https://github.com/xiaoyaoyou1212/BluetoothChat)（欢迎Fork参与）

### QQ交流群

![QQ群](http://img.blog.csdn.net/20170327191310083)

### 前景

蓝牙技术作为一种小范围无线连接技术，能够在设备间实现方便快捷、灵活安全、低成本、低功耗的数据和语音通信，是目前实现无线个人局域网的主流技术之一。同时，蓝牙系统以自组式组网的方式工作，每个蓝牙设备都可以在网络中实现路由选择的功能，可以形成移动自组网络。蓝牙的特性在许多方面正好符合Ad Hoc和WPAN的概念，显示了其真正的潜力所在。而且，将蓝牙与其他网络相连接可带来更广泛的应用，例如接入互联网、PSTN或公众移动通信网，可以使用户应用更方便或给用户带来更大的实惠。

蓝牙聊天作为一款针对局域网范围内的聊天软件，在办公密集，想实现快速稳定实时通讯还是比较有实用价值的。目前蓝牙技术发展迅速，5.0传输速率已经达到2Mbps，传输级别达到无损级别，有效工作距离可达300米，在蓝牙组网方面技术也在进一步更新，相信要不了多久会有很成熟的方案出来，这样一来就可以实现多人在线实时聊天功能，打破只能一对多实时聊天的界限。

### 技术简介

1、蓝牙通信的主从关系 
蓝牙技术规定每一对设备之间进行蓝牙通讯时，必须一个为主角色，另一为从角色，才能进行通信，通信时，必须由主端进行查找，发起配对，建链成功后，双方即可收发数据。理论上，一个蓝牙主端设备，可同时与7个蓝牙从端设备进行通讯。一个具备蓝牙通讯功能的设备， 可以在两个角色间切换，平时工作在从模式，等待其它主设备来连接，需要时，转换为主模式，向其它设备发起呼叫。一个蓝牙设备以主模式发起呼叫时，需要知道对方的蓝牙地址，配对密码等信息，配对完成后，可直接发起呼叫。  

2、蓝牙的呼叫过程
蓝牙主端设备发起呼叫，首先是查找，找出周围处于可被查找的蓝牙设备。主端设备找到从端蓝牙设备后，与从端蓝牙设备进行配对，此时需要输入从端设备的PIN码，也有设备不需要输入PIN码。配对完成后，从端蓝牙设备会记录主端设备的信任信息，此时主端即可向从端设备发起呼叫，已配对的设备在下次呼叫时，不再需要重新配对。已配对的设备，做为从端的蓝牙设备也可以发起建链请求，但做数据通讯的蓝牙模块一般不发起呼叫。链路建立成功后，主从两端之间即可进行双向的数据或语音通讯。在通信状态下，主端和从端设备都可以发起断链，断开蓝牙链路。

3、蓝牙一对一的串口数据传输应用
蓝牙数据传输应用中，一对一串口数据通讯是最常见的应用之一，蓝牙设备在出厂前即提前设好两个蓝牙设备之间的配对信息，主端预存有从端设备的PIN码、地址等，两端设备加电即自动建链，透明串口传输，无需外围电路干预。一对一应用中从端设备可以设为两种类型，一是静默状态，即只能与指定的主端通信，不被别的蓝牙设备查找；二是开发状态，既可被指定主端查找，也可以被别的蓝牙设备查找建链。

### 功能概述

蓝牙聊天功能主要分为以下几个模块：消息模块、好友模块以及个人模块。

#### 消息模块

支持一对一、一对多、多对多实时聊天，能传输文字、表情、图片、文件等。对方不在线时可支持离线消息发送，在对方在线时能及时推送过去。消息支持历史消息存储与查看。

#### 好友模块

支持附近好友添加，好友删除，好友分组显示，好友上下线提醒，好友昵称及分组名称修改。

#### 个人模块

展示个人信息，包含昵称、图像、加入时间等信息。

![个人中心](http://img.blog.csdn.net/20161013141713632)

该模块还未实现，目前实现功能主要有一对一实时聊天、能传输文字、表情、文件，支持好友添加、删除、分组。下文主要介绍已经实现的蓝牙通信流程。

### 操作流程

#### 查找已配对设备（即好友列表）

代码实现：
```
private void findDevice(){
    // 获得已经保存的配对设备
    Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
    if (pairedDevices.size() > 0) {
        mGroupFriendListData.clear();
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setGroupName(BluetoothAdapter.getDefaultAdapter().getName());
        List<FriendInfo> friendInfoList = new ArrayList<>();
        for (BluetoothDevice device : pairedDevices) {
            FriendInfo friendInfo = new FriendInfo();
            friendInfo.setIdentificationName(device.getName());
            friendInfo.setDeviceAddress(device.getAddress());
            friendInfo.setFriendNickName(device.getName());
            friendInfo.setOnline(false);
            friendInfo.setJoinTime(DateTime.getStringByFormat(new Date(), DateTime.DEFYMDHMS));
            friendInfo.setBluetoothDevice(device);
            friendInfoList.add(friendInfo);
        }
        groupInfo.setFriendList(friendInfoList);
        groupInfo.setOnlineNumber(0);
        mGroupFriendListData.add(groupInfo);
        mGroupFriendAdapter.setGroupInfoList(mGroupFriendListData);
    }
}
```

好友列表示例图：

![好友列表](http://img.blog.csdn.net/20161013141703709)

#### 启用设备的可发现性

如果要让本地设备可以被其他设备发现，那么就要调用ACTION_REQUEST_DISCOVERABLE操作意图的startActivityForResult(Intent, int)方法。这个方法会向系统设置发出一个启用可发现模式的请求。默认情况下，设备的可发现模式会持续120秒。通过给Intent对象添加EXTRA_DISCOVERABLE_DURATION附加字段，可以定义不同持续时间。应用程序能够设置的最大持续时间是3600秒，0意味着设备始终是可发现的。任何小于0或大于3600秒的值都会自动的被设为120秒。例如，以下代码把持续时间设置为300秒：

```
Intent discoverableIntent = new
Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
startActivity(discoverableIntent);
```

申请用户启用设备的可发现模式时，会显示一个对话框。如果响应“Yes”，那么设备的可发现模式会持续指定的时间，而且你的Activity会接收带有结果代码等于可发现设备持续时间的onActivityResult()回调方法的调用。如果用户响应“No”或有错误发生，则结果代码等于RESULT_CANCELED.

在可发现模式下，设备会静静的把这种模式保持到指定的时长。如果你想要在可发现模式被改变时获得通知，那么你可以注册一个ACTION_SCAN_MODE_CHANGED类型的Intent广播。这个Intent对象中包含了EXTRA_SCAN_MODE和EXTRA_PREVIOUS_SCAN_MODE附加字段，它们会分别告诉你新旧扫描模式。它们每个可能的值是：SCAN_MODE_CONNECTABLE_DISCOVERABLE，SCAN_MODE_CONNECTABLE或SCAN_MODE_NONE，它们分别指明设备是在可发现模式下，还是在可发现模式下但依然可接收连接，或者是在可发现模式下并不能接收连接。

如果你要初始化跟远程设备的连接，你不需要启用设备的可现性。只有在你想要把你的应用程序作为服务端来接收输入连接时，才需要启用可发现性，因为远程设备在跟你的设备连接之前必须能够发现它。

#### 搜索设备并进行配对（即添加好友）

简单的调用startDiscovery()方法就可以开始发现设备。该过程是异步的，并且该方法会立即返回一个布尔值来指明发现处理是否被成功的启动。通常发现过程会查询扫描大约12秒，接下来获取扫描发现的每个设备的蓝牙名称。

```
public class ScanBroadcastReceiver extends BroadcastReceiver {

    private IScanCallback<BluetoothDevice> scanCallback;
    private final Map<String, BluetoothDevice> mDeviceMap = new HashMap<>();

    public ScanBroadcastReceiver(IScanCallback<BluetoothDevice> scanCallback) {
        this.scanCallback = scanCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (scanCallback == null) {
            return;
        }
        if(intent.getAction().equals(BluetoothDevice.ACTION_FOUND)){
            //扫描到蓝牙设备
            BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (bluetoothDevice == null) {
                return;
            }
            if (!mDeviceMap.containsKey(bluetoothDevice.getAddress())) {
                mDeviceMap.put(bluetoothDevice.getAddress(), bluetoothDevice);
            }
            scanCallback.discoverDevice(bluetoothDevice);
        }else if(intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
            //扫描设备结束
            final List<BluetoothDevice> deviceList = new ArrayList<>(mDeviceMap.values());
            if(deviceList != null && deviceList.size() > 0){
                scanCallback.scanFinish(deviceList);
            } else{
                scanCallback.scanTimeout();
            }
        }
    }
}
```

搜索好友示例图：

![添加好友](http://img.blog.csdn.net/20161013141724366)

```
public class PairBroadcastReceiver extends BroadcastReceiver {

    private IPairCallback pairCallback;

    public PairBroadcastReceiver(IPairCallback pairCallback) {
        this.pairCallback = pairCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
            //取得状态改变的设备，更新设备列表信息（配对状态）
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if(device != null){
                resolveBondingState(device.getBondState());
            }
        }
    }

    private void resolveBondingState(final int bondState) {
        if (pairCallback == null) {
            return;
        }
        switch (bondState) {
            case BluetoothDevice.BOND_BONDED://已配对
                pairCallback.bonded();
                break;
            case BluetoothDevice.BOND_BONDING://配对中
                pairCallback.bonding();
                break;
            case BluetoothDevice.BOND_NONE://未配对
                pairCallback.unBonded();
                break;
            default:
                pairCallback.bondFail();
                break;
        }
    }
}
```

配对信息示例图：

![添加好友](http://img.blog.csdn.net/20161013141653177)

配对过程示例图：

![添加好友](http://img.blog.csdn.net/20161013141736695)

#### 连接设备（即好友建立通信通道）

当你想要连接两个设备时，一个必须通过持有一个打开的BluetoothServerSocket对象来作为服务端。服务套接字的用途是监听输入的连接请求，并且在一个连接请求被接收时，提供一个BluetoothSocket连接对象。在从BluetoothServerSocket对象中获取BluetoothSocket时，BluetoothServerSocket能够（并且也应该）被废弃，除非你想要接收更多的连接。

以下是建立服务套接字和接收一个连接的基本过程：

1、调用listenUsingRfcommWithServiceRecord(String, UUID)方法来获得一个BluetoothServerSocket对象。该方法中的String参数是一个可识别的你的服务端的名称，系统会自动的把它写入设备上的Service Discovery Protocol（SDP）数据库实体（该名称是任意的，并且可以简单的使用你的应用程序的名称）。UUID参数也会被包含在SDP实体中，并且是跟客户端设备连接的基本协议。也就是说，当客户端尝试跟服务端连接时，它会携带一个它想要连接的服务端能够唯一识别的UUID。只有在这些UUID完全匹配的情况下，连接才可能被接收。

2、通过调用accept()方法，启动连接请求。这是一个阻塞调用。只有在连接被接收或发生异常的情况下，该方法才返回。只有在发送连接请求的远程设备所携带的UUID跟监听服务套接字所注册的一个UUID匹配的时候，该连接才被接收。连接成功，accept()方法会返回一个被连接的BluetoothSocket对象。

3、除非你想要接收其他连接，否则要调用close()方法。该方法会释放服务套接字以及它所占用的所有资源，但不会关闭被连接的已经有accept()方法所返回的BluetoothSocket对象。跟TCP/IP不一样，每个RFCOMM通道一次只允许连接一个客户端，因此在大多数情况下，在接收到一个连接套接字之后，立即调用BluetoothServerSocket对象的close()方法是有道理的。

以下是以上过程实现的监听线程：

```
public class AcceptThread extends Thread {

    private BluetoothChatHelper mHelper;
    private final BluetoothServerSocket mServerSocket;
    private String mSocketType;

    public AcceptThread(BluetoothChatHelper bluetoothChatHelper, boolean secure) {
        mHelper = bluetoothChatHelper;
        BluetoothServerSocket tmp = null;
        mSocketType = secure ? "Secure" : "Insecure";

        try {
            if (secure) {
                tmp = mHelper.getAdapter().listenUsingRfcommWithServiceRecord(ChatConstant.NAME_SECURE, ChatConstant.UUID_SECURE);
            } else {
                tmp = mHelper.getAdapter().listenUsingInsecureRfcommWithServiceRecord(ChatConstant.NAME_INSECURE, ChatConstant.UUID_INSECURE);
            }
        } catch (IOException e) {
            BleLog.e("Socket Type: " + mSocketType + "listen() failed", e);
        }
        mServerSocket = tmp;
    }

    public void run() {
        BleLog.i("Socket Type: " + mSocketType + "BEGIN mAcceptThread" + this);
        setName("AcceptThread" + mSocketType);

        BluetoothSocket socket = null;

        while (mHelper.getState() != com.vise.basebluetooth.common.State.STATE_CONNECTED) {
            try {
                BleLog.i("wait new socket:" + mServerSocket);
                socket = mServerSocket.accept();
            } catch (IOException e) {
                BleLog.e("Socket Type: " + mSocketType + " accept() failed", e);
                break;
            }
            if (socket != null) {
                synchronized (this) {
                    if(mHelper.getState() == com.vise.basebluetooth.common.State.STATE_LISTEN
                            || mHelper.getState() == com.vise.basebluetooth.common.State.STATE_CONNECTING){
                        BleLog.i("mark CONNECTING");
                        mHelper.connected(socket, socket.getRemoteDevice(), mSocketType);
                    } else if(mHelper.getState() == com.vise.basebluetooth.common.State.STATE_NONE
                            || mHelper.getState() == com.vise.basebluetooth.common.State.STATE_CONNECTED){
                        try {
                            socket.close();
                        } catch (IOException e) {
                            BleLog.e("Could not close unwanted socket", e);
                        }
                    }
                }
            }
        }
        BleLog.i("END mAcceptThread, socket Type: " + mSocketType);
    }

    public void cancel() {
        BleLog.i("Socket Type" + mSocketType + "cancel " + this);
        try {
            mServerSocket.close();
        } catch (IOException e) {
            BleLog.e("Socket Type" + mSocketType + "close() of server failed", e);
        }
    }
}
```

以下是一个基本的连接过程：

1、通过调用BluetoothDevice的createRfcommSocketToServiceRecord(UUID)方法，获得一个BluetoothSocket对象。这个方法会初始化一个连接到BluetoothDevice对象的BluetoothSocket对象。传递给这个方法的UUID参数必须与服务端设备打开BluetoothServerSocket对象时所使用的UUID相匹配。在你的应用程序中简单的使用硬编码进行比对，如果匹配，服务端和客户端代码就可以应用这个BluetoothSocket对象了。

2、通过调用connect()方法来初始化连接。在这个调用中，为了找到匹配的UUID，系统会在远程的设备上执行一个SDP查询。如果查询成功，并且远程设备接收了该连接请求，那么它会在连接期间共享使用RFCOMM通道，并且connect()方法会返回。这个方法是一个阻塞调用。如果因为某些原因，连接失败或连接超时（大约在12秒之后），就会抛出一个异常。

以下是实现以上过程的连接线程：

```
public class ConnectThread extends Thread {

    private BluetoothChatHelper mHelper;
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private String mSocketType;

    public ConnectThread(BluetoothChatHelper bluetoothChatHelper, BluetoothDevice device, boolean secure) {
        mHelper = bluetoothChatHelper;
        mDevice = device;
        BluetoothSocket tmp = null;
        mSocketType = secure ? "Secure" : "Insecure";

        try {
            if (secure) {
                tmp = device.createRfcommSocketToServiceRecord(ChatConstant.UUID_SECURE);
            } else {
                tmp = device.createInsecureRfcommSocketToServiceRecord(ChatConstant.UUID_INSECURE);
            }
        } catch (IOException e) {
            BleLog.e("Socket Type: " + mSocketType + "create() failed", e);
        }
        mSocket = tmp;
    }

    public void run() {
        BleLog.i("BEGIN mConnectThread SocketType:" + mSocketType);
        setName("ConnectThread" + mSocketType);

        mHelper.getAdapter().cancelDiscovery();

        try {
            mSocket.connect();
        } catch (IOException e) {
            try {
                mSocket.close();
            } catch (IOException e2) {
                BleLog.e("unable to close() " + mSocketType + " socket during connection failure", e2);
            }
            mHelper.connectionFailed();
            return;
        }

        synchronized (this) {
            mHelper.setConnectThread(null);
        }

        mHelper.connected(mSocket, mDevice, mSocketType);
    }

    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
            BleLog.e("close() of connect " + mSocketType
                    + " socket failed", e);
        }
    }
}
```

在建立连接之前要调用cancelDiscovery()方法。在连接之前应该始终调用这个方法，并且不用实际的检查蓝牙发现处理是否正在运行也是安全的（如果想要检查，调用isDiscovering()方法）。

#### 管理连接（即好友间通信）

当你成功的连接了两个（或更多）设备时，每一个设备都有一个被连接的BluetoothSocket对象。这是良好的开始，因为你能够在设备之间共享数据。使用BluetoothSocket对象来传输任意数据的过程是简单的：

1、分别通过getInputStream()和getOutputStream()方法来获得通过套接字来处理传输任务的InputStream和OutputStream对象；

2、用read(byte[])和write（byte[]）方法来读写流中的数据。

以下为实现以上过程的通信线程：

```
public class ConnectedThread extends Thread {

    private final BluetoothChatHelper mHelper;
    private final BluetoothSocket mSocket;
    private final InputStream mInStream;
    private final OutputStream mOutStream;

    public ConnectedThread(BluetoothChatHelper bluetoothChatHelper, BluetoothSocket socket, String socketType) {
        BleLog.i("create ConnectedThread: " + socketType);
        mHelper = bluetoothChatHelper;
        mSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            BleLog.e("temp sockets not created", e);
        }

        mInStream = tmpIn;
        mOutStream = tmpOut;
    }

    public void run() {
        BleLog.i("BEGIN mConnectedThread");
        int bytes;
        byte[] buffer = new byte[1024];

        // Keep listening to the InputStream while connected
        while (true) {
            try {
                bytes = mInStream.read(buffer);
                byte[] data = new byte[bytes];
                System.arraycopy(buffer, 0, data, 0, data.length);
                mHelper.getHandler().obtainMessage(ChatConstant.MESSAGE_READ, bytes, -1, data).sendToTarget();
            } catch (IOException e) {
                BleLog.e("disconnected", e);
                mHelper.start(false);
                break;
            }
        }
    }

    public void write(byte[] buffer) {
        if(mSocket.isConnected()){
            try {
                mOutStream.write(buffer);
                mHelper.getHandler().obtainMessage(ChatConstant.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
                BleLog.e("Exception during write", e);
            }
        }
    }

    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
            BleLog.e("close() of connect socket failed", e);
        }
    }
}
```

发送消息示例图：

![发送消息](http://img.blog.csdn.net/20161013141758601)

发送表情示例图：

![发送表情](http://img.blog.csdn.net/20161013141809023)

发送文件示例图：

![发送文件](http://img.blog.csdn.net/20161013141747804)

### 关于作者
#### 作者：胡伟
#### 网站：[http://www.huwei.tech](http://www.huwei.tech)
#### 博客：[http://blog.csdn.net/xiaoyaoyou1212](http://blog.csdn.net/xiaoyaoyou1212)
