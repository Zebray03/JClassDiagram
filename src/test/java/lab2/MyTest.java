package lab2;

import diagram.ClassDiagram;
import diagram.ClassDiagramGenerator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyTest {
    public String generateUML(String testcase) throws URISyntaxException, IOException {
        URL resourceUrl = getClass().getResource(testcase);
        Path resourcePath = null;
        if (resourceUrl != null) {
            resourcePath = Paths.get(resourceUrl.toURI());
        }
        ClassDiagramGenerator generator = new ClassDiagramGenerator();
        ClassDiagram diagram = generator.parse(resourcePath);
        return diagram.generateUML();
    }

    public static int countSubstring(String str, String sub) {
        if (str == null || sub == null || str.isEmpty() || sub.isEmpty()) {
            return 0;
        }
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(sub, index)) != -1) {
            count++;
            index += sub.length(); // 移动到下一个位置继续查找
        }
        return count;
    }

    @Test
    public void testLibraryStructure() throws URISyntaxException, IOException {
        // 生成Library2相关UML图
        String uml = generateUML("MyTest.java");

        // 验证UML基础结构
        assertTrue(uml.startsWith("@startuml"), "UML diagram should start with @startuml");
        assertTrue(uml.contains("@enduml"), "UML diagram should end with @enduml");

        // 验证Library2类结构
        String expectedLibrary = "class Library2 {\n"
                + "    - managers: ArrayList<Manager>\n"
                + "    # friendLibrary: Library2\n"
                + "    + records: Map<Student, List<Book>>\n"
                + "}";
        assertTrue(uml.contains(expectedLibrary), "Library2 should contain correct attributes");

        // 验证Book类结构
        String expectedBook = "class Book {\n"
                + "    - price: Integer\n"
                + "    ~ pages: Page[][]\n"
                + "    + getPrice(): int\n"
                + "}";
        assertTrue(uml.contains(expectedBook), "Book should contain correct attributes and methods");

        // 验证Page类结构
        String expectedPage = "class Page {\n"
                + "    + content: String\n"
                + "}";
        assertTrue(uml.contains(expectedPage), "Page should contain content attribute");

        // 验证Student类结构
        String expectedStudent = "class Student {\n"
                + "    - age: int\n"
                + "    + name: String\n"
                + "}";
        assertTrue(uml.contains(expectedStudent), "Student should contain age and name attributes");

        // 验证Manager空类结构
        assertTrue(uml.contains("class Manager {\n}"), "Manager should be an empty class");

        // 验证关联关系
        assertTrue(uml.contains("Student <-- Library2"), "Student should associate to Library2");
        assertTrue(uml.contains("Book <-- Library2"), "Book should associate to Library2");
        assertTrue(uml.contains("Manager <-- Library2"), "Manager should associate to Library2");
        assertTrue(uml.contains("Page <-- Book"), "Page should associate to Book");

        // 确保关系线唯一性
        assertEquals(1, countSubstring(uml, "Student <-- Library2"), "Student-Library2 relation should appear once");
        assertEquals(1, countSubstring(uml, "Page <-- Book"), "Page-Book relation should appear once");
    }
}
