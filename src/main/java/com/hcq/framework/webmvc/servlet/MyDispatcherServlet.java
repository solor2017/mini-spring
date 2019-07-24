package com.hcq.framework.webmvc.servlet;

import com.hcq.framework.annotation.MyController;
import com.hcq.framework.annotation.MyRequestMapping;
import com.hcq.framework.context.MyApplicationContext;
import com.hcq.framework.webmvc.handler.MyHandlerAdapter;
import com.hcq.framework.webmvc.handler.MyHandlerMapping;
import com.hcq.framework.webmvc.view.MyModelAndView;
import com.hcq.framework.webmvc.view.MyView;
import com.hcq.framework.webmvc.view.MyViewResolver;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: solor
 * @Since: 2019/6/18 18:39
 * @Description:
 */
@Slf4j
public class MyDispatcherServlet extends HttpServlet {

    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    private MyApplicationContext context;

    private List<MyHandlerMapping> handlerMappings = new ArrayList<MyHandlerMapping>();
    private Map<MyHandlerMapping, MyHandlerAdapter> handlerAdapters = new HashMap<MyHandlerMapping, MyHandlerAdapter>();

    private List<MyViewResolver> viewResolvers = new ArrayList<MyViewResolver>();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doDispatch(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        context = new MyApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));

        initStrategies(context);
    }
    /**
     * web请求入口
     * @param req
     * @param resp
     * @throws ServletException
     */
    public void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        //url匹配handlemapping
        MyHandlerMapping handler = getHandle(req);
        if(handler == null){
            processDispatchResult(req,resp,new MyModelAndView("404"));
            return;
        }
        MyHandlerAdapter myHandlerAdapter = getHandleAdpter(handler);
        //handle执行invoke

        try {
            MyModelAndView myModelAndView = myHandlerAdapter.handle(req,resp,handler);
            processDispatchResult(req,resp,myModelAndView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回view


    }


    /**
     *
     * @param handler
     * @return
     */
    private MyHandlerAdapter getHandleAdpter(MyHandlerMapping handler) {
        return null;
    }

    /**
     * 初始化mvc九大组件
     * @param context
     */
    private void initStrategies(MyApplicationContext context) {


        initMultipartResolver(context);

        initLocaleResolver(context);

        initThemeResolver(context);

        initHandlerMappings(context);

        initHandlerAdapters(context);

        initHandlerExceptionResolvers(context);

        initRequestToViewNameTranslator(context);

        initViewResolvers(context);

        initFlashMapManager(context);
    }

    /**
     * 用来管理FlashMap的。主要用在redirect中传递参数
     * @param context
     */
    private void initFlashMapManager(MyApplicationContext context) {
    }

    /**
     * 初始化视图转换器
     * ViewResolver用来将String类型的视图名和Locale解析为View类型的视图。
     * View是用来渲染页面的，也就是将程序返回的参数填入模板里，生成html（也可能是其它类型）文件。
     * 这里就有两个关键问题：使用哪个模板？用什么技术（规则）填入参数？这其实是ViewResolver主要要做的工作，
     * ViewResolver需要找到渲染所用的模板和所用的技术（也就是视图的类型）进行渲染，具体的渲染过程则交由不同的视图自己完成。
     * @param context
     */
    private void initViewResolvers(MyApplicationContext context) {

        //读取模板文件信息
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootFilePath = this.getClass().getResource(templateRoot).getFile();

        File file = new File(templateRootFilePath);
        //可能存在多个模板
        for (String s : file.list()) {
            MyViewResolver myViewResolver = new MyViewResolver(s);
            this.viewResolvers.add(myViewResolver);
        }

    }

    /**
     * 初始化视图预处理器
     * ViewName是根据ViewName查找View，但有的Handler处理完后并没有设置View也没有设置ViewName，这时就需要从request获取ViewName了，
     * 如何从request中获取ViewName就是RequestToViewNameTranslator要做的事情了。
     * RequestToViewNameTranslator在Spring MVC容器里只可以配置一个，所以所有request到ViewName的转换规则都要在一个Translator里面全部实现。
     * @param context
     */
    private void initRequestToViewNameTranslator(MyApplicationContext context) {
        
    }

    /**
     * 初始化异常拦截器
     * 其它组件都是用来干活的。在干活的过程中难免会出现问题，出问题后怎么办呢？
     * 这就需要有一个专门的角色对异常情况进行处理，在SpringMVC中就是HandlerExceptionResolver。
     * 具体来说，此组件的作用是根据异常设置ModelAndView，之后再交给render方法进行渲染。
     * @param context
     */
    private void initHandlerExceptionResolvers(MyApplicationContext context) {
        
    }

    /**
     * 2.初始化参数适配器
     *spring中处理比较复杂，这里简化操作
     * 因为SpringMVC中的Handler可以是任意的形式，只要能处理请求就ok，
     * 但是Servlet需要的处理方法的结构却是固定的，都是以request和response为参数的方法。
     * 如何让固定的Servlet处理方法调用灵活的Handler来进行处理呢？这就是HandlerAdapter要做的事情。
     * @param context
     */
    private void initHandlerAdapters(MyApplicationContext context) {
        //把一个requet请求变成一个handler，参数都是字符串的，自动配到handler中的形参

        //可想而知，他要拿到HandlerMapping才能干活
        //就意味着，有几个HandlerMapping就有几个HandlerAdapter
        for (MyHandlerMapping handlerMapping : this.handlerMappings) {
            this.handlerAdapters.put(handlerMapping,new MyHandlerAdapter());
        }
    }

    /**
     * 1
     *是用来查找Handler的。在SpringMVC中会有很多请求，每个请求都需要一个Handler处理，
     * 具体接收到一个请求之后使用哪个Handler进行处理呢？这就是HandlerMapping需要做的事。
     * @param context
     */
    private void initHandlerMappings(MyApplicationContext context) {

        String [] beanNames = context.getBeanDefinitionNames();
        try {

            for (String beanName : beanNames) {
                
                Object controllerBean = context.getBean(beanName);
                Class<?> controllerClass  =  controllerBean.getClass();
                if (!controllerClass.isAnnotationPresent(MyController.class)) {continue;}
                //将url和hadlemaping进行比对
                String baseUrl = "";
                if (controllerClass.isAnnotationPresent(MyRequestMapping.class)) {
                    MyRequestMapping myRequestMapping = controllerClass.getAnnotation(MyRequestMapping.class);
                    baseUrl = myRequestMapping.value().trim();
                }

                Method[] methods = controllerClass.getMethods();

                for (Method method : methods) {
                    if (!method.isAnnotationPresent(MyRequestMapping.class)) {continue;}
                    MyRequestMapping annotation = method.getAnnotation(MyRequestMapping.class);
                    String requestPath = annotation.value();
                    String regex = ("/" + baseUrl + "/" + requestPath.replaceAll("\\*",".*")).
                            replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);
                    //参照spring构建一个handlerMapping的集合
                    this.handlerMappings.add(new MyHandlerMapping(pattern,controllerBean,method));
                    log.info("Mapped " + regex + "," + method);
                }


            }
        } catch (Exception e){
            e.printStackTrace();
        }


    }

    /**
     * 初始化模板处理器
     * 用于解析主题。SpringMVC中一个主题对应一个properties文件，里面存放着跟当前主题相关的所有资源、如图片、css样式等。
     * SpringMVC的主题也支持国际化，同一个主题不同区域也可以显示不同的风格。
     * SpringMVC中跟主题相关的类有 ThemeResolver、ThemeSource和Theme。主题是通过一系列资源来具体体现的，要得到一个主题的资源，
     * 首先要得到资源的名称，这是ThemeResolver的工作。然后通过主题名称找到对应的主题（可以理解为一个配置）文件，
     * 这是ThemeSource的工作。最后从主题中获取资源就可以了。
     * @param context
     */
    private void initThemeResolver(MyApplicationContext context) {
        
    }

    /**
     * 初始化本地语言环境
     * 解析视图需要两个参数：一是视图名，另一个是Locale。视图名是处理器返回的，Locale是从哪里来的？
     * 这就是LocaleResolver要做的事情。LocaleResolver用于从request解析出Locale，Locale就是zh-cn之类，表示一个区域，
     * 有了这个就可以对不同区域的用户显示不同的结果。SpringMVC主要有两个地方用到了Locale：一是ViewResolver视图解析的时候；
     * 二是用到国际化资源或者主题的时候。
     * @param context
     */
    private void initLocaleResolver(MyApplicationContext context) {
        
    }

    /**
     * 多文件上传的组件
     *  用于处理上传请求。处理方法是将普通的request包装成MultipartHttpServletRequest，后者可以直接调用getFile方法获取File，
     * 如果上传多个文件，还可以调用getFileMap得到FileName->File结构的Map。此组件中一共有三个方法，作用分别是判断是不是上传请求，
     * 将request包装成MultipartHttpServletRequest、处理完后清理上传过程中产生的临时资源。
     * @param context
     */
    private void initMultipartResolver(MyApplicationContext context) {
    }


    /**
     * 参照spring的processDispatchResult
     * @param req
     * @param resp
     * @param myModelAndView
     */
    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, MyModelAndView myModelAndView) {

        //将传入的myModelAndView转换成一个输出模板或outsream
        if (null == myModelAndView) {return;}
        if(this.viewResolvers.isEmpty()){return;}
        for (MyViewResolver viewResolver : this.viewResolvers) {
            try {
                MyView view = viewResolver.resolveViewName(myModelAndView.getViewName(),null);
                view.render(myModelAndView.getModel(),req,resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 从request中取得hadle
     * @param req
     * @return
     */
    private MyHandlerMapping getHandle(HttpServletRequest req) {

        if (this.handlerMappings.isEmpty()) {return  null;}
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath,"").replaceAll("/+", "/");
        for (MyHandlerMapping myHandlerMapping : handlerMappings) {
            Matcher matcher = myHandlerMapping.getPattern().matcher(url);
            if (!matcher.matches()) {continue;}
            return myHandlerMapping;
        }
        return null;

    }


}
