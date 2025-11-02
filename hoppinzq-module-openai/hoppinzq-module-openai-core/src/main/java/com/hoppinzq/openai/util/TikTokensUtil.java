package com.hoppinzq.openai.util;

import com.hoppinzq.model.openai.completion.chat.ChatMessage;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.ModelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

public class TikTokensUtil {

    private static final Map<String, Encoding> modelMap = new HashMap<>();
    private static final EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();

    static {
        for (ModelType modelType : ModelType.values()) {
            modelMap.put(modelType.getName(), registry.getEncodingForModel(modelType));
        }
        modelMap.put(ModelEnum.GPT_3_5_TURBO_0301.getName(), registry.getEncodingForModel(ModelType.GPT_3_5_TURBO));
        modelMap.put(ModelEnum.GPT_4_32K.getName(), registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelEnum.GPT_4_32K_0314.getName(), registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelEnum.GPT_4_0314.getName(), registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelEnum.GPT_4_1106_preview.getName(), registry.getEncodingForModel(ModelType.GPT_4));
    }

    public static Map<String, Encoding> getModelMap() {
        return modelMap;
    }

    public static Set<String> getModelName() {
        return modelMap.keySet();
    }


    /**
     * 对文本进行编码。
     *
     * @param enc  编码器对象，用于对文本进行编码。
     * @param text 需要编码的文本。
     * @return 编码后的整数列表，如果文本为空，则返回空列表。
     */
    public static List<Integer> encode(Encoding enc, String text) {
        return isBlank(text) ? new ArrayList<>() : enc.encode(text);
    }

    public static int tokens(Encoding enc, String text) {
        return encode(enc, text).size();
    }

    public static String decode(Encoding enc, List<Integer> encoded) {
        return enc.decode(encoded);
    }

    public static Encoding getEncoding(EncodingType encodingType) {
        Encoding enc = registry.getEncoding(encodingType);
        return enc;
    }

    public static List<Integer> encode(EncodingType encodingType, String text) {
        if (isBlank(text)) {
            return new ArrayList<>();
        }
        Encoding enc = getEncoding(encodingType);
        List<Integer> encoded = enc.encode(text);
        return encoded;
    }

    public static int tokens(EncodingType encodingType, String text) {
        return encode(encodingType, text).size();
    }

    public static String decode(EncodingType encodingType, List<Integer> encoded) {
        Encoding enc = getEncoding(encodingType);
        return enc.decode(encoded);
    }

    public static Encoding getEncoding(String modelName) {
        return modelMap.get(modelName);
    }

    public static List<Integer> encode(String modelName, String text) {
        if (isBlank(text)) {
            return new ArrayList<>();
        }
        Encoding enc = getEncoding(modelName);
        if (Objects.isNull(enc)) {
            return new ArrayList<>();
        }
        List<Integer> encoded = enc.encode(text);
        return encoded;
    }

    public static int tokens(String modelName, String text) {
        return encode(modelName, text).size();
    }


    /**
     * https://github.com/openai/openai-cookbook/blob/main/examples/How_to_count_tokens_with_tiktoken.ipynb
     *
     * @param modelName
     * @param messages
     * @return token
     */
    public static int tokens(String modelName, List<ChatMessage> messages) {
        Encoding encoding = getEncoding(modelName);
        int tokensPerMessage = 0;
        int tokensPerName = 0;
        //3.5统一处理
        if (modelName.equals("gpt-3.5-turbo-0301") || modelName.equals("gpt-3.5-turbo")) {
            tokensPerMessage = 4;
            tokensPerName = -1;
        }
        //4.0统一处理
        if (modelName.equals("gpt-4") || modelName.equals("gpt-4-0314")) {
            tokensPerMessage = 3;
            tokensPerName = 1;
        }
        int sum = 0;
        for (ChatMessage msg : messages) {
            sum += tokensPerMessage;
            sum += tokens(encoding, msg.getContent());
            sum += tokens(encoding, msg.getRole());
            sum += tokens(encoding, msg.getName());
            if (isNotBlank(msg.getName())) {
                sum += tokensPerName;
            }
        }
        sum += 3;
        return sum;
    }

    public static String decode(String modelName, List<Integer> encoded) {
        Encoding enc = getEncoding(modelName);
        return enc.decode(encoded);
    }

    public static ModelType getModelTypeByName(String name) {
        if (ModelEnum.GPT_3_5_TURBO_0301.getName().equals(name)) {
            return ModelType.GPT_3_5_TURBO;
        }
        if (ModelEnum.GPT_4.getName().equals(name)
                || ModelEnum.GPT_4_32K.getName().equals(name)
                || ModelEnum.GPT_4_32K_0314.getName().equals(name)
                || ModelEnum.GPT_4_0314.getName().equals(name)) {
            return ModelType.GPT_4;
        }

        for (ModelType modelType : ModelType.values()) {
            if (modelType.getName().equals(name)) {
                return modelType;
            }
        }
        return null;
    }

    public static boolean isBlankChar(int c) {
        return Character.isWhitespace(c) || Character.isSpaceChar(c) || c == 65279 || c == 8234 || c == 0 || c == 12644 || c == 10240 || c == 6158;
    }

    public static boolean isBlankChar(char c) {
        return isBlankChar((int) c);
    }

    public static boolean isNotBlank(CharSequence str) {
        return !isBlank(str);
    }

    public static boolean isBlank(CharSequence str) {
        int length;
        if (str != null && (length = str.length()) != 0) {
            for (int i = 0; i < length; ++i) {
                if (!isBlankChar(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum ModelEnum {
        /**
         * gpt-3.5-turbo
         */
        GPT_3_5_TURBO("gpt-3.5-turbo"),
        GPT_3_5_TURBO_0301("gpt-3.5-turbo-0301"),
        /**
         * GPT4.0
         */
        GPT_4("gpt-4"),
        GPT_4_0314("gpt-4-0314"),
        /**
         * GPT4.0 超长上下文
         */
        GPT_4_32K("gpt-4-32k"),
        GPT_4_32K_0314("gpt-4-32k-0314"),
        GPT_4_1106_preview("gpt-4-1106-preview");
        private String name;
    }

}
