import AliyunPoster.poster;
import com.alibaba.cloudapi.sdk.model.ApiResponse;
import com.aliyun.iotx.api.client.IoTApiClientBuilderParams;
import com.aliyun.iotx.api.client.IoTApiRequest;
import com.aliyun.iotx.api.client.SyncApiClient;
import com.aliyun.openservices.iot.api.Profile;
import com.aliyun.openservices.iot.api.message.MessageClientFactory;
import com.aliyun.openservices.iot.api.message.api.MessageClient;
import com.aliyun.openservices.iot.api.message.callback.MessageCallback;
import com.aliyun.openservices.iot.api.message.entity.Message;
import com.aliyun.openservices.iot.api.message.entity.MessageToken;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class Application {
    static String appKey = "28568463";
    static String appSecret = "76aeaecccd36b0669cc815efbe34985c";

    static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        String endpoint = String.format("https://%s.iot-as-http2.cn-shanghai.aliyuncs.com:443", appKey);

        Profile profile = Profile.getAppKeyProfile(endpoint, appKey, appSecret);

        // Set up message client
        MessageClient client = MessageClientFactory.messageClient(profile);
        MessageCallback msgCallBack = new MessageCallback() {
            @Override
            public Action consume(MessageToken messageToken) {
                Message m = messageToken.getMessage();
                logger.info("receive:" + new String(messageToken.getMessage().getPayload()));
                byte[] content = messageToken.getMessage().getPayload();
                JSONObject mycontent = new JSONObject(new String(content));
                String model = mycontent.getString("modelId");
                getContentFromDataId(model);
                return MessageCallback.Action.CommitSuccess;
            }
        };
        client.setMessageListener(msgCallBack);

        poster myposter = new poster();

        Thread posterThread = new Thread(myposter);
        posterThread.start();

        // 数据接收
        client.connect(msgCallBack);
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static void getContentFromDataId(String modelId) {
        logger.info("requesting for data with id:" + modelId);
        IoTApiClientBuilderParams ioTApiClientBuilderParams = new IoTApiClientBuilderParams();

        ioTApiClientBuilderParams.setAppKey(appKey);
        ioTApiClientBuilderParams.setAppSecret(appSecret);
        SyncApiClient syncClient = new SyncApiClient(ioTApiClientBuilderParams);

        Set<String> returnFields = new HashSet<String>();
        returnFields.add("*");

        IoTApiRequest request = new IoTApiRequest();
        request.setApiVer("0.0.3");
        request.putParam("modelId", modelId);
        request.putParam("pageSize", 20);
        request.putParam("pageNum", 1);
        request.putParam("returnFields", returnFields);

        //请求参数域名、path、request
        String host = "api.link.aliyun.com";
        String path = "/data/model/data/query";
        try {
            ApiResponse response = syncClient.postBody(host, path, request, true);
            logger.info("resposne header is:" + response.getHeaders().toString());
            logger.info("response body is:" + response.getBodyStr());
            logger.info("response content type is:" + response.getContentType());
            logger.info("response message is:" + response.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
