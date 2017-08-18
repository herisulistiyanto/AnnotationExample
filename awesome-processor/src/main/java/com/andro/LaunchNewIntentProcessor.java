package com.andro;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("com.andro.LaunchNewIntent")
public class LaunchNewIntentProcessor extends AbstractProcessor {

    private static final String METHOD_LAUNCHER_PREFIX = "start";
    private static final String METHOD_INTENT_PREFIX = "getIntent";
    private static final String PACKAGE_NAME = "com.andro.indieschool.myawesomeapp";
    private static final ClassName classIntent = ClassName.get("android.content", "Intent");
    private static final ClassName classContext = ClassName.get("android.content", "Context");

    private Filer filer;
    private Messager messager;
    private Elements elements;
    private Map<String, String> activitiesWithPackage;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elements = processingEnv.getElementUtils();

        activitiesWithPackage = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        try {
            /**
             * Proses mencari element yang terpasang annotasi
             */
            for (Element element : roundEnv.getElementsAnnotatedWith(LaunchNewIntent.class)) {
                if (ElementKind.CLASS != element.getKind()) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Annotasi ini hanya bisa dipake untuk class");
                    return true; //consumed disini
                }

                TypeElement typeElement = (TypeElement) element;
                activitiesWithPackage.put(
                        typeElement.getSimpleName().toString(),
                        elements.getPackageOf(typeElement).getQualifiedName().toString()
                );
            }

            /**
             * Proses Generating Class
             */
            TypeSpec.Builder generatedNavigatorClass = TypeSpec
                    .classBuilder("AwesomeNavigator")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            for (Map.Entry<String, String> element : activitiesWithPackage.entrySet()) {
                String namaActivity = element.getKey();
                String namaPackage = element.getValue();

                ClassName namaClassActivity = ClassName.get(namaPackage, namaActivity);
                MethodSpec intentLauncherMethod = MethodSpec
                        .methodBuilder(METHOD_LAUNCHER_PREFIX + namaActivity)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(classContext, "context")
                        .addStatement("$L.startActivity(new $T($L, $L)); //method launcher hasil generate", "context", classIntent, "context", namaClassActivity + ".class")
                        .build();

                MethodSpec intentMethod = MethodSpec
                        .methodBuilder(METHOD_INTENT_PREFIX + namaActivity)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(classIntent) //untuk membuat method return value
                        .addParameter(classContext, "context")
                        .addStatement("return new $T($L, $L); //method Intent Getter hasil generate", classIntent, "context", namaClassActivity + ".class")
                        .build();

                generatedNavigatorClass.addMethod(intentLauncherMethod);
                generatedNavigatorClass.addMethod(intentMethod);
            }

            /**
             * Membuat File berisi hasil generated class
             */
            JavaFile.builder(PACKAGE_NAME, generatedNavigatorClass.build())
                    .build()
                    .writeTo(filer);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
