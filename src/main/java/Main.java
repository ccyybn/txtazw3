import org.mozilla.universalchardet.UniversalDetector;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    private static final List<String> titleSuffix = List.of("卷", "部", "章", "节");
    private static final List<Pattern> titlePattern = titleSuffix.stream()
            .map(s -> Pattern.compile("^第([\\u4e00-\\u9fa5\\u767e\\u5343\\u96f6\\d]{1,10})(" + s + ")\\s+(.+)$"))
            .collect(Collectors.toList());
    private static final List<Pattern> titleEmptyPattern = titleSuffix.stream()
            .map(s -> Pattern.compile("^第([\\u4e00-\\u9fa5\\u767e\\u5343\\u96f6\\d]{1,10})(" + s + ")$"))
            .collect(Collectors.toList());
    private static int titleIdInc = 1;

    public static void main(String[] args) throws IOException {
        Console console = System.console();
        if (console == null && !GraphicsEnvironment.isHeadless()) {
            String filename = Main.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "cmd", "/k", "java -jar \"" + filename + "\""});
        } else {
            convert();
            System.out.println("Program has ended, please type 'exit' to close the console");
        }
//        convert();
//        System.exit(0);
    }

    private static void convert() throws IOException {
        String workingPath = System.getProperty("user.dir");
//        String workingPath = "D:\\Code\\git\\github\\txtazw3";
        System.out.println("working path: " + workingPath);
        File workingFolder = new File(workingPath);
        File[] files = workingFolder.listFiles();

        Pattern pattern = Pattern.compile("^(.+)\\.txt");
        for (File file : files) {
            if (file.getName().endsWith("_out.txt")) continue;
            List<Line> parsed = new ArrayList<>();
            Matcher matcher = pattern.matcher(file.getAbsolutePath());
            if (matcher.find()) {
                System.out.println();
                System.out.println("converting txt: " + file.getAbsolutePath());
                List<String> lines = readLines(file);

                for (String line : lines) {
                    line = prepareLine(line);
                    if (line.isEmpty()) continue;
                    Title title = parseTitle(line, titlePattern);
                    if (title == null) title = parseTitle(line, titleEmptyPattern);

                    if (title == null) {
                        parsed.add(new Paragraph(line));
                    } else {
                        parsed.add(title);
                    }
                }
                List<Title> titles = adjustTitles(parsed);
                TitleTree root = TitleTree.parseTree(titles);
                root.println();
                List<String> outLines = parsed.stream().map(Line::out).collect(Collectors.toList());
                writeLines(outLines, matcher.group(1) + "_out.txt");
            }
        }
    }

    private static void writeLines(List<String> outLines, String path) throws IOException {
        File file = new File(path);
        if (file.exists()) file.delete();
        FileWriter writer = new FileWriter(path);
        String newLine = System.getProperty("line.separator");
        for (String outLine : outLines) {
            writer.write(outLine + newLine);
        }
        writer.close();
    }

    private static List<String> readLines(File file) throws IOException {
        List<String> lines = new ArrayList<>();

        FileInputStream headInputStream = new FileInputStream(file);
        byte[] head = new byte[1000];
        headInputStream.read(head);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), detect(head)))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    private static List<Title> adjustTitles(List<Line> parsed) {
        List<Title> titles = parsed.stream().filter(l -> l instanceof Title).map(l -> (Title) l).collect(Collectors.toList());
        setParentId(titles, 0, 0, 0);
        List<Integer> levels = titles.stream().map(t -> t.level).distinct().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        Map<Integer, Integer> levelsMapping = new HashMap<>();
        for (int i = 0; i < levels.size(); i++) {
            levelsMapping.put(levels.get(i), i);
        }
        levelsMapping.forEach((level, correctLevel) -> titles.stream().filter(t -> t.level == level).forEach(t -> t.level = correctLevel));
        return titles;
    }

    private static int setParentId(List<Title> titles, int index, int parentId, int subLevel) {
        for (int i = index; i < titles.size(); i++) {
            Title title = titles.get(i);
            if (title.level == subLevel) {
                title.parentId = parentId;
            } else if (title.level > subLevel) {
                if (i > 0) {
                    Title previous = titles.get(i - 1);
                    i = setParentId(titles, i, previous.id, title.level);
                } else {
                    i = setParentId(titles, i, parentId, title.level);
                }
            } else {
                return i - 1;
            }
        }
        return titles.size() - 1;
    }

    private static Title parseTitle(String line, List<Pattern> titlePattern) {
        for (int i = 0; i < titlePattern.size(); i++) {
            Pattern pattern = titlePattern.get(i);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                try {
                    int groupCount = matcher.groupCount();
                    String titleName = "";
                    if (groupCount > 2) {
                        titleName = matcher.group(3);
                    } else {
                        System.out.println();
                        System.out.println("Empty title: " + line);
                        System.out.println();
                    }
                    int index = ChineseNum.convert(matcher.group(1));
                    String suffix = matcher.group(2);
                    return new Title(titleIdInc++, i, titleName, index, line, suffix);
                } catch (Exception e) {
                    System.err.println("无法解析标题: " + line);
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static String prepareLine(String line) {
        line = line.trim();
        line = line.replaceAll("\\(", "（").replaceAll("\\)", "）");
        line = line.replaceAll("^　+", "");
        line = line.replaceAll("^　+", "");
        return line;
    }


    private static String detect(byte[] content) {
        UniversalDetector detector = new UniversalDetector(null);
        //开始给一部分数据，让学习一下啊，官方建议是1000个byte左右（当然这1000个byte你得包含中文之类的）
        detector.handleData(content, 0, content.length);
        //识别结束必须调用这个方法
        detector.dataEnd();
        //神奇的时刻就在这个方法了，返回字符集编码。
        return detector.getDetectedCharset();
    }

}
