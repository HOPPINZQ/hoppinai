package com.hoppinzq.service.hander;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.bean.*;
import com.hoppinzq.service.constant.ApiCommConstant;
import com.hoppinzq.service.core.ApiRunnable;
import com.hoppinzq.service.core.ApiStore;
import com.hoppinzq.service.exception.ResultReturnException;
import com.hoppinzq.service.util.Base64Util;
import com.hoppinzq.service.util.CookieUtils;
import com.hoppinzq.service.util.JSONUtil;
import com.hoppinzq.service.util.StringUtil;
import com.hoppinzq.service.utils.ConvertJSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;

/**
 * @author:ZhangQi 默认网关抽象类
 */
public abstract class AbstractApiGatewayHandler implements ApiGatewayHandler {
    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayHandler.class);

    public void init(HttpServletRequest request, HttpServletResponse response, RequestParam requestParam) throws IOException {
        long start = System.currentTimeMillis();
        RequestContext.enter(requestParam);
        requestParam.setRequest(request);
        requestParam.setResponse(response);
        requestParam.setStart(start);
        String url = request.getRequestURL().toString();
        requestParam.setUrl(url);
        String method = request.getParameter(ApiCommConstant.METHOD);
        requestParam.setMethod(method);
        String params = request.getParameter(ApiCommConstant.PARAMS);
        requestParam.setParams(params);
        if ("GET".equals(request.getMethod())) {
            getHandler(request, response, requestParam);
        } else if ("POST".equals(request.getMethod())) {
            postHandler(request, response, requestParam);
        } else {
            otherHandler(request, response, requestParam);
        }
    }

    public void otherHandler(HttpServletRequest request, HttpServletResponse response, RequestParam requestParam) throws IOException {

    }


    public void getHandler(HttpServletRequest request, HttpServletResponse response, RequestParam requestParam) throws IOException {

    }

    public void postHandler(HttpServletRequest request, HttpServletResponse response, RequestParam requestParam) throws IOException {
        List<FormInfo> fileInfos = getPostData(request, requestParam);
        String params = requestParam.getParams();
        if (fileInfos.size() != 0) {
            if ("GET".equals(request.getMethod())) {
                throw new ResultReturnException(ErrorEnum.ZQ_GATEWAY_FILE_LOAD_MUST_POST);
            }
            Map paramsMap = JSONObject.parseObject(params, Map.class);
            if (paramsMap == null) {
                paramsMap = new HashMap();
            }
            StringBuilder formInfoStr = new StringBuilder();
            formInfoStr.append("[");
            for (int i = 0, j = fileInfos.size(); i < j; i++) {
                formInfoStr.append(fileInfos.get(i).toJsonString());
                if (i < j - 1) {
                    formInfoStr.append(",");
                }
            }
            formInfoStr.append("]");
            paramsMap.put("formInfos", formInfoStr);
            params = JSONObject.toJSONString(paramsMap);
            requestParam.setParams(params);
        } else {
            //post请求为null的情况，偶发,尚不清楚怎么回事
            StringBuffer jb = new StringBuffer();
            String line = null;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
            String jsonStr = jb.toString();
            if (StringUtil.isNotEmpty(jsonStr)) {
                params = JSON.parseObject(jsonStr).toJSONString();
            }
            requestParam.setParams(params);
        }
    }

    /**
     * 具体执行方法
     *
     * @param request
     * @return
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RequestParam requestParam = new RequestParam();
        init(request, response, requestParam);
        String method = requestParam.getMethod();
        String params = requestParam.getParams();
        Object result = null;
        try {
            token(request, response, requestParam);
            ApiRunnable apiRun = sysParamsValidate(request, response, method, params, requestParam);
            type(request, response, requestParam);
            sign(request, response, requestParam);
            cache(request, response, requestParam);
            request.setCharacterEncoding("UTF-8");
            logger.debug("请求接口={" + method + "} 参数=" + params + "");
            String timestamp = request.getParameter(ApiCommConstant.TIMESTAMP);
            requestParam.setTimestamp(timestamp);
            //ServiceMethodApiBean serviceMethodApiBean=requestParam.getApiRunnable().getServiceMethodApiBean();
            if (timestamp == null) {
                Object[] args = buildParams(apiRun, params, request, requestParam);
                result = apiRun.run(args);
                result = JSONObject.toJSON(ApiResponse.data(result, "操作成功"));
            } else {
                //缓存策略，需要redis
            }
            requestParam.setResult(result);
            long endTime = setEnd(requestParam);
            logger.debug("接口:" + request.getRequestURL().toString() + " 请求时长:" + (endTime - requestParam.getStart()));
            RequestInfo requestInfo = new RequestInfo("INFO", null);
            requestParam.setRequestInfo(requestInfo);
            afterSuccessRequest(request, response, requestParam);
            logger.debug("请求信息:\n {}", requestInfo);
        } catch (ResultReturnException e) {
            logger.error("调用接口={" + method + "}异常  参数=" + params + "", e);
            setEnd(requestParam);
            RequestInfo requestInfo = new RequestInfo("ERROR", e);
            result = handleError(e, requestInfo, requestParam);
            requestParam.setRequestInfo(requestInfo);
            afterErrorRequest(request, response, requestParam);
            logger.error("错误的请求:\n {}", requestInfo);
        } catch (InvocationTargetException e) {
            logger.error("调用接口={" + method + "}异常  参数=" + params + "", e.getTargetException());
            setEnd(requestParam);
            RequestInfo requestInfo = new RequestInfo("ERROR", e.getTargetException());
            result = handleError(e.getTargetException(), requestInfo, requestParam);
            requestParam.setRequestInfo(requestInfo);
            afterErrorRequest(request, response, requestParam);
            logger.error("错误的请求:\n {}", requestInfo);
        } catch (Exception e) {
            logger.error("其他异常", e);
            setEnd(requestParam);
            RequestInfo requestInfo = new RequestInfo("ERROR", e);
            result = handleError(e, requestInfo, requestParam);
            requestParam.setRequestInfo(requestInfo);
            afterErrorRequest(request, response, requestParam);
            logger.error("错误的请求:\n {}", requestInfo);
        } finally {
            response.setContentType("text/html; charset=UTF-8");
            afterRequest(request, response, requestParam);
            setOutParam(request, response, result, requestParam);
            RequestContext.exit();
        }
    }

    private long setEnd(RequestParam requestParam) {
        long endTime = System.currentTimeMillis();
        requestParam.setEnd(endTime);
        return endTime;
    }

    public Boolean right(HttpServletRequest request, HttpServletResponse response, RequestParam requestParam) throws IOException {
        //权限校验已移除
        return true;
    }

    /**
     * 签名验证
     *
     * @param request
     */
    public void sign(HttpServletRequest request, HttpServletResponse response, RequestParam requestParam) throws IOException {

    }

    /**
     * 缓存
     *
     * @param request
     * @param response
     */
    public void cache(HttpServletRequest request, HttpServletResponse response, RequestParam requestParam) throws IOException {
        //缓存已移除，由监听事件实现
    }

    /**
     * token 检验token，实现接口幂等
     *
     * @param request
     * @param response
     */
    public void token(HttpServletRequest request, HttpServletResponse response, RequestParam requestParam) throws IOException {
        if (request.getHeader("Authorization") != null) {
            requestParam.setToken(request.getHeader("Authorization"));
        }
        if (CookieUtils.getCookieValue(request, "ZQ_TOKEN") != null) {
            requestParam.setToken(CookieUtils.getCookieValue(request, "ZQ_TOKEN"));
        }
    }

    /**
     * 请求类型校验(重写)
     *
     * @param request
     * @param response
     */
    public void type(HttpServletRequest request, HttpServletResponse response, RequestParam requestParam) {
        String methodType = request.getMethod();//GET POST
        ApiMapping.Type requestType = requestParam.getApiRunnable().getServiceMethodApiBean().getRequestType();
        if (requestType != ApiMapping.Type.ALL) {
            if (!String.valueOf(requestType).equals(methodType)) {
                throw new ResultReturnException(ErrorEnum.COMMON_REQUEST_ERROR.getMsg() + ",该服务方法只接收:" + requestType + "请求，但是使用了" + methodType + "请求",
                        ErrorEnum.COMMON_REQUEST_ERROR.getCode());
            }
        }
    }

    /**
     * 成功后执行操作
     *
     * @param request
     * @param response
     */
    public void afterSuccessRequest(HttpServletRequest request, HttpServletResponse response, RequestParam requestParam) throws IOException {
        //System.out.println("请求成功！");
    }

    /**
     * 失败后执行操作
     *
     * @param request
     * @param response
     */
    public void afterErrorRequest(HttpServletRequest request, HttpServletResponse response, RequestParam requestParam) throws IOException {
        //System.out.println("请求失败！");
    }

    /**
     * 请求完成后执行操作（无论成功与否）
     *
     * @param request
     * @param response
     */
    public void afterRequest(HttpServletRequest request, HttpServletResponse response, RequestParam requestParam) throws IOException {
        //System.out.println("请求完成！");
    }

    public void setOutParam(HttpServletRequest request, HttpServletResponse response, Object result, RequestParam requestParam) throws IOException {
        PrintWriter out = response.getWriter();
        out.println(result.toString());
    }

    /***
     * 验证业务参数，和构建业务参数对象
     * @param run
     * @param paramJson
     * @param request
     * @return
     * @throws ResultReturnException
     */
    public Object[] buildParams(ApiRunnable run, String paramJson, HttpServletRequest request, RequestParam requestParam)
            throws ResultReturnException, IOException {
        Map<String, Object> map = null;
        try {
            map = JSONUtil.toMap(paramJson);
        } catch (IllegalArgumentException e) {
            throw new ResultReturnException(ErrorEnum.ZQ_GATEWAY_JSON_FORMAT_ERROR);
        }
        if (map == null) {
            map = new HashMap<>();
        }

        Method method = run.getTargetMethod();
        List<String> paramNames = Arrays.asList(new LocalVariableTableParameterNameDiscoverer()
                .getParameterNames(method));
        Class<?>[] paramTypes = method.getParameterTypes();

        for (Map.Entry<String, Object> m : map.entrySet()) {
            if (!paramNames.contains(m.getKey())) {
                throw new ResultReturnException(ErrorEnum.ZQ_GATEWAY_API_METHOD_PARAM_NOT_FOUND.getMsg() + "调用失败：接口不存在‘" + m.getKey() + "’参数",
                        ErrorEnum.ZQ_GATEWAY_API_METHOD_PARAM_NOT_FOUND.getCode());
            }
        }
        Object[] args = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            if (paramTypes[i].isAssignableFrom(HttpServletRequest.class)) {
                args[i] = request;
            } else if (map.containsKey(paramNames.get(i))) {
                try {
                    args[i] = ConvertJSONUtil.convertJsonToBean(map.get(paramNames.get(i)), paramTypes[i]);
                } catch (Exception e) {
                    throw new ResultReturnException(ErrorEnum.ZQ_GATEWAY_API_METHOD_ERROR_DATA.getMsg() + "调用失败：指定参数格式错误或值错误:‘" + paramNames.get(i) + "’,"
                            + e.getMessage(), ErrorEnum.ZQ_GATEWAY_API_METHOD_ERROR_DATA.getCode());
                }
            } else {
                args[i] = null;
            }
        }
        return args;
    }

    /**
     * 异常处理
     *
     * @param throwable
     * @param info
     * @return
     */
    public ApiResponse handleError(Throwable throwable, RequestInfo info, RequestParam requestParam) throws IOException {
        Integer code = 500;
        String message = "";
        if (throwable instanceof ResultReturnException) {
            code = ((ResultReturnException) throwable).getCode();
            message = throwable.getMessage();
        } // 扩展异常规范
        else {
            code = info.getCode();
            if (throwable instanceof Exception) {
                if (throwable instanceof SQLException) {
                    message = "SQL错误，请检查日志";
                } else {
                    message = throwable.getMessage();
                }
            } else {
                message = "出错了，错误码：" + info.getId();
            }
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(out);
        throwable.printStackTrace(stream);
        info.setException(throwable.toString());
        return ApiResponse.error(code, message);
    }

    /**
     * 解析form表单提交的数据，主要是针对前端传文件流，从而单独写了一套解析过程
     * todo 应该把文件流放到临时文件里，而不是把流写到一个字段里。
     *
     * @param request
     * @return
     */
    public List<FormInfo> getPostData(HttpServletRequest request, RequestParam requestParam) throws IOException {
        List<FormInfo> list = new ArrayList<>();
        //将当前上下文初始化给CommonsMutipartResolver
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        // 判断是否是多数据段提交格式
        if (multipartResolver.isMultipart(request)) {
            try {
                Collection<Part> parts = request.getParts();
                for (Iterator<Part> iterator = parts.iterator(); iterator.hasNext(); ) {
                    Part part = iterator.next();
                    FormInfo fileInfo = new FormInfo();
                    fileInfo.setName(part.getName());
                    fileInfo.setContentType(part.getContentType());
                    fileInfo.setSubmittedFileName(part.getSubmittedFileName());
                    fileInfo.setSize(part.getSize());
                    InputStream inputStream = part.getInputStream();
                    if (part.getContentType() == null) {
                        StringBuffer out = new StringBuffer();
                        byte[] b = new byte[4096];
                        for (int n; (n = inputStream.read(b)) != -1; ) {
                            out.append(new String(b, 0, n));
                        }
                        fileInfo.setInputStream(out.toString());
                    } else {
                        fileInfo.setInputStream(Base64Util.inputStreamToBase(inputStream));
                    }
                    list.add(fileInfo);
                }
            } catch (Exception ex) {
                //直接取request的输出流
                // todo 未实现，情形：后台之间（通过rpc,不是模拟http请求）相互调用传文件流，
                //  这个通过zq的rpc已经实现了后台直接将流做为header（在MethodInvocationHandler类里），这里未验证
                StringBuffer data = new StringBuffer();
                String line = null;
                BufferedReader reader = null;
                try {
                    reader = request.getReader();
                    while (null != (line = reader.readLine())) {
                        data.append(line);
                    }
                } catch (IOException e) {

                } finally {

                }
            }
        } else {

//            Enumeration<String> strings=request.getParameterNames();
//            while (strings.hasMoreElements()){
//                String postParam=strings.nextElement();
//                System.err.println(postParam);
//
//            }
            Map map = request.getParameterMap();
            Set keSet = map.entrySet();
            for (Iterator itr = keSet.iterator(); itr.hasNext(); ) {
                Map.Entry me = (Map.Entry) itr.next();
                Object requestKey = me.getKey();
                Object requestValue = me.getValue();
                String[] value = new String[1];
                if (requestValue instanceof String[]) {
                    value = (String[]) requestValue;
                } else {
                    value[0] = requestValue.toString();
                    break;
                }
                for (int k = 0; k < value.length; k++) {
                    //todo 可以将method和params都作为传参的data，得额外解析ok
                    String value_ = String.valueOf(requestKey);
                    if (value[k].length() != 0) {
                        value_ += "=" + value[k];//特殊字符=截取字符串补充
                    }
                    if (value_.indexOf("{") != -1 && value_.indexOf("}") != -1) {
                        requestParam.setParams(value_);
                    }
                }
            }
        }
        requestParam.setFormInfoList(list);
        return list;
    }

    /**
     * 系统参数校验
     * 重写该方法以实现自己的参数校验
     * 返回ApiRunnable对象
     *
     * @param request
     * @return
     * @throws ResultReturnException
     */
    public ApiRunnable sysParamsValidate(HttpServletRequest request, HttpServletResponse response, String apiName, String json, RequestParam requestParam) throws ResultReturnException, IOException {
        ApiRunnable api;
        if (apiName == null || "".equals(apiName.trim())) {
            throw new ResultReturnException(ErrorEnum.ZQ_GATEWAY_METHOD_NOT_FOUND);
        } else if (json == null) {
            throw new ResultReturnException(ErrorEnum.ZQ_GATEWAY_PARAMS_NOT_FOUND);
        } else if ((api = ApiStore.findApiRunnable(apiName)) == null) {
            throw new ResultReturnException(ErrorEnum.ZQ_GATEWAY_API_NOT_FOUND.getMsg() + ",API:" + apiName, ErrorEnum.ZQ_GATEWAY_API_NOT_FOUND.getCode());
        }
        requestParam.setApiRunnable(api);
        if (!right(request, response, requestParam)) {
            throw new ResultReturnException(ErrorEnum.COMMON_USER_TOKEN_OUT_DATE);
        }
        return api;
    }

}
