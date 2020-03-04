package com.weiquding.proxychecker;

import com.github.markusbernhardt.proxy.ProxySearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.List;

/**
 * 代理测试类
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/3/4
 * https://stackoverflow.com/questions/376101/setting-jvm-jre-to-use-windows-proxy-automatically
 */
public class ProxyChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyChecker.class);

    public static void main(String[] args) {
        if (args != null && args.length > 0 && "useProxyVole".equals(args[0])) {
            proxyVoleSelector();
        } else {
            defaultProxySelector();
        }

    }

    /**
     * Proxy Vole is a Java library to auto detect the platform network proxy settings.
     * https://github.com/MarkusBernhardt/proxy-vole
     * Use the Proxy Vole library to provide network connectivity out of the box for your Java application. It provides strategies for auto detecting the current proxy settings. There are many configurable strategies to choose from. At the moment Proxy Vole supports the following proxy detection strategies.
     * <p>
     * Read platform settings (Supports: Windows, KDE, Gnome, OSX)
     * Read browser setting (Supports: Firefox 3.x+, Internet Explorer; Chrome and Webkit use the platform settings)
     * Read environment variables (often used variables on Linux / Unix server systems)
     * Auto detection script by using WPAD/PAC (Not all variations supported)
     */
    private static void proxyVoleSelector() {
        //Using the default strategy to find the settings
        LOGGER.info("Using the default strategy of proxy-vole to find the settings");
        // Use the static factory method getDefaultProxySearch to create a proxy search instance
        // configured with the default proxy search strategies for the current environment.
        ProxySearch proxySearch = ProxySearch.getDefaultProxySearch();

        // Invoke the proxy search. This will create a ProxySelector with the detected proxy settings.
        ProxySelector proxySelector = proxySearch.getProxySelector();

        // Install this ProxySelector as default ProxySelector for all connections.
        ProxySelector.setDefault(proxySelector);

        // print proxies
        defaultProxySelector();
    }

    /**
     * Java DefaultProxySelector
     * 测试一: 1)不配置http_proxy环境变量；2)不设置Gnome代理；3)不设置java.net.useSystemProxies，不设置SystemProperties.
     * 测试二: 1)不配置http_proxy环境变量；2)不设置Gnome代理；3)设置java.net.useSystemProxies，不设置SystemProperties.
     * 测试三: 1)配置http_proxy环境变量；2)不设置Gnome代理；3)不设置java.net.useSystemProxies，不设置SystemProperties.
     * 测试四: 1)配置http_proxy环境变量；2)不设置Gnome代理；3)设置java.net.useSystemProxies，不设置SystemProperties.
     * 测试五：1)配置http_proxy环境变量；2)设置Gnome代理；3)不设置java.net.useSystemProxies，不设置SystemProperties.
     * 测试六：1)配置http_proxy环境变量；2)设置Gnome代理；3)设置java.net.useSystemProxies，不设置SystemProperties.
     * 测试七：1)配置http_proxy环境变量；2)设置Gnome代理；3)设置java.net.useSystemProxies；设置SystemProperties，但使用不同代理配置.
     */
    private static void defaultProxySelector() {
        LOGGER.info("detecting proxies");
        List<Proxy> proxies = null;
        try {
            ProxySelector proxySelector = ProxySelector.getDefault();
            if (proxySelector == null) {
                LOGGER.warn("Not found ProxySelector");
                return;
            }
            proxies = proxySelector.select(new URI("http://foo/bar"));
        } catch (URISyntaxException e) {
            LOGGER.error("Failed to select proxy: {}", e.getMessage());
        }
        if (proxies != null) {
            for (Proxy proxy : proxies) {
                LOGGER.info("proxy type:{} ", proxy.type());
                InetSocketAddress addr = (InetSocketAddress) proxy.address();
                if (addr == null) {
                    LOGGER.info("No Proxy");
                } else {
                    LOGGER.info("proxy hostname: {}", addr.getHostName());
                    System.setProperty("http.proxyHost", addr.getHostName());
                    LOGGER.info("proxy port: {}", addr.getPort());
                    System.setProperty("http.proxyPort", Integer.toString(addr.getPort()));
                }
            }
        }
    }

}