package AliyunPoster;

import com.alibaba.cloudapi.sdk.model.ApiResponse;
import com.aliyun.iotx.api.client.IoTApiClientBuilderParams;
import com.aliyun.iotx.api.client.IoTApiRequest;
import com.aliyun.iotx.api.client.SyncApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class poster implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(poster.class);

    private final String appKey = "28568463";
    private final String appSecret = "76aeaecccd36b0669cc815efbe34985c";

    public void createNewPoster() {
        IoTApiClientBuilderParams ioTApiClientBuilderParams = new IoTApiClientBuilderParams();

        ioTApiClientBuilderParams.setAppKey(appKey);
        ioTApiClientBuilderParams.setAppSecret(appSecret);

        SyncApiClient syncClient = new SyncApiClient(ioTApiClientBuilderParams);

        IoTApiRequest request = new IoTApiRequest();
        //设置api的版本
        request.setApiVer("0.0.3");
        //request.setId("42423423");
        //request.setVersion("1.2");

        Map<String,Object> props = new HashMap<>();
        props.put("iotid", "A123456");
        props.put("productKey", "P123456");
        props.put("deviceName", "D123456");
        props.put("age", 10);
        props.put("gender", "male");
        props.put("image", "xxxxxx");
        props.put("landmarks", "xxxxxx");
        props.put("faceId", "xxxxxx");
        props.put("faceIdEffectiveTime", 0L);
        props.put("recordTime", 0L);
        props.put("poseScore", 0.1);
        props.put("blurScore", 0.1);
        //props.put("groupId", "xxxxxx");

        // 接口参数
        request.putParam("modelId","CustomerFlowBasicFaceData");
        request.putParam("properties", props);

        //请求参数域名、path、request
        String host = "api.link.aliyun.com";
        String path = "/data/model/data/insert";
        try {
            ApiResponse response = syncClient.postBody(host, path, request, true);
            logger.info(response.getHeaders().toString());
            logger.info(response.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            createNewPoster();
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
