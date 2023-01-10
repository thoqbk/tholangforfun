package io.thoqbk.tholangforfun;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class App {
    public static void main(String[] args) throws IOException {
        if (args == null || args.length != 1) {
            System.out.println("Pass in single .tl4f file e.g. java -jar tl4f.jar hello.tl4f");
            return;
        }
        var input = Files.readString(Path.of(System.getProperty("user.dir"), args[0]), Charset.forName("utf8"));
        var program = new Parser(input).parse();
        var result = new Evaluator().eval(program);
        System.out.println("Exit with " + result);
    }
}
