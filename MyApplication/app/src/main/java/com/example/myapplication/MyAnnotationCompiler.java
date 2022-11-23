//package com.example.myapplication;
//
//import com.google.auto.service.AutoService;
//
//import java.util.concurrent.Flow;
//
//@AutoService(Flow.Processor.class)
//public class MyAnnotationCompiler extends AbstractProcessor {
//
//    /**
//     * 设置支持的源版本，默认为RELEASE_6
//     * 两种方式设置版本：
//     * 1. 此处返回指定版本
//     * 2. 类上面设置SupportedSourceVersion注解，并传入版本号
//     */
//    @Override
//    public SourceVersion getSupportedSourceVersion() {
//        return SourceVersion.latestSupported();
//    }
//
//    /**
//     * 设置支持的注解类型，默认为空集合（都不支持）
//     * 两种方式设置注解集合：
//     * 1. 此处返回支持的注解集合
//     * 2. 类上面设置SupportedAnnotationTypes注解，并传入需要支持的注解
//     */
//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        Set<String> types = new HashSet<>();
//        types.add(BindView.class.getCanonicalName());
//        return types;
//    }
//
//    /**
//     * 初始化操作
//     *
//     * @param processingEnv 环境
//     */
//    @Override
//    public synchronized void init(ProcessingEnvironment processingEnv) {
//        super.init(processingEnv);
//    }
//
//    /**
//     * 处理注解
//     *
//     * @param set              待处理的注解集合
//     * @param roundEnvironment RoundEnvironment
//     * @return 返回true表示后续处理器不再处理
//     */
//    @Override
//    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
//        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
//        //TypeElement->类    ExecutableElement->方法   VariableElement->属性
//        Map<String, List<VariableElement>> map = new HashMap<>(16);
//        for (Element element : elements) {
//            //属性元素
//            VariableElement variableElement = (VariableElement) element;
//            //获取类名
//            String activityName = variableElement.getEnclosingElement().getSimpleName().toString();
//            //根据类名将属性元素保存在集合中
//            List<VariableElement> variableElements = map.get(activityName);
//            if (variableElements == null) {
//                variableElements = new ArrayList<>();
//                map.put(activityName, variableElements);
//            }
//            variableElements.add(variableElement);
//        }
//
//        if (map.size() > 0) {
//            for (String activityName : map.keySet()) {
//                //根据类名获取属性元素集合
//                List<VariableElement> variableElements = map.get(activityName);
//                //获取类元素
//                TypeElement enclosingElement = (TypeElement) variableElements.get(0).getEnclosingElement();
//                //获取类的包名
//                String packageName = processingEnv.getElementUtils().getPackageOf(enclosingElement).toString();
//                //生成对应的类
//                generateClass(variableElements, packageName, activityName);
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 根据注解信息生成对应的类，本方法中手动生成类文件内容
//     * 我们还可以使用第三方工具JavaPoet优雅的生成，具体参考地址：https://github.com/square/javapoet
//     *
//     * @param variableElements 设置了对应注解的属性元素的集合
//     * @param packageName      包名
//     * @param activityName     类名
//     */
//    private void generateClass(List<VariableElement> variableElements, String packageName, String activityName) {
//        Writer writer = null;
//        try {
//            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(packageName + "." + activityName + "_ViewBinding");
//            writer = sourceFile.openWriter();
//            //包名
//            writer.write("package " + packageName + ";\n");
//            //导入包
//            writer.write("import com.payne.buf.IBinder;\n");
//            //类名以及实现的接口名
//            writer.write("public class " + activityName + "_ViewBinding implements IBinder<"
//                    + packageName + "." + activityName + "> {\n");
//            //实现接口中的方法
//            writer.write("  @Override\n");
//            writer.write("  public void bind(" + packageName + "." + activityName + " target) {\n");
//            //遍历属性元素集合，根据信息生成findViewById操作
//            for (VariableElement variableElement : variableElements) {
//                String variableName = variableElement.getSimpleName().toString();
//                int id = variableElement.getAnnotation(BindView.class).value();
//                TypeMirror typeMirror = variableElement.asType();
//                writer.write("      target." + variableName + " = (" + typeMirror + ") target.findViewById(" + id + ");\n");
//            }
//            writer.write("  }\n");
//            writer.write("}\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (writer != null) {
//                try {
//                    writer.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//}
//
