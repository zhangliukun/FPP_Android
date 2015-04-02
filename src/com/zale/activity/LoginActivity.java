package com.zale.activity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zale.R;
import com.zale.data.SettingsData;

public class LoginActivity extends Activity {

    private Button                   loginButton;
    private EditText                 editText;
    private TextView                 textView;
    // socket套接字
    private Socket                   clientSocket;
    // 用于保存用户的手机号码信息的持久化
    private SharedPreferences        preferences;
    private SharedPreferences.Editor editor;
    // 记录handler中的状态码
    private static final int         JSONDATACORRECT = 1;
    private static final int         JSONDATAERROR   = -1;
    private static final int         NETERROR        = 2;
    // 保存登录信息
    private JSONObject               loginInfo;
    private HttpClient               httpClient;
    // 获得服务器返回的主机地址的ip和端口和信息
    private String                   server_ip;
    private String                   server_port;
    private String                   message;
    // 用户自己输入的手机号
    private String                   Phone_Num       = null;
    private String                   Imei            = null;
    // http请求的json数据
    private String                   paramString;
    // 服务器返回的JSON数据的KEY值
    private String[]                 responseJsonKey = { "STATES", "SERVER_IP", "SERVER_PORT", "MESSAGE" };
    // 用于保存keyvalue值用于判断处理
    private HashMap<String, String>  JsonKeyValue;
    // 请求的服务器地址
    private static String            HTTP_URL_GET    = "http://58.210.161.122:8080/fpp/auth/user-auth.php?data=";

    private Handler                  handler         = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();
        initData();
        addListener();

    }

    /**
     * 初始化数据
     */
    private void initData() {
        preferences = getSharedPreferences("fpp_android", MODE_WORLD_WRITEABLE);
        editor = preferences.edit();
        Phone_Num = preferences.getString("phone_num", null);
        if (Phone_Num != null) {
            editText.setText(Phone_Num);
        }
        Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
        textView.setText(Imei);
    }

    /**
     * 增加监听器
     */
    private void addListener() {
        loginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // 读取edittext中的文本，更新到持久化
                Phone_Num = editText.getText().toString();
                editor.putString("phone_num", Phone_Num);
                editor.commit();
                // 创建用户信息
                loginInfo = createJSONObject(Imei, Phone_Num);
                // 测试创建只包含IMEI不包含手机号
                // loginInfo = createJSONObject(Imei, null);
                // 测试只包含手机号，不包含IMEI
                // loginInfo = createJSONObject(null, Phone_Num);
                // 创建Imei码视图
                // 开始进行与服务器交互
                httpGetRequest(loginInfo);
            }
        });

    }

    /**
     * 初始化控件
     */
    private void initUI() {
        loginButton = (Button) findViewById(R.id.login);
        editText = (EditText) findViewById(R.id.edit_phoneNum);
        textView = (TextView) findViewById(R.id.imei);

        this.handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case JSONDATACORRECT: {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                                .show();
                        //进入主界面
                        Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainActivityIntent);
                        finish();
                        break;
                    }
                    case JSONDATAERROR: {
                        Toast.makeText(getApplicationContext(), "JSON数据解析错误，程序即将退出",
                                Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new Builder(LoginActivity.this);
                        builder.setMessage("JSON数据解析错误，程序即将退出");
                        builder.setTitle("提示");
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                System.exit(0);
                            }
                        });
                        builder.create().show();

                        break;
                    }
                    case NETERROR: {
                        Toast.makeText(getApplicationContext(), "连接不上服务器，请检查服务器配置",
                                Toast.LENGTH_SHORT).show();
                    }
                    default: {
                        break;
                    }
                }

            }

        };
    }

    /**
     * 进行http提交请求得到服务器返回的数据
     * 
     * @param loginJSONObeject
     */
    private void httpGetRequest(final JSONObject loginJSONObeject) {
        httpClient = new DefaultHttpClient();
        // 设置连接超时
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        // 设置请求超时
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);

        new Thread() {
            @Override
            public void run() {
                try {
                    Log.d("loginInfo", loginJSONObeject.toString());
                    paramString = URLEncoder.encode(loginJSONObeject.toString(), "UTF-8");

                    HttpGet get = new HttpGet(HTTP_URL_GET + paramString);

                    Log.d("httpget", HTTP_URL_GET + paramString);
                    HttpResponse httpResponse = httpClient.execute(get);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        // 读取服务器的响应
                        String line = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                        Log.d("httpgetMessage", line);

                        // 测试JSON数据格式错误的数据
                        // line =
                        // "{\"STATtS\":\"FAILED\",\"SERVER_IP\":\"NULL\",\"SERVER_PORT\":\"NULL\",\"MESSAGE\":\"DATA ERROR\"}";

                        // 验证JSON数据格式
                        verify(line);

                    }
                    else {
                        Log.e("status error", "error");
                    }
                } catch (Exception e) {
                    Log.e(e.getMessage(), e.getLocalizedMessage());
                }
            }
        }.start();

    }

    /**
     * 获得loginInfo的json数据
     * 
     * @param ImeiData
     * @param PhoneNumData
     * @return
     */
    private JSONObject createJSONObject(String ImeiData, String PhoneNumData) {

        JSONObject loginInfo = new JSONObject();
        try {
            loginInfo.put("IMEI", ImeiData);
            loginInfo.put("PHONE_NUM", PhoneNumData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return loginInfo;
    }

    /**
     * 进行数据验证
     * 
     * @param jsonString
     */
    private void verify(String jsonString) {

        // 测试服务器返回的json数据格式错误问题
        // jsonString =
        // "{\"STATtS\":\"FAILED\",\"SERVER_IP\":\"NULL\",\"SERVER_PORT\":\"NULL\",\"MESSAGE\":\"DATA ERROR\"}";

        Log.d("testJsonData", jsonString);
        JSONObject jsonObject;
        JsonKeyValue = new HashMap<String, String>();
        try {
            jsonObject = new JSONObject(jsonString);
            for (int i = 0; i < responseJsonKey.length; i++) {
                String value = jsonObject.getString(responseJsonKey[i]);
                JsonKeyValue.put(responseJsonKey[i], value);
            }
            // 如果返回的状态是成功状态
            if (JsonKeyValue.get(responseJsonKey[0]).equals("SUCCESS")) {
                server_ip = JsonKeyValue.get(responseJsonKey[1]);
                server_port = JsonKeyValue.get(responseJsonKey[2]);
                message = JsonKeyValue.get(responseJsonKey[3]);
                
                //将IP和端口赋值给全局变量
                SettingsData.SERVER_IP = server_ip;
                SettingsData.SERVER_PORT = Integer.valueOf(server_port);
                
               


            }
            // 返回的是失败状态
            else {
                message = JsonKeyValue.get(responseJsonKey[3]);
            }
            // 解析JSON成功后返回是否通过验证
            Log.d("server_message", message);
            Message msg = new Message();
            msg.what = LoginActivity.JSONDATACORRECT;
            handler.sendMessage(msg);

        } catch (JSONException e) {
            // 解析JSON发现格式错误，退出程序
            Log.e("Json解析错误", "JSON数据解析错误");
            Message msg = new Message();
            msg.what = LoginActivity.JSONDATAERROR;
            handler.sendMessage(msg);
            e.printStackTrace();
        }
    }


    

}
