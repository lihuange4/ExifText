package kotlin.test.com.processorlib;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"kotlin.test.com.processorlib.GetAnnotation", "kotlin.test.com.processorlib.ZyaoAnnotation"})
public class MyProcessor extends AbstractProcessor {

    private static final String SPACE = " ";
    private static final String NEW_LINE = "\n";

    private Types mTypeUtils;
    private Filer mFiler;
    private Elements mElementUtils;
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mTypeUtils = processingEnv.getTypeUtils();
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(ZyaoAnnotation.class)) {
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                mMessager.printMessage(Diagnostic.Kind.ERROR, String.format("Only classes can be annotated with @%s",
                        ZyaoAnnotation.class.getSimpleName()));
                return true;
            }

            analysisAnnotated(annotatedElement);
        }

        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(GetAnnotation.class)) {
            if (annotatedElement.getKind() == ElementKind.CLASS) {
                Set<Modifier> modifiers = annotatedElement.getModifiers();

                PackageElement packageOf = mElementUtils.getPackageOf(annotatedElement);
                mMessager.printMessage(Diagnostic.Kind.NOTE, "packageOf : " + packageOf);
                TypeElement typeElement = (TypeElement) annotatedElement;
                String simpleName = typeElement.getSimpleName().toString();
                String newClassName = simpleName  + "$";
                StringBuilder sb = new StringBuilder();

                Element enclosingElement = annotatedElement.getEnclosingElement();
                Name packageName = ((PackageElement) enclosingElement).getQualifiedName();
                mMessager.printMessage(Diagnostic.Kind.NOTE, "packageName : " + packageName);

                sb.append(String.format("package %s;\n\n", packageName));


                for (Modifier modifier : modifiers) {
                    sb.append(modifier.toString() + SPACE);
                }


                sb.append("class" + SPACE);
                sb.append(newClassName);
                sb.append("{");
                sb.append(NEW_LINE);
                sb.append("String name;");
                sb.append(NEW_LINE);
                sb.append("}");

                mMessager.printMessage(Diagnostic.Kind.NOTE, "-----------StringBuilder last -------------");

                try {
//                    FileObject resource = mFiler.getResource(StandardLocation.CLASS_OUTPUT, packageName, simpleName);
//
//                    Writer writer1 = resource.openWriter();
//                    writer1.write("///////");
//                    writer1.flush();
//                    writer1.close();

                    JavaFileObject sourceFile = mFiler.createSourceFile(packageName + "." + newClassName);
                    Writer writer = sourceFile.openWriter();

                    writer.write(sb.toString());
                    writer.flush();
                    writer.close();

                    return true;

                } catch (IOException e) {
                    mMessager.printMessage(Diagnostic.Kind.ERROR, "---------IOException error----------");
                    mMessager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                }
            }
        }

        return true;
    }

    private static final String SUFFIX = "$$ZYAO";

    private void analysisAnnotated(Element classElement) {
        ZyaoAnnotation annotation = classElement.getAnnotation(ZyaoAnnotation.class);
        String name = annotation.name();
        String text = annotation.text();

        String newClassName = name + SUFFIX;

        mMessager.printMessage(Diagnostic.Kind.NOTE, "------------analysisAnnotated----------");

        StringBuilder builder = new StringBuilder()
                .append("package com.zyao89.demoprocessor.auto;\n\n")
                .append("public class ")
                .append(newClassName)
                .append(" {\n\n") // open class
                .append("\tpublic String getMessage() {\n") // open method
                .append("\t\treturn \"");

        // this is appending to the return statement
        builder.append(text).append(name).append(" !\\n");

        builder.append("\";\n") // end return
                .append("\t}\n") // close method
                .append("}\n"); // close class


        try { // write the file
            JavaFileObject source = mFiler.createSourceFile("com.zyao89.demoprocessor.auto." + newClassName);
            Writer writer = source.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // Note: calling e.printStackTrace() will print IO errors
            // that occur from the file already existing after its first run, this is normal
        }

        mMessager.printMessage(Diagnostic.Kind.NOTE, ">>> analysisAnnotated is finish... <<<");
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> strings = new HashSet<>();
        strings.add(GetAnnotation.class.getCanonicalName());
        return strings;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
