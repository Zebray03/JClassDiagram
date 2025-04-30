package diagram.config;

import java.util.Set;

public interface AnalyzerConfigParser {
    Set<String> parse(String configPath);
}
