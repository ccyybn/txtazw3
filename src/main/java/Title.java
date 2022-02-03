import com.google.common.base.Strings;
import com.ibm.icu.text.NumberFormat;

import java.util.Locale;

public class Title implements Line {
    public static final Locale CHINESE_NUMBERS = new Locale("C@numbers=hans");
    public static final NumberFormat FORMATTER = NumberFormat.getInstance(CHINESE_NUMBERS);
    public int id;
    public int level;
    public String titleName;
    public int titleIndex;
    public String content;
    public int parentId;
    public String suffix;

    public Title(int id, int level, String titleName, int titleIndex, String content, String suffix) {
        this.id = id;
        this.level = level;
        this.titleName = titleName;
        this.titleIndex = titleIndex;
        this.content = content;
        this.suffix = suffix;
    }

    @Override
    public String out() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < level + 1; i++) {
            builder.append("#");
        }
        builder.append(" ");
        builder.append("第");
        builder.append(FORMATTER.format(titleIndex));
        builder.append(suffix);
        if (!Strings.isNullOrEmpty(titleName)) {
            builder.append(" ");
            builder.append(titleName);
        }

//        builder.append(content);
        return builder.toString();
    }

    @Override
    public String toString() {
        return content;
    }

    public static void main(String[] args) {
        System.out.println(FORMATTER.format(61305));
    }
}
