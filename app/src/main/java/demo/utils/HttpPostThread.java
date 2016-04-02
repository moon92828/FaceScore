package demo.utils;

import android.os.Handler;
import android.os.Message;

import demo.net.MyPost;

/**
 * Created by moon9 on 2016/3/7.
 */
public class HttpPostThread implements Runnable {
    private Handler hand;
    private String url;
    private String value;
    private String[] img=null;
    private MyPost myGet = new MyPost();

    public HttpPostThread(Handler hand, String endParamerse, String value,
                          String[] img) {
        this.hand = hand;
        // 拼接访问服务器完整的地址
        url = endParamerse;
        this.value = value;
        this.img = img;
    }

    public HttpPostThread(Handler hand, String endParamerse, String value) {
        this.hand = hand;
        // 拼接访问服务器完整的地址
        url = endParamerse;
        this.value = value;
    }

    @Override
    public void run() {
        // 获取我们回调主ui的message
        Message msg = hand.obtainMessage();
        String result = null;
        if (img==null) {
            result = myGet.doPost(url, value);
        } else {
            result = myGet.doPost(url, img, value);
        }
        if (result != null) {
            msg.what = 200;
            msg.obj = result;
        }else{
            msg.what=404;
        }

        // 给主ui发送消息传递数据
        hand.sendMessage(msg);

    }

}
