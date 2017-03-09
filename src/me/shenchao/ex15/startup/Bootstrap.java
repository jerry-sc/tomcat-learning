package me.shenchao.ex15.startup;

//explain Digester and StandardContext
// use ContextConfig so we don't need to instantiate wrapper

import org.apache.catalina.Connector;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Loader;
import org.apache.catalina.connector.http.HttpConnector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.ContextConfig;


public final class Bootstrap {

    // invoke: http://localhost:8080/app1/Modern or
    // http://localhost:8080/app2/Primitive
    // note that we don't instantiate a Wrapper here,
    // ContextConfig reads the WEB-INF/classes dir and loads all servlets.
    public static void main(String[] args) {
        System.setProperty("catalina.base", System.getProperty("user.dir"));
        Connector connector = new HttpConnector();

        /**
         * 与其他容器不同的是，StandardContext容器必须有一个监听器，这个监听器会负责配置StandardContext实例，
         * 将configured置为true，否则无法启动
         */
        Context context = new StandardContext();
        // StandardContext's start method adds a default mapper
        context.setPath("/app1");
        context.setDocBase("app1");
        LifecycleListener listener = new ContextConfig();
        ((Lifecycle) context).addLifecycleListener(listener);

        Host host = new StandardHost();
        host.addChild(context);
        host.setName("localhost");
        host.setAppBase("webapps");

        Loader loader = new WebappLoader();
        context.setLoader(loader);
        connector.setContainer(host);
        try {
            connector.initialize();
            ((Lifecycle) connector).start();
            ((Lifecycle) host).start();
            Container[] c = context.findChildren();
            int length = c.length;
            for (int i = 0; i < length; i++) {
                Container child = c[i];
                System.out.println(child.getName());
            }

            // make the application wait until we press a key.
            System.in.read();
            ((Lifecycle) host).stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}