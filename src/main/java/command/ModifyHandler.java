package command;

import command.utils.CommandUtils;
import diagram.ClassDiagram;
import diagram.model.Attribute;
import diagram.model.ClassInfo;
import diagram.model.Method;

import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModifyHandler implements CommandHandler {
    private final ClassDiagram diagram;
    private final Deque<Runnable> undoStack;

    public ModifyHandler(ClassDiagram diagram, Deque<Runnable> undoStack) {
        this.diagram = diagram;
        this.undoStack = undoStack;
    }

    @Override
    public boolean canHandle(String command) {
        return command.startsWith("modify");
    }

    @Override
    public String handle(String command) {
        if (command.matches("modify field\\s+.+")) {
            return modifyField(command);
        } else if (command.matches("modify function\\s+.+")) {
            return modifyMethod(command);
        }
        return "Invalid modify command";
    }

    private String modifyField(String command) {
        Matcher m = Pattern.compile(
                        "modify field (?<target>\\w+) -n (?<name>\\w+)\\s*" +
                                "(?:--new-name=(?<newName>\\w+))?\\s*" +
                                "(?:--new-type=(?<newType>[\\w<>]+))?\\s*" +
                                "(?:--new-access=(?<newAccess>[+#~-]))?\\s*(?:--static)?")
                .matcher(command);
        if (!m.find()) throw new IllegalArgumentException("Invalid modify command");

        ClassInfo cls = CommandUtils.findClass(diagram, m.group("target"));
        Attribute attr = cls.getAttributes().stream()
                .filter(a -> a.getName().equals(m.group("name")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Field not found"));

        Attribute backup = CommandUtils.copyAttribute(attr);
        if (m.group("newName") != null) attr.setName(m.group("newName"));
        if (m.group("newType") != null) attr.setType(m.group("newType"));
        if (m.group("newAccess") != null) {
            attr.setVisibility(CommandUtils.parseVisibility(m.group("newAccess")));
        }
        attr.setStatic(command.contains("--static"));

        undoStack.push(() -> CommandUtils.restoreAttribute(attr, backup));
        return "Modified field: " + backup.getName();
    }

    private String modifyMethod(String command) {
        Matcher m = Pattern.compile(
                        "modify function (?<target>\\w+) -n (?<name>\\w+)\\s*" +
                                "(?:--new-name=(?<newName>\\w+))?\\s*" +
                                "(?:--new-params=(?<newParams>[\\w:,<>]+))?\\s*" +
                                "(?:--new-return=(?<newRet>[\\w<>]+))?\\s*" +
                                "(?:--new-access=(?<newAccess>[+#~-]))?\\s*" +
                                "(?:--static)?(?:--abstract)?")
                .matcher(command);
        if (!m.find()) throw new IllegalArgumentException("Invalid modify command");

        ClassInfo cls = CommandUtils.findClass(diagram, m.group("target"));
        Method method = cls.getMethods().stream()
                .filter(mtd -> mtd.getName().equals(m.group("name")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Method not found"));

        Method backup = CommandUtils.copyMethod(method);
        if (m.group("newName") != null) method.setName(m.group("newName"));
        if (m.group("newParams") != null) {
            method.setParameters(CommandUtils.parseParameters(m.group("newParams")));
        }
        if (m.group("newRet") != null) method.setReturnType(m.group("newRet"));
        if (m.group("newAccess") != null) {
            method.setVisibility(CommandUtils.parseVisibility(m.group("newAccess")));
        }
        method.setStatic(command.contains("--static"));
        method.setAbstract(command.contains("--abstract"));

        undoStack.push(() -> CommandUtils.restoreMethod(method, backup));
        return "Modified method: " + backup.getName();
    }
}
