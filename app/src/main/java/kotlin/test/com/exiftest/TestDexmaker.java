package kotlin.test.com.exiftest;

import android.os.Environment;

import com.google.dexmaker.BinaryOp;
import com.google.dexmaker.Code;
import com.google.dexmaker.Comparison;
import com.google.dexmaker.DexMaker;
import com.google.dexmaker.FieldId;
import com.google.dexmaker.Label;
import com.google.dexmaker.Local;
import com.google.dexmaker.MethodId;
import com.google.dexmaker.TypeId;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import dalvik.system.DexClassLoader;

/**
 * xmiles
 * Created by lihuange on 2018/10/23.
 */
public class TestDexmaker {
    public static void executeTest() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, IOException {
        DexMaker dexMaker = new DexMaker();
        TypeId typeId = TypeId.get("LTesta;");
        dexMaker.declare(typeId, "Testa.generated", Modifier.PUBLIC, TypeId.OBJECT);
//        generateHelloMethod(dexMaker, typeId);
        createMethod2(dexMaker, typeId);

        String path = "/data/data/kotlin.test.com.exiftest";
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File outputDir = new File(path);

//        ClassLoader loader = dexMaker.generateAndLoad(TestDexmaker.class.getClassLoader(), outputDir);
//        Class<?> helloWorldClass = loader.loadClass("Testa");

        // Execute our newly-generated code in-process.
//        String hello = (String) helloWorldClass.getMethod("hello").invoke(null);
//        System.out.println(hello);

//        int fib = (int) helloWorldClass.getMethod("fib", int.class).invoke(null, 5);
//        System.out.println(fib);


        DexClassLoader dexClassLoader = new DexClassLoader(Environment.getExternalStorageDirectory() + "/Generated-2032686691.jar", path, null, TestDexmaker.class.getClassLoader());

        Class<?> testa = dexClassLoader.loadClass("Testa");
        int fib = (Integer) testa.getMethod("fib", int.class).invoke(null, 5);
        ;
    }

    private static void generateHelloMethod(DexMaker dexMaker, TypeId typeId) {
        // Lookup some types we'll need along the way.
        TypeId<System> systemType = TypeId.get(System.class);
        TypeId<PrintStream> printStreamType = TypeId.get(PrintStream.class);

        // Identify the 'hello()' method on declaringType.
        MethodId hello = typeId.getMethod(TypeId.STRING, "hello");

        // Declare that method on the dexMaker. Use the returned Code instance
        // as a builder that we can append instructions to.
        Code code = dexMaker.declare(hello, Modifier.STATIC | Modifier.PUBLIC);

        // Declare all the locals we'll need up front. The API requires this.
        Local<Integer> a = code.newLocal(TypeId.INT);
        Local<Integer> b = code.newLocal(TypeId.INT);
        Local<Integer> c = code.newLocal(TypeId.INT);
        Local<String> s = code.newLocal(TypeId.STRING);
        Local<String> s2 = code.newLocal(TypeId.STRING);
        Local<PrintStream> localSystemOut = code.newLocal(printStreamType);

        TypeId<StringBuffer> stringBuffer = TypeId.get(StringBuffer.class);
        Local<StringBuffer> stringBufferLocal = code.newLocal(stringBuffer);


        Local<String> stringLocal = code.newLocal(TypeId.STRING);
        code.loadConstant(stringLocal, "执行的值：");

        MethodId<StringBuffer, Void> constructor = stringBuffer.getConstructor();
        code.newInstance(stringBufferLocal, constructor);


        // int a = 0xabcd;
        code.loadConstant(a, 0xabcd);

        // int b = 0xaaaa;
        code.loadConstant(b, 0xaaaa);

        code.loadConstant(s2, "+++++");

        // int c = a - b;
        code.op(BinaryOp.SUBTRACT, c, a, b);

        // String s = Integer.toHexString(c);
        MethodId<Integer, String> toHexString
                = TypeId.get(Integer.class).getMethod(TypeId.STRING, "toHexString", TypeId.INT);
        code.invokeStatic(toHexString, s, c);

        // System.out.println(s);
        FieldId<System, PrintStream> systemOutField = systemType.getField(printStreamType, "out");
        code.sget(systemOutField, localSystemOut);
        MethodId<PrintStream, Void> printlnMethod = printStreamType.getMethod(
                TypeId.VOID, "println", TypeId.STRING);
        code.invokeVirtual(printlnMethod, null, localSystemOut, s);


        MethodId<StringBuffer, StringBuffer> append = TypeId.get(StringBuffer.class).getMethod(TypeId.get
                (StringBuffer.class), "append", TypeId.STRING);
        MethodId<StringBuffer, String> toString = TypeId.get(StringBuffer.class).getMethod(TypeId.STRING, "toString");

        code.invokeVirtual(append, stringBufferLocal, stringBufferLocal, stringLocal);
        code.invokeVirtual(append, stringBufferLocal, stringBufferLocal, s);
        code.invokeVirtual(toString, s, stringBufferLocal);


        code.returnValue(s);

    }

    private static void createMethod2(DexMaker dexMaker, TypeId typeId) {
        MethodId fib = typeId.getMethod(TypeId.INT, "fib", TypeId.INT);
        Code code = dexMaker.declare(fib, Modifier.PUBLIC | Modifier.STATIC);

        Local<Integer> parameter = code.getParameter(0, TypeId.INT);
        Local<Integer> i1 = code.newLocal(TypeId.INT);
        Local<Integer> i2 = code.newLocal(TypeId.INT);

        Local<Integer> a = code.newLocal(TypeId.INT);
        Local<Integer> b = code.newLocal(TypeId.INT);
        Local<Integer> c = code.newLocal(TypeId.INT);
        Local<Integer> d = code.newLocal(TypeId.INT);
        Local<Integer> e = code.newLocal(TypeId.INT);

        Label label = new Label();
        code.loadConstant(i1, 1);//i1 = 1;
        code.loadConstant(i2, 2);//i2 = 2;

        code.compare(Comparison.GE, label, parameter, i2);//if(parameter < 2 ) {
        code.returnValue(parameter);//return parameter;
        code.mark(label);//}

        code.op(BinaryOp.SUBTRACT, a, parameter, i1); // a = parameter - i1;
        code.op(BinaryOp.SUBTRACT, b, parameter, i2); // b = parameter - i2;

        code.invokeStatic(fib, c, a); // c = fib(a)
        code.invokeStatic(fib, d, b); // d = fib(b)

        code.op(BinaryOp.ADD, e, c, d);

        code.returnValue(e);
    }

    public static class Fibonacci {
        public static int fib(int i) {
            if (i < 2) {
                return i;
            }
            return fib(i - 1) + fib(i - 2);
        }
    }
}
