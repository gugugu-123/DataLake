package com.example.demo.filter;

import com.example.demo.util.JsonTrimUtils;
import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TrimHttpServletRequestWrapper extends HttpServletRequestWrapper {
 
    /**
     * 暂存requestbody内容
     */
    private String content;
 
    /**
     * 暂存request
     */
    private HttpServletRequest request;
 
    /**
     * json数据类型
     */
    private static String JSON_CONTENT = "application/json";
 
    /**
     * 编码格式
     */
    private static String ENCODING_UTF_8 = "utf-8";
 
    /**
     * 构造方法
     *
     * @param request request
     */
    public TrimHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.request = request;
        //仅处理application/json
        if (JSON_CONTENT.equalsIgnoreCase(request.getContentType())) {
            String content = IOUtils.toString(request.getInputStream(), ENCODING_UTF_8);//获取文本数据;
            this.content = JsonTrimUtils.jsonTrim(content).toString();
        }
    }
 
    /**
     * 重写
     *
     * @param name 参数名
     * @return trim后的值
     */
    @Override
    public String getParameter(String name) {
        String parameter = super.getParameter(name);
        return parameter == null ? null : parameter.trim();
    }
 
    /**
     * 重写
     *
     * @param name 参数名
     * @return trim后的值
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] parameterValues = super.getParameterValues(name);
        if (parameterValues == null || parameterValues.length < 1) {
            return parameterValues;
        } else {
            String[] values = new String[parameterValues.length];
            for (int i = 0; i < parameterValues.length; i++) {
                values[i] = parameterValues[i].trim();
            }
            return values;
        }
    }
 
    /**
     * 重写
     *
     * @return ServletInputStream
     * @throws IOException IOException
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (!JSON_CONTENT.equalsIgnoreCase(request.getContentType())) {
            //非application/json数据不处理
            return super.getInputStream();
        } else {
            ByteArrayInputStream in = new ByteArrayInputStream(this.content.getBytes(ENCODING_UTF_8));
            return new ServletInputStream() {
                @Override
                public int read() throws IOException {
                    return in.read();
                }
 
                @Override
                public int read(byte[] b, int off, int len) throws IOException {
                    return in.read(b, off, len);
                }
 
                @Override
                public int read(byte[] b) throws IOException {
                    return in.read(b);
                }
 
                @Override
                public void setReadListener(ReadListener listener) {
                }
 
                @Override
                public boolean isReady() {
                    return false;
                }
 
                @Override
                public boolean isFinished() {
                    return false;
                }
 
                @Override
                public long skip(long n) throws IOException {
                    return in.skip(n);
                }
 
                @Override
                public void close() throws IOException {
                    in.close();
                }
 
                @Override
                public synchronized void mark(int readlimit) {
                    in.mark(readlimit);
                }
 
                @Override
                public synchronized void reset() throws IOException {
                    in.reset();
                }
            };
        }
    }
}