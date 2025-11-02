package com.hoppinzq.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BitcoinService {

    @Tool(name = "get_bitcoin_data", description = "获取近一个月的比特币数据，如果你需要获取比特币数据，或者用户要求你预测比特币走向，调用该工具即可")
    public String getData() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://min-api.cryptocompare.com/data/histohour?aggregate=6&e=CCCAGG&fsym=BTC&limit=120&tsym=USD&extraParams=widget")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        String string = response.body().string();
        JSONObject jsonObject = JSON.parseObject(string);
        JSONArray datas = jsonObject.getJSONArray("Data");
        JSONArray datasTemp = new JSONArray(datas.size());
        for (int i = 0; i < datas.size(); i++) {
            /**
             * 清洗数据
             */
            JSONObject data = datas.getJSONObject(i);
            JSONObject btn = new JSONObject();
            btn.put("time", data.getLong("time"));
            btn.put("close", data.getFloat("close"));
            btn.put("open", data.getFloat("open"));
            btn.put("high", data.getFloat("high"));
            btn.put("low", data.getFloat("low"));
            btn.put("volumeFrom", data.getFloat("volumeFrom"));
            btn.put("volumeTo", data.getFloat("volumeTo"));
            datasTemp.add(btn);
        }
        return datasTemp.toJSONString();
    }
}
