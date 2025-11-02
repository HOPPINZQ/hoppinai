package com.hoppinzq.function.bitcoin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.hoppinzq.function.zq.constants.AiFunctionCallResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class BitcoinFunctionCall extends AiFunctionCallResponse {

    public BitcoinFunctionCall(String date) {
        try {
            success(getData());
        } catch (Exception e) {
            e.printStackTrace();
            fail("获取比特币数据失败：" + e.getMessage());
        }
    }

    private static String getData() throws IOException {
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
            JSONObject data = datas.getJSONObject(i);
            Long time = data.getLong("time");
            Float close = data.getFloat("close");
            Float open = data.getFloat("open");
            Float high = data.getFloat("high");
            Float low = data.getFloat("low");
            Float volumeFrom = data.getFloat("volumeFrom");
            Float volumeTo = data.getFloat("volumeTo");
            JSONObject btn = new JSONObject();
            btn.put("time", time);
            btn.put("close", close);
            btn.put("open", open);
            btn.put("high", high);
            btn.put("low", low);
            btn.put("volumeFrom", volumeFrom);
            btn.put("volumeTo", volumeTo);
            datasTemp.add(btn);
        }
        return datasTemp.toJSONString();
    }

    public static class BitcoinFunctionCallRequest {
        @JsonPropertyDescription("日期")
        public String date;
    }
}
